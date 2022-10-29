/*
 * NOSA HEADER START
 *
 * The contents of this file are subject to the terms of the NASA Open
 * Source Agreement (NOSA), Version 1.3 only (the "Agreement").  You may
 * not use this file except in compliance with the Agreement.
 *
 * You can obtain a copy of the agreement at
 *   docs/NASA_Open_Source_Agreement_1.3.txt
 * or
 *   http://sscweb.gsfc.nasa.gov/tipsod/NASA_Open_Source_Agreement_1.3.txt.
 *
 * See the Agreement for the specific language governing permissions
 * and limitations under the Agreement.
 *
 * When distributing Covered Code, include this NOSA HEADER in each
 * file and include the Agreement file at
 * docs/NASA_Open_Source_Agreement_1.3.txt.  If applicable, add the
 * following below this NOSA HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 *
 * NOSA HEADER END
 *
 * Copyright (c) 2003-2006 United States Government as represented by the
 * National Aeronautics and Space Administration. No copyright is claimed
 * in the United States under Title 17, U.S.Code. All Other Rights Reserved.
 *
 * $Id: ImageHandler.java,v 1.18 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on May 24, 2002, 2:23 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionWindow;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ImageObserver;
import java.awt.image.LookupOp;
import java.awt.print.Printable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import javax.media.j3d.Canvas3D;

/**
 * The ImageHandler class responds to the printing and saving menu action by
 * implementing the ActionListener interface.
 *
 * @author rchimiak
 * @version $Revision: 1.18 $
 */
public class ImageHandler implements ActionListener, Printable, ImageObserver {

    /**
     * Reference to the parent class
     */
    protected OrbitViewer orbitViewer;
    protected Canvas3D canvas;
    /**
     * The image to be rendered
     */
    protected BufferedImage bImage = null;
    /**
     * Keeps the user choice when prompted to select the actual color or reverse
     * the colors for printing
     */
    protected boolean reverseColors = false;
    protected SatellitePositionWindow window;
    /**
     * Width of the picture to be printed or saved
     */
    public static final int offScreenWidth = 600;
    /**
     * Height of the picture to be printed or saved
     */
    public static final int offScreenHeight = 600;

    /**
     * Creates a new ImageHandler.
     */
    public ImageHandler(OrbitViewer ov, Canvas3D canvas) {

        orbitViewer = ov;
        this.canvas = canvas;
    }

    /**
     * Called to render the scene into an offscreen Canvas3D and save the image
     * to disk.
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {

        handleCommand();
    }

    protected void saveImage(int ext) {

        window = OrbitViewer.getSatellitePositionWindow();
        //Set Wait Cursor
        orbitViewer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ImageCapture imageCapture = null;

        switch (ext) {

            case SaveHandler.Exts.GIF:
            case SaveHandler.Exts.PNG:
                imageCapture = new ImageCapture(canvas,
                        window);
                break;
            case SaveHandler.Exts.JPG:
                imageCapture = new ImageCapture(canvas,
                        window, BufferedImage.TYPE_INT_RGB);

                break;
            case SaveHandler.Exts.PRINT:
                imageCapture = new ImageCapture(canvas,
                        window, BufferedImage.TYPE_3BYTE_BGR, false);
                break;
            default:
                break;
        }
        bImage = imageCapture.captureImage();
    }

    protected void removeImage() {

        if (bImage != null) {
            bImage.flush();
        }

        orbitViewer.setCursor(Cursor.getDefaultCursor());
        System.gc();
    }

    /**
     * Called as a response to the user print action.
     *
     * @return the value of Printable.PAGE_EXISTS
     */
    @Override
    public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int param) throws java.awt.print.PrinterException {

        if (param >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        saveImage(SaveHandler.Exts.PRINT);

        AffineTransform t2d = new AffineTransform();
        t2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        double xscale = pageFormat.getImageableWidth() / (double) bImage.getWidth();
        double yscale = pageFormat.getImageableHeight() / (double) bImage.getHeight();
        double scale = Math.min(xscale, yscale);
        t2d.scale(scale, scale);

        BufferedImage bimg
                = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        byte reverse[] = new byte[256];

        for (int j = 0; j < 256; j++) {

            reverse[j] = (byte) (255 - j);
        }

        ByteLookupTable blut = new ByteLookupTable(0, reverse);
        LookupOp lop = new LookupOp(blut, null);

        lop.filter(bImage, bimg);

        try {

            BufferedImage image = reverseColors == true ? bimg : bImage;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.drawImage(image, t2d, this);

            Font font = new Font("Arial", Font.PLAIN, 10);
            Color baseColor = reverseColors == true ? Color.black : Color.white;
            graphics.setFont(font);
            graphics.setColor(baseColor);

            int x = (int) pageFormat.getImageableX() + 10;
            int y = (int) pageFormat.getImageableY() + 10;

            graphics.drawString("Coordinate System: "
                    + window.getCoordinateField().getText(),
                    x,
                    y);
            y += 5;

            String[] satellites = new String[window.getTable().getRowCount()];
            Color[] colors = new Color[window.getTable().getRowCount()];
            for (int i = 0; i < satellites.length; i++) {

                satellites[i] = (String) window.getTable().getValueAt(i, 0);
                colors[i] = (Color) window.getTable().getValueAt(i, 1);
                graphics.setColor(reverseColors == true ? Util.invertColor(colors[i]) : colors[i]);
                graphics.drawString(satellites[i], x, y += 10);
            }

            graphics.setColor(baseColor);
            graphics.drawString(window.getTimeField().getText(), x, y += 15);

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM , yyyy");
            String date = dateFormat.format(new GregorianCalendar().getTime());

            int fontWidth = graphics.getFontMetrics().stringWidth("SSCWeb 3D    ");
            int fontHeight = graphics.getFontMetrics().getHeight() - 10;
            graphics.drawString("SSCWeb 3D   " + date, image.getWidth() - fontWidth - 150,
                    image.getHeight() - fontHeight - 5);

        } catch (Exception ex) {

            removeImage();
            return Printable.NO_SUCH_PAGE;
        }
        removeImage();

        return Printable.PAGE_EXISTS;
    }

    void handleCommand() {
    }

    @Override
    public boolean imageUpdate(java.awt.Image image, int param, int param2, int param3, int param4, int param5) {
        return false;
    }
}
