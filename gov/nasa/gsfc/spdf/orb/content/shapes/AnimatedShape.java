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
 * $Id: AnimatedShape.java,v 1.30 2017/03/06 20:05:00 rchimiak Exp $
 *
 * Created on March 20, 2002, 2:50 PM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphShape;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * The AnimatedShape class is used to build the spacecraft shape as specified in
 * the selection window. The possible shapes are : Sphere, Cube, Cone or
 * cylinder
 *
 * @author rchimiak
 * @version $Revision: 1.30 $
 */
public class AnimatedShape extends OrientedShape3D {

    private float radius;
    private Appearance app = null;
    private String shape;
    private String name;

    /**
     * Constructs and initializes a Shape3D node with the specified geometry and
     * appearance components.
     *
     * @param shape the shape requested (Sphere...)
     * @param color the color associated with the shape
     * @param name
     */
    public AnimatedShape(String shape, Color3f color, String name) {

        super();
        // setConstantScaleEnable(true);

        setName(name);

        setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
        setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);
        this.shape = shape;

        // create the appearance
        app = createAppearance();

        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        app.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);

        ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);

        ca.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

        app.setColoringAttributes(ca);
        RenderingAttributes ra = new RenderingAttributes();
        ra.setCapability(RenderingAttributes.ALLOW_VISIBLE_READ);
        ra.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);

        app.setRenderingAttributes(ra);

        setAppearance(app);
        setGeometry(shape);
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }

    public Appearance createAppearance() {

        return new Appearance();
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setGeometry(String shape) {

        removeAllGeometries();
       
        float index = (float)Math.log1p((double)OrbitViewer.getInfoPane().getSatWidth());

        float shapeSize = SatBranch.getSymbolSize()
                * OrbitViewer.getInfoPane().getSatWidth()
              //  * 0.8f;
               *index;
        
       
        //float footpointSize = shapeSize * 0.8f;
float footpointSize = shapeSize * index;
        this.radius = name.equalsIgnoreCase("footpoint")
                ? footpointSize
                : shapeSize;

        // create the geometry
        if (shape.equalsIgnoreCase("Sphere")) {

            Sphere sphere;
            // this.radius = name.equalsIgnoreCase("footpoint")? 
            // 0.12f*sizePercent :
            //  0.15f*sizePercent;
            //         footpointSize:shapeSize;

            sphere = name.equalsIgnoreCase("moon")
                    ? new Sphere(radius, Primitive.GEOMETRY_NOT_SHARED, 200, app)
                    : new Sphere(radius, Primitive.GEOMETRY_NOT_SHARED, app);

            Geometry sphereGeo = sphere.getShape().getGeometry();
            sphereGeo.setCapability(Geometry.ALLOW_INTERSECT);

            sphereGeo.setCapability(GeometryArray.ALLOW_FORMAT_READ);
            sphereGeo.setCapability(GeometryArray.ALLOW_COUNT_READ);
            sphereGeo.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
            sphereGeo.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
            sphereGeo.setCapability(GeometryArray.ALLOW_COORDINATE_READ);

            if (!System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                Sphere.clearGeometryCache();
            }

            addGeometry(sphereGeo);
        } else if (shape.equalsIgnoreCase("Cube")) {

            //   this.radius = //0.13f* sizePercent;
            //     shapeSize;  
            Box box = new Box(radius / 1.2f, radius / 1.2f, radius / 1.2f, Primitive.GEOMETRY_NOT_SHARED, app);
            if (!System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                Box.clearGeometryCache();
            }

            for (int i = 0; i < 6; i++) {

                (box.getShape(i).getGeometry()).setCapability(Geometry.ALLOW_INTERSECT);

                (box.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_FORMAT_READ);
                (box.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COUNT_READ);
                (box.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COUNT_WRITE);
                (box.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
                (box.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COORDINATE_READ);

                insertGeometry(box.getShape(i).getGeometry(), i);
            }
        } else if (shape.equalsIgnoreCase("Cone")) {

            //   this.radius = //0.15f* sizePercent;
            //   shapeSize;
            Cone cone = new Cone(radius / 1.3f, radius * 2, Primitive.GEOMETRY_NOT_SHARED, app);

            if (!System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                Cone.clearGeometryCache();
            }

            for (int i = 0; i < 2; i++) {

                (cone.getShape(i).getGeometry()).setCapability(Geometry.ALLOW_INTERSECT);
                (cone.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_FORMAT_READ);
                (cone.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COUNT_READ);
                (cone.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COUNT_WRITE);
                (cone.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
                (cone.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COORDINATE_READ);

                insertGeometry(cone.getShape(i).getGeometry(), i);
            }
        } else if (shape.equalsIgnoreCase("Cylinder")) {

            //  this.radius = //0.15f* sizePercent;
            //       shapeSize;
            Cylinder cylinder = new Cylinder(radius / 1.5f, radius * 1.8f, Primitive.GEOMETRY_NOT_SHARED, app);

            if (!System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                Cylinder.clearGeometryCache();
            }

            for (int i = 0; i < 2; i++) {

                (cylinder.getShape(i).getGeometry()).setCapability(Geometry.ALLOW_INTERSECT);
                (cylinder.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_FORMAT_READ);
                (cylinder.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COUNT_READ);
                (cylinder.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COUNT_WRITE);
                (cylinder.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
                (cylinder.getShape(i).getGeometry()).setCapability(GeometryArray.ALLOW_COORDINATE_READ);

                insertGeometry(cylinder.getShape(i).getGeometry(), i);
            }
        } else if (shape.equalsIgnoreCase("Diamond")) {

            //    this.radius =// 0.13f* sizePercent;
            //     shapeSize;
            Point3f s0 = new Point3f(0, 2 * radius / 1.2f, 0);
            Point3f s1 = new Point3f(0, 0, 1.4f * radius / 1.2f);
            Point3f s2 = new Point3f(1.4f * radius / 1.2f, 0, -0.6f * radius / 1.2f);
            Point3f s3 = new Point3f(-1.4f * radius / 1.2f, 0, -0.6f * radius / 1.2f);
            Point3f s4 = new Point3f(0, -2 * radius / 1.2f, 0);

            GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.TRIANGLE_FAN_ARRAY);
            geometryInfo.setCoordinates(new Point3f[]{s0, s1, s2, s3, s1,
                s2, s1, s3,
                s4, s1, s2, s3, s1});
            geometryInfo.setStripCounts(new int[]{5, 3, 5});

            Shape3D diamond = new Shape3D();
            diamond.setGeometry(geometryInfo.getGeometryArray());

            java.util.Enumeration e = diamond.getAllGeometries();

            while (e != null && e.hasMoreElements()) {

                Geometry g = (Geometry) e.nextElement();
                g.setCapability(Geometry.ALLOW_INTERSECT);

                g.setCapability(GeometryArray.ALLOW_FORMAT_READ);
                g.setCapability(GeometryArray.ALLOW_COUNT_READ);
                g.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
            }
            insertGeometry(diamond.getGeometry(), 0);
        }
    }

    public void setColor(Color3f c3f) {

        this.getAppearance().getColoringAttributes().setColor(c3f);
    }

    public void setShape(SatelliteGraphShape shape) {

        setGeometry(shape.toString());
        setShape(shape.toString());
    }
}
