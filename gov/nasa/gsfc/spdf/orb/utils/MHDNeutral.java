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
 * $Id: MHDNeutral.java,v 1.20 2015/10/30 14:18:51 rchimiak Exp $
 * Created on June 22, 2006, 1:10 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.gui.NeutralSheetWindow;
import gov.nasa.gsfc.spdf.orb.utils.Math.Mu;
import gov.nasa.gsfc.spdf.orb.utils.Math.T3;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.QuadArray;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author rchimiak
 */
public class MHDNeutral extends MHD implements GeometryUpdater {

    //  private boolean visible = false;
    //  private boolean firstTime = true;
    private final double xmax = -1.5;
    private final static int SIZEX = 64;
    private final static int SIZEY = 64;
    private final double[] Xs = new double[SIZEX];
    private final double[] Ys = new double[SIZEX];
    private double[] coords = null;
    private float[] colors = null;
    private float[] normals = null;

    /**
     * Creates a new instance of MHDNeutral
     */
    public MHDNeutral(NeutralSheetWindow nsWind) {

        super(nsWind);

        Color3f emissive = new Color3f(0.663f, 0.635f, 0.941f);

        mat.setEmissiveColor(emissive);
    }

    public void setCalendar(ModifiedJulianCalendar mjc) {

        MHD.mjc = mjc;
        //     update();
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
    }

    /*respond to a change in color on the surface window */
    @Override
    public void setColor(Color4f c) {
        color = c;

        Color3f emissive = new Color3f(c.get());
        float[] f = new float[3];
        emissive.get(f);
        for (int i = 0; i < f.length; i++) {
            f[i] = f[i] + f[i] / 2 > 1 ? 1 : f[i] + f[i] / 2;
        }

        emissive.set(f);

        mat.setEmissiveColor(emissive);

        update();
    }

    @Override
    public void setSWP(float newSwp) {
        //  firstTime = true;
        update();

    }

    @Override
    public void setMin(float newMin) {

        xmin = newMin;
        //  firstTime = true;
        update();
    }

    public void update() {
        if (mjc == null) {
            return;
        }
        // if ((firstTime == true || visible == true)) {
        //    if ( visible == true) {
        ((QuadArray) getShape().getGeometry()).updateData(this);
        //     }
    }

    // public void setVisible(boolean visible) {
    //      this.visible = visible;
    //  }
    //  public boolean isVisible() {
    //      return visible;
    //  }
    /**
     * Calculate the ymax intersect with the magnetopause in GSE coordinate
     * system
     *
     * @return the y max intersect with the magnetopause in the GSE coordinate
     * system (Re units);
     */
    public double calculateYLimits(double x) {

        float p0 = 2.04f;
        float s1 = 0.14f;
        float t1 = 65.0f;
        float t2 = 76.0028195f;
        float rho = (float) Math.pow((p0 / (float) swp), (1. / 6.6));
        float ymax = (float) (Math.sqrt(s1 * (Math.pow(t2 * rho, 2) - Math.pow(x + t1 * rho, 2))));
        return ymax;
    }

    @Override
    public void updateData(Geometry geometry) {

        double angle = new Mu(mjc.getMjd(), mjc.getHours()).getMu();
        double x, y, z, aberx, abery;
        float rh = 8f;
        float delx = 4f;
        float g = 10f;
        float ly = 10f;
        Point3d[] points = new Point3d[SIZEX * SIZEY];
        //       double[] coords;
        //      float[] colors;
        //      float[] normals;

        //    if (firstTime) {
        //        T3 transformGSEtoGSM = new T3(mjc.getMjd(), mjc.getHours());
        //        for (int i = 0; i < SIZEX; i++) {
        //           Xs[i] = xmax + (xmin / SIZEX) * i;
        //         Point3d y1 = new Point3d(0d, calculateYLimits(Xs[i]), 0d);
        //          transformGSEtoGSM.transform(y1);
        //         Ys[i] = y1.y;
        //     }
        coords = coords == null ? new double[3 * 4 * (SIZEX - 1) * (SIZEY - 1)]
                : ((QuadArray) geometry).getCoordRefDouble();
        colors = colors == null ? new float[4 * 4 * (SIZEX - 1) * (SIZEY - 1)]
                : ((QuadArray) geometry).getColorRefFloat();
        normals = normals == null ? new float[3 * 4 * (SIZEX - 1) * (SIZEY - 1)]
                : ((QuadArray) geometry).getNormalRefFloat();

        //      colors = new float[4 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        //       normals = new float[3 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        //      coords = new double[3 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        //      colors = new float[4 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        //       normals = new float[3 * 4 * (SIZEX - 1) * (SIZEY - 1)];
        //   } else {
        //   coords = ((QuadArray) geometry).getCoordRefDouble();
        //    colors = ((QuadArray) geometry).getColorRefFloat();
        //    normals = ((QuadArray) geometry).getNormalRefFloat();
        //     }
        double t1 = rh * Math.cos(angle);
        double t4 = Math.pow(delx * Math.cos(angle), 2);
        int ii = 0;

        for (int i = 0; i < SIZEX; i++) {

            x = Xs[i];

            double ymax = Ys[i];
            double ymin = -Ys[i];

            for (int j = 0; j < SIZEY; j++) {

                y = ymin + ((ymax - ymin) / SIZEY) * j;

                aberx = (float) (Math.cos(Math.toRadians(4)) * x + Math.sin(Math.toRadians(4)) * y);
                abery = (float) (-Math.sin(Math.toRadians(4)) * x + Math.cos(Math.toRadians(4)) * y);

                double t2 = Math.pow(aberx - t1, 2);
                double t3 = Math.pow(aberx + t1, 2);
                double t5 = g * Math.sin(angle) * Math.pow(abery, 4) / (Math.pow(abery, 4) + Math.pow(ly, 4));

                z = (float) (0.5 * Math.tan(angle) * (Math.sqrt(t2 + t4) - Math.sqrt(t3 + t4)) - t5);

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

    }

    @Override
    public void doInitialTransform() {

        T3 transformGSEtoGSM = new T3(mjc.getMjd(), mjc.getHours());

        for (int i = 0; i < SIZEX; i++) {

            Xs[i] = xmax + (xmin / SIZEX) * i;

            Point3d y1 = new Point3d(0d, calculateYLimits(Xs[i]), 0d);
            transformGSEtoGSM.transform(y1);

            Ys[i] = y1.y;

            ((QuadArray) this.getShape().getGeometry()).setCoordRefDouble(coords);
            ((QuadArray) this.getShape().getGeometry()).setColorRefFloat(colors);
            ((QuadArray) this.getShape().getGeometry()).setNormalRefFloat(normals);
            update();
        }

    }

    public static double distanceToNeutralSheet(float gsm[]) {

        if (gsm[0] > 0) {
            return Double.NaN;
        }

        // -------------------------------------------------------------
        //  taking care of the aberration (4 degrees)
        //  -------------------------------------------------------------
        double[] gseAlt = {0.9976 * gsm[0] - 0.0697 * gsm[1],
            0.0697 * gsm[0] + 0.9976 * gsm[1],
            gsm[2]
        };

        double angle = new Mu(mjc.getMjd(), mjc.getHours()).getMu();
        float rh = 8f;
        float delx = 4f;
        float g = 10f;
        float ly = 10f;
        double t1 = rh * Math.cos(angle);
        double t4 = Math.pow(delx * Math.cos(angle), 2);
        double t2 = Math.pow(gseAlt[0] - t1, 2);
        double t3 = Math.pow(gseAlt[0] + t1, 2);
        double t5 = g * Math.sin(angle) * Math.pow(gseAlt[1], 4) / (Math.pow(gseAlt[1], 4) + Math.pow(ly, 4));
        double z = (float) (0.5 * Math.tan(angle) * (Math.sqrt(t2 + t4) - Math.sqrt(t3 + t4)) - t5);

        return gsm[2] - z;
    }

}
