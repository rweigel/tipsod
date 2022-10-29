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
 * $Id: GroundStationsWindow.java,v 1.11 2015/10/30 14:18:50 rchimiak Exp $
 * Created on July 20, 2007, 9:46 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;
import gov.nasa.gsfc.spdf.ssc.client.GroundStationDescription;
import gov.nasa.gsfc.spdf.orb.content.shapes.GroundStations;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SwitchGroup;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.content.shapes.EarthShapeExtension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.media.j3d.Text3D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author rachimiak
 */
public class GroundStationsWindow extends JFrame {

    private GroundStations stationsShape = null;
    private GroundStationsTable table = null;
    private List< GroundStationDescription> stations
            = new ArrayList<GroundStationDescription>();

    /**
     * Creates a new instance of GroundStationsWindow
     */
    public GroundStationsWindow(List< GroundStationDescription> stations) {

        super();

        this.stations = stations;

        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(6000);

        if (stations.isEmpty()) {
            return;
        }

        String[] columnNames = {
            "", "Name", "Acronym", "Latitude", "Longitude"};

        Object[][] rows = new Object[stations.size()][columnNames.length];

        int i = 0;
        for (GroundStationDescription station : stations) {

            rows[i][0] = false;
            rows[i][1] = station.getName();
            rows[i][2] = station.getId();
            rows[i][3] = station.getLatitude();
            rows[i][4] = station.getLongitude();
            i++;
        }

        GroundStationsTableModel model = new GroundStationsTableModel(rows, columnNames);

        table = new GroundStationsTable(model);
        JScrollPane stationsPane = new JScrollPane(table);

        ListSelectionListener listSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                // If cell selection is enabled, both row and column change events are fired
                if (e.getSource() == table.getSelectionModel()
                        && table.getRowSelectionAllowed()) {

                    if (e.getValueIsAdjusting() || e.getFirstIndex() == -1) {
                        return;
                    }

                    for (int i = 0; i < table.getRowCount(); i++) {

                        SwitchGroup switchg = stationsShape.getSwitch(table.getValueAt(i, 2));

                        switchg.setChildMask(((Boolean) table.getValueAt(i, 0)) == true
                                ? switchg.getOptions()[0] : switchg.getOptions()[1]);
                    }
                    table.clearSelection();
                }
            }
        };
        table.getSelectionModel().
                addListSelectionListener(listSelectionListener);

        ItemListener itemListener = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (!table.getSelectAllChecbox().isSelected()) {

                    for (int i = 0; i < table.getRowCount(); i++) {

                        table.setValueAt(false, i, 0);
                    }
                    getStations().deselectAll();
                } else {

                    for (int i = 0; i < table.getRowCount(); i++) {

                        table.setValueAt(true, i, 0);
                    }
                    getStations().selectAll();
                }
            }
        };
        table.getSelectAllChecbox().addItemListener(itemListener);

        this.setTitle("Ground Stations");
        Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(scrnSize.width / 15, scrnSize.height / 15);

        Container contentPane = getContentPane();

        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.add(stationsPane);
        JLabel color = new JLabel("Color: ");

        color.setFont(Util.comicPlain14Font);

        final JButton colorButton = new JButton();

        final JColorChooser colorChooser = new JColorChooser();
        colorButton.setBackground(new Color(226, 26, 26));

        ActionListener colorListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                colorButton.setBackground(colorChooser.getColor());

                Vector listeners = (Vector) EarthShapeExtension.getEarthExtensionShapeSizeChangeListeners().clone();
                // a copy of the listeners

                EarthExtensionShapeColorChangeListener listener;
                // a specific listener

                for (int i = 0; i < listeners.size(); i++) {

                    listener = (EarthExtensionShapeColorChangeListener) listeners.elementAt(i);
                    //listener.color(new Color3f(colorButton.getBackground()));
                    listener.color(new Color3f(colorChooser.getColor()));
                }
            }
        };

        colorButton.addActionListener(colorListener);

        final JDialog dialog = JColorChooser.createDialog(colorButton,
                "Select a Color",
                true, colorChooser, colorListener, null);

        colorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                colorChooser.setColor(colorButton.getBackground());
                dialog.setVisible(true);
            }
        });

        JPanel arrowPanel = new JPanel(new GridLayout(2, 1));
        final BasicArrowButton downButton
                = new BasicArrowButton(SwingConstants.SOUTH) {

                    @Override
                    public Dimension getPreferredSize() {

                        return new Dimension(30, 17);
                    }
                };

        final BasicArrowButton upButton
                = new BasicArrowButton(SwingConstants.NORTH) {

                    @Override
                    public Dimension getPreferredSize() {

                        return new Dimension(30, 17);
                    }
                };

        arrowPanel.add(upButton);
        arrowPanel.add(downButton);

        JLabel labelDisplaySize = new JLabel("Size: ");
        labelDisplaySize.setFont(Util.comicPlain14Font);

        ActionListener displaySizeListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (stationsShape == null) {
                    return;
                }

                if (e.getSource() == upButton) {

                    EarthShapeExtension.setAdjust(EarthShapeExtension.getAdjust() * 1.1f);
                }

                if (e.getSource() == downButton) {

                    EarthShapeExtension.setAdjust(EarthShapeExtension.getAdjust() * 0.9f);

                }
                Vector listeners = (Vector) EarthShapeExtension.getEarthExtensionShapeSizeChangeListeners().clone();
                // a copy of the listeners

                EarthExtensionShapeSizeChangeListener listener;
                // a specific listener

                for (int i = 0; i < listeners.size(); i++) {

                    listener = (EarthExtensionShapeSizeChangeListener) listeners.elementAt(i);
                    listener.resize();
                }
            }
        };

        upButton.addActionListener(displaySizeListener);
        downButton.addActionListener(displaySizeListener);

        JLabel labelside = new JLabel("Stations Name on: ");
        labelside.setFont(Util.comicPlain14Font);

        String[] directionStrings = {"Right", "Left"};
        final JComboBox side = new JComboBox(directionStrings);

        side.setToolTipText("<html>Station names may be obscured by the Earth's 3D surface.<br>"
                + "Changing the side on which labels are displayed will generally help. </html>");

        side.setFont(Util.plainFont);

        TitledBorder sizeTitle = BorderFactory.createTitledBorder("Stations Display Features:");
        sizeTitle.setTitleFont(Util.labelFont);

        JPanel southPane = new JPanel();

        southPane.add(color);
        southPane.add(colorButton);
        southPane.add(Box.createHorizontalStrut(15));
        southPane.setBorder(sizeTitle);
        southPane.add(labelDisplaySize);
        southPane.add(arrowPanel);
        southPane.add(Box.createHorizontalStrut(15));
        southPane.add(labelside);
        southPane.add(side);

        mainPane.add(southPane, BorderLayout.SOUTH);
        ActionListener sideListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == side) {

                    stationsShape.reside(side.getSelectedIndex() == 0
                            ? Text3D.ALIGN_FIRST : Text3D.ALIGN_LAST);
                }
            }
        };
        side.addActionListener(sideListener);

        contentPane.add(mainPane);
        pack();
        setVisible(false);
    }

    public GroundStationsTable getTable() {

        return table;
    }

    public List< GroundStationDescription> getStationDescription() {

        return stations;
    }

    public GroundStations getStations() {

        if (stationsShape == null) {
            stationsShape = new GroundStations((GroundStationsTableModel) table.getModel());
        }

        return stationsShape;
    }
}
