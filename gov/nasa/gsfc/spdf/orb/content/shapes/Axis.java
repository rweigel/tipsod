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
 * $Id: Axis.java,v 1.25 2017/03/06 20:05:00 rchimiak Exp $
 *
 * Created on April 18, 2002, 11:27 AM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * The Axis class defines the X, Y, and Z axes. The dimension of each axis is
 * based on the overall dimension of the scene.
 *
 * @author rchimiak
 * @version $Revision: 1.25 $
 */
public class Axis extends TransformGroup {

    /**
     * Represents the point of origin (intersection of the 3 axes)
     */
    public final static Point3d origin = new Point3d(0, 0, 0);
    public final static int xAxis = 1;
    public final static int yAxis = 2;
    public final static int zAxis = 3;
    public final static int[] axisPlacement = {zAxis, xAxis, yAxis};
    private Shape3D shape = new Shape3D();
    private final Appearance appearance = new Appearance();
    private final Transform3D transform = new Transform3D();

    /**
     * Creates a set of axes on which to plot the selected spacecraft orbits.
     *
     * @param infoPane
     */
    public Axis(InfoPanel infoPane) {

        super();
        Color3f[] clrs = new Color3f[3];

        clrs[0] = new Color3f(0.85f, 0.18f, 0.18f); //red
        clrs[1] = new Color3f(0.18f, 0.85f, 0.18f); //green
        clrs[2] = new Color3f(0.18f, 0.18f, 0.85f); //blue

        shape.setGeometry(createGeometry(clrs));
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

        setAppearance(infoPane.getAxisLineWidth());
        addChild(shape);

    }

    public Axis(InfoPanel infoPane, Color3f[] clrs) {

        super();

        shape.setGeometry(createGeometry(clrs));
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

        setAppearance(infoPane.getAxisLineWidth());
        addChild(shape);

    }

    public void setScale(double scale) {

        transform.setScale(scale);
        setTransform(transform);
    }

    public void clear() {

        shape.removeAllGeometries();
        shape = null;
    }

    public Transform3D getTransform() {
        return transform;
    }

    /**
     * Creates the lines (geometry and color) that represent the axes
     */
    protected Geometry createGeometry(Color3f[] clrs) {

        LineArray axisLines = new LineArray(6, LineArray.COORDINATES | LineArray.COLOR_3);
        Point3d[] pts = new Point3d[6];
        //  Color3f[] clrs = new Color3f[3];

        // clrs[0] = new Color3f(0.85f,0.18f,0.18f); //red
        // clrs[1] = new Color3f(0.18f,0.85f,0.18f); //green
        // clrs[2] = new Color3f(0.18f,0.18f,0.85f); //blue
        pts[0] = new Point3d(-1, 0, 0);
        pts[1] = new Point3d(1, 0, 0);
        pts[2] = new Point3d(0, -1, 0);
        pts[3] = new Point3d(0, 1, 0);
        pts[4] = new Point3d(0, 0, -1);
        pts[5] = new Point3d(0, 0, 1);

        axisLines.setCoordinates(0, pts);

        axisLines.setCapability(Geometry.ALLOW_INTERSECT);

        axisLines.setColor(0, clrs[0]);
        axisLines.setColor(1, clrs[0]);
        axisLines.setColor(2, clrs[1]);
        axisLines.setColor(3, clrs[1]);
        axisLines.setColor(4, clrs[2]);
        axisLines.setColor(5, clrs[2]);
        setCapability(LineArray.ALLOW_VERTEX_ATTR_READ);

        return axisLines;
    }

    public void setAppearance(float lineWidth) {

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(lineWidth);
        appearance.setLineAttributes(lineAttributes);
        shape.setAppearance(appearance);
    }
}
