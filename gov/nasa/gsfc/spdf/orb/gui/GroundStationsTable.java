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
 * $Id: GroundStationsTable.java,v 1.5 2015/10/30 14:18:50 rchimiak Exp $
 * Created on September 20, 2007, 9:36 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.Comparator;
import java.util.Collections;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author rachimiak
 */
public class GroundStationsTable extends JTable {

    private JCheckBox selectAllCheckbox = new JCheckBox();

    /**
     * Creates a new instance of GroundStationsTable.
     */
    public GroundStationsTable(GroundStationsTableModel model) {

        super(model);
        JTableHeader header = getTableHeader();
        // Disable autoCreateColumnsFromModel otherwise all the column customizations
        // and adjustments will be lost when the model data is sorted
        setAutoCreateColumnsFromModel(false);
        // this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        header.addMouseListener(new ColumnHeaderListener());

        getColumnModel().getColumn(0).setHeaderValue(null);

        getColumnModel().getColumn(0).setHeaderRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                //Attach your action check box listeners here
                super.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);
                JPanel p = new JPanel();
                p.add(selectAllCheckbox);
                p.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

                return p;
            }
        });
    }

    // Regardless of sort order (ascending or descending), null values always appear last.
    // colIndex specifies a column in model.
    public void sortColumn(DefaultTableModel model, int colIndex, boolean ascending) {
        // Sort all the rows in descending order based on the
        // values in the second column of the model
        sortAllRowsBy(model, colIndex, true);
    }

    // Regardless of sort order (ascending or descending), null values always appear last.
    // colIndex specifies a column in model.
    @SuppressWarnings("unchecked")
    public void sortAllRowsBy(DefaultTableModel model, int colIndex, boolean ascending) {

        Vector data = model.getDataVector();

        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
    }

    // This comparator is used to sort vectors of data
    public class ColumnSorter implements Comparator {

        int colIndex;
        boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Object a, Object b) {
            Vector v1 = (Vector) a;
            Vector v2 = (Vector) b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);

            // Treat empty strains like nulls
            if (o1 instanceof String && ((String) o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String) o2).length() == 0) {
                o2 = null;
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable) o1).compareTo(o2);
                } else {
                    return ((Comparable) o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    }

    public class ColumnHeaderListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {

            JTable table = ((JTableHeader) evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();

            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            int mColIndex = table.convertColumnIndexToModel(vColIndex);

            if (vColIndex == 0) {

                selectAllCheckbox.doClick();
            } else {

                sortColumn(((DefaultTableModel) getModel()), mColIndex, true);
                // Return if not clicked on any column header
                if (vColIndex == -1) {
                    return;
                }
            }
            // Determine if mouse was clicked between column heads
            Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
            if (vColIndex == 0) {
                headerRect.width -= 3;    // Hard-coded constant
            } else {
                headerRect.grow(-3, 0);   // Hard-coded constant
            }
            if (!headerRect.contains(evt.getX(), evt.getY())) {
                // Mouse was clicked between column heads
                // vColIndex is the column head closest to the click

                // vLeftColIndex is the column head to the left of the click
                int vLeftColIndex = vColIndex;
                if (evt.getX() < headerRect.x) {
                    vLeftColIndex--;
                }
            }
        }
    }

    public JCheckBox getSelectAllChecbox() {

        return selectAllCheckbox;
    }
}
