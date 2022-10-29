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
 *   https://sscweb.gsfc.nasa.gov/tipsod/NASA_Open_Source_Agreement_1.3.txt.
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
 * Copyright (c) 2003-2017 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: TIPSODMenuBar.java,v 1.61 2018/06/05 16:48:10 rchimiak Exp $
 *
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.content.ContentBranch;

import gov.nasa.gsfc.spdf.orb.view.ViewBranch;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import gov.nasa.gsfc.spdf.orb.content.behaviors.MouseHandler;
import gov.nasa.gsfc.spdf.orb.utils.ImportHandler;
import gov.nasa.gsfc.spdf.orb.utils.PrintHandler;
import gov.nasa.gsfc.spdf.orb.utils.SaveHandler;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.View;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import apple.dts.samplecode.osxadapter.OSXAdapter;
import gov.nasa.gsfc.spdf.orb.content.HelioSatBranch;
import gov.nasa.gsfc.spdf.orb.utils.GeoExportHandler;
import gov.nasa.gsfc.spdf.orb.utils.HelioExportHandler;
import gov.nasa.gsfc.spdf.orb.utils.SelenoExportHandler;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JColorChooser;
import javax.vecmath.Color3f;

/**
 * The TIPSODMenuBar class implements the menuBar for this application.
 *
 * @author rchimiak
 * @version $Revision: 1.61 $
 */
public class TIPSODMenuBar 
    extends JMenuBar
    implements InvocationHandler {

    private JMenuItem satelliteChooser = new JMenuItem("Satellite Chooser...");
    private JMenuItem positionWindow = new JMenuItem("Position...");
    private JMenuItem bowshockMenuItem
            = new JMenuItem("Bowshock...");

    //**************************
    private JMenuItem bfieldMenuItem
            = new JMenuItem("Magnetic Field...");

    //************************************
    private JMenuItem magnetopauseMenuItem
            = new JMenuItem("Magnetopause...");
    private JMenuItem nsMenuItem
            = new JMenuItem("NeutralSheet...");
    private JMenuItem groundStationsMenuItem
            = new JMenuItem("Ground Stations...");
    private JMenuItem enableCoord = new JMenuItem("Enable Coordinate Labels");
    private JMenuItem disableCoord = new JMenuItem("Disable Coordinate Labels");
    private JMenu zoomMenu = new JMenu("Open Zoom Window");
    private JMenuItem loopMenu = new JMenu("Set Animation Loop Count");
    private JMenu toolMenu = new JMenu("Tools");
    private JMenu optionsMenu = new JMenu("Options");
    private int projection = View.PARALLEL_PROJECTION;
    static final int AXIS_BUTTONS = 3;
    private AxisRadioButton[] radioButtons
            = new AxisRadioButton[AXIS_BUTTONS];
    private ButtonGroup radioGroup = new ButtonGroup();
    private JMenuItem resetItem = new JMenuItem("Reset View");
    /**
     * The menu item relating to the print command.
     */
    private JMenuItem printItem;
    private JMenuItem exportItem = new JMenuItem("Export...");
    private JMenuItem importItem = new JMenuItem("Import...");
    private boolean coordinatesEnabled = false;
    private JMenuItem deleteBackground = new JMenuItem("Delete Background Image");
    private JMenuItem restoreBackground = new JMenuItem("Restore Background Image");
    private JMenuItem  SpaceColorItem = new JMenuItem("Space Color...");
    /**
     * Application that this menu is for. Used to initiate an orderly shutdown.
     */
    private OrbitViewer orbitViewer = null;

    /**
     * Creates a new TIPSODMenuBar.
     *
     * @param orbitViewer reference to menuBar creator
     */
    public TIPSODMenuBar(final OrbitViewer orbitViewer) {

        super();

        this.orbitViewer = orbitViewer;

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        //------------- File Menu ------------
        JMenu fileMenu = new JMenu("File");

        printItem = new JMenuItem("Print...");
        printItem.addActionListener(new PrintHandler(orbitViewer, OrbitViewer.getViewBranchArray()[0].getCanvas()));
        fileMenu.add(printItem);

        JMenuItem saveItem = new JMenuItem("Save As...");
        saveItem.addActionListener(new SaveHandler(orbitViewer, OrbitViewer.getViewBranchArray()[0].getCanvas()));
        fileMenu.add(saveItem);

        fileMenu.addSeparator();

        if (orbitViewer != null
                && orbitViewer.getContentBranch() != null) {

            exportItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {

                    switch (ControlPanel.getCentralBody()) {

                        case MOON:
                            SelenoExportHandler seh = new SelenoExportHandler(orbitViewer);
                            seh.actionPerformed(e);
                            break;
                        case SUN:
                            HelioExportHandler heh = new HelioExportHandler(orbitViewer);
                            heh.actionPerformed(e);
                            break;
                        default:
                            GeoExportHandler geh = new GeoExportHandler(orbitViewer);
                            geh.actionPerformed(e);
                            break;
                    }

                }
            });

            fileMenu.add(exportItem);
            exportItem.setEnabled(false);

            importItem.addActionListener(new ImportHandler(orbitViewer));
            fileMenu.add(importItem);

            fileMenu.addSeparator();
        }

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                orbitViewer.exit(0);
            }
        });

        fileMenu.add(exitItem);
        add(fileMenu);

        //------------ Options Menu--------------
        satelliteChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                if (OrbitViewer.getSatelliteChooser() != null
                        && OrbitViewer.getSatelliteChooser().isComplete()) {
                    OrbitViewer.getSatelliteChooser().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                            " Spacecraft selection window is not available",
                            "information message",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        satelliteChooser.setSelected(true);
        optionsMenu.add(satelliteChooser);

        positionWindow.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                if (OrbitViewer.getSatellitePositionWindow() != null) {
                    OrbitViewer.getSatellitePositionWindow().setVisible(true);
                }
            }
        });
        optionsMenu.add(positionWindow);

        optionsMenu.addSeparator();

        magnetopauseMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                OrbitViewer.getMagnetopauseWindow().setVisible(true);
            }
        });
        optionsMenu.add(magnetopauseMenuItem);

        bowshockMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                OrbitViewer.getBowshockWindow().setVisible(true);
            }
        });
        optionsMenu.add(bowshockMenuItem);

        //********************************
        bfieldMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                OrbitViewer.getBfieldWindow().setVisible(true);
                OrbitViewer.getBfieldWindow().saveInitialValues();
            }
        });
        optionsMenu.add(bfieldMenuItem);
//********************************

        nsMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {

                OrbitViewer.getNeutralSheetWindow().setVisible(true);
            }
        });
        optionsMenu.add(nsMenuItem);

        groundStationsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                OrbitViewer.getGroundStationsWindow().setVisible(true);
            }
        });
        optionsMenu.add(groundStationsMenuItem);
        add(optionsMenu);

        //------------- Tool Menu ------------
        resetItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                if (ContentBranch.getSatBranch() == null
                        || OrbitViewer.getSatellitePositionWindow() == null) {
                    return;
                }

                java.util.Enumeration enumButtons = radioGroup.getElements();

                while (enumButtons.hasMoreElements()) {

                    AxisRadioButton radioButton = (AxisRadioButton) enumButtons.nextElement();
                    if (radioButton.isSelected()) {

                        OrbitViewer.getViewBranchArray()[0].setCenterView(radioButton.Axis(), getProjection());

                        if (ContentBranch.getSatBranch().getZoom() != null) {

                            ViewBranch zoomViewBranch = ContentBranch.getSatBranch().getZoom().getViewBranch();

                            zoomViewBranch.setZoomView(OrbitViewer.getViewBranchArray()[0].getViewingPlatform().getViewPlatformTransform(),
                            ContentBranch.getSatBranch().getZoom().getSelection(), getProjection());
                            ContentBranch.getSatBranch().getZoom().setzoomFactorSpinner(1);
                        }
                        ContentBranch.getSatBranch().addAxisText(OrbitViewer.getInfoPane().getCoordinateWidth());

                        if (ContentBranch.getSatBranch() instanceof HelioSatBranch == false) {
                            ContentBranch.getEarthSurfaces().doInitialTransform();
                        }

                        if (coordActivated()) {

                            MouseListener[] mls = (OrbitViewer.getViewBranchArray()[0].getCanvas().getMouseListeners());

                            for (MouseListener ml : mls) {
                                if (ml instanceof MouseHandler) {
                                    for (BranchGroup bg : MouseHandler.getPickList()) {
                                        bg.detach();
                                        int index = ContentBranch.getSatBranch().numChildren();

                                        ContentBranch.getSatBranch().insertChild(bg, index);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
        toolMenu.add(resetItem);

        radioButtons[0] = new AxisRadioButton("xy View", true);
        radioButtons[1] = new AxisRadioButton("yz View", false);
        radioButtons[2] = new AxisRadioButton("xz View", false);

        for (AxisRadioButton radioButton : radioButtons) {
            radioGroup.add(radioButton);
        }
        toolMenu.addSeparator();

        // Create label and associate with text field
        JRadioButtonMenuItem defaultConfig = new JRadioButtonMenuItem("<default config>");
        defaultConfig.setSelected(true);
        JRadioButtonMenuItem undefinitely = new JRadioButtonMenuItem("undefinitely");
        ButtonGroup group = new ButtonGroup();
        group.add(defaultConfig);
        group.add(undefinitely);
        loopMenu.add(defaultConfig);
        loopMenu.add(undefinitely);

        class LoopListener implements ActionListener {

            @Override
            public void actionPerformed(final ActionEvent e) {

                final int LOOP_COUNT = 100;
                Animation.setLoopCount(e.getActionCommand().equalsIgnoreCase("undefinitely")
                        ? -1 : LOOP_COUNT);
            }
        }

        defaultConfig.addActionListener(new LoopListener());
        undefinitely.addActionListener(new LoopListener());

        toolMenu.add(loopMenu);
        toolMenu.addSeparator();

        JMenuItem zoomLocked = new JMenuItem("Locked Attitude Control");
        JMenuItem zoomUnlocked = new JMenuItem("Independent Attitude Control");
        zoomMenu.add(zoomLocked);
        zoomMenu.add(zoomUnlocked);

        class ZoomListener implements ActionListener {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (ContentBranch.getSatBranch() == null
                        || OrbitViewer.getSatellitePositionWindow() == null) {

                    JOptionPane.showMessageDialog(null,
                            "You must select a satellite \n"
                            + "before opening this window.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SatellitePositionTable table = OrbitViewer.getSatellitePositionWindow().getTable();
                int selection = 0;

                if (table.getRowCount() > 1) {

                    ButtonGroup group = new ButtonGroup();
                    Object[] possibilities = new Object[table.getRowCount() + 1];
                    possibilities[0] = "Zoom on the following satellite :";

                    for (int i = 0; i < possibilities.length - 1; i++) {

                        possibilities[i + 1] = new JRadioButton(table.getValueAt(i, 0).toString());
                        group.add((JRadioButton) possibilities[i + 1]);
                    }
                    ((JRadioButton) possibilities[1]).setSelected(true);

                    JOptionPane.showConfirmDialog(null,
                            possibilities,
                            "Zoom Selection",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null);

                    for (int i = 0; i < possibilities.length - 1; i++) {

                        if (((JRadioButton) possibilities[i + 1]).isSelected()) {

                            selection = i;
                            break;
                        }
                    }
                }

                if (e.getActionCommand().equalsIgnoreCase("Locked Attitude Control")) {
                    ContentBranch.getSatBranch().addZoom(new ZoomControl(orbitViewer, selection, true, getProjection()), selection);
                } else {
                    ContentBranch.getSatBranch().addZoom(new ZoomControl(orbitViewer, selection, false, getProjection()), selection);
                }

                zoomMenu.setEnabled(false);
            }
        }

        zoomLocked.addActionListener(new ZoomListener());
        zoomUnlocked.addActionListener(new ZoomListener());

        toolMenu.add(zoomMenu);
        toolMenu.addSeparator();

        enableCoord.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                MouseListener[] mls = (OrbitViewer.getViewBranchArray()[0].getCanvas().getMouseListeners());

                for (MouseListener ml : mls) {
                    if (ml instanceof MouseHandler) {
                        ((MouseHandler) ml).enablePicking(true);
                    }
                }

                toolMenu.remove(enableCoord);

                toolMenu.add(disableCoord, 6);

                coordinatesEnabled = true;
            }
        });
        toolMenu.add(enableCoord);

        disableCoord.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                if (ContentBranch.getSatBranch() != null) {
                    ContentBranch.getSatBranch().removeCoord();
                }

                MouseListener[] mls = (OrbitViewer.getViewBranchArray()[0].getCanvas().getMouseListeners());

                for (MouseListener ml : mls) {
                    if (ml instanceof MouseHandler) {
                        ((MouseHandler) ml).enablePicking(false);
                    }
                }

                toolMenu.remove(disableCoord);
                toolMenu.add(enableCoord, 6);
                coordinatesEnabled = false;
            }
        });

        toolMenu.addSeparator();
        
        
        SpaceColorItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                               
                Color newColor = JColorChooser.showDialog(
                              null,
                               "Space Color",
                               orbitViewer.getContentBranch().getBackgroundColor().get());                
                if (newColor != null)
                    orbitViewer.getContentBranch().setBackgroundColor(new Color3f(newColor));   
            }
        });                
        toolMenu.add(SpaceColorItem);
        toolMenu.addSeparator();
     
        //toolMenu.add(deleteBackground);
        toolMenu.add(restoreBackground);

        deleteBackground.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                if (orbitViewer != null
                        && orbitViewer.getContentBranch() != null) {

                    orbitViewer.getContentBranch().changeBackground(0);
                }
                toolMenu.remove(deleteBackground);
                SpaceColorItem.setEnabled(true);
                toolMenu.add(restoreBackground);

            }
        });

        restoreBackground.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {

                if (orbitViewer != null
                        && orbitViewer.getContentBranch() != null) {

                    orbitViewer.getContentBranch().changeBackground(1);
                }
                toolMenu.remove(restoreBackground);
                SpaceColorItem.setEnabled(false);
                toolMenu.add(deleteBackground);

            }
        });

        add(toolMenu);

        //--------------Help Menu -----------------
        add(new HelpMenu(orbitViewer.getRootPane()));
        final int HORIZONTAL_BUFFER_SPACE = 10;
        this.add(javax.swing.Box.createHorizontalStrut(HORIZONTAL_BUFFER_SPACE));
        add(HelpMenu.infoButton);

        if (System.getProperty("os.name").
                toLowerCase().startsWith("mac os x")) {

            registerForMacOsXEvents();
        }
    }

    public AxisRadioButton getRadioButton(final int i) {

        return radioButtons[i];
    }

    public void setZoomEnabled(boolean enabled) {

        zoomMenu.setEnabled(enabled);
    }

    public void setProjection(int projection) {

        this.projection = projection;
    }

    public int getProjection() {

        return projection;
    }

    public boolean coordActivated() {

        return coordinatesEnabled;
    }

    public void resetCoordinatesActivation() {

        if (coordinatesEnabled) {
            disableCoord.doClick();
        } else {
            enableCoord.doClick();
        }
    }

    public void resetView() {

//        java.util.Enumeration enumButtons = radioGroup.getElements();
//        AxisRadioButton radioButton = (AxisRadioButton) enumButtons.nextElement();
        //                   if (radioButton.isSelected()) {
        //                       OrbitViewer.getViewBranchArray()[0].setCenterView(radioButton.Axis(), getProjection());
        //                   }
        resetItem.doClick();
    }

    public JMenuItem getExportMenu() {

        return exportItem;
    }

    public JMenuItem getImportMenu() {

        return importItem;
    }


    /**
     * Registers methods of this class to handle OS X specific events.
     */
    public void registerForMacOsXEvents() {

        try {

            Class<?> aboutHandlerClass =
                Class.forName("java.awt.desktop.AboutHandler");
            Method setAboutMethod =
                Desktop.class.getDeclaredMethod(
                    "setAboutHandler",
                    new Class<?>[] {aboutHandlerClass});

            Class<?> quitHandlerClass =
                Class.forName("java.awt.desktop.QuitHandler");
            Method setQuitMethod =
                Desktop.class.getDeclaredMethod(
                    "setQuitHandler",
                    new Class<?>[] {quitHandlerClass});

            Object handlerProxy =
                Proxy.newProxyInstance(
                    TIPSODMenuBar.class.getClassLoader(),
                    new Class<?>[] {
                        aboutHandlerClass, quitHandlerClass
                    },
                    this);

            Desktop desktop = Desktop.getDesktop();

            setAboutMethod.invoke(desktop, handlerProxy);
            setQuitMethod.invoke(desktop, handlerProxy);

            return;
        }
        catch (ClassNotFoundException e) {

            // Java < 9.  Continue with code below for older Java.
        }
        catch (NoSuchMethodException e) {

            e.printStackTrace();
        }
        catch (IllegalAccessException e) {

            e.printStackTrace();
        }
        catch (InvocationTargetException e) {

            e.printStackTrace();
        }

        //
        // Do the following for Java < 9.  This can be eliminated
        // and the above reflection code rewritten without reflection
        // when Java < 9 is no longer supported.
        //
        try {

            OSXAdapter.setQuitHandler(this,
                    getClass().getDeclaredMethod("macOsXQuit"));
            OSXAdapter.setAboutHandler(this,
                    getClass().getDeclaredMethod("macOsXAbout"));
        } catch (NoSuchMethodException e) {

            System.err.println(
                    "TIPSODMenuBar.registerForMacOsXEvents: "
                    + "NoSuchMethodException: " + e.getMessage());
        }
    }


    /**
     * Processes a Desktop (Java 9 and later) method invocation for
     * About and Quit.
     *
     * @param proxy the proxy instance that the method was invoked on.
     * @param method the Method instance corresponding to the interface
     *     method invoked on the proxy instance.
     * @param args an array of objects containing the values of the
     *     arguments passed in the method invocation on the proxy
     *     instance, or null if interface method takes no arguments.
     * @return the value to return from the method invocation on the
     *     proxy instance.
     */
    public Object invoke(
        Object proxy,
        Method method,
        Object[] args) {

        if (method.getName().equals("handleAbout")) {

            macOsXAbout();
        }
        else if (method.getName().equals("handleQuitRequestWith")) {

            macOsXQuit();
        }
        else {

            System.err.println("SKTEditor.invoke: was called for " +
                method.getName());
        }
        return null;
    }


    public void centralBodiesSelection(ControlPanel.Body body) {

        Component[] subElements = optionsMenu.getMenuComponents();

        for (Component subElement : subElements) {

            if (subElement instanceof JMenuItem) {

                JMenuItem item = (JMenuItem) subElement;

                if (!item.equals(satelliteChooser)
                        && !item.equals(positionWindow)) {
                    switch (body) {

                        case EARTH:

                            if (!item.isEnabled()) {
                                item.setEnabled(true);
                            }
                            break;

                        case MOON:

                            if (item.equals(nsMenuItem) || item.equals(groundStationsMenuItem)) {
                                item.setEnabled(false);
                            } else {
                                item.setEnabled(true);
                            }
                            break;

                        case SUN:

                            if (item.isEnabled()) {
                                item.setEnabled(false);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * Handles OS X Quit event.
     *
     * @return true. Never actually returns.
     */
    public boolean macOsXQuit() {

        orbitViewer.exit(0);

        return true;
    }

    /**
     * Handles OS X About event.
     */
    public void macOsXAbout() {

        new AboutDialog(new JFrame()).setVisible(true);
    }
}

