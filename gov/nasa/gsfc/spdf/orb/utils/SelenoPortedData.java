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
 * $Id: SelenoPortedData.java,v 1.2 2015/10/30 14:18:51 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;

import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateData;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;

/**
 *
 * @author rchimiak
 */
public class SelenoPortedData extends PortedData {

    boolean isSolenocentric = true;
    Double[] X;
    Double[] Y;
    Double[] Z;
    String coordinateSystemValue = null;

    public SelenoPortedData(SatelliteData satelliteData,
            SatelliteGraphProperties properties) {

        super();

        List< CoordinateData> coordinates
                = satelliteData.getCoordinates();

        X = new Double[coordinates.get(0).getX().size()];
        Y = new Double[coordinates.get(0).getY().size()];
        Z = new Double[coordinates.get(0).getZ().size()];
        time = new Calendar[satelliteData.getTime().size()];

        X = coordinates.get(0).getX().toArray(X);
        Y = coordinates.get(0).getY().toArray(Y);
        Z = coordinates.get(0).getZ().toArray(Z);

        for (int i = 0; i < time.length; i++) {

            time[i] = satelliteData.getTime().get(i).
                    toGregorianCalendar();
        }

        id = satelliteData.getId();

        coordinateSystemValue = "SSE";

        this.properties = properties;

    }

    @Override
    public SatelliteData makeSatelliteData() {

        SatelliteData dat = new SatelliteData();

        dat.setId(id);

        DatatypeFactory datatypeFactory;

        try {

            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {

            System.err.println("DatatypeConfigurationExcetpion: "
                    + e.getMessage());
            return null;
        }
        List< XMLGregorianCalendar> satTimes = dat.getTime();
        for (Calendar time1 : time) {
            satTimes.add(datatypeFactory.newXMLGregorianCalendar((GregorianCalendar) time1));
        }

        CoordinateData coor = new CoordinateData();
        Collections.addAll(coor.getX(), X);
        Collections.addAll(coor.getY(), Y);
        Collections.addAll(coor.getZ(), Z);

        dat.getCoordinates().add(coor);

        coor.setCoordinateSystem(
                CoordinateSystem.GSE);

        return dat;
    }

}
