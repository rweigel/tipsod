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
 * $Id: Epsilon.java,v 1.7 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 11, 2002, 9:25 AM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

/**
 * the Epsilon class implements the angle for the obliquity of the sun's
 * ecliptic longitude
 *
 * @author rchimiak
 * @version $Revision: 1.7 $
 */
public class Epsilon extends Angle {

    private double epsilon = 0.0;

    /**
     * Creates a new instance of Epsilon.
     */
    public Epsilon(double mjd) {

        super(mjd);
        setEpsilon();
    }

    /**
     * Sets the angle epsilon based on a formula derived from the Almanac for
     * Computers.
     */
    public void setEpsilon() {

        epsilon = Math.toRadians(23.439 - 0.013 * T0);
    }

    /**
     * Returns the obliquity of the Sun's ecliptic longitude.
     *
     * @return the obliquity of the Sun's ecliptic longitude, based on a formula
     * derived from the Almanac for Computers
     */
    public double getEpsilon() {
        return epsilon;
    }
}
