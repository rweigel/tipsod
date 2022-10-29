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
 * $Id: MajorTickMark.java,v 1.21 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on April 24, 2002, 10:55 AM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * The MajorTickMark class implements the major tick marks on the axes as a
 * geometry of lines with color. Major Tick marks are located every 5 units.
 *
 * @author rchimiak
 * @version $Revision: 1.21 $
 */
public class MajorTickMark extends BranchGroup {

    private final Appearance appearance = new Appearance();
    private Shape3D shape = new Shape3D();
    private final TransformGroup transformGroup = new TransformGroup();

    public MajorTickMark(InfoPanel infoPane) {

        super();
        this.setCapability(BranchGroup.ALLOW_DETACH);

        setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

        setAppearance(infoPane.getAxisLineWidth());
        addChild(transformGroup);
        transformGroup.addChild(shape);
    }

    public MajorTickMark(int count, InfoPanel infoPane, double size) {

        this(infoPane);

        shape.setGeometry(createGeometry(count, size));
    }

    public void clear() {

        shape.removeAllGeometries();
        shape = null;
    }

    public void changeLength(int count, double size) {

        shape.removeAllGeometries();
        shape.addGeometry(createGeometry(count, size));
    }

    protected Geometry createGeometry(int count, double size) {

        int totalMajorT = count * 2 * 3;

        double dcount = (double) count;

        if (totalMajorT != 0) {
            double MajorT = 1d / (dcount);

            LineArray line = new LineArray(2 * totalMajorT, LineArray.COORDINATES);
            Point3d[] pts = new Point3d[2 * totalMajorT];
            int j = 0;

            for (int i = 0; i < count; i++, j++) {
                pts[j] = new Point3d((i + 1) * MajorT, size, 0);
                j++;
                pts[j] = new Point3d((i + 1) * MajorT, -size, 0);
            }
            for (int i = 0; i < count; i++, j++) {
                pts[j] = new Point3d(size, (i + 1) * MajorT, 0);
                j++;
                pts[j] = new Point3d(-size, (i + 1) * MajorT, 0);
            }
            for (int i = 0; i < count; i++, j++) {
                pts[j] = new Point3d(0, size, (i + 1) * MajorT);
                j++;
                pts[j] = new Point3d(0, -size, (i + 1) * MajorT);
            }
            for (int i = 0; i < count; i++, j++) {
                pts[j] = new Point3d(-(i + 1) * MajorT, size, 0);
                j++;
                pts[j] = new Point3d(-(i + 1) * MajorT, -size, 0);
            }
            for (int i = 0; i < count; i++, j++) {
                pts[j] = new Point3d(size, -(i + 1) * MajorT, 0);
                j++;
                pts[j] = new Point3d(-size, -(i + 1) * MajorT, 0);
            }
            for (int i = 0; i < count; i++, j++) {
                pts[j] = new Point3d(0, size, -(i + 1) * MajorT);
                j++;
                pts[j] = new Point3d(0, -size, -(i + 1) * MajorT);

            }
            line.setCoordinates(0, pts);
            line.setCapability(LineArray.ALLOW_COUNT_READ);
            line.setCapability(LineArray.ALLOW_VERTEX_ATTR_READ);
            line.setCapability(LineArray.ALLOW_INTERSECT);
            return line;
        } else {
            return null;
        }
    }

    /**
     * create the appearance by setting the color to white
     */
    protected void setAppearance(float lineWidth) {

        ColoringAttributes ca = new ColoringAttributes();
        ca.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
        ca.setColor(1, 1, 1);
        appearance.setColoringAttributes(ca);

        setWidth(lineWidth);
    }

    public void setTransform(Transform3D transform) {

        transformGroup.setTransform(transform);
    }

    public void setWidth(float lineWidth) {

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(lineWidth);
        appearance.setLineAttributes(lineAttributes);
        shape.setAppearance(appearance);
    }

    public void setColor(Color3f color) {

        shape.getAppearance().getColoringAttributes().setColor(color);
    }
}
