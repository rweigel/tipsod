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
 * $Id: GroundStationsTableModel.java,v 1.4 2015/10/30 14:18:50 rchimiak Exp $
 * Created on September 20, 2007, 9:37 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rachimiak
 */
public class GroundStationsTableModel extends DefaultTableModel {

    /**
     * Creates a new instance of GroundStationsTableModel.
     */
    public GroundStationsTableModel(Object[][] rows, String[] columnNames) {

        super(rows, columnNames);
    }

    @Override
    public Class getColumnClass(int column) {

        if (column == 0) {
            return Boolean.class;
        } else {
            return getValueAt(0, column).getClass();
        }
    }

    @Override
    public int getRowCount() {

        return getDataVector().size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int vColIndex) {

        return vColIndex == 0 ? true : false;
    }

    @Override
    public Object getValueAt(int row, int column) {

        return ((Vector) getDataVector().elementAt(row)).elementAt(column);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        if (col == 0) {

            ((Vector) getDataVector().get(row)).setElementAt(value, col);
        }

        fireTableCellUpdated(row, col);
    }
}
