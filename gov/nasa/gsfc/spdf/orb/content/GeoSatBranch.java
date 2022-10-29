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
 * $Id: GeoSatBranch.java,v 1.9 2018/06/06 14:47:29 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.behaviors.AnimBehavior;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import gov.nasa.gsfc.spdf.orb.content.behaviors.GeoAnimation;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SwitchGroup;
import gov.nasa.gsfc.spdf.orb.content.shapes.AnimatedShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.orb.content.shapes.AxisText;
import gov.nasa.gsfc.spdf.orb.content.shapes.GeoOrbitShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Grid;
import gov.nasa.gsfc.spdf.orb.content.shapes.GroundStations;
import gov.nasa.gsfc.spdf.orb.content.shapes.MajorTickMark;
import gov.nasa.gsfc.spdf.orb.content.shapes.OrbitShape;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.GeoSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.GeoTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionTableModel;
import gov.nasa.gsfc.spdf.orb.utils.EarthSurfaces;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateData;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
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
public class GeoSatBranch extends SatBranch {

    private List<SatelliteData> location = null;
    private CoordinateSystem coordinateSystem = null;
    private FootpointsGroup footpoints = null;

    /**
     * Creates the scene branch containing graphical objects.
     *
     * @param tempLocation an array of SatelliteLocation instances
     * @throws java.lang.CloneNotSupportedException
     */
    public GeoSatBranch(final List<SatelliteData> tempLocation) throws CloneNotSupportedException {

        super();

        OrbitViewer.setTogglePanel(OrbitViewer.GEO_TOGGLE_PANE);

        for (JCheckBox checkBoxArray : GeoTogglePanel.getCheckBoxArray()) {
            checkBoxArray.setEnabled(true);
        }

        switchArray = new Switch[GeoTogglePanel.getCheckBoxArray().length];
        ControlPanel.isImport = false;

        //--------------set Satellite Position Window-----------------
        GeoSatellitePositionWindow positionWindow = (GeoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow(ControlPanel.Body.EARTH.ordinal());
        SatelliteGraphTableModel graphModel = OrbitViewer.getSatelliteChooser().getSatModel();
        this.location = tempLocation;

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

        sgp = graphModel.getSelectedSatelliteGraphProperties(satNames);

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

            positionWindow.getCoordBorder().setTitle(
                    "coordinates (RE" + "/\u00B0):");

            positionWindow.getDistanceBorder().setTitle(
                    "distance to (RE" + "):");

            positionWindow.getTimeField().setValue(OrbitViewer.getControlPane().getStartDate());

            positionWindow.resetCaptureButton();

            graphModel.addTableModelListener(stChangeListnr);

        }
        //-----------------create fixed shape node -------------------------

        animatedShapeTarget = new TransformGroup[location.size()];

        //-------create the checkbox switches and the elements they control-------------
        addChild(tg);

        if (ControlPanel.getTracing()) {

            footpoints = new FootpointsGroup(location, sgp, graphModel, OrbitViewer.getInfoPane());
            positionWindow.setFootpointsEnabled(true);
        } else {
            positionWindow.setFootpointsEnabled(false);
        }

        os = new OrbitShape[location.size()];

        int k = 0;
        for (SatelliteData satData : location) {

            os[k] = new GeoOrbitShape(satData, sgp[k], graphModel, OrbitViewer.getInfoPane());
            k++;
        }

        tg.addChild(checkBoxSwitches());

        //need to deal differently with the planar views.  if visible set the checkbox otherwise
        //uncheck the box
        GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.PLANAR_VIEWS).
                setSelected(OrbitViewer.getPlanarPanel().isVisible());

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

        anim = new GeoAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
        tg.addChild(anim);

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {
            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            anim.getAnimBranch().addChild(anim.getTarget()[j]);

            shape.addChild(os[j]);

        }

        if (footpoints != null) {
            footpoints.symbolWidthChanged(symbolSize * OrbitViewer.getInfoPane().getSatWidth() * WIDTH_ADJUSTEMENT);
        }

        checkSurfaceArea();
        checkTracing(ControlPanel.getTracing());

        shape.addChild(anim.getAnimBranch());

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
     * @param footpoint if tracing is used in this imported file.
     */
    public GeoSatBranch(final List<SatelliteData> tempLocation,
            final SatelliteGraphProperties[] sgp,
            final boolean footpoint) {

        super();

        this.sgp = sgp;
        ControlPanel.isImport = true;
        ControlPanel.isSolenocentric = false;
       

        OrbitViewer.setTogglePanel(OrbitViewer.GEO_TOGGLE_PANE);

        for (JCheckBox checkBoxArray : GeoTogglePanel.getCheckBoxArray()) {
            checkBoxArray.setEnabled(true);
        }

        if (OrbitViewer.getSatelliteChooser() != null) {
            OrbitViewer.getSatelliteChooser().setVisible(false);
        }

        switchArray = new Switch[GeoTogglePanel.getCheckBoxArray().length];

        if (OrbitViewer.getControlPane() != null) {

            OrbitViewer.setPositionWindow(ControlPanel.Body.EARTH.ordinal());
        }
        GeoSatellitePositionWindow positionWindow = (GeoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow();

        this.location = tempLocation;

        //--------------set Satellite Position Window-----------------
        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][3];

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

            positionWindow.getCoordinateField().setText(
                    coordinateSystem.toString());

            boolean[] state = {coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSE),
                coordinateSystem.equals(CoordinateSystem.GSM)};
            positionWindow.setSurfacesEnabled(state);

        }

        //-----------------create fixed shape node -------------------------
        animatedShapeTarget = new TransformGroup[location.size()];

        //-------create the checkbox switches and the elements they control-------------
        addChild(tg);

        if (footpoint) {

            footpoints = new FootpointsGroup(location, sgp, null, OrbitViewer.getInfoPane());
            positionWindow.setFootpointsEnabled(true);
        } else {
            positionWindow.setFootpointsEnabled(false);
        }

        os = new OrbitShape[location.size()];

        for (int j = 0; j < os.length; j++) {

            os[j] = new GeoOrbitShape(location.get(j), sgp[j], null,
                    OrbitViewer.getInfoPane());
        }

        tg.addChild(checkBoxSwitches());

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

        anim = new GeoAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
        tg.addChild(anim);

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            anim.getAnimBranch().addChild(anim.getTarget()[j]);

            shape.addChild(os[j]);
        }
        if (footpoints != null) {
            footpoints.symbolWidthChanged(symbolSize * OrbitViewer.getInfoPane().getSatWidth() * WIDTH_ADJUSTEMENT);
        }
        checkSurfaceArea();
        checkTracing(footpoint);

        shape.addChild(anim.getAnimBranch());

        positionWindow.setVisible(true);
    }

    @Override
    public void addLocation(final List<? extends Object> tempLocation) {

        super.addLocation(tempLocation);

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.ORBIT)].addChild(shapeBranch);

        //--------------set Satellite Position Window-----------------
        GeoSatellitePositionWindow positionWindow = (GeoSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow(ControlPanel.Body.EARTH.ordinal());
        SatelliteGraphTableModel graphModel = OrbitViewer.getSatelliteChooser().getSatModel();
        this.location = (List<SatelliteData>) tempLocation;

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
            Logger.getLogger(GeoSatBranch.class.getName()).log(Level.SEVERE, null, ex);
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

        if (ControlPanel.getTracing()) {

            footpoints = new FootpointsGroup(location, sgp, graphModel, OrbitViewer.getInfoPane());
            positionWindow.setFootpointsEnabled(true);
        } else {
            positionWindow.setFootpointsEnabled(false);
        }

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

        anim = new GeoAnimation(location, this);

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

    ; 


    /**
     * Returns the main Foot points branch.
     *
     * @return the foot points node
     */
    public final FootpointsGroup getFootpoints() {

        return footpoints;
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

        if (footpoints != null) {
            footpoints.clear();
        }
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
        footpoints = null;
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

            if (i == GeoTogglePanel.getPosition(GeoTogglePanel.Element.ORBIT)
                    || i == GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_NORTH)
                    || i == GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_SOUTH)
                    || i == GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST)) {

                pickBranch.addChild(switchArray[i]);
            } else {

                bs.addChild(switchArray[i]);
            }
        }
        bs.addChild(pickBranch);

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.ORBIT)].addChild(shapeBranch);

        earthGroup.setSatParams(this);

        JCheckBox earthBox = GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.EARTH);

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.EARTH)].addChild(earthGroup);
        GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.GEO_GRID).setEnabled(true);
        GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.GROUND_STATIONS).setEnabled(true);

        // symbolSize = coordinateSystem == CoordinateSystem.GEO
        //         ? (float) new BoundingSphere(ContentBranch.getEarthGroup().getEarth().getBounds()).getRadius()/40
        //         : (float) getBounds(2).getRadius() / 100;
        symbolSize = (float) getBounds(2).getRadius() / 100;
        OrbitViewer.getInfoPane().setAxisSpanValue(symbolSize * 40);

        OrbitViewer.getInfoPane().setTicksCount(2);
        axis = new Axis(OrbitViewer.getInfoPane());

        OrbitViewer.getInfoPane().setSpanBorder("Axis Span (RE" + ")");
        scaleAxis((double) OrbitViewer.getInfoPane().getAxisSpanValue());
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.AXIS)].addChild(axis);

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.BOWSHOCK)].addChild(earthSurfaces.getBowshock());

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.MAGNETOPAUSE)].addChild(earthSurfaces.getMagnetopause());

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XYGRID)].addChild(new Grid(1d, 1d, 0.0));
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.YZGRID)].addChild(new Grid(0.0, 1d, 1d));
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XZGRID)].addChild(new Grid(1d, 0.0, 1d));

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET)].addChild(earthSurfaces.getNeutralSheet());
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.SUNLIGHT)].addChild(earthGroup.getSunlight());

        if (footpoints != null) {

            switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_NORTH)].addChild(footpoints.getNorthBound().getTransformGroup());
            switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_SOUTH)].addChild(footpoints.getSouthBound().getTransformGroup());
            switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST)].addChild(footpoints.getClosest().getTransformGroup());
        }
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.GEO_GRID)].addChild(earthGroup.getGeographTransform());
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.GROUND_STATIONS)].addChild(earthGroup.getStationsTransform());

        if (OrbitViewer.getGroundStationsWindow() != null) {

            GroundStations stationsShape = OrbitViewer.getGroundStationsWindow().getStations();
            for (int i = 0; i < OrbitViewer.getGroundStationsWindow().getTable().getRowCount(); i++) {

                SwitchGroup switchg = stationsShape.getSwitch(OrbitViewer.getGroundStationsWindow().getTable().getValueAt(i, 2));
                switchg.setChildMask(((Boolean) OrbitViewer.getGroundStationsWindow().getTable().getValueAt(i, 0))
                        ? switchg.getOptions()[0] : switchg.getOptions()[1]);
            }
        }

        for (GeoTogglePanel.Element el : GeoTogglePanel.Element.values()) {
            setMask(GeoTogglePanel.getPosition(el), GeoTogglePanel.getCheckBox(el));
        }

        if (!earthBox.isEnabled()) {
            earthBox.setEnabled(true);
        }

        if (earthGroup.getEarth() != null) {
            earthGroup.getEarth().setSunLight(GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        if (earthSurfaces.getNeutralSheet() != null) {
            earthSurfaces.getNeutralSheet().setSunLight(GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        if (earthSurfaces.getMagnetopause() != null) {
            earthSurfaces.getMagnetopause().setSunLight(GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        if (earthSurfaces.getBowshock() != null) {
            earthSurfaces.getBowshock().setSunLight(GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.SUNLIGHT).isSelected());
        }

        transformGrid();

        bs.setUserData("switch");
        return bs;

    }

    public BoundingSphere getGEOBounds() {

        BoundingSphere[] bounds = new BoundingSphere[2];

        bounds[0] = new BoundingSphere();

        if (os != null) {
            for (OrbitShape o : os) {
                bounds[0].combine(new BoundingSphere(o.getBounds()));
            }
        }
        bounds[1] = new BoundingSphere(ContentBranch.getEarthGroup().getEarth().getBounds());

        bounds[1].combine(bounds[0]);
        bounds[1].setRadius(bounds[1].getRadius());
        return bounds[1];
    }

    /**
     * Sets the switch items (Bow shock and Magnetopause) to be displayed based
     * on the selection state of the corresponding check boxes.
     *
     */
    public void checkSurfaceArea() {

        JCheckBox bowShockBox = GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.BOWSHOCK);
        JCheckBox magBox = GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.MAGNETOPAUSE);
        JCheckBox nsBox = GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.NEUTRAL_SHEET);

        if (coordinateSystem.equals(CoordinateSystem.GSE)) {
            if (!bowShockBox.isEnabled()) {
                bowShockBox.setEnabled(true);
            }

            if (!magBox.isEnabled()) {
                magBox.setEnabled(true);
            }

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.BOWSHOCK),
                    GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.BOWSHOCK));

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.MAGNETOPAUSE),
                    GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.MAGNETOPAUSE));

            nsBox.setEnabled(false);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET), Switch.CHILD_NONE);

        } else if (coordinateSystem.equals(CoordinateSystem.GSM)) {

            bowShockBox.setEnabled(false);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.BOWSHOCK), Switch.CHILD_NONE);

            magBox.setEnabled(false);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.MAGNETOPAUSE), Switch.CHILD_NONE);

            if (!nsBox.isEnabled()) {
                nsBox.setEnabled(true);
            }

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET),
                    GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.NEUTRAL_SHEET));

        } else {

            bowShockBox.setEnabled(false);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.BOWSHOCK), Switch.CHILD_NONE);

            magBox.setEnabled(false);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.MAGNETOPAUSE), Switch.CHILD_NONE);

            nsBox.setEnabled(false);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.NEUTRAL_SHEET), Switch.CHILD_NONE);
        }

    }



    /**
    * Adjust the check boxes to the foot point selection. If not enabled do not
     * enable any of the 3 check boxes if foot points enable allow north and
     * south or closest not all three.
     *
     * @param tracing selection or not of the foot point in the saved file.
     */
    public void checkTracing(final boolean tracing) {

        if (!tracing) {

            GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_NORTH).setEnabled(false);
            GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_SOUTH).setEnabled(false);
            GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST).setEnabled(false);
        } else {

            GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_NORTH).setEnabled(true);
            GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_SOUTH).setEnabled(true);
            GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST).setEnabled(true);

            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_NORTH),
                    GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_NORTH));
            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_SOUTH),
                    GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_SOUTH));
            setMask(GeoTogglePanel.getPosition(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST),
                    GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST));

            if (GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_NORTH).isSelected()
                    || GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_SOUTH).isSelected()) {
                GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST).setEnabled(false);
            } else if (GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_CLOSEST).isSelected()) {

                GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_NORTH).setEnabled(false);
                GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.FOOTPOINTS_SOUTH).setEnabled(false);
            }
        }
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

        if (footpoints != null) {
            footpoints.symbolWidthChanged(symbolSize * width * WIDTH_ADJUSTEMENT);
        }

    }

    /**
     * remove this class as an element of the graph change listener vector.
     */
    @Override
    public void removeSatelliteGraphChangeListener() {
        for (OrbitShape o : os) {
            o.removeSatelliteGraphChangeListener();
        }
        if (footpoints != null) {
            footpoints.removeSatelliteGraphChangeListener();
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

    /**
     * Called in response to the user changing the scale or changing the number
     * of ticks display by moving the Major Ticks slider.
     *
     * @param ticks number of ticks per axis per positive and negative values.
     */
    @Override
    public void addMajorTicks(final int ticks) {

        Enumeration e = switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.AXIS)].getAllChildren();

        while (e.hasMoreElements()) {

            Object child = e.nextElement();

            if (child instanceof MajorTickMark) {
                switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.AXIS)].removeChild((MajorTickMark) child);
            }

        }
        //  switchArray[SwitchElement.MAJOR_TICKS.ordinal()].removeAllChildren();
        majtm = new MajorTickMark(ticks, OrbitViewer.getInfoPane(), (symbolSize * OrbitViewer.getInfoPane().getTickWidth() / axis.getTransform().getScale()));
        if (axis != null) {
            majtm.setTransform(axis.getTransform());
        }

        // switchArray[SwitchElement.MAJOR_TICKS.ordinal()].addChild(majtm);
        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.AXIS)].addChild(majtm);
    }

    @Override
    public void addAxisText(final float scale) {

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.AXIS_TXT)].removeAllChildren();

        if (axis != null) {
            axisTxt = new AxisText(axis.getTransform().getScale() / scale, ((int) symbolSize * 4) + 2);

        }

        Transform3D transform = new Transform3D();
        transform.setScale(scale);

        if (axisTxt != null) {
            axisTxt.setTransform(transform);
        }

        switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.AXIS_TXT)].addChild(axisTxt);
    }

    @Override
    protected void transformGrid() {

        double r = getBounds(2).getRadius();

        Transform3D t = new Transform3D();

        if (switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XYGRID)].getAllChildren().hasMoreElements()) {

            ((Grid) switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XYGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XYGRID)].getChild(0)).setTransform(t);

            ((Grid) switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.YZGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.YZGRID)].getChild(0)).setTransform(t);

            ((Grid) switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XZGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[GeoTogglePanel.getPosition(GeoTogglePanel.Element.XZGRID)].getChild(0)).setTransform(t);
        }
    }

    /**
     * Called in response to the user acting on the Axis Span slider.
     *
     * @param virtualScale value in RE of the scale to be used.
     */
    @Override
    public void scaleAxis(final double virtualScale) {

        axis.setScale(virtualScale);
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
