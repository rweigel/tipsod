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
 * $Id: GeoGrid.java,v 1.15 2015/10/30 14:18:50 rchimiak Exp $
 * Created on May 24, 2007, 3:58 PM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import java.awt.Color;
import java.awt.Font;
import javax.vecmath.Point3f;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Node;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 *
 * @author rachimiak
 */
public class GeoGrid extends EarthShapeExtension {

    private int type;
    private final int latAngle = 10;
    private final int longitAngle = 10;
    private final Point3f[] longitArray;
    private final Point3f[] latArray;

    /**
     * Creates a new instance of GeoGrid
     */
    public GeoGrid() {

        super();

        int resolution = 360;
        int numCoords = resolution + 1;

        for (int lat = -90; lat < 100; lat = lat + latAngle) {

            LineStripArray lsa = new LineStripArray(numCoords,
                    LineArray.COORDINATES | LineArray.COLOR_3,
                    new int[]{numCoords});

            for (int longit = 0; longit < 360; longit++) {

                double[] spher = {1.006d, lat, longit};

                Point3d po = new Point3d(Footpoint.sphericalToCartesian(spher));
                lsa.setCoordinate(longit, po);
                lsa.setColor(longit, new Color3f(Color.GRAY.darker()));

            }
            Point3d lastPoint = new Point3d();
            lsa.getCoordinate(0, lastPoint);
            lsa.setCoordinate(360, lastPoint);
            lsa.setCapability(Geometry.ALLOW_INTERSECT);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_READ);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
            lsa.setCapability(GeometryArray.ALLOW_FORMAT_READ);
            lsa.setCapability(GeometryArray.ALLOW_COUNT_READ);
            lsa.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
            lsa.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
            lsa.setCapability(GeometryArray.ALLOW_COORDINATE_READ);

            Shape3D shape = new Shape3D(lsa);
            Appearance appearance = new Appearance();
            LineAttributes lineAttributes = new LineAttributes();
            appearance.setLineAttributes(lineAttributes);
            shape.setAppearance(appearance);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

            appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
            appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
            appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

            getBg().addChild(shape);
        }

        for (int longit = 0; longit < 360; longit = longit + longitAngle) {

            LineStripArray lsa = new LineStripArray(numCoords,
                    LineArray.COORDINATES | LineArray.COLOR_3,
                    new int[]{numCoords});

            int value = longit % 30 == 0 ? 90 : 60;

            for (int lat = -value; lat < value + 1; lat++) {

                double[] spher = {1.006d, lat, longit};

                Point3d po = new Point3d(Footpoint.sphericalToCartesian(spher));

                lsa.setCoordinate(lat + 90, po);

                Color color = longit % 30 == 0 ? Color.gray : Color.darkGray;

                lsa.setColor(lat + 90, new Color3f(color));
            }
            lsa.setCapability(Geometry.ALLOW_INTERSECT);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_READ);
            lsa.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
            lsa.setCapability(GeometryArray.ALLOW_FORMAT_READ);
            lsa.setCapability(GeometryArray.ALLOW_COUNT_READ);
            lsa.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
            lsa.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
            lsa.setCapability(GeometryArray.ALLOW_COORDINATE_READ);

            Shape3D shape = new Shape3D(lsa);
            Appearance appearance = new Appearance();
            LineAttributes lineAttributes = new LineAttributes();
            appearance.setLineAttributes(lineAttributes);
            shape.setAppearance(appearance);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

            appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
            appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
            appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

            getBg().addChild(shape);
        }

        longitArray = new Point3f[360 / longitAngle];

        for (int longit = 0; longit < 360; longit = longit + longitAngle) {

            double[] spher = {1.006d, 0d, longit};

            Point3d po = new Point3d(Footpoint.sphericalToCartesian(spher));

            longitArray[longit / longitAngle] = new Point3f(po);
        }

        latArray = new Point3f[190 / latAngle];
        for (int lat = -90; lat < 100; lat = lat + latAngle) {

            double[] spher = {1.006d, lat, 0d};

            Point3d po = new Point3d(Footpoint.sphericalToCartesian(spher));
            latArray[(lat + 90) / latAngle] = new Point3f(po);
        }

        setLabels();
    }

    public void setLabels() {

        getEarthTransform().setScale(getAdjust());
        getTransformG().setTransform(getEarthTransform());

        java.util.Enumeration enumeration = getTransformG().getAllChildren();

        while (enumeration != null && enumeration.hasMoreElements()) {

            Object object = enumeration.nextElement();

            if (object instanceof OrientedShape3D) {

                ((OrientedShape3D) object).removeAllGeometries();
                getTransformG().removeChild((Node) object);
            }
        }
        addLongitLabels();
        addLatLabels();
    }

    public void addLongitLabels() {

        for (int longit = 0; longit < 360; longit = longit + longitAngle) {

            Point3f pt = new Point3f(longitArray[longit / longitAngle].x / getAdjust(),
                    longitArray[longit / longitAngle].y / getAdjust(),
                    longitArray[longit / longitAngle].z / getAdjust());

            Text3D textGeom = new Text3D(getFont3d(), String.valueOf(longit), pt, Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
            OrientedShape3D textShape = new OrientedShape3D(textGeom, new Appearance(), OrientedShape3D.ROTATE_ABOUT_POINT, pt);
            ColoringAttributes c = new ColoringAttributes();
            c.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
            c.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

            textShape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
            textShape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
            c.setColor(255f / 255f, 255f / 255f, 242f / 255f);

            textShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            textShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

            textShape.getAppearance().setColoringAttributes(c);
            textShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            textShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
            textShape.setCapability(OrientedShape3D.ALLOW_POINT_READ);
            textShape.setCapability(OrientedShape3D.ALLOW_POINT_WRITE);

            getTransformG().addChild(textShape);
        }
    }

    public void addLatLabels() {

        for (int lat = -90; lat < 100; lat = lat + latAngle) {

            Point3f pt = new Point3f(latArray[(lat + 90) / latAngle].x / getAdjust(),
                    latArray[(lat + 90) / latAngle].y / getAdjust(),
                    latArray[(lat + 90) / latAngle].z / getAdjust());

            Font3D latFont3d = new Font3D(new Font(null, Font.PLAIN, 1), new FontExtrusion());
            Text3D textGeom = new Text3D(latFont3d, String.valueOf(lat), pt, Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
            OrientedShape3D textShape = new OrientedShape3D(textGeom, new Appearance(), OrientedShape3D.ROTATE_ABOUT_POINT, pt);
            ColoringAttributes c = new ColoringAttributes();
            c.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
            c.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
            textShape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
            textShape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
            c.setColor(255f / 255f, 255f / 255f, 242f / 255f);

            textShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            textShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

            textShape.getAppearance().setColoringAttributes(c);

            textShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            textShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
            textShape.setCapability(OrientedShape3D.ALLOW_POINT_READ);
            textShape.setCapability(OrientedShape3D.ALLOW_POINT_WRITE);
            getTransformG().addChild(textShape);
        }
    }

    @Override
    public void resize() {

        getEarthTransform().setScale(getAdjust());
        getTransformG().setTransform(getEarthTransform());
        java.util.Enumeration enumeration = getTransformG().getAllChildren();
        int longit = 0;
        int lat = -90;
        while (enumeration != null
                && enumeration.hasMoreElements()) {

            while (longit < 360) {

                Object object = enumeration.nextElement();

                if (object instanceof OrientedShape3D) {

                    ((OrientedShape3D) object).removeAllGeometries();

                    Point3f pt = new Point3f(longitArray[longit / longitAngle].x / getAdjust(),
                            longitArray[longit / longitAngle].y / getAdjust(),
                            longitArray[longit / longitAngle].z / getAdjust());

                    ((OrientedShape3D) object).addGeometry(new Text3D(getFont3d(), String.valueOf(longit), pt, Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT));
                    ((OrientedShape3D) object).setRotationPoint(pt);
                    longit = longit + longitAngle;
                }
            }
            while (lat < 100) {

                Object object = enumeration.nextElement();

                if (object instanceof OrientedShape3D) {

                    ((OrientedShape3D) object).removeAllGeometries();

                    Point3f pt = new Point3f(latArray[(lat + 90) / latAngle].x / getAdjust(),
                            latArray[(lat + 90) / latAngle].y / getAdjust(),
                            latArray[(lat + 90) / latAngle].z / getAdjust());
                    ((OrientedShape3D) object).addGeometry(new Text3D(getFont3d(), String.valueOf(lat), pt, Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT));
                    ((OrientedShape3D) object).setRotationPoint(pt);
                    lat = lat + latAngle;
                }
            }
        }
    }

    public void setWidth(final float lineWidth) {

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(lineWidth);

        java.util.Enumeration enumeration = getBg().getAllChildren();
        while (enumeration != null
                && enumeration.hasMoreElements()) {

            Object object = enumeration.nextElement();

            if (object instanceof Shape3D) {
                ((Shape3D) object).getAppearance().setLineAttributes(lineAttributes);
            }

        }
    }

    @Override
    public void color(final Color3f color) {

        java.util.Enumeration enumeration = getTransformG().getAllChildren();
        int longit = 0;
        int lat = -90;
        while (enumeration != null
                && enumeration.hasMoreElements()) {

            while (longit < 360) {

                Object object = enumeration.nextElement();

                if (object instanceof OrientedShape3D) {

                    ((OrientedShape3D) object).getAppearance().getColoringAttributes().setColor(
                            color);
                    longit = longit + longitAngle;
                }
            }
            while (lat < 100) {

                Object object = enumeration.nextElement();

                if (object instanceof OrientedShape3D) {

                    ((OrientedShape3D) object).getAppearance().getColoringAttributes().setColor(
                            color);
                    lat = lat + latAngle;
                }
            }
        }
    }

    public void rotateGeogrid(final double mjd, final double hours, final CoordinateSystem displayCoord) {

        rotate(mjd, hours, displayCoord);
    }
}
