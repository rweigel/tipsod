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
 * $Id: ColorEditor.java,v 1.7 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JColorChooser;
import javax.swing.JDialog;

/**
 * The ColorEditor class provides an editor for table cells that contain a Color
 * value.
 *
 * @version $Revision: 1.7 $
 * @author B. Harris
 */
public class ColorEditor extends DefaultCellEditor {

    /**
     * The cell's current Color value
     */
    private Color currentColor = null;

    /**
     * Constructs a table cell editor for a cell containing a Color value.
     */
    public ColorEditor() {

        super(new JCheckBox());

        final JButton button = new JButton("") {

            @Override
            public void setText(String s) {
                // Button never shows text -- only color.
            }
        };                             // a button to activate the color
        //  chooser dialog
        button.setBackground(Color.white);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        editorComponent = button;
        setClickCountToStart(1);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                fireEditingStopped();
            }
        });

        final JColorChooser colorChooser = new JColorChooser();

        ActionListener okListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                currentColor = colorChooser.getColor();
            }
        };                             // listener for dialog's OK button

        final JDialog dialog = JColorChooser.createDialog(button,
                "Select a Color",
                true, colorChooser,
                okListener, null);
        // the dialog with the color chooser

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                button.setBackground(currentColor);
                colorChooser.setColor(currentColor);
                // Without the following line, the dialog comes up
                // in the middle of the screen.
                // dialog.setLocationRelativeTo(button);
                dialog.setVisible(true);
            }
        });
    }

    @Override
    protected void fireEditingStopped() {

        super.fireEditingStopped();
    }

    @Override
    public Object getCellEditorValue() {

        return currentColor;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected,
            int row, int column) {

        ((JButton) editorComponent).setText(value.toString());
        currentColor = (Color) value;
        return editorComponent;
    }
}
