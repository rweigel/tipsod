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
 * $Id: HelioOrbitShape.java,v 1.5 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.utils.HelioUtil;
import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.PointArray;
import javax.swing.JOptionPane;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author rchimiak
 */
public class HelioOrbitShape extends OrbitShape {

    /**
     * Creates an OrbitShape based on the data received from the server
     *
     * @param satLocation an array of SatelliteLocation instances
     * @param satellitegp an array of SatelliteGraphProperties(satellites name,
     * color and shape) objects
     */
    public HelioOrbitShape(Trajectory satLocation, SatelliteGraphProperties satellitegp,
            SatelliteGraphTableModel graphModl, InfoPanel infoPane) {

        super(satellitegp, graphModl, infoPane);
        buildShape(satLocation);

    }

    protected double[][] getCoordinateData(ArrayList<List<Double>> list) {

        return HelioUtil.getCoordinateData(list);
    }

    @Override
    public void buildShape(Object satLocation) {

        removeAllGeometries();

        if (satLocation != null) {

            name = sgp.getDisplayName();
            try {

                Color3f color = new Color3f(sgp.getColor());

                Trajectory originalData
                        = (Trajectory.class.cast(satLocation));

                List< XMLGregorianCalendar> originalTime
                        = (Trajectory.class.cast(satLocation)).getTime();

                ArrayList<List<Double>>[] data = sanitize(originalData, originalTime);

                for (ArrayList<List<Double>> cd : data) {

                    double[][] coords = getCoordinateData(cd);

                    int numCoords = coords.length;
                    if (coords.length > 0) {

                        GeometryArray path;
                        if (numCoords == 1) {

                            path = new PointArray(numCoords, PointArray.COORDINATES | PointArray.COLOR_3 | PointArray.TEXTURE_COORDINATE_3);
                        } else {
                            path = new LineStripArray(numCoords,
                                    LineArray.COORDINATES | LineArray.COLOR_3,
                                    new int[]{numCoords});
                        }

                        path.setCapability(Geometry.ALLOW_INTERSECT);
                        path.setCapability(GeometryArray.ALLOW_COLOR_READ);
                        path.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
                        path.setCapability(GeometryArray.ALLOW_FORMAT_READ);
                        path.setCapability(GeometryArray.ALLOW_COUNT_READ);
                        path.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
                        path.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
                        path.setCapability(GeometryArray.ALLOW_COORDINATE_READ);

                        for (int i = 0; i < numCoords; i++) {
                            path.setCoordinate(i, new Point3d(coords[i][0],
                                    coords[i][1],
                                    coords[i][2]));
                            path.setColor(i, color);
                        }
                        addGeometry(path);
                    }
                }
                if (this.numGeometries() == 0) {
                    String id
                            = satLocation instanceof Trajectory
                                    ? (Trajectory.class.cast(satLocation)).getId()
                                    : "unknown";
                    JOptionPane.showMessageDialog(null,
                            " No data available in the specified time for: "
                            + id,
                            "information message",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception e) {

                System.err.println("Error '" + e.getMessage()
                        + "' while reading from '" + satLocation + "'");
            }

        }
    }

    //will need to add case for hole in the orbit not related to
    //start or end times.
    private ArrayList<List<Double>>[] sanitize(Trajectory original,
            List<XMLGregorianCalendar> time) {

        java.util.List<ArrayList<List<Double>>> lists = new ArrayList<ArrayList<List<Double>>>();
        ArrayList<List<Double>> data = new ArrayList<List<Double>>();
        List<Double> lat = new ArrayList<Double>();
        List<Double> longit = new ArrayList<Double>();
        List<Double> rad = new ArrayList<Double>();

        data.add(lat);
        data.add(longit);
        data.add(rad);

        ListIterator<Double> itx = original.getLatitude().listIterator();
        ListIterator<Double> ity = original.getLongitude().listIterator();
        ListIterator<Double> itz = original.getRadius().listIterator();

        Long[] t = new Long[]{time.get(0).
            toGregorianCalendar().getTimeInMillis(),
            time.get(0).
            toGregorianCalendar().getTimeInMillis()};

        while (itx.hasNext()) {

            Double dx = itx.next();
            Double dy = ity.next();
            Double dz = itz.next();

            if (new Point3d(dx, dy, dz).distance(new Point3d(0d, 0d, 0d)) != 0) {

                data.get(0).add(dx);
                data.get(1).add(dy);
                data.get(2).add(dz);

                t[1] = time.get(itx.previousIndex()).
                        toGregorianCalendar().getTimeInMillis();

            } else {

                if (t[0].compareTo(t[1]) != 0) {

                    timeList.add(t);
                    lists.add(data);
                }
                if (!timeList.isEmpty()) {

                    t = new Long[]{timeList.get(timeList.size() - 1)[1],
                        timeList.get(timeList.size() - 1)[1]};
                    data = new ArrayList<List<Double>>();

                }

                lat = new ArrayList<Double>();
                longit = new ArrayList<Double>();
                rad = new ArrayList<Double>();

                data.add(lat);
                data.add(longit);
                data.add(rad);

                while (itx.hasNext()
                        && new Point3d(itx.next(), ity.next(), itz.next()).distance(new Point3d(0d, 0d, 0d)) == 0) {
                }
                if (itx.hasNext()) {

                    t[0] = time.get(itx.previousIndex()).
                            toGregorianCalendar().getTimeInMillis();
                }
            }
        }
        if (t[0].compareTo(t[1]) != 0) {

            timeList.add(t);
            lists.add(data);
        }

        return lists.toArray((ArrayList<List<Double>>[]) Array.newInstance(data.getClass(), lists.size()));

    }

}
