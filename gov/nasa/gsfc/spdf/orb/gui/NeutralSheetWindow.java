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
 * $Id: NeutralSheetWindow.java,v 1.10 2015/10/30 14:18:50 rchimiak Exp $
 * Created on June 27, 2006, 4:27 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color4f;

/**
 *
 * @author rchimiak
 */
public class NeutralSheetWindow extends SurfaceWindow {

    /**
     * Creates a new instance of NeutralSheetWindow.
     */
    public NeutralSheetWindow() {

        this.setTitle("Neutral Sheet");
        color = new Color4f(0.345f, 0.290f, 0.874f, 1f);
        radioButtons[2].setSelected(true);
        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(scrnSize.width / 15, scrnSize.height / 15);
        opacitySlider.setValue(50);
        colorButton.setBackground(color.get());

        opacitySlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (model != null) {
                    model.setOpacity(getOpacity());
                }
            }
        });
        radioButtons[0].addItemListener(new RadioButtonListener());
        radioButtons[1].addItemListener(new RadioButtonListener());
        radioButtons[2].addItemListener(new RadioButtonListener());
    }

    private class RadioButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (model != null && e.getStateChange() == ItemEvent.SELECTED) {

                model.setPolygonAttributes(getPolygonAttributes());
                model.setTransparencyAttributes(getTransparencyAttributes());
            }
        }
    }
}
