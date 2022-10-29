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
 * $Id: InfoPanel.java,v 1.31 2016/09/01 20:50:25 rchimiak Exp $
 *
 * Created on May 6, 2002, 10:31 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The InfoPanel class displays the scale text field.
 *
 * @author rchimiak
 * @version $Revision: 1.31 $
 */
public class InfoPanel extends JPanel {

    private final JSlider spanLogSlider;
    //   private final JLabel logVal = new JLabel("Value is: 1");
    //private TitledBorder spanTitle ;
    final JSpinner spinner = new JSpinner();
    final JSpinner majorSpinner = new JSpinner();
    JPanel span;
    private final JSlider majorSlider;
    private WidthPanel widthPane = null;

    /**
     * Creates an InfoPanel instance to display the scale for the scene
     */
    public InfoPanel() {

        super();

        setLayout(new BorderLayout());

        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(2, 1, 5, 5));

        widthPane = new WidthPanel();

        add(widthPane, BorderLayout.NORTH);

        span = new JPanel(new BorderLayout());
        spanLogSlider = getLogSlider();

        setSpanBorder("Axis Span (" + (ControlPanel.isSolenocentric()
                ? "RM" : "RE") + ")");

        JPanel spin = new JPanel();
        spin.setBorder(new EmptyBorder(5, 10, 5, 10));
        spin.add(spinner);
        span.add(spin, BorderLayout.NORTH);
        span.add(spanLogSlider);

        spinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        spinner.setPreferredSize(new Dimension(80, 23));
        spinner.setFont(Util.plainFont);
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();
                spanLogSlider.setValue((Integer) s.getValue());
            }
        });

        panel.add(span);

        JPanel major = new JPanel(new BorderLayout());
        majorSlider = getMajorSlider();
        TitledBorder majorTitle = BorderFactory.createTitledBorder("Major Ticks");
        majorTitle.setTitleFont(Util.labelFont);
        major.setBorder(majorTitle);
        major.add(majorSlider);

        JPanel majorSpin = new JPanel();
        majorSpin.setBorder(new EmptyBorder(5, 10, 5, 10));
        majorSpin.add(majorSpinner);
        major.add(majorSpin, BorderLayout.NORTH);
        major.add(majorSlider);

        majorSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        majorSpinner.setPreferredSize(new Dimension(80, 23));
        majorSpinner.setFont(Util.plainFont);
        majorSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();
                majorSlider.setValue((Integer) s.getValue());
            }
        });

        panel.add(major);

      //  panel.add(major);
        add(panel);

        JPanel legend = new JPanel();
        JLabel text = new JLabel("<html><font style color= #FF0000  size = +1><I>----</I> x</font>&nbsp;&nbsp;&nbsp; <font style color= #00CC00 size = +1><I>----</I> y</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br><font style color= #0000FF size = +1 ><I>----</I> z</font><br></html>");
        legend.add(text);

        TitledBorder legendTitle = BorderFactory.createTitledBorder("Key");
        legendTitle.setTitleFont(Util.labelFont);
        legend.setBorder(legendTitle);
        add(legend, BorderLayout.SOUTH);
    }

    public final JSlider getMajorSlider() {
        final JSlider slider = new LogarithmicJSlider(JSlider.VERTICAL, 1, 100, 1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
       

        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent event) {

                majorSpinner.setValue((int) slider.getValue());
                if (ContentBranch.getSatBranch() != null && !slider.getModel().getValueIsAdjusting()) {
                    ContentBranch.getSatBranch().addMajorTicks(slider.getValue());
                }
            }
        });

        return slider;
    }


    public final JSlider getLogSlider() {

        final JSlider slider = new LogarithmicJSlider(JSlider.VERTICAL, 1, 1000, 1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(10);

        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                //               logVal.setText("Value is: " + (double) slider.getValue());
                spinner.setValue((int) slider.getValue());
                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().scaleAxis((double) slider.getValue());

                }
            }
        });
        return slider;
    }

    public final void setSpanBorder(String title) {

        TitledBorder spanTitle = BorderFactory.createTitledBorder(title);

        spanTitle.setTitleFont(Util.labelFont);

        span.setBorder(spanTitle);

    }

    public float getTickWidth() {
        return widthPane.getTickWidth();
    }

    public float getCoordinateWidth() {
        return widthPane.getCoordinateWidth();
    }

    public float getLineWidth() {
        return widthPane.getLineWidth();
    }

    public float getAxisLineWidth() {
        return widthPane.getAxisLineWidth();
    }

    public float getSatWidth() {
        return widthPane.getSatWidth();
    }

    public SpinnerModel getSatWidthSpinnerModel() {
        return widthPane.getSatWidthSpinnerModel();
    }

    public SpinnerModel getlineWidthSpinnerModel() {
        return widthPane.getlineWidthSpinnerModel();
    }

    public SpinnerModel getAxisWidthSpinnerModel() {
        return widthPane.getAxisWidthSpinnerModel();
    }

    public int getTicksCount() {
        return majorSlider.getValue();
    }

    public void setTicksCount(int count) {
        majorSlider.setValue(count);
    }

    public int getAxisSpanValue() {
        return spanLogSlider.getValue();
    }

    public void setAxisSpanValue(double value) {
        spanLogSlider.setValue((int) Math.ceil(value));
    }
}
