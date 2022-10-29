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
 * Copyright (c) 2003-2006 United States Government as represented by the
 * National Aeronautics and Space Administration. No copyright is claimed
 * in the United States under Title 17, U.S.Code. All Other Rights Reserved.
 *
 * $Id: Grid.java,v 1.15 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on May 29, 2002, 2:04 PM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Appearance;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

//import com.sun.j3d.utils.geometry.*;
//import javax.vecmath.*;
//import javax.media.j3d.*;
/**
 * The Grid class implements a transform group node used to display the semi
 * transparent XY, XZ and YZ planes.
 *
 * @author rchimiak
 * @version $Revision: 1.15 $
 */
public class Grid extends TransformGroup {

    /**
     * Creates a semi-transparent box used to display the XY, XZ, and YZ planes.
     */
    public static final Color3f D_GRID_COLOR = new Color3f(1.0f, 0.0f, 0.0f);
    public static final float D_TRANSFACTOR = 0.65f;

    private final Shape3D gridLines = new Shape3D();
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public Grid(double inputx, double inputy, double inputz) {

        x = inputx;
        y = inputy;
        z = inputz;

        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        gridLines.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        gridLines.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        gridLines.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        Box box = new Box((float) x, (float) y, (float) z, createAppearance());
        addChild(box);
        addChild(gridLines);
    }

    public void makeGridLines(double radius, int scale) {

        if (radius >= 1) {

            Point3d[] pts = new Point3d[(int) radius * 2 * 4];
            LineArray line = new LineArray((int) radius * 2 * 4, LineArray.COORDINATES);

            int j = 0;
            if (x != 0) {

                for (int i = -2 * (int) radius * scale; i < 2 * (int) radius * scale; i = (i + 2 * scale), j++) {
                    pts[j] = new Point3d((i) / (scale * 2 * radius) * x, y, z);
                    j++;
                    pts[j] = new Point3d((i) / (scale * 2 * radius) * x, -y, -z);
                }
            }
            if (y != 0) {

                for (int i = -2 * (int) radius * scale; i < 2 * (int) radius * scale; i = (i + 2 * scale), j++) {
                    pts[j] = new Point3d(x, (i) / (scale * 2 * radius) * y, z);
                    j++;
                    pts[j] = new Point3d(-x, (i) / (scale * 2 * radius) * y, -z);
                }
            }

            if (z != 0) {
                for (int i = -2 * (int) radius * scale; i < 2 * (int) radius * scale; i = (i + 2 * scale), j++) {
                    pts[j] = new Point3d(x, y, (i) / (scale * 2 * radius) * z);
                    j++;
                    pts[j] = new Point3d(-x, -y, (i) / (scale * 2 * radius) * z);
                }
            }

            line.setCoordinates(0, pts);

            Appearance app = new Appearance();
            TransparencyAttributes tatt = new TransparencyAttributes(
                    TransparencyAttributes.NICEST, D_TRANSFACTOR);
            app.setTransparencyAttributes(tatt);

            gridLines.setGeometry(line);
            gridLines.setAppearance(app);
        }
    }

    /**
     * returns the box appearance.
     */
    protected Appearance createAppearance() {

        Appearance app = new Appearance();
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.BLENDED);
        ta.setTransparency(0.85f);
        app.setTransparencyAttributes(ta);
        return app;
    }
}
