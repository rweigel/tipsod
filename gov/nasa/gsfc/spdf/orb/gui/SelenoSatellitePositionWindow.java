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
 * $Id: SelenoSatellitePositionWindow.java,v 1.3 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import static gov.nasa.gsfc.spdf.orb.OrbitViewer.showDocument;
import static gov.nasa.gsfc.spdf.orb.gui.SatellitePositionWindow.CaptureButtonPanel.CAPTURE;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import gov.nasa.gsfc.spdf.orb.utils.SolenocentricUtils;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.vecmath.Point3f;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author rchimiak
 */
public class SelenoSatellitePositionWindow extends SatellitePositionWindow {

    private final InfoPanel infoPanel = new InfoPanel();
    private final CaptureButtonPanel captureButtonPane = new CaptureButtonPanel();
    private SolenocentricUtils sse = null;

    /**
     * Creates a new instance of SatellitePositionWindow
     */
    public SelenoSatellitePositionWindow() {

        super();

        getContentPane().add(infoPanel, BorderLayout.NORTH);

        if (OrbitViewer.isConnected()) {

            getContentPane().add(captureButtonPane, BorderLayout.SOUTH);

        }
        this.setMinimumSize(new Dimension(360, 100));

    }

    public void setSse(SolenocentricUtils sse) {

        this.sse = sse;

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
            posArray[1] = new Float(table.getModel().getValueAt(
                    i, Column.Y).toString());
            posArray[2] = Float.parseFloat(table.getModel().getValueAt(
                    i, Column.Z).toString());

            if (ControlPanel.isSolenocentric()) {

                Point3f point = new Point3f(posArray);
                Transform3D t = new Transform3D();

                //     SelenoAnimation.getEarthAnimation().getPositionPath().getTarget().getTransform(t);
                t.invert();
                t.transform(point);
                point.get(posArray);
            }
            table.getModel().setValueAt(ControlPanel.isSolenocentric()
                    ? new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDPause.distanceToMagnetopause(posArray) / PhysicalConstants.MOON_TO_EARTH_RADIUS)
                    : new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDPause.distanceToMagnetopause(posArray)), i, Column.MAGNETOPAUSE);

            table.getModel().setValueAt(ControlPanel.isSolenocentric()
                    ? new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDSurf.distanceToBowshock(posArray) / PhysicalConstants.MOON_TO_EARTH_RADIUS)
                    : new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDSurf.distanceToBowshock(posArray)), i, Column.BOWSHOCK);

            table.getModel().setValueAt(ControlPanel.isSolenocentric()
                    ? new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDNeutral.distanceToNeutralSheet(posArray) / PhysicalConstants.MOON_TO_EARTH_RADIUS)
                    : new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDNeutral.distanceToNeutralSheet(posArray)), i, Column.NEUTRAL_SHEET);
        }
    }

    /**
     * Update the X,Y,Z coordinates as a response to a change in timef interest
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

        /*      if (ControlPanel.isSolenocentric()) {

         Transform3D t = new Transform3D();

         SelenoAnimation.getEarthAnimation().getPositionPath().getTarget().getTransform(t);
         t.invert();
         t.transform(position);
         position.get(posArray);
         }*/
        for (int i = 0; i < 3; i++) {

            table.getModel().setValueAt(doublePosArray[i], order, i + Column.X);
            table.getModel().setValueAt(Footpoint.CartesianToSpherical(doublePosArray)[i], order, i + Column.RADIUS);
        }
        if (ControlPanel.getCentralBody() != null && !ControlPanel.getCentralBody().equals(ControlPanel.Body.SUN)) {

            table.getModel().setValueAt(ControlPanel.isSolenocentric()
                    ? new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDPause.distanceToMagnetopause(posArray) / PhysicalConstants.MOON_TO_EARTH_RADIUS)
                    : new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDPause.distanceToMagnetopause(posArray)), order, Column.MAGNETOPAUSE);
            table.getModel().setValueAt(ControlPanel.isSolenocentric()
                    ? new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDSurf.distanceToBowshock(posArray) / PhysicalConstants.MOON_TO_EARTH_RADIUS)
                    : new Double(
                            gov.nasa.gsfc.spdf.orb.utils.MHDSurf.distanceToBowshock(posArray)), order, Column.BOWSHOCK);

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

        // private  final Font plain = new Font("Comic Sans MS",Font.PLAIN,12);
        /**
         * Create an instance of the top panel of the position window
         */
        public InfoPanel() {

            super();

            //********************Distance to panel********************
            magCheck.setFont(Util.comicPlain12Font);
            bowCheck.setFont(Util.comicPlain12Font);

            magCheck.addItemListener(this);
            bowCheck.addItemListener(this);

            JPanel distancePanel = new JPanel();

            distancePanel.setLayout(new BoxLayout(distancePanel, BoxLayout.Y_AXIS));
            distancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            distancePanel.add(magCheck);
            distancePanel.add(bowCheck);

            distanceBorder = BorderFactory.createTitledBorder("distance to (" + (ControlPanel.isSolenocentric() ? "RM" : "RE") + "):");
            distanceBorder.setTitleFont(Util.labelFont);

            distancePanel.setBorder(distanceBorder);

            //********************footpoints********************
            //********************layout********************
            JPanel p = new JPanel(new GridLayout(1, 2, 15, 0));
            p.add(coordinatesPanel);
            p.add(distancePanel);

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

            getTable().removeColumns("Neutral Sheet");

            getTable().removeColumns("North Lat");
            getTable().removeColumns("North Long");

            getTable().removeColumns("South Lat");
            getTable().removeColumns("South Long");

            getTable().removeColumns("Closest Lat");
            getTable().removeColumns("Closest Long");

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
                } else {
                    getTable().removeColumns(s);
                }
            }
            getTable().calcColumnWidths();
            SelenoSatellitePositionWindow.this.pack();
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

            return column;
        }

        @Override
        public void setEnabled(boolean state) {

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

            }
        }
    }

    private class CaptureButtonPanel extends SatellitePositionWindow.CaptureButtonPanel {

        private SwingWorker<URI, Void> worker;

        public CaptureButtonPanel() {

            super("RM");
            captureButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    //     ArrayList listeners = (ArrayList) dataCaptureListeners.clone();
                    // a copy of the listeners
                    //   DataCaptureListener listener;
                    // a specific listener
                    try {

                        final String command = e.getActionCommand();

                        if (e.getActionCommand().startsWith(CAPTURE)) {

                            if (sse != null) {
                                setProgressNote("Capturing...");
                                //  index++;

                                worker = new SwingWorker<URI, Void>() {

                                    URI uri = null;

                                    @Override
                                    protected URI doInBackground() {

                                        return uri = displayText(command);

                                    }

                                    @Override
                                    protected void done() {
                                        try {

                                            showDocument(uri.toURL());

                                            setProgressFinished();
                                            resetCaptureButton();

                                            //need to display dialog to explain catch
                                        } catch (MalformedURLException ex) {
                                            setProgressFinished();
                                            resetCaptureButton();

                                        } catch (NullPointerException ex) {
                                            setProgressFinished();
                                            resetCaptureButton();
                                        }

                                    }
                                    // Start the worker thread

                                };
                                worker.execute();

                                toggleButton();

                            } else {

                                toggleButton();

                                setProgressFinished();
                            }
                        } else {

                            if (worker != null) {

                                worker.cancel(true);

                            }
                            setProgressFinished();

                            resetCaptureButton();

                        }
                    } catch (Exception ex) {

                        setProgressFinished();

                        resetCaptureButton();
                    }
                }
            });
            buttonPanel.add(captureButton);

        }

        private URI displayText(String tempUnits) {
            try {

                File temp = File.createTempFile("SSElisting", ".txt");
                temp.deleteOnExit();
                BufferedWriter out = new BufferedWriter(new FileWriter(temp));

                String units = tempUnits.indexOf("km") > 0 ? "KM" : "RM";

                List<SatelliteData> location = sse.getLocationData();
                if (location != null) {

                    List<XMLGregorianCalendar> time = location.get(0).getTime();

                    writeIntro(out, time, units);
                    int k = -1;
                    for (SatelliteData satData : location) {

                        k++;

                        if (satData != null && !satData.getId().equalsIgnoreCase("moon")) {

                            out.write("Satellite: " + getTable().getValueAt(k, 0).toString());
                            out.newLine();
                            out.newLine();
                            out.append("yyyy-mm-dd  hh:mm:ss            ");

                            if (infoPanel.cartCheck.isSelected()) {
                                if (units.contains("RM")) {
                                    out.append("X (Rm)           Y(Rm)              Z(Rm)          ");
                                } else {
                                    out.append("X (km)                Y(km)                 Z(km)          ");
                                }

                            }
                            if (infoPanel.spherCheck.isSelected()) {
                                out.append("latitude         longitude             radius");

                            }

                            out.newLine();
                            out.newLine();

                            double[] initcart = {
                                units.equalsIgnoreCase("RM")
                                ? PhysicalConstants.kmToRm(satData.getCoordinates().get(0).getX().get(0))
                                : satData.getCoordinates().get(0).getX().get(0),
                                units.equalsIgnoreCase("RM")
                                ? PhysicalConstants.kmToRm(satData.getCoordinates().get(0).getY().get(0))
                                : satData.getCoordinates().get(0).getY().get(0),
                                units.equalsIgnoreCase("RM")
                                ? PhysicalConstants.kmToRm(satData.getCoordinates().get(0).getZ().get(0))
                                : satData.getCoordinates().get(0).getZ().get(0)
                            };
                            CharSequence xBuf = (Util.roundToString(initcart[0], 1)).length() < 7 ? "  " : "";
                            CharSequence yBuf = (Util.roundToString(initcart[1], 1)).length() < 7 ? "  " : "";
                            CharSequence zBuf = (Util.roundToString(initcart[2], 1)).length() < 7 ? "  " : "";
                            double[] initspher = Footpoint.CartesianToSpherical(initcart);

                            CharSequence latBuf = (Util.roundToString(initspher[1], 1)).length() < 7 ? "  " : "";
                            CharSequence lonBuf = (Util.roundToString(initspher[2], 1)).length() < 7 ? "  " : "";
                            CharSequence radBuf = (Util.roundToString(initspher[0], 1)).length() < 7 ? "  " : "";

                            for (int j = 0; j < satData.getTime().size(); j++) {

                                double[] cart = {
                                    units.equalsIgnoreCase("RM")
                                    ? PhysicalConstants.kmToRm(satData.getCoordinates().get(0).getX().get(j))
                                    : satData.getCoordinates().get(0).getX().get(j),
                                    units.equalsIgnoreCase("RM")
                                    ? PhysicalConstants.kmToRm(satData.getCoordinates().get(0).getY().get(j))
                                    : satData.getCoordinates().get(0).getY().get(j),
                                    units.equalsIgnoreCase("RM")
                                    ? PhysicalConstants.kmToRm(satData.getCoordinates().get(0).getZ().get(j))
                                    : satData.getCoordinates().get(0).getZ().get(j)
                                };

                                double[] spher = Footpoint.CartesianToSpherical(cart);

                                out.append(Util.formatter.format(satData.getTime().get(j).toGregorianCalendar().getTime()) + "          ");
                                if (infoPanel.cartCheck.isSelected()) {

                                    String x = Util.roundToString(cart[0], 3);
                                    out.append(x.contains("-") ? xBuf : xBuf + " ");
                                    out.append(x + "          ");

                                    String y = Util.roundToString(cart[1], 3);
                                    out.append(y.contains("-") ? yBuf : yBuf + " ");
                                    out.append(y + "          ");

                                    String z = Util.roundToString(cart[2], 3);
                                    out.append(z.contains("-") ? zBuf : zBuf + " ");
                                    out.append(z + "          ");

                                }
                                if (infoPanel.spherCheck.isSelected()) {

                                    String lat = Util.roundToString(spher[1], 3);
                                    out.append(lat.contains("-") ? latBuf : latBuf + " ");
                                    out.append(lat + "          ");

                                    String lon = Util.roundToString(spher[2], 3);
                                    out.append(lon.contains("-") ? lonBuf : lonBuf + " ");
                                    out.append(lon + "          ");

                                    String rad = Util.roundToString(spher[0], 3);
                                    out.append(rad.contains("-") ? radBuf : radBuf + " ");
                                    out.append(rad + "          ");

                                }

                                out.newLine();

                            }

                            out.newLine();
                            out.newLine();

                        }
                    }

                }

                out.close();
                return temp.toURI();

            } catch (IOException ex) {

                return null;
            }

        }

        private void writeIntro(BufferedWriter bw, List<XMLGregorianCalendar> time, String unit) throws IOException {
            bw.write("Date: " + new Date().toString());
            bw.newLine();
            bw.write("Start Time: " + time.get(0).toString() + "       "
                    + "Stop Time: " + time.get(time.size() - 1));
            bw.newLine();
            bw.write("Coordinate: " + SelenoSatellitePositionWindow.this.getCoordinateField().getText());
            bw.newLine();
            bw.write("Unit: ");
            bw.append(unit.contains("km") ? "km" : "RM");
            bw.newLine();
            bw.newLine();
        }

    }

}
