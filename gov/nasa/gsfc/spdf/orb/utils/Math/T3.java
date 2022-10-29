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
 * $Id: T3.java,v 1.8 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 11, 2002, 2:14 PM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

import javax.media.j3d.Transform3D;

/**
 * The T3 class implements the transformation between the GSE and GSM geocentric
 * coordinate systems.
 *
 * @author rchimiak
 * @version $Revision: 1.8 $
 */
public class T3 extends Transform3D {

    /**
     * Creates a new instance of T3
     *
     * @param mjd the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858
     * @param hours is the time in hours since that preceding UT midnight
     */
    public T3(double mjd, double hours) {

        Psi psi = new Psi(mjd, hours);
        this.rotX(psi.getPsi());
    }
}
