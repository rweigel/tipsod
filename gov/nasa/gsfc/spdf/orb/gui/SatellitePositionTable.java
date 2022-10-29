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
 * $Id: SatellitePositionTable.java,v 1.13 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on October 23, 2002, 2:45 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * The SatellitePositionTable implements the table displaying satellites and
 * corresponding position coordinates in the position window.
 *
 * @author rchimiak
 * @version $Revision: 1.13 $
 */
public class SatellitePositionTable extends JTable {

    /**
     * Creates a SatellitePositionTable and assigns the color and editor.
     *
     * @param model this table associated model
     */
    public SatellitePositionTable(SatellitePositionTableModel model) {

        super(model);

        setDefaultRenderer(Color.class, new ColorTableCellRenderer(true));
        setRowSelectionAllowed(false);
    }

    public void calcColumnWidths() {
        JTableHeader header = getTableHeader();
        TableCellRenderer defaultHeaderRenderer = null;

        if (header != null) {
            defaultHeaderRenderer = header.getDefaultRenderer();
        }

        TableColumnModel columns = getColumnModel();
        TableModel data = getModel();
        int margin = columns.getColumnMargin() + 15; // only JDK1.3
        int rowCount = data.getRowCount();
        int totalWidth = 0;

        for (int i = columns.getColumnCount() - 1; i >= 0; --i) {
            TableColumn column = columns.getColumn(i);
            int columnIndex = column.getModelIndex();
            int width = -1;
            TableCellRenderer h = column.getHeaderRenderer();

            if (h == null) {
                h = defaultHeaderRenderer;
            }

            if (h != null) {
                Component c = h.getTableCellRendererComponent(this, column.getHeaderValue(),
                        false, false, -1, i);

                width = c.getPreferredSize().width;
            }
            for (int row = rowCount - 1; row >= 0; --row) {
                TableCellRenderer r = getCellRenderer(row, i);

                Component c = r.getTableCellRendererComponent(this,
                        data.getValueAt(row, columnIndex),
                        false, false, row, i);

                if (c != null) {
                    width = Math.max(width, c.getPreferredSize().width);
                }
            }
            if (width >= 0) {
                column.setPreferredWidth(width + margin);
            }

            totalWidth += column.getPreferredWidth();
            Dimension size = getPreferredScrollableViewportSize();

            size.width = totalWidth;

            size.height = 100;
            setPreferredScrollableViewportSize(size);
        }
    }

    // This method adds a new column to table without reconstructing
    // all the other columns.
    public void addColumns(String s) {

        SatellitePositionTableModel model = (SatellitePositionTableModel) getModel();

        if (model.getColumnIndex(s) > -1) {
            this.addColumn(new TableColumn(model.getColumnIndex(s)));
        }

    }

    // This method remove a new column to table without reconstructing
    // all the other columns.
    public void removeColumns(String s) {

        try {

            removeColumn(getColumn(s));
        } catch (IllegalArgumentException iae) {
        }
    }
}
