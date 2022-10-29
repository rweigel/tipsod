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
 * $Id: SurfaceWindow.java,v 1.22 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on December 4, 2002, 1:13 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.util.Vector;
import javax.vecmath.Color4f;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.utils.MHD;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Base window for the surface area windows (magnetopause and bowshock windows).
 * This class will never be instanciated
 *
 * @author rchimiak
 * @version $Revision: 1.22 $
 */
public abstract class SurfaceWindow extends JFrame implements SwpChangeListener {

    protected ButtonGroup radioGroup = new ButtonGroup();
    protected JButton colorButton = new JButton();
    protected JTextField swp = new JTextField();
    protected JTextField tail = new JTextField();
    protected JSlider opacitySlider = new JSlider(0, 100, 20);
    protected JRadioButton[] radioButtons = new JRadioButton[3];
    protected MHD model = null;
    protected Color4f color = null;
    //   private static Vector < SwpChangeListener > swpChangeListeners =
    //           new Vector  < SwpChangeListener > ();

    /**
     * Creates a new instance of SurfaceWindow
     */
    public SurfaceWindow() {

        super();

        Container contentPane = getContentPane();

        contentPane.setLayout(new GridLayout(3, 1, 10, 10));

        JPanel colorPanel = new JPanel(new GridLayout(2, 1));
        JLabel labelColor = new JLabel("Color: ");

        Font oldfont = labelColor.getFont();
        Font font = new Font(oldfont.getName(), Font.PLAIN, oldfont.getSize());

        labelColor.setFont(Util.labelFont);
        colorPanel.add(labelColor);
        colorPanel.add(colorButton);

        final JColorChooser colorChooser = new JColorChooser();

        ActionListener okListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                colorButton.setBackground(colorChooser.getColor());
                if (model != null) {
                    model.setColor(new Color4f(colorChooser.getColor()));
                }
            }
        };

        final JDialog dialog = JColorChooser.createDialog(colorButton,
                "Select a Color",
                true, colorChooser, okListener, null);

        colorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                colorChooser.setColor(colorButton.getBackground());
                dialog.setVisible(true);
            }
        });

        tail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                model.setMin(Float.parseFloat(tail.getText()));

            }
        });

        JPanel opacityPanel = new JPanel(new GridLayout(2, 1));
        JLabel opacity = new JLabel("Opacity: ");
        opacity.setFont(Util.labelFont);
        opacityPanel.add(opacity);
        opacitySlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        opacityPanel.add(opacitySlider);

        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new GridLayout(2, 1, 5, 5));
        firstPanel.add(colorPanel);
        firstPanel.add(opacityPanel);
        contentPane.add(firstPanel);

        TitledBorder bFieldTitle = BorderFactory.createTitledBorder("B Field Model Parameters");
        bFieldTitle.setTitleFont(Util.labelFont);
        TitledBorder displayTitle = BorderFactory.createTitledBorder("Display Model Parameters");
        displayTitle.setTitleFont(Util.labelFont);

        JPanel params = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel SWPPanel = new JPanel(new GridLayout(1, 2, 3, 3));
        SWPPanel.setBorder(bFieldTitle);
        JLabel swpLabel = new JLabel("SWP (nP):");
        swpLabel.setFont(font);
        SWPPanel.add(swpLabel);

        swp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        swp.setText("2.04");
        swp.setColumns(5);
        swp.setEditable(true);
        swp.setBackground(new Color(255, 255, 255));
        SWPPanel.add(swp);

        addSwpChangeListener(this);

        swp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Vector listeners = (Vector) swpChangeListeners.clone();
                // a copy of the listeners

                SwpChangeListener listener;
                // a specific listener

                for (int i = 0; i < listeners.size(); i++) {

                    listener = (SwpChangeListener) listeners.elementAt(i);
                    listener.swpChanged(swp.getText());
                }
            }
        });

        JPanel tailPanel = new JPanel(new GridLayout(1, 2, 3, 3));
        tailPanel.setBorder(displayTitle);
        JLabel tailLabel = new JLabel("Min at (RE):");
        tailLabel.setFont(font);
        tailPanel.add(tailLabel);

        tail.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.white, Color.darkGray));
        tail.setText("-45");
        tail.setColumns(5);
        tail.setEditable(true);
        tail.setBackground(new Color(255, 255, 255));
        tailPanel.add(tail);

        params.add(SWPPanel);
        params.add(tailPanel);

        contentPane.add(params);
        JPanel radioPanel = new JPanel(new GridLayout(3, 1));

        radioButtons[0] = new JRadioButton("wireframe", true);
        radioButtons[1] = new JRadioButton("screen_door", false);
        radioButtons[2] = new JRadioButton("surface", false);

        for (JRadioButton radioButton : radioButtons) {
            radioGroup.add(radioButton);
            radioPanel.add(radioButton);
            radioButton.setFont(font);
        }

        TitledBorder radioTitle = BorderFactory.createTitledBorder("Display Style");
        radioTitle.setTitleFont(Util.labelFont);
        radioPanel.setBorder(radioTitle);
        contentPane.add(radioPanel);
        pack();
        setVisible(false);
    }

    @Override
    public void swpChanged(String newSwp) {

        swp.setText(newSwp);
        if (model != null) {
            model.setSWP(getSWP());
        }
    }

    public void setModel(MHD model) {

        this.model = model;
    }

    public void addSwpChangeListener(SwpChangeListener listener) {

        swpChangeListeners.add(listener);
    }

    /**
     * Unregisters the given listener.
     *
     * @param listener the listener that is to be removed
     */
    public void removeSwpChangeListener(SwpChangeListener listener) {

        swpChangeListeners.removeElement(listener);
    }

    /**
     * Returns the button determining the color selected to display the surface
     * areas
     *
     * @return a Button object
     */
    public JButton getColorButton() {
        return colorButton;
    }

    /**
     * Returns the swp value. This could be the default value of 2.0 or a
     * different value entered by the user in a provided field text
     *
     * @return the swp value as a float
     */
    public float getSWP() {
        if (swp.getText().length() == 0) {
            return 2.04f;
        }
        return Float.parseFloat(swp.getText());
    }

    /**
     * Returns the selection to display the surface area as a plain surface or a
     * wireframe surface. The selection is allowed using radio buttons
     *
     * @return the polygon attribute (POLYGON_FILL or POLYGON_LINE)
     */
    public int getPolygonAttributes() {

        if (radioButtons[0].isSelected()) {
            return PolygonAttributes.POLYGON_LINE;
        } else {
            return PolygonAttributes.POLYGON_FILL;
        }
    }

    public int getTransparencyAttributes() {

        if (radioButtons[1].isSelected()) {
            return TransparencyAttributes.SCREEN_DOOR;
        } else {
            return TransparencyAttributes.NICEST;
        }
    }

    /**
     * Returns the value of the surface opacity(inverse of transparency) as
     * selected via a slider bar.
     *
     * @return the opacity of the surface area
     */
    public float getOpacity() {

        return ((1f - (float) (opacitySlider.getValue()) / 100));
    }
}
