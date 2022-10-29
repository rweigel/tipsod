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
 * $Id: HelioAnimation.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.HelioSatBranch;
import gov.nasa.gsfc.spdf.orb.gui.HelioTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.Slider;
import gov.nasa.gsfc.spdf.orb.utils.ModifiedJulianCalendar;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import javax.media.j3d.Alpha;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3f;

/**
 *
 * @author rchimiak
 */
public class HelioAnimation extends Animation {

    private final ModifiedJulianCalendar mjc = new ModifiedJulianCalendar();

    public HelioAnimation(final List<Trajectory> location, final HelioSatBranch parent) {

        super(location, parent);
    }

    @Override
    protected AnimBehavior createAnimBehavior(TransformGroup target, Object location,
            int j, Alpha alpha, Slider slider) {

        return new HelioAnimBehavior(target, (Trajectory) location,
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

            Point3f point = animation[i].getPositionPath().getPosition();

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
            //  }

            Util.invokeLater2(new Runnable() {
                @Override
                public void run() {

                    time.setValue(present.getTime());
                    OrbitViewer.getSatellitePositionWindow().updateTime(present.getTime());
                }
            });
        }

        mjc.setTime(present.getTime());

        this.wakeupOn(new WakeupOnElapsedFrames(0));

        if (alpha.finished()) {
            stop();
        }

        if (HelioTogglePanel.getCheckBoxArray()[HelioTogglePanel.getPosition(HelioTogglePanel.Element.ORBIT)].isSelected() == false) {

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

                animation[i].getPositionPath().processOrbitManually();

            }
        }

    }

    /**
     * Called when user starts the animation by clicking on the play icon on the
     * tool bar. starts the animation.
     */
    @Override
    public void play() {

        super.play();
    }

    /**
     * Commands the temporary stop of the animation. Responds to the tool bar
     * pause action.
     */
    @Override
    public void pause() {

        super.pause();
    }

    /**
     * Called when the user clicks on the stop icon in the tool bar. Stops the
     * animation and resets the scene to the beginning.
     */
    @Override
    public void stop() {

        super.stop();
        //  alpha.setLoopCount(0);

    }

    /**
     * Manually connects the time with the slider offset and the spacecrat
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

        if (animation != null) {
            for (AnimBehavior animation1 : animation) {
                if (animation1.getPositionPath() != null) {
                    animation1.getPositionPath().processManual(percentCompleted);
                }
            }
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
