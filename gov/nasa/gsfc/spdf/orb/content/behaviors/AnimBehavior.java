
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
 * $Id: AnimBehavior.java,v 1.36 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on March 21, 2002, 1:20 PM
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.Slider;

import java.util.List;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * The AnimBehavior class is used to implement the animation of an orbit path
 * for a satellite
 *
 * @author rchimiak
 * @version $Revision: 1.36 $
 */
public abstract class AnimBehavior {

    private AnimInterpolator positionPath = null;

    private static final int MILLI_SECONDS_PER_MINUTE = 60000;

    /**
     * Creates a new behavior to implement the animation of a satellite's orbit
     * path.
     *
     * @param target transform node acted upon by the interpolator linking the
     * shape and the path.
     * @param satLocation a SatelliteLocation instance with the requested
     * information
     * @param j selected satellites count position.
     * @param alpha node component that convert a time value into an value in
     * the range 0 to 1.
     * @param slider widget used to control the animation
     */
    public AnimBehavior(final TransformGroup target,
            final Object satLocation, final int j,
            final Alpha alpha, final Slider slider) {

        super();

        if (satLocation != null) {

            double[][] coords = getCoordinateData(satLocation);

            if (coords != null) {

                int numCoords = coords.length;
                float[] knots;
                Point3f[] positions;

                if (coords.length > 1) {

                    knots = new float[numCoords];

                    positions = new Point3f[numCoords];

                    for (int i = 0; i < numCoords; i++) {
                        knots[i] = (float) i / (float) (numCoords - 1);

                        positions[i] = new Point3f((float) coords[i][0],
                                (float) coords[i][1],
                                (float) coords[i][2]);
                    }

                    knots[numCoords - 1] = 1.0f;

                    long endTimeInMins
                            = getTime(satLocation).
                            get(getTime(satLocation).size() - 1).
                            toGregorianCalendar().getTimeInMillis() / MILLI_SECONDS_PER_MINUTE;

                    long beginningTimeInMins
                            = getTime(satLocation).get(0).
                            toGregorianCalendar().getTimeInMillis() / MILLI_SECONDS_PER_MINUTE;

                    long totalTimeInMins = endTimeInMins - beginningTimeInMins;

                    totalTimeInMins = totalTimeInMins > Integer.MAX_VALUE ? Integer.MAX_VALUE : totalTimeInMins;

                    slider.setMaximum(positions.length > totalTimeInMins
                            ? (positions.length - 1)
                            : (int) totalTimeInMins);

                    Transform3D transform = new Transform3D();

                    positionPath = new AnimInterpolator(alpha, target,
                            transform, knots,
                            positions,
                            j);

                    positionPath.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
                } else {

                    positions = new Point3f[2];
                    positions[0] = new Point3f((float) coords[0][0],
                            (float) coords[0][1],
                            (float) coords[0][2]);
                    positions[1] = positions[0];

                    long endTimeInMins = 0;
                    if (OrbitViewer.getControlPane() != null) {
                        endTimeInMins
                                = OrbitViewer.getControlPane().getEndDate().getTime() / MILLI_SECONDS_PER_MINUTE;
                    }
                    long beginningTimeInMins
                            = getTime(satLocation).get(0).
                            toGregorianCalendar().getTimeInMillis() / MILLI_SECONDS_PER_MINUTE;
                    long totalTimeInMins = endTimeInMins - beginningTimeInMins;

                    totalTimeInMins = totalTimeInMins > Integer.MAX_VALUE ? Integer.MAX_VALUE : totalTimeInMins;

                    slider.setMaximum(positions.length > totalTimeInMins
                            ? (positions.length - 1)
                            : (int) totalTimeInMins);

                    positionPath = new AnimInterpolator(alpha, target,
                            new Transform3D(), new float[]{0, 1},
                            positions,
                            j);
                }
            }
        }
    }

    protected abstract double[][] getCoordinateData(Object satLocation);

    protected abstract List<XMLGregorianCalendar> getTime(Object satLocation);

    /**
     * Called when the satellites selection changes.
     */
    public void clear() {

        positionPath = null;
    }

    /**
     * Gets the position interpolator that defines the translational
     * modification performed on the transform group controlling the sphere.
     *
     * @return The positionPathInterpolator behavior node
     */
    public final AnimInterpolator getPositionPath() {
        return positionPath;
    }

    /**
     * Sets the position interpolator in order to linearly interpolate among a
     * series of predefined knot/position pairs
     *
     * @param posInterp The positionPathInterpolator behavior node
     */
    public void setPositonPath(final AnimInterpolator posInterp) {
        positionPath = posInterp;
    }
}
