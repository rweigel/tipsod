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
 * $Id: PrintHandler.java,v 1.14 2015/10/30 14:18:51 rchimiak Exp $
 * Created on May 23, 2002, 1:12 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.media.j3d.Canvas3D;
import javax.swing.JOptionPane;

/**
 * The PrintHandler class implements the necessary steps to print an image of
 * the scene being presented.
 *
 * @author rchimiak
 * @version $Revision: 1.14 $
 */
public class PrintHandler extends ImageHandler {

    /**
     * Creates a new PrintHandler to respond appropriately to a print command
     * from the user
     */
    public PrintHandler(OrbitViewer ov, Canvas3D canvas) {
        super(ov, canvas);
    }

    @Override
    void handleCommand() {

        reverseColors = JOptionPane.showConfirmDialog(null, "Reverse Colors for printing?",
                "Printing Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        PrinterJob printJob = PrinterJob.getPrinterJob();

        PageFormat pageFormat = printJob.defaultPage();
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        pageFormat = printJob.validatePage(pageFormat);
        printJob.setPrintable(this, pageFormat);

        if (printJob.printDialog()) {

            try {
                printJob.print();
            } catch (PrinterException ex) {
            }
        }
    }
}
