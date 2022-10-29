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
 * $Id: HelpMenu.java,v 1.13 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This class implements the Help menu for the application.
 *
 * @author B. Harris
 * @version $Revision: 1.13 $
 */
public class HelpMenu extends JMenu {

    /**
     * The "Help Contents" item.
     */
    private JMenuItem helpContent = new JMenuItem("Help Contents");
    /**
     * The "Tips" item.
     */
    private JMenuItem helpTips = new JMenuItem("Tips");
    /**
     * The "About" item.
     */
    private JMenuItem about = new JMenuItem("About");
    public static JButton infoButton = new JButton(
            new ImageIcon(TIPSODMenuBar.class.getResource("/images/Help.gif")));
    /**
     * The path to the help files.
     */
    public static final String helpPath = "help/";

    /**
     * Constructs a HelpMenu.
     *
     * @param comp the Component to enable the Help keyboard actions on
     */
    public HelpMenu(Component comp) {

        setText("Help");
        infoButton.setBorder(BorderFactory.createEmptyBorder());
        infoButton.setBackground(comp.getBackground());
        setMnemonic(KeyEvent.VK_H);

        String helpFile = helpPath + "HelpSet.hs";
        // help set filename
        ClassLoader classLoader = getClass().getClassLoader();
        // the class loader that is being
        //  used.  Note that the
        //  ClassLoader.getSystemClassLoader()
        //  is not the one to use under Java
        //  Web Start.
        URL helpSetUrl = classLoader.getResource(helpFile);
        // URL of help set

        if (helpSetUrl == null) {

            //
            // try non-WebStart method
            //
            helpSetUrl = getClass().getResource(helpFile);

            if (helpSetUrl == null) {

                System.err.println("Couldn't find help contents resource ("
                        + helpFile + ")");
                return;
            }
        }

        HelpSet mainHS;         // the Help Set

        try {

            mainHS = new HelpSet(classLoader, helpSetUrl);
        } catch (HelpSetException ex) {

            System.err.println("Couldn't create HelpSet (" + helpSetUrl + ")");
            System.err.println("because: " + ex.getMessage());
            return;
        }

        HelpBroker mainHB = mainHS.createHelpBroker();
        // the Help Broker
        mainHB.enableHelpKey(comp, "Intro", mainHS);

        add(helpContent);

        helpContent.setMnemonic(KeyEvent.VK_H);
        helpContent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                ActionEvent.CTRL_MASK));
        helpContent.setEnabled(true);
        helpContent.addActionListener(new CSH.DisplayHelpFromSource(mainHB));

        add(helpTips);

        helpTips.addActionListener(new DisplayHelpId(mainHB, "Tips"));

        addSeparator();

        add(about);
        about.setMnemonic(KeyEvent.VK_A);
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        about.setEnabled(true);
        about.addActionListener(new DisplayHelpId(mainHB, "About"));

        infoButton.setToolTipText("Common Tasks");

        infoButton.addActionListener(new DisplayHelpId(mainHB, "Tips"));
    }

    protected static class DisplayHelpId implements ActionListener {

        protected HelpBroker helpBroker = null;
        protected String helpId = null;

        public DisplayHelpId(HelpBroker hb, String id) {

            helpBroker = hb;
            helpId = id;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            helpBroker.setCurrentID(helpId);
            helpBroker.setDisplayed(true);

        }
    }
}
