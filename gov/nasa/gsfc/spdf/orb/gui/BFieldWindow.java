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
 * $Id: BFieldWindow.java,v 1.9 2015/10/30 14:18:50 rchimiak Exp $
 * Created on June 18, 2007, 10:05 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.BFieldModelOptions;
import gov.nasa.gsfc.spdf.ssc.client.BFieldModelParameters;
import gov.nasa.gsfc.spdf.ssc.client.ExternalBFieldModel;
import gov.nasa.gsfc.spdf.ssc.client.InternalBFieldModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.ButtonModel;

/**
 *
 * @author rachimiak
 */
public class BFieldWindow extends JFrame implements SwpChangeListener {

    protected JRadioButton[] internalRadioButtons = new JRadioButton[2];
    protected JRadioButton[] externalRadioButtons = new JRadioButton[3];
    protected JTextField swp = new JTextField();
    protected JTextField kp = new JTextField();
    protected JTextField bx = new JTextField();
    protected JTextField by = new JTextField();
    protected JTextField bz = new JTextField();
    protected JTextField dst = new JTextField();
    //  protected JTextField limit = new JTextField();
    protected ButtonGroup internalRadioGroup = new ButtonGroup();
    protected ButtonGroup externalRadioGroup = new ButtonGroup();
    protected HashMap<String, ButtonModel> buttonMap = new HashMap<String, ButtonModel>();
    protected HashMap<String, String> textMap = new HashMap<String, String>();

    /**
     * Creates a new instance of BFieldWindow
     */
    public BFieldWindow() {

        super();

        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1, 10, 10));

        // contentPane.setLayout(new GridLayout(2, 1, 10, 10));
        this.setTitle("Magnetic Field");
        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(scrnSize.width / 15, scrnSize.height / 15);

        TitledBorder bFieldTitle = BorderFactory.createTitledBorder("B Field Model");
        bFieldTitle.setTitleFont(Util.labelFont);
        JPanel bFieldModel = new JPanel(new GridLayout(1, 2, 3, 3));
        bFieldModel.setBorder(bFieldTitle);

        JLabel internal = new JLabel("Internal: ");
        TitledBorder internalTitle = BorderFactory.createTitledBorder("Internal");
        internalTitle.setTitleFont(Util.modelFont);

        Font oldfont = internal.getFont();
        Font font = new Font(oldfont.getName(), Font.PLAIN, oldfont.getSize());

        JPanel internalRadioPanel = new JPanel(new GridLayout(2, 1));
        internalRadioPanel.setBorder(internalTitle);
        internalRadioButtons[0] = new JRadioButton("IGRF", true);
        internalRadioButtons[1] = new JRadioButton("Dipole", false);

        for (JRadioButton internalRadioButton : internalRadioButtons) {
            internalRadioGroup.add(internalRadioButton);
            internalRadioPanel.add(internalRadioButton);
            internalRadioButton.setFont(font);
            internalRadioButton.addItemListener(new RadioButtonListener());
        }
        JPanel externalRadioPanel = new JPanel(new GridLayout(3, 1));

        TitledBorder externalTitle = BorderFactory.createTitledBorder("External");
        externalRadioPanel.setBorder(externalTitle);
        externalTitle.setTitleFont(Util.modelFont);

        externalRadioButtons[0] = new JRadioButton("Tsyganenko 96", false);
        externalRadioButtons[1] = new JRadioButton("Tsyganenko 89c", true);
        externalRadioButtons[2] = new JRadioButton("Tsyganenko 87", false);

        for (JRadioButton externalRadioButton : externalRadioButtons) {
            externalRadioGroup.add(externalRadioButton);
            externalRadioPanel.add(externalRadioButton);
            externalRadioButton.setFont(font);
            externalRadioButton.addItemListener(new RadioButtonListener());
        }
        bFieldModel.add(internalRadioPanel);
        bFieldModel.add(externalRadioPanel);
        // contentPane.add(bFieldModel);
        mainPanel.add(bFieldModel);

        TitledBorder bFieldParamTitle = BorderFactory.createTitledBorder("B Field Model Parameters");
        bFieldParamTitle.setTitleFont(Util.labelFont);

        JPanel params = new JPanel(new GridLayout(5, 2, 3, 4));
        params.setBorder(bFieldParamTitle);

        JPanel KPPanel = new JPanel(new GridLayout(1, 2));

        JLabel KPLabel = new JLabel("Kp Index:");
        KPLabel.setFont(font);
        KPPanel.add(KPLabel);

        kp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));

        kp.setText("3");
        kp.setColumns(5);
        kp.setEditable(true);
        kp.setEnabled(true);
        kp.setToolTipText("range: [0, 6], default:3");

        kp.setBackground(new Color(255, 255, 255));
        KPPanel.add(kp);

        params.add(KPPanel);

        JPanel DSTPanel = new JPanel(new GridLayout(1, 2));

        JLabel DSTLabel = new JLabel("DST Index:");
        DSTLabel.setFont(font);
        DSTPanel.add(DSTLabel);

        dst.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        dst.setText("-20");
        dst.setColumns(5);
        dst.setEditable(true);
        dst.setBackground(new Color(255, 255, 255));
        dst.setEnabled(false);
        dst.setToolTipText("range: [-400 nT, 200 nT], default:-20 nT");
        DSTPanel.add(dst);
        params.add(DSTPanel);

        JPanel SWPPanel = new JPanel(new GridLayout(1, 2));

        JLabel swpLabel = new JLabel("SWP (nP):");
        swpLabel.setFont(font);
        SWPPanel.add(swpLabel);

        swp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        swp.setText("2.04");
        swp.setColumns(5);
        swp.setEditable(true);
        swp.setBackground(new Color(255, 255, 255));
        swp.setToolTipText("range: [0 nPa, 30 nPa], default:2.04 nPa");
        SWPPanel.add(swp);
        swp.setEnabled(false);
        params.add(SWPPanel);

        JPanel IMFPanel = new JPanel(new GridLayout(1, 2));

        JLabel IMFLabel = new JLabel("IMF (nT) :");
        IMFLabel.setFont(font);
        IMFPanel.add(IMFLabel);

        JPanel bPanel = new JPanel(new GridLayout(1, 3));

        bx = new JTextField();
        bx.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        bx.setText("0.0");
        bx.setColumns(3);
        bx.setEditable(true);
        bx.setBackground(new Color(255, 255, 255));
        bx.setEnabled(false);
        bx.setToolTipText("range: [-100 nT, 100 nT], default:0 nT");
        bPanel.add(bx);

        by = new JTextField();
        by.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        by.setText("0.0");
        by.setColumns(3);
        by.setEditable(true);
        by.setBackground(new Color(255, 255, 255));
        by.setEnabled(false);
        by.setToolTipText("range: [-100 nT, 100 nT], default:0 nT");
        bPanel.add(by);

        bz = new JTextField();
        bz.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        bz.setText("0.0");
        bz.setColumns(3);
        bz.setEditable(true);
        bz.setBackground(new Color(255, 255, 255));
        bz.setEnabled(false);
        bz.setToolTipText("range: [-100 nT, 100 nT], default:0 nT");
        bPanel.add(bz);

        IMFPanel.add(bPanel);
        params.add(IMFPanel);

        /*   JPanel limitPanel = new JPanel(new GridLayout(1, 2));

         JLabel limtLabel = new JLabel("Stop At (km):");
         limtLabel.setFont(font);
         limitPanel.add(limtLabel);

         limit.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
         limit.setText("100");
         limit.setColumns(5);
         limit.setEditable(true);
         limit.setBackground(new Color(255, 255, 255));
         limit.setEnabled(true);
         limit.setToolTipText(" default:100 km");
         limitPanel.add(limit);
         params.add(limitPanel);
         //  contentPane.add(params);*/
        mainPanel.add(params);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionButtonListener());
        //
        final JButton setButton = new JButton("Apply");
        setButton.setActionCommand("Apply");
        setButton.addActionListener(new ActionButtonListener());
        getRootPane().setDefaultButton(setButton);
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(setButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(cancelButton);

        //Put everything together, using the content pane's BorderLayout.
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        pack();
        setVisible(false);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                resetToInitialValues();

            }
        });
        boolean add = swpChangeListeners.add(this);
    }

    public BFieldModelOptions getBFieldModelOptions() {

        BFieldModelOptions bFieldModelOptions
                = new BFieldModelOptions();

        bFieldModelOptions.setInternalModel(
                getInternalBFieldModel());

        // bFieldModelOptions.setFieldLinesStopAltitude(getFieldLinesStopAltitude());
        bFieldModelOptions.setExternalModel(getBFieldModelParameters());

        return bFieldModelOptions;

    }

    private BFieldModelParameters getBFieldModelParameters() {

        BFieldModelParameters bFieldModelParams
                = new BFieldModelParameters();
        bFieldModelParams.setUseFixedValues(true);

        bFieldModelParams.setModel(getExternalBFieldModel());
        bFieldModelParams.setSolarWindPressure(getSolarWindPressure());

        if (kp.isEnabled()) {
            bFieldModelParams.setParameterValues(getKpValue());
        }

        if (dst.isEnabled()) {
            bFieldModelParams.setDst(getDst());
        }

        if (by.isEnabled()) {
            bFieldModelParams.setByImf(getBy());
        }

        if (bz.isEnabled()) {
            bFieldModelParams.setBzImf(getBz());
        }

        return bFieldModelParams;

    }

    @Override
    public void swpChanged(String newSwp) {

        swp.setText(newSwp);
    }

    private InternalBFieldModel getInternalBFieldModel() {

        for (Enumeration e = internalRadioGroup.getElements(); e.hasMoreElements();) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (b.getModel() == internalRadioGroup.getSelection()) {
                return b.getText().equalsIgnoreCase("IGRF") ? InternalBFieldModel.IGRF : InternalBFieldModel.SIMPLE_DIPOLE;
            }
        }
        return InternalBFieldModel.IGRF;
    }

    /*   private double getFieldLinesStopAltitude() {

     try {

     return Double.parseDouble(limit.getText());
     } catch (NumberFormatException nfe) {

     return 100d;
     }
     }*/
    private float getSolarWindPressure() {

        try {

            return Float.parseFloat(swp.getText());
        } catch (NumberFormatException nfe) {

            return 2.04f;
        }
    }

    private short getKpValue() {

        try {

            double d = Math.floor(Double.parseDouble(kp.getText()));

            if (getExternalBFieldModel().value().equalsIgnoreCase("T87")) {

                return d < 5 ? (short) d : (short) 5;
            } else if (getExternalBFieldModel().value().equalsIgnoreCase("T89c")) {

                return d < 6 ? (short) d : (short) 6;
            } else {
                return (short) 3;
            }

        } catch (NumberFormatException nfe) {

            return (short) 3;
        }
    }

    private int getDst() {

        try {

            return Integer.parseInt(dst.getText());
        } catch (NumberFormatException nfe) {

            return -20;
        }
    }

    private float getBy() {

        try {

            float byValue = Float.parseFloat(by.getText());

            return (byValue < 100f || byValue > -100f) ? byValue : 0f;

        } catch (NumberFormatException nfe) {

            return 0f;
        }
    }

    private float getBz() {

        try {

            float bzValue = Float.parseFloat(bz.getText());

            return (bzValue < 100f || bzValue > -100f) ? bzValue : 0f;

        } catch (NumberFormatException nfe) {

            return 0f;
        }
    }

    private ExternalBFieldModel getExternalBFieldModel() {

        for (Enumeration e = externalRadioGroup.getElements(); e.hasMoreElements();) {
            JRadioButton b = (JRadioButton) e.nextElement();

            if (b.getModel() == externalRadioGroup.getSelection()) {
                return b.getText().contains("87") ? ExternalBFieldModel.T_87
                        : b.getText().contains("89") ? ExternalBFieldModel.T_89_C
                                : ExternalBFieldModel.T_96;

            }
        }
        return ExternalBFieldModel.T_89_C;

    }

    public void saveInitialValues() {

        buttonMap.put("internal", internalRadioGroup.getSelection());
        buttonMap.put("external", externalRadioGroup.getSelection());

        textMap.put("swp", swp.getText());
        textMap.put("kp", kp.getText());
        textMap.put("bx", bx.getText());
        textMap.put("by", by.getText());
        textMap.put("bz", bz.getText());
        textMap.put("dst", dst.getText());
        //  textMap.put("limit", limit.getText());

    }

    private void resetToInitialValues() {

        internalRadioGroup.setSelected((buttonMap.get("internal")), true);
        externalRadioGroup.setSelected((buttonMap.get("external")), true);
        swp.setText(textMap.get("swp"));
        kp.setText(textMap.get("kp"));
        bx.setText(textMap.get("bx"));
        by.setText(textMap.get("by"));
        bz.setText(textMap.get("bz"));
        dst.setText(textMap.get("dst"));
        //   limit.setText(textMap.get("limit").toString());
    }

    private class RadioButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {

                if (((JRadioButton) e.getItem()).equals(externalRadioButtons[0])) {

                    dst.setEnabled(true);
                    by.setEnabled(true);
                    bz.setEnabled(true);
                    kp.setEnabled(false);
                    swp.setEnabled(true);
                } else if (((JRadioButton) e.getItem()).equals(externalRadioButtons[1])
                        || ((JRadioButton) e.getItem()).equals(externalRadioButtons[2])) {

                    dst.setEnabled(false);
                    by.setEnabled(false);
                    bz.setEnabled(false);
                    kp.setEnabled(true);
                    swp.setEnabled(false);

                    kp.setToolTipText(((JRadioButton) e.getItem()).equals(externalRadioButtons[2])
                            ? "range: [0, 5], default:3"
                            : "range: [0, 6], default:3");
                }
            }
        }
    }

    private class ActionButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if ("Apply".equals(e.getActionCommand())) {

                if (OrbitViewer.getSatelliteChooser().getSatModel().getSelectedSatelliteCount() > 0
                        && ControlPanel.getTracing()) {

                    OrbitViewer.getSatelliteChooser().performGraphAction();
                }

                Vector listeners = (Vector) swpChangeListeners.clone();
                // a copy of the listeners

                SwpChangeListener listener;
                // a specific listener

                for (int i = 0; i < listeners.size(); i++) {

                    listener = (SwpChangeListener) listeners.elementAt(i);
                    listener.swpChanged(swp.getText());
                }

            } else {
                resetToInitialValues();
            }
            BFieldWindow.this.setVisible(false);
        }
    }
}
