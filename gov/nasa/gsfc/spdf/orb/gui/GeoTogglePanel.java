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
 * $Id: GeoTogglePanel.java,v 1.5 2018/06/05 16:48:10 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

/**
 *
 * @author rchimiak
 */
public class GeoTogglePanel extends TogglePanel {

    public enum Element {

        ORBIT,
        EARTH,
        AXIS,
        AXIS_TXT,
        PLANAR_VIEWS,
        TOOLBAR,
        MAGNETOPAUSE,
        BOWSHOCK,
        NEUTRAL_SHEET,
        XYGRID,
        YZGRID,
        XZGRID,
        FOOTPOINTS_NORTH,
        FOOTPOINTS_SOUTH,
        FOOTPOINTS_CLOSEST,
        GEO_GRID,
        GROUND_STATIONS,
        SUNLIGHT;

    }

    private final static JCheckBox[] checkBoxArray = new JCheckBox[Element.values().length];

    private final static int rows = 3;
    private final static int columns = 5;

    public GeoTogglePanel() {

        super(rows, columns);

        checkBoxArray[Element.ORBIT.ordinal()] = new JCheckBox("Orbits");
        checkBoxArray[Element.EARTH.ordinal()] = new JCheckBox("Earth");
        checkBoxArray[Element.AXIS.ordinal()] = new JCheckBox("Axis");
        checkBoxArray[Element.AXIS_TXT.ordinal()] = new JCheckBox("Labels");
        checkBoxArray[Element.PLANAR_VIEWS.ordinal()] = new JCheckBox("Planar Views");
        checkBoxArray[Element.TOOLBAR.ordinal()] = new JCheckBox("Animation Bar");
        checkBoxArray[Element.MAGNETOPAUSE.ordinal()] = new JCheckBox("Magnetopause");
        checkBoxArray[Element.BOWSHOCK.ordinal()] = new JCheckBox("Bowshock");
        checkBoxArray[Element.NEUTRAL_SHEET.ordinal()] = new JCheckBox("Neutral Sheet");
        checkBoxArray[Element.XYGRID.ordinal()] = new JCheckBox("xy Plane");
        checkBoxArray[Element.YZGRID.ordinal()] = new JCheckBox("zy Plane");
        checkBoxArray[Element.XZGRID.ordinal()] = new JCheckBox("xz Plane");
        checkBoxArray[Element.FOOTPOINTS_NORTH.ordinal()] = new JCheckBox("Footpoints North");
        checkBoxArray[Element.FOOTPOINTS_SOUTH.ordinal()] = new JCheckBox("Footpoints South");
        checkBoxArray[Element.FOOTPOINTS_CLOSEST.ordinal()] = new JCheckBox("Footpoints Closest");
        checkBoxArray[Element.GEO_GRID.ordinal()] = new JCheckBox("Geographic Grid");
        checkBoxArray[Element.GROUND_STATIONS.ordinal()] = new JCheckBox("Ground Stations");
        checkBoxArray[Element.SUNLIGHT.ordinal()] = new JCheckBox("Sunlight");

        for (JCheckBox checkBoxArray1 : checkBoxArray) {
            checkBoxArray1.setBackground(OrbitViewer.BEIGE);
            add(checkBoxArray1);
        }

        for (Element e : Element.values()) {
            checkBoxArray[e.ordinal()].setEnabled(false);
            if (e.equals(Element.ORBIT)
                    || e.equals(Element.EARTH)
                    || e.equals(Element.AXIS)
                    || e.equals(Element.AXIS_TXT)
                    || e.equals(Element.TOOLBAR)) {
                checkBoxArray[e.ordinal()].setSelected(true);
            } else {
                checkBoxArray[e.ordinal()].setSelected(false);
            }
        }

        if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")
                && Util.getVersion() < 1.7d) {

            checkBoxArray[Element.PLANAR_VIEWS.ordinal()].setSelected(false);
            checkBoxArray[Element.PLANAR_VIEWS.ordinal()].setEnabled(false);
            checkBoxArray[Element.SUNLIGHT.ordinal()].setSelected(false);
            checkBoxArray[Element.SUNLIGHT.ordinal()].setEnabled(false);
        }
        /**
         * The listener class for receiving checkBox events to be processed
         */
        class CheckBoxListener implements ItemListener {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Object source = e.getItemSelectable();

                int i = 0;
                while (source != checkBoxArray[i]) {
                    i++;
                }

                SatBranch satB = ContentBranch.getSatBranch();

                if (satB != null) {

                    if (i == Element.ORBIT.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.ORBIT));
                    }

                    if (i == Element.EARTH.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.EARTH));
                    } else if (i == Element.AXIS.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.AXIS));
                    } else if (i == Element.AXIS_TXT.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.AXIS_TXT));
                    } else if (i == Element.MAGNETOPAUSE.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.MAGNETOPAUSE));

                        OrbitViewer.getMagnetopauseWindow().setVisible((e.getStateChange() == ItemEvent.SELECTED));
                    } else if (i == Element.BOWSHOCK.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.BOWSHOCK));
                        OrbitViewer.getBowshockWindow().setVisible((e.getStateChange() == ItemEvent.SELECTED));
                    } else if (i == Element.PLANAR_VIEWS.ordinal()) {

                        OrbitViewer.getPlanarPanel().setVisible(
                                (e.getStateChange() == ItemEvent.SELECTED));
                        if (satB.getZoom() != null) {
                            satB.getZoom().getPlanarPanel().setVisible(e.getStateChange() == ItemEvent.SELECTED);
                        }
                        
                    } else if (i == Element.TOOLBAR.ordinal()) {

                        OrbitViewer.getToolBar().setVisible(
                                (e.getStateChange() == ItemEvent.SELECTED));
                    } else if (i == Element.XYGRID.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.XYGRID));
                    } else if (i == Element.YZGRID.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.YZGRID));
                    } else if (i == Element.XZGRID.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.XZGRID));
                    } else if (i == Element.NEUTRAL_SHEET.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.NEUTRAL_SHEET));

                        OrbitViewer.getNeutralSheetWindow().setVisible((e.getStateChange() == ItemEvent.SELECTED));
                    } else if (i == Element.SUNLIGHT.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.SUNLIGHT));

                        ContentBranch.getEarthGroup().
                                getEarth().setSunLight((e.getStateChange() == ItemEvent.SELECTED));

                        ContentBranch.getEarthSurfaces().
                                getNeutralSheet().
                                setSunLight((e.getStateChange() == ItemEvent.SELECTED));

                        ContentBranch.getEarthSurfaces().
                                getMagnetopause().
                                setSunLight((e.getStateChange() == ItemEvent.SELECTED));

                        ContentBranch.getEarthSurfaces().
                                getBowshock().
                                setSunLight((e.getStateChange() == ItemEvent.SELECTED));
                    } else if (i == Element.FOOTPOINTS_NORTH.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.FOOTPOINTS_NORTH));

                        if (e.getStateChange() == ItemEvent.SELECTED) {

                            checkBoxArray[Element.FOOTPOINTS_CLOSEST.ordinal()].setEnabled(false);
                        } else if (!checkBoxArray[Element.FOOTPOINTS_SOUTH.ordinal()].isSelected()) {

                            checkBoxArray[Element.FOOTPOINTS_CLOSEST.ordinal()].setEnabled(true);
                        }
                    } else if (i == Element.FOOTPOINTS_SOUTH.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.FOOTPOINTS_SOUTH));

                        if (e.getStateChange() == ItemEvent.SELECTED) {

                            checkBoxArray[Element.FOOTPOINTS_CLOSEST.ordinal()].setEnabled(false);
                        } else if (!checkBoxArray[Element.FOOTPOINTS_NORTH.ordinal()].isSelected()) {

                            checkBoxArray[Element.FOOTPOINTS_CLOSEST.ordinal()].setEnabled(true);
                        }
                    } else if (i == Element.FOOTPOINTS_CLOSEST.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.FOOTPOINTS_CLOSEST));

                        if (e.getStateChange() == ItemEvent.SELECTED) {

                            checkBoxArray[Element.FOOTPOINTS_NORTH.ordinal()].setEnabled(false);
                            checkBoxArray[Element.FOOTPOINTS_SOUTH.ordinal()].setEnabled(false);
                        } else {

                            checkBoxArray[Element.FOOTPOINTS_NORTH.ordinal()].setEnabled(true);
                            checkBoxArray[Element.FOOTPOINTS_SOUTH.ordinal()].setEnabled(true);
                        }
                    } else if (i == Element.GEO_GRID.ordinal()) {
                        satB.setMask(i, getCheckBox(Element.GEO_GRID));
                    } else if (i == Element.GROUND_STATIONS.ordinal()) {

                        satB.setMask(i, getCheckBox(Element.GROUND_STATIONS));
                        OrbitViewer.getGroundStationsWindow().setVisible((e.getStateChange() == ItemEvent.SELECTED));
                    }
                }
            }
        }
        for (JCheckBox checkBoxArray1 : checkBoxArray) {
            checkBoxArray1.addItemListener(new CheckBoxListener());
        }

    }

    /**
     * Returns the group of check boxes corresponding to the selection and
     * consequent display of specific items on the scene-graph.
     *
     * @return an array of the check boxes on the toggle panel
     */
    public static JCheckBox[] getCheckBoxArray() {
        return checkBoxArray;
    }

    public static JCheckBox getCheckBox(Element e) {
        return checkBoxArray[e.ordinal()];
    }

    public static int getPosition(Element e) {
        return e.ordinal();

    }

}
