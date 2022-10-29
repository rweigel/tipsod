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
 * $Id: Theta.java,v 1.9 2020/10/19 21:06:33 rchimiak Exp $
 *
 * Created on October 11, 2002, 8:40 AM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

/**
 * The Theta class implements the Greenwich sidereal time angle.
 *
 * @author rchimiak
 * @version $Revision: 1.9 $
 */
public class Theta extends Angle {

    private double theta = 0.0;

    /**
     * Creates a new instance of Theta
     */
    public Theta(double mjd, double hours) {
        super(mjd, hours);
        setTheta();
    }

    /**
     * Calculates the Greenwich sidereal time angle based on a formula drived
     * from the Almanac for Computer
     */
    public void setTheta() {

        theta = 100.461 + 36000.770 * T0 + 15.04107 * H;

    }

    /**
     * Returns the Greenwich sidereal time angle.
     *
     * @return theta
     */
    public double getTheta() {
      
        return Math.toRadians(theta);
    }
}
