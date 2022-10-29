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
 * $Id: MHDPause.java,v 1.22 2020/10/19 21:06:33 rchimiak Exp $
 * Created on November 18, 2002, 9:30 AM
 */
package gov.nasa.gsfc.spdf.orb.utils;

/*
 * MHDPause.java
 *
 * Created on June 7, 2000, 10:08 AM
 * By Ravi Kulkarni
 * Copyright @2000
 * University of Maryland
 * All Rights Reserved
 */
import javax.media.j3d.*;
import javax.vecmath.*;

import gov.nasa.gsfc.spdf.orb.gui.*;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SelenoAnimation;


/*  ****************************************
 **** Sibeck's Magnetopause Surface ****
 ****************************************
 po = 2.04
 s1 = 0.14
 s2 = 18.2
 s3 = -217.2
 t1 = s2/(2 s1) = 65.0
 t2 = sqrt(t1**2 - s3/s1) = 76.0028195
 xmin = -45.0
 rho = (po/psw)**(1/6)
 r**2 = y**2+z**2 = s1*[(t2*rho)**2-(x+t1*rho)**2]
 ***********************************************
 */
public class MHDPause extends MHD {

    private final static int SIZEX = 128;
    private final static int SIZEY = 128;

    public MHDPause() {
        super(2.0f, 0.2f, 0.0f, 360.0f, new Color4f(1.0f, 1.0f, 0.0f, 0.4f));
    }

    public MHDPause(MagnetopauseWindow magWind) {

        super(magWind);

        Color3f emissive = new Color3f(1f, 1f, 204f / 255f);
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
        // doRotation();
    }

    public float frmp(float rho, float x) {

        float s1 = 0.14f;
        float t1 = 65.0f;
        float t2 = 76.0028195f;
        float r;
        if (x < -65) {
            x = -65;
        }
        float t = s1 * (float) (Math.pow(t2 * rho, 2) - Math.pow((x + t1 * rho), 2));

        if (t <= 0.0f) {
            r = 0.0f;
        } else {

            r = (float) Math.sqrt(t);
        }
        return r;
    }

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

        float rho = (float) Math.pow((2.04 / (float) prsw), (1. / 6.0));

        float xmax = 11.0028195f * rho;
        float cmin = xmin;
        int ii = 0;

        for (int i = 0; i < SIZEX; i++) {

            if (i == 0) {

                x = xmin;

                r = this.frmp(rho, cmin);

            } else {

                x = ((xmax - cmin) * (i - 1) / (SIZEX - 2) + cmin);

                r = this.frmp(rho, x);

            }
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
            Point3d p3 = vcounts[cc++];
            Point3d p4 = vcounts[cc++];

            Vector3d A = new Vector3d(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
            Vector3d B = new Vector3d(p4.x - p1.x, p4.y - p1.y, p4.z - p1.z);
            Vector3d C = new Vector3d();

            C.cross(A, B);
            C.normalize();

            for (int n = 0; n < 4; n++) {

                normals[nn++] = (float) C.x;
                normals[nn++] = (float) C.y;
                normals[nn++] = (float) C.z;
            }
        }

        ((QuadArray) getShape().getGeometry()).setCoordRefDouble(coords);

        ((QuadArray) getShape().getGeometry()).setColorRefFloat(colors);
        ((QuadArray) getShape().getGeometry()).setNormalRefFloat(normals);
    }

    @Override
    public void doInitialTransform() {

        Transform3D tr = new Transform3D();
        tr.rotZ(Math.toRadians(-4.d));

        this.setTransform(tr);
        if (SelenoAnimation.getEarthAnimation() != null) {
            this.doTranslation(SelenoAnimation.getEarthAnimation().getPositionPath().getTarget());
        }
    }

    @Override
    public void doTranslation(TransformGroup tg) {

        //current rotation + translation transform
        Transform3D trans = new Transform3D();
        this.getTransform(trans);

        //new translation vector        
        Transform3D t = new Transform3D();
        tg.getTransform(t);
        Vector3f v = new Vector3f();
        t.get(v);

        //set the final translation to this new value
        trans.setTranslation(v);

        this.setTransform(trans);

    }

    /*   public static double distanceToMagnetopause(float gse[]) {

     double S1 = 0.14;
     float rho = (float) Math.pow((2.04 / (float) swp), (1. / 6.6));
     double S2 = 18.2 * rho;
     double S3 = -217.2 * rho * rho;


     // -------------------------------------------------------------
     //  taking care of the aberation (4 degrees)
     //  -------------------------------------------------------------

     double[] gseAlt = {0.9976 * gse[0] - 0.0697 * gse[1],
     0.0697 * gse[0] + 0.9976 * gse[1],
     gse[2]
     };

     double sign = gseAlt[1] < 0 ? -1 : 1;

     gseAlt[1] = sign * (Math.sqrt(Math.pow(gseAlt[1], 2) + Math.pow(gse[2], 2)));


     double T1 = S1 + Math.pow((gseAlt[1] / gseAlt[0]), 2);
     double X0 = -S2 / (2.0d * T1) + Math.sqrt(S2 * S2 - 4.0d * T1 * S3) / (2.0d * T1);

     if (gseAlt[0] < 0) {

     X0 = -S2 / (2.0d * T1) - Math.sqrt(S2 * S2 - 4.0d * T1 * S3) / (2.0d * T1);
     }
     double Y0 = X0 * (gseAlt[1] / gseAlt[0]);

     double distance = 0;

     if (gseAlt[0] < -40.0) {

     distance = Math.abs(gseAlt[1]) - 27.107;
     }

     double stepSize = .005;

     double dis = Math.sqrt(((gseAlt[0] * gseAlt[0]) + (gseAlt[1] * gseAlt[1]))) - Math.sqrt((X0 * X0) + (Y0 * Y0));
     if (Math.abs(dis) < 1)  stepSize = .0005;
     if (Math.abs(dis) < 0.5)  stepSize = .00005;

     double si = dis < 0 ? -1 : 1;

     double Y = (1 + stepSize) * Y0;
     double X = 0;

     for (int n = 2; n < 2000; n++) {

     double dy = stepSize * Y;

     dy = si * dy;

     Y = Y + dy;


     if ((S2 * S2 - 4.0 * S1 * (S3 + Y * Y)) < 0) {

     return distance;
     }
     X = -S2 / (2.0d * S1) + Math.sqrt(S2 * S2 - 4.0d * S1 * (S3 + Y * Y)) / (2.0d * S1);

     Vector3d v = new Vector3d(gseAlt[0] - X, gseAlt[1] - Y, 0);
     double dist = si * v.length();

     if (n == 2) {
     distance = 10000;
     }


     if (Math.abs(dist) - Math.abs(distance) >= 0) {

     return distance;

     }

     distance = dist;

     }

     return distance;
     }*/
    public static double distanceToMagnetopause(float gse[]) {

        double S1 = 0.14;
        float rho = (float) Math.pow((2.04 / (float) swp), (1. / 6.6));
        double S2 = 18.2 * rho;
        double S3 = -217.2 * rho * rho;

        // -------------------------------------------------------------
        //  taking care of the aberation (4 degrees)
        //  -------------------------------------------------------------
        double[] gseAlt = {0.9976 * gse[0] - 0.0697 * gse[1],
            0.0697 * gse[0] + 0.9976 * gse[1],
            gse[2]
        };

        // -------------------------------------------------------------
        //  taking care of the rotation to flatten and staying in the positive Y quadrants
        //  -------------------------------------------------------------
        gseAlt[1] = (Math.sqrt(Math.pow(gseAlt[1], 2) + Math.pow(gse[2], 2)));

        double X0;
        double Y0;
        double distance = 0;
        double stepSize = 0.005;

        // -------------------------------------------------------------
        //  X, Y intersection of spacecraft position with mag
        //  line slope = YSA/XSA   = Y/X
        //  Y = X*YSA/XSA
        //  plug it in function: (S1 + YSA/XSA)**2)*X**2 + S2*X + S3 = 0
        //  T1* X**2 + S2 * X + S3 = 0
        //  T1 is parameter for X square
        //  -------------------------------------------------------------
        if (gseAlt[0] > 0) {

            double T1 = S1 + Math.pow((gseAlt[1] / gseAlt[0]), 2);

            // -------------------------------------------------------------
            // get the X0 roots of line (spacecraft, earth intersecting with mag)
            // solve function  T1 * x**2 + S2 * X + S3 = 0 with respect to X
            // gives Xs where line between spacecraft and earth crosses the mag
            //  -------------------------------------------------------------
            X0 = (-S2 / (2.0d * T1)) + (Math.sqrt(S2 * S2 - 4.0d * T1 * S3) / (2.0d * T1));
            Y0 = Math.abs(X0 * (gseAlt[1] / gseAlt[0]));

        } else {

            // -------------------------------------------------------------
            // get the Y0 positive root of line where slope is infinite (X0 = gseAlt[0])
            //  -------------------------------------------------------------
            X0 = gseAlt[0];
            Y0 = Math.sqrt(-S1 * gseAlt[0] * gseAlt[0] - S2 * gseAlt[0] - S3);

        }

        if (gseAlt[0] < -42.024) {
            distance = Math.abs(gseAlt[1]) - 27.107;

            return distance;
        }

        // -------------------------------------------------------------
        //  check out if spacecraft in or out of the mag
        //  -------------------------------------------------------------
        double si = ((gseAlt[0] * gseAlt[0]) + (gseAlt[1] * gseAlt[1])) - ((X0 * X0) + (Y0 * Y0)) < 0 ? -1 : 1;

        double Y = (1 + stepSize) * Y0;
        double X;

        for (int n = 2; n < 2000; n++) {

            double dy = stepSize * Y;

            dy = si * dy;
            Y = Y + dy;
            // -------------------------------------------------------------
            //  calculate the intersection of a line with a greater y and the mag
            // find the X value by finding the root of a line going from
            //  the spacecraft to the new Y
            // the function is : S1*X**2 + S2*X +S3 +Y**2 = 0
            //  -------------------------------------------------------------

            if ((S2 * S2 - 4.0 * S1 * (S3 + Y * Y)) < 0) {

                return distance;
            }

            X = -S2 / (2.0d * S1) + Math.sqrt(S2 * S2 - 4.0d * S1 * (S3 + Y * Y)) / (2.0d * S1);

            // -------------------------------------------------------------
            //  find the length of this new line from spacecraft to mag
            //  -------------------------------------------------------------
            Vector3d v = new Vector3d(gseAlt[0] - X, gseAlt[1] - Y, 0);
            double dist = si * v.length();

            if (n == 2) {
                distance = 10000;
            }

            // -------------------------------------------------------------
            //  we have gone too far
            //  -------------------------------------------------------------
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
