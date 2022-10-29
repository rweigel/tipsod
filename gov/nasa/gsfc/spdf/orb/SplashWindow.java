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
 * $Id: SplashWindow.java,v 1.7 2017/03/06 20:05:00 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb;

import gov.nasa.gsfc.spdf.orb.gui.SelectionWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;

/**
 *
 * @author rchimiak
 */
public class SplashWindow extends JWindow {

    private static SplashWindow instance = null;

    /**
     * creates a new splash screen
     */
    protected SplashWindow() {

        super();

        JLabel imageLabel = new JLabel(new ImageIcon(SplashWindow.class.getResource("/images/SplashWind.jpg")));
        imageLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        Dimension ImageSize = imageLabel.getPreferredSize();
        imageLabel.setBounds(0, 0, ImageSize.width, ImageSize.height);
        String line = null;

        try {

            InputStream inStrm = SelectionWindow.class.getResourceAsStream(
                    "/build.txt");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inStrm));

            line = in.readLine();
            in.close();
        } catch (IOException e) {

            return;
        }

        JLabel label = new JLabel(line);
        label.setFont(new Font("Georgia", Font.PLAIN, 15));
        label.setForeground(Color.white);
        Dimension labelSize = label.getPreferredSize();
        int xComp = ImageSize.width/2 - labelSize.width/2;
        label.setBounds(xComp, 230, labelSize.width, labelSize.height);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(imageLabel.getPreferredSize());
        layeredPane.add(imageLabel, new Integer(0), 1);
        layeredPane.add(label, new Integer(1), 0);

        setSize(imageLabel.getPreferredSize());
        getContentPane().add(layeredPane, BorderLayout.CENTER);

        pack();

        // center splash window
        setLocationRelativeTo(null);

    }

    /**
     *
     * @return SplashWindow the splash screen to display while the application
     * is being loaded.
     */
    public static SplashWindow getInstance() {
        if (instance == null) {
            instance = new SplashWindow();
        }
        return instance;
    }
}
