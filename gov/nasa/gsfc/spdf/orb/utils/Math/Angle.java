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
 * $Id: Angle.java,v 1.6 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 11, 2002, 8:40 AM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

/**
 * Calculates the T0 and H parameters common to earth rotation angles
 *
 * @author rchimiak
 * @version $Revision: 1.6 $
 */
public class Angle {

    /**
     * The time in Julian centuries from 12:00 UT on 1 January 2000 to the
     * midnight Universal Time (UT) preceding the time of interest.
     */
    protected double T0 = 0.0;
    /**
     * The time in hours since that preceding UT midnight.
     */
    protected double H = 0.0;

    /**
     * Creates a new instance of Angles at time 1 January 2000
     */
    public Angle() {
    }

    /**
     * Creates a new instance of Angles given an mjd date and assuming UT
     * midnight.
     *
     * @param mjd Time measured in days from 00:00 UT on 17 November 1858
     * (Julian Date 2400000.5)
     */
    public Angle(double mjd) {

        T0 = (mjd - 51544.5) / 36525.0;
    }

    /**
     * Creates a new instance of Angles given both an mjd date and a number of
     * hours since the preceding UT midnight.
     *
     * @param mjd Time measured in days from 00:00 UT on 17 November 1858
     * (Julian Date 2400000.5)
     * @param hours Time in hours since that preceding UT midnight
     */
    public Angle(double mjd, double hours) {

        T0 = (mjd - 51544.5) / 36525.0;
        H = hours;

    }

    /**
     * Returns T0 which depends on the date used in the calculation of angles of
     * rotation.
     *
     * @return The time in Julian centuries from 12:00 UT on 1 January 2000 to
     * the midnight Universal Time (UT) preceding the time of interest
     *
     */
    public double getT0() {
        return T0;
    }

    /**
     * Returns H representing a number of hours and fraction thereof used in the
     * calculation of angles of rotation.
     *
     * @return The time in hours for the time of interest since UT midnight
     */
    public double getH() {
        return H;
    }
}
