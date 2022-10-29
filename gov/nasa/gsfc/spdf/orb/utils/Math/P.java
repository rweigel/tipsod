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
 * $Id: P.java,v 1.7 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 11, 2002, 2:15 PM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

import javax.media.j3d.Transform3D;

/**
 * The P class implements the transformation between the GEI2000 and GEI
 * geocentric coordinate systems.
 *
 * @author rchimiak
 * @version $Revision: 1.7 $
 */
public class P extends Transform3D {

    private double zA = 0.0;
    private double thetaA = 0.0;
    private double zetaA = 0.0;

    /**
     * Creates a new instance of P
     *
     * @param mjd the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858
     */
    public P(double mjd) {

        calculateProcessionAngle(mjd);

        this.rotZ(zA);
        Transform3D thetaRot = new Transform3D();
        thetaRot.rotY(-thetaA);
        this.mul(thetaRot);
        Transform3D zetaRot = new Transform3D();
        zetaRot.rotZ(zetaA);
        this.mul(zetaRot);
    }

    /**
     * Calculates the three procession angles (zA, thetaA and zetaA) based on a
     * formula derived from the Astronomical Almanac
     *
     * @param mjd the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858
     */
    public void calculateProcessionAngle(double mjd) {

        Angle a = new Angle(mjd);

        double T0 = a.getT0();

        double T0Square = Math.pow(T0, 2);

        zA = Math.toRadians(0.64062 * T0 + 0.00030 * T0Square);
        thetaA = Math.toRadians(0.55675 * T0 - 0.00012 * T0Square);
        zetaA = Math.toRadians(0.64062 * T0 + 0.00008 * T0Square);
    }
}
