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
 * $Id: SatBranch.java,v 1.82 2015/10/30 14:18:50 rchimiak Exp $
 * Created on April 24, 2002, 1:37 PM
 */

package gov.nasa.gsfc.spdf.orb.content;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.behaviors.AnimBehavior;
import gov.nasa.gsfc.spdf.orb.content.behaviors.Animation;
import gov.nasa.gsfc.spdf.orb.content.behaviors.MouseHandler;
import gov.nasa.gsfc.spdf.orb.content.shapes.AnimatedShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.orb.content.shapes.AxisText;
import gov.nasa.gsfc.spdf.orb.content.shapes.MajorTickMark;
import gov.nasa.gsfc.spdf.orb.content.shapes.OrbitShape;
import gov.nasa.gsfc.spdf.orb.gui.GeoTogglePanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatellitePositionWindow;
import gov.nasa.gsfc.spdf.orb.gui.ZoomControl;
import java.util.Enumeration;
import java.util.List;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JCheckBox;

/**
 * The SatBranch class implements the scene to be displayed.
 *
 * @author rchimiak
 * @version $Revision: 1.82 $
 */
public abstract class SatBranch extends BranchGroup {

    SatelliteTableChangeListener stChangeListnr
            = new SatelliteTableChangeListener();
    TransformGroup tg = new TransformGroup();
    TransformGroup shape = new TransformGroup();
    BranchGroup shapeBranch = new BranchGroup();
    OrbitShape[] os = null;
    Axis axis = null;
    protected MajorTickMark majtm = null;
    protected AxisText axisTxt = null;
    TransformGroup[] animatedShapeTarget;
    protected ZoomControl zoomControl = null;
    BranchGroup pickBranch = new BranchGroup();
    static float symbolSize;
    protected Switch[] switchArray;

    SatellitePositionTableModel model = null;
    Animation anim;

    SatelliteGraphProperties[] sgp = null;
    static final int CARTESIAN_COORDINATES = 3;
    protected BoundingSphere[] boundingArray;

    protected static final float WIDTH_ADJUSTEMENT = 0.65f;

    public SatBranch() {

        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        //------------- create first transform group ----------------
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        tg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        tg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        tg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        shape.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        shape.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

        shapeBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        shapeBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        shapeBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        shapeBranch.setCapability(BranchGroup.ALLOW_DETACH);

        shapeBranch.addChild(shape);
        if (OrbitViewer.isConnected()) {

            OrbitViewer.getControlPane().setCameraSetUpState(true);
        }

    }

    public void addLocation(final List<? extends Object> tempLocation) {

        anim.clear();
        shapeBranch.detach();

        shape.removeAllChildren();

    }

    /**
     * Returns the displays information of the selected satellites
     *
     * @return display information
     */
    public final SatelliteGraphProperties[] getProperties() {

        return sgp;
    }

    /**
     * Returns the Branch that allows for interaction between the user's mouse
     * and the scene-graph.
     *
     * @return the pick branch.
     */
    public final BranchGroup getPickBranch() {

        return pickBranch;
    }

    public void setVisible(final int i, boolean visible) {

        ((AnimatedShape) animatedShapeTarget[i].getChild(0)).getAppearance().getRenderingAttributes().setVisible(visible);

    }

    public boolean getVisible(final int i) {

        return ((AnimatedShape) animatedShapeTarget[i].getChild(0)).getAppearance().getRenderingAttributes().getVisible();

    }

    /**
     * Returns the coordinate system that is being used in this context. Could
     * be one of GEI_J2000, GEI_TOD, GEO, GM, GSE, GSM, SM, SSE).
     *
     * @return the coordinate system being used.
     */
    public abstract Object getCoordinateSystem();

    /**
     * Returns a float from which all of the scene-graph sizes are going to
     * derived. Can be changed by the user acting on the Symbol spinner.
     *
     * @return a size defined as a float.
     */
    public static float getSymbolSize() {
        return symbolSize;
    }

    /**
     * The zoom control is a frame containing the zoom scene-graph. This is
     * being called in response to the user asking for a zoom window to be
     * displayed.
     *
     * @param zoom the zoom window to be displayed
     * @param selection which satellite is being selected for zooming. Only one
     * satellite can be zoomed at a time.
     */
    public void addZoom(final ZoomControl zoom, final int selection) {

        zoomControl = zoom;
        anim.getAnimationNodes()[zoom.getSelection()].getPositionPath().setZoom(zoom);

        BranchGroup b = new BranchGroup();
        b.setUserData("zoomAxis");

        b.setCapability(BranchGroup.ALLOW_DETACH);
        b.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        b.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        b.addChild(zoom.getAxisTransformGroup());
        anim.getTarget()[selection].addChild(b);
    }

    /**
     * Called when the selection of satellites being graphed has changed and the
     * scene-graph and all associated with it needs to be redrawn.
     */
    public abstract void clear();

    /**
     * Called when the zoom window is closed. Takes away any nodes related to
     * the zoom function and adjust the menu items.
     */
    public void removeZoom() {

        if (!zoomExist()) {
            return;
        }

        int selection = zoomControl.getSelection();
        OrbitViewer.getTipsodMenuBar().setZoomEnabled(true);

        if (zoomControl.isLocked()) {
            OrbitViewer.getViewBranchArray()[0].getOrbitBehavior().removeCallback(zoomControl.getViewBranch());
        }

        anim.getAnimationNodes()[selection].getPositionPath().setZoom(null);
        Enumeration e = anim.getTarget()[selection].getAllChildren();

        while (e.hasMoreElements()) {

            Node sgObject = (Node) e.nextElement();
            Object userData = sgObject.getUserData();
            if (userData instanceof String
                    && ((String) userData).compareTo("zoomAxis") == 0) {
                anim.getTarget()[selection].removeChild(sgObject);
            }
        }
        zoomControl.getViewBranch().getView().removeAllCanvas3Ds();
        zoomControl.getViewBranch().getView().attachViewPlatform(null);
        zoomControl.dispose();
        zoomControl = null;
    }

    /**
     * Returns the status on the zoom window. Is there one already existing?
     * Only one zoom window can be open.
     *
     * @return zoom open or not.
     */
    public boolean zoomExist() {
        return zoomControl != null;
    }

    /**
     * Returns an instance of the zoom window or null if the zoom window is not
     * displayed.
     *
     * @return the zoom window instance.
     */
    public ZoomControl getZoom() {
        return zoomControl;
    }

    /**
     * Creates the check box switches and the elements associated with the
     * switches (earth, axis, ticks)
     *
     * @return a branch group heading the switched elements to facilitate
     * removing and adding this branch
     */
    protected abstract BranchGroup checkBoxSwitches();

    /**
     * returns all of the data information concerning the selected satellites
     *
     * @return a list of satellite data information provided by the Satellite
     * Information Center
     */
    public abstract List<? extends Object> getSatelliteData();

    public BoundingSphere getBounds(final int i) {

        BoundingSphere bounds = new BoundingSphere();

        for (int j = 0; j < i; j++) {

            bounds.combine(boundingArray[j]);
        }
        return bounds;
    }

    protected void setBoundingSpheres() {

        boundingArray = new BoundingSphere[3];

        boundingArray[0] = new BoundingSphere();

        if (os != null) {
            for (OrbitShape o : os) {
                boundingArray[0].combine(new BoundingSphere(o.getBounds()));
            }
        }

        if (ContentBranch.getEarthGroup() != null && ContentBranch.getEarthGroup().getEarth() != null) {

            boundingArray[1] = new BoundingSphere(ContentBranch.getEarthGroup().getEarth().getBounds());

            if (this instanceof HelioSatBranch != true) {
                boundingArray[1].setRadius(3);
            }
        }

        if (ContentBranch.getEarthSurfaces() != null && ContentBranch.getEarthSurfaces().getBowshock() != null
                && (GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.BOWSHOCK).isSelected()
                || GeoTogglePanel.getCheckBox(GeoTogglePanel.Element.MAGNETOPAUSE).isSelected())) {
            boundingArray[2] = new BoundingSphere(ContentBranch.getEarthSurfaces().getBowshock().getBounds());
        }

    }

    /**
     * Sets the switch items (earth, axis, planes) to be displayed based on the
     * selection state of the corresponding check boxes.
     *
     * @param i the switch array indexes
     * @param checkbox
     */
    public abstract void setMask(final int i, JCheckBox checkbox);

    /**
     * Returns an array containing the animation behavior nodes.
     *
     * @return the animation behavior objects for each selected spacecraft
     */
    public AnimBehavior[] getAnimationNodes() {
        return anim.getAnimationNodes();
    }

    /**
     *
     * @return the behavior node of the animation either manually or
     * automatically by using the play, pause and stop icons on the tool bar.
     */
    public Animation getAnimation() {
        return anim;
    }

    /**
     * Called when the user disable the coordinate labels under the Tool menu
     */
    public void removeCoord() {

        for (int i = 1; i < numChildren();) {
            removeChild(i);
        }
    }

    /**
     * Responds to the user action of changing the scale thru the given scale
     * widget
     *
     * @param width new width
     */
    public void lineWidthChanged(final float width) {
        for (OrbitShape o : os) {
            o.setAppearance(width);
        }

    }

    /**
     * Respond to the user action of changing the width of the axis and the tick
     * marks.
     *
     * @param width the new width.
     */
    public void axisWidthChanged(final float width) {

        axis.setAppearance(width);
        majtm.setWidth(width);

        if (zoomControl != null) {

            zoomControl.getAxis().setAppearance(width);
            zoomControl.getMajorTicks().setWidth(width);
        }
    }

    /**
     * Responds to the user action of changing the width of the symbols
     * (spacecraft shape, orbit, foot points...).
     *
     * @param width the new width
     */
    public void symbolWidthChanged(final float width) {
        for (TransformGroup animatedShapeTarget1 : animatedShapeTarget) {
            AnimatedShape animShape = (AnimatedShape) animatedShapeTarget1.getChild(0);
            animShape.setGeometry(animShape.getShape());
        }

    }

    /**
     * Responds to the user action of changing the width of the tick marks
     *
     * @param width the new width
     */
    public void tickMarkWidthChanged(final float width) {

        if (majtm != null) {
            majtm.changeLength(OrbitViewer.getInfoPane().getTicksCount(), (symbolSize * width / axis.getTransform().getScale()));
        }

        if (zoomControl != null) {

            zoomControl.getMajorTicks().changeLength(zoomControl.getTickCount(), (symbolSize * width / zoomControl.getAxisTransform().getScale()));
        }

    }

    /**
     * Responds to the user action of changing the width of the tick marks
     *
     * @param width the new width
     */
    public void coordinateWidthChanged(final float width) {

        Transform3D transform = new Transform3D();
        transform.setScale(width);

        MouseHandler.setTransform(transform);
        addAxisText(width);
    }

    public abstract void addMajorTicks(final int ticks);

    public abstract void addAxisText(final float scale);

    protected abstract void transformGrid();

    public abstract void scaleAxis(final double virtualScale);

    public abstract Node getCheckBoxSwitchElement(final int i);

    /**
     * remove this class as an element of the graph change listener vector.
     */
    public void removeSatelliteGraphChangeListener() {
        for (OrbitShape o : os) {
            o.removeSatelliteGraphChangeListener();
        }

    }

    private class SatelliteTableChangeListener implements javax.swing.event.TableModelListener {

        @Override
        public void tableChanged(final javax.swing.event.TableModelEvent e) {

            int row = e.getFirstRow();
            int column = e.getColumn();
            SatelliteGraphTableModel tableModel = (SatelliteGraphTableModel) e.getSource();

            Object data = tableModel.getValueAt(row, column);
            String displayName = (String) tableModel.getValueAt(row, 1);

            switch (column) {

                case 2:
                    model.setValueAt(data, model.getRow(displayName), SatellitePositionWindow.Column.COLOR);
                    break;

                default:

                    break;
            }
        }
    }

}
