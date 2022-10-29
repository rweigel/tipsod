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
 * $Id: SelectionWindow.java,v 1.53 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on July 1, 2002, 9:57 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteDescription;
import gov.nasa.gsfc.spdf.ssc.client.SpaseObservatoryDescription;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import gov.nasa.gsfc.spdf.helio.client.ObjectDescription;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * The SelectionWindow class implements the window allowing the user to select a
 * set of satellites and other parameters for display.
 *
 * @author rchimiak
 * @version $Revision: 1.53 $
 */
public class SelectionWindow extends JFrame {

    private JButton orbButton = null;
    private SatelliteGraphPropertiesTable satTable = null;
    private SatelliteGraphTableModel satModel = null;
    private ControlPanel controlPane;
    private boolean windowComplete = true;
    /**
     * Map whose key is a SPASE Observatory ResourceID and whose value is the
     * corresponding SSC satellite identifier.
     */
    private final Map< String, String> spaseToSscMap
            = new HashMap<String, String>();
    /**
     * Map whose key is the name of an Observatory to its Observatory group if
     * it has one.
     */
    private final Map< String, String> observatoryToGroupMap
            = new HashMap<String, String>();
    public final static String UPDATE = "Graph Orbits";
    public final static String CANCEL = "Cancel";
    /**
     * The listeners that are to be notified when the parameters for the graph
     * have been specified.
     */
    private final Vector< GraphSpecifiedListener> graphSpecifiedListeners
            = new Vector<GraphSpecifiedListener>();
    /**
     * The label containing the additional note that is displayed with the
     * progress bar. Used, for example, to show which file the is currently
     * being copied during a multiple-file copy.
     */
    private JLabel progressNote = null;
    /**
     * The progress bar.
     */
    private JProgressBar progressBar = null;

    /**
     * Creates a new SelectionWindow
     *
     * @param sats array of strings of available satellites
     * @param spaseObservatories SPASE Observatory descriptions.
     * @throws java.lang.CloneNotSupportedException
     */
    public SelectionWindow(List< SatelliteDescription> sats,
            List< SpaseObservatoryDescription> spaseObservatories) throws CloneNotSupportedException {

        super("Satellite Chooser");

        for (SpaseObservatoryDescription observatory : spaseObservatories) {

            spaseToSscMap.put(observatory.getResourceId(),
                    observatory.getId());

        }

        setSize(680, 480);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //------------- create table panel ------------------
        // At this point sats cannot be null;
        satModel = new SatelliteGraphTableModel(sats);

        setSatelliteGraphProperties("/properties.txt");
        if (satModel != null) {

            satModel.addGroup(observatoryToGroupMap);

            satTable = new SatelliteGraphPropertiesTable(satModel);
        } else {
            windowComplete = false;
        }

        if (satTable != null) {

            JScrollPane sp = new JScrollPane(satTable);
            contentPane.add(sp, BorderLayout.CENTER);

            //------------- create control panel ----------------
            controlPane = new ControlPanel(satTable);

            contentPane.add(controlPane, BorderLayout.NORTH);

            //------------- create get/update panel ----------------
            JPanel orbButtonPane = new JPanel();
            orbButtonPane.setLayout(new BoxLayout(orbButtonPane,
                    BoxLayout.Y_AXIS));

            contentPane.add(orbButtonPane, BorderLayout.SOUTH);
            orbButton = createOrbButton();

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(orbButton);
            orbButtonPane.add(buttonPanel);

            progressNote = new JLabel();
            JPanel notePanel = new JPanel();
            notePanel.add(progressNote);

            progressBar = new JProgressBar(0, 2);
            JPanel barPanel = new JPanel();
            barPanel.add(progressBar);
            orbButtonPane.add(notePanel);
            orbButtonPane.add(barPanel);
        } else {
            windowComplete = false;
        }

        //register the listener
        if (orbButton != null) {
            orbButton.addActionListener(new GetOrbListener());
        }

    }

    public void setHelioSatelliteGraphProperties(List< ObjectDescription> helioSats) throws CloneNotSupportedException {

        Set<String> names = new HashSet<String>();
        for (ListIterator<ObjectDescription> iter = helioSats.listIterator(); iter.hasNext();) {
            ObjectDescription element = iter.next();

            if (names.contains(element.getName())) {
                iter.remove();

            } else {
                names.add(element.getName());
            }
        }

        satModel.setHelioSatelliteGraphProperties(helioSats);
        setSatelliteGraphProperties("/helioProperties.txt");
        //compare with the earth ones already there

    }

    /**
     * Returns the button used to notify the server that a request is being made
     * based on the parameters being passed.
     *
     * @return
     */
    public JButton createOrbButton() {

        orbButton = new JButton(UPDATE);
        orbButton.setBorder(new CompoundBorder(BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        orbButton.setPreferredSize(new Dimension(120, 30));
        return orbButton;
    }

    /**
     * Returns the control panel component of the spacecraft selection window.
     * The control panel comprises the time spinners, coordinate spinner and
     * resolution.
     *
     * @return the selection window control panel component
     */
    public ControlPanel getControlPane() {
        return controlPane;
    }

    /**
     * Returns the Table model of the satelliteGraphProperties.
     *
     * @return
     */
    public SatelliteGraphTableModel getSatModel() {
        return satModel;
    }

    /**
     * The spacecraft selection window will be displayed only if built in its
     * entirety.
     *
     * @return boolean representing the completion state of this window
     */
    public boolean isComplete() {
        return windowComplete;
    }

    /**
     * Sets special default SatelliteGraphProperties in the given model.
     *
     * @param model the SatelliteGraphTableModel in which the special properties
     * are to be set
     */
    private void setSatelliteGraphProperties(String properties) throws CloneNotSupportedException {

        try {

            InputStream inStrm = SelectionWindow.class.getResourceAsStream(
                    properties);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inStrm));
            String line;

            while ((line = in.readLine()) != null) {

                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                String name = tokenizer.nextToken();

                Color color = getColor(tokenizer.nextToken(), name);

                SatelliteGraphShape symbol = SatelliteGraphShape.getInstance(tokenizer.nextToken());

                LineStyle lineStyle = LineStyle.getInstance(
                        tokenizer.nextToken());
                SatelliteGraphProperties satProperties;

                satProperties = !properties.contains("helio")
                        ? satModel.getSatelliteGraphProperties(name)
                        : satModel.getSatelliteGraphProperties(name, ControlPanel.Body.SUN);
                // the default properties for
                //  this satellite
                if (satProperties != null) {

                    satProperties.setColor(color);
                    satProperties.setShape(symbol);
                    satProperties.setLineStyle(lineStyle);

                    if (!properties.contains("helio")) {

                        satModel.setSatelliteGraphProperties(satProperties);
                        SatelliteGraphProperties clone = (SatelliteGraphProperties) satProperties.clone();
                        if (!clone.getName().equalsIgnoreCase("moon")) {
                            satModel.getBodyArray(ControlPanel.Body.MOON).add(clone);
                        }

                    } else {
                        satModel.setSatelliteGraphProperties(satProperties, ControlPanel.Body.SUN);

                    }

                    /**
                     * used for the barrel group until new properties.txt file
                     */
                    if (name.contains("barrel")) {

                        observatoryToGroupMap.put(name, "BARREL");

                    }
                }

            }

        } catch (IOException e) {
        }
    }

    private Color getColor(String stringColor, String name) {

        StringTokenizer tokenizer = new StringTokenizer(stringColor, ", ");

        return new Color(Integer.parseInt(tokenizer.nextToken()),
                Integer.parseInt(tokenizer.nextToken()),
                Integer.parseInt(tokenizer.nextToken()));
    }

    /**
     * Registers the given listener to receive GraphSpecifiedEvent events.
     *
     * @param listener the listener whose graphSpecified method will be invoked
     * when an event occurs
     */
    public void addGraphSpecifiedListener(GraphSpecifiedListener listener) {

        graphSpecifiedListeners.add(listener);
    }

    /**
     * Unregisters the given listener.
     *
     * @param listener the listener that is to be removed
     */
    public void removeGraphSpecifiedListener(GraphSpecifiedListener listener) {

        graphSpecifiedListeners.removeElement(listener);
    }

    /**
     * Sets the additional note that is displayed along with the progress bar.
     *
     * @param value a string specifying the note to display
     */
    public void setProgressNote(String value) {

        progressNote.setText(value);
    }

    /**
     * Sets the indeterminate property of the progress bar, which determines
     * whether the progress bar is in determinate or indeterminate mode. By
     * default, the progress bar is determinate.
     *
     * @param newValue true if the progress bar should change to indeterminate
     * mode; false if it should revert to normal.
     */
    public void setProgressIndeterminate(boolean newValue) {

        progressBar.setIndeterminate(newValue);
    }

    /**
     * Sets the progress bar's maximum value.
     *
     * @param value the new maximum
     */
    public void setProgressMaximum(int value) {

        progressBar.setMaximum(value);
    }

    /**
     * Sets the progress bar's minimum value.
     *
     * @param value the new minimum
     */
    public void setProgressMinimum(int value) {

        progressBar.setMinimum(value);
    }

    /**
     * Sets the progress bar's current value.
     *
     * @param value the new value
     */
    public void setProgressValue(int value) {

        progressBar.setValue(value);
    }

    /**
     * Sets the progress bar's StringPainted value.
     *
     * @param value true if the progress bar should render a string
     */
    public void setProgressStringPainted(boolean value) {

        progressBar.setStringPainted(value);
    }

    /**
     * Sets the progress to a finished state.
     */
    public void setProgressFinished() {

        setProgressNote(null);
        setProgressValue(0);
        setProgressIndeterminate(false);

    }

    /**
     * Sets the starting date value.
     *
     * @param value starting date value (UTC TimeZone)
     */
    public void setStartDate(Date value) {

        controlPane.setStartDate(value);
    }

    /**
     * Sets the ending date value.
     *
     * @param value ending date value (UTC TimeZone)
     */
    public void setEndDate(Date value) {

        controlPane.setEndDate(value);
    }

    /**
     * Sets the resolution value.
     *
     * @param value new resolution value
     */
    public void setResolution(int value) {

        controlPane.setResolution(value);
    }

    /**
     * Sets the tracing option.
     *
     * @param value new tracing option value
     */
    public void setTracing(boolean value) {

        ControlPanel.setTracing(value);
    }

    /**
     * Sets the CoordinateSystem option.
     *
     * @param value new CoordinateSystem value
     */
    public void setCoordinateSystem(CoordinateSystem value) {

        controlPane.setCoordinateSystem(value);
    }

    /**
     * Sets the specified satellites to be selected for graphing.
     *
     * @param sats names of satellites to be selected for graphing
     */
    public void setSatellitesSelected(List< String> sats) {

        List< String> sscSats = new ArrayList<String>(sats.size());
        // SSC IDs of selected
        // satellites
        for (String sat : sats) {

            String sscId = spaseToSscMap.get(sat);
            // SSC ID corresponding to the
            // given SPASE ResourceID
            if (sscId != null) {

                sscSats.add(sscId);
            } else { // already an SSC id

                sscSats.add(sat);
            }
        }

        String[] sscSatArray = new String[sscSats.size()];
        // array version of sscSats
        satModel.setSelected(sscSats.toArray(sscSatArray));
    }

    /**
     * Sets all selections to their default values.
     */
    public void setDefaultValues() {

        controlPane.setDefaultStartDate();
        controlPane.setDefaultEndDate();
        controlPane.setDefaultResolution();
        controlPane.setDefaultCoordinateSystem(ControlPanel.getCentralBody().toString());
        ControlPanel.setDefaultTracing();

        satModel.clearAllSelected();
    }

    /**
     * Initiates a graphing action as if the user "clicked" on the graph button.
     *
     * @return true if graphing action was initiated. false if a graphing action
     * was already in progress at the time of this call.
     */
    public boolean performGraphAction() {

        if (orbButton.getText().equals(UPDATE)) {

            orbButton.doClick();
            return true;
        } else {

            return false;
        }
    }

    /**
     * Initiates a cancel action as if the user "clicked" on the cancel button.
     *
     * @return true if cancel action was initiated. false if no graphing action
     * was in progress at the time of this call.
     */
    public boolean performCancelAction() {

        if (orbButton.getText().equals(CANCEL)) {

            orbButton.doClick();
            return true;
        } else {

            return false;
        }
    }

    public void toggleButton() {

        if (orbButton.getText().equalsIgnoreCase(UPDATE) && progressBar.isIndeterminate() == true) {
            orbButton.setText(CANCEL);
        } else {
            resetOrbButton();
        }
    }

    public void resetOrbButton() {

        orbButton.setText(UPDATE);

    }

    private class GetOrbListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            Vector listeners = (Vector) graphSpecifiedListeners.clone();
            // a copy of the listeners

            GraphSpecifiedListener listener;
            // a specific listener

            try {

                if (e.getActionCommand().equals(UPDATE)) {

                    //  if (!satModel.groupAvailable()) {
                    //     return;
                    //  }
                    SatelliteGraphProperties[] selectedSatelliteGraphProperties;

                    if (ControlPanel.isSolenocentric()) {

                        selectedSatelliteGraphProperties = new SatelliteGraphProperties[satModel.getSelectedSatelliteGraphProperties().length + 1];
                        selectedSatelliteGraphProperties[0] = satModel.getSelenoSatelliteGraphProperties();
                        System.arraycopy(satModel.getSelectedSatelliteGraphProperties(), 0, selectedSatelliteGraphProperties, 1, satModel.getSelectedSatelliteGraphProperties().length);

                    } else {
                        selectedSatelliteGraphProperties
                                = satModel.getSelectedSatelliteGraphProperties();
                        // the graph properties selected by
                        //  the user
                    }
                    Date startDate = controlPane.getStartDate();
                    // the starting date that the user
                    //  has entered

                    Date endDate = controlPane.getEndDate();
                    // the ending date that the user has
                    //  entered

                    GraphSpecifiedEvent event
                            = controlPane.getCoordinateSystem() instanceof gov.nasa.gsfc.spdf.helio.client.CoordinateSystem
                                    ? new GraphSpecifiedEvent(SelectionWindow.this,
                                            selectedSatelliteGraphProperties,
                                            startDate, endDate,
                                            controlPane.getResolution(),
                                            (gov.nasa.gsfc.spdf.helio.client.CoordinateSystem) controlPane.getCoordinateSystem(),
                                            false)
                                    : new GraphSpecifiedEvent(SelectionWindow.this,
                                            selectedSatelliteGraphProperties,
                                            startDate, endDate,
                                            controlPane.getResolution(),
                                            (CoordinateSystem) controlPane.getCoordinateSystem(),
                                            ControlPanel.getTracing());

                    // the event that is to be sent to
                    //  listeners
                    for (int i = 0; i < listeners.size(); i++) {

                        listener = (GraphSpecifiedListener) listeners.elementAt(i);
                        listener.graphSpecified(event);
                    }

                    toggleButton();
                } else {

                    toggleButton();

                    for (int i = 0; i < listeners.size(); i++) {

                        listener = (GraphSpecifiedListener) listeners.elementAt(i);
                        listener.graphCancel();
                    }
                    setProgressFinished();
                }

            } catch (Exception ex) {

                setProgressFinished();
                resetOrbButton();

            }

        }
    }
}
