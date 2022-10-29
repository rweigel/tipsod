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
 * $Id: EarthGroup.java,v 1.32 2015/10/30 14:18:50 rchimiak Exp $
 * EarthGroup.java
 *
 * Created on September 4, 2002, 11:52 AM
 */
package gov.nasa.gsfc.spdf.orb.content;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.shapes.Earth;
import gov.nasa.gsfc.spdf.orb.content.shapes.GeoGrid;
import gov.nasa.gsfc.spdf.orb.content.shapes.GroundStations;
import gov.nasa.gsfc.spdf.orb.content.shapes.SunLight;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.utils.ModifiedJulianCalendar;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.util.Calendar;
import java.util.Date;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

/**
 * The EarthGroup class implements a transform group to manipulate the earth.
 *
 * @author rchimiak
 * @version $Revision: 1.32 $
 */
public class EarthGroup extends BranchGroup {

    private CoordinateSystem cs = null;
    private final TransformGroup earthRot = new TransformGroup();
    private final TransformGroup earthScale = new TransformGroup();
    private ModifiedJulianCalendar mjc = null;
    private SunLight sl;
    private Earth earth = null;
    private GeoGrid geogrid = null;
    private GroundStations groundStations = null;

    /**
     * construct the node that control the earth movement (rotation)
     */
    public EarthGroup() {

        earthRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        earthRot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        earthRot.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        earthScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        earthScale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        earthScale.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        earth = new Earth(1);

        if (earth != null) {

            earth.setCapability(Node.ALLOW_BOUNDS_READ);
            earthRot.addChild(earth);
        }

        addChild(earthScale);
        earthScale.addChild(earthRot);
        //  addChild(earthRot);
    }

    /**
     * set the branch that handles the display of the orbit data.
     *
     * @param satBranch
     */
    public void setSatParams(final SatBranch satBranch) {

        if (ControlPanel.isImport) {

            if (((SatelliteData) satBranch.getSatelliteData().get(0)).getTime().size() > 0) {

                Calendar c
                        = ((SatelliteData) satBranch.getSatelliteData().get(0)).getTime().get(0).
                        toGregorianCalendar();

                mjc = new ModifiedJulianCalendar(c.getTime());
            }
        } else if (OrbitViewer.getToolBar().getTime().getValue() == null) {
            mjc = new ModifiedJulianCalendar(
                    (Date) OrbitViewer.getControlPane().getStartDate());
        } else {
            mjc = new ModifiedJulianCalendar(
                    (Date) OrbitViewer.getToolBar().getTime().getValue());
        }

        if (!ControlPanel.isImport) {
            cs = (CoordinateSystem) OrbitViewer.getControlPane().getCoordinateSystem();
        } else {
            cs = (CoordinateSystem) satBranch.getCoordinateSystem();
        }

        sl = new SunLight(cs, mjc);

       rotateEarth(mjc.getMjd(), mjc.getHours());

    }

    /**
     * handles the sun light (directional light + coordinate system).
     *
     * @return the sun light.
     */
    public final SunLight getSunlight() {
        return sl;
    }

    /**
     * return the earth shape (sphere of radius 1).
     *
     * @return the earth shape.
     */
    public final Earth getEarth() {
        return earth;
    }

    /**
     * return the calendar used to calculate the initial time when the earth
     * group was set.
     *
     * @return the calendar being used when the orbit is first positioned.
     */
    public final ModifiedJulianCalendar getCurrentCalendar() {

        return mjc;
    }

    /**
     * + Returns the latitude and longitude shapes that can be displayed on the
     * scene graph.
     *
     * @return the grid
     */
    public final GeoGrid getGeogrid() {

        return geogrid;

    }

    /**
     * Returns the transform for the grid (latitude, longitude, labels) that
     * gets activated with the earth rotation.
     *
     * @return the grid transform.
     */
    public TransformGroup getGeographTransform() {

        if (geogrid == null) {
            geogrid = new GeoGrid();
        } else {
            geogrid.setLabels();
        }

        return geogrid.getTransform();

    }

    /**
     * Returns the transform that works on the ground stations display,
     *
     * @return the ground stations transform.
     */
    public TransformGroup getStationsTransform() {

        if (groundStations == null) {
            groundStations = OrbitViewer.getGroundStationsWindow().getStations();
        }

        return groundStations.getTransform();
    }

    /**
     * clears the geographic grid and ground stations from the display.
     */
    public void clear() {

        if (geogrid != null) {
            geogrid.clear();
        }

        if (groundStations != null) {
            groundStations.clear();
        }
    }

    public void scaleEarth(float scale) {

        Transform3D transform3D = new Transform3D();

        transform3D.setScale(scale);
        earthScale.setTransform(transform3D);

    }

    /**
     * Positions the earth according to time and coordinate system.
     *
     * @param mjd the Modified Julian Date which is the time measured in days
     * from 00:00 UT on 17 November 1858
     * @param hours is the time in hours since that preceding UT midnight
     */
    public void rotateEarth(final double mjd, final double hours) {

        if (geogrid != null) {
            geogrid.rotateGeogrid(mjd, hours, cs);
        }

        if (groundStations != null) {
            groundStations.rotateStations(mjd, hours, cs);
        }

        if (earth == null) {
            return;
        }

        earthRot.setTransform(Util.rotate(cs, mjd, hours, true));

        if (earth.getAppearance().getMaterial() != null) {
            sl.rotateDirection(mjd, hours);
        }

    }

}
