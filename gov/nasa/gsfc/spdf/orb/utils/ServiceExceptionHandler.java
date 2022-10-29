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
 * $Id: ServiceExceptionHandler.java,v 1.12 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on July 16, 2002, 12:26 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import javax.swing.JOptionPane;

/**
 * The ServiceExceptionHandler class responds to exception arising while
 * connecting to the server.
 *
 * @author rchimiak
 * @version $Revision: 1.12 $
 */
public class ServiceExceptionHandler {

    /**
     * Constructs a ConnectException to handle a disconnect with the server.
     *
     * @param msg the detail message.
     **@param source the object from which the exception is being created
     * @throws java.lang.CloneNotSupportedException
     */
    public ServiceExceptionHandler(final String msg, final OrbitViewer source) throws CloneNotSupportedException {

        Object[] options = {"CONTINUE", "TRY AGAIN", "EXIT"};

        int sel = JOptionPane.showOptionDialog(null, msg, "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);

        switch (sel) {

            case JOptionPane.CLOSED_OPTION:
                source.exit(0);
            case JOptionPane.YES_OPTION:
                OrbitViewer.setConnected(false);
                OrbitViewer.getTipsodMenuBar().getExportMenu().setEnabled(false);
                OrbitViewer.getTipsodMenuBar().getImportMenu().setEnabled(true);
                return;
            case JOptionPane.NO_OPTION:
                source.connect();
                source.getSpacecrafts();
                break;
            case JOptionPane.CANCEL_OPTION:
                source.exit(0);
            default:
                source.exit(0);
        }
    }
}
