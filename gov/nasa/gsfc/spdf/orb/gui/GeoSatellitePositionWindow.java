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
 * $Id: GeoSatellitePositionWindow.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on October 22, 2002, 9:51 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import javax.media.j3d.Transform3D;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.FootpointsGroup;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SelenoAnimation;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.vecmath.Point3f;

/**
 * The SatellitePositionWindow implements a small window to display the
 * coordinates of the selected satellites as they vary as a function of time
 *
 * @author rchimiak
 ** @version $Revision: 1.2 $
 */
public class GeoSatellitePositionWindow extends SatellitePositionWindow {

    private final InfoPanel infoPanel = new InfoPanel();
    private final CaptureButtonPanel captureButtonPane = new CaptureButtonPanel();

    /**
     * Creates a new instance of SatellitePositionWindow
     */
    public GeoSatellitePositionWindow() {

        super();

        getContentPane().add(infoPanel, BorderLayout.NORTH);

        if (OrbitViewer.isConnected()) {

            getContentPane().add(captureButtonPane, BorderLayout.SOUTH);

        }

    }

    /**
     * Returns the JFormattedTextField containing the time corresponding to
     * position of the satellite on its orbit at that moment.
     *
     * @return the time field
     */
    @Override
    public JFormattedTextField getTimeField() {

        return infoPanel.getTimeField();
    }

    @Override
    public ArrayList<Integer> getSelectedCheckboxes() {

        return infoPanel.getSelectedCheckboxes();
    }

    /**
     * Returns the JTextField containing the user selected coordinate system
     * currently being represented.
     *
     * @return the coordinate display field
     */
    @Override
    public JTextField getCoordinateField() {

        return infoPanel.getCoordinateField();
    }

    @Override
    public TitledBorder getCoordBorder() {

        return infoPanel.getCoordBorder();
    }

    public TitledBorder getDistanceBorder() {

        return infoPanel.getDistanceBorder();
    }

    @Override
    public void clearCheckBoxes() {

        infoPanel.clearCheckBoxes();
    }

    @Override
    public void resetCaptureButton() {

        captureButtonPane.resetCaptureButton();
    }

    @Override
    public void setProgressNote(String value) {

        captureButtonPane.setProgressNote(value);

    }

    @Override
    public void setProgressFinished() {

        captureButtonPane.setProgressFinished();
    }

    @Override
    public void addDataCaptureListener(DataCaptureListener listener) {

        captureButtonPane.addDataCaptureListener(listener);

    }

    @Override
    public void removeDataCaptureListener(DataCaptureListener listener) {

        captureButtonPane.removeDataCaptureListener(listener);

    }

    @Override
    public void SWPChanged() {

        for (int i = 0; i < table.getRowCount(); i++) {

            float[] posArray = new float[3];

            posArray[0] = Float.parseFloat(table.getModel().getValueAt(
                    i, Column.X).toString());
            posArray[1] = Float.parseFloat(table.getModel().getValueAt(
                    i, Column.Y).toString());
            posArray[2] = Float.parseFloat(table.getModel().getValueAt(
                    i, Column.Z).toString());

            if (ControlPanel.isSolenocentric()) {

                Point3f point = new Point3f(posArray);
                Transform3D t = new Transform3D();

                SelenoAnimation.getEarthAnimation().getPositionPath().getTarget().getTransform(t);
                t.invert();
                t.transform(point);
                point.get(posArray);
            }
            table.getModel().setValueAt(gov.nasa.gsfc.spdf.orb.utils.MHDPause.distanceToMagnetopause(posArray), i, Column.MAGNETOPAUSE);

            table.getModel().setValueAt(gov.nasa.gsfc.spdf.orb.utils.MHDSurf.distanceToBowshock(posArray), i, Column.BOWSHOCK);

            table.getModel().setValueAt(gov.nasa.gsfc.spdf.orb.utils.MHDNeutral.distanceToNeutralSheet(posArray), i, Column.NEUTRAL_SHEET);
        }
    }

    /**
     * Update the X,Y,Z coordinates as a response to a change in time
     *
     * @param position, a point representing the satellite position at that
     * time.
     * @param order, which selected satellite is being updated
     */
    @Override
    public void updatePosition(Point3f position, int order) {

        if (order == -1) {
            return;
        }

        float[] posArray = new float[3];
        double[] doublePosArray = new double[3];
        position.get(posArray);

        for (int i = 0; i < 3; i++) {

            doublePosArray[i] = ControlPanel.isSolenocentric()
                    ? (double) (posArray[i]) / PhysicalConstants.MOON_TO_EARTH_RADIUS
                    : (double) (posArray[i]);
        }

        for (int i = 0; i < 3; i++) {

            table.getModel().setValueAt(doublePosArray[i], order, i + Column.X);
            table.getModel().setValueAt(Footpoint.CartesianToSpherical(doublePosArray)[i], order, i + Column.RADIUS);
        }

        table.getModel().setValueAt(gov.nasa.gsfc.spdf.orb.utils.MHDPause.distanceToMagnetopause(posArray), order, Column.MAGNETOPAUSE);
        table.getModel().setValueAt(gov.nasa.gsfc.spdf.orb.utils.MHDSurf.distanceToBowshock(posArray), order, Column.BOWSHOCK);

        table.getModel().setValueAt(gov.nasa.gsfc.spdf.orb.utils.MHDNeutral.distanceToNeutralSheet(posArray), order, Column.NEUTRAL_SHEET);

    }

    public void updateFootpoints(FootpointsGroup.HemisphericTypes type, Point3f position, int order) {

        float[] posArray = new float[3];
        double[] doublePosArray = new double[3];
        position.get(posArray);

        for (int i = 0; i < 3; i++) {

            doublePosArray[i] = (double) posArray[i];
        }

        for (int i = 1; i < 3; i++) {

            table.getModel().setValueAt(Footpoint.CartesianToSpherical(doublePosArray)[i], order, i - 1 + Column.NFLAT + type.ordinal() * 2);
        }
    }

    public void setSurfacesEnabled(boolean[] state) {

        infoPanel.setSurfacesEnabled(state);

    }

    public void setFootpointsEnabled(boolean state) {

        infoPanel.setEnabled(state);
    }

    /**
     * The InfoPanel class implements the top portion of the position window. It
     * contains a field to display time and a field to display the coordinate
     * system.
     */
    private class InfoPanel extends SatellitePositionWindow.InfoPanel {

        private final TitledBorder distanceBorder;
        private final JCheckBox magCheck = new JCheckBox("Magnetopause");
        private final JCheckBox bowCheck = new JCheckBox("Bowshock");
        private final JCheckBox neutCheck = new JCheckBox("Neutral Sheet");
        private final JCheckBox northCheck = new JCheckBox("North");
        private final JCheckBox southCheck = new JCheckBox("South");
        private final JCheckBox closestCheck = new JCheckBox("Closest");

        // private  final Font plain = new Font("Comic Sans MS",Font.PLAIN,12);
        /**
         * Create an instance of the top panel of the position window
         */
        public InfoPanel() {

            super();

            //********************Distance to panel********************
            magCheck.setFont(Util.comicPlain12Font);
            bowCheck.setFont(Util.comicPlain12Font);
            neutCheck.setFont(Util.comicPlain12Font);

            magCheck.addItemListener(this);
            bowCheck.addItemListener(this);
            neutCheck.addItemListener(this);

            JPanel distancePanel = new JPanel();

            distancePanel.setLayout(new BoxLayout(distancePanel, BoxLayout.Y_AXIS));
            distancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            distancePanel.add(magCheck);
            distancePanel.add(bowCheck);
            distancePanel.add(neutCheck);

            distanceBorder = BorderFactory.createTitledBorder("distance to (" + (ControlPanel.isSolenocentric() ? "RM" : "RE") + "):");
            distanceBorder.setTitleFont(Util.labelFont);

            distancePanel.setBorder(distanceBorder);

            //********************footpoints********************
            northCheck.setFont(Util.comicPlain12Font);
            southCheck.setFont(Util.comicPlain12Font);
            closestCheck.setFont(Util.comicPlain12Font);

            northCheck.addItemListener(this);
            southCheck.addItemListener(this);
            closestCheck.addItemListener(this);

            JPanel footpointPanel = new JPanel();

            footpointPanel.setLayout(new BoxLayout(footpointPanel, BoxLayout.Y_AXIS));
            footpointPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            footpointPanel.add(northCheck);
            footpointPanel.add(southCheck);
            footpointPanel.add(closestCheck);

            TitledBorder footpointTitle = BorderFactory.createTitledBorder("footpoints (\u00B0):");
            footpointTitle.setTitleFont(Util.labelFont);

            footpointPanel.setBorder(footpointTitle);

            //********************layout********************
            JPanel p = new JPanel(new GridLayout(1, 3, 15, 0));
            p.add(coordinatesPanel);
            p.add(distancePanel);
            p.add(footpointPanel);
            add(p);
            add(Box.createVerticalStrut(15));
        }

        @Override
        public void clearCheckBoxes() {

            super.clearCheckBoxes();

            if (!magCheck.isSelected()) {
                getTable().removeColumns("Magnetopause");
            }

            if (!bowCheck.isSelected()) {
                getTable().removeColumns("Bowshock");
            }

            if (!neutCheck.isSelected()) {
                getTable().removeColumns("Neutral Sheet");
            }

            if (!northCheck.isSelected()) {

                getTable().removeColumns("North Lat");
                getTable().removeColumns("North Long");
            }

            if (!southCheck.isSelected()) {

                getTable().removeColumns("South Lat");
                getTable().removeColumns("South Long");
            }

            if (!closestCheck.isSelected()) {

                getTable().removeColumns("Closest Lat");
                getTable().removeColumns("Closest Long");
            }
        }

        /**
         * Listens to the check boxes.
         */
        @Override
        public void itemStateChanged(ItemEvent e) {

            Object source = e.getItemSelectable();
            String s = ((JCheckBox) source).getText();
            if (e.getStateChange() == ItemEvent.SELECTED) {

                if (s.equalsIgnoreCase("Cartesian")) {

                    getTable().addColumns("X");
                    getTable().addColumns("Y");
                    getTable().addColumns("Z");
                } else if (s.equalsIgnoreCase("Spherical")) {

                    //  getTable().addColumns("Radius");
                    getTable().addColumns("Latitude");
                    getTable().addColumns("Longitude");
                    getTable().addColumns("Radius");
                } else if (s.equalsIgnoreCase("North")) {

                    getTable().addColumns("North Lat");
                    getTable().addColumns("North Long");
                } else if (s.equalsIgnoreCase("South")) {

                    getTable().addColumns("South Lat");
                    getTable().addColumns("South Long");
                } else if (s.equalsIgnoreCase("Closest")) {

                    getTable().addColumns("Closest Lat");
                    getTable().addColumns("Closest Long");
                } else {

                    getTable().addColumns(s);
                }
            } else {

                if (s.equalsIgnoreCase("Cartesian")) {

                    getTable().removeColumns("X");
                    getTable().removeColumns("Y");
                    getTable().removeColumns("Z");
                } else if (s.equalsIgnoreCase("Spherical")) {

                    //  getTable().removeColumns("Radius");
                    getTable().removeColumns("Latitude");
                    getTable().removeColumns("Longitude");
                    getTable().removeColumns("Radius");
                } else if (s.equalsIgnoreCase("North")) {

                    getTable().removeColumns("North Lat");
                    getTable().removeColumns("North Long");
                } else if (s.equalsIgnoreCase("South")) {

                    getTable().removeColumns("South Lat");
                    getTable().removeColumns("South Long");
                } else if (s.equalsIgnoreCase("Closest")) {

                    getTable().removeColumns("Closest Lat");
                    getTable().removeColumns("Closest Long");
                } else {
                    getTable().removeColumns(s);
                }
            }
            getTable().calcColumnWidths();
            GeoSatellitePositionWindow.this.pack();
        }

        public TitledBorder getDistanceBorder() {

            return distanceBorder;
        }

        @Override
        public ArrayList<Integer> getSelectedCheckboxes() {

            ArrayList<Integer> column = new ArrayList<Integer>();

            if (cartCheck.isSelected()) {

                column.add(Column.X);
                column.add(Column.Y);
                column.add(Column.Z);
            }

            if (spherCheck.isSelected()) {

                column.add(Column.RADIUS);
                column.add(Column.LATITUDE);
                column.add(Column.LONGITUDE);
            }

            if (magCheck.isSelected()) {

                column.add(Column.MAGNETOPAUSE);
            }

            if (bowCheck.isSelected()) {

                column.add(Column.BOWSHOCK);
            }

            if (neutCheck.isSelected()) {

                column.add(Column.NEUTRAL_SHEET);
            }

            if (northCheck.isSelected()) {

                column.add(Column.NFLAT);
            }

            if (southCheck.isSelected()) {

                column.add(Column.SFLAT);
            }

            if (closestCheck.isSelected()) {

                if (!northCheck.isSelected()) {

                    column.add(Column.NFLAT);
                }

                if (!southCheck.isSelected()) {

                    column.add(Column.SFLAT);
                }
            }

            return column;
        }

        @Override
        public void setEnabled(boolean state) {

            if (!state) {

                northCheck.setSelected(state);
                southCheck.setSelected(state);
                closestCheck.setSelected(state);
            }

            northCheck.setEnabled(state);
            southCheck.setEnabled(state);
            closestCheck.setEnabled(state);
        }

        public void setSurfacesEnabled(boolean[] state) {

            if (state != null) {

                if (!state[0]) {
                    magCheck.setSelected(state[0]);
                    magCheck.setEnabled(state[0]);
                }
                if (state.length > 1 && !state[1]) {
                    bowCheck.setSelected(state[1]);
                    bowCheck.setEnabled(state[1]);
                }
                if (state.length > 2 && !state[2]) {
                    neutCheck.setSelected(state[2]);
                    neutCheck.setEnabled(state[2]);
                }
            }
        }
    }

    private class CaptureButtonPanel extends SatellitePositionWindow.CaptureButtonPanel {

        public CaptureButtonPanel() {

            super("RE");
            captureButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ArrayList listeners = (ArrayList) dataCaptureListeners.clone();
                    // a copy of the listeners

                    DataCaptureListener listener;
                    // a specific listener

                    try {

                        if (e.getActionCommand().startsWith(CAPTURE)) {

                            for (Object listener1 : listeners) {
                                listener = (DataCaptureListener) listener1;
                                listener.dataCapture(getSelectedCheckboxes(), e.getActionCommand());
                                toggleButton();
                            }

                        } else {

                            toggleButton();

                            for (Object listener1 : listeners) {
                                listener = (DataCaptureListener) listener1;
                                listener.dataCaptureCancel();
                            }
                            setProgressFinished();
                        }
                    } catch (Exception ex) {

                        setProgressFinished();

                        resetCaptureButton();
                    }
                }
            });
            buttonPanel.add(captureButton);

        }

    }

}
