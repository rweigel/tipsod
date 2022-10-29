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
 * $Id: ControlPanel.java,v 1.53 2017/10/02 17:07:32 rchimiak Exp $
 *
 * Created on March 12, 2002, 12:59 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.GeoSatBranch;
import gov.nasa.gsfc.spdf.orb.content.HelioSatBranch;
import gov.nasa.gsfc.spdf.orb.content.SelenoSatBranch;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * ControlPanel.java The ControlPanel class implements control spinners in the
 * Satellite Chooser window.
 *
 * @author rchimiak
 * @version $Revision: 1.53 $
 */
public class ControlPanel extends JPanel {

    private JComboBox coordList;
    private static JComboBox bodiesList;
    private JSpinner resolutionSpinner;
    private final JSpinner fromSpinner;
    private final JSpinner toSpinner;
    private final JPanel timeSpin;
    private final JPanel coordSelect;
    private static final JRadioButton tracing = new JRadioButton("Enabled to(km)");
    private static final JTextField limit = new JTextField();
    private static final JRadioButton noTracing = new JRadioButton("Not Enabled", true);
    private static final ButtonGroup group = new ButtonGroup();
    public static boolean isSolenocentric = false;
    public static boolean isImport = false;
    public static boolean keepCamSetting = false;
    private Body oldSelection = null;
    private Body newSelection = null;
    public static final int DEFAULT_RESOLUTION = 1;
    private final Font font;
    private static final SimpleTimeZone UTC_TIME_ZONE
            = new SimpleTimeZone(0, "UTC");
    private static final JCheckBox settingButton = new JCheckBox("keep camera set up");

    public enum Body {

        EARTH, MOON, SUN
    }

    /**
     * Instantiates a controlPanel object, and responds to changes fired by its
     * spinners.
     *
     * @param satTable
     */
    public ControlPanel(final SatelliteGraphPropertiesTable satTable) {

        super();

        setLayout(new BorderLayout());
        setBorder(new EtchedBorder(
                getBackground().darker(), getBackground().brighter()));
        add(Box.createVerticalStrut(2), BorderLayout.NORTH);
        add(Box.createVerticalStrut(2), BorderLayout.SOUTH);
        add(Box.createVerticalStrut(2), BorderLayout.EAST);
        add(Box.createVerticalStrut(2), BorderLayout.WEST);

        JPanel center = new JPanel(new GridLayout(1, 4, 2, 2));

        Font oldfont = tracing.getFont();
        font = new Font(oldfont.getName(), Font.PLAIN, oldfont.getSize());
        coordSelect = new JPanel();

        coordSelect.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(getBackground().brighter(), getBackground().darker()),
                new EmptyBorder(0, 10, 2, 10)));
        coordSelect.setLayout(new GridLayout(2, 1, 0, 2));

        bodiesList = createCentralBodiesList();
        coordList = createCoordinateList();

        center.add(coordSelect);

        timeSpin = new JPanel();
        timeSpin.setLayout(new GridLayout(2, 1, 0, 2));
        timeSpin.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(getBackground().brighter(), getBackground().darker()),
                new EmptyBorder(0, 3, 2, 3)));
        fromSpinner = createTimeSpinner("fr:");
        setDefaultStartDate();
        toSpinner = createTimeSpinner("to:");
        setDefaultEndDate();
        center.add(timeSpin);
        center.add(createTracingPanel());

        JPanel eastPane = new JPanel();
        eastPane.setLayout(new GridLayout(2, 1, 0, 2));
        eastPane.add(createResolutionSpinnerPane());
        eastPane.add(createCameraSettingsPane());

        center.add(eastPane);

        group.add(tracing);

        
        settingButton.putClientProperty("SelectionState", keepCamSetting());
        
        tracing.setFont(font);
        tracing.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                limit.setEnabled(true);
                coordList.setSelectedItem(CoordinateSystem.GEO.value());
            }
        });

        group.add(noTracing);
        noTracing.setFont(font);
        noTracing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                coordList.setSelectedItem(CoordinateSystem.GSE.value());
                limit.setEnabled(false);
            }
        });

        bodiesList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                oldSelection = newSelection; //changes memory address of oldItem.
                newSelection = Body.valueOf(bodiesList.getSelectedItem().toString());

                if (sameAsBranch(newSelection)) {

                    settingButton.setEnabled(true);
                   
                    settingButton.setSelected((Boolean) settingButton.getClientProperty("SelectionState"));

                } else {

                    settingButton.setSelected(false);
                    settingButton.setEnabled(false);
                }

                coordList.removeAllItems();

                try {
                    ((SatelliteGraphTableModel) satTable.getModel()).setData(oldSelection, newSelection);
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

                switch (newSelection) {

                    case EARTH:
                        for (CoordinateSystem cs : CoordinateSystem.values()) {
                            coordList.addItem(cs.value());
                        }
                        setDefaultCoordinateSystem(Body.EARTH.toString());

                        for (Enumeration e = group.getElements(); e.hasMoreElements();) {
                            JRadioButton b = (JRadioButton) e.nextElement();
                            b.setEnabled(true);

                        }
                        if (group.isSelected(tracing.getModel())) {
                            limit.setEnabled(true);
                        }
                        OrbitViewer.getTipsodMenuBar().centralBodiesSelection(Body.EARTH);
                        break;

                    case MOON:
                        coordList.addItem("SSE");
                        setDefaultCoordinateSystem(Body.MOON.toString());

                        for (Enumeration e = group.getElements(); e.hasMoreElements();) {
                            JRadioButton b = (JRadioButton) e.nextElement();
                            b.setEnabled(false);
                        }

                        limit.setEnabled(false);
                        OrbitViewer.getTipsodMenuBar().centralBodiesSelection(Body.MOON);
                        break;

                    case SUN:
                        for (gov.nasa.gsfc.spdf.helio.client.CoordinateSystem cs : gov.nasa.gsfc.spdf.helio.client.CoordinateSystem.values()) {
                            coordList.addItem(cs.value());
                        }
                        setDefaultCoordinateSystem(Body.SUN.toString());

                        for (Enumeration e = group.getElements(); e.hasMoreElements();) {
                            JRadioButton b = (JRadioButton) e.nextElement();
                            b.setEnabled(false);
                        }
                        limit.setEnabled(false);
                        OrbitViewer.getTipsodMenuBar().centralBodiesSelection(Body.SUN);
                        break;
                }

            }
        });
        bodiesList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.DESELECTED && ContentBranch.getSatBranch() != null
                        && sameAsBranch((Body) e.getItem())) {

                    settingButton.putClientProperty("SelectionState", keepCamSetting());
                }

            }
        });

        add(center, BorderLayout.CENTER);
        newSelection = Body.EARTH;
    }

    private Boolean sameAsBranch(Body body) {

        switch (body) {

            case EARTH:
                if (ContentBranch.getSatBranch() instanceof GeoSatBranch) {

                    return true;
                }
                break;
            case MOON:
                if (ContentBranch.getSatBranch() instanceof SelenoSatBranch) {

                    return true;
                }
                break;
            case SUN:
                if (ContentBranch.getSatBranch() instanceof HelioSatBranch) {

                    return true;
                }
                break;

        }
        return false;

    }

    public static Boolean isSolenocentric() {

        if (bodiesList == null) {
            return isSolenocentric;
        }

        return Body.valueOf(bodiesList.getSelectedItem().toString()).compareTo(Body.MOON) == 0;
    }

    public static void setSolenocentric(Boolean value) {

        bodiesList.setSelectedItem(value ? Body.MOON : Body.EARTH);

    }

    private JPanel createCameraSettingsPane() {

        JPanel settingPane = new JPanel();
        settingPane.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(getBackground().brighter(), getBackground().darker()),
                new EmptyBorder(0, 3, 2, 3)));

        settingButton.setFont(Util.labelFont);
        settingButton.setHorizontalAlignment(JCheckBox.LEADING);
        settingButton.setHorizontalTextPosition(JCheckBox.LEADING);

        settingPane.add(settingButton);
        settingButton.setEnabled(false);
        return settingPane;

    }

    /**
     * Creates the widget used to display the list of available coordinates.
     *
     * @return the combo box containing the coordinate system list
     */
    public final JComboBox createCoordinateList() {

        JPanel coordPane = new JPanel();

        coordPane.setLayout(new BoxLayout(coordPane, BoxLayout.Y_AXIS));

        JLabel coordinates = new JLabel("coordinates:");
        coordinates.setFont(Util.labelFont);
        coordinates.setAlignmentX(Component.CENTER_ALIGNMENT);
        coordPane.add(coordinates);

        coordList = new JComboBox();
        for (CoordinateSystem cs : CoordinateSystem.values()) {
            coordList.addItem(cs.value());
        }

        setDefaultCoordinateSystem(Body.EARTH.toString());
        coordList.setFont(font);
        coordPane.add(coordList);
        coordSelect.add(coordPane);
        return coordList;
    }

    private JPanel createTracingPanel() {

        JPanel tracingPane = new JPanel();
        tracingPane.setBorder(new EtchedBorder(getBackground().brighter(),
                getBackground().darker()));
        tracingPane.setLayout(new BoxLayout(tracingPane, BoxLayout.Y_AXIS));
        JLabel tracingLabel = new JLabel("Field-Line Tracing:");
        tracingLabel.setFont(Util.labelFont);
        tracingPane.add(tracingLabel);

        tracingPane.add(tracing);
        JPanel limitPane = new JPanel();
        limit.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        limit.setText("100");
        limit.setColumns(5);
        limit.setEditable(true);
        limit.setBackground(new Color(255, 255, 255));
        limit.setEnabled(false);
        limit.setToolTipText(" default:100 km");
        limit.setMaximumSize(new Dimension(100, 20));

        limitPane.add(limit);
        tracingPane.add(limitPane);

        tracingPane.add(noTracing);
        return tracingPane;

    }

    /**
     * Creates the widget used to display the list of available coordinates.
     *
     * @return the combo box containing the coordinate system list
     */
    public final JComboBox createCentralBodiesList() {

        JPanel bodiesPane = new JPanel();
        bodiesPane.setLayout(new BoxLayout(bodiesPane, BoxLayout.Y_AXIS));

        JLabel centralBodies = new JLabel("central bodies:");
        centralBodies.setFont(Util.labelFont);
        centralBodies.setAlignmentX(Component.CENTER_ALIGNMENT);
        bodiesPane.add(centralBodies);

        bodiesList = new JComboBox(Body.values());

        bodiesList.setSelectedItem(Body.EARTH);
        bodiesList.setFont(font);
        bodiesPane.add(bodiesList);
        coordSelect.add(bodiesPane);
        return bodiesList;
    }

    /**
     * Creates the resolution spinner determining the number of points to send
     * back (1 represents all of the points).
     *
     * @return the resolution spinner, default value of 1(all points)
     */
    public final JPanel createResolutionSpinnerPane() {

        JPanel resPane = new JPanel();
        resPane.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(getBackground().brighter(), getBackground().darker()),
                new EmptyBorder(0, 3, 2, 3)));

        SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);

        JLabel sampling = new JLabel("db sampling:");
        sampling.setFont(Util.labelFont);

        resPane.add(sampling);

        resolutionSpinner = new JSpinner(model);
        resolutionSpinner.setFont(font);
        resolutionSpinner.setPreferredSize(new Dimension(60, 20));

        resPane.add(resolutionSpinner);

        return resPane;
    }

    /**
     * Creates the widget used to display the from and to time selection.The
     * starting time is the current GMT time-24 hours, the ending time is the
     * current GMT time.
     *
     * @param title from or to label in this widget
     * @return the time spinner
     */
    private JSpinner createTimeSpinner(String title) {

        JPanel timePane = new JPanel();
        timePane.setLayout(new BoxLayout(timePane, BoxLayout.Y_AXIS));

        SpinnerDateModel sdm = new SpinnerDateModel();

        sdm.setCalendarField(Calendar.DAY_OF_WEEK);

        JSpinner spinner = new JSpinner(sdm);

        spinner.setFont(font);

        JSpinner.DateEditor jde = new JSpinner.DateEditor(spinner, "yyyy-MM-dd  HH:mm");
        spinner.setEditor(jde);
        //jde.getFormat().setTimeZone(UTC_TIME_ZONE); 

        JLabel label = new JLabel(title);
        label.setFont(Util.labelFont);
        timePane.add(label);
        timePane.add(spinner);
        timeSpin.add(timePane);

        return spinner;
    }

    public JComboBox getBodiesList() {
        return bodiesList;
    }

    /**
     * Returns the spinner with the requested starting time.
     *
     * @return the spinner with the starting time
     */
    public JSpinner getFromSpinner() {
        return fromSpinner;
    }

    /**
     * Returns the spinner with the requested ending time.
     *
     * @return the spinner with the ending time
     */
    public JSpinner getToSpinner() {
        return toSpinner;
    }

    /**
     * Returns the combo box with the list of coordinate systems
     *
     * @return
     */
    public JComboBox getCoordList() {
        return coordList;
    }

    /**
     * Returns the spinner with the chosen resolution
     *
     * @return
     */
    public JSpinner getResolutionSpinner() {
        return resolutionSpinner;
    }

    public double getFieldLinesStopAltitude() {

        try {

            return Double.parseDouble(limit.getText());
        } catch (NumberFormatException nfe) {

            return 100d;
        }
    }

    public void setFieldLinesStopAltitude(String alt) {

        limit.setText(alt);
    }

    /**
     * Provides the starting date value (UTC TimeZone).
     *
     * @return the start date
     */
    public Date getStartDate() {

        return getDefaultTimeZoneDate(getFromSpinner());
    }

    /**
     * Sets the starting date value.
     *
     * @param value new starting date value (UTC TimeZone)
     */
    public void setStartDate(Date value) {

        getFromSpinner().getModel().setValue(value);
    }

    /**
     * Sets the starting date to its default value.
     */
    public void setDefaultStartDate() {

        //  Calendar rightNow = new GregorianCalendar(UTC_TIME_ZONE);
        Calendar rightNow = new GregorianCalendar();

        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MILLISECOND, 0);

        setStartDate(rightNow.getTime());

        fromSpinner.setValue(fromSpinner.getPreviousValue());

    }

    private Date getDefaultTimeZoneDate(JSpinner spinner) {

        GregorianCalendar utcCal = new GregorianCalendar();
        Date date = (Date) spinner.getValue();

        utcCal.setTime(date);

        GregorianCalendar defaultCal = new GregorianCalendar(UTC_TIME_ZONE);
        // calendar with the default time
        // zone
        defaultCal.set(Calendar.YEAR, utcCal.get(Calendar.YEAR));
        defaultCal.set(Calendar.MONTH, utcCal.get(Calendar.MONTH));
        defaultCal.set(Calendar.DAY_OF_MONTH,
                utcCal.get(Calendar.DAY_OF_MONTH));
        defaultCal.set(Calendar.HOUR_OF_DAY,
                utcCal.get(Calendar.HOUR_OF_DAY));
        defaultCal.set(Calendar.MINUTE,
                utcCal.get(Calendar.MINUTE));
        defaultCal.set(Calendar.SECOND,
                utcCal.get(Calendar.SECOND));
        defaultCal.set(Calendar.MILLISECOND,
                utcCal.get(Calendar.MILLISECOND));

        return defaultCal.getTime();

    }

    /**
     * Provides the ending date value (UTC TimeZone).
     *
     * @return the end date
     */
    public Date getEndDate() {

        return getDefaultTimeZoneDate(getToSpinner());
    }

    /**
     * Sets the ending date value.
     *
     * @param value new starting date value (UTC TimeZone)
     */
    public void setEndDate(Date value) {

        getToSpinner().getModel().setValue(value);

    }

    /**
     * Sets the ending date to its default value.
     */
    public void setDefaultEndDate() {

        // Calendar rightNow = new GregorianCalendar(UTC_TIME_ZONE);
        // current time
        Calendar rightNow = new GregorianCalendar();

        rightNow.set(Calendar.HOUR_OF_DAY, 0);
        rightNow.set(Calendar.MINUTE, 0);
        rightNow.set(Calendar.SECOND, 0);
        rightNow.set(Calendar.MILLISECOND, 0);

        setEndDate(rightNow.getTime());
    }

    /**
     * Provides the resolution value.
     *
     * @return the resolution value
     */
    public int getResolution() {

        return ((SpinnerNumberModel) getResolutionSpinner().getModel()).getNumber().intValue();
    }

    /**
     * Sets the resolution value.
     *
     * @param value new resolution value
     */
    public void setResolution(int value) {

        SpinnerNumberModel resolutionModel
                = (SpinnerNumberModel) getResolutionSpinner().getModel();
        // spinner model for resolution
        // value
        resolutionModel.setValue(value);
    }

    /**
     * Sets the resolution to its default value.
     */
    public void setDefaultResolution() {

        setResolution(DEFAULT_RESOLUTION);
    }

    public static boolean getTracing() {

        return getSelection().equals(tracing);
    }

    private static JRadioButton getSelection() {
        for (Enumeration e = group.getElements(); e.hasMoreElements();) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (b.getModel() == group.getSelection()) {
                return b;
            }
        }
        return null;
    }

    /**
     * Sets the tracing value.
     *
     * @param value new tracing value.
     */
    public static void setTracing(boolean value) {

        group.setSelected(tracing.getModel(), value);
    }

    /**
     * Sets the tracing value to its default value.
     */
    public static void setDefaultTracing() {

        setTracing(false);
    }

    /**
     * Provides the selected Coordinate System .
     *
     * @return the CoordinateSystem value
     */
    public Object getCoordinateSystem() {

        switch (getCentralBody()) {

            case SUN:
                return gov.nasa.gsfc.spdf.helio.client.CoordinateSystem.valueOf(getCoordList().getSelectedItem().toString());

            case MOON:
                return CoordinateSystem.GSE;

            default:

                return CoordinateSystem.fromValue(getCoordList().getSelectedItem().toString());
        }

    }

    public void setCameraSetUpState(boolean state) {

        settingButton.setEnabled(state);
    }

    /**
     * Sets the CoordinateSystem selection.
     *
     * @param value new CoordinateSystem selection
     */
    public void setCoordinateSystem(CoordinateSystem value) {

        getCoordList().setSelectedItem(value.toString());
    }

    /**
     * Sets the CoordinateSystem selection to its default value.
     */
    public void setDefaultCoordinateSystem(String body) {

        switch (Body.valueOf(body)) {

            case EARTH:
                getCoordList().setSelectedItem(CoordinateSystem.GSE.value());
                break;
            case MOON:
                getCoordList().setSelectedItem("SSE");
                break;
            case SUN:
                getCoordList().setSelectedItem(gov.nasa.gsfc.spdf.helio.client.CoordinateSystem.HGI.value());
                break;

        }
    }

    /**
     * Provides the selected Central Body .
     *
     * @return the Central Body value
     */
    public static Body getCentralBody() {

        if (bodiesList == null) {
            return null;
        }

        return (Body) bodiesList.getSelectedItem();
    }

    /**
     * Sets the Central Body selection.
     *
     * @param value new Central Body selection
     */
    public void setCentralBody(Body value) {

        getBodiesList().setSelectedItem(value);
    }

    //  public static JCheckBox getSettingButton() {
    //      return settingButton;
    //  }
    public static Boolean keepCamSetting() {

        return settingButton.isSelected();
    }
}
