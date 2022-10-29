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
 * $Id: Animation.java,v 1.31 2015/10/30 14:18:50 rchimiak Exp $
 * Created on June 12, 2007, 9:07 AM
 *
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import java.util.GregorianCalendar;
import java.util.Enumeration;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.media.j3d.Alpha;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Behavior;
import javax.media.j3d.Group;
import javax.swing.JSpinner;
import javax.swing.JFormattedTextField;
import javax.swing.SpinnerNumberModel;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.Slider;
import gov.nasa.gsfc.spdf.orb.gui.ZoomControl;
import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.utils.AnimatedGifEncoder;
import gov.nasa.gsfc.spdf.orb.utils.ImageCapture;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.util.Date;
import javax.vecmath.Point3f;

/**
 *
 * @author rachimiak
 */
public abstract class Animation extends Behavior {

    private TransformGroup[] target;
    protected final BranchGroup rootObj = new BranchGroup();
    protected AnimBehavior[] animation;
    protected Slider slider = null;
    protected CustomAlpha alpha = new CustomAlpha(-1, -1);
    private JSpinner speedSpinner = null;
    protected JFormattedTextField time = null;
    private ImageCapture imageCapture = null;
    private AnimatedGifEncoder encoder = null;
    protected static GregorianCalendar beginning = new GregorianCalendar(Util.UTC_TIME_ZONE);
    protected static GregorianCalendar present = new GregorianCalendar(Util.UTC_TIME_ZONE);
    protected static GregorianCalendar end = new GregorianCalendar(Util.UTC_TIME_ZONE);
    private static final int LOOP_BEGINNING = 180;
    private static int loopCount = LOOP_BEGINNING;
    public static final int TIME_FROM_ZERO_TO_ONE = 18000;
    protected SatBranch parent = null;
    protected static final Point3f center = new Point3f(0f, 0f, 0f);

    /**
     * Creates a new instance of Animation.
     *
     * @param location data relating to the satellites.
     * @param parent the parent node to the animation.
     */
    public Animation(final List<? extends Object> location, final SatBranch parent) {

        animation = new AnimBehavior[location.size()];

        target = new TransformGroup[location.size()];

        this.slider = OrbitViewer.getSlider();

        for (int j = 0; j < location.size(); j++) {
            target[j] = new TransformGroup();

            animation[j]
                    = createAnimBehavior(target[j], location.get(j),
                            j, alpha, slider);

            rootObj.addChild(animation[j].getPositionPath());

            target[j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            target[j].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            target[j].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            target[j].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            target[j].setCapability(Group.ALLOW_CHILDREN_WRITE);
        }

        this.parent = parent;

        this.speedSpinner = OrbitViewer.getToolBar().getSpeedSpinner();

        time = OrbitViewer.getToolBar().getTime();

        if (!ControlPanel.isImport) {

            beginning.setTime(OrbitViewer.getControlPane().getStartDate());

            present.setTime(OrbitViewer.getControlPane().getStartDate());

            end.setTime(OrbitViewer.getControlPane().getEndDate());
        }

    }

    protected abstract AnimBehavior createAnimBehavior(TransformGroup target, Object location,
            int j, Alpha alpha, Slider slider);

    /**
     * Returns the transform group for the satellites
     *
     * @return the satellites transform group
     */
    public final TransformGroup[] getTarget() {

        return target;
    }

    /**
     * Returns the node that holds the animation.
     *
     * @return animation node
     */
    public final BranchGroup getAnimBranch() {

        return rootObj;
    }

    /**
     * Returns an array of animation behavior.
     *
     * @return animation behavior array.
     */
    public final AnimBehavior[] getAnimationNodes() {

        return animation;
    }

    /**
     * Sets the number of times to run this animation. a value of _1 specifies
     * indefinite looping.
     *
     * @param loopCount number of runs.
     */
    public static void setLoopCount(final int loopCount) {
        Animation.loopCount = loopCount;
    }

    /**
     * Returns the Calendar set to the beginning of the animation.
     *
     * @return calendar with time corresponding to the beginning of the
     * animation
     */
    public static GregorianCalendar getBeginning() {
        return beginning;
    }

    /**
     * Called when the graph is first displayed with a new satellites selection.
     * Set initial conditions for some of the animation elements.
     */
    @Override
    public void initialize() {

        setEnable(false);

        slider.setManual(true);

    }

    /**
     * Called directly by the Java3D behavior scheduler and not this
     * application. Called only during automatic animation, not manual. Move
     * elements of the animation along with the satellites.
     *
     * @param criteria enumeration of triggered wake up criteria.
     */
    @Override
    public abstract void processStimulus(final Enumeration criteria);

    /**
     * the animation length from beginning to end.
     *
     * @return the animation length in milliseconds
     *
     */
    public static long getTotalTime() {

        return end.getTimeInMillis() - beginning.getTimeInMillis();
    }

    /**
     * the speed at which the animation performs. Defaulted to 10 but changeable
     * by user thru a spinner.
     *
     * @return the speed spinner value (no units arbitrary).
     */
    public int getSpeed() {

        return (((SpinnerNumberModel) speedSpinner.getModel()).getNumber().intValue());
    }

    /**
     * gives the animation a pointer to the zoom window for each spacecraft in
     * the display.
     *
     * @param zoom a reference to the zoom window or null if none opened.
     */
    public void setZoom(final ZoomControl zoom) {

        for (AnimBehavior animation1 : animation) {

            animation1.getPositionPath().setZoom(zoom);
        }
    }

    /**
     * Called when user starts the animation by clicking on the play icon on the
     * tool bar. starts the animation.
     */
    public void play() {

        alpha.setLoopCount(loopCount);

        if (alpha.getIncreasingAlphaDuration() == -1) {
            alpha.setIncreasingAlphaDuration(TIME_FROM_ZERO_TO_ONE / getSpeed());
            alpha.setStartTime(System.currentTimeMillis());
        }

        if (alpha.isPaused()) {
            alpha.resume();
        }

        setEnable(true);
        slider.setManual(false);

        for (AnimBehavior animation1 : animation) {
            animation1.getPositionPath().play();
        }

    }

    /**
     * Commands the temporary stop of the animation. Responds to the tool bar
     * pause action.
     */
    public void pause() {

        if (!alpha.isPaused()
                && alpha.getIncreasingAlphaDuration() != -1) {

            setEnable(false);
            slider.setManual(true);
            alpha.pause();

            for (AnimBehavior animation1 : animation) {
                animation1.getPositionPath().pause();
            }

        }
    }

    public boolean isPaused() {

        return alpha.isPaused();
    }

    public boolean isPlayed() {

        return (alpha.getIncreasingAlphaDuration() != -1 && !alpha.isPaused());
    }

    public boolean isStopped() {

        return (alpha.getIncreasingAlphaDuration() == -1);
    }

    /**
     * Called when the user clicks on the stop icon in the tool bar. Stops the
     * animation and resets the scene to the beginning.
     */
    public void stop() {

        slider.setManual(true);
        slider.setValue(0);
        present.setTime(new Date(beginning.getTimeInMillis()));

        alpha.setIncreasingAlphaDuration(-1);

        for (AnimBehavior animation1 : animation) {
            animation1.getPositionPath().stop();
        }

    }

    /**
     * Manually connects the time with the slider offset and the spacecraft
     * position.
     */
    public abstract void processManual();

    /**
     * Removes animation elements when starting a new graph scene.
     */
    public void clear() {

        for (int i = 0; i < animation.length; i++) {

            animation[i].clear();
            animation[i] = null;
            target[i] = null;
        }
        animation = null;
        target = null;
    }

    /**
     * Sets the value of the alpha duration in order to control the speed of the
     * animation.
     */
    public void setAlpha() {

        if (alpha.getIncreasingAlphaDuration() != -1) {

            int speed = (((SpinnerNumberModel) speedSpinner.getModel()).getNumber().intValue());
            alpha.setIncreasingAlphaDuration(TIME_FROM_ZERO_TO_ONE / (speed));
        }
    }

    /**
     * Start the process of recording the animation when the scene is saved into
     * a animated gif movie.
     *
     * @param outFile the file to write the animated gif to.
     * @param rate playback rate of animated gif
     * @param repeat number of loop repetitions
     */
    public void startRecording(final String outFile, final int rate, final int repeat) {

        encoder = new AnimatedGifEncoder();

        try {

            encoder.setRepeat(repeat);
            imageCapture = new ImageCapture(OrbitViewer.getViewBranchArray()[0].getCanvas(), OrbitViewer.getSatellitePositionWindow());

            encoder.start(outFile);
            encoder.setFrameRate(rate);
        } catch (Exception ex) {
        }
    }

    /**
     * process the recording of the saved animated gif.
     */
    public void processRecording() {

        final int timeToSleepInMilliseconds = 100;

        try {
            Thread.sleep(timeToSleepInMilliseconds);
        } catch (InterruptedException ex) {
        }

        BufferedImage buff = imageCapture.captureImage();
        if (encoder != null) {
            boolean addFrame = encoder.addFrame(buff);
        }
    }

    /**
     * end the recording of the saved animated gif.
     */
    public void stopRecording() {
        stop();
        imageCapture.finish();
        imageCapture = null;
        encoder.finish();
        encoder = null;
    }

    //this class defines an Alpha class that reads time/alpha
//value pairs from a file and linearly interpolates between
//them. It illustrates creating your own Alpha class for
//interpolation.
    protected class CustomAlpha extends Alpha {

        public CustomAlpha(int loopCount, long increasingAlphaDuration) {

            super(loopCount, increasingAlphaDuration);
        }

        // core method override
        // returns the Alpha value for a given time
        @Override
        public float value() {

            long total = end.getTimeInMillis() - beginning.getTimeInMillis();
            long actual = present.getTimeInMillis() - beginning.getTimeInMillis();

            float percentTime = (float) actual / (float) total;

            if (percentTime > 1) {
                return 0;
            }

            float value = percentTime + (1f / this.getIncreasingAlphaDuration());

            return value;

        }
    }
}
