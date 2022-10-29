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
 * $Id: GeoPortedData.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
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
import gov.nasa.gsfc.spdf.ssc.client.Hemisphere;
import gov.nasa.gsfc.spdf.ssc.client.BTraceData;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;

/**
 *
 * @author rchimiak
 */
public class GeoPortedData extends PortedData {

    boolean footpoint = false;
    String firstHemisphereValue = null;
    Float[] firstLatitude = null;
    Float[] firstLongitude = null;
    Double[] firstArcLength = null;
    String secondHemisphereValue = null;
    Float[] secondLatitude = null;
    Float[] secondLongitude = null;
    Double[] secondArcLength = null;
    Double[] X;
    Double[] Y;
    Double[] Z;
    String coordinateSystemValue = null;

    public GeoPortedData(SatelliteData satelliteData,
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

        if (coordinates.get(0).getCoordinateSystem() == null) {
            coordinateSystemValue = "GSE";
        } else {
            coordinateSystemValue
                    = coordinates.get(0).getCoordinateSystem().value();
        }

        this.properties = properties;

        if (satelliteData.getBTraceData() != null
                && satelliteData.getBTraceData().size() > 0) {

            footpoint = true;

            List<BTraceData> bTraceData
                    = satelliteData.getBTraceData();

            firstLatitude
                    = new Float[bTraceData.get(0).getLatitude().size()];
            firstLongitude
                    = new Float[bTraceData.get(0).getLongitude().size()];
            firstArcLength
                    = new Double[bTraceData.get(0).getArcLength().size()];
            firstHemisphereValue
                    = bTraceData.get(0).getHemisphere().value();

            firstLatitude
                    = bTraceData.get(0).getLatitude().toArray(firstLatitude);
            firstLongitude
                    = bTraceData.get(0).getLongitude().toArray(firstLongitude);
            firstArcLength
                    = bTraceData.get(0).getArcLength().toArray(firstArcLength);

            secondLatitude
                    = new Float[bTraceData.get(1).getLatitude().size()];
            secondLongitude
                    = new Float[bTraceData.get(1).getLongitude().size()];
            secondArcLength
                    = new Double[bTraceData.get(1).getArcLength().size()];
            secondHemisphereValue
                    = bTraceData.get(1).getHemisphere().value();

            secondLatitude
                    = bTraceData.get(1).getLatitude().toArray(secondLatitude);
            secondLongitude
                    = bTraceData.get(1).getLongitude().toArray(secondLongitude);
            secondArcLength
                    = bTraceData.get(1).getArcLength().toArray(secondArcLength);
        }
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

        if (footpoint == true) {

            BTraceData[] bTraceData = new BTraceData[2];

            for (int i = 0; i < bTraceData.length; i++) {

                bTraceData[i] = new BTraceData();
            }

            Collections.addAll(bTraceData[0].getLatitude(),
                    firstLatitude);
            Collections.addAll(bTraceData[0].getLongitude(),
                    firstLongitude);
            Collections.addAll(bTraceData[0].getArcLength(),
                    firstArcLength);
            bTraceData[0].setHemisphere(
                    Hemisphere.fromValue(firstHemisphereValue));

            Collections.addAll(bTraceData[1].getLatitude(),
                    secondLatitude);
            Collections.addAll(bTraceData[1].getLongitude(),
                    secondLongitude);
            Collections.addAll(bTraceData[1].getArcLength(),
                    secondArcLength);
            bTraceData[1].setHemisphere(
                    Hemisphere.fromValue(secondHemisphereValue));

            dat.getBTraceData().add(bTraceData[0]);
            dat.getBTraceData().add(bTraceData[1]);
        }

        coor.setCoordinateSystem(
                CoordinateSystem.fromValue(coordinateSystemValue));

        return dat;
    }

    public String getCoordinateSystemValue() {

        return coordinateSystemValue;
    }

}
