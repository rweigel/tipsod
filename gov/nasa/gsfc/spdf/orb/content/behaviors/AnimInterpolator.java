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
 * $Id: AnimInterpolator.java,v 1.41 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on May 7, 2002, 9:46 AM
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import java.util.Enumeration;

import javax.media.j3d.Alpha;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.ZoomControl;
import gov.nasa.gsfc.spdf.orb.utils.Util;

/**
 * The AnimInterpolator class defines a behavior that modifies the position of a
 * spacecraft on the orbit by interpolating among a series of predefined
 * knot/position pairs.
 *
 * @author rchimiak
 * @version $Revision: 1.41 $
 */
public class AnimInterpolator extends BaseInterpolator {

    private ZoomControl zoom = null;
    private int order = 0;

    /**
     * Construct a new PositionPathInterpolator that varies the transform of the
     * target TransformGroup.
     *
     * @param alpha the alpha object for this interpolator
     * @param target the TransformGroup node affected by this translator
     * @param axisOfTranslation the transform that defines the local coordinate
     * system in which this interpolator operates
     * @param knots an array of knot values that specify interpolation points
     * @param positions points determining the plot of the orbit
     * @param j spacecraft position in the spacecraft list
     */
    public AnimInterpolator(final Alpha alpha,
            final TransformGroup target,
            final Transform3D axisOfTranslation,
            final float[] knots, final Point3f[] positions,
            final int j) {

        super(alpha, target, axisOfTranslation, knots, positions);
        this.order = j;

    }

    public void processOrbitManually() {

        super.updatePosition();

        Util.invokeLater2(new Runnable() {

            @Override
            public void run() {

                OrbitViewer.getSatellitePositionWindow().updatePosition(getPosition(), order);

            }
        });

    }

    @Override
    public void processStimulus(final Enumeration criteria) {

        super.processStimulus(criteria);

        Util.invokeLater2(new Runnable() {

            @Override
            public void run() {

                OrbitViewer.getSatellitePositionWindow().updatePosition(getPosition(), order);

                if (zoom != null) {
                    zoom.setSpacecraftZoom(getPosition().x, getPosition().y, getPosition().z);
                }
            }
        });

    }

    /**
     * Manually connects the time with the slider offset and the spacecraft
     * position.
     *
     * @param percentCompleted how much of the overall animation is completed
     */
    @Override
    public void processManual(final float percentCompleted) {

        super.processManual(percentCompleted);
        if (order == -1) {

            return;
        }

        Util.invokeLater2(new Runnable() {

            @Override
            public void run() {

                OrbitViewer.getSatellitePositionWindow().updatePosition(getPosition(), order);

                if (zoom != null) {
                    zoom.setSpacecraftZoom(getPosition().x, getPosition().y, getPosition().z);
                }
            }
        });
    }

    /**
     * if the zoom is on, the center of rotation is not the earth but the
     * position of the selected zoomed satellite.
     *
     * @param zoom the zoom window
     */
    public void setZoom(final ZoomControl zoom) {

        this.zoom = zoom;

        if (zoom != null) {
            zoom.setRotationCenter(getPosition());
        }
    }

    /**
     * Commands the automatic control of the spacecraft position based on the
     * slider position. Updates time adequately.
     */
    public void play() {

        setEnable(true);
    }

    /**
     * Commands the temporary stop of the animation. Responds to the tool bar
     * pause action.
     */
    public void pause() {

        setEnable(false);
    }

    /**
     * Brings the animation to an end. reset the spacecraft to original
     * position.
     */
    public void stop() {

        setEnable(false);
    }
}
