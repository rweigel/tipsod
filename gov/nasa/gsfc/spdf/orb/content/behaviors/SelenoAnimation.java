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
 * $Id: SelenoAnimation.java,v 1.3 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.EarthGroup;
import gov.nasa.gsfc.spdf.orb.content.SelenoSatBranch;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.SelenoTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.Slider;
import gov.nasa.gsfc.spdf.orb.utils.MHDPause;
import gov.nasa.gsfc.spdf.orb.utils.MHDSurf;
import gov.nasa.gsfc.spdf.orb.utils.ModifiedJulianCalendar;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import javax.media.j3d.Alpha;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author rchimiak
 */
public class SelenoAnimation extends Animation {

    private static AnimBehavior earthAnimation = null;
    private final ModifiedJulianCalendar mjc = new ModifiedJulianCalendar();

    /**
     *
     * @author rchimiak
     * @param location
     * @param parent
     */
    public SelenoAnimation(final List<SatelliteData> location, final SelenoSatBranch parent) {

        super(location, parent);

        if (!ControlPanel.isImport) {

            earthAnimation = null;
            earthAnimation = createAnimBehavior(parent.getEarthTransformGroup(), parent.getSse().getEarthData(), -1, alpha, slider);
            rootObj.addChild(earthAnimation.getPositionPath());

            Point3f p = new Point3f();
            Transform3D t = new Transform3D();

            earthAnimation.getPositionPath().getPosition(0, p);
            earthAnimation.getPositionPath().getTarget().getTransform(t);
            t.setTranslation(new Vector3f(p.x, p.y, p.z));
            earthAnimation.getPositionPath().getTarget().setTransform(t);

        } else {

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
            if (earthAnimation != null) {
                earthAnimation.getPositionPath().setEnable(false);
            }

            slider.setManual(true);
            slider.setValue(0);
            present.setTime(new Date(beginning.getTimeInMillis()));
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

            mjc.setTime(present.getTime());

            if (parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.EARTH)) != null
                    && (SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.EARTH).isSelected())) {

                ((EarthGroup) ((SelenoSatBranch) parent).getEarthTransformGroup().getChild(0)).rotateEarth(mjc.getMjd(), mjc.getHours());
            }
        }

        this.wakeupOn(new WakeupOnElapsedFrames(0));

        if (parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.BOWSHOCK)) != null && earthAnimation != null) {
            ((MHDSurf) parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.BOWSHOCK))).doTranslation(earthAnimation.getPositionPath().getTarget());
        }

        if (parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MAGNETOPAUSE)) != null && earthAnimation != null) {
            ((MHDPause) parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MAGNETOPAUSE))).doTranslation(earthAnimation.getPositionPath().getTarget());
        }

        if (SelenoTogglePanel.getCheckBoxArray()[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.ORBIT)].isSelected() == false) {

            for (AnimBehavior animation1 : animation) {
                animation1.getPositionPath().processOrbitManually();
            }
        }
    }

    /**
     * Returns the behavior object for the earth.
     *
     * @return earth animation behavior instance.
     */
    public static AnimBehavior getEarthAnimation() {

        return earthAnimation;
    }

    /**
     * Called when user starts the animation by clicking on the play icon on the
     * tool bar. starts the animation.
     */
    @Override
    public void play() {

        super.play();

        if (earthAnimation != null) {
            earthAnimation.getPositionPath().play();
        }
    }

    /**
     * Commands the temporary stop of the animation. Responds to the tool bar
     * pause action.
     */
    @Override
    public void pause() {

        super.pause();

        if (earthAnimation != null) {
            earthAnimation.getPositionPath().pause();
        }
    }

    /**
     * Called when the user clicks on the stop icon in the tool bar. Stops the
     * animation and resets the scene to the beginning.
     */
    @Override
    public void stop() {

        super.stop();

        if (earthAnimation != null) {
            earthAnimation.getPositionPath().stop();
        }
    }

    /**
     * Manually connects the time with the slider offset and the spacecraft
     * position.
     */
    @Override
    public void processManual() {

        float percentCompleted = (float) (slider.getValue()) / (float) (slider.getMaximum());

        present.setTime(new Date(beginning.getTimeInMillis() + (long) (percentCompleted * getTotalTime())));

        present.set(GregorianCalendar.SECOND, 0);
        time.setValue(present.getTime());
        mjc.setTime(present.getTime());

        OrbitViewer.getSatellitePositionWindow().updateTime(present.getTime());
        if (parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.EARTH)) != null
                && (SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.EARTH).isSelected())) {
            ((EarthGroup) ((SelenoSatBranch) parent).getEarthTransformGroup().getChild(0)).rotateEarth(mjc.getMjd(), mjc.getHours());

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
        if (earthAnimation != null) {
            earthAnimation.getPositionPath().processManual(percentCompleted);
        }

        if (parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.BOWSHOCK)) != null && earthAnimation != null) {
            ((MHDSurf) parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.BOWSHOCK))).doTranslation(earthAnimation.getPositionPath().getTarget());
        }

        if (parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MAGNETOPAUSE)) != null && earthAnimation != null) {
            ((MHDPause) parent.getCheckBoxSwitchElement(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MAGNETOPAUSE))).doTranslation(earthAnimation.getPositionPath().getTarget());
        }
    }
}
