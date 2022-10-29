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
 * $Id: ColorTableCellRenderer.java,v 1.6 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Component;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.Border;

/**
 * This class provides a table cell renderer for cells that contain a Color
 * value.
 *
 * @version $Revision: 1.6 $
 * @author B. Harris
 */
public class ColorTableCellRenderer
        extends JLabel
        implements TableCellRenderer {

    /**
     * The border to use when the cell is not selected.
     */
    private Border unselectedBorder = null;
    /**
     * The border to use when the cell is selected.
     */
    private Border selectedBorder = null;
    /**
     * Whether the cell is to have a border.
     */
    private boolean isBordered = true;

    /**
     * Constructs a TableCellRenderer for cells that contain a Color value.
     *
     * @param isBordered specifies whether the cell is to have a border
     */
    public ColorTableCellRenderer(boolean isBordered) {

        this.isBordered = isBordered;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object color,
            boolean isSelected,
            boolean hasFocus,
            int row, int column) {

        setBackground((Color) color);
        if (isBordered) {

            if (isSelected) {

                if (selectedBorder == null) {

                    selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                            table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {

                if (unselectedBorder == null) {

                    unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                            table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        return this;
    }
}
