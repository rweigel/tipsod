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
 * $Id: Lambda.java,v 1.8 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 11, 2002, 8:41 AM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

/**
 * The class Lambda implements the angle for the Sun's mean longitude
 *
 * @author rchimiak
 * @version $Revision: 1.8 $
 */
public class Lambda extends Angle {

    private double lambda = 0.0;
    private double lambda0 = 0.0;

    /**
     * Creates a new instance of Lambda
     */
    public Lambda(double mjd, double hours) {

        super(mjd, hours);
        setLambda();
        setLambda0();
    }

    /**
     * Sets the angle Lambda based on a formula derived from the Almanac for
     * Computers.
     */
    public void setLambda() {

        lambda = 280.460 + 36000.772 * T0 + 0.04107 * H;
    }

    /**
     * Returns M which is the Sun's mean anomality used as an intermediate step
     * in the calculation of the Sun's ecliptic longitude.
     *
     * @return the Sun's mean anomality for a given date and time
     */
    protected double getSunMeanAnomaly() {

        return Math.toRadians(357.528 + 35999.050 * T0 + 0.04107 * H);
    }

    /**
     * Returns the angle Lambda representing the Sun's mean longitude.
     *
     * @return the Sun's mean longitude for a given date and time
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Sets the Sun's ecliptic longitude, using the Sun's mean anomality.
     */
    public void setLambda0() {

        lambda0 = Math.toRadians(lambda + (1.915 - 0.0048 * T0) * Math.sin(getSunMeanAnomaly())
                + 0.020 * Math.sin(2 * getSunMeanAnomaly()));
    }

    /**
     * Returns lambda0, the Sun's ecliptic longitude
     *
     * @return lambda0, the Sun's ecliptic longitude using the Sun's mean
     * longitude
     */
    public double getLambda0() {
        return lambda0;
    }
}
