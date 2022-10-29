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
 * $Id: HelioSatellitePositionWindow.java,v 1.4 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on October 22, 2002, 9:51 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import static gov.nasa.gsfc.spdf.orb.OrbitViewer.showDocument;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.vecmath.Point3f;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import javax.swing.SwingWorker;

/**
 * The SatellitePositionWindow implements a small window to display the
 * coordinates of the selected satellites as they vary as a function of time
 *
 * @author rchimiak
 ** @version $Revision: 1.4 $
 */
/**
 *
 * @author rchimiak
 */
public class HelioSatellitePositionWindow extends SatellitePositionWindow {

    private final InfoPanel infoPanel = new InfoPanel();
    private final CaptureButtonPanel captureButtonPane = new CaptureButtonPanel();

    /**
     * Creates a new instance of SatellitePositionWindow
     */
    public HelioSatellitePositionWindow() {
        super();

        getContentPane().add(infoPanel, BorderLayout.NORTH);

        getContentPane().add(captureButtonPane, BorderLayout.SOUTH);
        this.setMinimumSize(new Dimension(360, 100));

    }

    public void setDataCaptureTrajectory(List<Trajectory> traj) {

        captureButtonPane.setTrajectories(traj);
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

    }

    @Override
    public void setProgressFinished() {

    }

    @Override
    public void addDataCaptureListener(DataCaptureListener listener) {

    }

    @Override
    public void removeDataCaptureListener(DataCaptureListener listener) {

    }

    @Override
    public void SWPChanged() {
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

            doublePosArray[i] = (double) (posArray[i]);
        }

        for (int i = 0; i < 3; i++) {

            table.getModel().setValueAt(doublePosArray[i], order, i + Column.X);
            table.getModel().setValueAt(Footpoint.CartesianToSpherical(doublePosArray)[i], order, i + Column.RADIUS);
        }

    }

    /**
     * The InfoPanel class implements the top portion of the position window. It
     * contains a field to display time and a field to display the coordinate
     * system.
     */
    private class InfoPanel extends SatellitePositionWindow.InfoPanel {

        public InfoPanel() {

            super();

            JPanel p = new JPanel(new GridLayout(1, 1, 15, 0));
            p.add(coordinatesPanel);

            add(p);
            add(Box.createVerticalStrut(15));

        }

        @Override
        public void clearCheckBoxes() {
            super.clearCheckBoxes();
            getTable().removeColumns("Magnetopause");

            getTable().removeColumns("Bowshock");

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
            HelioSatellitePositionWindow.this.pack();
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

            return column;
        }

    }

    private class CaptureButtonPanel extends SatellitePositionWindow.CaptureButtonPanel {

        private List<Trajectory> dataCaptureTrajectories = null;
        //    private int index = 0;
        private SwingWorker<URI, Void> worker;

        public CaptureButtonPanel() {

            super("au");
            captureButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {

                    final String command = e.getActionCommand();

                    try {

                        if (e.getActionCommand().startsWith(CAPTURE)) {

                            if (dataCaptureTrajectories != null) {
                                setProgressNote("Capturing...");
                                //  index++;

                                worker = new SwingWorker<URI, Void>() {

                                    URI uri = null;

                                    @Override
                                    protected URI doInBackground() {

                                        return uri = writeToFile(command);

                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            showDocument(uri.toURL());

                                            setProgressFinished();
                                            resetCaptureButton();

                                            // need to display dialog to explain catch
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

        private void setTrajectories(List<Trajectory> traj) {

            this.dataCaptureTrajectories = traj;

        }

        private URI writeToFile(String unit) {

            //  File file = new File(index + "Helio listing.txt");
            try {

                File file = File.createTempFile("Helio listing", ".txt");
                file.deleteOnExit();

                // file.createNewFile();
                List<XMLGregorianCalendar> time = dataCaptureTrajectories.get(0).getTime();

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                writeIntro(bw, time, unit);
                int j = 0;
                for (Trajectory satData : dataCaptureTrajectories) {

                    List<Double> radius = satData.getRadius();
                    List<Double> latitude = satData.getLatitude();
                    List<Double> longitude = satData.getLongitude();

                    bw.write("Satellite: " + getTable().getValueAt(j, 0).toString());
                    bw.newLine();
                    bw.newLine();
                    bw.write("       Time              ");

                    if (HelioSatellitePositionWindow.this.infoPanel.cartCheck.isSelected()) {
                        bw.append("    X       ");
                        bw.append("  Y       ");
                        bw.append("  Z       ");
                    }
                    if (HelioSatellitePositionWindow.this.infoPanel.spherCheck.isSelected()) {
                        bw.append("Latitude    ");
                        bw.append("Longitude    ");
                        bw.append("Radius");
                    }
                    bw.newLine();
                    for (int i = 0; i < time.size(); i++) {
                        bw.write(time.get(i).toString());
                        bw.append("      ");
                        if (HelioSatellitePositionWindow.this.infoPanel.cartCheck.isSelected()) {

                            double[] coordinates = Footpoint.sphericalToCartesian(new double[]{satData.getRadius().get(0),
                                satData.getLatitude().get(0),
                                satData.getLongitude().get(0)});

                            double X = coordinates[0];
                            double Y = coordinates[1];
                            double Z = coordinates[2];

                            if (unit.contains("km")) {
                                bw.append(String.valueOf(Math.round((X * 149597870.691) * 1000.0) / 1000.0));
                                bw.append("     ");
                                bw.append(String.valueOf(Math.round((Y * 149597870.691) * 1000.0) / 1000.0));
                                bw.append("     ");
                                bw.append(String.valueOf(Math.round((Z * 149597870.691) * 1000.0) / 1000.0));
                                bw.append("     ");
                            } else {
                                bw.append(String.valueOf(Math.round(coordinates[0] * 1000.0) / 1000.0));
                                bw.append("     ");
                                bw.append(String.valueOf(Math.round(coordinates[1] * 1000.0) / 1000.0));
                                bw.append("     ");
                                bw.append(String.valueOf(Math.round(coordinates[2] * 1000.0) / 1000.0));
                                bw.append("     ");
                            }
                        }

                        if (HelioSatellitePositionWindow.this.infoPanel.spherCheck.isSelected()) {
                            bw.append(latitude.get(i).toString());
                            bw.append("         ");
                            bw.append(longitude.get(i).toString());
                            bw.append("      ");
                            bw.append(radius.get(i).toString());
                        }
                        bw.newLine();

                    }
                    bw.newLine();
                    j++;

                }
                bw.close();

                return file.toURI();

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
            bw.write("Coordinate: " + HelioSatellitePositionWindow.this.getCoordinateField().getText());
            bw.newLine();
            bw.write("Unit: ");
            bw.append(unit.contains("km") ? "km" : "au");
            bw.newLine();
            bw.newLine();
        }

    }

}
