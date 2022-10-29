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
 * $Id: SolenocentricUtils.java,v 1.9 2015/10/30 14:18:51 rchimiak Exp $
 * Created on November 26, 2007, 4:30 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;
import javax.xml.datatype.XMLGregorianCalendar;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateData;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;

/**
 *
 * @author rachimiak
 */
public class SolenocentricUtils {

    List<Double> XEarthCoord = null;
    List<Double> YEarthCoord = null;
    List<Double> ZEarthCoord = null;
    int moonIndex = 0;
    List<SatelliteData> location;
    SatelliteData earthData = null;

    public SolenocentricUtils() {
    }

    /**
     * Creates a new instance of SolenocentricUtils
     */
    public SolenocentricUtils(List<SatelliteData> location) {

        this.location = location;

        while (moonIndex < location.size()
                && !location.get(moonIndex).getId().equalsIgnoreCase("moon")) {
            moonIndex++;
        }

        List<CoordinateData> moonCoordinateData
                = location.get(moonIndex).getCoordinates();

        List<Double> XMoonCoord
                = new ArrayList<Double>(moonCoordinateData.get(0).getX().size());
        List<Double> YMoonCoord
                = new ArrayList<Double>(moonCoordinateData.get(0).getY().size());
        List<Double> ZMoonCoord
                = new ArrayList<Double>(moonCoordinateData.get(0).getZ().size());

        XEarthCoord
                = new ArrayList<Double>(moonCoordinateData.get(0).getX().size());
        YEarthCoord
                = new ArrayList<Double>(moonCoordinateData.get(0).getY().size());
        ZEarthCoord
                = new ArrayList<Double>(moonCoordinateData.get(0).getZ().size());

        for (Double xMoonCoord : moonCoordinateData.get(0).getX()) {

            XMoonCoord.add(xMoonCoord);
            XEarthCoord.add(-(xMoonCoord));
        }

        for (Double yMoonCoord : moonCoordinateData.get(0).getY()) {

            YMoonCoord.add(yMoonCoord);
            YEarthCoord.add(-(yMoonCoord));
        }

        for (Double zMoonCoord : moonCoordinateData.get(0).getZ()) {

            ZMoonCoord.add(zMoonCoord);
            ZEarthCoord.add(-(zMoonCoord));
        }

        for (SatelliteData locData : location) {

            if (locData != null) {

                List<CoordinateData> coords = locData.getCoordinates();

                CoordinateData coordinateData = coords.get(0);

                List<Double> XSSE
                        = new ArrayList<Double>(coordinateData.getX().size());
                List<Double> YSSE
                        = new ArrayList<Double>(coordinateData.getY().size());
                List<Double> ZSSE
                        = new ArrayList<Double>(coordinateData.getZ().size());

                List<Double> XMoon
                        = new ArrayList<Double>(coordinateData.getX().size());
                List<Double> YMoon
                        = new ArrayList<Double>(coordinateData.getY().size());
                List<Double> ZMoon
                        = new ArrayList<Double>(coordinateData.getZ().size());

                XMoon.add(XMoonCoord.get(0));
                YMoon.add(YMoonCoord.get(0));
                ZMoon.add(ZMoonCoord.get(0));

                int k = 1;

                if (locData.getTime().size() > 1
                        && location.get(moonIndex).getTime().size() > 1) {

                    for (int j = 1; j < locData.getTime().size(); j++) {

                        XMLGregorianCalendar time
                                = locData.getTime().get(j);

                        while (location.get(moonIndex).getTime().get(k).
                                toGregorianCalendar().getTimeInMillis()
                                < time.toGregorianCalendar().getTimeInMillis()
                                && k < location.get(moonIndex).
                                getTime().size() - 1) {

                            k++;
                        }

                        XMoon.add(XMoonCoord.get(k));
                        YMoon.add(YMoonCoord.get(k));
                        ZMoon.add(ZMoonCoord.get(k));

                    }
                } else if (location.get(moonIndex).getTime().size() == 1) {

                    for (int j = 1; j < locData.getTime().size(); j++) {

                        XMoon.add(XMoon.get(0));
                        YMoon.add(YMoon.get(0));
                        ZMoon.add(ZMoon.get(0));
                    }
                }

                for (int j = 0; j < coordinateData.getX().size(); j++) {
                    XSSE.add(coordinateData.getX().get(j)
                            - XMoon.get(j));
                }

                for (int j = 0; j < coordinateData.getY().size(); j++) {
                    YSSE.add(coordinateData.getY().get(j)
                            - YMoon.get(j));
                }

                for (int j = 0; j < coordinateData.getZ().size(); j++) {
                    ZSSE.add(coordinateData.getZ().get(j)
                            - ZMoon.get(j));
                }

                Collections.copy(coordinateData.getX(), XSSE);
                Collections.copy(coordinateData.getY(), YSSE);
                Collections.copy(coordinateData.getZ(), ZSSE);
            }
        }
    }

    public SatelliteData getEarthData() {

        if (earthData == null) {

            earthData = new SatelliteData();

            CoordinateData coordData = new CoordinateData();
            coordData.getX().addAll(XEarthCoord);
            coordData.getY().addAll(YEarthCoord);
            coordData.getZ().addAll(ZEarthCoord);

            earthData.getCoordinates().add(coordData);
            earthData.getTime().addAll(
                    location.get(moonIndex).getTime());

            return earthData;
        } else {
            return earthData;
        }
    }

    public void setEarthData(SatelliteData earthData) {

        this.earthData = earthData;
    }

    public BoundingSphere getBounds() {

        Point3d lower
                = new Point3d(PhysicalConstants.kmToRe(XEarthCoord.get(0)),
                        PhysicalConstants.kmToRe(YEarthCoord.get(0)),
                        PhysicalConstants.kmToRe(ZEarthCoord.get(0)));
        Point3d higher
                = new Point3d(PhysicalConstants.kmToRe(
                                XEarthCoord.get(XEarthCoord.size() - 1)),
                        PhysicalConstants.kmToRe(
                                YEarthCoord.get(YEarthCoord.size() - 1)),
                        PhysicalConstants.kmToRe(
                                ZEarthCoord.get(ZEarthCoord.size() - 1)));
        return new BoundingSphere(new Point3d((lower.x + higher.x) / 2, (lower.y + higher.y) / 2, (lower.z + higher.z) / 2), (lower.distance(higher)) / 2);
    }

    public List<SatelliteData> getLocationData() {

        try {

            ArrayList<SatelliteData> newList = new ArrayList(location);
            Collections.copy(newList, location);

            return newList;
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        return null;

    }
}
