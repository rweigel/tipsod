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
 * $Id: RotationTransform.java,v 1.5 2020/10/19 21:06:33 rchimiak Exp $
 * Created on May 15, 2007, 3:27 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.utils.Math.P;
import gov.nasa.gsfc.spdf.orb.utils.Math.T1;
import gov.nasa.gsfc.spdf.orb.utils.Math.T2;
import gov.nasa.gsfc.spdf.orb.utils.Math.T3;
import gov.nasa.gsfc.spdf.orb.utils.Math.T4;
import gov.nasa.gsfc.spdf.orb.utils.Math.T5;
import javax.media.j3d.Transform3D;

/**
 *
 * @author rachimiak
 */
public class RotationTransform {

    /**
     * Creates a new instance of RotationTransform
     */
    public RotationTransform() {
    }

    public static Transform3D GEOTransform() {

        Transform3D rotx = new Transform3D();
        rotx.rotX(Math.PI / 2);

        Transform3D rotz = new Transform3D();
        rotz.rotZ(Math.PI / 2);

        Transform3D geoTrans = new Transform3D();
        geoTrans.mul(rotz, rotx);

        return geoTrans;
    }

    public static Transform3D GEITransform(double mjd, double hours) {

        Transform3D geiTrans = new Transform3D();
        
        geiTrans.mulInverse(new T1(mjd, hours));

        return geiTrans;
    }

    public static Transform3D GSETransform(double mjd, double hours) {

        Transform3D gseTrans = new Transform3D();
        gseTrans.mul(new T2(mjd, hours), GEITransform(mjd, hours));

        return gseTrans;
    }

    public static Transform3D GSMTransform(double mjd, double hours) {

        Transform3D gsmTrans = new Transform3D();
        gsmTrans.mul(new T3(mjd, hours), GSETransform(mjd, hours));

        return gsmTrans;
    }

    public static Transform3D SMTransform(double mjd, double hours) {

        Transform3D smTrans = new Transform3D();
        smTrans.mul(new T4(mjd, hours), GSMTransform(mjd, hours));

        return smTrans;
    }

    public static Transform3D GMTransform() {

        Transform3D gmTrans = new T5();

        return gmTrans;
    }

    public static Transform3D GEI2000Transform(double mjd, double hours) {

        Transform3D gei2000Trans = new Transform3D();

        gei2000Trans.mulInverse(new P(mjd));
        gei2000Trans.mul(gei2000Trans, GEITransform(mjd, hours));

        return gei2000Trans;
    }
}
