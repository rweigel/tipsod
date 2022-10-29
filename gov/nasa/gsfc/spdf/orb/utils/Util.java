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
 * $Id: Util.java,v 1.23 2020/10/19 21:06:33 rchimiak Exp $
 * Created on August 7, 2007, 10:54 AM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.awt.Font;
import java.util.SimpleTimeZone;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import javax.media.j3d.Transform3D;
import javax.media.j3d.LineAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import com.sun.j3d.utils.picking.PickIntersection;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.orb.content.shapes.PositionShape;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingUtilities;
import sun.awt.AppContext;

/**
 *
 * @author rachimiak
 */
public class Util {

    public static final Font labelFont = new Font("Comic Sans MS", Font.BOLD, 14);
    public static final Font modelFont = new Font("Comic Sans MS", Font.BOLD, 12);
    public static final Font comicPlain14Font = new Font("Comic Sans MS", Font.PLAIN, 14);
    public static final Font comicPlain12Font = new Font("Comic Sans MS", Font.PLAIN, 12);
    public static final Font plainFont = new Font("Dialog.plain", Font.PLAIN, 12);
    public final static int XY = 1;
    public final static int ZY = 2;
    public final static int XZ = 3;
    public final static int CHECKBOX_SWITCHES_COUNT = 18;
    public final static int ORBIT_INDEX = 0;
    public final static int EARTH_GROUP_INDEX = 1;
    public final static int AXIS_INDEX = 2;
    public final static int MAJOR_TICKS_INDEX = 3;
    public final static int AXIS_TXT_INDEX = 4;
    public final static int MAGNETOPAUSE_INDEX = 5;
    public final static int BOWSHOCK_INDEX = 6;
    public final static int NEUTRAL_SHEET_INDEX = 7;
    public final static int XYGRID_INDEX = 8;
    public final static int YZGRID_INDEX = 9;
    public final static int XZGRID_INDEX = 10;
    public final static int FOOTPOINTS_NORTH_INDEX = 11;
    public final static int FOOTPOINTS_SOUTH_INDEX = 12;
    public final static int FOOTPOINTS_CLOSEST_INDEX = 13;
    public final static int GEO_GRID_INDEX = 14;
    public final static int GROUND_STATIONS_INDEX = 15;
    public final static int SUNLIGHT_INDEX = 16;
    public final static int ORBIT_TOGGLE_POSITION = 0;
    public final static int EARTH_TOGGLE_POSITION = 1;
    public final static int AXIS_TOGGLE_POSITION = 2;
    public final static int AXIS_TXT_TOGGLE_POSITION = 3;
    public final static int PLANAR_VIEWS_TOGGLE_POSITION = 4;
    public final static int TOOLBAR_TOGGLE_POSITION = 5;
    public final static int MAGNETOPAUSE_TOGGLE_POSITION = 6;
    public final static int BOWSHOCK_TOGGLE_POSITION = 7;
    public final static int NEUTRAL_SHEET_TOGGLE_POSITION = 8;
    public final static int XYGRID_TOGGLE_POSITION = 9;
    public final static int YZGRID_TOGGLE_POSITION = 10;
    public final static int XZGRID_TOGGLE_POSITION = 11;
    public final static int FOOTPOINTS_NORTH_TOGGLE_POSITION = 12;
    public final static int FOOTPOINTS_SOUTH_TOGGLE_POSITION = 13;
    public final static int FOOTPOINTS_CLOSEST_TOGGLE_POSITION = 14;
    public final static int GEO_GRID_TOGGLE_POSITION = 15;
    public final static int GROUND_STATIONS_TOGGLE_POSITION = 16;
    public final static int SUNLIGHT__TOGGLE_POSITION = 17;

    private static AppContext evtContext;
    /**
     * A UTC TimeZone.
     */
    public static final SimpleTimeZone UTC_TIME_ZONE
            = new SimpleTimeZone(0, "UTC");

    public final static SimpleDateFormat formatter = getTimeFormatter();

    /**
     * Creates a new instance of Util.
     */
    public Util() {
    }

    private static SimpleDateFormat getTimeFormatter() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        format.setTimeZone(UTC_TIME_ZONE);
        return format;
    }

    /**
     * Positions the earth according to time and coordinate system.
     *
     * @param mjd the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858
     * @param hours is the time in hours since that preceding UT midnight
     */
    public static Transform3D rotate(CoordinateSystem cs, double mjd, double hours, boolean earth) {

        Transform3D rot = new Transform3D();

        if (earth) {
           rot.set(RotationTransform.GEOTransform());
        }

        if (cs.equals(CoordinateSystem.GEI_TOD)) {

            rot.mul(RotationTransform.GEITransform(mjd, hours), rot);
        } else if (cs.equals(CoordinateSystem.GSE)) {

            rot.mul(RotationTransform.GSETransform(mjd, hours), rot);

        } else if (cs.equals(CoordinateSystem.GSM)) {

            rot.mul(RotationTransform.GSMTransform(mjd, hours), rot);
        } else if (cs.equals(CoordinateSystem.SM)) {

            rot.mul(RotationTransform.SMTransform(mjd, hours), rot);
        } else if (cs.equals(CoordinateSystem.GM)) {

            rot.mul(RotationTransform.GMTransform(), rot);
        } else if (cs.equals(CoordinateSystem.GEI_J_2000)) {

            rot.mul(RotationTransform.GEI2000Transform(mjd, hours), rot);
        }

        return rot;
    }

    /**
     * Returns the pattern selected by the user for the plot
     *
     * @param pattern string representing a linePattern ( could be: Dash,
     * Dash-Dot, Dot,or solid)
     * @return a representation of a line pattern
     */
    public static int getLinePattern(String pattern) {

        if (pattern.equalsIgnoreCase("Dash")) {
            return LineAttributes.PATTERN_DASH;
        }

        if (pattern.equalsIgnoreCase("Dash_Dot")) {
            return LineAttributes.PATTERN_DASH_DOT;
        }

        if (pattern.equalsIgnoreCase("Dot")) {
            return LineAttributes.PATTERN_DOT;
        } else {
            return LineAttributes.PATTERN_SOLID;
        }
    }

    public static Color invertColor(Color color) {
        return new Color(color.getRGB() ^ 0xffffff);
    }

    public static Color3f invertColor(Color3f color3f) {
        return new Color3f(invertColor(color3f.get()));
    }

    public static Color getRandomColor() {

        return new Color(new Random().nextInt(256),
                new Random().nextInt(256),
                new Random().nextInt(256));
    }

    /*      public static long interpolateOrbitTime(PickIntersection pi, Point3d p, int vi, int vc) {
     long adjust = 0;
     Point3d p1 = new Point3d();
     Point3d p2 = new Point3d();

     if (vi > 0 && vc > 1 && vc - 1 > vi) {

     pi.getGeometryArray().getCoordinate(vi - 1, p1);

     pi.getGeometryArray().getCoordinate(vi + 1, p2);
     double d = Math.abs(p2.distance(p1));
     double dist = Math.abs(p.distance(p1));

     adjust = dist == 0 ? 0 : (long) ((Animation.getTotalTime() * 2 / (vc - 1d)) * (dist / d));
     } else if (vi == 0) {

     pi.getGeometryArray().getCoordinate(vi, p1);
     pi.getGeometryArray().getCoordinate(vi + 1, p2);
     double d = Math.abs(p2.distance(p1));
     double dist = Math.abs(p.distance(p1));
     adjust = dist == 0 ? 0 : (long) ((Animation.getTotalTime() * 1 / (vc - 1d)) * (dist / d));

     return Animation.getBeginning().getTimeInMillis() + adjust;
     } else if (vc - 1 == vi) {

     pi.getGeometryArray().getCoordinate(vi - 1, p1);
     pi.getGeometryArray().getCoordinate(vi, p2);
     double d = Math.abs(p2.distance(p1));
     double dist = Math.abs(p.distance(p1));
     adjust = Math.abs(p.distance(p1)) == 0 ? 0 : (long) ((Animation.getTotalTime() * 1 / (vc - 1d)) * (dist / d));

     } else {
     adjust = 0;
     }

     return Animation.getBeginning().getTimeInMillis()
     + ((Animation.getTotalTime() * (vi - 1) / (vc - 1))) + adjust;


     }*/
    public static long interpolateOrbitTime(PositionShape node, PickIntersection pi) {
        long adjust;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();

        int vi = pi.getPrimitiveVertexIndices()[pi.getClosestVertexIndex()];
        int gi = pi.getGeometryArrayIndex();
        int vc = (pi.getGeometryArray()).getVertexCount();

        Point3d p = pi.getPointCoordinates();

        long[] timeBoundaries = node.getTimeBoundaries(gi);
        long totalTime = timeBoundaries[1] - timeBoundaries[0];

        if (vi > 0 && vc > 1 && vc - 1 > vi) {

            pi.getGeometryArray().getCoordinate(vi - 1, p1);

            pi.getGeometryArray().getCoordinate(vi + 1, p2);
            double d = Math.abs(p2.distance(p1));
            double dist = Math.abs(p.distance(p1));

            adjust = dist == 0 ? 0 : (long) ((totalTime * 2 / (vc - 1d)) * (dist / d));
        } else if (vi == 0) {

            pi.getGeometryArray().getCoordinate(vi, p1);
            pi.getGeometryArray().getCoordinate(vi + 1, p2);
            double d = Math.abs(p2.distance(p1));
            double dist = Math.abs(p.distance(p1));
            adjust = dist == 0 ? 0 : (long) ((totalTime * 1 / (vc - 1d)) * (dist / d));

            return timeBoundaries[0] + adjust;
        } else if (vc - 1 == vi) {

            pi.getGeometryArray().getCoordinate(vi - 1, p1);
            pi.getGeometryArray().getCoordinate(vi, p2);
            double d = Math.abs(p2.distance(p1));
            double dist = Math.abs(p.distance(p1));
            adjust = Math.abs(p.distance(p1)) == 0 ? 0 : (long) ((totalTime * 1 / (vc - 1d)) * (dist / d));

        } else {
            adjust = 0;
        }

        return timeBoundaries[0]
                + ((totalTime * (vi - 1) / (vc - 1))) + adjust;

    }

    /**
     * Round a double value to a specified number of decimal places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return value rounded to places decimal places.
     */
    public static double round(double val, int places) {

        long factor = (long) Math.pow(10, places);

        // Shift the decimal the correct number of places
        // to the right.
        val = val * factor;

        // Round to the nearest integer.
        long tmp = Math.round(val);

        // Shift the decimal the correct number of places
        // back to the left.
        return (double) tmp / factor;
    }

    /**
     * Round a float value to a specified number of decimal places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return string rounded to places decimal places.
     */
    public static String roundToString(double val, int places) {

        long factor = (long) Math.pow(10, places);

        // Shift the decimal the correct number of places
        // to the right.
        val = val * factor;

        // Round to the nearest integer.
        long tmp = Math.round(val);

        // Shift the decimal the correct number of places
        // back to the left and return formatted as a String.
        return new DecimalFormat("################0.000").format((double) tmp / factor);
    }
    
    public static double getVersion() {
    String version = System.getProperty("java.version");
   
    return Double.parseDouble (version.substring (0, Math.min(version.length(), 3)));
}

    /**
     * Round a float value to a specified number of decimal places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return value rounded to places decimal places.
     */
    public static float round(float val, int places) {
        return (float) round((double) val, places);
    }

    public static void saveAppContext() {

        evtContext = AppContext.getAppContext();

    }

    /**
     * Work around for a Java Web start problem that was introduced with Java 7
     * update 25.
     */
    
    public static void invokeLater2(Runnable rn) {

/* old code
    if (AppContext.getAppContext() == null) {
//        sun.awt.SunToolkit.invokeLaterOnAppContext(evtContext, rn);
    } else {
        SwingUtilities.invokeLater(rn);
    }
*/
        Class<?> sunToolkitClass = null;

        try {

            sunToolkitClass = Class.forName("sun.awt.SunToolkit");
        }
        catch (ClassNotFoundException e) {

            // ignore - expected on some platforms
        }

        if (AppContext.getAppContext() == null &&
            sunToolkitClass != null) {

            Class<?>[] parameters = 
                new Class<?>[] {AppContext.class , Runnable.class};

            try {

                Method invokeLaterOnAppContext =
                    sunToolkitClass.getDeclaredMethod(
                        "invokeLaterOnAppContext", parameters);

                invokeLaterOnAppContext.invoke(null, evtContext, rn);
            }
            catch (NoSuchMethodException e) {

                System.err.println(
                    "Util: sun.awt.SunToolkit.getDeclaredMethod() " +
                    "failed: " + e.getMessage());
            }
            catch (IllegalAccessException e) {

                System.err.println(
                    "Util: sun.awt.SunToolkit.invokeLaterOnAppContext() " +
                    "failed: " + e.getMessage());
            }
            catch (InvocationTargetException e) {

                System.err.println(
                    "Util: sun.awt.SunToolkit.invokeLaterOnAppContext() " +
                    "failed: " + e.getMessage());
            }
        }
        else {

            SwingUtilities.invokeLater(rn);
        }

    }
}
