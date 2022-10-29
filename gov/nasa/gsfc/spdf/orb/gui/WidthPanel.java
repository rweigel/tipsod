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
 * $Id: WidthPanel.java,v 1.14 2017/03/06 20:05:00 rchimiak Exp $
 * Created on April 2, 2007, 1:32 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import javax.swing.border.TitledBorder;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author rachimiak
 */
public class WidthPanel extends JPanel {

    private final JSpinner satelliteWidthSpinner;
    private final JSpinner coordinateWidthSpinner;
    private final JSpinner tickMarkWidthSpinner;
    private final JSpinner lineWidthSpinner;
    private final JSpinner axisWidthSpinner;
    private static final SpinnerModel satelliteWidthModel = new SpinnerNumberModel(100, 1, 300, 1);
    private static final SpinnerModel coordinateWidthModel = new SpinnerNumberModel(100, 1, 300, 1);
    private static final SpinnerModel tickMarkWidthModel = new SpinnerNumberModel(100, 1, 300, 1);
    private static final SpinnerModel lineWidthModel = new SpinnerNumberModel(2, 0, 10, 1);
    private static final SpinnerModel axisWidthModel = new SpinnerNumberModel(2, 1, 10, 1);

    JPanel cards;
    final static String SYMBOLPANEL = " Symbol";
    final static String COORDINATEPANEL = " Coordinates";
    final static String TICKMARKPANEL = " Tick Marks";
    final static String ORBITPANEL = " Orbit";
    final static String AXISPANEL = " Axis ans Grid";

    /**
     * Creates a new instance of WidthPanel.
     */
    public WidthPanel() {

        TitledBorder sizeTitle = BorderFactory.createTitledBorder("Thickness");
        setBorder(sizeTitle);

        sizeTitle.setTitleFont(Util.labelFont);
        
        setLayout(new GridLayout(2, 1));
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        String comboBoxItems[] = {SYMBOLPANEL, COORDINATEPANEL, TICKMARKPANEL, ORBITPANEL, AXISPANEL};
        JComboBox cb = new JComboBox(comboBoxItems);
        cb.setEditable(false);
        cb.setFont(Util.plainFont);
        cb.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent evt) {

                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, (String) evt.getItem());

            }
        }
        );
        comboBoxPane.add(cb);

        cards = new JPanel(new CardLayout());

        JPanel satelliteWidthPane = new JPanel();

        satelliteWidthSpinner = new JSpinner(satelliteWidthModel);
        satelliteWidthSpinner.setPreferredSize(new Dimension(80, 23));
        satelliteWidthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().symbolWidthChanged(getSatWidth());
                }
            }
        });
        satelliteWidthPane.add(satelliteWidthSpinner);

        JPanel coordinateWidthPane = new JPanel();

        coordinateWidthSpinner = new JSpinner(coordinateWidthModel);
        coordinateWidthSpinner.setPreferredSize(new Dimension(80, 23));
        coordinateWidthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().coordinateWidthChanged(getCoordinateWidth());
                }
            }
        });
        coordinateWidthPane.add(coordinateWidthSpinner);

        JPanel tickMarkWidthPane = new JPanel();

        tickMarkWidthSpinner = new JSpinner(tickMarkWidthModel);
        tickMarkWidthSpinner.setPreferredSize(new Dimension(80, 23));
        tickMarkWidthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().tickMarkWidthChanged(getTickWidth());
                }
            }
        });
        tickMarkWidthPane.add(tickMarkWidthSpinner);

        JPanel lineWidthPane = new JPanel();
        lineWidthSpinner = new JSpinner(lineWidthModel);
        lineWidthSpinner.setPreferredSize(new Dimension(80, 23));
        lineWidthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().lineWidthChanged(getLineWidth());
                }

            }
        });
        lineWidthPane.add(lineWidthSpinner);

        JPanel axisWidthPane = new JPanel();

        axisWidthSpinner = new JSpinner(axisWidthModel);
        axisWidthSpinner.setPreferredSize(new Dimension(80, 23));
        axisWidthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().axisWidthChanged(getAxisLineWidth());
                }
            }
        });
        axisWidthPane.add(axisWidthSpinner);

        cards.add(satelliteWidthPane, SYMBOLPANEL);
        cards.add(coordinateWidthPane, COORDINATEPANEL);
        cards.add(tickMarkWidthPane, TICKMARKPANEL);
        cards.add(lineWidthPane, ORBITPANEL);
        cards.add(axisWidthPane, AXISPANEL);
        cards.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.add(comboBoxPane);
        this.add(cards);
    }

    public float getSatWidth() {
        return (((SpinnerNumberModel) satelliteWidthSpinner.getModel()).getNumber().floatValue() / 100);
    }

    public float getCoordinateWidth() {
        return (((SpinnerNumberModel) coordinateWidthSpinner.getModel()).getNumber().floatValue() / 100);
    }

    public float getTickWidth() {
        return (((SpinnerNumberModel) tickMarkWidthSpinner.getModel()).getNumber().floatValue() / 100);
    }

    public float getLineWidth() {
        return (((SpinnerNumberModel) lineWidthSpinner.getModel()).getNumber().floatValue());
    }

    public float getAxisLineWidth() {
        return (((SpinnerNumberModel) axisWidthSpinner.getModel()).getNumber().floatValue());
    }

    public SpinnerModel getSatWidthSpinnerModel() {
        return satelliteWidthModel;
    }

    public SpinnerModel getCoordinateWidthSpinnerModel() {
        return coordinateWidthModel;
    }

    public SpinnerModel getTickWidthSpinnerModel() {
        return tickMarkWidthModel;
    }

    public SpinnerModel getlineWidthSpinnerModel() {
        return lineWidthModel;
    }

    public SpinnerModel getAxisWidthSpinnerModel() {
        return axisWidthModel;
    }
}
