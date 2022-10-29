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
 *   http://cdaweb.gsfc.nasa.gov/cdas/NASA_Open_Source_Agreement_1.3.txt.
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
 * $Id: AboutDialog.java,v 1.11 2015/10/30 14:18:50 rchimiak Exp $
 * AboutDialog.java
 *
 *$Id: AboutDialog.java,v 1.11 2015/10/30 14:18:50 rchimiak Exp $
 * Created on November 7, 2006, 9:23 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

/**
 *
 * @author rachimiak
 */
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;

/**
 * The AboutDialog.
 */
class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {

        super(parent, true);
        setResizable(false);
        getContentPane().setLayout(new GridBagLayout());
        setSize(400, 180);
        setTitle("About");
        // setLocationRelativeTo is only available in JDK 1.4
        try {

            setLocationRelativeTo(parent);
        } catch (NoSuchMethodError e) {

            Dimension paneSize = this.getSize();
            Dimension screenSize = this.getToolkit().getScreenSize();
            this.setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
        }
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(close);
        JLabel label1 = new JLabel("SSCWeb 4D Orbit Viewer");
        label1.setFont(new Font("dialog", Font.PLAIN, 30));

        GridBagConstraints constraintsLabel1 = new GridBagConstraints();
        constraintsLabel1.gridx = 3;
        constraintsLabel1.gridy = 0;
        constraintsLabel1.gridwidth = 1;
        constraintsLabel1.gridheight = 1;
        constraintsLabel1.anchor = GridBagConstraints.CENTER;
        getContentPane().add(label1, constraintsLabel1);

        String currentVersion = OrbitViewer.getVersionString();

        JLabel label2 = new JLabel(currentVersion);
        label2.setFont(new Font("dialog", Font.PLAIN, 14));
        GridBagConstraints constraintsLabel2 = new GridBagConstraints();
        constraintsLabel2.gridx = 2;
        constraintsLabel2.gridy = 1;
        constraintsLabel2.gridwidth = 2;
        constraintsLabel2.gridheight = 1;
        constraintsLabel2.anchor = GridBagConstraints.CENTER;
        getContentPane().add(label2, constraintsLabel2);

        String newerVersion = OrbitViewer.getNewerVersionString();
        String newVersionText;

        if (newerVersion != null) {

            newVersionText = "A newer version (" + newerVersion
                    + ") is available.";
        } else {

            newVersionText = "You are running the latest version.";
        }

        JLabel newVersionLabel = new JLabel(newVersionText);
        newVersionLabel.setFont(new Font("dialog", Font.PLAIN, 14));
        GridBagConstraints constraintsLabel3 = new GridBagConstraints();
        constraintsLabel3.gridx = 2;
        constraintsLabel3.gridy = 2;
        constraintsLabel3.gridwidth = 2;
        constraintsLabel3.gridheight = 1;
        constraintsLabel3.anchor = GridBagConstraints.CENTER;
        getContentPane().add(newVersionLabel, constraintsLabel3);

        GridBagConstraints constraintsButton1 = new GridBagConstraints();
        constraintsButton1.gridx = 2;
        constraintsButton1.gridy = 3;
        constraintsButton1.gridwidth = 2;
        constraintsButton1.gridheight = 1;
        constraintsButton1.anchor = GridBagConstraints.CENTER;
        constraintsButton1.insets = new Insets(8, 0, 8, 0);
        getContentPane().add(close, constraintsButton1);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                dispose();
            }
        });
    }
}
