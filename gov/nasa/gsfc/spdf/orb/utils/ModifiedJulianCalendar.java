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
 * $Id: ModifiedJulianCalendar.java,v 1.10 2020/10/19 21:06:33 rchimiak Exp $
 *
 * Created on October 10, 2002, 1:01 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The ModifiedJulianCalendar class implements the Modified Julian Calendar used
 * to represent a Modified Julian Date
 *
 * @author rchimiak
 * @version $Revision: 1.10 $
 */
public class ModifiedJulianCalendar extends GregorianCalendar {

    /**
     * Gregorian date of 17 November 1858, representing the beginning of the
     * Modified Julian Calendar
     */
    public static final GregorianCalendar start = new GregorianCalendar(1858, 10, 17, 0, 0, 0);
    /**
     * number of millisecond in a day
     */
    public static final int milliSecInDay = 1000 * 60 * 60 * 24;
    private double mjd = 0.0;

    /**
     * Creates a new instance of a JulianCalendar
     */
    public ModifiedJulianCalendar() {

        super(Util.UTC_TIME_ZONE);
        start.setTimeZone(Util.UTC_TIME_ZONE);
    }

    /**
     * Creates a new instance of a JulianCalendar
     *
     * @param date the time of interest expressed as a Gregorian date
     */
    public ModifiedJulianCalendar(java.util.Date date) {

        this();
        this.setTime(date);
    }

    /**
     * Returns mjd, the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858. It is normally used to specify slowly
     * changing quantities(annual or longer).
     *
     * @return The modified Julian Date
     */
    public double getMjd() {

        mjd = ((double) getTimeInMillis() - (double) start.getTimeInMillis()) / milliSecInDay;
        return mjd;
    }

    /**
     * Returns the number of hours including fraction since the UT midnight
     * preceding the day of interest.
     *
     * @return a number of hours since midnight
     */
    public double getHours() {

        double h = get(Calendar.HOUR_OF_DAY);
        double m = get(Calendar.MINUTE);

        return (h + (m / 60));
    }
}
