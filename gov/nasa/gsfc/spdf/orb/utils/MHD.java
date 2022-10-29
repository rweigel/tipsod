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
 * $Id: MHD.java,v 1.20 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on December 17, 2002, 1:22 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.SurfaceWindow;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

/**
 * Superclass for MHDSurf and MHDPause, is never instantiated directly
 *
 * @author rchimiak
 * @version $Revision: 1.20 $
 */
public abstract class MHD extends TransformGroup {

    protected Shape3D shape = new Shape3D();
    protected Material mat = new Material();
    protected Color4f color = null;
    protected static float swp = 2.04f;
    protected float xmin = -45;
    protected static ModifiedJulianCalendar mjc = null;

    /**
     * Constructor for MHD
     *
     * @param surfWind the magnetopause or bow shock window
     */
    public MHD(SurfaceWindow surfWind) {

        this(surfWind.getSWP(), 0.2f, 0.0f, 360.0f,
                new Color4f(surfWind.getColorButton().getBackground()));
        swp = surfWind.getSWP();

    }

    public MHD(float psw, float sina,
            float start, float end, Color4f cl) {

        color = cl;

        Color3f diffuseColor = new Color3f(.9f, .9f, .7f);
        Color3f specularColor = new Color3f(.9f, .9f, .7f);
        mat.setCapability(Material.ALLOW_COMPONENT_READ);
        mat.setCapability(Material.ALLOW_COMPONENT_WRITE);

        mat.setSpecularColor(specularColor);
        mat.setDiffuseColor(diffuseColor);
        mat.setShininess(0.1f);

        mat.setLightingEnable(true);
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);

        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);

        setCapability(TransformGroup.ALLOW_BOUNDS_READ);

        buildModel(psw, sina, start, end);

        addChild(shape);
    }

    public void setMin(float newMin) {

        xmin = newMin;
        buildModel(swp, 0.2f, 0.0f, 360.0f);
    }

    public void setScale(float newScale) {

        Transform3D t = new Transform3D();
        t.setScale(1 / newScale);
        this.setTransform(t);
    }

    public void buildModel(float psw, float sina,
            float start, float end) {
        int i, j;
        int SIZEX = 64;
        int SIZEY = 64;
        int[] vcounts = new int[4 * (SIZEX - 1) * (SIZEY - 1)];
        int[] vcolors = new int[SIZEX * SIZEY];

        for (i = 0; i < SIZEX * SIZEY; i++) {
            vcolors[i] = 0;
        }

        for (j = 0; j < (SIZEY - 1); j++) {
            for (i = 0; i < (SIZEX - 1); i++) {
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 0] = (SIZEX) * j + i + 0;
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 1] = (SIZEX) * j + i + 1;
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 2] = (SIZEX) * (j + 1) + i + 1;
                vcounts[4 * (SIZEX - 1) * j + 4 * i + 3] = (SIZEX) * (j + 1) + i + 0;
            }
        }

        IndexedQuadArray tetra = new IndexedQuadArray(SIZEX * SIZEY,
                IndexedQuadArray.COORDINATES | IndexedQuadArray.COLOR_4,
                4 * (SIZEX - 1) * (SIZEY - 1));

        tetra.setCoordinateIndices(0, vcounts);
        tetra.setColorIndices(0, vcolors);

        tetra.setCapability(Geometry.ALLOW_INTERSECT);
        tetra.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
        tetra.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

        shape.setGeometry(tetra);
        this.setCoordinate(swp, start, end);

    }

    public void setCoordinate(float psw,
            float start, float end) {
    }

    /*respond to a change in color on the surface window */
    public void setColor(Color4f c) {
        color = c;

        Color3f emissive = new Color3f(c.get());

        mat.setEmissiveColor(emissive);

        buildModel(swp, 0.2f, 0.0f, 360.0f);
    }
    /*respond to a change in opacity on the surface window */

    public void setOpacity(float opac) {

        getShape().getAppearance().getTransparencyAttributes().setTransparency(opac);
    }

    /**
     * returns the shape for that geometry
     */
    public Shape3D getShape() {
        return shape;
    }

    abstract void doInitialTransform();

    public void doTranslation(TransformGroup tg) {
    }

    /*respond to a change in swp value on the surface window */
    public void setSWP(float newSwp) {

        swp = newSwp;
        buildModel(swp, 0.2f, 0.0f, 360.0f);

        OrbitViewer.getSatellitePositionWindow().SWPChanged();
    }

    /*Change from surface to wireframe or screen_door
     *@param attribute the surface or wireframe or screen_door PolygonAttributes integer value*/
    public void setPolygonAttributes(int attribute) {

        getShape().getAppearance().setPolygonAttributes(new PolygonAttributes(attribute,
                PolygonAttributes.CULL_NONE, 0, true, 0));
    }

    /*Change from NICEST to SCREEN_DOOR and vice-versa
     *@param attribute the Nicest or Screen_door transparency mode integer value*/
    public void setTransparencyAttributes(int attribute) {

        getShape().getAppearance().getTransparencyAttributes().setTransparencyMode(attribute);
    }
}
