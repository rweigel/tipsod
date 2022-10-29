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
 * $Id: Slider.java,v 1.21 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on April 10, 2002, 8:52 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The slider class creates the slider widget used to control the animation.
 *
 * @author rchimiak
 * @version $Revision: 1.21 $
 */
public class Slider extends JSlider {

    private boolean manual = true;
    /**
     * The maximum value in the slider rang. e
     */
    private final static int max = 0;

    /**
     * Creates a new Slider instance.
     */
    public Slider() {

        super(JSlider.HORIZONTAL, 0, max, 0);

        putClientProperty("JSlider.isFilled", Boolean.TRUE);
        addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null) {

                    if (manual) {

                        Animation animation = ContentBranch.getSatBranch().getAnimation();

                        if (animation != null) {
                            animation.processManual();
                        }
                    }
                }
            }
        });
    }

    /**
     * Represents the selection mode chosen by the user.
     *
     * @return yes for manual and no for automatic mode.
     */
    public boolean getManual() {
        return manual;
    }

    /**
     * Sets the slider selection mode.
     *
     * @param value true for manual, false for automatic.
     */
    public void setManual(boolean value) {
        manual = value;
    }

    public void resetMax() {

        this.setMaximum(max);
    }
}
