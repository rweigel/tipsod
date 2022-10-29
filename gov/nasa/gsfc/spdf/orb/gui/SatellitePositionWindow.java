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
 * $Id: SatellitePositionWindow.java,v 1.31 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on October 22, 2002, 9:51 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.utils.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableModel;
import javax.vecmath.Point3f;

public abstract class SatellitePositionWindow extends JFrame {

    protected SatellitePositionTable table = null;

    public abstract interface Column {

        int SATELLITE = 0,
                COLOR = 1,
                X = 2,
                Y = 3,
                Z = 4,
                RADIUS = 5,
                LATITUDE = 6,
                LONGITUDE = 7,
                MAGNETOPAUSE = 8,
                BOWSHOCK = 9,
                NEUTRAL_SHEET = 10,
                NFLAT = 11,
                NFLON = 12,
                SFLAT = 13,
                SFLON = 14,
                CFLAT = 15,
                CFLON = 16;
    }

    /**
     * Creates a new instance of SatellitePositionWindow
     */
    public SatellitePositionWindow() {
        super("Position");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        table = new SatellitePositionTable(new SatellitePositionTableModel());

        JScrollPane scrollpane = new JScrollPane(table);

        contentPane.add(scrollpane, BorderLayout.CENTER);

        setVisible(false);

    }

    /**
     * Returns the table to display the satellites positions.
     *
     * @return the satellitePositionTable
     */
    public SatellitePositionTable getTable() {

        return table;
    }

    /**
     * Returns the JFormattedTextField containing the time corresponding to
     * position of the satellite on its orbit at that moment.
     *
     * @return the time field
     */
    public abstract JFormattedTextField getTimeField();

    public abstract ArrayList<Integer> getSelectedCheckboxes();

    /**
     * Returns the JTextField containing the user selected coordinate system
     * currently being represented.
     *
     * @return the coordinate display field
     */
    public abstract JTextField getCoordinateField();

    public abstract TitledBorder getCoordBorder();

    public abstract void clearCheckBoxes();

    public abstract void resetCaptureButton();

    public abstract void setProgressNote(String value);

    public abstract void setProgressFinished();

    public abstract void addDataCaptureListener(DataCaptureListener listener);

    public abstract void removeDataCaptureListener(DataCaptureListener listener);

    public abstract void SWPChanged();

    public abstract void updatePosition(Point3f position, int order);

    public void updateTime(Date date) {

        getTimeField().setValue(date);

    }

    public void clear() {

        getTable().setModel(new DefaultTableModel());
        getTimeField().setText("");
        getCoordinateField().setText("");
    }

    /**
     * The InfoPanel class implements the top portion of the position window. It
     * contains a field to display time and a field to display the coordinate
     * system.
     */
    protected abstract class InfoPanel extends JPanel implements ItemListener {

        protected JFormattedTextField time;
        protected JTextField coordinates;
        protected TitledBorder coordBorder;
        protected JCheckBox cartCheck = new JCheckBox("Cartesian");
        protected JCheckBox spherCheck = new JCheckBox("Spherical");
        protected JPanel coordinatesPanel = new JPanel();

        // private  final Font plain = new Font("Comic Sans MS",Font.PLAIN,12);
        /**
         * Create an instance of the top panel of the position window
         */
        public InfoPanel() {

            super();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            //********************Time panel********************
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            df.setTimeZone(Util.UTC_TIME_ZONE);
            time = new JFormattedTextField(df);
            time.setFocusable(false);
            time.setPreferredSize(new Dimension(100, 20));
            time.setEditable(false);
            time.setBackground(new Color(255, 255, 255));
            JPanel timePanel = new JPanel();

            timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
            TitledBorder timeTitle = BorderFactory.createTitledBorder("time:");
            timeTitle.setTitleFont(Util.labelFont);
            timePanel.setBorder(timeTitle);
            timePanel.add(time);
            add(timePanel);
            add(Box.createVerticalStrut(12));

            //********************Coordinate System panel********************
            coordinates = new JTextField();

            coordinates.setEditable(false);
            coordinates.setBackground(new Color(255, 255, 255));

            JPanel coordinatePanel = new JPanel();

            coordinatePanel.setLayout(new BoxLayout(coordinatePanel, BoxLayout.Y_AXIS));

            TitledBorder coordinateTitle = BorderFactory.createTitledBorder("coordinate system:");
            coordinateTitle.setTitleFont(Util.labelFont);

            coordinatePanel.setBorder(coordinateTitle);
            coordinatePanel.add(coordinates);
            add(coordinatePanel);

            add(Box.createVerticalStrut(12));

            //********************Coordinate Display panel********************
            cartCheck.setFont(Util.comicPlain12Font);
            cartCheck.setSelected(true);

            spherCheck.setFont(Util.comicPlain12Font);

            cartCheck.addItemListener(this);
            spherCheck.addItemListener(this);

            coordinatesPanel.setLayout(new BoxLayout(coordinatesPanel, BoxLayout.Y_AXIS));
            coordinatesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel pa = new JPanel();
            pa.setLayout(new BoxLayout(pa, BoxLayout.X_AXIS));
            pa.setAlignmentX(Component.LEFT_ALIGNMENT);
            pa.add(cartCheck);
            pa.add(Box.createHorizontalStrut(60));
            coordinatesPanel.add(pa);

            JPanel pan = new JPanel();
            pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
            pan.setAlignmentX(Component.LEFT_ALIGNMENT);
            pan.add(spherCheck);
            pan.add(Box.createHorizontalStrut(60));
            coordinatesPanel.add(pan);
            coordinatesPanel.add(pan);

            coordBorder = BorderFactory.createTitledBorder("coordinates (" + (ControlPanel.isSolenocentric() ? "RM" : "RE") + "/\u00B0):");
            coordBorder.setTitleFont(Util.labelFont);

            coordinatesPanel.setBorder(coordBorder);

        }

        public void clearCheckBoxes() {

            if (!cartCheck.isSelected()) {

                getTable().removeColumns("X");
                getTable().removeColumns("Y");
                getTable().removeColumns("Z");
            }

            if (!spherCheck.isSelected()) {

                getTable().removeColumns("Radius");
                getTable().removeColumns("Latitude");
                getTable().removeColumns("Longitude");
            }

        }

        /**
         * Listens to the check boxes.
         *
         * @param e
         */
        @Override
        public abstract void itemStateChanged(ItemEvent e);

        /**
         * @return the JFormattedTextField displaying the time
         */
        public JFormattedTextField getTimeField() {
            return time;
        }

        /**
         * @return the JTextField displaying the coordinate system being used
         * for the given coordinates.
         */
        public JTextField getCoordinateField() {
            return coordinates;
        }

        public TitledBorder getCoordBorder() {

            return coordBorder;
        }

        public abstract ArrayList<Integer> getSelectedCheckboxes();

        @Override
        public void setEnabled(boolean state) {

        }

    }

    protected abstract class CaptureButtonPanel extends JPanel {

        protected ArrayList<DataCaptureListener> dataCaptureListeners
                = new ArrayList<DataCaptureListener>();
        public final static String CAPTURE = "Capture Positions ";
        public final static String CANCEL = "Cancel";
        protected JButton captureButton = new JButton();
        protected ButtonGroup arrowGroup = new ButtonGroup();
        protected JPanel buttonPanel = new JPanel();

        /**
         * The label containing the additional note that is displayed while the
         * data file is being retrieved to let users know that progress is being
         * made.
         */
        protected JLabel progressNote = null;

        public CaptureButtonPanel(final String unit) {

            super();

            captureButton.setBorder(new CompoundBorder(BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(7, 15, 7, 15)));

            //Create the radio buttons.
            final BasicArrowButton radiusButton = new BasicArrowButton(BasicArrowButton.NORTH) {

                @Override
                public String getText() {

                    return CAPTURE + "(" + unit + ")";
                }
            };

            final BasicArrowButton kmButton = new BasicArrowButton(BasicArrowButton.SOUTH) {

                @Override
                public String getText() {

                    return CAPTURE + "(km)";
                }
            };

            radiusButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (captureButton.getText().equalsIgnoreCase(CANCEL)) {
                        return;
                    }

                    captureButton.setText(CAPTURE + "(" + unit + ")");
                    kmButton.setSelected(false);
                    radiusButton.setEnabled(false);
                    kmButton.setEnabled(true);
                    radiusButton.setSelected(true);
                }
            });

            kmButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (captureButton.getText().equalsIgnoreCase(CANCEL)) {
                        return;
                    }
                    captureButton.setText(CAPTURE + "(km)");
                    radiusButton.setSelected(false);
                    radiusButton.setEnabled(true);
                    kmButton.setEnabled(false);
                    kmButton.setSelected(true);
                }
            });

            arrowGroup.add(radiusButton);

            arrowGroup.add(kmButton);

            //Put the radio buttons in a column in a panel.
            JPanel radioPanel = new JPanel(new GridLayout(0, 1));
            radioPanel.add(radiusButton);
            radiusButton.doClick();
            radioPanel.add(kmButton);

            add(buttonPanel, BorderLayout.CENTER);

            add(radioPanel, BorderLayout.WEST);

            progressNote = new JLabel();
            JPanel notePanel = new JPanel();
            notePanel.add(progressNote);

            add(notePanel);

        }

        /**
         * Registers the given listener to receive data capture request.
         *
         * @param listener the listener whose dataCapture method will be invoked
         * when an event occurs
         */
        public void addDataCaptureListener(DataCaptureListener listener) {

            dataCaptureListeners.add(listener);
        }

        /**
         * Unregisters the given listener.
         *
         * @param listener the listener that is to be removed
         */
        public void removeDataCaptureListener(DataCaptureListener listener) {

            dataCaptureListeners.remove(listener);
        }

        /**
         * Sets the additional note that is displayed along with the progress
         * bar.
         *
         * @param value a string specifying the note to display
         */
        public void setProgressNote(String value) {

            progressNote.setText(value);
        }

        /**
         * Sets the progress to a finished state.
         */
        public void setProgressFinished() {

            setProgressNote(null);
        }

        public void toggleButton() {

            if (captureButton.getText().startsWith(CAPTURE)) {
                captureButton.setText(CANCEL);
            } else {
                resetCaptureButton();
            }
        }

        public void resetCaptureButton() {

            Enumeration elements = arrowGroup.getElements();
            while (elements.hasMoreElements()) {
                BasicArrowButton button = (BasicArrowButton) elements.nextElement();
                if (button.isSelected()) {

                    captureButton.setText(button.getText());
                }
            }
        }

    }

}
