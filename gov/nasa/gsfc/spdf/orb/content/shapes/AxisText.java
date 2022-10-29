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
 * $Id: AxisText.java,v 1.26 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on April 22, 2002, 8:50 AM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

/**
 * The AxisText class is used to write the X Y and Z labels sligtly off from the
 * positive axes.
 *
 * @author rchimiak
 * @version $Revision: 1.26 $
 */
public class AxisText extends BranchGroup {

    private final TransformGroup transformGroup = new TransformGroup();

    public AxisText(double scale, int fontSize) {

        super();

        this.setCapability(BranchGroup.ALLOW_DETACH);

        setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        createGeometries(scale, fontSize);

        this.setCapability(BranchGroup.ALLOW_DETACH);

        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        addChild(transformGroup);
    }

    public void setTransform(Transform3D transform) {

        double s = transform.getScale();

        transform.setScale(s);

        transformGroup.setTransform(transform);
    }

    /**
     * Creates the text and its position on the graph
     */
    protected void createGeometries(double scale, int fontSize) {

        float f = SatBranch.getSymbolSize();
        float adjust = 1f;
        if (f < 1) {
            adjust = 3 * f;
        } else if (f < 2) {
            adjust = 3f / (2f * f);
        }

        Font3D f3d = new Font3D(new Font(null, Font.PLAIN, fontSize), new FontExtrusion());
        Point3f p;

        OrientedShape3D orientSX = new OrientedShape3D();
        orientSX.setCapability(Geometry.ALLOW_INTERSECT);
        p = new Point3f((float) (scale / adjust + .3 * fontSize), 0f, 0f);

        orientSX.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
        orientSX.setRotationPoint(p);
        orientSX.setGeometry(new Text3D(f3d, "x", p));
        orientSX.setAppearance(createAppearance());

        OrientedShape3D orientSY = new OrientedShape3D();
        orientSY.setCapability(Geometry.ALLOW_INTERSECT);
        p = new Point3f(0f, (float) (scale / adjust + .3 * fontSize), 0f);

        orientSY.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
        orientSY.setRotationPoint(p);
        orientSY.setGeometry(new Text3D(f3d, "y", p));
        orientSY.setAppearance(createAppearance());

        OrientedShape3D orientSZ = new OrientedShape3D();
        orientSZ.setCapability(Geometry.ALLOW_INTERSECT);
        p = new Point3f(0f, 0f, (float) ((scale / adjust) + .3 * fontSize));

        orientSZ.setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
        orientSZ.setRotationPoint(p);
        orientSZ.setGeometry(new Text3D(f3d, "z", p));
        orientSZ.setAppearance(createAppearance());

        BranchGroup textBranch = new BranchGroup();
        textBranch.setCapability(BranchGroup.ALLOW_DETACH);

        TransformGroup textGroup = new TransformGroup();
        Transform3D textTransform = new Transform3D();
        textTransform.setScale(adjust);
        textBranch.addChild(textGroup);

        textGroup.addChild(orientSX);
        textGroup.addChild(orientSY);
        textGroup.addChild(orientSZ);
        textGroup.setTransform(textTransform);
        transformGroup.addChild(textBranch);
    }

    /**
     * Defines the color of the text
     */
    protected Appearance createAppearance() {

        Appearance app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

        ColoringAttributes ca = new ColoringAttributes();

        ca.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

        ca.setColor(1f, 1f, 1f);
        app.setColoringAttributes(ca);
        return app;
    }
}
