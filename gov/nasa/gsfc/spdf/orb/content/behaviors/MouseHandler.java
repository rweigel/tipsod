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
 * $Id: MouseHandler.java,v 1.35 2015/10/30 14:18:50 rchimiak Exp $
 * Created on June 11, 2002, 2:45 PM
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import javax.media.j3d.BranchGroup;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.ContentBranch;
import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import gov.nasa.gsfc.spdf.orb.content.shapes.AnimatedShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.FootpointShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.PositionShape;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

/**
 * This class implements the response to Mouse clicks on the orbit plots by
 * displaying coordinates and associated time at the location of the click
 *
 * @author rchimiak
 * @version $Revision: 1.35 $
 */
public class MouseHandler implements MouseListener, MouseMotionListener {

    private PickCanvas pickCanvas = null;
    private int fontSize = 0;
    private static boolean picking = false;
    private static final ArrayList<CoordBranch> pickList = new ArrayList<CoordBranch>();

    /**
     * Creates a new MouseHandler to respond to the user selecting points on the
     * orbit plot and displays coordinates or time or both
     */
    public MouseHandler() {

        pickCanvas = new PickCanvas(OrbitViewer.getViewBranchArray()[0].getCanvas(), ContentBranch.getSatBranch().getPickBranch());

        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        if (ContentBranch.getSatBranch() != null) {

            fontSize = (int) (ContentBranch.getSatBranch().getBounds(2).getRadius() / 20);

        }
        if (fontSize == 0) {
            fontSize++;
        }
    }

    /**
     * Called from the menu "Enable Coordinate Labels".
     *
     * @param pickingEnabled true to enable, false to disable
     */
    public void enablePicking(final boolean pickingEnabled) {

        picking = pickingEnabled;
    }

    /**
     * invoked when a mouse button has been pressed on a component. resort to
     * default behavior.
     *
     * @param mouseEvent event which indicates that the mouse was pressed.
     */
    @Override
    public void mousePressed(final java.awt.event.MouseEvent mouseEvent) {
    }

    /**
     * invoked when a mouse cursor entered an area around the orbit shape.
     * resort to default behavior
     *
     * @param mouseEvent event which indicates that the mouse has entered a
     * possible picking area.
     */
    @Override
    public void mouseEntered(final java.awt.event.MouseEvent mouseEvent) {
    }

    /**
     * invoked when the mouse button has been moved on the orbit shape (with no
     * buttons down). When this occurs, the cursor changes from default to a
     * crosshair indicating that it is ready for action.
     *
     * @param mouseEvent event that indicates that the mouse has moved within a
     * possible picking area.
     */
    @Override
    public void mouseMoved(final java.awt.event.MouseEvent mouseEvent) {

        pickCanvas.setShapeLocation(mouseEvent);

        PickResult[] result = pickCanvas.pickAllSorted();

        OrbitViewer.getStatusBar().handleCursorPositionChange(result);

        if (result != null) {

            OrbitViewer.getViewBranchArray()[0].getCanvas().setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));

        } else {

            OrbitViewer.getViewBranchArray()[0].getCanvas().setCursor(Cursor.getDefaultCursor());

        }
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged.
     * resort to default behavior
     *
     * @param mouseEvent event that indicates that the mouse is being dragged
     * within a possible picking area.
     */
    @Override
    public void mouseDragged(final java.awt.event.MouseEvent mouseEvent) {
    }

    /**
     * Invoked when a mouse button has been released on a component. Resort to
     * default behavior
     *
     * @param mouseEvent event that indicates that the mouse is being released
     * after a click
     */
    @Override
    public void mouseReleased(final java.awt.event.MouseEvent mouseEvent) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on
     * a component. When this event is generated by the orbit shape, the
     * coordinates corresponding to the location being clicked are being
     * displayed
     *
     * @param mouseEvent event that indicates that the mouse is being clicked
     * while in a picking area.
     */
    @Override
    public void mouseClicked(final java.awt.event.MouseEvent mouseEvent) {

        if (picking) {

            pickCanvas.setShapeLocation(mouseEvent);

            PickResult[] result = pickCanvas.pickAllSorted();

            if (result == null) {
                return;
            }

            PickIntersection pi = result[0].getIntersection(0);

            Node node = result[0].getObject();

            if (!(node instanceof Shape3D)) {

                return;
            }
            if (node instanceof FootpointShape) {

                return;
            }
            if (node instanceof AnimatedShape) {

                if (result.length == 1 || node.getUserData() != null) {

                    return;
                } else {

                    node = result[1].getObject();
                    if (node instanceof PositionShape == false) {
                        return;
                    }
                    pi = result[1].getIntersection(0);
                }
            }
            CoordBranch cb = new CoordBranch((PositionShape) node, pi);

            pickList.add(cb);

            int index = ContentBranch.getSatBranch().numChildren();
            ContentBranch.getSatBranch().insertChild(cb, index);
        }
    }

    /**
     * Invoked when a mouse exits a component. Resort to default behavior
     *
     * @param mouseEvent event to indicate that the mouse has left a possible
     * picking area.
     */
    @Override
    public void mouseExited(final java.awt.event.MouseEvent mouseEvent) {
    }

    /**
     * Call to respond to a change in the text font and width
     *
     * @param transform the new transform corresponding to the new width of the
     * elements.
     */
    public static void setTransform(final Transform3D transform) {

        if (pickList.isEmpty()) {
            return;
        }

        Iterator<CoordBranch> itr = pickList.iterator();

        while (itr.hasNext()) {

            CoordBranch b = ((CoordBranch) itr.next());
            b.setTransform(transform);
        }
    }

    /**
     * Reference to the vector that contains the list of elements picked and
     * displayed.
     *
     * @return the list of picked elements.
     */
    public static ArrayList<CoordBranch> getPickList() {
        return pickList;
    }

    private class CoordBranch extends BranchGroup {

        private final TransformGroup transformGroup = new TransformGroup();
        private final long time;
        private String s = null;
        private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private final Font3D font3d;
        private final Appearance app = new Appearance();
        private Point3d originalPoint = new Point3d();

        public CoordBranch(final PositionShape node, final PickIntersection pi) {

            super();

            df.setTimeZone(Util.UTC_TIME_ZONE);

            setCapability(BranchGroup.ALLOW_DETACH);
            setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            setCapability(BranchGroup.ALLOW_DETACH);

            transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
            transformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

            //  int vi = pi.getPrimitiveVertexIndices()[pi.getClosestVertexIndex()];
            // int vc = (pi.getGeometryArray()).getVertexCount();
            time = Util.interpolateOrbitTime(node, pi);
            originalPoint = pi.getPointCoordinates();

            font3d = new Font3D(new Font(null, Font.PLAIN, fontSize), new FontExtrusion());

            ColoringAttributes ca = new ColoringAttributes();

            ca.setColor(new Color3f(pi.getPointColor().get()));

            app.setColoringAttributes(ca);

            Transform3D t = new Transform3D();
            t.set(OrbitViewer.getInfoPane().getSatWidth());

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            s = nf.format(originalPoint.x) + " "
                    + nf.format(originalPoint.y) + " "
                    + nf.format(originalPoint.z);

            setTransform(t);
            addChild(transformGroup);
        }

        protected void createGeometries(final Point3d p) {

            float ss = SatBranch.getSymbolSize();

            float adjust;
            if (ss < 1) {
                adjust = ss * 2;
            } else if (ss < 2) {
                adjust = 1f / (2f * ss);
            } else {
                adjust = 1f / ss;
            }

            Point3d po = new Point3d(p.x / adjust, p.y / adjust, (p.z) / adjust);
            Point3f pos = new Point3f(po);
            Point3d tpo = new Point3d(p.x / adjust, (p.y / adjust) - fontSize, p.z / adjust);
            Point3f tpos = new Point3f(tpo);

            String stime = df.format(new Date(time));
            Text3D t = new Text3D(font3d, s, pos, Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);
            Text3D ttime = new Text3D(font3d, stime, tpos, Text3D.ALIGN_FIRST, Text3D.PATH_RIGHT);

            t.setCapability(Geometry.ALLOW_INTERSECT);
            ttime.setCapability(Geometry.ALLOW_INTERSECT);

            //mark location of coordinate
            LineArray line = new LineArray(4, LineArray.COORDINATES);
            Point3d[] pts = new Point3d[4];

            pts[0] = new Point3d((p.x + adjust / 4), (p.y + adjust / 4), p.z);
            pts[1] = new Point3d((p.x - adjust / 4), (p.y - adjust / 4), p.z);
            pts[2] = new Point3d((p.x - adjust / 4), (p.y + adjust / 4), p.z);
            pts[3] = new Point3d((p.x + adjust / 4), (p.y - adjust / 4), p.z);

            line.setCoordinates(0, pts);
            LineAttributes lineAttributes = new LineAttributes();
            lineAttributes.setLineWidth(3);
            app.setLineAttributes(lineAttributes);
            line.setCapability(Geometry.ALLOW_INTERSECT);

            BranchGroup textBranch = new BranchGroup();
            textBranch.setCapability(BranchGroup.ALLOW_DETACH);
            Transform3D textTransform = new Transform3D();
            TransformGroup textGroup = new TransformGroup();

            textTransform.setScale(adjust);

            textBranch.addChild(textGroup);

            OrientedShape3D orientCoord = new OrientedShape3D(t, app, OrientedShape3D.ROTATE_ABOUT_POINT, pos);
            OrientedShape3D orientTime = new OrientedShape3D(ttime, app, OrientedShape3D.ROTATE_ABOUT_POINT, pos);
            textGroup.addChild(orientCoord);
            textGroup.addChild(orientTime);
            textGroup.setTransform(textTransform);

            transformGroup.addChild(textBranch);

            BranchGroup crossBranch = new BranchGroup();
            crossBranch.setCapability(BranchGroup.ALLOW_DETACH);
            crossBranch.addChild(new Shape3D(line, app));

            transformGroup.addChild(crossBranch);
        }

        private void setTransform(final Transform3D transform) {

            transformGroup.removeAllChildren();

            Point3d p = new Point3d(originalPoint.x, originalPoint.y, originalPoint.z);
            p.scale(1 / transform.getScale());

            transformGroup.setTransform(transform);

            createGeometries(p);
        }
    } // end of CoordBranch
}
