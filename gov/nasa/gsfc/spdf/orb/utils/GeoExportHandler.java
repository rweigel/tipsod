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
 * Copyright (c) 2003-2009 United States Government as represented by the
 * National Aeronautics and Space Administration. No copyright is claimed
 * in the United States under Title 17, U.S.Code. All Other Rights Reserved.
 *
 * $Id: GeoExportHandler.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.GeoSatBranch;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.ssc.client.GroundStationDescription;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rchimiak
 */
public class GeoExportHandler extends ExportHandler {

    static ArrayList< PortedStation> stations
            = new ArrayList<PortedStation>();

    public GeoExportHandler(OrbitViewer ov) {

        super(ov);
    }

    @Override
    public void setData(List<? extends Object> satelliteData,
            SatelliteGraphProperties[] properties) {

        arr.clear();

        int i = 0;
        for (Object satData : satelliteData) {

            if (properties != null) {

                arr.add(new GeoPortedData((SatelliteData) satData, properties[i]));
                if (stations.isEmpty() && OrbitViewer.getGroundStationsWindow() != null) {

                    List< GroundStationDescription> stationArr
                            = OrbitViewer.getGroundStationsWindow().getStationDescription();

                    for (GroundStationDescription station : stationArr) {

                        stations.add(new PortedStation(station));
                    }
                }

            }
            i++;
        }

    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {

        super.actionPerformed(actionEvent);

        GeoSatBranch satBranch = (GeoSatBranch) ContentBranch.getSatBranch();

        setData(satBranch.getSatelliteData(), satBranch.getProperties());

        FileOutputStream fos;

        ObjectOutputStream out;

        try {
            fos = new FileOutputStream(new File(directory + "/" + name));

            out = new ObjectOutputStream(fos);
            out.writeObject(arr);
            out.writeObject(stations);

            out.flush();
            out.close();
        } catch (IOException ex) {
        }
    }
}
