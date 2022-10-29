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
 * $Id: SatelliteGraphPropertiesTable.java,v 1.20 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ToolTipManager;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;

public class SatelliteGraphPropertiesTable extends JTable {

    /**
     * Constructs an editable table of satellite graph properties.
     *
     * @param satGraphModel the table model
     */
    public SatelliteGraphPropertiesTable(
            SatelliteGraphTableModel satGraphModel) {

        super(satGraphModel);

        TableColumnModel tcm = getColumnModel();

        tcm.getColumn(0).setPreferredWidth(30);
        tcm.getColumn(1).setPreferredWidth(160);
        tcm.getColumn(2).setPreferredWidth(100);
        tcm.getColumn(3).setPreferredWidth(100);
        tcm.getColumn(4).setPreferredWidth(105);

        tcm.getColumn(0).setIdentifier("checkboxes");
        tcm.getColumn(1).setIdentifier("names");
        tcm.getColumn(2).setIdentifier("color");
        tcm.getColumn(3).setIdentifier("shape");
        tcm.getColumn(4).setIdentifier("pattern");

        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(6000);

        setDefaultRenderer(Color.class, new ColorTableCellRenderer(true));
        final ColorEditor colorEditor = new ColorEditor();
        setDefaultEditor(Color.class, colorEditor);
        setRowSelectionAllowed(false);

        TableColumn shapeColumn = getColumnModel().getColumn(3);
        JComboBox shapeComboBox = new JComboBox();
        shapeComboBox.addItem(SatelliteGraphShape.SPHERE);
        shapeComboBox.addItem(SatelliteGraphShape.CUBE);
        shapeComboBox.addItem(SatelliteGraphShape.CONE);
        shapeComboBox.addItem(SatelliteGraphShape.CYLINDER);
        shapeComboBox.addItem(SatelliteGraphShape.DIAMOND);
        shapeColumn.setCellEditor(new DefaultCellEditor(shapeComboBox));

        TableColumn patternColumn = getColumnModel().getColumn(4);
        JComboBox patternComboBox = new JComboBox();
        patternComboBox.addItem(LineStyle.SOLID);
        patternComboBox.addItem(LineStyle.DASH);
        patternComboBox.addItem(LineStyle.DOT);
        patternComboBox.addItem(LineStyle.DASH_DOT);
        patternComboBox.addItem(LineStyle.DASH_DOT_DOT_DOT);
        patternColumn.setCellEditor(new DefaultCellEditor(patternComboBox));

    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer,
            int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (c instanceof JComponent) {

            JComponent jc = (JComponent) c;

            if (vColIndex == this.getColumnModel().
                    getColumnIndex("names")) {

                setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                jc.setToolTipText(((SatelliteGraphTableModel) this.getModel()).getTooltip(rowIndex));
            } else {

                setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            //  this.setSelectionBackground(this.getBackground());
        }
        return c;
    }
}
