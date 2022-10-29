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
 * $Id: BowshockWindow.java,v 1.16 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on December 3, 2002, 12:10 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color4f;

/**
 * Instanciates the window allowing the user to change some of the bowshock
 * display attributes.
 *
 * @author rchimiak
 * @version $Revision: 1.16 $
 */
public class BowshockWindow extends SurfaceWindow {

    /**
     * Creates a new instance of BowshockWindow.
     */
    public BowshockWindow() {

        this.setTitle("Bowshock");
        color = new Color4f(0.0f, 0.6f, 0.0f, 1f);
        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(scrnSize.width / 15, scrnSize.height / 15);

        colorButton.setBackground(color.get());

        opacitySlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {

                model.setOpacity(getOpacity());
                model.setTransparencyAttributes(
                        opacitySlider.getValue() == opacitySlider.getMaximum()
                        && getPolygonAttributes() == PolygonAttributes.POLYGON_FILL
                                ? TransparencyAttributes.SCREEN_DOOR
                                : getTransparencyAttributes());
            }
        });

        radioButtons[0].addItemListener(new RadioButtonListener());
        radioButtons[1].addItemListener(new RadioButtonListener());
        radioButtons[2].addItemListener(new RadioButtonListener());
    }

    private class RadioButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (model != null && e.getStateChange() == ItemEvent.SELECTED) {

                model.setPolygonAttributes(getPolygonAttributes());
                model.setTransparencyAttributes(
                        opacitySlider.getValue() == opacitySlider.getMaximum()
                        && getPolygonAttributes() == PolygonAttributes.POLYGON_FILL
                                ? TransparencyAttributes.SCREEN_DOOR
                                : getTransparencyAttributes());
            }
        }
    }
}
