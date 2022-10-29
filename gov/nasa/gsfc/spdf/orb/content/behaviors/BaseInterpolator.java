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
 * $Id: BaseInterpolator.java,v 1.8 2017/03/06 20:05:00 rchimiak Exp $
 * Created on September 27, 2007, 4:19 PM
 *
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import java.util.Enumeration;

import javax.media.j3d.PositionPathInterpolator;
import javax.media.j3d.Alpha;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author rachimiak
 */
public class BaseInterpolator extends PositionPathInterpolator {

    private int knotsLength = 0;
    private final Point3f pointPosition = new Point3f();
    private float currentInterValue = 0f;

    /**
     * Creates a new instance of interpolator from the AnimBehavior object.
     * Interpolator for the satellites positions during animation.
     *
     * @param alpha the alpha node component object the provides method for
     * converting time into a range of 0-1
     * @param target the transform group node affected by this translator
     * @param axisOfTranslation default axis of translation
     * @param knots array of values that translate each position point received
     * into an interpolation value from 0 to 1.
     * @param positions array of satellite positions as received from the web
     * services or calculated in the case of SSE.
     */
    public BaseInterpolator(final Alpha alpha,
            final TransformGroup target,
            final Transform3D axisOfTranslation,
            final float[] knots, final Point3f[] positions) {

        super(alpha, target, axisOfTranslation, knots, positions);

        this.knotsLength = knots.length;

        Point3f firstPoint = new Point3f();

        this.getPosition(0, firstPoint);

        pointPosition.x = firstPoint.x;
        pointPosition.y = firstPoint.y;
        pointPosition.z = firstPoint.z;
    }

    /**
     * Default Interpolator behavior initialization routine. It is called once
     * when the behavior becomes "live" NOTE: Applications should not call this
     * method. It is called by the Java 3D behavior scheduler.
     */
    @Override
    public void initialize() {

        super.initialize();
        setEnable(false);
    }

    /**
     * Computes the position value between the points returned by the web
     * services.
     *
     * @param alphaValue the alpha value being used at that instant of the
     * animation.
     */
    protected void computeInterpolation(final float alphaValue) {

        int i;

        for (i = 0; i < this.knotsLength; i++) {

            if ((i == 0 && alphaValue <= getKnot(i))
                    || (i > 0 && alphaValue >= getKnot(i - 1) && alphaValue <= getKnot(i))) {

                if (i == 0) {
                    currentInterValue = 0f;
                    currentKnotIndex = 0;
                } else {
                    currentInterValue
                            = (alphaValue - getKnot(i - 1)) / (getKnot(i) - getKnot(i - 1));
                    currentKnotIndex = i - 1;
                }
                break;
            }
        }
    }

    private void computePosition() {

        if (currentKnotIndex == 0 && currentInterValue == 0f) {

            Point3f firstPoint = new Point3f();
            this.getPosition(0, firstPoint);
            pointPosition.x = firstPoint.x;
            pointPosition.y = firstPoint.y;
            pointPosition.z = firstPoint.z;
        } else {

            Point3f currentPoint = new Point3f();
            Point3f nextPoint = new Point3f();
            this.getPosition(currentKnotIndex, currentPoint);
            this.getPosition(currentKnotIndex + 1, nextPoint);

            if ((nextPoint.distance(new Point3f(0, 0, 0)) > 0
                    && currentPoint.distance(new Point3f(0, 0, 0)) == 0
                    && currentInterValue < 1)
                    || (nextPoint.distance(new Point3f(0, 0, 0)) == 0
                    && currentPoint.distance(new Point3f(0, 0, 0)) > 0
                    && currentInterValue < 1)) {

                pointPosition.x = 0;
                pointPosition.y = 0;
                pointPosition.z = 0;
            } else {

                pointPosition.x = currentPoint.x
                        + (nextPoint.x - currentPoint.x) * currentInterValue;
                pointPosition.y = currentPoint.y
                        + (nextPoint.y - currentPoint.y) * currentInterValue;
                pointPosition.z = currentPoint.z
                        + (nextPoint.z - currentPoint.z) * currentInterValue;

            }
        }
    }

    /**
     * Called only when displaying foot points
     *
     * @param percentCompleted how much of the animation is completed going from
     * 0 to 1.
     * @return the position index.
     */
    public int getIndex(final float percentCompleted) {

        return (Math.round(((float) (knotsLength - 1)) * percentCompleted));
    }

    /**
     * satellite position at this instant of the animation.
     *
     * @return the point referencing that satellite position.
     */
    public Point3f getPosition() {
        return pointPosition;
    }

    /**
     * Receives and processes the animation interpolator ongoing messages. It is
     * invoked by the behavior scheduler for every interpolation. It
     * synchronizes the time with the slider's position.
     *
     * @param criteria an enumeration of WakeupCriterion that have triggered.
     */
    @Override
    public void processStimulus(final Enumeration criteria) {

        super.processStimulus(criteria);

        computeInterpolation(getAlpha().value());

        computePosition();
    }

    public void updatePosition() {

        computeInterpolation(getAlpha().value());

        computePosition();

    }

    /**
     * Manually connects the time with the slider offset and the spacecraft
     * position.
     *
     * @param percentCompleted how much of the animation is completed.
     */
    public void processManual(final float percentCompleted) {

        computeInterpolation(percentCompleted);
        computePosition();

        Transform3D trans = new Transform3D();
        trans.set(new Vector3f(pointPosition.x, pointPosition.y, pointPosition.z));
        this.getTarget().setTransform(trans);
    }
}
