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
 * $Id: SatellitePositionTableModel.java,v 1.17 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on October 23, 2002, 2:47 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;

/**
 * The SatellitePositionTableModel class implements the data model for the table
 * included in the position window
 *
 * @author rchimiak
 * @version $Revision: 1.17 $
 */
public class SatellitePositionTableModel extends DefaultTableModel {

    private String[] columnNames = {"Satellite", "Color",
        "X", "Y", "Z",
        "Radius", "Latitude", "Longitude",
        "Magnetopause", "Bowshock", "Neutral Sheet",
        "North Lat", "North Long", "South Lat",
        "South Long", "Closest Lat", "Closest Long"};
    private SatellitePosition[] satPosition = null;

    /**
     * Creates the default model for a position table, with no rows or columns.
     */
    public SatellitePositionTableModel() {

        super();
    }

    /**
     * Creates a model with the list of displayed satellites in the first column
     * and 0,0,0 for the X,Y, and Z coordinates.
     *
     * @param satNames a string array consisting of the selected satellites
     */
    public SatellitePositionTableModel(String[] satNames) {

        this();
        satPosition = new SatellitePosition[satNames.length];

        for (int i = 0; i < satNames.length; i++) {
            satPosition[i] = new SatellitePosition(satNames[i]);
        }
    }

    /**
     * Creates a model with rows of satellite name, X,Y, and Z coordinates at
     * orbit initial time.
     *
     * @param satNames a string array containing the selected satellites
     * @param position a 2 dimensional array with rows of selected satellites
     * and their corresponding initial position coordinates
     */
    public SatellitePositionTableModel(String[] satNames, SatelliteGraphProperties[] properties, double[][] position, double[][] distance) {

        this();
        satPosition = new SatellitePosition[satNames.length];

        for (int i = 0; i < satNames.length; i++) {
            if (satNames[i] != null && properties[i] != null) {
                satPosition[i] = new SatellitePosition(satNames[i], properties[i], position[i][0],
                        position[i][1], position[i][2],
                        distance[i][0], distance[i][1], distance[i][2]);
            }
        }
    }

    /**
     * Returns the number of columns in this model. Should be 4, (satellite,
     * X,Y, and Z coordinates).
     *
     * @return the number of Columns
     *
     */
    @Override
    public int getColumnCount() {

        return columnNames.length;
    }

    /**
     * Returns the number of rows which is the number of selected satellites.
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount() {

        if (satPosition != null) {
            return satPosition.length;
        } else {
            return 0;
        }
    }

    public int getRow(String name) {

        for (int i = 0; i < satPosition.length; i++) {

            if (satPosition[i].getDisplayName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return 0;
    }

    public SatellitePosition[] getSatPosition() {

        return satPosition;
    }

    @Override
    public java.lang.Object getValueAt(int row, int col) {

        switch (col) {

            case SatellitePositionWindow.Column.SATELLITE:

                return (satPosition[row].getDisplayName());

            case SatellitePositionWindow.Column.COLOR:

                return (satPosition[row].getColor());

            case SatellitePositionWindow.Column.X:
            case SatellitePositionWindow.Column.Y:
            case SatellitePositionWindow.Column.Z:

                return (satPosition[row].getPosition()[col - SatellitePositionWindow.Column.X]);

            case SatellitePositionWindow.Column.RADIUS:
            case SatellitePositionWindow.Column.LATITUDE:
            case SatellitePositionWindow.Column.LONGITUDE:

                return (Footpoint.CartesianToSpherical(satPosition[row].getPosition())[col - SatellitePositionWindow.Column.RADIUS]);

            case SatellitePositionWindow.Column.MAGNETOPAUSE:
            case SatellitePositionWindow.Column.BOWSHOCK:
            case SatellitePositionWindow.Column.NEUTRAL_SHEET:

                if (String.valueOf(satPosition[row].getDistance(
                        this.getColumnName(col))).equalsIgnoreCase("NaN")) {
                    return null;
                }

                return (satPosition[row].getDistance(
                        this.getColumnName(col)));

            case SatellitePositionWindow.Column.NFLAT:
            case SatellitePositionWindow.Column.NFLON:
            case SatellitePositionWindow.Column.SFLAT:
            case SatellitePositionWindow.Column.SFLON:
            case SatellitePositionWindow.Column.CFLAT:
            case SatellitePositionWindow.Column.CFLON:

                if (String.valueOf(satPosition[row].getFootpoint(
                        col - SatellitePositionWindow.Column.NFLAT)).equalsIgnoreCase("NaN")) {
                    return null;
                }

                return (satPosition[row].getFootpoint(
                        col - SatellitePositionWindow.Column.NFLAT));

            default:

                return null;
        }
    }

    @Override
    public Class getColumnClass(int col) {

        if (col > 1) {
            return Double.class;
        } else {
            return getValueAt(0, col).getClass();
        }
    }

    @Override
    public String getColumnName(int col) {

        if (col < getColumnCount()) {

            return columnNames[col];
        }
        return "";
    }

    public int getColumnIndex(String s) {

        for (int i = 0; i < columnNames.length; i++) {

            if (columnNames[i].equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        try {

            switch (col) {

                case SatellitePositionWindow.Column.SATELLITE:

                    satPosition[row].setName(value.toString());
                    break;

                case SatellitePositionWindow.Column.COLOR:

                    satPosition[row].setColor((Color) value);
                    break;

                case SatellitePositionWindow.Column.X:
                case SatellitePositionWindow.Column.Y:
                case SatellitePositionWindow.Column.Z:

                    satPosition[row].setPosition(col - 2, ((Double) value));
                    break;

                case SatellitePositionWindow.Column.MAGNETOPAUSE:
                case SatellitePositionWindow.Column.BOWSHOCK:
                case SatellitePositionWindow.Column.NEUTRAL_SHEET:

                    satPosition[row].setDistance(col - 8, ((Double) value));
                    break;

                case SatellitePositionWindow.Column.NFLAT:
                case SatellitePositionWindow.Column.NFLON:
                case SatellitePositionWindow.Column.SFLAT:
                case SatellitePositionWindow.Column.SFLON:
                case SatellitePositionWindow.Column.CFLAT:
                case SatellitePositionWindow.Column.CFLON:

                    satPosition[row].setFootpoint(col - 11, ((Double) value));
                    break;

                default:

                    break;
            }
            fireTableCellUpdated(row, col);
        } catch (Exception e) {
        }
    }

    /**
     * Clears the rows and columns relating to a previous display of selected
     * satellites, to get ready for the new display.
     */
    public void clear() {

        if (satPosition != null) {

            for (int i = 0; i < satPosition.length; i++) {

                for (int j = 0; j < columnNames.length; j++) {
                    setValueAt(null, i, j);
                }
            }
        }
    }

    /**
     * The Satellite Position class implements the structure of a satellite
     * model row with some associated methods.
     */
    private class SatellitePosition {

        private String name = null;
        private String displayName = null;
        private Color color = null;
        private final double[] position = new double[3];
        private final double[] distance = new double[3];
        private final double[] footpoints = new double[6];

        /**
         * Builds a satellite position object.
         *
         * @param name, the satellite name
         * @param X, the initial X coordinate for that satellite
         * @param Y, the initial Y coordinate for that satellite
         * @param Z, the initial Z coordinate for that satellite
         */
        public SatellitePosition(String name, SatelliteGraphProperties properties,
                double X, double Y, double Z,
                double bsDistance, double magDistance, double neutDistance) {

            this.name = name;
            this.displayName = properties.getDisplayName();
            this.color = properties.getColor();
            this.position[0] = X;
            this.position[1] = Y;
            this.position[2] = Z;
            this.distance[0] = magDistance;
            this.distance[1] = bsDistance;
            this.distance[2] = neutDistance;

            this.footpoints[0] = 0;
            this.footpoints[1] = 0;
            this.footpoints[2] = 0;
            this.footpoints[3] = 0;
            this.footpoints[4] = 0;
            this.footpoints[5] = 0;
        }

        /**
         * Builds a satellitePosition instance with a satellite name and a value
         * of 0 for each coordinate.
         *
         * @param name, the name of the satellite to be displayed as a row in
         * the position table.
         */
        public SatellitePosition(String name) {

            this(name, new SatelliteGraphProperties(name),
                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f);
        }

        public String getName() {
            return this.name;
        }

        public void setName(String satName) {
            name = satName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public void setDisplayName(String satDisplayName) {
            displayName = satDisplayName;
        }

        public Color getColor() {
            return this.color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public double[] getPosition() {
            return this.position;
        }

        public void setPosition(double[] satPosition) {

            position[0] = satPosition[0];
            position[1] = satPosition[1];
            position[2] = satPosition[2];
        }

        public void setPosition(int i, double satCoord) {

            position[i] = satCoord;
        }

        public double getFootpoint(int i) {
            return this.footpoints[i];
        }

        public void setFootpoint(int i, double footpoint) {

            footpoints[i] = footpoint;
        }

        public void setFootpoint(double[] footpoints) {

            this.footpoints[0] = footpoints[0];
            this.footpoints[1] = footpoints[1];
            this.footpoints[2] = footpoints[2];
            this.footpoints[3] = footpoints[3];
            this.footpoints[4] = footpoints[4];
            this.footpoints[5] = footpoints[5];
        }

        public double getDistance(String columnName) {

            if (columnName.equalsIgnoreCase("Magnetopause")) {
                return distance[0];
            } else if (columnName.equalsIgnoreCase("BowShock")) {
                return distance[1];
            } else if (columnName.equalsIgnoreCase("Neutral Sheet")) {
                return distance[2];
            }

            return Double.NaN;
        }

        public void setDistance(double[] satDistance) {

            distance[0] = satDistance[0];
            distance[1] = satDistance[1];
            distance[2] = satDistance[2];
        }

        public void setDistance(int i, double satDist) {

            if (new Double(satDist).compareTo(Double.NaN) == 0) {
                distance[i] = satDist;
            } else {

                distance[i] = (double) (Math.round((float) satDist * 10)) / 10d;

            }
        }
    }
}
