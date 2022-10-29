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
 * $Id: EarthShapeExtension.java,v 1.13 2015/10/30 14:18:50 rchimiak Exp $ 
 * Created on July 19, 2007, 4:41 PM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import java.awt.Font;
import gov.nasa.gsfc.spdf.orb.gui.EarthExtensionShapeSizeChangeListener;
import gov.nasa.gsfc.spdf.orb.gui.EarthExtensionShapeColorChangeListener;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import java.util.Vector;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;

/**
 *
 * @author rachimiak
 */
public class EarthShapeExtension implements EarthExtensionShapeSizeChangeListener,
        EarthExtensionShapeColorChangeListener {

    private BranchGroup bg = new BranchGroup();
    private final TransformGroup rotGroup = new TransformGroup();
    private static float adjust = 0.03f;
    private Transform3D earthTransform = new Transform3D();
    private TransformGroup transformG = new TransformGroup();
    private static final Vector< EarthExtensionShapeSizeChangeListener> earthExtensionShapeSizeChangeListeners
            = new Vector< EarthExtensionShapeSizeChangeListener>();
    private static final Vector< EarthExtensionShapeColorChangeListener> earthExtensionShapeColorChangeListeners
            = new Vector< EarthExtensionShapeColorChangeListener>();
    private Font3D font3d = new Font3D(new Font("Comic Sans MS", Font.BOLD, 1), new FontExtrusion());
    private Font3D crossFont = new Font3D(new Font("Comic Sans MS", Font.BOLD, 2), new FontExtrusion());

    /**
     * Creates a new instance of EarthShapeExtension
     */
    public EarthShapeExtension() {

        rotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        transformG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg.addChild(transformG);
        rotGroup.addChild(bg);
        addEarthExtensionShapeSizeChangeListener(this);
        addEarthExtensionShapeColorChangeListener(this);
    }

    public BranchGroup getBg() {
        return bg;
    }

    public void setBg(BranchGroup bg) {
        this.bg = bg;
    }

    public Transform3D getEarthTransform() {
        return earthTransform;
    }

    public void setEarthTransform(Transform3D transform) {
        this.earthTransform = transform;
    }

    public TransformGroup getTransformG() {
        return transformG;
    }

    public void setTransformG(TransformGroup transformG) {
        this.transformG = transformG;
    }

    public Font3D getCrossFont() {
        return crossFont;
    }

    public void setCrossFont(Font3D crossFont) {
        this.crossFont = crossFont;
    }

    public Font3D getFont3d() {
        return font3d;
    }

    public void setFont3d(Font3D font3d) {
        this.font3d = font3d;
    }

    public void addEarthExtensionShapeSizeChangeListener(EarthExtensionShapeSizeChangeListener listener) {

        earthExtensionShapeSizeChangeListeners.add(listener);
    }

    public void addEarthExtensionShapeColorChangeListener(EarthExtensionShapeColorChangeListener listener) {

        earthExtensionShapeColorChangeListeners.add(listener);
    }

    /**
     * Unregisters the given listener.
     *
     * @param listener the listener that is to be removed
     */
    public void removeEarthExtensionShapeSizeChangeListener(EarthExtensionShapeSizeChangeListener listener) {

        earthExtensionShapeSizeChangeListeners.removeElement(listener);
    }

    public static Vector getEarthExtensionShapeSizeChangeListeners() {

        return earthExtensionShapeSizeChangeListeners;
    }

    public void clear() {

        java.util.Enumeration enumeration = transformG.getAllChildren();

        while (enumeration != null && enumeration.hasMoreElements()) {

            Object object = enumeration.nextElement();

            if (object instanceof OrientedShape3D) {

                ((OrientedShape3D) object).removeAllGeometries();
            }
        }
        transformG.removeAllChildren();
    }

    public TransformGroup getTransform() {

        return rotGroup;
    }

    public void rotate(double mjd, double hours, CoordinateSystem displayCoord) {

        rotGroup.setTransform(Util.rotate(displayCoord, mjd, hours, false));

    }

    public static float getAdjust() {

        return adjust;
    }

    public static void setAdjust(float value) {

        adjust = value;
    }

    @Override
    public void resize() {
    }

    @Override
    public void color(Color3f color) {
    }
}
