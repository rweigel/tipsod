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
 * $Id: PhysicalConstants.java,v 1.6 2015/10/30 14:18:51 rchimiak Exp $
 * Created on March 20, 2007, 3:20 PM
 *
 */
package gov.nasa.gsfc.spdf.orb.utils;

/**
 *
 * @author rachimiak
 */
public class PhysicalConstants {

    public static final double EARTH_RADIUS_KM = 6378.140;
    public static final double MOON_TO_EARTH_RADIUS = 0.272509;
    public static final double MOON_RADIUS_KM = 1738.10;

    public static double kmToRe(double value) {

        return value / EARTH_RADIUS_KM;
    }

    public static double kmToRm(double value) {

        return value / MOON_RADIUS_KM;
    }

    public static double kmToRe(Double value) {

        return value.doubleValue() / EARTH_RADIUS_KM;
    }
}
