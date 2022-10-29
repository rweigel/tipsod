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
 * $Id: SunLight.java,v 1.13 2015/10/30 14:18:50 rchimiak Exp $
 * Created on July 12, 2006, 1:28 PM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.utils.Math.P;
import gov.nasa.gsfc.spdf.orb.utils.Math.T1;
import gov.nasa.gsfc.spdf.orb.utils.Math.T2;
import gov.nasa.gsfc.spdf.orb.utils.Math.T3;
import gov.nasa.gsfc.spdf.orb.utils.Math.T4;
import gov.nasa.gsfc.spdf.orb.utils.Math.T5;
import gov.nasa.gsfc.spdf.orb.utils.ModifiedJulianCalendar;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
//import gov.nasa.gsfc.spdf.ssc.client.*;

/**
 *
 * @author rchimiak
 */
public class SunLight extends BranchGroup {

    private final DirectionalLight dl = new DirectionalLight(new Color3f(0.9f, 0.9f, 0.9f), new Vector3f(-1, 0, 0));
    private CoordinateSystem cs = null;

    /**
     * Creates a new instance of SunLight
     */
    public SunLight(final CoordinateSystem cs, final ModifiedJulianCalendar mjc) {

        this.cs = cs;
        BoundingSphere bounds
                = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY);
        AmbientLight al = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
        al.setInfluencingBounds(bounds);
        addChild(al);

        dl.setCapability(Light.ALLOW_COLOR_WRITE);
        dl.setCapability(Light.ALLOW_STATE_WRITE);
        dl.setCapability(Light.ALLOW_STATE_READ);
        dl.setCapability(DirectionalLight.ALLOW_DIRECTION_READ);
        dl.setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);

        dl.setInfluencingBounds(bounds);
        addChild(dl);
    }

    public void rotateDirection(final double mjd, final double hours) {

        Vector3f v = new Vector3f(-1, 0, 0);

        if (cs.equals(CoordinateSystem.GEI_TOD)) {

            Transform3D geiTrans = new Transform3D();
            geiTrans.mulInverse(new T2(mjd, hours));
            geiTrans.transform(v);
            dl.setDirection(v);
        } else if (cs.equals(CoordinateSystem.GEO)) {

            Transform3D geoTrans = new Transform3D();
            geoTrans.mul(new T1(mjd, hours));
            geoTrans.mulInverse(new T2(mjd, hours));
            geoTrans.transform(v);
            dl.setDirection(v);
        } else if (cs.equals(CoordinateSystem.SM)) {

            Transform3D smTrans = new Transform3D();
            smTrans.mul(new T4(mjd, hours));
            smTrans.mul(new T3(mjd, hours));
            smTrans.transform(v);
            dl.setDirection(v);
        } else if (cs.equals(CoordinateSystem.GM)) {

            Transform3D gmTrans = new Transform3D();
            gmTrans.mul(new T5());
            gmTrans.mul(new T1(mjd, hours));
            gmTrans.mulInverse(new T2(mjd, hours));
            gmTrans.transform(v);
            dl.setDirection(v);
        } else if (cs.equals(CoordinateSystem.GEI_J_2000)) {

            Transform3D j2000Trans = new Transform3D();
            j2000Trans.mulInverse(new P(mjd));
            j2000Trans.mulInverse(new T2(mjd, hours));
            j2000Trans.transform(v);
            dl.setDirection(v);
        }

    }

    public DirectionalLight getDirectionalLight() {
        return dl;
    }
}
