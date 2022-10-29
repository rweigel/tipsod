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
 * $Id: HelioSatBranch.java,v 1.6 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content;

import gov.nasa.gsfc.spdf.helio.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.behaviors.AnimBehavior;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import gov.nasa.gsfc.spdf.orb.content.behaviors.HelioAnimation;
import gov.nasa.gsfc.spdf.orb.content.shapes.AnimatedShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.orb.content.shapes.AxisText;
import gov.nasa.gsfc.spdf.orb.content.shapes.Grid;
import gov.nasa.gsfc.spdf.orb.content.shapes.HelioOrbitShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.MajorTickMark;
import gov.nasa.gsfc.spdf.orb.content.shapes.OrbitShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Sun;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.HelioSatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.HelioTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionTableModel;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
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
public class HelioSatBranch extends SatBranch {

    private List<Trajectory> location = null;
    private gov.nasa.gsfc.spdf.helio.client.CoordinateSystem coordinateSystem = null;

    /**
     * Creates the scene branch containing graphical objects.
     *
     * @param tempLocation an array of SatelliteLocation instances
     * @throws java.lang.CloneNotSupportedException
     */
    public HelioSatBranch(final List<Trajectory> tempLocation) throws CloneNotSupportedException {

        super();

        OrbitViewer.setTogglePanel(OrbitViewer.HELIO_TOGGLE_PANE);

        switchArray = new Switch[HelioTogglePanel.getCheckBoxArray().length];
        ControlPanel.isImport = false;

        SatelliteGraphTableModel graphModel = OrbitViewer.getSatelliteChooser().getSatModel();

        ListIterator<Trajectory> listIterator = tempLocation.listIterator();

        while (listIterator.hasNext()) {
            if (listIterator.next().getTime().isEmpty()) {
                listIterator.remove();

            }
        }

        this.location = tempLocation;

        for (Trajectory temp : location) {

            graphModel.verifyTimes(temp);

        }

        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][3];

        if (location.size() > 0) {

            coordinateSystem = location.get(0).getCoordinateSystem();
        }
        //--------------set Satellite Position Window-----------------
        HelioSatellitePositionWindow positionWindow = (HelioSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow(ControlPanel.Body.SUN.ordinal());

        int i = 0;
        for (Trajectory satData : location) {

            if (satData != null) {

                satNames[i] = satData.getId();

                if (satData.getRadius() != null
                        && satData.getLatitude() != null
                        && satData.getLongitude() != null) {

                    coordinates[i] = Footpoint.sphericalToCartesian(new double[]{satData.getRadius().get(0),
                        satData.getLatitude().get(0),
                        satData.getLongitude().get(0)});
                }

            }
            i++;
        }

        sgp = graphModel.getSelectedSatelliteGraphProperties(satNames);

        if (satNames.length > 0 && satNames[0] != null) {

            model = new SatellitePositionTableModel(
                    satNames, sgp, coordinates, distances);

            positionWindow.getTable().setModel(model);
            positionWindow.clearCheckBoxes();
            positionWindow.getTable().calcColumnWidths();

            positionWindow.pack();

            positionWindow.getCoordinateField().setText(OrbitViewer.getControlPane().getCoordinateSystem().toString());

            positionWindow.getCoordBorder().setTitle(
                    "coordinates (" + ("au") + "/\u00B0):");

            positionWindow.getTimeField().setValue(OrbitViewer.getControlPane().getStartDate());

            positionWindow.resetCaptureButton();

            positionWindow.setDataCaptureTrajectory(tempLocation);

            graphModel.addTableModelListener(stChangeListnr);

        }
        //-----------------create fixed shape node -------------------------

        animatedShapeTarget = new TransformGroup[location.size()];

        shape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

        //-------create the checkbox switches and the elements they control-------------
        addChild(tg);

        os = new OrbitShape[location.size()];

        int k = 0;
        for (Trajectory satData : location) {

            os[k] = new HelioOrbitShape(satData, sgp[k], graphModel, OrbitViewer.getInfoPane());
            k++;
        }

        tg.addChild(checkBoxSwitches());

        //need to deal differently with the planar views.  if visible set the checkbox otherwise
        //uncheck the box
        HelioTogglePanel.getCheckBox(HelioTogglePanel.Element.PLANAR_VIEWS).
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

        anim = new HelioAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
        tg.addChild(anim);

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {
            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            anim.getAnimBranch().addChild(anim.getTarget()[j]);

            shape.addChild(os[j]);
        }

        shape.addChild(anim.getAnimBranch());

        positionWindow.setVisible(true);

        OrbitViewer.getTipsodMenuBar().getExportMenu().setEnabled(true);
        OrbitViewer.getTipsodMenuBar().getImportMenu().setEnabled(true);

        OrbitViewer.getTipsodMenuBar().getExportMenu().setEnabled(true);
        OrbitViewer.getTipsodMenuBar().getImportMenu().setEnabled(true);

    }

    public HelioSatBranch(final List<Trajectory> tempLocation,
            final SatelliteGraphProperties[] sgp) {

        super();

        this.sgp = sgp;

        ControlPanel.isImport = true;
        ControlPanel.isSolenocentric = false;

        OrbitViewer.setTogglePanel(OrbitViewer.HELIO_TOGGLE_PANE);
        if (OrbitViewer.getSatelliteChooser() != null) {
            OrbitViewer.getSatelliteChooser().setVisible(false);
        }

        switchArray = new Switch[HelioTogglePanel.getCheckBoxArray().length];

        if (OrbitViewer.getControlPane() != null) {

            OrbitViewer.setPositionWindow(ControlPanel.Body.SUN.ordinal());
        }

        HelioSatellitePositionWindow positionWindow = (HelioSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow();

        this.location = tempLocation;

        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][3];

        coordinateSystem = location.get(0).getCoordinateSystem();
        //--------------set Satellite Position Window-----------------

        int i = 0;
        for (Trajectory satData : location) {

            if (satData != null) {

                satNames[i] = satData.getId();

                if (satData.getRadius() != null
                        && satData.getLatitude() != null
                        && satData.getLongitude() != null) {

                    coordinates[i] = Footpoint.sphericalToCartesian(new double[]{satData.getRadius().get(0),
                        satData.getLatitude().get(0),
                        satData.getLongitude().get(0)});
                }

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

            positionWindow.getCoordinateField().setText(coordinateSystem.toString());

            positionWindow.getCoordBorder().setTitle(
                    "coordinates (" + ("au") + "/\u00B0):");

            positionWindow.setDataCaptureTrajectory(tempLocation);

        }
        //-----------------create fixed shape node -------------------------

        animatedShapeTarget = new TransformGroup[location.size()];

        shape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

        //-------create the checkbox switches and the elements they control-------------
        addChild(tg);

        os = new OrbitShape[location.size()];

        int k = 0;
        for (Trajectory satData : location) {

            os[k] = new HelioOrbitShape(satData, sgp[k], null, OrbitViewer.getInfoPane());
            k++;
        }

        tg.addChild(checkBoxSwitches());
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            animatedShapeTarget[j] = new TransformGroup();
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            animatedShapeTarget[j].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
            animatedShapeTarget[j].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            animatedShapeTarget[j].setCapability(Group.ALLOW_CHILDREN_WRITE);

            // float radius = symbolSize * OrbitViewer.getInfoPane().getSatWidth();
            // AnimatedShape animatedShape = new AnimatedShape(sgp[j].getShape().toString(), radius, new Color3f(sgp[j].getColor()), sgp[j].getName());
            AnimatedShape animatedShape = new AnimatedShape(sgp[j].getShape().toString(), new Color3f(sgp[j].getColor()), sgp[j].getName());
            animatedShapeTarget[j].addChild(animatedShape);
        }

        anim = new HelioAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
        tg.addChild(anim);

        //------------------create animated shape node ---------------------
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            anim.getTarget()[j].addChild(animatedShapeTarget[j]);

            os[j].setAnimatedShape((AnimatedShape) animatedShapeTarget[j].getChild(0), j);

            anim.getAnimBranch().addChild(anim.getTarget()[j]);

            shape.addChild(os[j]);
        }
        shape.addChild(anim.getAnimBranch());

        positionWindow.setVisible(true);

    }

    @Override
    public void addLocation(final List<? extends Object> tempLocation) {

        super.addLocation(tempLocation);

        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.ORBIT)].addChild(shapeBranch);

        //--------------set Satellite Position Window-----------------
        HelioSatellitePositionWindow positionWindow = (HelioSatellitePositionWindow) OrbitViewer.getSatellitePositionWindow(ControlPanel.Body.SUN.ordinal());
        SatelliteGraphTableModel graphModel = OrbitViewer.getSatelliteChooser().getSatModel();

        ListIterator<Trajectory> listIterator = ((List<Trajectory>) tempLocation).listIterator();

        while (listIterator.hasNext()) {
            if (listIterator.next().getTime().isEmpty()) {
                listIterator.remove();

            }
        }

        this.location = (List<Trajectory>) tempLocation;

        for (Trajectory temp : location) {

            graphModel.verifyTimes(temp);

        }

        coordinateSystem = location.get(0).getCoordinateSystem();

        String[] satNames = new String[location.size()];
        double[][] coordinates = new double[location.size()][CARTESIAN_COORDINATES];
        double[][] distances = new double[location.size()][3];

        int i = 0;
        for (Trajectory satData : location) {

            if (satData != null) {

                satNames[i] = satData.getId();

                if (satData.getRadius() != null
                        && satData.getLatitude() != null
                        && satData.getLongitude() != null) {

                    coordinates[i] = Footpoint.sphericalToCartesian(new double[]{satData.getRadius().get(0),
                        satData.getLatitude().get(0),
                        satData.getLongitude().get(0)});
                }

            }
            i++;
        }

        try {
            sgp = graphModel.getSelectedSatelliteGraphProperties(satNames);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(HelioSatBranch.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (satNames[0] != null) {

            model = new SatellitePositionTableModel(
                    satNames, sgp, coordinates, distances);

            positionWindow.getTable().setModel(model);
            positionWindow.clearCheckBoxes();
            positionWindow.getTable().calcColumnWidths();

            positionWindow.pack();

            positionWindow.getCoordinateField().setText(OrbitViewer.getControlPane().getCoordinateSystem().toString());

            positionWindow.getTimeField().setValue(OrbitViewer.getControlPane().getStartDate());

            positionWindow.resetCaptureButton();

            graphModel.addTableModelListener(stChangeListnr);

        }
        //-----------------create fixed shape node -------------------------

        animatedShapeTarget = new TransformGroup[location.size()];

        os = new OrbitShape[location.size()];

        int k = 0;
        for (Trajectory satData : location) {

            os[k] = new HelioOrbitShape(satData, sgp[k], graphModel, OrbitViewer.getInfoPane());
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

        anim = new HelioAnimation(location, this);

        anim.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
        BranchGroup bb = new BranchGroup();
        bb.setCapability(BranchGroup.ALLOW_DETACH);
        bb.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bb.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        bb.addChild(anim);

        tg.addChild(bb);
        for (int j = 0; j < animatedShapeTarget.length; j++) {

            //------------------create animated shape node ---------------------
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

        positionWindow.setVisible(true);

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

    @Override
    public CoordinateSystem getCoordinateSystem() {

        return coordinateSystem;
    }

    /*  @Override
     protected BoundingSphere[] getBoundingSpheres() {

     BoundingSphere[] bounds = new BoundingSphere[1];

     bounds[0] = new BoundingSphere();

     if (os != null) {
     for (OrbitShape o : os) {
     bounds[0].combine(new BoundingSphere(o.getBounds()));
     }
     }

     bounds[0].setRadius(3d);

     return bounds;
     }*/
    /**
     * returns all of the data information concerning the selected satellites
     *
     * @return a list of satellite data information provided by the Satellite
     * Information Center
     */
    @Override
    public List<Trajectory> getSatelliteData() {

        return location;
    }

    ; 
    
     @Override
    protected BranchGroup checkBoxSwitches() {

        BranchGroup bs = new BranchGroup();

        Sun sun = ContentBranch.getSun();

        bs.setCapability(BranchGroup.ALLOW_DETACH);
        bs.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        pickBranch.setCapability(BranchGroup.ALLOW_DETACH);
        pickBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        this.setBoundingSpheres();

        for (int i = 0; i < switchArray.length; i++) {
            switchArray[i] = new Switch();
            switchArray[i].setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            switchArray[i].setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            switchArray[i].setCapability(Switch.ALLOW_CHILDREN_EXTEND);
            switchArray[i].setCapability(Switch.ALLOW_SWITCH_WRITE);

            if (i == HelioTogglePanel.getPosition(HelioTogglePanel.Element.ORBIT)) {

                pickBranch.addChild(switchArray[i]);
            } else {

                bs.addChild(switchArray[i]);
            }
        }
        bs.addChild(pickBranch);

        symbolSize = (float) getBounds(1).getRadius() / 80;

        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.ORBIT)].addChild(shapeBranch);

        OrbitViewer.getInfoPane().setAxisSpanValue(symbolSize * 40);

        OrbitViewer.getInfoPane().setTicksCount(2);
        axis = new Axis(OrbitViewer.getInfoPane());

        OrbitViewer.getInfoPane().setSpanBorder("Axis Span (au" + ")");
        scaleAxis((double) OrbitViewer.getInfoPane().getAxisSpanValue());
        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.AXIS)].addChild(axis);

        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.SUN)].addChild(sun);

        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XYGRID)].addChild(new Grid(1d, 1d, 0.0));
        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.YZGRID)].addChild(new Grid(0.0, 1d, 1d));
        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XZGRID)].addChild(new Grid(1d, 0.0, 1d));

        for (HelioTogglePanel.Element el : HelioTogglePanel.Element.values()) {
            setMask(HelioTogglePanel.getPosition(el), HelioTogglePanel.getCheckBox(el));
        }
        transformGrid();

        bs.setUserData("switch");

        return bs;
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
     * Sets the switch items (earth, axis, planes) to be displayed based on the
     * selection state of the corresponding check boxes.
     *
     * @param i the switch array indexes
     * @param checkbox
     */
    @Override
    public void setMask(final int i, JCheckBox checkbox) {

        switchArray[i].setWhichChild(checkbox.isSelected()
                ? Switch.CHILD_ALL : Switch.CHILD_NONE);

    }

    /**
     * Called in response to the user changing the scale or changing the number
     * of ticks display by moving the Major Ticks slider.
     *
     * @param ticks number of ticks per axis per positive and negative values.
     */
    @Override
    public void addMajorTicks(final int ticks) {

        Enumeration e = switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.AXIS)].getAllChildren();

        while (e.hasMoreElements()) {

            Object child = e.nextElement();

            if (child instanceof MajorTickMark) {
                switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.AXIS)].removeChild((MajorTickMark) child);
            }

        }
        //  switchArray[SwitchElement.MAJOR_TICKS.ordinal()].removeAllChildren();
        majtm = new MajorTickMark(ticks, OrbitViewer.getInfoPane(), (symbolSize * OrbitViewer.getInfoPane().getTickWidth() / axis.getTransform().getScale()));
        if (axis != null) {
            majtm.setTransform(axis.getTransform());
        }

        // switchArray[SwitchElement.MAJOR_TICKS.ordinal()].addChild(majtm);
        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.AXIS)].addChild(majtm);
    }

    /**
     * Called when the scale or number of ticks have changed to redraw the
     * labels (X,Y,Z) with the appropriate size and direction.
     *
     * @param scale how large should the labels be.
     */
    @Override
    public void addAxisText(final float scale) {

        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.AXIS_TXT)].removeAllChildren();

        if (axis != null) {
            axisTxt = new AxisText(axis.getTransform().getScale() / scale, ((int) symbolSize * 4) + 2);

        }

        Transform3D transform = new Transform3D();
        transform.setScale(scale);

        if (axisTxt != null) {
            axisTxt.setTransform(transform);
        }

        switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.AXIS_TXT)].addChild(axisTxt);
    }

    @Override
    protected void transformGrid() {

        double r = getBounds(2).getRadius();

        Transform3D t = new Transform3D();

        if (switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XYGRID)].getAllChildren().hasMoreElements()) {

            ((Grid) switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XYGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XYGRID)].getChild(0)).setTransform(t);

            ((Grid) switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.YZGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.YZGRID)].getChild(0)).setTransform(t);

            ((Grid) switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XZGRID)].getChild(0)).makeGridLines(r, 1);
            t.setScale(new Vector3d(2 * r, 2 * r, 2 * r));
            ((Grid) switchArray[HelioTogglePanel.getPosition(HelioTogglePanel.Element.XZGRID)].getChild(0)).setTransform(t);
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
