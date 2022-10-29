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
 * $Id: Qe.java,v 1.7 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 11, 2002, 10:34 AM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

import javax.vecmath.Vector3d;

/**
 * The Qe class implements a unit vector Qe describing the direction of the
 * geomagnetic dipole axis in the GSE coordinate system.
 *
 * @author rchimiak
 * @version $Revision: 1.7 $
 */
public class Qe extends Vector3d {

    /**
     * GEO latitude of the geomagnetic pole
     */
    public final static double lat = Math.toRadians(79.749);
    /**
     * GEO longitude of the geomagnetic pole.
     */
    public final static double longi = Math.toRadians(288.265);

    /**
     * Creates a new instance of Qe.
     *
     * @param mjd represents a Modified Julian Date which is the time measured
     * in days from 00:00 UT on 17 November 1858 (Julian Date 2400000.5)
     * @param hours is the time in hours since that preceding UT midnight
     */
    public Qe(double mjd, double hours) {

        super(Math.cos(lat) * Math.cos(longi),
                Math.cos(lat) * Math.sin(longi), Math.sin(lat));

        T2 t2 = new T2(mjd, hours);
        T1 t1 = new T1(mjd, hours);

        t2.mulInverse(t1);
        t2.transform(this);

    }
}
