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
 * $Id: SatelliteGraphTableModel.java,v 1.34 2017/03/06 20:05:00 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.helio.client.ObjectDescription;
import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.BTraceData;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateData;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.awt.Color;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteDescription;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SimpleTimeZone;
import javax.swing.table.AbstractTableModel;
import javax.vecmath.Point3d;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class provides a TableModel of SatelliteGraphProperties.
 *
 * @see SatelliteGraphPropertiesTable
 * @see SatelliteGraphProperties
 * @version $Revision: 1.34 $
 * @author B. Harris
 */
public class SatelliteGraphTableModel extends AbstractTableModel {

    /**
     * Table column names.
     */
    private static final String[] COLUMN = {
        "", "Satellite", "Color", "Shape", "Pattern"};

    private static DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Init Error!", e);
        }
    }

    /**
     *
     * /**
     * The values of various graph properties for each satellite.
     */
    private ArrayList< SatelliteGraphProperties> satProperties = null;
    private ArrayList< SatelliteGraphProperties> earthSatProperties = null;
    private ArrayList< SatelliteGraphProperties> selenoSatProperties = null;
    private ArrayList< SatelliteGraphProperties> helioSatProperties = null;
    private final static Map<String, List<SatelliteGraphProperties>> groupMap = new HashMap<String, List<SatelliteGraphProperties>>();
    private final static Map<String, ArrayList<XMLGregorianCalendar>> groupTimeMap = new HashMap<String, ArrayList<XMLGregorianCalendar>>();
    private final static Map<String, Integer> sscResolution = new HashMap<String, Integer>();
    private final static SimpleTimeZone utcZone = new SimpleTimeZone(0, "UTC");

    public SatelliteGraphTableModel() {
    }

    /**
     * provides the available time to be displayed in a tooltip.
     *
     * @param satellites
     */
    public SatelliteGraphTableModel(
            List< SatelliteDescription> satellites) {

        satProperties
                = new ArrayList< SatelliteGraphProperties>(satellites.size());
        earthSatProperties
                = new ArrayList< SatelliteGraphProperties>(satellites.size());
        selenoSatProperties
                = new ArrayList< SatelliteGraphProperties>();

        for (SatelliteDescription satellite : satellites) {

            String tooltip = satellite.getName() + "  "
                    + getTimeAvailable(satellite);

            satProperties.add(
                    new SatelliteGraphProperties(satellite.getId(),
                            satellite.getName(),
                            tooltip,
                            satellite.getStartTime(),
                            satellite.getEndTime()));
            sscResolution.put(satellite.getId(), satellite.getResolution());

        }
        Collections.sort(satProperties,SortCaseInSensitive.ORDER);
        Collections.sort(satProperties, SortAgain.ORDER);

    }

    public void setHelioSatelliteGraphProperties(List< ObjectDescription> helioSats) {

        helioSatProperties
                = new ArrayList< SatelliteGraphProperties>(helioSats.size());

        for (ObjectDescription satellite : helioSats) {

            String tooltip = satellite.getName() + "  "
                    + getTimeAvailable(satellite);

            helioSatProperties.add(
                    new SatelliteGraphProperties(satellite.getId(),
                            satellite.getName(),
                            tooltip,
                            satellite.getStartDate(),
                            satellite.getEndDate()));
        }
        Collections.sort(helioSatProperties,SortCaseInSensitive.ORDER);

    }

    public ArrayList< SatelliteGraphProperties> getBodyArray(ControlPanel.Body body) {

        switch (ControlPanel.Body.valueOf(body.name())) {

            case MOON:
                return selenoSatProperties;
            case SUN:
                return helioSatProperties;
            default:
                return earthSatProperties;

        }
    }

    public static boolean isInGroup(String id) {

        Iterator<List<SatelliteGraphProperties>> it = groupMap.values().iterator();

        while (it.hasNext()) {

            Iterator<SatelliteGraphProperties> listIt = it.next().iterator();
            while (listIt.hasNext()) {

                if (listIt.next().getName().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }

        return false;

    }

    public void verifyTimes(SatelliteData sd) {

        checkForGaps(sd);

        checkForOffset(sd);

    }

    private void checkForGaps(SatelliteData sd) {

        //start and end time on the control panel
        Date start = OrbitViewer.getControlPane().getStartDate();
        Date end = OrbitViewer.getControlPane().getEndDate();

        //real spacecraft times coming back with each point
        List<XMLGregorianCalendar> cals = sd.getTime();

        //real spacecraft start and end times
        long first = cals.get(0).toGregorianCalendar().getTime().getTime();
        long last = cals.get(cals.size() - 1).toGregorianCalendar().getTime().getTime();

        //time between two points
        long interval;

        if (sscResolution.get(sd.getId()) != null) {

            interval = OrbitViewer.getControlPane().getResolution() * sscResolution.get(sd.getId()) * 1000;

        } else {
            //assuming even distribution, interval of time between each point
            interval = last - first == 0 ? (start.getTime() - end.getTime()) : (last - first) / (cals.size() - 1);
        }

        //list of all of the points
        List<CoordinateData> coordList = sd.getCoordinates();

        CoordinateData coordData = coordList.get(0);
        List<Double> X = coordData.getX();
        List<Double> Y = coordData.getY();
        List<Double> Z = coordData.getZ();

        // if spacecraft does not have points in the beginning
        if (start.getTime() < first) {

            //find out how many points are missing based on interval and
            // add points with 0s
            int knots = (int) ((first - start.getTime()) / interval) + 1;

            for (int i = 1; i < knots + 1; i++) {

                GregorianCalendar cali = new GregorianCalendar();
                cali.setTimeInMillis(first - (i * interval));
                cali.setTimeZone(utcZone);
                cals.add(0, datatypeFactory.newXMLGregorianCalendar(cali));

                X.add(0, 0d);
                Y.add(0, 0d);
                Z.add(0, 0d);

                if (sd.getBTraceData().isEmpty()) {
                } else {
                    Iterator<BTraceData> listIt = sd.getBTraceData().listIterator();
                    while (listIt.hasNext()) {

                        BTraceData bTraceData = listIt.next();
                        bTraceData.getArcLength().add(0, Double.NaN);
                        bTraceData.getLatitude().add(0, new Float(Double.NaN));
                        bTraceData.getLongitude().add(0, new Float(Double.NaN));

                    }
                }

            }
        }
        //if there are points missing at the end find out how many and
        // add 0s.
        if (end.getTime() > last) {

            int knots = (int) ((end.getTime() - last) / interval) + 1;

            for (int i = 1; i < knots + 1; i++) {

                GregorianCalendar cali = new GregorianCalendar();
                cali.setTimeInMillis(last + (i * interval));
                cali.setTimeZone(utcZone);
                cals.add(datatypeFactory.newXMLGregorianCalendar(cali));

                X.add(0d);
                Y.add(0d);
                Z.add(0d);

                if (sd.getBTraceData().isEmpty()) {
                } else {

                    Iterator<BTraceData> listIt = sd.getBTraceData().listIterator();
                    while (listIt.hasNext()) {

                        BTraceData bTraceData = listIt.next();
                        bTraceData.getArcLength().add(Double.NaN);
                        bTraceData.getLatitude().add(new Float(Double.NaN));
                        bTraceData.getLongitude().add(new Float(Double.NaN));

                    }

                }
            }

        }

        //is there a gap?
        if (((end.getTime() - start.getTime()) / interval) + 2 > cals.size()) {

            //checking between each points to find gap
            for (ListIterator<XMLGregorianCalendar> iter = cals.listIterator(); iter.hasNext();) {

                long time = iter.next().toGregorianCalendar().getTime().getTime();

                if (iter.hasNext()) {

                    long nextTime = cals.get(iter.nextIndex()).toGregorianCalendar().getTime().getTime();

                    while (nextTime - time > interval) {

                        int index = iter.nextIndex();

                        GregorianCalendar cali = new GregorianCalendar();
                        cali.setTimeInMillis(time + interval);
                        cali.setTimeZone(utcZone);
                        iter.add(datatypeFactory.newXMLGregorianCalendar(cali));

                        X.add(index, 0d);
                        Y.add(index, 0d);
                        Z.add(index, 0d);

                        if (sd.getBTraceData().isEmpty()) {
                        } else {

                            Iterator<BTraceData> listIt = sd.getBTraceData().listIterator();
                            while (listIt.hasNext()) {

                                BTraceData bTraceData = listIt.next();
                                bTraceData.getArcLength().add(index, Double.NaN);
                                bTraceData.getLatitude().add(index, new Float(Double.NaN));
                                bTraceData.getLongitude().add(index, new Float(Double.NaN));

                            }

                        }

                        time = cali.getTime().getTime();

                    }

                }
            }
        }

    }

    public void checkForOffset(SatelliteData sd) {

        //start and end time on the control panel
        long start = OrbitViewer.getControlPane().getStartDate().getTime();
        long end = OrbitViewer.getControlPane().getEndDate().getTime();

        CoordinateData coordData = sd.getCoordinates().get(0);
        List<Double> X = coordData.getX();
        List<Double> Y = coordData.getY();
        List<Double> Z = coordData.getZ();

        double interval = OrbitViewer.getControlPane().getResolution() * sscResolution.get(sd.getId()) * 1000;
        List<XMLGregorianCalendar> cals = sd.getTime();

        //check the calendar and remove what is at least one interval before the start time and after the end time 
        for (ListIterator<XMLGregorianCalendar> iter = cals.listIterator(); iter.hasNext();) {

            if (iter.hasNext()) {
                long time = iter.next().toGregorianCalendar().getTime().getTime();
                if (time <= start - interval || time >= end + interval) {
                    iter.remove();
                    int index = iter.nextIndex();
                    X.remove(index);
                    Y.remove(index);
                    Z.remove(index);

                    if (sd.getBTraceData().isEmpty()) {
                    } else {

                        Iterator<BTraceData> listIt = sd.getBTraceData().listIterator();
                        while (listIt.hasNext()) {

                            BTraceData bTraceData = listIt.next();

                            bTraceData.getArcLength().remove(index);
                            bTraceData.getLatitude().remove(index);
                            bTraceData.getLongitude().remove(index);

                        }
                    }
                }
            }
        }
        //we are fine we can stop
        if (cals.get(0).toGregorianCalendar().getTime().getTime() == start
                && cals.get(cals.size() - 1).toGregorianCalendar().getTime().getTime() == end) {

        } else {

            applyInterpolation(sd);
        }

    }

    private void applyInterpolation(SatelliteData sd) {

        long start = OrbitViewer.getControlPane().getStartDate().getTime();
        long end = OrbitViewer.getControlPane().getEndDate().getTime();

        CoordinateData coordData = sd.getCoordinates().get(0);
        List<Double> X = coordData.getX();
        List<Double> Y = coordData.getY();
        List<Double> Z = coordData.getZ();

        List<XMLGregorianCalendar> cals = sd.getTime();

        //find out greatest common denominator between interval and actual time
        int reso = OrbitViewer.getControlPane().getResolution() * sscResolution.get(sd.getId());
        long offsetBeginning = (start - cals.get(0).toGregorianCalendar().getTime().getTime()) / 1000;
        long offsetEnd = (cals.get(cals.size() - 1).toGregorianCalendar().getTime().getTime() - end) / 1000;

        int commonDeno = gcd((int) offsetBeginning, (int) offsetEnd);

        double alpha = (double) commonDeno / (double) reso;

        int pass;
        if (reso % commonDeno == 0) {

            pass = reso / commonDeno;
        } else {
            pass = (reso / 60);
            commonDeno = 60;
            alpha = 60d / (double) reso;
        }

        for (ListIterator<XMLGregorianCalendar> iter = cals.listIterator(); iter.hasNext();) {

            long iterTime = iter.next().toGregorianCalendar().getTime().getTime();
            long milliInter = (long) commonDeno * 1000;
            int count = 1;

            if (iter.hasNext()) {

                while (count < pass) {

                    GregorianCalendar cali = new GregorianCalendar();
                    cali.setTimeInMillis(iterTime + count * milliInter);
                    cali.setTimeZone(utcZone);
                    iter.add(datatypeFactory.newXMLGregorianCalendar(cali));

                    count++;
                }

            }
        }

        //apply the interpolation to X, Y, Z, entries
        List<List<Double>> points = new ArrayList<List<Double>>(3);
        points.add(X);
        points.add(Y);
        points.add(Z);

        applyInterpolationToPoints(points, alpha, pass);

        if (sd.getBTraceData().isEmpty()) {
        } else {

            applyInterpolationToFootpoints(sd.getBTraceData(), alpha, pass);
        }

        for (ListIterator<XMLGregorianCalendar> iter = cals.listIterator(); iter.hasNext();) {
            if (iter.hasNext()) {

                XMLGregorianCalendar next = iter.next();

                long i = next.toGregorianCalendar().getTime().getTime();
                if (i < start || i > end || i % 60000 != 0) {
                    iter.remove();
                    int index = iter.nextIndex();
                    X.remove(index);
                    Y.remove(index);
                    Z.remove(index);

                    if (sd.getBTraceData().isEmpty()) {
                    } else {
                        Iterator<BTraceData> listIt = sd.getBTraceData().listIterator();
                        while (listIt.hasNext()) {

                            BTraceData bTraceData = listIt.next();

                            bTraceData.getArcLength().remove(index);
                            bTraceData.getLatitude().remove(index);
                            bTraceData.getLongitude().remove(index);

                        }
                    }
                }
            }
        }
    }

    public static int gcd(int p, int q) {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }

    private void applyInterpolationToPoints(List<List<Double>> list, double alpha, int pass) {

        ListIterator<Double> itx = list.get(0).listIterator();
        ListIterator<Double> ity = list.get(1).listIterator();
        ListIterator<Double> itz = list.get(2).listIterator();

        while (itx.hasNext()) {

            Point3d currentPoint = new Point3d(itx.next(), ity.next(), itz.next());

            if (itx.hasNext()) {
                int index = itx.nextIndex();

                Point3d nextPoint = new Point3d(list.get(0).get(index), list.get(1).get(index), list.get(2).get(index));
                int count = 1;

                Point3d tempP = new Point3d();

                while (count < pass) {

                    if (currentPoint.distance(new Point3d(0d, 0d, 0d)) > 0 && nextPoint.distance(new Point3d(0d, 0d, 0d)) > 0) {

                        tempP.x = currentPoint.x + (nextPoint.x - currentPoint.x) * (alpha * count);
                        tempP.y = currentPoint.y + (nextPoint.y - currentPoint.y) * (alpha * count);
                        tempP.z = currentPoint.z + (nextPoint.z - currentPoint.z) * (alpha * count);
                        // currentPoint.interpolate(nextPoint, alpha);
                        itx.add(tempP.x);
                        ity.add(tempP.y);
                        itz.add(tempP.z);

                    } else {

                        itx.add(currentPoint.x);
                        ity.add(currentPoint.y);
                        itz.add(currentPoint.z);
                    }
                    count++;
                }
            }

        }

    }

    private void applyInterpolationToFootpoints(List<BTraceData> list, double alpha, int pass) {

        Iterator<BTraceData> listIt = list.listIterator();

        while (listIt.hasNext()) {

            BTraceData bTraceData = listIt.next();

            ListIterator<Double> radiusItr = bTraceData.getArcLength().listIterator();
            ListIterator<Float> latitudeItr = bTraceData.getLatitude().listIterator();
            ListIterator<Float> longitudeItr = bTraceData.getLongitude().listIterator();

            while (radiusItr.hasNext()) {

                Double radius = radiusItr.next();
                Float latitude = latitudeItr.next();

                Float longitude = longitudeItr.next();

                if (radiusItr.hasNext()) {

                    final int index = radiusItr.nextIndex();

                    int count = 1;

                    Double nextRadius = bTraceData.getArcLength().get(index);
                    Float nextLatitude = bTraceData.getLatitude().get(index);
                    Float nextLongitude = bTraceData.getLongitude().get(index);
                    while (count < pass) {
                        if (radius > 0 && nextRadius > 0) {

                            radiusItr.add(radius + ((nextRadius - radius) * alpha * count));
                            latitudeItr.add(latitude + ((nextLatitude - latitude) * (float) alpha * count));
                            longitudeItr.add(longitude + ((nextLongitude - longitude) * (float) alpha * count));
                        } else {

                            radiusItr.add(Double.NaN);
                            latitudeItr.add(new Float(Double.NaN));
                            longitudeItr.add(new Float(Double.NaN));

                        }

                        count++;

                    }
                }

            }

        }

    }

    public void verifyTimes(Trajectory tr) {

        Date start = OrbitViewer.getControlPane().getStartDate();
        Date end = OrbitViewer.getControlPane().getEndDate();

        List<XMLGregorianCalendar> cals = tr.getTime();

        long first = cals.get(0).toGregorianCalendar().getTime().getTime();
        long last = cals.get(cals.size() - 1).toGregorianCalendar().getTime().getTime();

        //assuming even distribution, interval of time between each point
        long interval = last - first == 0 ? (start.getTime() - end.getTime()) : (last - first) / (cals.size() - 1);

        List<Double> latitudes = tr.getLatitude();
        List<Double> longitudes = tr.getLongitude();
        List<Double> radius = tr.getRadius();

        //checking between each points to find gap
        for (ListIterator<XMLGregorianCalendar> iter = cals.listIterator(); iter.hasNext();) {

            long time = iter.next().toGregorianCalendar().getTime().getTime();

            if (iter.hasNext()) {

                long nextTime = cals.get(iter.nextIndex()).toGregorianCalendar().getTime().getTime();

                while (nextTime - time > interval) {

                    int index = iter.nextIndex();

                    GregorianCalendar cali = new GregorianCalendar();
                    cali.setTimeInMillis(time + interval);
                    cali.setTimeZone(utcZone);
                    iter.add(datatypeFactory.newXMLGregorianCalendar(cali));

                    latitudes.add(index, 0d);
                    longitudes.add(index, 0d);
                    radius.add(index, 0d);

                    time = cali.getTime().getTime();
                }
            }
        }

        // if spacecraft does not have points in the beginning
        if (start.getTime() < first) {

            int knots = (int) ((first - start.getTime()) / interval);
            for (int i = 0; i < knots; i++) {

                GregorianCalendar cali = new GregorianCalendar();
                cali.setTimeInMillis(start.getTime() + i * interval);
                cali.setTimeZone(utcZone);
                cals.add(i, datatypeFactory.newXMLGregorianCalendar(cali));

                latitudes.add(i, 0d);
                longitudes.add(i, 0d);
                radius.add(i, 0d);

            }
        }
        //if there are points missing at the end find out how many and
        // add 0s.
        if (end.getTime() > last) {

            int knots = (int) ((end.getTime() - last) / interval);
            for (int i = 1; i < knots + 1; i++) {

                GregorianCalendar cali = new GregorianCalendar();
                cali.setTimeInMillis(last + i * interval);
                cali.setTimeZone(utcZone);
                cals.add(datatypeFactory.newXMLGregorianCalendar(cali));

                latitudes.add(0d);
                longitudes.add(0d);
                radius.add(0d);

            }

        }
    }

    public void setData(ControlPanel.Body previouslySelectedBody, ControlPanel.Body newlySelectedBody) throws CloneNotSupportedException {

        ArrayList< SatelliteGraphProperties> current = getBodyArray(previouslySelectedBody);

        current.clear();

        for (SatelliteGraphProperties satPropertiesElt : satProperties) {

            current.add((SatelliteGraphProperties) satPropertiesElt.clone());
        }

        satProperties.clear();

        ArrayList< SatelliteGraphProperties> newSelection = getBodyArray(newlySelectedBody);

        for (SatelliteGraphProperties satPropertiesElt : newSelection) {

            satProperties.add((SatelliteGraphProperties) satPropertiesElt.clone());
        }

        this.fireTableDataChanged();

    }

    private SatelliteGraphProperties getPropertiesFromGroup(String id) {

        Iterator<List<SatelliteGraphProperties>> it = groupMap.values().iterator();

        while (it.hasNext()) {

            Iterator<SatelliteGraphProperties> listIt = it.next().iterator();
            while (listIt.hasNext()) {

                SatelliteGraphProperties prop = listIt.next();

                if (prop.getName().equalsIgnoreCase(id)) {
                    return prop;
                }
            }

        }

        return null;

    }

    private SimpleDateFormat getFormatter() {

        SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        // default locale date formatter

        formatter.setTimeZone(utcZone);
        return formatter;
    }

    private String getTimeAvailable(SatelliteDescription satellite) {

        return getFormatter().format(
                satellite.getStartTime().toGregorianCalendar().getTime())
                + " - "
                + getFormatter().format(
                        satellite.getEndTime().toGregorianCalendar().getTime());

    }

    private String getTimeAvailable(ObjectDescription satellite) {

        return getFormatter().format(
                satellite.getStartDate().toGregorianCalendar().getTime())
                + " - "
                + getFormatter().format(
                        satellite.getEndDate().toGregorianCalendar().getTime());

    }

    /**
     * provide the tooltip (observatory name and available times)
     *
     * @param row
     * @return tooltip
     */
    public String getTooltip(int row) {

        return (satProperties.get(row)).getTooltip();
    }

    /**
     * Constructs a SatelliteGraphTableModel containing the given satellite
     * graph properties.
     *
     * @param satGraphProperties the satellite graph properties to initialize
     * this model with
     */
    public SatelliteGraphTableModel(
            SatelliteGraphProperties[] satGraphProperties) {

        satProperties
                = new ArrayList< SatelliteGraphProperties>(satGraphProperties.length);

        for (SatelliteGraphProperties satGraphPropertie : satGraphProperties) {

            satProperties.add(new SatelliteGraphProperties(satGraphPropertie));
        }

        Collections.sort(satProperties,SortCaseInSensitive.ORDER);
        Collections.sort(satProperties, SortAgain.ORDER);
    }

    @Override
    public int getColumnCount() {

        return COLUMN.length;
    }

    @Override
    public int getRowCount() {

        return satProperties.size();
    }

    @Override
    public String getColumnName(int col) {

        return COLUMN[col];
    }

    @Override
    public java.lang.Object getValueAt(int row, int col) {

        switch (col) {

            case 0:
                return (satProperties.get(row)).isSelected();

            case 1:
                return (satProperties.get(row)).getDisplayName();

            case 2:
                return (satProperties.get(row)).getColor();

            case 3:
                return (satProperties.get(row)).getShape();

            default:
                return (satProperties.get(row)).getLineStyle();
        }
    }

    @Override
    public Class getColumnClass(int col) {

        return getValueAt(0, col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {

        if (col == 0) {
            return !((String) getValueAt(row, 1)).equalsIgnoreCase("moon") || !ControlPanel.isSolenocentric();
        }
        return col != 1;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        try {

            switch (col) {

                case 0:

                    (satProperties.get(row)).setSelected((Boolean) value);
                    break;

                case 2:
                    (satProperties.get(row)).setColor((Color) value);
                    break;

                case 3:
                    (satProperties.get(row)).setShape((SatelliteGraphShape) value);
                    break;

                case 4:
                    (satProperties.get(row)).setLineStyle((LineStyle) value);
                    break;
            }
            fireTableCellUpdated(row, col);
        } catch (Exception e) {
        }
    }

    /**
     * Provides the number of satellites that are selected to be graphed.
     *
     * @return
     */
    public int getSelectedSatelliteCount() {

        int selectedCount = 0;         // number of satellites selected

        ListIterator<SatelliteGraphProperties> satItr = satProperties.listIterator();
        while (satItr.hasNext()) {

            SatelliteGraphProperties next = satItr.next();
            if (next.isSelected()) {
                {
                    if (groupMap.containsKey(next.getName())) {

                        ListIterator<SatelliteGraphProperties> listItr = groupMap.get(next.getName()).listIterator();

                        while (listItr.hasNext()) {

                            selectedCount++;
                        }

                    } else {

                        selectedCount++;
                    }
                }

            }
        }

        return selectedCount;
    }

    /**
     * Provides the names of all the currently selected satellites.
     *
     * @return the names of all the currently selected satellites (zero length
     * array if none are currently selected)
     */
    /**
     * Provides the names of all the currently selected satellites.
     *
     * @return the names of all the currently selected satellites (zero length
     * array if none are currently selected)
     * @throws java.lang.CloneNotSupportedException
     */
    public String[] getSelectedSatelliteNames() throws CloneNotSupportedException {

        SatelliteGraphProperties[] properties = getSelectedSatelliteGraphProperties();
        String[] names = new String[properties.length];
        for (int i = 0; i < properties.length; i++) {

            names[i] = properties[i].getName();
        }

        return names;
    }

    /**
     * Provides the graph property values for all the currently selected
     * satellites.
     *
     * @return the graph properties of all the currently selected satellites
     * @throws java.lang.CloneNotSupportedException
     */
    public SatelliteGraphProperties[] getSelectedSatelliteGraphProperties() throws CloneNotSupportedException {

        ArrayList<SatelliteGraphProperties> graphPropertiesList = new ArrayList<SatelliteGraphProperties>();

        ListIterator<SatelliteGraphProperties> satItr = satProperties.listIterator();
        while (satItr.hasNext()) {

            SatelliteGraphProperties next = satItr.next();
            if (next.isSelected()) {

                if (groupMap.containsKey(next.getName())) {

                    ListIterator<SatelliteGraphProperties> listItr = groupMap.get(next.getName()).listIterator();

                    while (listItr.hasNext()) {

                        SatelliteGraphProperties sgp = listItr.next();

                        SatelliteGraphProperties clonedSGP = (SatelliteGraphProperties) sgp.clone();
                        clonedSGP.setStartTime(groupTimeMap.get(next.getName()).get(0));
                        clonedSGP.setStopTime(groupTimeMap.get(next.getName()).get(1));

                        graphPropertiesList.add(clonedSGP);

                    }

                } else {

                    graphPropertiesList.add((SatelliteGraphProperties) (next).clone());
                }
            }
        }

        return graphPropertiesList.toArray(new SatelliteGraphProperties[graphPropertiesList.size()]);

    }

    public SatelliteGraphProperties[] getSelectedSatelliteGraphProperties(String[] selectedNames) throws CloneNotSupportedException {

        SatelliteGraphProperties[] graphPropertiesList = new SatelliteGraphProperties[selectedNames.length];

        for (int i = 0; i < graphPropertiesList.length; i++) {

            if (isInGroup(selectedNames[i])) {

                graphPropertiesList[i] = getPropertiesFromGroup(selectedNames[i]);

            } else {
                graphPropertiesList[i] = getSatelliteGraphProperties(selectedNames[i]);
            }
        }

        return graphPropertiesList;

    }

    /**
     * Sets the SatelliteGraphProperties of the specified satellite. The
     * satellite whose properties are to be set should already exist in the
     * model (i.e., the method will not add a new satellite to the model).
     *
     * @param value
     * @return true if the properties where set and false if the given satellite
     * doesn't exist in the model
     */
    public boolean setSatelliteGraphProperties(SatelliteGraphProperties value) {

        ListIterator litr = satProperties.listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = (SatelliteGraphProperties) litr.next();

            int i = satProperties.indexOf(element);

            if (element.getName().equalsIgnoreCase(value.getName())) {

                setValueAt(value.getColor(), i, 2);

                setValueAt(value.getShape(), i, 3);

                setValueAt(value.getLineStyle(), i, 4);

                return true;
            }
        }
        return false;
    }

    public boolean setSatelliteGraphProperties(SatelliteGraphProperties value, ControlPanel.Body body) {

        ListIterator litr = getBodyArray(body).listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = (SatelliteGraphProperties) litr.next();

            if (element.getName().equalsIgnoreCase(value.getName())) {

                element.setColor(value.getColor());

                element.setShape(value.getShape());

                element.setLineStyle(value.getLineStyle());

                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param graphProperties
     */
    public void setSatelliteGraphProperties(SatelliteGraphProperties[] graphProperties) {

        satProperties = new ArrayList< SatelliteGraphProperties>();
        satProperties.addAll(Arrays.asList(graphProperties));
    }

    /**
     * Gets the SatelliteGraphProperties of the specified satellite.
     *
     * @param name the name of the satellite whose properties are to be gotten
     * @return the graph properties of the specified satellite or null if the
     * satellite doesn't exist in the model
     * @throws java.lang.CloneNotSupportedException
     */
    public SatelliteGraphProperties getSatelliteGraphProperties(String name) throws CloneNotSupportedException {

        ListIterator litr = satProperties.listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = (SatelliteGraphProperties) litr.next();

            if (element.getName().equalsIgnoreCase(name)) {

                return ((SatelliteGraphProperties) element.clone());
            }
        }
        return null;
    }

    public SatelliteGraphProperties getSatelliteGraphProperties(String name, ControlPanel.Body body) throws CloneNotSupportedException {

        ListIterator litr = getBodyArray(body).listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = (SatelliteGraphProperties) litr.next();

            if (element.getName().equalsIgnoreCase(name)) {

                return ((SatelliteGraphProperties) element.clone());
            }
        }
        return null;
    }

    public SatelliteGraphProperties getSelenoSatelliteGraphProperties() throws CloneNotSupportedException {

        ListIterator litr = earthSatProperties.listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = (SatelliteGraphProperties) litr.next();

            if (element.getName().equalsIgnoreCase("moon")) {

                return ((SatelliteGraphProperties) element.clone());
            }
        }
        return null;
    }

    /**
     * Get the row containing the specified satellite.
     *
     * @param name name of satellite to find
     * @return row containing the specified satellite. -1 if not found.
     */
    public int getSatelliteRow(String name) {

        for (int i = 0; i < satProperties.size(); i++) {

            SatelliteGraphProperties satProp = satProperties.get(i);
            // i'th SatelliteGraphProperties

            if (satProp.getName().equalsIgnoreCase(name)) {

                return i;
            }
        }

        return -1;
    }

    /**
     * Set the specified satellite to be graphed.
     *
     * @param name name of satellite to graph
     * @return true if specified satellite set to be graphed. false if specified
     * satellite not found.
     */
    public boolean setSelected(String name) {

        int row = getSatelliteRow(name);
        // row containing specified
        // satellite
        if (row > -1) {

            this.setValueAt(true, row, 0);

            return true;
        } else {

            return false;
        }
    }

    /**
     * Sets the specified satellites to be graphed.
     *
     * @param names names of satellite to graph
     */
    public void setSelected(String[] names) {
        for (String name : names) {
            setSelected(name);
        }
    }

    /**
     * Clears all selected satellites.
     */
    public void clearAllSelected() {

        ListIterator litr = satProperties.listIterator();

        while (litr.hasNext()) {

            setValueAt(false, litr.nextIndex(), 0);
            litr.next();
        }
    }

    /**
     * the satellites that are bundled into a group and do not require being
     * displayed individually
     *
     * @param map relation between an observatory and its group.
     * @throws java.lang.CloneNotSupportedException
     */
    public void addGroup(Map< String, String> map) throws CloneNotSupportedException {

        ListIterator<SatelliteGraphProperties> litr = satProperties.listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = litr.next();

            if (map.containsKey(element.getName())) {

                litr.remove();

                String groupName = map.get(element.getName());

                if (groupMap.containsKey(groupName)) {

                    groupMap.get(groupName).add((SatelliteGraphProperties) element.clone());

                } else {

                    List<SatelliteGraphProperties> list = new ArrayList<SatelliteGraphProperties>();
                    list.add((SatelliteGraphProperties) element.clone());
                    groupMap.put(groupName, list);
                }
            }
        }

        Iterator it = groupMap.entrySet().iterator();

        while (it.hasNext()) {

            String groupName = ((Map.Entry) it.next()).getKey().toString();
            makeGroupTime(groupName);
            SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            formatter.setTimeZone(Util.UTC_TIME_ZONE);
            String tooltip = groupName + "  "
                    + formatter.format(getGroupTime(groupName)[0].toGregorianCalendar().getTime())
                    + " - "
                    + formatter.format(getGroupTime(groupName)[1].toGregorianCalendar().getTime());

            satProperties.add(new SatelliteGraphProperties(groupName, "all " + groupName, tooltip, null, null));

        }

        Collections.sort(satProperties, SortWithGroup.ORDER);

        ListIterator<SatelliteGraphProperties> litr2 = satProperties.listIterator();

        while (litr2.hasNext()) {

            int index = litr2.nextIndex();

            if (litr2.next().getDisplayName().startsWith("all")) {

                setValueAt(Color.WHITE, index, 2);
                setValueAt(null, index, 3);
                setValueAt(null, index, 4);
            }
        }
        modifySelenoforGroups(map);

    }

    private void modifySelenoforGroups(Map< String, String> map) throws CloneNotSupportedException {

        ListIterator<SatelliteGraphProperties> litr = selenoSatProperties.listIterator();

        while (litr.hasNext()) {

            SatelliteGraphProperties element = litr.next();

            if (map.containsKey(element.getName())) {

                litr.remove();
            }
        }

        ListIterator<SatelliteGraphProperties> litr2 = satProperties.listIterator();
        while (litr2.hasNext()) {

            SatelliteGraphProperties prop = litr2.next();

            if (prop.getDisplayName().startsWith("all")) {
                selenoSatProperties.add((SatelliteGraphProperties) prop.clone());
            }

        }
        Collections.sort(selenoSatProperties, SortWithGroup.ORDER);

    }

    private void makeGroupTime(final String id) {

        XMLGregorianCalendar startTime = groupMap.get(id).get(0).getStartTime();
        XMLGregorianCalendar stopTime = groupMap.get(id).get(0).getStopTime();

        ListIterator<SatelliteGraphProperties> listItr = groupMap.get(id).listIterator();

        while (listItr.hasNext()) {

            SatelliteGraphProperties sgp = listItr.next();

            if (startTime.toGregorianCalendar().after(sgp.getStartTime().toGregorianCalendar())) {

                startTime = sgp.getStartTime();
            }

            if (stopTime.toGregorianCalendar().before(sgp.getStopTime().toGregorianCalendar())) {

                stopTime = sgp.getStopTime();
            }
        }

        ArrayList current = new ArrayList();
        current.add(startTime);
        current.add(stopTime);

        groupTimeMap.put(id, current);

        // return calendar;
    }

    public static XMLGregorianCalendar[] getGroupTime(String groupName) {

        ListIterator<XMLGregorianCalendar> listItr = groupTimeMap.get(groupName).listIterator();

        XMLGregorianCalendar[] calendar = {listItr.next(), listItr.next()};

        return calendar;

    }

    public static int getMaxSatelliteResolution(String[] satNames) {

        int max = sscResolution.get(satNames[0]);

        for (int i = 1; i < satNames.length; i++) {
            if (sscResolution.get(satNames[i]) > max) {
                max = sscResolution.get(satNames[i]);
            }
        }
        return max;
    }

    public static String getGroupName(String satName) {

        if (isInGroup(satName)) {

            Iterator it = groupMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = ((Map.Entry) it.next());

                ListIterator<SatelliteGraphProperties> list = ((List<SatelliteGraphProperties>) entry.getValue()).listIterator();
                while (list.hasNext()) {

                    if (satName.equalsIgnoreCase(list.next().getName())) {
                        return (String) entry.getKey();
                    }

                }

            }
        }

        return null;

    }
}

class SortCaseInSensitive {
    static final Comparator< SatelliteGraphProperties> ORDER
            = new Comparator< SatelliteGraphProperties>() {
                @Override
                public int compare(SatelliteGraphProperties d1,
                        SatelliteGraphProperties d2) {
                  
                         return d1.getDisplayName().compareToIgnoreCase(d2.getDisplayName());
                }
            };
}


class SortAgain {

    static final Comparator< SatelliteGraphProperties> ORDER
            = new Comparator< SatelliteGraphProperties>() {
                @Override
                public int compare(SatelliteGraphProperties d1,
                        SatelliteGraphProperties d2) {

                    if (d1.getName().startsWith("themis") && d1.getName().endsWith("pred")
                    && d2.getName().startsWith("themis") && !d2.getName().endsWith("pred")) {
                        return 1;
                    }
                    return 0;
                }
            };
}

class SortWithGroup {

    static final Comparator< SatelliteGraphProperties> ORDER
            = new Comparator< SatelliteGraphProperties>() {
                @Override
                public int compare(SatelliteGraphProperties d1,
                        SatelliteGraphProperties d2) {

                    return d1.getDisplayName().startsWith("all")
                            ? d1.getDisplayName().substring(4).compareToIgnoreCase(d2.getDisplayName().startsWith("all")
                                            ? d2.getDisplayName().substring(4) : d2.getDisplayName())
                            : d1.getDisplayName().compareToIgnoreCase(d2.getDisplayName().startsWith("all")
                                            ? d2.getDisplayName().substring(4) : d2.getDisplayName());
                }
            };
}
