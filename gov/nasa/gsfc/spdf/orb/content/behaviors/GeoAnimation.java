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
 * $Id: GeoAnimation.java,v 1.5 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.EarthGroup;
import gov.nasa.gsfc.spdf.orb.content.FootpointsGroup;
import gov.nasa.gsfc.spdf.orb.content.GeoSatBranch;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.GeoTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.Slider;

import gov.nasa.gsfc.spdf.orb.utils.MHDNeutral;
import gov.nasa.gsfc.spdf.orb.utils.ModifiedJulianCalendar;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import javax.media.j3d.Alpha;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.xml.datatype.XMLGregorianCalendar;

public class GeoAnimation extends Animation {

    private FootpointsGroup footpoints = null;

    private final ModifiedJulianCalendar mjc = new ModifiedJulianCalendar();

    /**
     *
     * @author rchimiak
     */
    public GeoAnimation(final List<SatelliteData> location, final GeoSatBranch parent) {

        super(location, parent);

        if (ControlPanel.isImport) {

            List<XMLGregorianCalendar> times
                    = parent.getSatelliteData().get(0).getTime();
            // times of 1st satellite
            if (times.size() > 0) {

                beginning.setTime(
                        times.get(0).toGregorianCalendar().getTime());
                present.setTime(
                        times.get(0).toGregorianCalendar().getTime());
                end.setTime(
                        times.get(times.size() - 1).toGregorianCalendar().
                        getTime());
            }
        }

    }

    @Override
    protected AnimBehavior createAnimBehavior(TransformGroup target, Object location,
            int j, Alpha alpha, Slider slider) {

        return new GeoAnimBehavior(target, (SatelliteData) location,
                j, alpha, slider);
    }

    /**
     * Called when the graph is first displayed with a new satellites selection.
     * Set initial conditions for some of the animation elements.
     */
    @Override
    public void initialize() {

        super.initialize();

        if (parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET)) != null) {

            ((MHDNeutral) parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET))).setCalendar(mjc);
        }

        footpoints = ((GeoSatBranch) parent).getFootpoints();
        if (footpoints != null) {
            footpoints.setAlpha(alpha);
        }

        processManual();
        this.wakeupOn(new WakeupOnElapsedFrames(0));
    }

    /**
     * Called directly by the Java3D behavior scheduler and not this
     * application. Called only during automatic animation, not manual. Move
     * elements of the animation along with the satellites.
     *
     * @param criteria enumeration of triggered wake up criteria.
     */
    @Override
    public void processStimulus(final Enumeration criteria) {

        for (int i = 0; i < animation.length; i++) {

            if (animation[i].getPositionPath().getPosition().distance(center) == 0) {

                if (parent.getVisible(i)) {
                    parent.setVisible(i, false);
                }

            } else {

                if (!parent.getVisible(i)) {
                    parent.setVisible(i, true);
                }
            }
        }

        if (alpha.getIncreasingAlphaDuration() == -1) {

            setEnable(false);

            for (AnimBehavior animation1 : animation) {
                animation1.getPositionPath().setEnable(false);
            }

            if (footpoints != null) {
                footpoints.setAnimEnable(this.getEnable());
            }
            slider.setManual(true);

            present.setTime(new Date(beginning.getTimeInMillis()));

            Util.invokeLater2(new Runnable() {
                @Override
                public void run() {

                    slider.setValue(0);

                    time.setValue(present.getTime());
                    OrbitViewer.getSatellitePositionWindow().updateTime(present.getTime());
                }
            });
            alpha.setLoopCount(0);

        } else {

            Util.invokeLater2(new Runnable() {
                @Override
                public void run() {

                    slider.setValue((int) (alpha.value() * slider.getMaximum()));

                }
            });

            present.setTime(new Date(beginning.getTimeInMillis() + (long) (alpha.value() * (getTotalTime()))));

            Util.invokeLater2(new Runnable() {
                @Override
                public void run() {

                    time.setValue(present.getTime());
                    OrbitViewer.getSatellitePositionWindow().updateTime(present.getTime());
                }
            });
        }

        mjc.setTime(present.getTime());

        if ((parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET)) == null || !GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.NEUTRAL_SHEET).isEnabled())
                || !GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.NEUTRAL_SHEET).isSelected()) {
        } else {
            ((MHDNeutral) parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET))).update();
        }

        if (parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.EARTH)) != null) {

            ((EarthGroup) parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.EARTH))).rotateEarth(mjc.getMjd(), mjc.getHours());
        }

        if (footpoints != null) {
            footpoints.rotateFootpoint(mjc.getMjd(), mjc.getHours());
        }

        this.wakeupOn(new WakeupOnElapsedFrames(0));

//        if (alpha.finished()) {
//            stop();
//        }
//new
        if (GeoTogglePanel.getCheckBoxArray()[GeoTogglePanel.getPosition(GeoTogglePanel.Element.ORBIT)].isSelected() == false) {

            for (AnimBehavior animation1 : animation) {
                animation1.getPositionPath().processOrbitManually();
            }
        }
        if (footpoints != null) {

            if (GeoTogglePanel.getCheckBoxArray()[GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_NORTH)].isSelected() == false) {

                footpoints.processFootpointManually(FootpointsGroup.HemisphericTypes.NORTH);

            }
            if (GeoTogglePanel.getCheckBoxArray()[GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_SOUTH)].isSelected() == false) {

                footpoints.processFootpointManually(FootpointsGroup.HemisphericTypes.SOUTH);

            }
            if (GeoTogglePanel.getCheckBoxArray()[GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST)].isSelected() == false) {

                footpoints.processFootpointManually(FootpointsGroup.HemisphericTypes.CLOSEST);

            }

        }

    }
//new

    /**
     * Called when user starts the animation by clicking on the play icon on the
     * tool bar. starts the animation.
     */
    @Override
    public void play() {

        super.play();

        if (footpoints != null) {

            footpoints.setAnimEnable(this.getEnable());
        }

    }

    /**
     * Commands the temporary stop of the animation. Responds to the tool bar
     * pause action.
     */
    @Override
    public void pause() {

        super.pause();
        if (footpoints != null) {

            footpoints.setAnimEnable(this.getEnable());
        }
        //     alpha.setLoopCount(0);

    }

    /**
     * Called when the user clicks on the stop icon in the tool bar. Stops the
     * animation and resets the scene to the beginning.
     */
    @Override
    public void stop() {

        super.stop();

        if (footpoints != null) {
            footpoints.setAnimEnable(false);
        }

    }

    /**
     * Manually connects the time with the slider offset and the spacecrat
     * position.
     */
    @Override
    public void processManual() {

        float percentCompleted = (float) (slider.getValue()) / (float) (slider.getMaximum());

        present.setTime(new Date(beginning.getTimeInMillis() + (long) (percentCompleted * getTotalTime())));
        if (present.get(GregorianCalendar.SECOND) > 30) {

            int min = present.get(GregorianCalendar.MINUTE);
            present.set(GregorianCalendar.MINUTE, min + 1);
        }

        present.set(GregorianCalendar.SECOND, 0);
        time.setValue(present.getTime());
        mjc.setTime(present.getTime());

        OrbitViewer.getSatellitePositionWindow().updateTime(present.getTime());

        if (parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET)) != null
                && GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.NEUTRAL_SHEET).isEnabled()
                && GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.NEUTRAL_SHEET).isSelected()) {
            ((MHDNeutral) parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET))).update();
        }
        if (parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.EARTH)) != null) {
            ((EarthGroup) parent.getCheckBoxSwitchElement(GeoTogglePanel.getPosition(GeoTogglePanel.Element.EARTH))).rotateEarth(mjc.getMjd(), mjc.getHours());
        }

        if (footpoints != null) {

            footpoints.rotateFootpoint(mjc.getMjd(), mjc.getHours());

            for (AnimBehavior animation1 : animation) {
                footpoints.rotateAnimatedShape(animation1.getPositionPath().getIndex(percentCompleted));
                footpoints.rotateAnimatedShape(percentCompleted);
            }
        }
        if (animation != null) {

            for (int i = 0; i < animation.length; i++) {

                if (animation[i].getPositionPath().getPosition().distance(center) == 0) {

                    if (parent.getVisible(i)) {
                        parent.setVisible(i, false);
                    }
                } else {

                    if (!parent.getVisible(i)) {
                        parent.setVisible(i, true);
                    }
                }

                animation[i].getPositionPath().processManual(percentCompleted);

            }
        }

    }

}
