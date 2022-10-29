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
 * $Id: ToolPanel.java,v 1.8 2015/10/30 14:18:50 rchimiak Exp $
 * Created on March 15, 2007, 10:24 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.media.j3d.View;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author rachimiak
 */
public class ToolPanel extends JPanel {

    private JRadioButton parallel
            = new JRadioButton("parallel", true);
    private JRadioButton perspective
            = new JRadioButton("perspective", false);
    private ButtonGroup projectionGroup
            = new ButtonGroup();
    private JRadioButton[] radioButtons
            = new JRadioButton[3];
    private ButtonGroup radioGroup = new ButtonGroup();

    /**
     * Creates a new instance of ToolPane.l
     */
    public ToolPanel() {

        JPanel main = new JPanel();
        main.setLayout(new GridLayout(1, 2));

        Font oldfont = parallel.getFont();
        Font font = new Font(oldfont.getName(), Font.PLAIN, oldfont.getSize());

        JPanel projectionPane = new JPanel();
        projectionPane.setLayout(new BoxLayout(projectionPane, BoxLayout.X_AXIS));

        parallel.setFont(font);
        perspective.setFont(font);

        JLabel projectionTitle = new JLabel("Projection:");
        projectionTitle.setFont(Util.labelFont);

        projectionPane.add(projectionTitle);
        projectionPane.add(parallel);
        projectionPane.add(perspective);

        OrbitViewer.getTipsodMenuBar().setProjection(View.PARALLEL_PROJECTION);

        class ProjectionListener implements ItemListener {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (perspective.isSelected()) {

                    OrbitViewer.getTipsodMenuBar().setProjection(View.PERSPECTIVE_PROJECTION);
                    OrbitViewer.getTipsodMenuBar().resetView();
                } else if (parallel.isSelected()) {

                    OrbitViewer.getTipsodMenuBar().setProjection(View.PARALLEL_PROJECTION);
                    OrbitViewer.getTipsodMenuBar().resetView();
                }
            }
        }
        projectionGroup.add(parallel);
        projectionGroup.add(perspective);
        parallel.addItemListener(new ProjectionListener());
        perspective.addItemListener(new ProjectionListener());

        JPanel orientationPane = new JPanel();
        orientationPane.setLayout(new BoxLayout(orientationPane, BoxLayout.X_AXIS));

        JLabel orientationTitle = new JLabel("  Views:");
        orientationTitle.setFont(Util.labelFont);

        orientationPane.add(orientationTitle);

        radioButtons[0] = new JRadioButton("xy View", true);
        radioButtons[1] = new JRadioButton("yz View", false);
        radioButtons[2] = new JRadioButton("xz View", false);

        OrbitViewer.getTipsodMenuBar().getRadioButton(0).setSelected(true);

        class RadioButtonListener implements ItemListener {

            @Override
            public void itemStateChanged(ItemEvent e) {

                for (int i = 0; i < radioButtons.length; i++) {

                    if (radioButtons[i].isSelected()) {
                        OrbitViewer.getTipsodMenuBar().getRadioButton(i).setSelected(true);
                        OrbitViewer.getTipsodMenuBar().resetView();
                        return;
                    }
                }
            }
        }
        for (JRadioButton radioButton : radioButtons) {
            radioGroup.add(radioButton);
            radioButton.addItemListener(new RadioButtonListener());
            radioButton.setFont(font);
            orientationPane.add(radioButton);
        }
        main.add(projectionPane);
        main.add(orientationPane);

        add(main);
    }
}
