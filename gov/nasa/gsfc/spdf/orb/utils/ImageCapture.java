/**
 * ********************************************************
 * Copyright (C) 2005, Michael N. Jacobs, All Rights Reserved
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *
 *************************************************************
 */
/*
 *$Id: ImageCapture.java,v 1.9 2015/10/30 14:18:51 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionWindow;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Raster;
import javax.swing.JFormattedTextField;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class ImageCapture {

    private Canvas3D canvas;
    ImageComponent2D image;
    Raster raster;
    private String[] satellites = null;
    private Color[] colors = null;
    private JFormattedTextField time = null;
    private String coordinateSystem = new String();
    private String date = new String();
    private int type;
    private boolean comments;

    public ImageCapture(Canvas3D aCanvas,
            SatellitePositionWindow window) {

        this(aCanvas, window, BufferedImage.TYPE_INT_ARGB);
    }

    public ImageCapture(Canvas3D aCanvas,
            SatellitePositionWindow window, int type) {

        this(aCanvas, window, type, true);
    }

    public ImageCapture(Canvas3D aCanvas,
            SatellitePositionWindow window,
            int type,
            boolean comments) {

        canvas = aCanvas;

        this.type = type;
        this.comments = comments;
        satellites = new String[window.getTable().getRowCount()];
        colors = new Color[window.getTable().getRowCount()];
        for (int i = 0; i < satellites.length; i++) {

            satellites[i] = (String) window.getTable().getValueAt(i, 0);
            colors[i] = (Color) window.getTable().getValueAt(i, 1);
        }
        time = window.getTimeField();
        coordinateSystem = window.getCoordinateField().getText();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM , yyyy");
        GregorianCalendar c = new GregorianCalendar();
        date = dateFormat.format(c.getTime());
    }

    public BufferedImage captureImage() {

        GraphicsContext3D context = canvas.getGraphicsContext3D();
        context.readRaster(getRaster());

        try {

            BufferedImage bi = (getRaster().getImage()).getImage();

            if (comments == false) {
                return bi;
            }

            Graphics2D g = bi.createGraphics();

            if (OrbitViewer.getPlanarPanel().isVisible()) {

                Raster[] rastArray = new Raster[3];
                ImageComponent2D[] imageArray = new ImageComponent2D[3];

                for (int i = 0; i < 3; i++) {

                    Canvas3D canv = OrbitViewer.getViewBranchArray()[i + 1].getCanvas();

                    int height = canv.getHeight();

                    int width = canv.getWidth();

                    imageArray[i] = new ImageComponent2D(
                            ImageComponent2D.FORMAT_RGB,
                            new BufferedImage(width, height, type));

                    rastArray[i]
                            = new Raster(
                                    new Point3f(-1.0f, -1.0f, -1.0f),
                                    Raster.RASTER_COLOR,
                                    0,
                                    0,
                                    width,
                                    height,
                                    imageArray[i],
                                    null);

                    rastArray[i].setCapability(Raster.ALLOW_IMAGE_WRITE);
                    rastArray[i].setCapability(Raster.ALLOW_SIZE_READ);
                    canv.getGraphicsContext3D().readRaster(rastArray[i]);

                }

                for (int i = 0; i < 3; i++) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g.drawImage((imageArray[i]).getImage(), canvas.getWidth(), ((canvas.getHeight()) / 3) * i, null);
                    Color3f backColor = new Color3f();
                    ContentBranch.getBackground().getColor(backColor);

                    g.setColor(new Color(backColor.x, backColor.y, backColor.z));

                    g.fillRect(canvas.getWidth(), ((canvas.getHeight() / 3) * (i + 1)) - 20, 200, 20);
                }
            }

            Font font = new Font("Arial", Font.PLAIN, 10);
            g.setFont(font);

            g.drawString("Coordinate System: " + coordinateSystem, 10, 15);
            int y = 15;
            for (int i = 0; i < satellites.length; i++) {

                g.setColor(colors[i]);
                y += 10;
                g.drawString(satellites[i], 10, y);
            }
            g.setColor(Color.white);
            g.drawString(time.getText(), 10, y += 15);

            FontMetrics fm = g.getFontMetrics();

            int fontWidth = fm.stringWidth("SSCWeb 3D    ");
            int fontHeight = fm.getHeight() - 10;
            g.drawString("SSCWeb 3D   " + date, bi.getWidth() - fontWidth - 150,
                    bi.getHeight() - fontHeight - 5);

            g.dispose();

            return bi;

        } catch (Exception e) {
        }
        return null;
    }

    public void finish() {

        image = null;
        raster = null;
        canvas = null;
    }

    protected ImageComponent2D getImage(int width, int height) {

        if (image == null) {

            BufferedImage bi
                    = new BufferedImage(width, height, type);

            image
                    = new ImageComponent2D(
                            ImageComponent2D.FORMAT_RGB,
                            bi);
        }
        image.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
        image.setCapability(ImageComponent2D.ALLOW_FORMAT_READ);
        image.setCapability(ImageComponent2D.ALLOW_SIZE_READ);

        return image;
    }

    protected Raster getRaster() {

        if (raster == null) {

            int height = (canvas.getBounds().height);

            int width = OrbitViewer.getPlanarPanel().isVisible()
                    ? (canvas.getBounds().width) + 180
                    : (canvas.getBounds().width);

            raster
                    = new Raster(
                            new Point3f(-1.0f, -1.0f, -1.0f),
                            Raster.RASTER_COLOR,
                            0,
                            0,
                            width,
                            height,
                            getImage(width, height),
                            null);

        }
        raster.setCapability(Raster.ALLOW_IMAGE_WRITE);
        raster.setCapability(Raster.ALLOW_SIZE_READ);
        return raster;
    }

    /**
     * prints the contents of buff2 on buff1 with the given opaque value.
     */
    private void addImage(BufferedImage buff1, BufferedImage buff2,
            float opaque, int x, int y) {
        Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque));
        g2d.drawImage(buff2, x, y, null);
        g2d.dispose();
    }
}
