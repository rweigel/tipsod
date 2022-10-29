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
 * $Id: SelenoSatBranch.java,v 1.5 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.behaviors.AnimBehavior;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SelenoAnimation;
import gov.nasa.gsfc.spdf.orb.content.shapes.AnimatedShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.orb.content.shapes.AxisText;
import gov.nasa.gsfc.spdf.orb.content.shapes.GeoOrbitShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Grid;
import gov.nasa.gsfc.spdf.orb.content.shapes.MajorTickMark;
import gov.nasa.gsfc.spdf.orb.content.shapes.Moon;
import gov.nasa.gsfc.spdf.orb.content.shapes.OrbitShape;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SelenoSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.SelenoTogglePanel;
import gov.nasa.gsfc.spdf.orb.utils.EarthSurfaces;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import gov.nasa.gsfc.spdf.orb.utils.SolenocentricUtils;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateData;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JCheckBox;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author rchimiak
 */
public class SelenoSatBranch extends SatBranch {

    private CoordinateSystem coordinateSystem = null;
    private List<SatelliteData> location = null;
    private SolenocentricUtils sse = null;
    private TransformGroup earthTransformGroup = null;
    private static final int SURFACES = 3;

    /**
     * Creates the scene branch containing graphical objects.
     *
     * @param tempLocation an array of SatelliteLocation instances
     * @throws java.lang.CloneNotSupportedException
     */
    public SelenoSatBranch(final List<SatelliteData> tempLocation) throws CloneNotSupportedException {

        super();

        earthTransformGroup = new TransformGroup();
        earthTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        earthTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        earthTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        earthTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

        OrbitViewer.setTogglePanel(OrbitViewer.SELENO_TOGGLE_PANE);

        switchArray = new Switch[SelenoTogglePanel.getCheckBoxArray().length];
        ControlPanel.isImport = false;

        //--------------set Satellite Position Window-----------------
        SelenoSatellitePositionWindow positionWindow = (SelenoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow(ControlPanel.Body.MOON.ordinal());
        SatelliteGraphTableModel graphModel = OrbitViewer.getSatelliteChooser().getSatModel();

        this.location = tempLocation;

        if (tempLocation.size() > 0) {

            sse = new SolenocentricUtils(location);

            positionWindow.setSse(sse);
            location.remove(0);
        }
        for (SatelliteData temp : location) {

            graphModel.verifyTimes(temp);

        }

        coordinateSystem = location.get(0).getCoordinates().get(0).getCoordinateSystem();

        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][SURFACES];
        int i = 0;
        for (SatelliteData satData : location) {

            if (satData != null) {

                satNames[i] = satData.getId();

                List<CoordinateData> coords = satData.getCoordinates();
                coordinates[i][0] = PhysicalConstants.kmToRe(
                        coords.get(0).getX().get(0));
                coordinates[i][1] = PhysicalConstants.kmToRe(
                        coords.get(0).getY().get(0));
                coordinates[i][2] = PhysicalConstants.kmToRe(
                        coords.get(0).getZ().get(0));

            }
            i++;
        }

        sgp = graphModel.getSelectedSatelliteGraphProperties(satNames);

        if (satNames[0] != null) {

            model = new SatellitePositionTableModel(
                    satNames, sgp, coordinates, distances);

            positionWindow.getTable().setModel(model);
            positionWindow.clearCheckBoxes();
            positionWindow.getTable().calcColumnWidths();

            positionWindow.pack();

            positionWindow.getCoordinateField().setText("SSE");

            boolean[] state = {coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSE),};
            positionWindow.setSurfacesEnabled(state);

            positionWindow.getCoordBorder().setTitle("RM" + "/\u00B0):");

            positionWindow.getDistanceBorder().setTitle(
                    "distance to (RM):");

            positionWindow.getTimeField().setValue(OrbitViewer.getControlPane().getStartDate());

            positionWindow.resetCaptureButton();

            graphModel.addTableModelListener(stChangeListnr);

        }
        //-----------------create fixed shape node -------------------------

        animatedShapeTarget = new TransformGroup[location.size()];

        shape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

        //-------create the checkbox switches and the elements they control-------------
        addChild(tg);

        positionWindow.setFootpointsEnabled(false);

        os = new OrbitShape[location.size()];

        int k = 0;
        for (SatelliteData satData : location) {

            os[k] = new GeoOrbitShape(satData, sgp[k], graphModel, OrbitViewer.getInfoPane());
            k++;
        }
        tg.addChild(checkBoxSwitches());

        //need to deal differently with the planar views.  if visible set the checkbox otherwise
        //uncheck the box
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            animatedShapeTarget[j] = new TransformGroup();
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            animatedShapeTarget[j].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            animatedShapeTarget[j].setCapability(Group.ALLOW_CHILDREN_WRITE);
            AnimatedShape animatedShape = new AnimatedShape(sgp[j].getShape().toString(), new Color3f(sgp[j].getColor()), sgp[j].getName());
            animatedShapeTarget[j].addChild(animatedShape);
        }

        anim = new SelenoAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
        tg.addChild(anim);

        earthTransformGroup.addChild(ContentBranch.getEarthGroup());
        shape.addChild(anim.getAnimBranch());

       // tg.addChild(checkBoxSwitches());
        //need to deal differently with the planar views.  if visible set the checkbox otherwise
        //uncheck the box
        SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.PLANAR_VIEWS).
                setSelected(OrbitViewer.getPlanarPanel().isVisible());
        //setSelected(OrbitViewer.getPlanarPanel().isVisible());

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            anim.getAnimBranch().addChild(anim.getTarget()[j]);

            shape.addChild(os[j]);

        }

        positionWindow.setVisible(true);

        OrbitViewer.getTipsodMenuBar().getExportMenu().setEnabled(true);
        OrbitViewer.getTipsodMenuBar().getImportMenu().setEnabled(true);

    }

    /**
     * Creates the scene branch containing graphical objects. Constructed when
     * the import function is used
     *
     * @param tempLocation an array of SatelliteLocation instances
     * @param sgp satellite graph properties in this imported file.
     */
    public SelenoSatBranch(final List<SatelliteData> tempLocation,
            final SatelliteGraphProperties[] sgp) {

        super();

        earthTransformGroup = new TransformGroup();
        earthTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        earthTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        earthTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        earthTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

        this.sgp = sgp;
        ControlPanel.isImport = true;
        ControlPanel.isSolenocentric = true;

        OrbitViewer.setTogglePanel(OrbitViewer.SELENO_TOGGLE_PANE);

        switchArray = new Switch[SelenoTogglePanel.getCheckBoxArray().length];
        if (OrbitViewer.getSatelliteChooser() != null) {
            OrbitViewer.getSatelliteChooser().setVisible(false);
        }

        if (OrbitViewer.getControlPane() != null) {

            OrbitViewer.setPositionWindow(ControlPanel.Body.MOON.ordinal());
        }

        SelenoSatellitePositionWindow positionWindow = (SelenoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow();

        positionWindow.setSse(sse);

        this.location = new ArrayList<SatelliteData>();

        this.location.addAll(tempLocation);

        //--------------set Satellite Position Window-----------------
        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][SURFACES];

        coordinateSystem
                = location.get(0).getCoordinates().get(0).
                getCoordinateSystem();

        int i = 0;
        for (SatelliteData satData : location) {

            if (satData != null) {

                satNames[i] = satData.getId();

                List<CoordinateData> coords = satData.getCoordinates();
                coordinates[i][0] = PhysicalConstants.kmToRe(
                        coords.get(0).getX().get(0));
                coordinates[i][1] = PhysicalConstants.kmToRe(
                        coords.get(0).getY().get(0));
                coordinates[i][2] = PhysicalConstants.kmToRe(
                        coords.get(0).getZ().get(0));
            }
            i++;

        }

        if (satNames[0] != null) {

            model = new SatellitePositionTableModel(
                    satNames, sgp, coordinates, distances);

            positionWindow.getTable().setModel(model);
            positionWindow.clearCheckBoxes();
            positionWindow.getTable().calcColumnWidths();

            positionWindow.pack();

            positionWindow.getCoordinateField().setText("SSE");

            boolean[] state = {coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSM)};
            positionWindow.setSurfacesEnabled(state);

        }

        //-----------------create fixed shape node -------------------------
        animatedShapeTarget = new TransformGroup[location.size()];

        shape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

        //-------create the checkbox switches and the elements they control-------------
        addChild(tg);

        positionWindow.setFootpointsEnabled(false);

        os = new OrbitShape[location.size()];

        for (int j = 0; j < os.length; j++) {

            os[j] = new GeoOrbitShape(location.get(j), sgp[j], null,
                    OrbitViewer.getInfoPane());
        }
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            animatedShapeTarget[j] = new TransformGroup();
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            animatedShapeTarget[j].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            animatedShapeTarget[j].setCapability(Group.ALLOW_CHILDREN_WRITE);

            // float radius = symbolSize * OrbitViewer.getInfoPane().getSatWidth();
            //AnimatedShape animatedShape = new AnimatedShape(sgp[j].getShape().toString(), radius, new Color3f(sgp[j].getColor()), sgp[j].getName());
            AnimatedShape animatedShape = new AnimatedShape(sgp[j].getShape().toString(), new Color3f(sgp[j].getColor()), sgp[j].getName());
            animatedShapeTarget[j].addChild(animatedShape);
        }

        anim = new SelenoAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));

        tg.addChild(anim);

        earthTransformGroup.addChild(ContentBranch.getEarthGroup());
        shape.addChild(anim.getAnimBranch());

        tg.addChild(checkBoxSwitches());

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {
            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            anim.getAnimBranch().addChild(anim.getTarget()[j]);

            shape.addChild(os[j]);

        }

        positionWindow.setVisible(true);
    }

    @Override
    public void addLocation(final List<? extends Object> tempLocation) {

        super.addLocation(tempLocation);

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.ORBIT)].addChild(shapeBranch);

        //--------------set Satellite Position Window-----------------
        SelenoSatellitePositionWindow positionWindow = (SelenoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow(ControlPanel.Body.MOON.ordinal());
        SatelliteGraphTableModel graphModel = OrbitViewer.getSatelliteChooser().getSatModel();

        this.location = (List<SatelliteData>) tempLocation;

        if (tempLocation.size() > 0) {

            sse = new SolenocentricUtils(location);

            positionWindow.setSse(sse);
            location.remove(0);
        }

        for (SatelliteData temp : location) {

            graphModel.verifyTimes(temp);

        }
        coordinateSystem = location.get(0).getCoordinates().get(0).getCoordinateSystem();

        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][3];
        int i = 0;
        for (SatelliteData satData : location) {

            if (satData != null) {

                satNames[i] = satData.getId();

                List<CoordinateData> coords = satData.getCoordinates();
                coordinates[i][0] = PhysicalConstants.kmToRe(
                        coords.get(0).getX().get(0));
                coordinates[i][1] = PhysicalConstants.kmToRe(
                        coords.get(0).getY().get(0));
                coordinates[i][2] = PhysicalConstants.kmToRe(
                        coords.get(0).getZ().get(0));
            }
            i++;
        }

        try {
            sgp = graphModel.getSelectedSatelliteGraphProperties(satNames);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SelenoSatBranch.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (satNames[0] != null) {

            model = new SatellitePositionTableModel(
                    satNames, sgp, coordinates, distances);

            positionWindow.getTable().setModel(model);
            positionWindow.clearCheckBoxes();
            positionWindow.getTable().calcColumnWidths();

            positionWindow.pack();

            positionWindow.getCoordinateField().setText(OrbitViewer.getControlPane().getCoordinateSystem().toString());

            boolean[] state = {coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSM)};
            positionWindow.setSurfacesEnabled(state);

            positionWindow.getTimeField().setValue(OrbitViewer.getControlPane().getStartDate());

            positionWindow.resetCaptureButton();

            graphModel.addTableModelListener(stChangeListnr);

        }
        //-----------------create fixed shape node -------------------------

        animatedShapeTarget = new TransformGroup[location.size()];

        positionWindow.setFootpointsEnabled(false);

        os = new OrbitShape[location.size()];

        int k = 0;
        for (SatelliteData satData : location) {

            os[k] = new GeoOrbitShape(satData, sgp[k], graphModel, OrbitViewer.getInfoPane());
            k++;
        }
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            animatedShapeTarget[j] = new TransformGroup();
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            animatedShapeTarget[j].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            animatedShapeTarget[j].setCapability(Group.ALLOW_CHILDREN_WRITE);

            AnimatedShape animatedShape = new AnimatedShape(sgp[j].getShape().toString(), new Color3f(sgp[j].getColor()), sgp[j].getName());

            animatedShapeTarget[j].addChild(animatedShape);
        }

        anim = new SelenoAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));

        BranchGroup bb = new BranchGroup();
        bb.setCapability(BranchGroup.ALLOW_DETACH);
        bb.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bb.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        bb.addChild(anim);

        tg.addChild(bb);

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {
            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            BranchGroup bc = new BranchGroup();
            bc.setCapability(BranchGroup.ALLOW_DETACH);
            bc.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            bc.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            bc.addChild(anim.getTarget()[j]);

            anim.getAnimBranch().addChild(bc);

            BranchGroup sb = new BranchGroup();
            sb.setCapability(BranchGroup.ALLOW_DETACH);
            sb.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            sb.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

            sb.addChild(os[j]);

            shape.addChild(sb);

        }

        shape.addChild(anim.getAnimBranch());

        ContentBranch.getEarthGroup().setSatParams(this);

        positionWindow.setVisible(true);

    }

    public TransformGroup getEarthTransformGroup() {

        return earthTransformGroup;
    }

    /**
     * returns all of the data information concerning the selected satellites
     *
     * @return a list of satellite data information provided by the Satellite
     * Information Center
     */
    @Override
    public List<SatelliteData> getSatelliteData() {

        return location;
    }

    /**
     * Returns the coordinate system that is being used in this context. Could
     * be one of GEI_J2000, GEI_TOD, GEO, GM, GSE, GSM, SM, SSE).
     *
     * @return the coordinate system being used.
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {

        return coordinateSystem;
    }

    /**
     * Called when the selection of satellites being graphed has changed and the
     * scene-graph and all associated with it needs to be redrawn.
     */
    @Override
    public void clear() {

        if (stChangeListnr != null && OrbitViewer.getSatelliteChooser()
                != null) {

            OrbitViewer.getSatelliteChooser().getSatModel().removeTableModelListener(stChangeListnr);
        }

        for (int i = 0; i < animatedShapeTarget.length; i++) {

            animatedShapeTarget[i].removeAllChildren();
            anim.getTarget()[i].removeAllChildren();
            animatedShapeTarget[i] = null;
            anim.getTarget()[i] = null;
        }

        //  if (((SelenoAnimation) anim).getEarthTarget() != null) {
        //       ((SelenoAnimation) anim).getEarthTarget().removeAllChildren();
        //  }
        if (earthTransformGroup != null) {

            earthTransformGroup.removeAllChildren();
        }

        //       ((SelenoAnimation) anim).getEarthTarget().removeAllChildren();
        //  }
        anim.getAnimBranch().removeAllChildren();

        if (anim != null) {
            anim.clear();
        }

        if (os != null) {
            for (OrbitShape o : os) {
                o.clear();
            }
        }
        pickBranch.removeAllChildren();

        shape.removeAllChildren();

        if (axis != null) {
            axis.clear();
        }
        if (majtm != null) {
            majtm.clear();
        }

        for (Switch switchArray1 : switchArray) {
            switchArray1.removeAllChildren();
        }
        tg.removeAllChildren();
        removeAllChildren();

        switchArray = null;

        tg = null;
        shape = null;
        axis = null;
        os = null;
        anim = null;
        animatedShapeTarget = null;
        pickBranch = null;
        majtm = null;
        coordinateSystem = null;
        OrbitViewer.getSatellitePositionWindow().clear();
    }

    /**
     * Creates the check box switches and the elements associated with the
     * switches (earth, axis, ticks)
     *
     * @return a branch group heading the switched elements to facilitate
     * removing and adding this branch
     */
    @Override
    protected BranchGroup checkBoxSwitches() {

        BranchGroup bs = new BranchGroup();
        EarthGroup earthGroup = ContentBranch.getEarthGroup();

        EarthSurfaces earthSurfaces = ContentBranch.getEarthSurfaces();
        Moon moon = ContentBranch.getMoon();

        bs.setCapability(BranchGroup.ALLOW_DETACH);
        bs.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        this.setBoundingSpheres();
        pickBranch.setCapability(BranchGroup.ALLOW_DETACH);
        pickBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        for (int i = 0; i < switchArray.length; i++) {
            switchArray[i] = new Switch();
            switchArray[i].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            switchArray[i].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            switchArray[i].setCapability(Switch.ALLOW_CHILDREN_EXTEND);
            switchArray[i].setCapability(Switch.ALLOW_SWITCH_WRITE);

            if (i == SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.ORBIT)) {

                pickBranch.addChild(switchArray[i]);
            } else {

                bs.addChild(switchArray[i]);
            }
        }
        bs.addChild(pickBranch);

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.ORBIT)].addChild(shapeBranch);
        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MOON)].addChild(moon);

        earthGroup.setSatParams(this);

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.EARTH)].addChild(earthTransformGroup);

        SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.EARTH).setSelected(true);

        symbolSize = (float) getBounds(2).getRadius() / 100;

        OrbitViewer.getInfoPane().setAxisSpanValue(symbolSize * 40);

        OrbitViewer.getInfoPane().setTicksCount(2);
        axis = new Axis(OrbitViewer.getInfoPane());

        OrbitViewer.getInfoPane().setSpanBorder("Axis Span (RM)");
        scaleAxis((double) OrbitViewer.getInfoPane().getAxisSpanValue());
        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.AXIS)].addChild(axis);

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.BOWSHOCK)].addChild(earthSurfaces.getBowshock());

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MAGNETOPAUSE)].addChild(earthSurfaces.getMagnetopause());

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XYGRID)].addChild(new Grid(1d, 1d, 0.0));
        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.YZGRID)].addChild(new Grid(0.0, 1d, 1d));
        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XZGRID)].addChild(new Grid(1d, 0.0, 1d));

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.SUNLIGHT)].addChild(earthGroup.getSunlight());

        for (SelenoTogglePanel.Element el : SelenoTogglePanel.Element.values()) {
            setMask(SelenoTogglePanel.getPosition(el), SelenoTogglePanel.getCheckBox(el));
        }

        if (ContentBranch.getMoon() != null) {
            ContentBranch.getMoon().setSunLight(SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        if (earthSurfaces.getMagnetopause() != null) {
            earthSurfaces.getMagnetopause().setSunLight(SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        if (earthSurfaces.getBowshock() != null) {
            earthSurfaces.getBowshock().setSunLight(SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        transformGrid();

        bs.setUserData("switch");

        return bs;
    }

    /**
     * Sets the switch items (Bow shock and Magnetopause) to be displayed based
     * on the selection state of the corresponding check boxes.
     *
     */
    public void checkSurfaceArea() {

        JCheckBox bowShockBox = SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.BOWSHOCK);
        JCheckBox magBox = SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.MAGNETOPAUSE);

        if (!bowShockBox.isEnabled()) {
            bowShockBox.setEnabled(true);
        }

        if (!magBox.isEnabled()) {
            magBox.setEnabled(true);
        }

        setMask(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.BOWSHOCK),
                SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.BOWSHOCK));

        setMask(SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.MAGNETOPAUSE),
                SelenoTogglePanel.getCheckBox(SelenoTogglePanel.Element.MAGNETOPAUSE));

    }

    /**
     * Returns an array containing the animation behavior nodes.
     *
     * @return the animation behavior objects for each selected spacecraft
     */
    @Override
    public AnimBehavior[] getAnimationNodes() {
        return anim.getAnimationNodes();
    }

    /**
     *
     * @return the behavior node of the animation either manually or
     * automatically by using the play, pause and stop icons on the tool bar.
     */
    @Override
    public Animation getAnimation() {
        return anim;
    }

    /**
     * Respond to the user action of changing the width of the axis and the tick
     * marks.
     *
     * @param width the new width.
     */
    @Override
    public void axisWidthChanged(final float width) {

        super.axisWidthChanged(width);
        (ContentBranch.getEarthGroup().getGeogrid()).setWidth(width);

    }

    /**
     * Responds to the user action of changing the width of the symbols
     * (spacecraft shape, orbit, foot points...).
     *
     * @param width the new width
     */
    @Override
    public void symbolWidthChanged(final float width) {

        super.symbolWidthChanged(width);

    }

    /**
     * remove this class as an element of the graph change listener vector.
     */
    @Override
    public void removeSatelliteGraphChangeListener() {
        for (OrbitShape o : os) {
            o.removeSatelliteGraphChangeListener();
        }

    }

    /**
     * Sets the switch items (earth, axis, planes) to be displayed based on the
     * selection state of the corresponding check boxes.
     *
     * @param i the switch array indexes
     */
    @Override
    public void setMask(final int i, JCheckBox checkbox) {

        switchArray[i].setWhichChild(checkbox.isSelected()
                ? Switch.CHILD_ALL : Switch.CHILD_NONE);

    }

    public void setMask(final int i, final int j) {

        switchArray[i].setWhichChild(j);

    }

    public SolenocentricUtils getSse() {

        return sse;
    }

    /**
     * Called in response to the user changing the scale or changing the number
     * of ticks display by moving the Major Ticks slider.
     *
     * @param ticks number of ticks per axis per positive and negative values.
     */
    @Override
    public void addMajorTicks(final int ticks) {

        Enumeration e = switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.AXIS)].getAllChildren();

        while (e.hasMoreElements()) {

            Object child = e.nextElement();

            if (child instanceof MajorTickMark) {
                switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.AXIS)].removeChild((MajorTickMark) child);
            }

        }

        majtm = new MajorTickMark(ticks, OrbitViewer.getInfoPane(), (symbolSize * OrbitViewer.getInfoPane().getTickWidth() / axis.getTransform().getScale()));
        if (axis != null) {
            majtm.setTransform(axis.getTransform());
        }

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.AXIS)].addChild(majtm);
    }

    @Override
    public void addAxisText(final float scale) {

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.AXIS_TXT)].removeAllChildren();

        if (axis != null) {
            axisTxt = new AxisText(axis.getTransform().getScale() / scale, ((int) symbolSize * 4) + 2);

        }

        Transform3D transform = new Transform3D();
        transform.setScale(scale);

        if (axisTxt != null) {
            axisTxt.setTransform(transform);
        }

        switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.AXIS_TXT)].addChild(axisTxt);
    }

    @Override
    protected void transformGrid() {

        double r = getBounds(2).getRadius();

        Transform3D t = new Transform3D();

        if (switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XYGRID)].getAllChildren().hasMoreElements()) {

            ((Grid) switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XYGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XYGRID)].getChild(0)).setTransform(t);

            ((Grid) switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.YZGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.YZGRID)].getChild(0)).setTransform(t);

            ((Grid) switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XZGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[SelenoTogglePanel.getPosition(SelenoTogglePanel.Element.XZGRID)].getChild(0)).setTransform(t);
        }
    }

    /**
     * Called in response to the user acting on the Axis Span slider.
     *
     * @param virtualScale value in RE of the scale to be used.
     */
    @Override
    public void scaleAxis(final double virtualScale) {

        axis.setScale(ControlPanel.isSolenocentric() ? virtualScale * PhysicalConstants.MOON_TO_EARTH_RADIUS : virtualScale);
        addMajorTicks(OrbitViewer.getInfoPane().getTicksCount());
        addAxisText(OrbitViewer.getInfoPane().getCoordinateWidth());
    }

    /**
     * Return a specific check box switch node at position i.
     *
     * @param i the check box position
     * @return a check box switch node
     */
    @Override
    public Node getCheckBoxSwitchElement(final int i) {

        if (switchArray[i].getAllChildren().hasMoreElements()) {

            return switchArray[i].getChild(0);
        }

        return null;
    }

}
