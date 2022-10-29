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
 * $Id: ExportHandler.java,v 1.8 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on November 29, 2007, 3:32 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;

/**
 *
 * @author rachimiak
 */
public abstract class ExportHandler implements ActionListener {

    OrbitViewer ov;

    static ArrayList< PortedData> arr
            = new ArrayList<PortedData>();

    String name;
    String directory;

    public ExportHandler(OrbitViewer ov) {

        this.ov = ov;
    }

    public abstract void setData(
            List<? extends Object> satelliteData,
            SatelliteGraphProperties[] properties);

    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {

        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        if (ContentBranch.getSatBranch() == null) {
            return;
        }
        directory = fc.getCurrentDirectory().toString();
        name = fc.getSelectedFile().getName();

    }
}
