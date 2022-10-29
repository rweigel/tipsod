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
 * $Id: FootpointAnimInterpolator.java,v 1.14 2015/10/30 14:18:50 rchimiak Exp $
 * Created on June 4, 2007, 4:06 PM
 *
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import java.util.Enumeration;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.shapes.FootpointShape;
import gov.nasa.gsfc.spdf.orb.gui.GeoSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import javax.media.j3d.Alpha;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

/**
 *
 * @author rachimiak
 */
public class FootpointAnimInterpolator extends BaseInterpolator {

    private FootpointShape shape = null;
    private int order = 0;

    /**
     * Creates a new instance of FootpointAnimInterpolator used to define the
     * foot points displacements along the satellites movements.
     *
     * @param alpha the alpha node component. Provide methods to convert time
     * into a range 0 to 1.
     * @param target
     * @param axisOfTranslation
     * @param knots
     * @param positions
     * @param shape
     * @param order
     */
    public FootpointAnimInterpolator(final Alpha alpha,
            final TransformGroup target,
            final Transform3D axisOfTranslation,
            final float[] knots, final Point3f[] positions, final FootpointShape shape, final int order) {

        super(alpha, target, axisOfTranslation, knots, positions);
        this.order = order;

        this.shape = shape;
    }

    @Override
    public void processStimulus(final Enumeration criteria) {

        super.processStimulus(criteria);
        Util.invokeLater2(new Runnable() {

            @Override
            public void run() {

                ((GeoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow()).updateFootpoints(shape.getHemisphere(), getPosition(), order);
                if (getAlpha().getIncreasingAlphaDuration() == -1) {
                    setEnable(false);
                }

                if (!shape.isNaN(currentKnotIndex) && !shape.isChangingHemisphere(currentKnotIndex)) {

                    if (!shape.isVisible()) {

                        shape.setVisible(true);

                    }
                } else if (shape.isVisible()) {
                    shape.setVisible(false);
                }
            }
        });
    }

    /**
     * Manually connects the time with the slider offset and the spacecraft
     * position.
     */
    @Override
    public void processManual(final float percentCompleted) {

        super.processManual(percentCompleted);
        Util.invokeLater2(new Runnable() {

            @Override
            public void run() {

                ((GeoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow()).updateFootpoints(shape.getHemisphere(), getPosition(), order);
            }
        });
    }

    public void processFootpointManually() {

        super.updatePosition();

        Util.invokeLater2(new Runnable() {

            @Override
            public void run() {

                ((GeoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow()).updateFootpoints(shape.getHemisphere(), getPosition(), order);
            }
        });
    }

    /**
     * Removes the reference to the foot point shape that this interpolator is
     * acting upon
     */
    public void clear() {

        shape = null;
    }
}
