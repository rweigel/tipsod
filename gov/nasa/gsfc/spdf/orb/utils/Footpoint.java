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
 * $Id: Footpoint.java,v 1.17 2015/10/30 14:18:50 rchimiak Exp $
 * Created on May 16, 2007, 10:52 AM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.util.Calendar;
import javax.vecmath.Point3d;
import gov.nasa.gsfc.spdf.ssc.client.BFieldTraceOptions;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.Hemisphere;

/**
 *
 * @author rachimiak
 */
/**
 * Creates a new instance of Footpoint
 */
public class Footpoint {

    public static final double RE = 6371.2;

    /**
     * Creates a new instance of Footpoint
     */
    public Footpoint() {
    }

    public static BFieldTraceOptions createBFieldTraceOptions(CoordinateSystem coord,
            Hemisphere hemisphere,
            boolean latitude,
            boolean longitude,
            boolean arcLenght) {

        BFieldTraceOptions bFieldTraceOptions = new BFieldTraceOptions();
        bFieldTraceOptions.setCoordinateSystem(coord);
        bFieldTraceOptions.setHemisphere(hemisphere);
        bFieldTraceOptions.setFootpointLatitude(latitude);
        bFieldTraceOptions.setFootpointLongitude(longitude);
        bFieldTraceOptions.setFieldLineLength(arcLenght);

        return bFieldTraceOptions;
    }

    public static double[] CartesianToSpherical(double[] car) {

        //   cartesian[x,y,z] --> spehrical[r,theta,phi]
        //    theta = colatitude( =90 degrees - latitude);
        //     phi = longitude;
        double x = car[0];
        double y = car[1];
        double z = car[2];
        double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        if (r == 0d) {

            double[] spher = {0d, 0d, 0d};
            return spher;
        }
        double S = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double theta = Math.acos(z / r);
        double phi = x > 0 ? Math.asin(y / S) : Math.PI - Math.asin(y / S);
        // double[] spher = {r,90 - Math.toDegrees(theta), Math.toDegrees(phi)};
        double[] spher = {r, 90 - Math.toDegrees(theta), (Math.toDegrees(phi) + 360) % 360};
        return spher;
    }

    public static double[] sphericalToCartesian(double[] spher) {

        /*
         *spherical[r,theta,phi] --> cartesian[x,y,z]
         theta = colatitude( =90 degrees - latitude);
         phi = longitude;
         x = rsin(theta)cos(phi)
         y = rsin(theta)sin(phi)
         z = rcos(theta)
         **/
        if (spher[1] < -90 || spher[1] > 90 || spher[2] < -180 || spher[2] > 360) {

            double[] cart = {Double.NaN,
                Double.NaN,
                Double.NaN};

            return cart;
        }

        double rr = (spher[0]);
        double alat = Math.toRadians(90 - spher[1]);
        if (spher[2] < 0) {
            spher[2] += 360;
        }
        double along = Math.toRadians(spher[2]);

        double[] cart = {rr * Math.sin(alat) * Math.cos(along),
            rr * Math.sin(alat) * Math.sin(along),
            rr * Math.cos(alat)
        };

        return cart;
    }

    public static Point3d changeToCoordinateSystem(double[] p,
            Calendar time,
            CoordinateSystem displayCoord) {

        ModifiedJulianCalendar mjc
                = new ModifiedJulianCalendar(time.getTime());

        Point3d point = new Point3d(p);

        Util.rotate(displayCoord, mjc.getMjd(), mjc.getHours(), false).transform(point);

        return point;
    }
}
