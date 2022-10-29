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
 * $Id: OrbitViewer.java,v 1.156 2018/06/05 16:48:10 rchimiak Exp $
 */

/*
 * orbitViewer.java
 *
 * Created on March 12, 2002, 11:59 AM
 */
package gov.nasa.gsfc.spdf.orb;

import gov.nasa.gsfc.spdf.helio.client.HelioExternalException;
import gov.nasa.gsfc.spdf.helio.client.HelioExternalException_Exception;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.behaviors.MouseHandler;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.orb.gui.BFieldWindow;
import gov.nasa.gsfc.spdf.orb.gui.BowshockWindow;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.DataCaptureListener;
import gov.nasa.gsfc.spdf.orb.gui.GraphSpecifiedEvent;
import gov.nasa.gsfc.spdf.orb.gui.GraphSpecifiedListener;
import gov.nasa.gsfc.spdf.orb.gui.GroundStationsWindow;
import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import gov.nasa.gsfc.spdf.orb.gui.MagnetopauseWindow;
import gov.nasa.gsfc.spdf.orb.gui.NeutralSheetWindow;
import gov.nasa.gsfc.spdf.orb.gui.PlanarLabel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.SelectionWindow;
import gov.nasa.gsfc.spdf.orb.gui.Slider;
import gov.nasa.gsfc.spdf.orb.gui.StatusBar;
import gov.nasa.gsfc.spdf.orb.gui.TIPSODMenuBar;
import gov.nasa.gsfc.spdf.orb.gui.TIPSODToolBar;
import gov.nasa.gsfc.spdf.orb.gui.HelioTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.ToolPanel;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import gov.nasa.gsfc.spdf.orb.utils.ServiceExceptionHandler;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;

import javax.media.j3d.Locale;

import javax.media.j3d.VirtualUniverse;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;
import javax.jnlp.BasicService;

import gov.nasa.gsfc.spdf.orb.view.ViewBranch;
import gov.nasa.gsfc.spdf.ssc.client.BFieldModelOptions;
import gov.nasa.gsfc.spdf.ssc.client.BFieldTraceOptions;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateComponent;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.DataFileRequest;
import gov.nasa.gsfc.spdf.ssc.client.DataRequest;
import gov.nasa.gsfc.spdf.ssc.client.DataResult;
import gov.nasa.gsfc.spdf.ssc.client.DateFormat;
import gov.nasa.gsfc.spdf.ssc.client.DistanceFromOptions;
import gov.nasa.gsfc.spdf.ssc.client.DistanceUnits;
import gov.nasa.gsfc.spdf.ssc.client.FileResult;
import gov.nasa.gsfc.spdf.ssc.client.FilteredCoordinateOptions;
import gov.nasa.gsfc.spdf.ssc.client.FormatOptions;
import gov.nasa.gsfc.spdf.ssc.client.GroundStationDescription;
import gov.nasa.gsfc.spdf.ssc.client.Hemisphere;
import gov.nasa.gsfc.spdf.ssc.client.OutputOptions;
import gov.nasa.gsfc.spdf.ssc.client.ResultStatusCode;
import gov.nasa.gsfc.spdf.ssc.client.SSCExternalException;
import gov.nasa.gsfc.spdf.ssc.client.SSCExternalException_Exception;
import gov.nasa.gsfc.spdf.ssc.client.SSCResourceLimitExceededException_Exception;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteDescription;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteSituationCenterInterface;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteSituationCenterService;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteSpecification;
import gov.nasa.gsfc.spdf.ssc.client.SpaseObservatoryDescription;
import gov.nasa.gsfc.spdf.ssc.client.TimeFormat;
import gov.nasa.gsfc.spdf.ssc.client.ValueOptions;
import gov.nasa.gsfc.spdf.helio.client.HeliocentricTrajectoriesInterface;
import gov.nasa.gsfc.spdf.helio.client.HeliocentricTrajectoriesService;
import gov.nasa.gsfc.spdf.helio.client.ObjectDescription;
import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.content.GeoSatBranch;
import gov.nasa.gsfc.spdf.orb.gui.GeoSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.GeoTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.HelioSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.SelenoSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.SelenoTogglePanel;
import gov.nasa.gsfc.spdf.ssc.client.SSCDatabaseLockedException_Exception;
import java.awt.CardLayout;
import java.awt.HeadlessException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrbitViewer
        extends JFrame
        implements SingleInstanceListener {

    /**
     * JNLP SingleInstanceService. null if we are not executing in a JNLP
     * environment.
     */
    private SingleInstanceService singleInstanceService = null;
    /**
     * Factory that creates <code>javax.xml.datatype Objects</code>.
     */
    private DatatypeFactory datatypeFactory = null;
    /**
     * Reference to the first node on the content branch.
     */
    private ContentBranch contentBranch = null;
    /**
     * Reference to the menuBar.
     */
    private static TIPSODMenuBar tipsodMenuBar;
    /**
     * Reference to the tool bar.
     */
    private static TIPSODToolBar toolBar;
    /**
     * Reference to the ToolPanel.
     */
    private ToolPanel toolPane;
    /**
     * Reference to the slider controlling the animation.
     */
    private static Slider slider;
    /**
     * Reference to the server.
     */
    private static SatelliteSituationCenterInterface ssc = null;
    /**
     * URL of SSC web services.
     */
    private String sscUrl = null;
    /**
     * Reference to the panel with time and scale information.
     */
    private static InfoPanel infoPane;
    /**
     * background color for the control panel.
     */
    public static final Color BEIGE = new Color(250, 195, 150);
    /**
     * part of the 3d configuration.
     */
    private javax.media.j3d.Locale locale;
    private static final int PLANAR_VIEWS_ROWS = 3;
    /**
     * Reference to the three small panels displaying planar views of the orbit
     * plots.
     */
    private static JPanel planarPanel
            = new JPanel(new GridLayout(PLANAR_VIEWS_ROWS, 1));
    private static final int NUMBER_OF_VIEWS = 4;
    /**
     * Array containing the 4 views, 1 main and 3 planar projections.
     */
    private static ViewBranch[] viewBranchArray = new ViewBranch[NUMBER_OF_VIEWS];
    /**
     * Reference to the array containing the 4 panels to display the 4 views.
     */
    private JPanel[] arrayOfPan;
    /**
     * Reference to the selection window communicating with the server side to
     * request satellite information.
     */
    private static SelectionWindow satelliteChooser = null;
    /**
     * Reference to the window displaying time and position coordinates for
     * selected satellites.
     */
    private static SatellitePositionWindow satellitePositionWindow = null;
    /**
     * Reference to the editing window containing information for the bow shock
     * display.
     */
    private static BowshockWindow bowshockWindow = new BowshockWindow();
    /**
     * Reference to the editing window containing information for the bow shock
     * display.
     */
    private static GroundStationsWindow groundStationsWindow;
    /**
     * Reference to the editing window containing information for the
     * magnetopause display.
     */
    private static final int PLANAR_VIEW_HEIGHT = 180;
    private static final int PLANAR_VIEW_WIDTH = 150;
    private static MagnetopauseWindow magnetopauseWindow
            = new MagnetopauseWindow();
    private static NeutralSheetWindow neutralSheetWindow
            = new NeutralSheetWindow();
    private static BFieldWindow bfieldWindow
            = new BFieldWindow();
    private static StatusBar statusBar
            = new StatusBar();
    private List<SatelliteDescription> sats = null;
//    private static SplashWindow splashWindow = null;
    private static final String OUT_OF_MEMORY_STRING
            = "<html>Your request has "
            + "exceeded the amount of memory available.<br>"
            + "Please resubmit your request using one of the "
            + "following strategies:<ul>"
            + "<li>Reduce the time range requested"
            + "<li>Increase the resolution factor"
            + "<li>Disable Field-Line Tracing "
            + "</ul></html>";
    private static final long MEMORY_ALLOCATED = 25000000L;
    private static final long MEMORY_AVAIL
            = Runtime.getRuntime().maxMemory() - MEMORY_ALLOCATED;
    private static final int MEM_FACTOR_NO_TRACING = 900;
    private static final int MEM_FACTOR_TRACING = 3000;
    private static final long DAYS_IN_MILLI = 24 * 60 * 60 * 1000L;
    private static boolean connected = true;
    private static final Object basicServiceObject = getBasicServiceObject();

    private static HeliocentricTrajectoriesInterface helio = null;
    /**
     * URL of SSC web services.
     */
    private String helioUrl = null;
    private List<ObjectDescription> helioSats = null;

    private static JPanel togglePane;

    public final static String GEO_TOGGLE_PANE = "GEO";
    public final static String SELENO_TOGGLE_PANE = "SELENO";
    public final static String HELIO_TOGGLE_PANE = "HELIO";
   

    private static SatellitePositionWindow[] satPosWindowsArray;

    /**
     *
     * // private BasicService bs = null; /**Creates a new orbit viewer which
     * can then be displayed as an applet or application.
     *
     * @throws Exception for all unreported exception
     */
    public OrbitViewer() throws Exception {

        datatypeFactory = DatatypeFactory.newInstance();

        /* the planar views work on the mac with Java 7 */
        try {

            singleInstanceService
                    = (SingleInstanceService) ServiceManager.lookup(
                            "javax.jnlp.SingleInstanceService");
        } catch (UnavailableServiceException e) {
            //
            // We are probably executing in a non-JNLP environment so
            // don't worry about the exception.
            //
        }
        try {

            //    splashWindow = new SplashWindow();
            //   splashWindow.setVisible(true);
            Container contentPane = getContentPane();

            //create the 4 panels used to display the 4 views.
            arrayOfPan = new JPanel[NUMBER_OF_VIEWS];
            for (int i = 0; i < NUMBER_OF_VIEWS; i++) {
                arrayOfPan[i] = new JPanel(new BorderLayout());
            }

            //----------- set up Universe -------------------------
            VirtualUniverse virtualUniverse = new VirtualUniverse();
            locale = new javax.media.j3d.Locale(virtualUniverse);

            //-------------- create  panel --------------------------
           /*Main Panel supporting the planar views,
             center view, and toggle panel. */
            JPanel rightPane = new JPanel(new BorderLayout());
            planarPanel.setPreferredSize(new Dimension(
                    PLANAR_VIEW_HEIGHT, PLANAR_VIEW_WIDTH));

            JPanel newPan = new JPanel();
            newPan.setLayout(new BorderLayout());
            newPan.add(statusBar, BorderLayout.NORTH);

            GeoTogglePanel geoTogglePane = new GeoTogglePanel();
            geoTogglePane.setName(GEO_TOGGLE_PANE);

            HelioTogglePanel helioTogglePane = new HelioTogglePanel();
            helioTogglePane.setName(HELIO_TOGGLE_PANE);

            SelenoTogglePanel selenoTogglePane = new SelenoTogglePanel();
            selenoTogglePane.setName(SELENO_TOGGLE_PANE);

            slider = new Slider();
            toolBar = new TIPSODToolBar();

            togglePane = new JPanel();
            togglePane.setLayout(new CardLayout());

            togglePane.add(geoTogglePane, GEO_TOGGLE_PANE);
            togglePane.add(selenoTogglePane, SELENO_TOGGLE_PANE);
            togglePane.add(helioTogglePane, HELIO_TOGGLE_PANE);
            rightPane.add(newPan, BorderLayout.SOUTH);

            newPan.add(togglePane);

            for (int i = 1; i < NUMBER_OF_VIEWS; i++) {
                arrayOfPan[i].add(new PlanarLabel(i), BorderLayout.NORTH);
            }

            rightPane.add(arrayOfPan[0], BorderLayout.CENTER);
            rightPane.add(planarPanel, BorderLayout.EAST);

            // ------------create content Branch -------------------
            contentBranch = new ContentBranch();
            locale.addBranchGraph(contentBranch);

            infoPane = new InfoPanel();

            //------------- create final panel --------------------
            rightPane.add(infoPane, BorderLayout.WEST);

            JPanel tool = new JPanel();
            tool.setLayout(new BorderLayout());
            tool.add(toolBar);

            contentPane.add(tool, BorderLayout.NORTH);
            contentPane.add(rightPane, BorderLayout.CENTER);

            //-------------- Create view Branch --------------------
            for (int i = 0; i < NUMBER_OF_VIEWS; i++) {
                viewBranchArray[i] = new ViewBranch();
                locale.addBranchGraph(viewBranchArray[i]);

                arrayOfPan[i].add(viewBranchArray[i].getCanvas());
                arrayOfPan[i].setBorder(new LineBorder(BEIGE, 1));

                if (i > 0) {

                    planarPanel.add(arrayOfPan[i]);
                }
            }

            planarPanel.setVisible(false);

            //------------ set up Menu ----------------------------
            tipsodMenuBar = new TIPSODMenuBar(this);
            this.setJMenuBar(tipsodMenuBar);

            toolPane = new ToolPanel();
            tool.add(toolPane, BorderLayout.SOUTH);

            //------------------Connect to Server -------------------
            connect();

            //--------Get list of Spacecrafts from Server ------------
            if (ssc != null) {
                getSpacecrafts();
            }

            if (helio != null) {

                getHelioSpacecrafts();
            }

            System.err.println("******************************");
            try {

                Package pkg
                        = Class.forName("java.lang.String").getPackage();                                       // j3d package

                System.err.println("JRE version: "
                        + pkg.getImplementationVersion());
            } catch (ClassNotFoundException e) {

                System.err.println("Java Class not found");
            }

            try {

                Package j3dPkg
                        = Class.forName("javax.media.j3d.VirtualUniverse").getPackage();

                System.err.println("Java3D library version: "
                        + j3dPkg.getImplementationVersion());
            } catch (ClassNotFoundException e) {

                System.err.println("Java3D Class not found");
            }
            String renderer = System.getProperty("j3d.rend");
            System.err.println("Java3D renderer: "
                    + (renderer == null ? "default" : renderer));

            try {

                Package joglPkg = Class.forName("javax.media.opengl.GLCanvas").getPackage();
                System.err.println("JOGL library version: "
                        + joglPkg.getImplementationVersion());
            } catch (ClassNotFoundException e) {

                System.err.println("JOGL Class not found");
            }

        } catch (java.lang.Exception exception) {

            if (exception instanceof java.lang.IllegalStateException) {

                String message = "<html><center>You do not appear to have <br>"
                        + "a valid Java3D configuration. </center></html>";

                JOptionPane.showMessageDialog(null, message,
                        " failure",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            System.out.println(exception.getMessage());
            System.out.println(exception.getClass());

            this.exit(0);
        }

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {

                exit(0);
            }
        });

        //while compiling with 1.5;  needed to display the data listing
        //in the browser.
        //     startBrowser();
    }

    /**
     *
     * @return the panel composed of a set of three planar views on the right
     * side
     */
    public static JPanel getPlanarPanel() {
        return planarPanel;
    }

    /**
     *
     * @return the array of the four views (the main one and the 3 planar views.
     */
    public static ViewBranch[] getViewBranchArray() {
        return viewBranchArray;
    }

    /**
     *
     * @return the array of the four views (the main one and the 3 planar views.
     */
    public static SatellitePositionWindow[] getSatPosWindowsArray() {
        return satPosWindowsArray;
    }

    /**
     *
     * @return the selection window that pops up at the beginning with the list
     * of satellites and other options.
     */
    public static SelectionWindow getSatelliteChooser() {
        return satelliteChooser;
    }

    /**
     *
     * @return the small display that allow for some custom parameters to
     * display the bow shock.
     */
    public static BowshockWindow getBowshockWindow() {
        return bowshockWindow;
    }

    /**
     *
     * @return the window that shows the position of the satellites along with
     * other configurable distances.
     */
    public static SatellitePositionWindow getSatellitePositionWindow() {
        return satellitePositionWindow;
    }

    public static SatellitePositionWindow getSatellitePositionWindow(int i) {

        return satPosWindowsArray[i];
    }

    public static void setPositionWindow(int i) {

        satellitePositionWindow = satPosWindowsArray[i];

    }

    /**
     *
     * @return the windows that displays the list of selectable ground stations.
     */
    public static GroundStationsWindow getGroundStationsWindow() {
        return groundStationsWindow;
    }

    /**
     *
     * @return the display that allows for some custom configuration of the
     * magnetopause display.
     */
    public static MagnetopauseWindow getMagnetopauseWindow() {
        return magnetopauseWindow;
    }

    /**
     *
     * @return the display that allows for some custom configuration of the
     * neutral sheet display.
     */
    public static NeutralSheetWindow getNeutralSheetWindow() {
        return neutralSheetWindow;
    }

    /**
     *
     * @return the display that allows for some custom configuration of the
     * magnetic field display (may have to be removed).
     */
    public static BFieldWindow getBfieldWindow() {
        return bfieldWindow;
    }

    /**
     *
     * @return the display at the bottom that gives location information as the
     * mouse is being moved.
     */
    public static StatusBar getStatusBar() {
        return statusBar;
    }

    /**
     *
     * @return the on-line status.
     */
    public static boolean isConnected() {
        return connected;
    }

    /**
     *
     * @param isConnected on-line status.
     */
    public static void setConnected(final boolean isConnected) {
        OrbitViewer.connected = isConnected;
    }

    /**
     * Calls the routine to connect to the server.
     *
     * @throws java.lang.CloneNotSupportedException
     */
    public final void connect() throws CloneNotSupportedException {

        if (ssc == null) {
            ssc = connectToSsc();
        }

        if (ssc == null) {
            new ServiceExceptionHandler("SSCWeb Connection failed at this time", this);
        }

        if (helio == null) {
            helio = connectToHelioServices();
        }

        if (helio == null) {
            new ServiceExceptionHandler("HelioWeb Connection failed at this time", this);
        }

    }

    final HeliocentricTrajectoriesInterface connectToHelioServices() {
        
       helioUrl = System.getProperty("jnlp.helio.url");

        if (helioUrl == null) {

            helioUrl = "https://sscweb.gsfc.nasa.gov/WS/helio/1/HeliocentricTrajectoriesService?wsdl";

            System.err.println("Warning: jnlp.helio.url system property is not defined,"
                    + " trying with a value of " + helioUrl);
        }
        
        try {

            HeliocentricTrajectoriesService service
                    = new HeliocentricTrajectoriesService(
                            new URL(helioUrl),
                            new QName("http://helio.spdf.gsfc.nasa.gov/",
                                    "HeliocentricTrajectoriesService"));

            helio = service.getHeliocentricTrajectoriesPort();

            String dumpMsgs = System.getProperty(
                    "com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump");
            // property indicating whether the
            // SOAP messages are to be displayed

            if (dumpMsgs == null || !dumpMsgs.equals("true")) {

                //
                // Only request binary XML if we are not interested in
                // dumping/debugging the SOAP messages.
                //
                Map<String, Object> requestContextMap
                        = ((BindingProvider) helio).getRequestContext();
                // request context map

                // Option to request Fast Infoset (ITU-T Rec. X.891 |
                // ISO/IEC 24824-1) encoding ("binary XML").
                requestContextMap.put(
                        "com.sun.xml.ws.client.ContentNegotiation",
                        "pessimistic");
            }

            return helio;
        } catch (WebServiceException e) {

            System.err.println("No valid connection: " + e.getMessage());

        } catch (MalformedURLException e) {

            System.err.println("Malformed helio URL: " + e.getMessage());
        }

        return null;
    }

    /**
     * Establishes a connection to the SSC remote interface.
     *
     * @return a reference to the remote SSC object or null if a connection
     * could not be made
     */
    final SatelliteSituationCenterInterface connectToSsc() {

        sscUrl = System.getProperty("jnlp.ssc.url");

        if (sscUrl == null) {

            sscUrl = "https://sscweb.sci.gsfc.nasa.gov/WS/ssc/2/SatelliteSituationCenterService?wsdl";

            System.err.println("Warning: jnlp.ssc.url system property is not defined,"
                    + " trying with a value of " + sscUrl);
        }
        System.setProperty("http.agent",
                "TIPSOD (" + System.getProperty("os.name") + " "
                + System.getProperty("os.arch") + ")");
        try {

            //SSC web service
            SatelliteSituationCenterService service
                    = new SatelliteSituationCenterService(
                            new URL(sscUrl),
                            new QName("http://ssc.spdf.gsfc.nasa.gov/",
                                    "SatelliteSituationCenterService"));

            // SSC web service port
            ssc = service.getSatelliteSituationCenterPort();

            String dumpMsgs = System.getProperty(
                    "com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump");
            // property indicating whether the
            // SOAP messages are to be displayed

            if (dumpMsgs == null || !dumpMsgs.equals("true")) {

                //
                // Only request binary XML if we are not interested in
                // dumping/debugging the SOAP messages.
                //
                Map<String, Object> requestContextMap
                        = ((BindingProvider) ssc).getRequestContext();
                // request context map

                // Option to request Fast Infoset (ITU-T Rec. X.891 |
                // ISO/IEC 24824-1) encoding ("binary XML").
                requestContextMap.put(
                        "com.sun.xml.ws.client.ContentNegotiation",
                        "pessimistic");
            }

            return ssc;
        } catch (WebServiceException e) {

            System.err.println("No valid connection: " + e.getMessage());

        } catch (MalformedURLException e) {

            System.err.println("Malformed SSC URL: " + e.getMessage());
        }

        return null;
    }

    /**
     * Calls the server getAllSatellites() method.
     *
     * @throws java.lang.CloneNotSupportedException
     */
    public final void getSpacecrafts() throws CloneNotSupportedException {

        try {

            if (sats == null) {

                sats = ssc.getAllSatellites();

                List<SpaseObservatoryDescription> spaseObservatories
                        = ssc.getAllSpaseObservatories();
                // SPASE observatories available
                // at SSC
                try {

                    groundStationsWindow = new GroundStationsWindow(ssc.getAllGroundStations());
                } catch (Exception e) {

                    JOptionPane.showMessageDialog(null, "No Ground Stations available at this time",
                            "Communications failure",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                if (sats != null) {

                    satelliteChooser
                            = new SelectionWindow(sats, spaseObservatories);

                    satPosWindowsArray = new SatellitePositionWindow[ControlPanel.Body.values().length];
                    satPosWindowsArray[ControlPanel.Body.EARTH.ordinal()] = new GeoSatellitePositionWindow();
                    satPosWindowsArray[ControlPanel.Body.MOON.ordinal()] = new SelenoSatellitePositionWindow();
                    satPosWindowsArray[ControlPanel.Body.SUN.ordinal()] = new HelioSatellitePositionWindow();

                    satellitePositionWindow = satPosWindowsArray[ControlPanel.Body.EARTH.ordinal()];

                    satelliteChooser.addGraphSpecifiedListener(
                            new GraphRequestListener());

                    satPosWindowsArray[ControlPanel.Body.EARTH.ordinal()].addDataCaptureListener(
                            new DataCaptureImpListener());
                    satPosWindowsArray[ControlPanel.Body.MOON.ordinal()].addDataCaptureListener(
                            new DataCaptureImpListener());
                } else {

                    new ServiceExceptionHandler("No Spacecrafts ", this);
                }
            }
        } catch (SSCExternalException_Exception e) {

            System.err.println(e.getCause());

        } catch (HeadlessException e) {

            System.err.println(e.getCause());

        }

    }

    /**
     * Calls the server getAllSatellites() method.
     */
    public final void getHelioSpacecrafts() {

        try {

            if (helioSats == null) {

                helioSats = helio.getAllObjects();

                if (helioSats != null) {

                    satelliteChooser.setHelioSatelliteGraphProperties(helioSats);

                }
            }
        } catch (HelioExternalException_Exception e) {

            System.err.println(e.getCause());

        } catch (CloneNotSupportedException e) {

            System.err.println(e.getCause());

        }
    }

    /**
     *
     * @return the primary node in the orbit viewer tree.
     */
    public final ContentBranch getContentBranch() {

        return contentBranch;
    }

    /**
     * Once an orbit file as been requested, a branch is designed to handle
     * creating the orbit from the data.
     *
     * @param location an array of SatelliteLocation instances
     * @throws java.lang.CloneNotSupportedException
     */
    public void addSatBranch(final List<? extends Object> location) throws CloneNotSupportedException {

        try {

            contentBranch.addSatBranch(location);

            setViewBranches();

        } catch (OutOfMemoryError memErr) {

            JOptionPane.showMessageDialog(null,
                    "Your request to the SSC computer failed for "
                    + "the following reason:\n"
                    + OUT_OF_MEMORY_STRING,
                    "Server request failure",
                    JOptionPane.INFORMATION_MESSAGE);

            removeSatBranch();
        }
    }

    /**
     * Called when the application wants to use a previous saved file through
     * the import menu function. Such a case could be when the application is
     * run off-line
     *
     * @param location list of selected satellite data such as coordinates ...
     * @param properties array of selected satellite properties such as names,
     * display names...
     * @param footpoint are footprints part of the scene
     * @param stations list of all the ground stations
     */
    public void addSatBranch(final List<SatelliteData> location,
            final SatelliteGraphProperties[] properties, final boolean footpoint,
            final List<GroundStationDescription> stations, ControlPanel.Body body) {
        try {

            if (satellitePositionWindow != null) {
                satellitePositionWindow.dispose();
            }

            if (!OrbitViewer.isConnected()) {

                switch (body) {

                    case MOON:
                        satellitePositionWindow = new SelenoSatellitePositionWindow();
                        break;
                    default:
                        satellitePositionWindow = new GeoSatellitePositionWindow();
                        if (groundStationsWindow == null) {
                            groundStationsWindow = new GroundStationsWindow(stations);
                        }
                        break;
                }

            }

            contentBranch.addSatBranch(location, properties, footpoint, body);

            setViewBranches();
        } catch (OutOfMemoryError memErr) {

            JOptionPane.showMessageDialog(null,
                    "Your request to the SSC computer failed for "
                    + "the following reason:\n"
                    + OUT_OF_MEMORY_STRING,
                    "Server request failure",
                    JOptionPane.INFORMATION_MESSAGE);

            removeSatBranch();
        }
    }

    /**
     * Called when the application wants to use a previous saved file through
     * the import menu function. Such a case could be when the application is
     * run off-line
     *
     * @param traj list of selected satellite data such as coordinates ...
     * @param properties array of selected satellite properties such as names,
     * display names...
     */
    public void addSatBranch(final List<Trajectory> traj,
            final SatelliteGraphProperties[] properties) {
        try {

            if (satellitePositionWindow != null) {
                satellitePositionWindow.dispose();
            }

            if (!OrbitViewer.isConnected()) {

                satellitePositionWindow = new HelioSatellitePositionWindow();

            }

            contentBranch.addSatBranch(traj, properties, false, ControlPanel.Body.SUN);

            setViewBranches();

        } catch (OutOfMemoryError memErr) {

            JOptionPane.showMessageDialog(null,
                    "Your request to the SSC computer failed for "
                    + "the following reason:\n"
                    + OUT_OF_MEMORY_STRING,
                    "Server request failure",
                    JOptionPane.INFORMATION_MESSAGE);

            removeSatBranch();
        }
    }

    public void setViewBranches() {

      
        for (int i = 0; i < 3; i++) {

            viewBranchArray[i + 1].setSatBranch(ContentBranch.getSatBranch());

            viewBranchArray[i + 1].setPlanarView(Axis.axisPlacement[i]);
        }

        viewBranchArray[0].setSatBranch(ContentBranch.getSatBranch());
        viewBranchArray[0].setOrbitBehavior();
        viewBranchArray[0].setUserData("center");

        tipsodMenuBar.resetView();

            viewBranchArray[0].getCanvas().addMouseListener(
                    new MouseHandler());
            viewBranchArray[0].getCanvas().addMouseMotionListener(
                    new MouseHandler());
      

    }

    /**
     * remove the node associated with any previous display before graphing a
     * new set of satellite orbits.
     */
    public void removeSatBranch() {

        MouseListener[] mls = (viewBranchArray[0].getCanvas().getMouseListeners());

        for (MouseListener ml : mls) {
            if (ml instanceof MouseHandler) {
                viewBranchArray[0].getCanvas().removeMouseListener(ml);
            }
        }
        MouseMotionListener[] mmls = (viewBranchArray[0].getCanvas().getMouseMotionListeners());

        for (MouseMotionListener mml : mmls) {
            if (mml instanceof MouseHandler) {
                viewBranchArray[0].getCanvas().removeMouseMotionListener(mml);
            }
        }

        contentBranch.removeSatBranch();
    }

    /**
     * Provides access to the panel containing the main display
     *
     * @return arrayOfPan[0] the first element into the array 4 panels (one
     * main, three secondary)
     */
    public final JPanel getCenterPane() {

        return arrayOfPan[0];
    }

    /**
     * Returns the spacecraft selection window top component
     *
     * @return the spacecraft selection window control panel component
     */
    public static ControlPanel getControlPane() {

        if (satelliteChooser != null) {
            return satelliteChooser.getControlPane();
        }
        return null;
    }

    /**
     * This class responds to user requests to generate a graph.
     */
    private class GraphRequestListener implements GraphSpecifiedListener {

        private LocationRetrievalTask retrievalTask = null;
        protected List<String> outsideRange = new ArrayList<String>();

        @Override
        public void graphCancel() {

            if (retrievalTask != null) {
                retrievalTask.interrupt();
            }
        }

        @Override
        public void graphSpecified(final GraphSpecifiedEvent event) {

            String[] selectedSatNames = event.getSelectedSatelliteNames();
            SatelliteGraphProperties[] selectedSGPs = null;
            try {
                selectedSGPs = event.getSatelliteGraphProperties();
                // the satellites that the user has
                //  selected
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(OrbitViewer.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (selectedSatNames.length == 0 || (ControlPanel.isSolenocentric() && selectedSatNames.length == 1)) {

                JOptionPane.showMessageDialog(null,
                        "No satellites selected.\n"
                        + "Please select one or more satellites.",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);

                event.setProgressFinished();

                return;
            }

            Date beginDate = event.getBeginDate();
            // the begining date that the user
            //  has entered
            Date endDate = event.getEndDate();
            // the ending date that the user has
            //  entered
            String[][] selectedSatellites = event.getSelectedSatellites();
            // the satellites that the user has
            //  selected

            if (!applicableTime(beginDate, endDate)) {

                event.setProgressFinished();

                return;
            }
            if (!satelliteLocationAvailable(selectedSGPs, beginDate,
                    endDate)) {

                event.setProgressFinished();

                return;
            }

            if (!memoryAvail(selectedSatellites, beginDate, endDate, event.getTracing(), event.getResolution())) {

                event.setProgressFinished();

                return;
            }

            if (event.getTracing() && !distanceAdequate(selectedSatNames, beginDate, endDate, event.getResolution())) {

                event.setProgressFinished();

                return;
            }

            retrievalTask
                    = getControlPane().getCoordinateSystem() instanceof gov.nasa.gsfc.spdf.helio.client.CoordinateSystem
                            ? new HelioLocationRetrievalTask(selectedSatNames,
                                    (gov.nasa.gsfc.spdf.helio.client.CoordinateSystem) event.getCoordinateSystem(),
                                    event.getResolution(), beginDate,
                                    endDate)
                            : new GeoLocationRetrievalTask(selectedSatNames,
                                    (CoordinateSystem) event.getCoordinateSystem(),
                                    event.getResolution(), beginDate,
                                    endDate, event.getTracing());

            event.setProgressNote("Calculating satellite orbit information");
            event.setProgressIndeterminate(true);

            if (!ControlPanel.keepCamSetting()) {

                removeSatBranch();

            }

            retrievalTask.start();
        }

        private boolean applicableTime(final Date start, final Date end) {

            if (end.compareTo(start) < 1) {

                JOptionPane.showMessageDialog(null,
                        "The specified time range is not valid.\n"
                        + "Please change the time range.",
                        "Time Range Not Valid",
                        JOptionPane.INFORMATION_MESSAGE);

                return false;
            }
            return true;
        }

        private boolean memoryAvail(final String[][] satellites, final Date start, final Date end, final boolean tracing, final int resolution) {

            long time = end.getTime() - start.getTime();
            long points = 0;
            double res = 0;

            if (getControlPane().getCoordinateSystem() instanceof gov.nasa.gsfc.spdf.helio.client.CoordinateSystem) {
                return true;
            }

            for (int i = 0; i < satellites.length; i++) {

                for (SatelliteDescription sat : sats) {

                    if (sat.getId().equals(satellites[i][0])) {

                        points += time / (sat.getResolution() * resolution * 1000);

                        res = (res * i + sat.getResolution()) / (double) (i + 1);

                    }
                }
            }

            long acceptableDays = getAcceptableDaysForServerResource(tracing, satellites.length, res * resolution);

            if (time > acceptableDays * DAYS_IN_MILLI) {

                JLabel message = new JLabel("<html>Your request may "
                        + "exceed the SSC computer's resource limits.<br>"
                        + "A typical request with " + satellites.length + " satellites at the database sampling"
                        + "<br>you have chosen should not go over " + acceptableDays + " days."
                        + "<br>We suggest the following:<ul>"
                        + "<li>Reduce the time range requested"
                        + "<li>Reduce the number of satellites selected"
                        + "<li>Increase the resolution factor"
                        + "</ul>" + "Do you really want to continue?</html>");

                int option = JOptionPane.showConfirmDialog(null, message,
                        "Ask yourself this Question", JOptionPane.YES_NO_OPTION);

                if (option != JOptionPane.OK_OPTION) {
                    return false;
                }
            }

            int factor = tracing ? MEM_FACTOR_TRACING : MEM_FACTOR_NO_TRACING;

            if (MEMORY_AVAIL - points * factor < 0) {

                JLabel message = new JLabel("<html>Your request may exceed"
                        + "the memory available on your machine.<br>"
                        + "A typical request with " + satellites.length + " satellites at the database sampling"
                        + "<br>you have chosen should not go over " + (MEMORY_AVAIL * resolution) / (factor * 60 * 24) + " days."
                        + "<br>We suggest the following:<ul>"
                        + "<li>Reduce the time range requested"
                        + "<li>Reduce the number of satellites selected"
                        + "<li>Increase the resolution factor"
                        + "</ul>" + "Do you really want to continue?</html>");

                int option = JOptionPane.showConfirmDialog(null, message,
                        "Ask yourself this Question", JOptionPane.YES_NO_OPTION);

                return option == JOptionPane.OK_OPTION;
            }

            return true;
        }

        private boolean distanceAdequate(final String[] satellites, final Date start, final Date end, final int resolution) {

            DataResult results;

            DataRequest request = new DataRequest();

            for (String satellite : satellites) {
                SatelliteSpecification satSpec
                        = new SatelliteSpecification();
                satSpec.setId(satellite);
                satSpec.setResolutionFactor(resolution);
                request.getSatellites().add(satSpec);
            }

            request.setBeginTime(newXMLGregorianCalendar(start));
            request.setEndTime(newXMLGregorianCalendar(end));

            OutputOptions outOptions = new OutputOptions();

            ValueOptions valueOptions = new ValueOptions();
            valueOptions.setBFieldStrength(false);
            valueOptions.setDipoleInvLat(false);
            valueOptions.setDipoleLValue(false);
            valueOptions.setRadialDistance(true);

            outOptions.setValueOptions(valueOptions);

            request.setOutputOptions(outOptions);

            double originalMin, min;
            originalMin = min = PhysicalConstants.EARTH_RADIUS_KM + getControlPane().getFieldLinesStopAltitude();

            try {

                results = ssc.getData(request);

                List<SatelliteData> sd = results.getData();

                if (sd == null) {
                    return false;
                }

                ListIterator<SatelliteData> listItr = sd.listIterator();
                while (listItr.hasNext()) {

                    for (Double temp : listItr.next().getRadialLength()) {

                        if (temp > 0 && temp < min) {
                            min = temp;
                        }
                    }
                }

            } catch (SSCExternalException_Exception ex) {
                //continue will be dealt with appropriately with the continuing request                                
                return true;

            } catch (SSCResourceLimitExceededException_Exception ex) {
                //continue will be dealt with appropriately with the continuing request                                
                return true;

            } catch (SSCDatabaseLockedException_Exception ex) {
                //continue will be dealt with appropriately with the continuing request                                
                return true;
            }

            if (min < originalMin) {

                Object[] options = {"Reduce",
                    "Do not Reduce",
                    "Cancel"};

                int response = JOptionPane.showOptionDialog(null,
                        "<html><p>The minimum height in this time range of the missions selected is " + String.valueOf((int) (min - PhysicalConstants.EARTH_RADIUS_KM)) + "  km,<br />"
                        + " which is less than the specified field-line trace height of  " + String.valueOf((int) (originalMin - PhysicalConstants.EARTH_RADIUS_KM)) + "  km.<br />"
                        + "Mission heights less than the trace height will not display their closest footpoints.</p>"
                        + "<br /> Do you want to reduce the trace height to " + String.valueOf((int) (min - PhysicalConstants.EARTH_RADIUS_KM)) + "  km and continue? <br /><br />"
                        + "</html>", "Confirm Field-line Trace Height Selection",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                switch (response) {

                    case JOptionPane.YES_OPTION:

                        getControlPane().setFieldLinesStopAltitude(
                                String.valueOf((int) (min - PhysicalConstants.EARTH_RADIUS_KM)));
                        return true;

                    case JOptionPane.NO_OPTION:
                        return true;

                    case JOptionPane.CANCEL_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                        return false;

                }

            }

            return true;

        }

        private int getAcceptableDaysForServerResource(final boolean tracing, final int satelliteCount, final double res) {

            //int resolution = (int) Math.round(res / 60);
            int resolution = (int) Math.round(res / 15) > 0 ? (int) Math.round(res / 15) : 1;

            switch (satelliteCount) {

                case 1:
                    return (tracing ? 15 * resolution : 150 * resolution);
                case 2:
                    return (tracing ? 7 * resolution : 80 * resolution);
                case 3:
                    return (tracing ? 5 * resolution : 60 * resolution);
                default:
                    if (satelliteCount < 10) {
                        return tracing ? 3 * resolution : 36 * resolution;
                    }

                    if (satelliteCount < 20) {
                        return tracing ? 2 * resolution : 20 * resolution;
                    } else {
                        return tracing ? 1 * resolution : 10 * resolution;
                    }

            }

        }

        /**
         * Determines if SSC can provide location information for all of the
         * given satellite within the given time range.
         *
         * @param satellites contains the names of the satellites
         * @param start beginning of time range
         * @param end end of time range
         * @return true if SSC can provide location information of all of the
         * given satellites
         */
        private boolean satelliteLocationAvailable(final SatelliteGraphProperties[] selectedSGPs,
                final Date start, final Date end) {

            ArrayList<SatelliteGraphProperties> noData = new ArrayList<SatelliteGraphProperties>();

            HashMap<String, Integer> cache = new HashMap<String, Integer>();

            Date availableStart;
            Date availableStop;

            for (SatelliteGraphProperties selectedSGP : selectedSGPs) {
                //
                // The following WS call is unnecessary when using
                // a version of the SSC WS getAllSatellite() method
                // that returns SatelliteDescription objects.  In
                // that case, we have already obtained the time range
                // information necessary to validate this choosen
                // time range and satellites.

                String groupName = SatelliteGraphTableModel.getGroupName(selectedSGP.getName());

                if (groupName == null) {

                    availableStart = selectedSGP.getStartTime().toGregorianCalendar().getTime();
                    availableStop = selectedSGP.getStopTime().toGregorianCalendar().getTime();

                    if (start.after(availableStop) || end.before(availableStart)) {

                        noData.add(selectedSGP);
                        outsideRange.add(selectedSGP.getName());

                    }
                } else {

                    availableStart = SatelliteGraphTableModel.getGroupTime(groupName)[0].toGregorianCalendar().getTime();
                    availableStop = SatelliteGraphTableModel.getGroupTime(groupName)[1].toGregorianCalendar().getTime();

                    if (!cache.containsKey(groupName)) {

                        cache.put(groupName, 1);
                        outsideRange.add(selectedSGP.getName());

                        if ((start.after(availableStop) || end.before(availableStart))) {

                            SatelliteGraphProperties sp = new SatelliteGraphProperties("all " + groupName);

                            sp.setStartTime(SatelliteGraphTableModel.getGroupTime(groupName)[0]);
                            sp.setStopTime(SatelliteGraphTableModel.getGroupTime(groupName)[1]);

                            noData.add(sp);

                        }

                    } else {

                        outsideRange.add(selectedSGP.getName());

                        if ((start.after(availableStop) || end.before(availableStart))) {

                            cache.put(groupName, cache.get(groupName) + 1);

                        }
                    }
                }

            }

            if (noData.isEmpty()) {

                return true;
            }

            int size = noData.size();

            for (Map.Entry< String, Integer> entry : cache.entrySet()) {

                size += entry.getValue() - 1;

            }

            if (size == selectedSGPs.length
                    || (ControlPanel.getCentralBody().equals(ControlPanel.Body.MOON)
                    && size == selectedSGPs.length - 1)) {

                JOptionPane.showMessageDialog(null,
                        "The specified time range is not available for \n"
                        + "any of the selected satellites.\n"
                        + "Please change the time range or selected satellites.",
                        "Time Range Not Available",
                        JOptionPane.INFORMATION_MESSAGE);

                return false;
            } else {

                SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                formatter.setTimeZone(Util.UTC_TIME_ZONE);

                String message = "<html>The specified time range is not available for:<ul> ";

                for (SatelliteGraphProperties s : noData) {
                    message += "<li>" + s.getDisplayName()
                            + ", available from "
                            + formatter.format(s.getStartTime().toGregorianCalendar().getTime()) + " to "
                            + formatter.format(s.getStopTime().toGregorianCalendar().getTime()) + "</li>";

                }
                message += "</ul>Do you wish to continue </html>";

                int response = JOptionPane.showConfirmDialog(null,
                        message,
                        "Confirm Selection",
                        JOptionPane.YES_NO_OPTION);

                return response == 0;
            }

        }

        protected boolean dataExpected(String name) {

            for (String list : outsideRange) {

                if (list.equalsIgnoreCase(name)) {
                    return false;
                }
            }
            return true;

        }

        /**
         * Provides the interval of time over which SSC can provide information
         * for the given satellite.
         *
         * @param id satellite identifier
         * @return time interval of spcified satellite or null if specified
         * satellite is unknown
         */
        public Calendar[] getSatelliteTime(final String id) {

            Calendar[] calendar = null;

            if (getControlPane().getCoordinateSystem() instanceof gov.nasa.gsfc.spdf.helio.client.CoordinateSystem) {

                for (ObjectDescription obj : helioSats) {

                    if (obj.getId().equals(id)) {
                        calendar = new Calendar[2];
                        calendar[0] = obj.getStartDate().toGregorianCalendar();
                        calendar[1] = obj.getEndDate().toGregorianCalendar();

                    }

                }

            } else {
                for (SatelliteDescription sat : sats) {

                    if (sat.getId().equals(id)) {
                        calendar = new Calendar[2];
                        calendar[0]
                                = sat.getStartTime().toGregorianCalendar();
                        calendar[1] = sat.getEndTime().toGregorianCalendar();
                    }
                }
            }
            return calendar;
        }

        // }
        //         return null;
        /**
         * This class represents the lengthy satellite location retrieval task.
         * It performs this lengthy operation in a separate thread but performs
         * periodic status update notifications and the completion notification
         * on the main event dispatch thread.
         */
        private abstract class LocationRetrievalTask<I> extends gov.nasa.gsfc.spdf.orb.utils.SwingWorker {

            /**
             * The names of the satellites whose location information we are to
             * retrieve.
             */
            String[] satellites = null;
            /**
             * The resolution of the location information.
             */
            int resolution;
            /**
             * The beginning date of the location information.
             */
            //   XMLGregorianCalendar begin = null;
            Date beginDate = null;
            /**
             * The ending date of the location information.
             */
            // XMLGregorianCalendar end = null;
            Date endDate = null;
            /**
             * Any exception that occurred during the retrieval operation.
             */
            Exception exception = null;

            boolean interrupted = false;

            /**
             * Constructs a LocationRetrievalTask.
             *
             * @param satellites names of the satellites whose location is to be
             * retrieved
             * @param resolution the requested resolution of the location
             * information
             * @param beginDate beginning date of location information
             * @param endDate ending date of location information
             *
             */
            public LocationRetrievalTask(final String[] satellites,
                    int resolution, final Date beginDate,
                    final Date endDate) {

                this.satellites = satellites;
                this.resolution = resolution;
                this.beginDate = beginDate;
                this.endDate = endDate;

                //   this.begin = newXMLGregorianCalendar(beginDate);
                //   this.end = newXMLGregorianCalendar(endDate);
            }

            @Override
            public abstract Object construct(); // end construct()

            @Override
            public void interrupt() {

                super.interrupt();
                interrupted = true;
            }

            @Override
            public abstract void finished();

            /**
             * Indicates whether the given exception is reporting a resource
             * limit exceeded exception.
             *
             * @param e the exception to be examined
             * @return true if the given exception is reporting a resource limit
             * exceeded exception. Otherwise, false.
             */
            protected boolean isResourceLimitExceededException(final Exception e) {

                if (e instanceof SSCExternalException_Exception
                        || e instanceof HelioExternalException_Exception) {

                    String msg = e.getMessage().toLowerCase();
                    // the exception's message

                    if (msg.contains("resource")
                            && msg.contains("limit")
                            && msg.contains("exceeded")) {

                        return true;
                    }
                }
                return false;
            }

            protected boolean isNonContinuouException(final Exception e) {

                if (e instanceof SSCExternalException_Exception
                        || e instanceof HelioExternalException_Exception) {

                    String msg = e.getMessage().toLowerCase();
                    // the exception's message

                    if (msg.contains("non continuous")) {

                        return true;
                    }
                }
                return false;
            }

            /**
             * This is a Runnable class that is used to update the progress of
             * the retrieval operation. It is intended to be created in the
             * retrieval thread and then scheduled for execution on the main
             * event dispatching thread where the Swing progress components can
             * be updated.
             */
            class UpdateProgressTask implements Runnable {

                /**
                 * The current progress value.
                 */
                private int status;
                /**
                 * The progress note.
                 */
                private String note;

                /**
                 * Creates an UpdateProgressTask.
                 *
                 * @param status the new progress value
                 * @param note the new progress note
                 */
                public UpdateProgressTask(final int status, final String note) {

                    this.status = status;
                    this.note = note;
                }

                @Override
                public void run() {

                    satelliteChooser.setProgressValue(status);
                    satelliteChooser.setProgressNote(note);
                }
            }
        } // end LocationRetrievalTask class

        /**
         * This class represents the lengthy satellite location retrieval task.
         * It performs this lengthy operation in a separate thread but performs
         * periodic status update notifications and the completion notification
         * on the main event dispatch thread.
         */
        private class GeoLocationRetrievalTask extends LocationRetrievalTask<SatelliteData> {

            /**
             * The coordinate system for the location information.
             */
            CoordinateSystem coordSystem = null;
            /**
             * The requested location information.
             */
            private List<SatelliteData> locations = null;

            boolean tracing = false;

            /**
             * Constructs a LocationRetrievalTask.
             *
             * @param satellites names of the satellites whose location is to be
             * retrieved
             * @param coordinateSystem the requested coordinate system for the
             * retrieved location information
             * @param resolution the requested resolution of the location
             * information
             * @param beginDate beginning date of location information
             * @param endDate ending date of location information
             * @param tracing ending date of location information
             */
            public GeoLocationRetrievalTask(final String[] satellites,
                    final int resolution, final Date beginDate,
                    final Date endDate, final boolean tracing) {

                super(satellites, resolution, beginDate, endDate);

                this.tracing = tracing;

            }

            /**
             * Constructs a LocationRetrievalTask.
             *
             * @param satellites names of the satellites whose location is to be
             * retrieved
             * @param coordinateSystem the requested coordinate system for the
             * retrieved location information
             * @param resolution the requested resolution of the location
             * information
             * @param beginDate beginning date of location information
             * @param endDate ending date of location information
             * @param tracing ending date of location information
             */
            public GeoLocationRetrievalTask(final String[] satellites,
                    final CoordinateSystem coordinateSystem,
                    final int resolution, final Date beginDate,
                    final Date endDate, final boolean tracing) {

                this(satellites, resolution, beginDate, endDate, tracing);
                this.coordSystem = coordinateSystem;

            }

            @Override
            public Object construct() {

                //
                // This method runs outside the main event dispatch thread
                // so NO SWING in this method
                //
                try {

                    satellitePositionWindow.dispose();

                    SwingUtilities.invokeLater(new UpdateProgressTask(0,
                            "Retrieving/calculating orbit information"));

                    DataResult results = null;
                    try {

                        results = ssc.getData(createRequest());

                    } catch (OutOfMemoryError memErr) {

                        throw new Exception(OUT_OF_MEMORY_STRING);
                    }

                    if (results.getStatusCode() == ResultStatusCode.SUCCESS
                            && results.getData() != null
                            && results.getData().size() > 0) {

                        locations = results.getData();
                        /*   try {

                         checkForContiguous(locations);
                         } catch (Exception e) {

                         throw e;
                         }*/
                    } else {

                        String msg = results.getStatusText() != null && !results.getStatusText().isEmpty()
                                ? results.getStatusText().get(0)
                                : "No data returned from the SSC server";

                        SSCExternalException sscException
                                = new SSCExternalException();
                        sscException.setMessage(msg);

                        throw new SSCExternalException_Exception(
                                msg, sscException);
                    }

                    if (!interrupted) {
                        SwingUtilities.invokeLater(
                                new UpdateProgressTask(satellites.length,
                                        "Graphing orbits"));
                    }
                } catch (Exception e) {

                    //
                    // Save the exception so it can be handled later on the
                    // main event dispatch thread.
                    //
                    exception = e;
                }
                return null;

            } // end construct()

            protected DataRequest createRequest() {

                DataRequest request = new DataRequest();

                for (String satellite : satellites) {
                    SatelliteSpecification satSpec
                            = new SatelliteSpecification();
                    satSpec.setId(satellite);
                    satSpec.setResolutionFactor(resolution);
                    request.getSatellites().add(satSpec);
                }

                //find out how much time must be added to begin time
                //and end times to get a few more points in case the orbit
                //does not fall exactly on the request start and end
                int max = SatelliteGraphTableModel.getMaxSatelliteResolution(satellites) * resolution;

                beginDate = new Date(beginDate.getTime() - max * 1000);
                endDate = new Date(endDate.getTime() + max * 1000);

                request.setBeginTime(newXMLGregorianCalendar(beginDate));
                request.setEndTime(newXMLGregorianCalendar(endDate));

                OutputOptions outOptions = new OutputOptions();

                FilteredCoordinateOptions xCoordOpt = new FilteredCoordinateOptions();
                xCoordOpt.setComponent(CoordinateComponent.fromValue("X"));
                xCoordOpt.setCoordinateSystem(coordSystem);

                FilteredCoordinateOptions yCoordOpt = new FilteredCoordinateOptions();
                yCoordOpt.setComponent(CoordinateComponent.fromValue("Y"));
                yCoordOpt.setCoordinateSystem(coordSystem);

                FilteredCoordinateOptions zCoordOpt = new FilteredCoordinateOptions();
                zCoordOpt.setComponent(CoordinateComponent.fromValue("Z"));
                zCoordOpt.setCoordinateSystem(coordSystem);

                outOptions.getCoordinateOptions().add(xCoordOpt);
                outOptions.getCoordinateOptions().add(yCoordOpt);
                outOptions.getCoordinateOptions().add(zCoordOpt);

                if (tracing) {

                    BFieldTraceOptions northBFieldTraceOptions
                            = new BFieldTraceOptions();
                    // North BField trace options
                    northBFieldTraceOptions.setCoordinateSystem(CoordinateSystem.GEO);
                    northBFieldTraceOptions.setHemisphere(Hemisphere.NORTH);
                    northBFieldTraceOptions.setFieldLineLength(true);
                    northBFieldTraceOptions.setFootpointLatitude(true);
                    northBFieldTraceOptions.setFootpointLongitude(true);

                    outOptions.getBFieldTraceOptions().
                            add(northBFieldTraceOptions);

                    BFieldTraceOptions southBFieldTraceOptions
                            = new BFieldTraceOptions();
                    // South BField trace options
                    southBFieldTraceOptions.setCoordinateSystem(CoordinateSystem.GEO);
                    southBFieldTraceOptions.setHemisphere(Hemisphere.SOUTH);
                    southBFieldTraceOptions.setFieldLineLength(true);
                    southBFieldTraceOptions.setFootpointLatitude(true);
                    southBFieldTraceOptions.setFootpointLongitude(true);

                    outOptions.getBFieldTraceOptions().
                            add(southBFieldTraceOptions);
                }
                //*********************************
                //   request.setBFieldModelOptions(bfieldWindow.getBFieldModelOptions());
                BFieldModelOptions bField = bfieldWindow.getBFieldModelOptions();
                bField.setFieldLinesStopAltitude(getControlPane().getFieldLinesStopAltitude());
                request.setBFieldModelOptions(bField);
                //****************************************
                request.setOutputOptions(outOptions);

                return request;

            }

            @Override
            public void interrupt() {

                super.interrupt();
                interrupted = true;
            }

            @Override
            public void finished() {

                //
                // runs in event-dispatching thread so Swing is okay
                //
                if (exception == null) {

                    if (!interrupted && locations != null) {

                        List<SatelliteData> revisedLoc
                                = verifyCompleteness(satellites, locations);

                        if (ContentBranch.getSatBranch() == null) {

                            try {
                                addSatBranch(revisedLoc);
                            } catch (CloneNotSupportedException ex) {
                                Logger.getLogger(OrbitViewer.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } else {

                            ContentBranch.getSatBranch().addLocation(revisedLoc);
                        }

                        satelliteChooser.setProgressFinished();
                        satelliteChooser.resetOrbButton();
                        interrupted = false;
                        //                   tipsodMenuBar.resetView();

                        locations = null;
                        satellites = null;

                    }
                } else { // an exception occurred

                    satelliteChooser.setProgressFinished();
                    satelliteChooser.resetOrbButton();
                    interrupted = false;
                    locations = null;
                    satellites = null;

                    if (isResourceLimitExceededException(exception)) {

                        JLabel message = new JLabel("<html>Your request has "
                                + "exceeded the SSC computer's resource limits.<br>"
                                + "Please resubmit your request using one of the "
                                + "following strategies:<ul>"
                                + "<li>Reduce the time range requested"
                                + "<li>Reduce the number of satellites selected"
                                + "<li>Increase the resolution factor"
                                + "</ul></html>");
                        // the message that is to be displayed
                        // in the dialog window

                        JOptionPane.showMessageDialog(OrbitViewer.satelliteChooser, message,
                                "Resource limit exceeded",
                                JOptionPane.INFORMATION_MESSAGE);

                    } /*else if (isNonContinuouException(exception)) {

                     JLabel message = new JLabel(exception.getMessage());
                     // the message that is to be displayed
                     // in the dialog window

                     JOptionPane.showMessageDialog(null, message,
                     "Data non continuous",
                     JOptionPane.INFORMATION_MESSAGE);
                     return;
                     } */ else {

                        System.err.println("Exception thrown by "
                                + "getSatelliteLocation was: "
                                + exception.getMessage());

                        JOptionPane.showMessageDialog(null,
                                "Your request to the SSC computer failed for "
                                + "the following reason:\n"
                                + exception.getMessage(),
                                "Server request failure",
                                JOptionPane.INFORMATION_MESSAGE);

                    }
                } // end exception
            } // end finished()

            /**
             * Verifies that the given array of satellite locations contains
             * information for all the given satellites. A dialog message is
             * displayed for each satellite that is found to be missing.
             *
             * @param requestedSatellites the names of the satellites
             * @param locations array of location information that is to be
             * checked for completeness
             * @return the revised array of location information
             */
            private List<SatelliteData> verifyCompleteness(
                    final String[] requestedSatellites,
                    final List<SatelliteData> locations) {

                boolean[] active = new boolean[requestedSatellites.length];
                List<SatelliteData> revisedLoc
                        = new ArrayList<SatelliteData>(requestedSatellites.length);

                for (SatelliteData satData : locations) {

                    if (satData != null) {

                        String name = satData.getId();

                        for (int k = 0; k < requestedSatellites.length;
                                k++) {

                            if (requestedSatellites[k].equalsIgnoreCase(name)) {

                                Array.setBoolean(active, k, true);
                                //revisedLoc.set(k, satData);
                                revisedLoc.add(satData);
                            }
                        }
                    }
                }
                for (int l = 0; l < active.length; l++) {
                    if (!active[l] && dataExpected(requestedSatellites[l])) {
                        JOptionPane.showMessageDialog(null,
                                " No data were available for the following"
                                + " spacecraft: " + requestedSatellites[l],
                                "information message",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                outsideRange.clear();
                return revisedLoc;
            }

        } // end GeoLocationRetrievalTask class

        /**
         * This class represents the lengthy satellite location retrieval task.
         * It performs this lengthy operation in a separate thread but performs
         * periodic status update notifications and the completion notification
         * on the main event dispatch thread.
         */
        private class HelioLocationRetrievalTask extends LocationRetrievalTask<Trajectory> {

            private gov.nasa.gsfc.spdf.helio.client.CoordinateSystem coordSystem = null;
            private List<Trajectory> locations = null;

            /**
             * Constructs a LocationRetrievalTask.
             *
             * @param satellites names of the satellites whose location is to be
             * retrieved
             * @param coordinateSystem the requested coordinate system for the
             * retrieved location information
             * @param resolution the requested resolution of the location
             * information
             * @param beginDate beginning date of location information
             * @param endDate ending date of location information
             * @param tracing ending date of location information
             */
            public HelioLocationRetrievalTask(final String[] satellites,
                    final gov.nasa.gsfc.spdf.helio.client.CoordinateSystem coordinateSystem,
                    final int resolution, final Date beginDate,
                    final Date endDate) {

                super(satellites, resolution, beginDate, endDate);
                this.coordSystem = coordinateSystem;

            }

            @Override
            public Object construct() {

                //
                // This method runs outside the main event dispatch thread
                // so NO SWING in this method
                //
                try {

                    satellitePositionWindow.dispose();

                    SwingUtilities.invokeLater(new UpdateProgressTask(0,
                            "Retrieving/calculating orbit information"));

                    gov.nasa.gsfc.spdf.helio.client.DataResult results = null;

                    try {

                        results = helio.getTrajectories(createRequest());

                    } catch (OutOfMemoryError memErr) {

                        throw new Exception(OUT_OF_MEMORY_STRING);
                    }

                    if (results.getStatusCode() == gov.nasa.gsfc.spdf.helio.client.ResultStatusCode.SUCCESS
                            || results.getStatusCode() == gov.nasa.gsfc.spdf.helio.client.ResultStatusCode.CONDITIONAL_SUCCESS
                            && results.getTrajectories() != null
                            && results.getTrajectories().size() > 0) {

                        locations = results.getTrajectories();
                        try {

                            checkForContiguous(locations);
                        } catch (Exception e) {

                            throw e;
                        }

                    } else {

                        String msg = results.getStatusText().get(0);

                        HelioExternalException helioException = new HelioExternalException();
                        helioException.setMessage(msg);

                        throw new HelioExternalException_Exception(msg, helioException);

                    }

                    if (!interrupted) {
                        SwingUtilities.invokeLater(
                                new UpdateProgressTask(satellites.length,
                                        "Graphing orbits"));
                    }
                } catch (Exception e) {

                    //
                    // Save the exception so it can be handled later on the
                    // main event dispatch thread.
                    //
                    exception = e;
                }
                return null;

            } // end construct()

            protected gov.nasa.gsfc.spdf.helio.client.Request createRequest() {

                gov.nasa.gsfc.spdf.helio.client.Request request = new gov.nasa.gsfc.spdf.helio.client.Request();

                request.getObjects().addAll(Arrays.asList(satellites));

                request.setBeginDate(newXMLGregorianCalendar(beginDate));
                request.setEndDate(newXMLGregorianCalendar(endDate));
                request.setResolution(resolution);
                request.setCoordinateSystem(coordSystem);

                return request;
            }

            @Override
            public void finished() {

                //
                // runs in event-dispatching thread so Swing is okay
                //
                if (exception == null) {

                    if (!interrupted && locations != null) {

                        List<Trajectory> revisedLoc
                                = verifyCompleteness(satellites, locations);

                        if (ContentBranch.getSatBranch() == null) {

                            try {
                                addSatBranch(revisedLoc);
                            } catch (CloneNotSupportedException ex) {
                                Logger.getLogger(OrbitViewer.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } else {

                            ContentBranch.getSatBranch().addLocation(revisedLoc);
                        }

                        //  addSatBranch(revisedLoc);
                        satelliteChooser.setProgressFinished();
                        satelliteChooser.resetOrbButton();
                        interrupted = false;
                        //                   tipsodMenuBar.resetView();

                        locations = null;
                        satellites = null;

                    }
                } else { // an exception occurred

                    satelliteChooser.setProgressFinished();
                    satelliteChooser.resetOrbButton();
                    interrupted = false;
                    locations = null;
                    satellites = null;

                    if (isResourceLimitExceededException(exception)) {

                        JLabel message = new JLabel("<html>Your request has "
                                + "exceeded the SSC computer's resource limits.<br>"
                                + "Please resubmit your request using one of the "
                                + "following strategies:<ul>"
                                + "<li>Reduce the time range requested"
                                + "<li>Reduce the number of satellites selected"
                                + "<li>Increase the resolution factor"
                                + "</ul></html>");
                        // the message that is to be displayed
                        // in the dialog window

                        JOptionPane.showMessageDialog(OrbitViewer.satelliteChooser, message,
                                "Resource limit exceeded",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else if (isNonContinuouException(exception)) {

                        JLabel message = new JLabel(exception.getMessage());
                        // the message that is to be displayed
                        // in the dialog window

                        JOptionPane.showMessageDialog(null, message,
                                "Data non continuous",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {

                        System.err.println("Exception thrown by "
                                + "getSatelliteLocation was: "
                                + exception.getMessage());

                        JOptionPane.showMessageDialog(null,
                                "Your request to the SSC computer failed for "
                                + "the following reason:\n"
                                + exception.getMessage(),
                                "Server request failure",
                                JOptionPane.INFORMATION_MESSAGE);

                    }
                } // end exception
            } // end finished

            /**
             * Verifies that the given array of satellite locations contains
             * information for all the given satellites. A dialog message is
             * displayed for each satellite that is found to be missing.
             *
             * @param requestedSatellites the names of the satellites
             * @param locations array of location information that is to be
             * checked for completeness
             * @return the revised array of location information
             */
            private List<Trajectory> verifyCompleteness(
                    final String[] requestedSatellites,
                    final List<Trajectory> locations) {

                boolean[] active = new boolean[requestedSatellites.length];
                List<Trajectory> revisedLoc
                        = new ArrayList<Trajectory>(requestedSatellites.length);

                for (int i = 0; i < requestedSatellites.length; i++) {

                    active[i] = false;
                    revisedLoc.add(null);
                }

                for (Trajectory satData : locations) {

                    if (satData != null) {

                        String name = satData.getId();

                        for (int k = 0; k < requestedSatellites.length;
                                k++) {

                            if (requestedSatellites[k].equalsIgnoreCase(name)) {

                                Array.setBoolean(active, k, true);
                                revisedLoc.set(k, satData);
                            }
                        }
                    }
                }
                for (int l = 0; l < active.length; l++) {
                    if (!active[l]) {
                        JOptionPane.showMessageDialog(null,
                                " No data were available for the following"
                                + " spacecraft: " + requestedSatellites[l],
                                "information message",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                return revisedLoc;
            }

            private void checkForContiguous(
                    final List<Trajectory> dataArray)
                    throws Exception {

                long time
                        = endDate.getTime()
                        - beginDate.getTime();

                for (Trajectory satData : dataArray) {

                    for (SatelliteDescription sat : sats) {

                        if (sat.getId().equals(satData.getId())) {

                            long points
                                    = time / (sat.getResolution() * resolution * 1000);

                            if (points > satData.getTime().size()) {

                                throw new Exception("<html>Your request contained "
                                        + "non continuous data for \" " + sat.getName()
                                        + " \".<br>Non continuous data cannot"
                                        + " currently be displayed in TIPSOD.  <br>"
                                        + "Please Change the time range or selected satellites "
                                        + "</html>");
                            }
                        }
                    }
                }

            }

        } // end HelioLocationRetrievalTask class
    }

    /**
     * This class responds to user requests to generate a listing of the data.
     */
    private class DataCaptureImpListener implements DataCaptureListener {

        private ListingRetrievalTask retrievalTask = null;

        @Override
        public void dataCaptureCancel() {

            if (retrievalTask != null) {
                retrievalTask.interrupt();
            }
        }

        @Override
        public void dataCapture(final ArrayList<Integer> check, String unit) {

            retrievalTask
                    = new ListingRetrievalTask(check, unit);
            satellitePositionWindow.setProgressNote("Capturing...");
            retrievalTask.start();
        }

        /**
         * This class represents the lengthy satellite listing data retrieval
         * task. It performs this lengthy operation in a separate thread but the
         * completion is done notification on the main event dispatch thread.
         */
        private class ListingRetrievalTask
                extends gov.nasa.gsfc.spdf.orb.utils.SwingWorker {

            /**
             * The check boxes associated with the position window.
             * Intrinsically carries the display output selection made by the
             * user.
             */
            private ArrayList<Integer> chexs = null;
            /**
             * Any exception that occurred during the retrieval operation.
             */
            private Exception exception = null;
            /**
             * The requested location information.
             */
            private List<String> urls = null;
            /**
             * To know when a cancel action has taken place.
             */
            private boolean interrupted = false;
            private final String unit;

            /**
             * Constructs a ListingRetrievalTask.
             *
             * @param checkboxes the selection from the position window
             *
             */
            public ListingRetrievalTask(final ArrayList<Integer> checkboxes, String unit) {

                this.chexs = checkboxes;
                this.unit = unit;
            }

            @Override
            public Object construct() {

                //
                // This method runs outside the main event dispatch thread
                // so NO SWING in this method
                //
                try {

                    FileResult results = null;
                    try {

                        results = ssc.getDataFiles(createRequest());

                    } catch (OutOfMemoryError memErr) {

                        throw new Exception(OUT_OF_MEMORY_STRING);
                    }

                    if (results.getStatusCode() == ResultStatusCode.SUCCESS
                            && results.getUrls() != null
                            && results.getUrls().size() > 0) {

                        urls = results.getUrls();

                    } else {

                        String msg = results.getStatusText().get(0);

                        SSCExternalException sscException
                                = new SSCExternalException();
                        sscException.setMessage(msg);

                        throw new SSCExternalException_Exception(
                                msg, sscException);
                    }

                } catch (Exception e) {

                    //
                    // Save the exception so it can be handled later on the
                    // main event dispatch thread.
                    //
                    exception = e;

                    //  satellitePositionWindow.setProgressFinished();
                    // return false;
                }
                return null;

            } // end construct()

            private DataFileRequest createRequest() throws CloneNotSupportedException {

                DataFileRequest request = new DataFileRequest();

                CoordinateSystem coordSystem = (CoordinateSystem) getControlPane().getCoordinateSystem();

                String[] satellites = satelliteChooser.getSatModel().getSelectedSatelliteNames();

                for (String satellite : satellites) {
                    SatelliteSpecification satSpec
                            = new SatelliteSpecification();
                    satSpec.setId(satellite);
                    satSpec.setResolutionFactor(getControlPane().getResolution());
                    request.getSatellites().add(satSpec);
                }

                request.setBeginTime(newXMLGregorianCalendar(
                        getControlPane().getStartDate()));

                request.setEndTime(newXMLGregorianCalendar(
                        getControlPane().getEndDate()));

                Iterator<Integer> itr = chexs.iterator();

                OutputOptions outOptions = new OutputOptions();

                DistanceFromOptions distanceFromOptions
                        = new DistanceFromOptions();

                while (itr.hasNext()) {

                    switch (itr.next()) {

                        case SatellitePositionWindow.Column.X:

                            FilteredCoordinateOptions xCoordOpt
                                    = new FilteredCoordinateOptions();
                            xCoordOpt.setComponent(
                                    CoordinateComponent.fromValue("X"));
                            xCoordOpt.setCoordinateSystem(coordSystem);

                            outOptions.getCoordinateOptions().add(xCoordOpt);
                            break;

                        case SatellitePositionWindow.Column.Y:

                            FilteredCoordinateOptions yCoordOpt
                                    = new FilteredCoordinateOptions();
                            yCoordOpt.setComponent(
                                    CoordinateComponent.fromValue("Y"));
                            yCoordOpt.setCoordinateSystem(coordSystem);

                            outOptions.getCoordinateOptions().add(yCoordOpt);
                            break;

                        case SatellitePositionWindow.Column.Z:

                            FilteredCoordinateOptions zCoordOpt
                                    = new FilteredCoordinateOptions();
                            zCoordOpt.setComponent(
                                    CoordinateComponent.fromValue("Z"));
                            zCoordOpt.setCoordinateSystem(coordSystem);

                            outOptions.getCoordinateOptions().add(zCoordOpt);
                            break;

                        case SatellitePositionWindow.Column.RADIUS:

                            ValueOptions value = new ValueOptions();
                            value.setRadialDistance(true);
                            outOptions.setValueOptions(value);
                            break;

                        case SatellitePositionWindow.Column.LATITUDE:

                            FilteredCoordinateOptions latOpt
                                    = new FilteredCoordinateOptions();
                            latOpt.setComponent(
                                    CoordinateComponent.fromValue("LAT"));
                            latOpt.setCoordinateSystem(coordSystem);

                            outOptions.getCoordinateOptions().add(latOpt);
                            break;

                        case SatellitePositionWindow.Column.LONGITUDE:

                            FilteredCoordinateOptions lonOpt
                                    = new FilteredCoordinateOptions();
                            lonOpt.setComponent(
                                    CoordinateComponent.fromValue("LON"));
                            lonOpt.setCoordinateSystem(coordSystem);

                            outOptions.getCoordinateOptions().add(lonOpt);
                            break;

                        case SatellitePositionWindow.Column.MAGNETOPAUSE:

                            distanceFromOptions.setMPause(true);
                            break;

                        case SatellitePositionWindow.Column.BOWSHOCK:

                            distanceFromOptions.setBowShock(true);
                            break;

                        case SatellitePositionWindow.Column.NEUTRAL_SHEET:

                            distanceFromOptions.setNeutralSheet(true);
                            break;

                        case SatellitePositionWindow.Column.NFLAT:

                            BFieldTraceOptions nFoot
                                    = new BFieldTraceOptions();
                            nFoot.setFootpointLatitude(true);
                            nFoot.setFootpointLongitude(true);
                            nFoot.setHemisphere(Hemisphere.NORTH);

                            outOptions.getBFieldTraceOptions().add(nFoot);
                            break;

                        case SatellitePositionWindow.Column.SFLAT:

                            BFieldTraceOptions sFoot
                                    = new BFieldTraceOptions();
                            sFoot.setFootpointLatitude(true);
                            sFoot.setFootpointLongitude(true);
                            sFoot.setHemisphere(Hemisphere.SOUTH);

                            outOptions.getBFieldTraceOptions().add(sFoot);
                            break;

                        default:
                            break;

                    }
                }
//////////////*******************************************
                // request.setBFieldModelOptions(bFieldModelOptions);
                // request.setBFieldModelOptions(bfieldWindow.getBFieldModelOptions());
                BFieldModelOptions bField = bfieldWindow.getBFieldModelOptions();
                bField.setFieldLinesStopAltitude(getControlPane().getFieldLinesStopAltitude());
                request.setBFieldModelOptions(bField);
//////////////*******************************************+
                FormatOptions formatOptions = new FormatOptions();

                formatOptions.setDateFormat(DateFormat.YY_MM_DD);
                formatOptions.setTimeFormat(TimeFormat.HH_MM_SS);

                formatOptions.setDistanceUnits(unit.indexOf("km") > 0 ? DistanceUnits.KM : DistanceUnits.RE);

                final short precision = 3;
                formatOptions.setDistancePrecision(precision);
                request.setFormatOptions(formatOptions);
                outOptions.setDistanceFromOptions(distanceFromOptions);
                request.setOutputOptions(outOptions);

                return request;
            }

            @Override
            public void interrupt() {

                super.interrupt();
                interrupted = true;
            }

            @Override
            public void finished() {

                //
                // runs in event-dispatching thread so Swing is okay
                //
                if (exception == null) {

                    if (!interrupted && urls != null) {

                        Iterator<String> urlsIter = urls.iterator();

                        while (urlsIter.hasNext()) {

                            String url = urlsIter.next();
                            try {
                                showDocument(new URL(url));

                                /* if (basicServiceObject != null) {
                                 ((BasicService)basicServiceObject).showDocument(new URL(url));
                                 } else {

                                 try {

                                 Class<?> desktopClass =
                                 Class.forName(
                                 "java.awt.Desktop");
                                 // Desktop class
                                 Method getDesktopMethod =
                                 desktopClass.getDeclaredMethod(
                                 "getDesktop");
                                 // Desktop.getDesktop method
                                 Object desktop =
                                 getDesktopMethod.invoke(null);
                                 // Desktop object
                                 Method browseMethod =
                                 desktopClass.getDeclaredMethod(
                                 "browse", URI.class);
                                 // Desktop.browse method
                                 browseMethod.invoke(desktop,
                                 new URI(url));
                                 } catch (ClassNotFoundException e) {

                                 // Java < 1.6
                                 System.err.println(
                                 "Failed to launch a "
                                 + "browser for " + url + ".");
                                 System.err.println(
                                 "Launching a browser is "
                                 + "not supported in a "
                                 + "pre-Java 6 and non-JNLP "
                                 + "run-time environment.");
                                 }
                                 }*/
                            } catch (MalformedURLException mue) {

                                exception = mue;
                                //   } catch (java.net.URISyntaxException ue) {
                            } catch (Exception e) {
                            }
                        }
                        satellitePositionWindow.setProgressFinished();
                        satellitePositionWindow.resetCaptureButton();
                        interrupted = false;
                        tipsodMenuBar.resetView();

                        urls = null;
                    }
                } else { // an exception occurred

                    satellitePositionWindow.setProgressFinished();
                    satellitePositionWindow.resetCaptureButton();
                    interrupted = false;
                    urls = null;

                    if (isResourceLimitExceededException(exception)) {

                        JLabel message = new JLabel("<html>Your request has "
                                + "exceeded the SSC computer's resource limits.<br>"
                                + "Please resubmit your request using one of the "
                                + "following strategies:<ul>"
                                + "<li>Reduce the time range requested"
                                + "<li>Reduce the number of satellites selected"
                                + "<li>Increase the resolution factor"
                                + "</ul></html>");
                        // the message that is to be displayed
                        // in the dialog window

                        JOptionPane.showMessageDialog(OrbitViewer.satelliteChooser,
                                message,
                                "Resource limit exceeded",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {

                        System.err.println("Exception thrown by "
                                + "getDataFiles was: "
                                + exception.getMessage());

                        JOptionPane.showMessageDialog(null,
                                "Your request to the SSC computer failed for "
                                + "the following reason:\n"
                                + exception.getMessage(),
                                "Server request failure",
                                JOptionPane.INFORMATION_MESSAGE);

                    }
                } // end exception
            } // end finished()

            /**
             * Indicates whether the given exception is reporting a resource
             * limit exceeded exception.
             *
             * @param e the exception to be examined
             * @return true if the given exception is reporting a resource limit
             * exceeded exception. Otherwise, false.
             */
            private boolean isResourceLimitExceededException(final Exception e) {

                if (e instanceof SSCExternalException_Exception) {

                    String msg = e.getMessage().toLowerCase();
                    // the exception's message

                    if (msg.contains("resource")
                            && msg.contains("limit")
                            && msg.contains("exceeded")) {

                        return true;
                    }
                }
                return false;
            }

            /**
             * This is a Runnable class that is used to update the progress of
             * the retrieval operation. It is intended to be created in the
             * retrieval thread and then scheduled for execution on the main
             * event dispatching thread where the Swing progress components can
             * be updated.
             */
            private class UpdateProgressTask implements Runnable {

                /**
                 * The current progress value.
                 */
                private final int status;
                /**
                 * The progress note.
                 */
                private final String note;

                /**
                 * Creates an UpdateProgressTask.
                 *
                 * @param status the new progress value
                 * @param note the new progress note
                 */
                public UpdateProgressTask(final int status, final String note) {

                    this.status = status;
                    this.note = note;
                }

                @Override
                public void run() {

                    satellitePositionWindow.setProgressNote(note);
                }
            }
        } // end LocationRetrievalTask class
    }

    /**
     * Creates a new XMLGregorianCalendar representing the same instance in time
     * as that of the given Date.
     *
     * @param date Date value
     * @return XMLGregorianCalendar equivalent of the given date
     */
    private XMLGregorianCalendar newXMLGregorianCalendar(final Date date) {

        GregorianCalendar gregorianCalendar
                = new GregorianCalendar(Util.UTC_TIME_ZONE);
        // GregorianCalendar equivalent
        // of the given date
        gregorianCalendar.setTime(date);

        return datatypeFactory.newXMLGregorianCalendar(
                gregorianCalendar);
    }

    /**
     * Register/de registers this application with the <code>
     * javax.jnlp.SingleInstanceService</code>.
     *
     * @param value true causes this application to be registered as a
     * <code>SingleInstanceListener</code>. false causes this application to be
     * removed as a <code>
     *            SingleInstanceListener</code>.
     */
    public void setSingleInstanceApplication(final boolean value) {

        if (singleInstanceService != null) {

            if (value) {

                singleInstanceService.addSingleInstanceListener(this);
            } else {

                singleInstanceService.removeSingleInstanceListener(this);
            }
        }
    }

    /**
     * Responds to a new activation of this application.
     *
     * @param args application arguments
     */
    @Override
    public void newActivation(final String[] args) {

        if (args.length > 0) {

            GraphSpecification graphSpec
                    = GraphSpecification.getInstanceFromArgs(args);
            // graph specification based upon
            // arguments
            setSelections(graphSpec, satelliteChooser);
        }
    }

    /**
     * Terminates this application. The argument serves as a status code; by
     * convention, a nonzero status code indicates abnormal termination. This
     * method never returns normally.
     *
     * @param status exit status.
     */
    public final void exit(final int status) {

        setSingleInstanceApplication(false);

        System.exit(status);
    }

    /**
     *
     * @return the main menu
     */
    public static TIPSODMenuBar getTipsodMenuBar() {
        return tipsodMenuBar;
    }

    /**
     *
     * @param menuBar the main menu.
     */
    public final void setTipsodMenuBar(final TIPSODMenuBar menuBar) {
        OrbitViewer.tipsodMenuBar = menuBar;
    }

    /**
     *
     * @return the animation bar (play, pause stop).
     */
    public static TIPSODToolBar getToolBar() {
        return toolBar;
    }

    /**
     *
     * @param toolBar the container that group the animation components (play,
     * pause, stop).
     */
    public final void setToolBar(final TIPSODToolBar toolBar) {
        OrbitViewer.toolBar = toolBar;
    }

    /**
     *
     * @return panel that group together the Projection and Views radio choice
     * buttons.
     */
    public final ToolPanel getToolPane() {
        return toolPane;
    }

    /**
     *
     * @param toolPane panel that contains the Projection and Views choices.
     */
    public final void setToolPane(final ToolPanel toolPane) {
        this.toolPane = toolPane;
    }

    /**
     *
     * @return the slider that allows for satellites movement with respect to
     * time; either manually or automatically by using the tool bar components.
     */
    public static Slider getSlider() {
        return slider;
    }

    /**
     *
     * @param slider the slider that controls the satellites movement with
     * respect to time
     */
    public final void setSlider(final Slider slider) {
        OrbitViewer.slider = slider;
    }

    /**
     *
     * @return the main link to the Satellite Situation Center web services
     */
    public static SatelliteSituationCenterInterface getSsc() {
        return ssc;
    }

    /**
     *
     * @param ssc the main link to the Satellite Situation Center Web Services
     */
    public static void setSsc(final SatelliteSituationCenterInterface ssc) {
        OrbitViewer.ssc = ssc;
    }

    /**
     *
     * @return the panel that group together the display specific components
     * such as thickness and Axis Span and ticks count.
     */
    public static InfoPanel getInfoPane() {
        return infoPane;
    }

    /**
     *
     * @param infoPane the panel that group together the display components
     * (thickness...).
     */
    public final void setInfoPane(final InfoPanel infoPane) {
        OrbitViewer.infoPane = infoPane;
    }

    public static void setTogglePanel(String chosenPane) {

        CardLayout cl = (CardLayout) (togglePane.getLayout());
        cl.show(togglePane, chosenPane);

    }

    /**
     *
     * @return the top container within the VirtualUniverse for all branch
     * nodes.
     */
    public final javax.media.j3d.Locale get3DLocale() {
        return locale;
    }

    /**
     *
     * @param locale container for all of the branch nodes that form this
     * application tree.
     */
    public final void set3DLocale(final Locale locale) {
        this.locale = locale;
    }

    /**
     * The following allows this to be run as an application as well as an
     * applet
     *
     * @param args array of string arguments
     * @throws Exception returns any exceptions that were not caught previously.
     */
    public static void main(final String[] args) throws Exception {

        String osName = System.getProperty("os.name");

        if (osName != null && osName.contains("OS X")) {

           // String javaVersion = System.getProperty("java.version");
               double javaVersion = Util.getVersion();
            File j3dcoreJar
                    = new File("/System/Library/Java/Extensions/j3dcore.jar");

          //  if (javaVersion != null && javaVersion.charAt(2) > '6'
          if (javaVersion >= 1.7d
                    && j3dcoreJar.exists()) {

                String msg
                        = "The Java 6 3D libraries were found on your\n"
                        + "computer.  That version of the 3D libraries\n"
                        + "are incompatible with versions of Java newer\n"
                        + "than Java 6.  In order to run this application\n"
                        + "with a version of Java newer than 6, the old\n"
                        + "3D libraries must be removed.  Refer to the\n"
                        + "instructions at https://sscweb.gsfc.nasa.gov/.";

                JOptionPane.showOptionDialog(new JFrame(), msg,
                        "SSC 4D Orbit Viewer",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE, null,
                        new String[]{"Close"}, null);

                System.exit(0);
            } else if (javaVersion < 1.7d
                    && !j3dcoreJar.exists()) {

                String msg
                        = "The Java 6 3D libraries are missing from your\n"
                        + "computer.  The Java 6 3D libraries are \n"
                        + "required to run this application.  Please\n"
                        + "restore the libraries before attempting to\n"
                        + "run this version of this application.  Refer\n"
                        + "to the instructions at\n"
                        + "https://sscweb.gsfc.nasa.gov/.";

                JOptionPane.showOptionDialog(new JFrame(), msg,
                        "SSC 4D Orbit Viewer",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE, null,
                        new String[]{"Close"}, null);

                System.exit(0);
            }
      
        
       }

        SplashWindow splashWindow = SplashWindow.getInstance();
        splashWindow.setVisible(true);
        //
        // The following sleep is required in order for the splash 
        // window's contents (more than just a grey window) to 
        // (consistently) show on Solaris 10 with Java 1.5 and 1.6
        // (and possibly other platforms).  Once support for Java 1.5
        // is no longer required, this splash code should be replaced
        // by java.awt.SplashScreen.
        //
        Thread.sleep(500);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            //     @Override
            @Override
            public void run() {

                try {

                    Util.saveAppContext();

                    GraphSpecification graphSpec = null;
                    // graph specification based upon
                    // arguments
                    if (args.length > 0) {

                        graphSpec = GraphSpecification.getInstanceFromArgs(args);
                    }
                    OrbitViewer frame = new OrbitViewer();

                    //  frame.setSize(new Dimension(840, 740));
                    frame.setSize(new Dimension(960, 800));
                    frame.setLocationRelativeTo(null);

                    frame.setVisible(true);

                    if (satelliteChooser != null && satelliteChooser.isComplete()) {

                        satelliteChooser.setVisible(true);

                        if (graphSpec != null) {

                            frame.setSingleInstanceApplication(
                                    graphSpec.getSingleInstance());
                            setSelections(graphSpec, satelliteChooser);
                        }
                    } else if (isConnected()) {
                        JOptionPane.showMessageDialog(null,
                                " Spacecraft selection window is not available",
                                "information message",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {

                    JOptionPane.showMessageDialog(null,
                            "An unexpected error has occured:" + "\n"
                            + e.getCause().toString() + ".\n"
                            + "The application is being terminated.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    SplashWindow.getInstance().dispose();
                    System.exit(0);
                }
                SplashWindow.getInstance().dispose();

                checkForNewVersion();
            }
        });

    }

    /**
     * Sets the selection values in the given SelectionWindow based upon the
     * given command line arguments values.
     *
     * @param graphSpec graph specification values
     * @param selectionWindow the selection window
     */
    private static void setSelections(
            final GraphSpecification graphSpec,
            final SelectionWindow selectionWindow) {

        selectionWindow.performCancelAction();
        selectionWindow.setDefaultValues();

        if (graphSpec == null) {

            return;
        }

        Date beginDate = graphSpec.getBeginTime();
        // start of time range
        Date endDate = graphSpec.getEndTime();
        // end of time range
        CoordinateSystem coordSystem = graphSpec.getCoordinateSystem();
        // requested coordinate system
        int resolution = graphSpec.getResolution();
        // requested resolution
        java.util.List<String> sats = graphSpec.getSatellites();
        // requested satellites

        if (beginDate != null) {

            selectionWindow.setStartDate(beginDate);
        }

        if (endDate != null) {

            selectionWindow.setEndDate(endDate);
        }

        if (coordSystem != null) {

            selectionWindow.setCoordinateSystem(coordSystem);
        }

        if (resolution > 0) {

            selectionWindow.setResolution(resolution);
        }

        if (sats.size() > 0) {

            selectionWindow.setSatellitesSelected(sats);
        }

        selectionWindow.setTracing(graphSpec.getBFieldTrace());

        if (beginDate != null && endDate != null && sats.size() > 0) {

            selectionWindow.performGraphAction();
        }

    }

    /* private void startBrowser() {

     // if running from jnlp, then start browser will work
     try {

     bs = (BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
     if (!bs.isWebBrowserSupported()) {
     bs = null;
     }
     } catch (javax.jnlp.UnavailableServiceException ue) {
     bs = null;
     }
     }*/
    private static BasicService getBasicServiceObject() {
        try {

            BasicService bs = (BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
            if (bs != null && !bs.isWebBrowserSupported()) {
                return null;
            } else {
                return bs;
            }
        } catch (javax.jnlp.UnavailableServiceException ue) {
            return null;
        }
    }

    public static void showDocument(URL url) {

        try {

            Class<?> desktopClass
                    = Class.forName(
                            "java.awt.Desktop");
            // Desktop class
            Method getDesktopMethod
                    = desktopClass.getDeclaredMethod(
                            "getDesktop");
            // Desktop.getDesktop method
            Object desktop
                    = getDesktopMethod.invoke(null);
            // Desktop object
            Method browseMethod
                    = desktopClass.getDeclaredMethod(
                            "browse", URI.class
                    );
            // Desktop.browse method
            browseMethod.invoke(desktop, url.toURI());

        } catch (Exception e) {

            // Java < 1.6
            try {

                ((BasicService) basicServiceObject).showDocument(url);
                return;

            } catch (Exception ex) {
            }

            System.err.println(
                    "Failed to launch a "
                    + "browser for " + url + ".");

        }
    }

    /**
     * Determines if we are runnning in a JNLP environment.
     *
     * @return true if executing in a JNLP environment. Otherwise, false.
     */
    public static boolean runningInJnlpEnvironment() {

        try {

            ServiceManager.lookup("javax.jnlp.SingleInstanceService");

            return true;
        } catch (UnavailableServiceException e) {

            return false;
        }
    }

    /**
     * When executing on OS X, checks to see if there is a newer version of this
     * application available at our "home" URL. This check is only done on OS X
     * because that is the only platform where we run without Web Start. Web
     * Start is responsible for version checks/updates when we are running under
     * Web Start.
     *
     */
    private static void checkForNewVersion() {

        String newerVersion = getNewerVersionString();

        if (newerVersion != null) {

            String msg
                    = "<html>There is a newer version of the "
                    + "SSC 4D Orbit Viewer at<br>"
                    + getHomeUrl() + ".<br>"
                    + "Current " + getVersionString() + "<br>"
                    + "Most recent " + newerVersion + "<br>"
                    + "Do you want to visit the site now?</html>";
            // The message to display to the
            // user.  The initial value is
            // just a fragment of an HTML
            // message.

            int reply = JOptionPane.showConfirmDialog(
                    null, msg, "Update Available",
                    JOptionPane.YES_NO_OPTION);
            // user's reply
            if (reply == JOptionPane.YES_OPTION) {

                try {

                    showDocument(new URL(getHomeUrl()));
                } catch (MalformedURLException e) {

                    System.err.println(
                            "HomeUrl of " + getHomeUrl()
                            + " caused a MalformedURLException: "
                            + e.getMessage());
                }
            }
        }
    }

    /**
     * When executing on OS X, checks to see if there is a newer version of this
     * application available at our "home" URL. This check is only done on OS X
     * because that is the only platform where we run without Web Start. Web
     * Start is responsible for version checks/updates when we are running under
     * Web Start.
     *
     * @return a string representation of the newer version if one exists. If
     * there is no newer version, null is returned.
     */
    public static String getNewerVersionString() {

        if (!runningInJnlpEnvironment()) {

            String version = getVersionString();
            // String representation of our
            // version.
            if (version == null) {

                System.err.println("Could not determine the "
                        + "version of this application.");

                return null;
            }
            Integer[] iVersion = getVersion(version);
            if (iVersion == null) {

                System.err.println("Syntax error in the "
                        + "version of this application.");

                return null;
            }
            String upToDateVersion = getUpToDateVersionString();
            // String representation of the
            // most recent version available.
            if (upToDateVersion == null) {

                System.err.println("Could not determine the most "
                        + "recent version of this application.");

                return null;
            }
            Integer[] iUpToDateVersion = getVersion(upToDateVersion);
            if (iUpToDateVersion == null) {

                System.err.println("Syntax error in the most "
                        + "recent version of this application.");

                return null;
            }
            int compareToVersions
                    = compareToVersions(iVersion, iUpToDateVersion);
            // indicates whether our current
            // version is less then, equal 
            // to, or greater than the most
            // up-to-date version available.

            if (compareToVersions < 0) {

                return upToDateVersion;
            } else {

                return null;
            }
        }

        return null;
    }

    /**
     * Compares the two given version values and returns a negative, zero, or a
     * positive integer to indicate with the first value is less than, equal to,
     * or greater than the second value.
     *
     * @param v1 first value to compare.
     * @param v2 second value to compare.
     * @return a negative, zero, or a positive integer to indicate whether the
     * first value is less than, equal to, or greater than the second value.
     */
    private static int compareToVersions(
            Integer[] v1, Integer[] v2) {

        for (int i = 0; i < v1.length; i++) {

            int result = v1[i].compareTo(v2[i]);

            if (result != 0) {

                return result;
            }
        }

        return 0;
    }

    /**
     * A regular expression to capture the numeric version portion of this
     * application version from the version string which also contains a label
     * and date value.
     */
    private static final Pattern VERSION_PATTERN
            = Pattern.compile(
                    "^Version (.*),.*");

    /**
     * Gets an Integer array representation of this application's version value
     * from the String representation.
     *
     * @param versionStr String representation of this application's version
     * value.
     * @return Integer array representation of the given String representation.
     */
    private static Integer[] getVersion(String versionStr) {

        Matcher m = VERSION_PATTERN.matcher(versionStr);
        // version pattern matcher
        if (m.matches()) {

            String[] components = m.group(1).split("\\.");
            // component version values
            Integer[] version = new Integer[components.length];
            // Integer component version
            // values

            for (int i = 0; i < version.length; i++) {

                version[i] = new Integer(components[i]);
            }

            return version;
        } else {

            return null;
        }
    }

    /**
     * Gets this application's version (as a String value).
     *
     * @return this application's version.
     */
    public static String
            getVersionString() {

        InputStream inStream
                = OrbitViewer.class
                .getResourceAsStream("/build.txt");
        // input stream pointing to
        // file containing this 
        // application's version

        return getVersionString(inStream);
    }

    /**
     * Gets this application's "home" page URL.
     *
     * @return this application's home page URL.
     */
    public static String getHomeUrl() {
        

        return System.getProperty("home.url");
    }

    /**
     * Gets the "up-to-date" version value of this application from this
     * application's "home" location.
     *
     * @return up-to-date version of this application. A null value is returned
     * if the value cannot be determined.
     */
    public static String getUpToDateVersionString() {

        try {

            String homeUrl = getHomeUrl();
            // url of this app.
            if (homeUrl == null) {

                System.err.println(
                        "OrbitViewer.getUpToDateVersionString: home URL "
                        + "is null.  Cannot determine up-to-date version.");
                return null;
            }
            URL versionUrl = new URL(getHomeUrl() + "build.txt");
            // url of file containing
            // the most recent version

            return getVersionString(versionUrl.openStream());
        } catch (MalformedURLException e) {

            System.err.println("MalformedURLExeption: "
                    + getHomeUrl() + "build.txt" + ": " + e.getMessage());

        } catch (IOException e) {

            System.err.println("IOException: "
                    + getHomeUrl() + "build.txt" + ": " + e.getMessage());
        }

        return null;
    }

    /**
     * Gets the version string from the given InputStream.
     *
     * @param versionStream InputStream from which to read the version String
     * value.
     * @return String contianing the version value read from the given
     * versionStream.
     */
    private static String getVersionString(
            InputStream versionStream) {

        String version = null;         // version value
        BufferedReader reader
                = new BufferedReader(new InputStreamReader(versionStream));
        // reader for reading the
        // version value
        try {

            version = reader.readLine();
        } catch (IOException e) {

            System.err.println(
                    "IOException while reading version: " + e.getMessage());
        } finally {

            try {

                reader.close();
            } catch (IOException e) {

                System.err.println("IOException while attempting to "
                        + "close reader: " + e.getMessage());
            }
        }

        return version;
    }
}

