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
 * $Id: MHDSurf.java,v 1.24 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on November 18, 2002, 9:27 AM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.content.behaviors.SelenoAnimation;
import gov.nasa.gsfc.spdf.orb.gui.BowshockWindow;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/*
 * MHDSurf.java
 *
 * By Ravi Kulkarni
 * Copyright @2002
 * University of Maryland
 * All Rights Reserved
 */
/*  **** Fairfield's Bow Shock Surface ****
 ****************************************
 po = 2.04
 x0 = 14.3 * rho - 14.462
 rho = (po/psw)**(1/6)
 xmin = -45.0, xmax = 14.462 * rho
 x1 = x-x0
 x2 = 0.0148 * x1 - 0.64
 (r+x2)**2 = 0.038319 * x1**2 - 45.662944 * x + 652.5096
 ***********************************************
 */
/**
 * The MHDSurf class implements the bowshock object to be displayed
 *
 * @author rchimiak
 * @version $Revision: 1.24 $
 */
public class MHDSurf extends MHD {

    private final static int SIZEX = 128;
    private final static int SIZEY = 128;
    private final static double A = 0.0296;
    private final static double B = -0.0381;
    private final static double C = -1.280;
    private final static double D = 45.644;
    private final static double E = -652.10;

    /**
     * Creates the bowshock object based on default values.
     */
    public MHDSurf() {

        super(2.0f, 0.2f, 0.0f, 360.0f, new Color4f(0.0f, 0.4f, 0.0f, 1f));
    }

    public MHDSurf(BowshockWindow bowWind) {

        super(bowWind);

        setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);

        Color3f emissive = new Color3f(0f, 0.851f, 0f);

        mat.setEmissiveColor(emissive);
    }

    public void setSunLight(boolean sunlight) {

        shape.getAppearance().setMaterial(sunlight ? mat : null);
    }

    @Override
    public void buildModel(float psw, float sina,
            float start, float end) {
        QuadArray tetra = new QuadArray(4 * (SIZEX - 1) * (SIZEY - 1),
                QuadArray.COORDINATES
                | QuadArray.NORMALS
                | QuadArray.BY_REFERENCE
                | QuadArray.COLOR_4);

        tetra.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
        tetra.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
        tetra.setCapability(GeometryArray.ALLOW_COUNT_READ);
        shape.setGeometry(tetra);

        setCoordinate(psw, start, end);
        //  doRotation();
    }

    public float frbs(float rho, float x) {

        float r;
        float x0 = 14.3f * rho - 14.462f;
        float x1 = x - x0;

        if (Math.pow(((A * x1 + C)), 2) - 4f * (B * Math.pow(x1, 2) + D * (x1) + E) < 0.) {
            r = 0.f;
        } else {

            r = (float) ((Math.sqrt(Math.pow(((A * x1 + C)), 2) - 4f * (B * Math.pow(x1, 2) + D * (x1) + E)) / 2));
        }

        if (r <= 0.) {
            r = 0.f;
        }

        return r;
    }

    private static float getXmax() {

        float z = (float) (B - A * A / 4f);
        float u = (float) (D - (A * C / 2.d));
        float w = (float) (E - C * C / 4f);
        return (float) (-u + Math.sqrt(u * u - 4f * z * w)) / (float) (2f * z);
    }

    @Override
    public void doInitialTransform() {

        Transform3D tr = new Transform3D();
        tr.rotZ(Math.toRadians(-4.82d));

        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(0d, .3131d, 0d));

        trans.mul(tr);

        this.setTransform(trans);

        if (SelenoAnimation.getEarthAnimation() != null) {

            this.doTranslation(SelenoAnimation.getEarthAnimation().getPositionPath().getTarget());
        }
    }

    @Override
    public void doTranslation(TransformGroup tg) {

        //current rotation + translation transform
        Transform3D trans = new Transform3D();
        this.getTransform(trans);

        // original translation vector        
        Transform3D originalTrans = new Transform3D();
        originalTrans.setTranslation(new Vector3d(0d, .3131d, 0d));

        //new translation vector        
        Transform3D t = new Transform3D();
        tg.getTransform(t);

        // add both translations get vector
        t.mul(originalTrans);
        Vector3f v = new Vector3f();
        t.get(v);

        //set the final translation to this new value
        trans.setTranslation(v);

        this.setTransform(trans);

    }

    /**
     * Set the geometry of the shape)
     */
    @Override
    public void setCoordinate(float psw,
            float start, float end) {

        float r, theta;
        float x, y, z;
        float pi = 3.1415927f;

        Point3d[] points = new Point3d[SIZEX * SIZEY];
        double[] coords = new double[3 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        float[] colors = new float[4 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        float[] normals = new float[3 * 4 * (SIZEX - 1) * (SIZEY - 1)];

        double prsw = psw;
        float rho = (float) Math.pow((2.04f / (float) prsw), (1.f / 6.6f));
        float xmax = getXmax() + 14.3f * rho - 14.462f;

        int ii = 0;
        float x0 = 14.3f * rho - 14.462f;

        for (int i = 0; i < SIZEX; i++) {

            x = (xmax - xmin) * i / (SIZEX - 1) + xmin;
            r = this.frbs(rho, x);

            for (int j = 0; j < SIZEY; j++) {  // Do rotation

                theta = 2 * pi * (start + (end - start) * j / (SIZEY - 1)) / 360.0f;

                y = r * (float) Math.sin(theta);
                z = r * (float) Math.cos(theta);

                points[ii++] = new Point3d(x, y, z);
            }
        }
        Point3d[] vcounts = new Point3d[4 * (SIZEX - 1) * (SIZEY - 1)];

        for (int j = 0; j < (SIZEY - 1); j++) {

            for (int i = 0; i < (SIZEX - 1); i++) {

                vcounts[4 * (SIZEX - 1) * j + 4 * i + 0] = points[(SIZEX) * j + i + 0];
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 1] = points[(SIZEX) * j + i + 1];
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 2] = points[(SIZEX) * (j + 1) + i + 1];
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 3] = points[(SIZEX) * (j + 1) + i + 0];
            }
        }
        int jj = 0;
        int kk = 0;

        for (Point3d vcount : vcounts) {
            coords[jj++] = vcount.x;
            coords[jj++] = vcount.y;
            coords[jj++] = vcount.z;
            colors[kk++] = color.x;
            colors[kk++] = color.y;
            colors[kk++] = color.z;
            colors[kk++] = color.w;
        }
        int nn = 0;
        int cc = 0;

        for (int i = 0; i < vcounts.length / 4; i++) {

            Point3d p1 = vcounts[cc++];
            Point3d p2 = vcounts[cc++];
            Point3d p4 = vcounts[cc++];

            Vector3d alpha = new Vector3d(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
            Vector3d beta = new Vector3d(p4.x - p1.x, p4.y - p1.y, p4.z - p1.z);
            Vector3d gamma = new Vector3d();

            gamma.cross(alpha, beta);
            gamma.normalize();

            for (int n = 0; n < 4; n++) {

                normals[nn++] = (float) (gamma.x);
                normals[nn++] = (float) (gamma.y);
                normals[nn++] = (float) (gamma.z);
            }
        }

        ((QuadArray) getShape().getGeometry()).setCoordRefDouble(coords);

        ((QuadArray) getShape().getGeometry()).setColorRefFloat(colors);
        ((QuadArray) getShape().getGeometry()).setNormalRefFloat(normals);
    }

    public static double olddistanceToBowshock(float gse[]) {

        float rho = (float) Math.pow((2.04 / (float) swp), (1. / 6.6));
        double X0 = 14.3f * rho - 14.462f;

        double XS = (double) gse[0];
        double YS = (double) gse[1];
        double ZS = (double) gse[2];

        double XSA = 0.9976 * XS - 0.0697 * YS;
        double YSA = 0.0697 * XS + 0.9976 * YS;
        double ZSA;

        double XP = 0.999898 * XSA - 0.0143122 * YSA;
        double YP = 0.0143122 * XSA + 0.999898 * YSA;

        XSA = XP;
        YSA = YP;
        ZSA = ZS;

        double sign = YSA < 0 ? -1 : 1;

        YSA = sign * (Math.sqrt(Math.pow((YSA - 0.3131), 2) + Math.pow(ZSA, 2))) + 0.3131;
        double XT = 0.999898 * XSA + 0.0143122 * YSA;
        double YT = -0.0143122 * XSA + 0.999898 * YSA;
        XSA = XT;
        YSA = YT;

        double x1 = XSA - X0;
        double Y = YSA;

        Y = 1.01 * Y;
        double ysh = 0;
        double X;
        double distance = 0;

        for (int n = 2; n < 2000; n++) {

            double dy = 0.01 * Y;

            if (XSA < getXmax() + X0) {

                ysh = -(A * (x1) + C) / 2.0 + sign * Math.sqrt(Math.pow((A * x1 + C), 2) - 4.0 * (B * Math.pow(x1, 2) + D * (x1) + E)) / 2.0;
            }
//if((XSA < getXmax() + X0)&& (Math.abs( ysh)>Math.abs(YSA))){
            // dy = -dy;
//}
            Y = Y - dy;
            X = X0 - (D + A * Y) / (2.0 * B) + Math.sqrt(Math.pow((D + A * Y), 2) - 4.0 * B * (E + Math.pow(Y, 2) + C * Y)) / (2.0 * B);

            double DISQ = Math.pow((XSA - X), 2) + Math.pow((YSA - Y), 2);
            double dist = Math.sqrt(DISQ);

            if (XSA < getXmax() + X0 && Math.abs(YSA) <= Math.abs(ysh)) {
                dist = -dist;
            }
            ysh = -1000.0;
            if (n == 2) {
                distance = 10000;
            }

            if (Math.abs(dist) - Math.abs(distance) >= 0) {

                return distance;
            }
            distance = dist;
        }
        return distance;
    }

    public static double distanceToBowshock(float gse[]) {

        float rho = (float) Math.pow((2.04 / (float) swp), (1. / 6.6));
        double X0 = 14.3f * rho - 14.462f;

        // -------------------------------------------------------------
        //  taking care of the aberration (4 degrees)
        //  -------------------------------------------------------------
        double[] gseAlt = {0.9976 * gse[0] - 0.0697 * gse[1],
            0.0697 * gse[0] + 0.9976 * gse[1],
            gse[2]
        };

        // -------------------------------------------------------------
        //  further rotation of 0.82 degrees
        //  -------------------------------------------------------------
        double XP = 0.999898 * gseAlt[0] - 0.0143122 * gseAlt[1];
        double YP = 0.0143122 * gseAlt[0] + 0.999898 * gseAlt[1];

        gseAlt[0] = XP;
        gseAlt[1] = YP;

        // -------------------------------------------------------------
        //  taking care of the rotation to flatten and stay in the positive Y quadrants
        //  -------------------------------------------------------------
        gseAlt[1] = (Math.sqrt(Math.pow((gseAlt[1] - 0.3131), 2) + Math.pow(gseAlt[2], 2))) + 0.3131;

        double XT = 0.999898 * gseAlt[0] + 0.0143122 * gseAlt[1];
        double YT = -0.0143122 * gseAlt[0] + 0.999898 * gseAlt[1];
        gseAlt[0] = XT;
        gseAlt[1] = YT;

        double x1 = gseAlt[0] - X0;

        double X;
        double distance = 0;
        double stepSize = 0.005;
        double Y0 = stepSize;

        // -------------------------------------------------------------
        //  Is spacecraft inside or outside ? -1 for inside, 1 for outside
        //  -------------------------------------------------------------
        double si = 1;
        if (gseAlt[0] < getXmax() + X0) {

            Y0 = -(A * (x1) + C) / 2.0 + Math.sqrt(Math.pow((A * x1 + C), 2) - 4.0 * (B * Math.pow(x1, 2) + D * (x1) + E)) / 2.0;

            si = gseAlt[1] - Y0 > 0 ? 1 : -1;
        }

        double Y = (1 + stepSize) * Y0;

        for (int n = 2; n < 2000; n++) {

            double dy = stepSize * Y;

            dy = si * dy;
            Y = Y + dy;
            X = X0 - (D + A * Y) / (2.0 * B) + Math.sqrt(Math.pow((D + A * Y), 2) - 4.0 * B * (E + Math.pow(Y, 2) + C * Y)) / (2.0 * B);

            Vector3d v = new Vector3d(gseAlt[0] - X, gseAlt[1] - Y, 0);
            double dist = si * v.length();

            if (n == 2) {
                distance = 10000;
            }

            if (Math.abs(dist) - Math.abs(distance) >= 0) {

                if (n == 3 && stepSize > 0.000005) {

                    stepSize = stepSize / 10;
                    Y = (1 + stepSize) * Y0;
                    n = 1;

                } else {

                    return distance;
                }
            }
            distance = dist;
        }
        return distance;
    }
}
