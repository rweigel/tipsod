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
 * $Id: FootpointsGroup.java,v 1.23 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on May 30, 2007, 9:40 AM
 *
 */
package gov.nasa.gsfc.spdf.orb.content;

import gov.nasa.gsfc.spdf.orb.content.shapes.FootpointShape;
import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.BTraceData;
import gov.nasa.gsfc.spdf.ssc.client.Hemisphere;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.media.j3d.Alpha;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

public class FootpointsGroup extends BranchGroup {

    private CoordinateSystem cs = null;

    /**
     * type of foot points. Could be north, south, or the combination of the
     * closest foot point to the satellite at some moment in time.
     */
    public enum HemisphericTypes {

        /**
         * the northern foot point
         */
        NORTH,
        /**
         * the southern foot point
         */
        SOUTH,
        /**
         * the closest foot point
         */
        CLOSEST
    }
    private static final int HEMISPHERIC_BRANCH_TYPES = 3;
    private final HemisphericBranch[] hemisphericBranchArray
            = new HemisphericBranch[HEMISPHERIC_BRANCH_TYPES];

    /**
     * Creates a new instance of FootpointsGroup
     *
     * @param location
     * @param satellitegp
     * @param graphModl
     * @param infoPane
     */
    public FootpointsGroup(final List<SatelliteData> location,
            final SatelliteGraphProperties[] satellitegp,
            final SatelliteGraphTableModel graphModl, final InfoPanel infoPane) {

        if (location.size() > 0) {
            this.cs = location.get(0).getCoordinates().get(0).
                    getCoordinateSystem();
        }

        setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        int i = 0;
        for (SatelliteData satData : location) {

            for (BTraceData bTraceData : satData.getBTraceData()) {

                FootpointShape footpointShape
                        = new FootpointShape(bTraceData,
                                satData.getTime(),
                                satellitegp[i],
                                graphModl,
                                infoPane, null, i);

                if (bTraceData.getHemisphere() == Hemisphere.NORTH) {

                    getHemisphericBranch(HemisphericTypes.NORTH).add(footpointShape);
                    footpointShape.setHemisphere(HemisphericTypes.NORTH);
                    footpointShape.setShapeParent(getHemisphericBranch(HemisphericTypes.NORTH).getTransformGroup());

                } else {

                    getHemisphericBranch(HemisphericTypes.SOUTH).add(footpointShape);
                    footpointShape.setHemisphere(HemisphericTypes.SOUTH);
                    footpointShape.setShapeParent(getHemisphericBranch(HemisphericTypes.SOUTH).getTransformGroup());
                }
            }

            if (satData.getBTraceData().size() == 1) {

                FootpointShape footpointShape
                        = new FootpointShape(satData.getBTraceData().get(0),
                                satData.getTime(),
                                satellitegp[i],
                                graphModl,
                                infoPane, null, i);

                getHemisphericBranch(HemisphericTypes.CLOSEST).add(footpointShape);
                footpointShape.setHemisphere(HemisphericTypes.CLOSEST);
                footpointShape.setShapeParent(getHemisphericBranch(HemisphericTypes.CLOSEST).getTransformGroup());
            } else if (satData.getBTraceData().size() == 2) {

                getHemisphericBranch(HemisphericTypes.CLOSEST).add(
                        makeClosest(satData,
                                satellitegp[i],
                                graphModl, infoPane, i));
            }
            i++;
        }
    }

    private HemisphericBranch getHemisphericBranch(final HemisphericTypes type) {

        if (hemisphericBranchArray[type.ordinal()] == null) {

            hemisphericBranchArray[type.ordinal()] = new HemisphericBranch();
        }

        return hemisphericBranchArray[type.ordinal()];
    }

    /**
     * Clears the display of the foot points.
     */
    public void clear() {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.clear();
            }
        }
    }
    /**
     * Value returned by SSC to indicate "not applicable" (NA).
     */
    private static final Double SSC_NA = -1.0E31;

    private FootpointShape makeClosest(final SatelliteData location, final SatelliteGraphProperties satellitegp,
            final SatelliteGraphTableModel graphModl, final InfoPanel infoPane, final int order) {

        BTraceData tracedata = new BTraceData();
        int len = location.getTime().size();

        Float[] lat = new Float[len];
        Float[] longit = new Float[len];
        boolean[] change = new boolean[len];
        Hemisphere hemisphere = location.getBTraceData().get(0).getHemisphere();

        for (int i = 0; i < len; i++) {

            lat[i] = location.getBTraceData().get(0).getLatitude().get(i);
            longit[i] = location.getBTraceData().get(0).getLongitude().get(i);
            change[i] = false;
        }

        for (int i = 0; i < len; i++) {

            if (location.getBTraceData().get(0).
                    getArcLength().get(i).isNaN()
                    || location.getBTraceData().get(0).
                    getArcLength().get(i).equals(SSC_NA)) {

                lat[i] = location.getBTraceData().get(1).
                        getLatitude().get(i);
                longit[i] = location.getBTraceData().get(1).
                        getLongitude().get(i);

                if (location.getBTraceData().get(0).getHemisphere()
                        == hemisphere) {

                    change[i] = true;
                    hemisphere = location.getBTraceData().get(1).
                            getHemisphere();
                }
            } else if (location.getBTraceData().get(1).
                    getArcLength().get(i).isNaN()
                    || location.getBTraceData().get(1).
                    getArcLength().get(i).equals(SSC_NA)) {

                if (location.getBTraceData().get(1).getHemisphere()
                        == hemisphere) {

                    change[i] = true;
                    hemisphere = location.getBTraceData().get(0).
                            getHemisphere();
                }
            } else if (location.getBTraceData().get(1).
                    getArcLength().get(i)
                    < location.getBTraceData().get(0).
                    getArcLength().get(i)) {

                lat[i] = location.getBTraceData().get(1).
                        getLatitude().get(i);
                longit[i] = location.getBTraceData().get(1).
                        getLongitude().get(i);
                if (location.getBTraceData().get(0).getHemisphere()
                        == hemisphere) {

                    change[i] = true;
                    hemisphere = location.getBTraceData().get(1).
                            getHemisphere();
                }
            } else if (location.getBTraceData().get(1).getHemisphere()
                    == hemisphere) {

                change[i] = true;
                hemisphere = location.getBTraceData().get(0).
                        getHemisphere();
            }
        }

        tracedata.getLatitude().addAll(Arrays.asList(lat));
        tracedata.getLongitude().addAll(Arrays.asList(longit));

        FootpointShape footpointShape = new FootpointShape(tracedata,
                location.getTime(),
                satellitegp,
                graphModl,
                infoPane, change, order);

        footpointShape.setShapeParent(getHemisphericBranch(HemisphericTypes.CLOSEST).getTransformGroup());
        footpointShape.setHemisphere(HemisphericTypes.CLOSEST);

        return footpointShape;
    }

    /**
     * Calls each hemispheric branch to set the new alpha node component object
     * that will be used during animation.
     *
     * @param alpha the alpha component
     */
    public void setAlpha(final Alpha alpha) {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.setAlpha(alpha);
            }
        }
    }

    /**
     * Calls each hemispheric branch to change the width of the symbol
     * representing the foot point in response to a user action.
     *
     * @param width the new width
     */
    public void symbolWidthChanged(final float width) {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.symbolWidthChanged(width);
            }
        }
    }

    /**
     * Calls each hemispheric branch to remove the listener to a change in
     * satellites selection.
     */
    public void removeSatelliteGraphChangeListener() {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.removeSatelliteGraphChangeListener();
            }
        }
    }

    /**
     * Calls each hemispheric branch to change the tracing line width in
     * response to a user action.
     *
     * @param lineWidth the new line width
     */
    public void setAppearance(final float lineWidth) {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.setAppearance(lineWidth);
            }
        }
    }

    /**
     * Returns the northern tracing.
     *
     * @return
     */
    public HemisphericBranch getNorthBound() {

        return hemisphericBranchArray[HemisphericTypes.NORTH.ordinal()];

    }

    /**
     * Return the southern tracing
     *
     * @return
     */
    public HemisphericBranch getSouthBound() {

        return hemisphericBranchArray[HemisphericTypes.SOUTH.ordinal()];
    }

    /**
     * Returns the closest hemispheric branch which is made up of a combination
     * of north and south tracing.
     *
     * @return the closest hemispheric branch.
     */
    public HemisphericBranch getClosest() {

        return hemisphericBranchArray[HemisphericTypes.CLOSEST.ordinal()];
    }

    /**
     * Calls each hemispheric branch to rotate the foot point shape following
     * the earth rotation.
     *
     * @param mjd the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858
     * @param hours is the time in hours since that preceding UT midnight
     */
    public void rotateFootpoint(final double mjd, final double hours) {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.rotateFootpoint(mjd, hours);
            }
        }
    }

    /**
     * Calls each hemispheric branch to rotate the foot point shape associated
     * with that branch as the time moves.
     *
     * @param index the percent of the move already passed.
     */
    public void rotateAnimatedShape(final float index) {
        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.rotateAnimatedShape(index);
            }
        }
    }

    public void processFootpointManually(HemisphericTypes type) {

        getHemisphericBranch(type).processFootpointManually();

    }

    /**
     * /**
     * Enables the changing of the foot point position with respect to time.
     *
     * @param enable if the foot point is displayed.
     */
    public void setAnimEnable(final boolean enable) {

        for (HemisphericBranch hemisphericBranchArray1 : hemisphericBranchArray) {
            if (hemisphericBranchArray1 != null) {
                hemisphericBranchArray1.setAnimEnable(enable);
            }
        }
    }

    /**
     * This class keep track of everything related to an hemispheric node.
     */
    public class HemisphericBranch extends BranchGroup {

        private final Transform3D rot = new Transform3D();
        private final TransformGroup rotGroup = new TransformGroup(rot);

        /**
         * Construct the hemispheric branch. One per hemispheric types.
         */
        public HemisphericBranch() {

            rotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            rotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            rotGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        }

        /**
         * the transform group for the foot point
         *
         * @return the rotational transform group.
         */
        public TransformGroup getTransformGroup() {

            return rotGroup;
        }

        /**
         * Get the alpha node component object from the animation and passes it
         * to the foot point shape object.
         *
         * @param alpha the animation alpha component.
         */
        public void setAlpha(final Alpha alpha) {

            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();
                if (object instanceof FootpointShape && ((FootpointShape) object).getPositionPath() != null) {

                    ((FootpointShape) object).getPositionPath().setAlpha(alpha);
                }
            }
        }

        /**
         * Add a foot point shape for each satellites and hemispheric type.
         *
         * @param child the foot point for a singular type and spacecraft.
         */
        public void add(final Node child) {

            rotGroup.addChild(child);

            rotGroup.addChild(((FootpointShape) child).getTransformGroup());
        }

        /**
         * Clears all the foot point tracing from the scene graph.
         */
        public void clear() {

            Enumeration e = rotGroup.getAllChildren();

            while (e.hasMoreElements()) {

                Object object = e.nextElement();

                if (object instanceof FootpointShape) {

                    ((FootpointShape) object).removeAllGeometries();
                    ((FootpointShape) object).clear();
                }
            }
        }

        /**
         * Remove the listener for changing the satellites selections.
         */
        public void removeSatelliteGraphChangeListener() {

            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();
                if (object instanceof FootpointShape) {

                    ((FootpointShape) object).removeSatelliteGraphChangeListener();
                }
            }
        }

        /**
         * Reset the width of the line representing the foot point tracing.
         *
         * @param lineWidth the new tracing width
         */
        public void setAppearance(final float lineWidth) {

            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();
                if (object instanceof FootpointShape) {

                    ((FootpointShape) object).setAppearance(lineWidth);
                }
            }
        }

        /**
         * Changes the dimension of the symbol representing the foot point in
         * response to a user request.
         *
         * @param width the new width.
         */
        public void symbolWidthChanged(final float width) {

            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();
                if (object instanceof FootpointShape) {

                    ((FootpointShape) object).symbolWidthChanged(width);
                }
            }
        }

        /**
         * Enables the changing of the foot point position with respect to time.
         *
         * @param enable if the foot point is displayed.
         */
        public void setAnimEnable(final boolean enable) {

            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();
                if (object instanceof FootpointShape) {

                    ((FootpointShape) object).setAnimEnable(enable);
                }
            }
        }

        /**
         * rotate the foot point shape following the earth rotation.
         *
         * @param mjd the Modified Julian Date which is the time measured in
         * days from 00:00 UT on 17 November 1858
         * @param hours is the time in hours since that preceding UT midnight
         */
        public void rotateFootpoint(final double mjd, final double hours) {

            rotGroup.setTransform(Util.rotate(cs, mjd, hours, false));
        }

        /**
         * move the foot point shape as the earth is rotating.
         *
         * @param index percent completed of the displacement.
         */
        public void rotateAnimatedShape(final float index) {

            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();

                if (object instanceof FootpointShape) {
                    ((FootpointShape) object).rotateAnimatedShape(index);
                }
            }
        }

        public void processFootpointManually() {
            java.util.Enumeration enumeration = rotGroup.getAllChildren();

            while (enumeration != null && enumeration.hasMoreElements()) {

                Object object = enumeration.nextElement();

                if (object instanceof FootpointShape) {
                    ((FootpointShape) object).processFootpointManually();
                }
            }

        }

    }
}
