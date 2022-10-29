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
 * $Id: ZoomControl.java,v 1.16 2017/03/06 20:05:00 rchimiak Exp $
 * Created on November 16, 2006, 10:47 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.Hashtable;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import javax.swing.Box;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import static gov.nasa.gsfc.spdf.orb.OrbitViewer.BEIGE;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.view.ViewBranch;
import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SwitchGroup;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.orb.content.shapes.MajorTickMark;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import gov.nasa.gsfc.spdf.orb.utils.PrintHandler;
import gov.nasa.gsfc.spdf.orb.utils.SaveHandler;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.view.ViewBranch.MyOrbitBehavior;
import java.awt.Dimension;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author rachimiak
 */
public class ZoomControl extends JFrame {

    private final ViewBranch viewBranch = new ViewBranch();
    private SatBranch satBranch = null;
    private int selection = 0;
    private Point3d zoomCenter = null;
    private final Vector3d v = new Vector3d();
    private ZoomAxis zoomAxis;
    final static String RESPAN = "Re Span";
    final static String KMSPAN = "Km Span";
    private JPanel cards = new JPanel(new CardLayout());
    private boolean locked = false;
    private JRadioButton km = new JRadioButton("Kilometers", false);
    private JRadioButton re = new JRadioButton(ControlPanel.isSolenocentric() ? "Lunar Radii" : "Earth Radii", true);
    private final JRadioButton axisOn = new JRadioButton("On", true);
    private final JRadioButton axisOff = new JRadioButton("Off", false);
    private final JPanel planarPanel;
    private static final int KM_TICK_SPACING = 10;
    private static final int KM_SLIDER_MIN = 10;
    private static final int KM_SLIDER_MAX = 10000;
    private static final int KM_SLIDER_VALUE = 100;
    private static final int RE_TICK_SPACING = 10;
    private static final int RE_SLIDER_MIN = 1;
    private static final int RE_SLIDER_MAX = 1000;
    private static final int RE_SLIDER_VALUE = 1;
    private static final int MAJOR_TICK_SPACING = 10;
    private static final int MINOR_TICK_SPACING = 10;
    private static final int TICKS_SLIDER_MIN = 1;
    private static final int TICKS_SLIDER_MAX = 100;
    private static final int TICKS_SLIDER_VALUE = 1;
    private static final int ZOOM_FRAME_X_LOCATION = 50;
    private static final int ZOOM_FRAME_Y_LOCATION = 50;
    private static final int ZOOM_FRAME_WIDTH = 960;
    private static final int ZOOM_FRAME_HEIGHT = 760;
    private static double ORIGINAL_SCALE = 1d;

    final JSpinner zoomFactorSpinner = new JSpinner();
    final JSpinner reSpanSpinner = new JSpinner();
    final JSpinner kmSpanSpinner = new JSpinner();
    final JSpinner majorSpinner = new JSpinner();
    final JSlider reSpanSlider;
    final JSlider kmSpanlider;
    final JSlider majorSlider;

    /**
     * Creates a new instance of ZoomControl
     */
    public ZoomControl(final OrbitViewer ov, final int selection, final boolean locked, int projection) {

        super("Zoom");
        this.selection = selection;
        this.locked = locked;
        satBranch = ContentBranch.getSatBranch();
        zoomAxis = new ZoomAxis(ov);
        reSpanSlider = getReSlider();
        kmSpanlider = getKmSlider();
        majorSlider = getTicksSlider();

        setLocation(ZOOM_FRAME_X_LOCATION, ZOOM_FRAME_Y_LOCATION);
        setSize(ZOOM_FRAME_WIDTH, ZOOM_FRAME_HEIGHT);

        JPanel p = new JPanel(new BorderLayout());
        p.add(viewBranch.getCanvas());
        p.setBorder(BorderFactory.createEtchedBorder());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(p, BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        //***************************create planar View Branches********************************************
        JPanel[] arrayOfPan = new JPanel[3];

        for (int i = 0; i < 3; i++) {
            arrayOfPan[i] = new JPanel(new BorderLayout());

            arrayOfPan[i].add(new PlanarLabel(i + 1), BorderLayout.NORTH);

        }
        planarPanel
                = new JPanel(new GridLayout(3, 1));

        planarPanel.setPreferredSize(new Dimension(
                180, 150));

        ViewBranch[] viewBranchArray = new ViewBranch[3];

        for (int i = 0; i < 3; i++) {
            viewBranchArray[i] = new ViewBranch();
            ov.get3DLocale().addBranchGraph(viewBranchArray[i]);

            arrayOfPan[i].add(viewBranchArray[i].getCanvas());
            arrayOfPan[i].setBorder(new LineBorder(BEIGE, 1));
            planarPanel.add(arrayOfPan[i]);
            viewBranchArray[i].setSatBranch(satBranch);
        }

        mainPanel.add(planarPanel, BorderLayout.EAST);

        planarPanel.setVisible(OrbitViewer.getPlanarPanel().isVisible());

        //***************************end planar View Branches******************************************** 
        JPanel left = new JPanel(new BorderLayout());

        JPanel control = new JPanel();
        control.setLayout(new BorderLayout());

        left.add(control);

        JPanel northernPane = new JPanel();
        northernPane.setLayout(new BoxLayout(northernPane, BoxLayout.Y_AXIS));

        JPanel zoomFactorPane = new JPanel(new BorderLayout());

        TitledBorder zoomFactorTitle = BorderFactory.createTitledBorder("Zoom Factor:");
        zoomFactorTitle.setTitleFont(Util.labelFont);
        zoomFactorPane.setBorder(zoomFactorTitle);

        JPanel factorSpinnerPane = new JPanel();

        Integer value = 1;
        Integer min = 1;
        Integer max = 4000;
        Integer step = 1;

        zoomFactorSpinner.setModel(new SpinnerNumberModel(value, min, max, step));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(zoomFactorSpinner, "#"); 
        zoomFactorSpinner.setEditor(editor);
        zoomFactorSpinner.setPreferredSize(new Dimension(80, 23));

        zoomFactorSpinner.setFont(Util.plainFont);

        zoomFactorSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();

                double v = (((Integer) s.getValue()).doubleValue() - 1) / 10d;

                viewBranch.getView().setScreenScale(ORIGINAL_SCALE * (1d + v));

            }
        });

        factorSpinnerPane.add(zoomFactorSpinner);
        zoomFactorPane.add(factorSpinnerPane);
        northernPane.add(zoomFactorPane);
        northernPane.add(new WidthPanel());

        control.add(northernPane, BorderLayout.NORTH);

        JPanel zoomAxisPane = new JPanel();
        GridLayout gl = new GridLayout(3, 1);
        zoomAxisPane.setLayout(gl);

        JPanel legend = new JPanel();

        JLabel text = new JLabel("<html><font style color= #FA58F4  size = +0><I>----</I> x</font>&nbsp;&nbsp;&nbsp; <font style color= #D7DF01 size = +0><I>----</I> y</font>&nbsp;&nbsp; <br><font style color= #2EFEF7 size = +0 ><I>----</I> z</font><br></html>");
        legend.add(text);
        legend.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        left.add(legend, BorderLayout.SOUTH);

        mainPanel.add(left, BorderLayout.WEST);

        JPanel north = new JPanel();

        JButton print = new JButton((" print    "), new ImageIcon(ZoomControl.class.getResource("/images/print16.gif")));
        print.setFont(Util.labelFont);
        print.setBorder(BorderFactory.createEmptyBorder());

        print.addActionListener(new PrintHandler(ov, viewBranch.getCanvas()));

        JButton saveAs = new JButton((" save as "), new ImageIcon(ZoomControl.class.getResource("/images/save16.gif")));
        saveAs.setFont(Util.labelFont);
        saveAs.setBorder(BorderFactory.createEmptyBorder());
        saveAs.addActionListener(new SaveHandler(ov, viewBranch.getCanvas()));

        JButton close = new JButton((" exit "),
                new ImageIcon(ZoomControl.class.getResource("/images/Exit.gif")));
        close.setFont(Util.labelFont);
        close.setBorder(BorderFactory.createEmptyBorder());
        close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                satBranch.removeZoom();
            }
        });

        north.add(print);
        north.add(Box.createHorizontalStrut(30));
        north.add(saveAs);
        north.add(Box.createHorizontalStrut(30));
        north.add(close);
        mainPanel.add(north, BorderLayout.NORTH);

        Font oldfont = re.getFont();
        Font font = new Font(oldfont.getName(), Font.PLAIN, oldfont.getSize());
        JPanel axisPane = new JPanel();

        axisPane.setLayout(new GridLayout(3, 1));

        axisOn.setFont(font);
        axisOn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                SwitchGroup[] swtch = zoomAxis.getSwichGroup();

                for (SwitchGroup swtch1 : swtch) {
                    swtch1.setChildMask(swtch1.getOptions()[0]);
                }
                re.setEnabled(true);
                km.setEnabled(true);
                reSpanSlider.setEnabled(true);
                kmSpanlider.setEnabled(true);
                majorSlider.setEnabled(true);
            }
        });

        axisOff.setFont(font);
        axisOff.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                SwitchGroup[] swtch = zoomAxis.getSwichGroup();
                for (SwitchGroup swtch1 : swtch) {
                    swtch1.setChildMask(swtch1.getOptions()[1]);
                }
                re.setEnabled(false);
                km.setEnabled(false);
                reSpanSlider.setEnabled(false);
                kmSpanlider.setEnabled(false);
                majorSlider.setEnabled(false);
            }
        });

        ButtonGroup axisGroup = new ButtonGroup();
        axisGroup.add(axisOn);
        axisGroup.add(axisOff);

        JPanel unitsPane = new JPanel();
        unitsPane.setLayout(new GridLayout(3, 1));

        re.setFont(font);
        re.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, RESPAN);
                resetSlider(reSpanSlider);
            }
        });

        km.setFont(font);
        km.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, KMSPAN);
                resetSlider(kmSpanlider);
            }
        });

        ButtonGroup unitGroup = new ButtonGroup();
        unitGroup.add(re);
        unitGroup.add(km);

        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));

        JLabel axisLabel = new JLabel(" Display:");
        axisLabel.setFont(Util.labelFont);

        axisPane.add(axisLabel);
        axisPane.add(axisOn);
        axisPane.add(axisOff);
        pan.add(axisPane);

        JLabel unitLabel = new JLabel(" Units:");
        unitLabel.setFont(Util.labelFont);
        unitsPane.add(unitLabel);
        unitsPane.add(re);
        unitsPane.add(km);
        pan.add(unitsPane);

        zoomAxisPane.add(pan);

        JPanel reSpanPane = new JPanel(new BorderLayout());
        reSpanPane.setBorder(new EmptyBorder(1, 18, 5, 18));
        reSpanPane.add(reSpanSpinner, BorderLayout.NORTH);
        reSpanPane.add(reSpanSlider);
        cards.add(reSpanPane, RESPAN);

        JPanel kmSpanPane = new JPanel(new BorderLayout());
        kmSpanPane.setBorder(new EmptyBorder(1, 18, 5, 18));
        kmSpanPane.add(kmSpanSpinner, BorderLayout.NORTH);
        kmSpanPane.add(kmSpanlider);
        cards.add(kmSpanPane, KMSPAN);

        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, RESPAN);

        JPanel span = new JPanel(new BorderLayout());

        TitledBorder spanTitle = BorderFactory.createTitledBorder(" Span:");

        spanTitle.setTitleFont(Util.labelFont);

        span.setBorder(spanTitle);

        span.add(cards);

        reSpanSpinner.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        reSpanSpinner.setPreferredSize(new Dimension(80, 23));
        reSpanSpinner.setFont(Util.plainFont);
        reSpanSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();
                reSpanSlider.setValue((Integer) s.getValue());
            }
        });

        kmSpanSpinner.setModel(new SpinnerNumberModel(100, 10, 10000, 10));
        kmSpanSpinner.setPreferredSize(new Dimension(80, 23));
        kmSpanSpinner.setFont(Util.plainFont);
        kmSpanSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();
                kmSpanSpinner.setValue((Integer) s.getValue());
            }
        });

        zoomAxisPane.add(span);

        JPanel major = new JPanel(new BorderLayout());
        TitledBorder majorTitle = BorderFactory.createTitledBorder(" Ticks:");
        majorTitle.setTitleFont(Util.labelFont);
        major.setBorder(majorTitle);

        JPanel majorPane = new JPanel(new BorderLayout());
        majorPane.setBorder(new EmptyBorder(1, 18, 5, 18));
        majorPane.add(majorSpinner, BorderLayout.NORTH);
        majorPane.add(majorSlider);

        majorSpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        majorSpinner.setPreferredSize(new Dimension(80, 23));

        majorSpinner.setFont(Util.plainFont);
        majorSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();
                majorSlider.setValue((Integer) s.getValue());
            }
        });

        major.add(majorPane);
        zoomAxisPane.add(major);

        control.add(zoomAxisPane);

        TitledBorder zoomAxisTitle = BorderFactory.createTitledBorder("Zoom Axis");
        zoomAxisTitle.setTitleFont(Util.labelFont);
        zoomAxisPane.setBorder(zoomAxisTitle);

        viewBranch.setSatBranch(satBranch);
        viewBranch.setOrbitBehavior();
        viewBranch.setZoomView(
                OrbitViewer.getViewBranchArray()[0].getViewingPlatform().
                getViewPlatformTransform(), selection, projection);

        ORIGINAL_SCALE = viewBranch.getView().getScreenScale();

        for (int i = 0; i < 3; i++) {

            viewBranchArray[i].setZoomPlanarView(Axis.axisPlacement[i], selection);
            viewBranchArray[i].setPlanarOrbitBehavior();
            viewBranch.getOrbitBehavior().register(viewBranchArray[i].getOrbitBehavior());

        }

        viewBranch.getOrbitBehavior().setLocked(locked);
        if (locked) {

            java.util.Enumeration e = ov.get3DLocale().getAllBranchGraphs();
            while (e.hasMoreElements()) {

                BranchGroup bg = (BranchGroup) e.nextElement();
                if (bg instanceof ViewBranch) {

                    MyOrbitBehavior ob = ((ViewBranch) bg).getOrbitBehavior();
                    if (ob != null && ob.isLocked()) {

                        ob.addCallback(viewBranch);
                        viewBranch.getOrbitBehavior().addCallback((ViewBranch) bg);
                    }
                }
            }
        }
        setVisible(true);
        ov.get3DLocale().addBranchGraph(viewBranch);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                satBranch.removeZoom();
            }
        });
    }
    
    public void setzoomFactorSpinner(int factor){
        
        zoomFactorSpinner.setValue(factor);
    }

    public ViewBranch getViewBranch() {

        return viewBranch;
    }

    public JPanel getPlanarPanel() {

        return planarPanel;
    }

    public boolean isLocked() {
        return locked;
    }

    public final JSlider getTicksSlider() {

        final JSlider slider = new LogarithmicJSlider(JSlider.VERTICAL, TICKS_SLIDER_MIN, TICKS_SLIDER_MAX, TICKS_SLIDER_VALUE);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(MAJOR_TICK_SPACING);
        slider.setMinorTickSpacing(MINOR_TICK_SPACING);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                majorSpinner.setValue((int) slider.getValue());
                if (satBranch != null && !slider.getModel().getValueIsAdjusting()) {
                    zoomAxis.addMajorTicks(slider.getValue());
                }
            }

        }
        );
        return slider;
    }

    protected Hashtable< Integer, JLabel> getLabelTable() {

        Hashtable< Integer, JLabel> labels;
        labels = new Hashtable< Integer, JLabel>(4);
        for (int q = 0; q <= 1000; q += 200) {
            labels.put(q,
                    new JLabel("" + q / 100, JLabel.CENTER));
        }

        return labels;
    }

    public final JSlider getKmSlider() {

        final JSlider slider = new LogarithmicJSlider(JSlider.VERTICAL, KM_SLIDER_MIN, KM_SLIDER_MAX, KM_SLIDER_VALUE);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(KM_TICK_SPACING);
        slider.setMinorTickSpacing(KM_TICK_SPACING);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                // logVal.setText("Value is: " + (double) slider.getValue());
                if (satBranch != null) {
                    kmSpanSpinner.setValue((int) slider.getValue());
                    zoomAxis.scale(((double) slider.getValue()) / (6378d));
                }
            }
        });
        return slider;
    }

    public final JSlider getReSlider() {

        final JSlider slider = new LogarithmicJSlider(JSlider.VERTICAL, RE_SLIDER_MIN, RE_SLIDER_MAX, RE_SLIDER_VALUE);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(RE_TICK_SPACING);
        slider.setMinorTickSpacing(RE_TICK_SPACING);

        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent event) {

                if (satBranch != null) {
                    reSpanSpinner.setValue((int) slider.getValue());
                    zoomAxis.scale(ControlPanel.isSolenocentric()
                            ? ((double) slider.getValue())
                            * PhysicalConstants.MOON_TO_EARTH_RADIUS
                            : (double) slider.getValue());
                }
            }
        });
        return slider;
    }

    public void resetSlider(JSlider slider) {

        slider.getModel().setValueIsAdjusting(true);
        slider.getModel().setValueIsAdjusting(false);
    }

    public ViewBranch getView() {
        return viewBranch;
    }

    public int getSelection() {
        return selection;
    }

    public void setRotationCenter(Point3f p) {

        setRotationCenter(new Point3d((double) p.x, (double) p.y, (double) p.z));
    }

    private void setRotationCenter(Point3d p) {

        viewBranch.getOrbitBehavior().setRotationCenter(p);
    }

    public void setSpacecraftZoom(double x, double y, double z) {

        Transform3D ta = new Transform3D();

        if (zoomCenter == null) {
            zoomCenter = new Point3d(x, y, z);
        }

        TransformGroup tg = viewBranch.getViewingPlatform().getViewPlatformTransform();

        tg.getTransform(ta);
        ta.get(v);
        v.x -= -x + zoomCenter.x;
        v.y -= -y + zoomCenter.y;
        v.z -= -z + zoomCenter.z;

        zoomCenter.x = x;
        zoomCenter.y = y;
        zoomCenter.z = z;

        ta.setTranslation(v);
        tg.setTransform(ta);

        setRotationCenter(zoomCenter);
    }

    public TransformGroup getAxisTransformGroup() {
        return zoomAxis.getTransformGroup();
    }

    public Transform3D getAxisTransform() {
        return zoomAxis.getTransform();
    }

    public ZoomAxis getZoomAxis() {
        return zoomAxis;
    }

    public Axis getAxis() {
        return zoomAxis.getAxis();
    }

    public MajorTickMark getMajorTicks() {
        return zoomAxis.getMajorTicks();
    }

    public int getTickCount() {
        return majorSlider.getValue();
    }

    public class ZoomAxis {

        static final int SPAN = 0;
        static final int MAJOR = 1;
        private Axis zoAxis = null;
        private MajorTickMark majorTicks = null;
        private final BranchGroup branchGroup = new BranchGroup();
        private final TransformGroup transformGroup = new TransformGroup();
        private double scale = 3d;
        private int majorTicksCount = 1;
        private final SwitchGroup[] switchGroup = new SwitchGroup[2];
        private final InfoPanel infoPane;
        private final Transform3D transform = new Transform3D();

        public ZoomAxis(OrbitViewer ov) {

            infoPane = OrbitViewer.getInfoPane();

            branchGroup.setCapability(BranchGroup.ALLOW_DETACH);
            branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            branchGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

            branchGroup.setUserData("zoomAxis");

            for (int i = 0; i < switchGroup.length; i++) {

                switchGroup[i] = new SwitchGroup();
                switchGroup[i].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
                switchGroup[i].setCapability(Group.ALLOW_CHILDREN_WRITE);
                switchGroup[i].setCapability(SwitchGroup.ALLOW_CHILDREN_EXTEND);
                switchGroup[i].setChildMask(switchGroup[i].getOptions()[0]);
                branchGroup.addChild(switchGroup[i]);
            }
            Color3f[] clrs = new Color3f[3];

            clrs[0] = new Color3f(0.98f, 0.3451f, 0.957f); //red
            clrs[1] = new Color3f(0.8431f, 0.875f, 0.004f); //green
            clrs[2] = new Color3f(0.181f, 0.996f, 0.969f); //blue
            zoAxis = new Axis(infoPane, clrs);
            zoAxis.setScale(ControlPanel.isSolenocentric() ? (4d) * PhysicalConstants.MOON_TO_EARTH_RADIUS : 1d);

            majorTicks = new MajorTickMark(infoPane);

            switchGroup[0].addChild(zoAxis);
            switchGroup[1].addChild(majorTicks);
            transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            transformGroup.addChild(branchGroup);

        }

        public SwitchGroup[] getSwichGroup() {

            return switchGroup;
        }

        public void scale(double virtualScale) {

            transformGroup.getTransform(transform);

            this.scale = virtualScale;

            transform.setScale(scale);
            transformGroup.setTransform(transform);
            addMajorTicks(ZoomControl.this.majorSlider.getValue());
        }

        public void addMajorTicks(int majorTicksCount) {

            switchGroup[1].removeAllChildren();

            majorTicks = new MajorTickMark(majorTicksCount, infoPane, (SatBranch.getSymbolSize() * infoPane.getSatWidth() / transform.getScale()));
            majorTicks.setTransform(zoAxis.getTransform());
            BranchGroup b = new BranchGroup();
            b.setCapability(BranchGroup.ALLOW_DETACH);
            b.addChild(majorTicks);

            switchGroup[1].addChild(b);
            this.majorTicksCount = majorTicksCount;
        }

        public Axis getAxis() {
            return zoAxis;
        }

        public MajorTickMark getMajorTicks() {
            return majorTicks;
        }

        public TransformGroup getTransformGroup() {

            return transformGroup;
        }

        public Transform3D getTransform() {

            return transform;
        }
    }
}
