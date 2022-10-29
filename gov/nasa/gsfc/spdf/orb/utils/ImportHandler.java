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
 * $Id: ImportHandler.java,v 1.8 2015/10/30 14:18:51 rchimiak Exp $
 * Created on November 29, 2007, 3:32 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import gov.nasa.gsfc.spdf.ssc.client.GroundStationDescription;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;

/**
 *
 * @author rachimiak
 */
public class ImportHandler implements ActionListener {

    JFileChooser fc;
    OrbitViewer ov;

    /**
     * Creates a new instance of ImportHandler
     */
    public ImportHandler(OrbitViewer ov) {

        fc = new JFileChooser();
        this.ov = ov;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {

        int returnVal = fc.showOpenDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String directory = fc.getCurrentDirectory().toString();
        String name = fc.getSelectedFile().getName();

        FileInputStream fis;
        ObjectInputStream in;
        try {
            fis = new FileInputStream(new File(directory + "/" + name));
            in = new ObjectInputStream(fis);

            ArrayList portedDataArr = (ArrayList) in.readObject();

            if (portedDataArr.isEmpty()) {
                return;
            }

            if (portedDataArr.get(0) instanceof GeoPortedData == true) {

                List< SatelliteData> location
                        = new ArrayList<SatelliteData>(portedDataArr.size());

                SatelliteGraphProperties[] properties = new SatelliteGraphProperties[portedDataArr.size()];

                if (OrbitViewer.getControlPane() != null) {

                    OrbitViewer.getControlPane().setCentralBody(ControlPanel.Body.EARTH);
                }

                boolean footpoint = ((GeoPortedData) portedDataArr.get(0)).footpoint;
                int i = 0;
                for (Iterator iterator = portedDataArr.iterator(); iterator.hasNext();) {

                    GeoPortedData satData = (GeoPortedData) iterator.next();
                    location.add(satData.makeSatelliteData());
                    properties[i] = satData.getProperties();
                    i++;
                }
                ArrayList portedStationArr = (ArrayList) in.readObject();

                List< GroundStationDescription> stations
                        = new ArrayList<GroundStationDescription>(portedStationArr.size());
                i = 0;
                for (Iterator iterator = portedStationArr.iterator(); iterator.hasNext();) {

                    PortedStation station = (PortedStation) iterator.next();
                    stations.add(station.makeGroundStation());

                    i++;
                }
                ov.removeSatBranch();

                ov.addSatBranch(location, properties, footpoint, stations, ControlPanel.Body.EARTH);
            } else if (portedDataArr.get(0) instanceof SelenoPortedData == true) {

                List< SatelliteData> location
                        = new ArrayList<SatelliteData>(portedDataArr.size());

                SatelliteGraphProperties[] properties = new SatelliteGraphProperties[portedDataArr.size()];
                if (OrbitViewer.getControlPane() != null) {

                    OrbitViewer.getControlPane().setCentralBody(ControlPanel.Body.MOON);

                }
                int i = 0;
                for (Iterator iterator = portedDataArr.iterator(); iterator.hasNext();) {

                    SelenoPortedData satData = (SelenoPortedData) iterator.next();
                    location.add(satData.makeSatelliteData());
                    properties[i] = satData.getProperties();
                    i++;
                }

                ov.removeSatBranch();

                ov.addSatBranch(location, properties, false, null, ControlPanel.Body.MOON);

            } else if (portedDataArr.get(0) instanceof HelioPortedData == true) {
                List< Trajectory> trajectories
                        = new ArrayList<Trajectory>(portedDataArr.size());

                SatelliteGraphProperties[] properties = new SatelliteGraphProperties[portedDataArr.size()];
                if (OrbitViewer.getControlPane() != null) {

                    OrbitViewer.getControlPane().setCentralBody(ControlPanel.Body.SUN);

                }
                int i = 0;
                for (Iterator iterator = portedDataArr.iterator(); iterator.hasNext();) {

                    HelioPortedData satData = (HelioPortedData) iterator.next();
                    trajectories.add(satData.makeSatelliteData());
                    properties[i] = satData.getProperties();
                    i++;
                }

                ov.removeSatBranch();

                ov.addSatBranch(trajectories, properties);

            }
            in.close();

        } catch (FileNotFoundException e) {
            ControlPanel.isImport = false;
            JOptionPane.showMessageDialog(null, e.getMessage());

        } catch (IOException ex) {
            ControlPanel.isImport = false;
        } catch (ClassNotFoundException ex) {
            ControlPanel.isImport = false;
        }
    }
}
