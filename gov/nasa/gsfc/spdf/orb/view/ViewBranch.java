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
 * $Id: ViewBranch.java,v 1.47 2017/03/06 20:05:00 rchimiak Exp $
 *
 * Created on March 15, 2002, 11:16 AM
 */
package gov.nasa.gsfc.spdf.orb.view;

import java.util.Vector;
import java.awt.event.MouseEvent;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import gov.nasa.gsfc.spdf.orb.content.GeoSatBranch;
import gov.nasa.gsfc.spdf.orb.content.SatBranch;
import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

/**
 * The ViewBranch class implements view branches for the scene graph.
 *
 * @author rchimiak
 * @version $Revision: 1.47 $
 */
public class ViewBranch extends BranchGroup {

    private final ViewingPlatform viewingPlatform = new ViewingPlatform();
    private final Canvas3D canvas3D = createCanvas3D(false);
    private View view = null;
    private SatBranch sb = null;
    private final Vector3d viewVector = new Vector3d();
    private double viewDistance;
    private final Point3d center = new Point3d();
    private final Point3d origin = new Point3d(0.0, 0.0, 0.0);
    private double radius = 0;
    private MyOrbitBehavior orbit = null;
    private static final int originalZoomFactor = 10;

    //for Mac with Java 7 and new 3D component
    //private boolean relocate = true;
    /**
     * Creates a new viewBranch
     */
    public ViewBranch() {
        setCapability(BranchGroup.ALLOW_DETACH);
        setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);

        createView();
    }

    private void createView() {
        Viewer viewer = new Viewer(canvas3D);
        viewer.setViewingPlatform(viewingPlatform);
        view = viewer.getView();

        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.attachViewPlatform(viewingPlatform.getViewPlatform());

        addChild(viewingPlatform);
    }

    public MyOrbitBehavior getOrbitBehavior() {
        return orbit;
    }
    
    

    /**
     * Utility method to create a Canvas3D component. The Canvas3D is used by
     * java 3D to output the view
     *
     * @param offscreen true for an offscreen canvas, false if this canvas is to
     * be displayed on the screen
     */
    public Canvas3D createCanvas3D(boolean offscreen) {

        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice d = e.getDefaultScreenDevice();
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        GraphicsConfiguration c = d.getBestConfiguration(template);
        template.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);

        Canvas3D c3d = new Canvas3D(c, offscreen);
        //for Mac with Java 7 and new 3D component
       /* Canvas3D c3d = new Canvas3D(c, offscreen) {
         @Override
         public void postRender() {

         if (relocate) {

         relocate = false;
         this.setLocation(0, 0);
         }
         }
         };*/

        return c3d;
    }

    /**
     * The implementation of the 3 planar views with respect to the XY, XZ, and
     * YZ axes
     *
     * @param axis designates which view is being implemented
     */
    public void setPlanarView(int axis) {
        Transform3D t3d;
        viewVector.set(center.x, center.y, center.z);
        if (sb != null) {
            viewDistance = radius / Math.tan(view.getFieldOfView() / 2);
        }

        t3d = doTransform(axis);
        viewingPlatform.getViewPlatformTransform().setTransform(t3d);
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setScreenScale((view.getCanvas3D(0).getScreen3D().getPhysicalScreenWidth()) * 1.2 / (distanceFromCenter(t3d)));
    }

    public void setZoomPlanarView(int axis, int selection) {

        Transform3D t3d;

        if (sb != null) {

            Point3f p = sb.getAnimationNodes()[selection].getPositionPath().getPosition();

            Point3d dposition = new Point3d((double) p.x, (double) p.y, (double) p.z);

            viewVector.set(dposition.x, dposition.y, dposition.z);
            viewDistance = radius / Math.tan(view.getFieldOfView() / 2);
        }

        t3d = doTransform(axis);
        viewingPlatform.getViewPlatformTransform().setTransform(t3d);
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setScreenScale((view.getCanvas3D(0).getScreen3D().getPhysicalScreenWidth()) * 1.2 / (distanceFromCenter(t3d)));
       
    }

    /**
     * Implementation of the main view in a 3 dimensional format.
     *
     * @param axis designates which projected view is being implemented as the
     * center view
     */
    public void setCenterView(int axis, int projection) {
        view.stopView();

        view.setFieldOfView(.85);

        viewVector.set(center.x, center.y, center.z);

        viewDistance = radius / Math.tan(view.getFieldOfView() / 2);

        Transform3D t3d = doTransform(axis);

        getOrbitBehavior().setLocked(true);
        viewingPlatform.getViewPlatformTransform().setTransform(t3d);

        getOrbitBehavior().integrateTransforms();

        float backDistance = (((float) viewDistance + (float) radius) * 2);

        if (sb != null && sb.getCoordinateSystem() == CoordinateSystem.GEO) {

            backDistance *= 10;
        }
        view.setBackClipDistance(backDistance);
        view.setBackClipPolicy(View.VIRTUAL_EYE);
        float frontDistance = backDistance / 100;

        view.setFrontClipDistance(frontDistance);
        view.setFrontClipPolicy(View.VIRTUAL_EYE);

        if (projection == View.PARALLEL_PROJECTION) {

            view.setProjectionPolicy(View.PARALLEL_PROJECTION);
            view.setScreenScalePolicy(View.SCALE_EXPLICIT);
            view.setScreenScale((view.getCanvas3D(0).getScreen3D().getPhysicalScreenWidth()) * 1.1 / (distanceFromCenter(t3d)));

        } else {

            view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
            view.setScreenScalePolicy(View.SCREEN_VIEW);
            view.setWindowEyepointPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);
            view.getViewPlatform().setViewAttachPolicy(View.NOMINAL_HEAD);
            view.setFieldOfView(getCoordinateSpecificBounds().getRadius() / radius);
        }

        view.startView();

        viewVector.set(center.x, center.y, center.z);

        Transform3D tt3d = doTransform(axis);

        viewingPlatform.getViewPlatformTransform().setTransform(tt3d);
        this.getOrbitBehavior().setTransFactors(10, 10);
        this.getOrbitBehavior().setZoomCount(0);

    }

    public void setZoomView(TransformGroup tg0, int selection, int projection) {

        Point3f p = sb.getAnimationNodes()[selection].getPositionPath().getPosition();

        Point3d dposition = new Point3d((double) p.x, (double) p.y, (double) p.z);
        orbit.setRotationCenter(dposition);

        BoundingSphere[] bounds = new BoundingSphere[2];
        bounds[0] = getCoordinateSpecificBounds();
        bounds[1] = new BoundingSphere();
        bounds[1].combine(dposition);
        bounds[1].combine(bounds[0]);

        view.setFieldOfView(0.9);
        radius = bounds[1].getRadius();

        viewDistance = 1.1*radius / Math.tan(view.getFieldOfView() / 2);

        float backDistance = (((float) viewDistance + (float) radius) * 2);

        setZoomTransform(tg0, dposition);
        view.setBackClipDistance(backDistance);
        view.setBackClipPolicy(View.VIRTUAL_EYE);
        float frontDistance = backDistance / 100;

        view.setFrontClipDistance(frontDistance);
        view.setFrontClipPolicy(View.VIRTUAL_EYE);

        if (projection == View.PARALLEL_PROJECTION) {

            view.setProjectionPolicy(View.PARALLEL_PROJECTION);
            view.setScreenScalePolicy(View.SCALE_EXPLICIT);
            Transform3D t = new Transform3D();
            viewingPlatform.getViewPlatformTransform().getTransform(t);

            Vector3d centerToView = new Vector3d();
            Matrix3d rotMatrix = new Matrix3d();
            Vector3d transVector = new Vector3d();

            t.get(rotMatrix, transVector);
            centerToView.sub(transVector, dposition);

            double distance = sb != null ? centerToView.length() * bounds[1].getRadius() / radius : 1;

            view.setScreenScale((view.getCanvas3D(0).getScreen3D().getPhysicalScreenWidth()) * 1.1 / (distance));

        } else {

            view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
            view.setScreenScalePolicy(View.SCREEN_VIEW);
            view.setWindowEyepointPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);
            view.getViewPlatform().setViewAttachPolicy(View.NOMINAL_HEAD);
            view.setFieldOfView(getCoordinateSpecificBounds().getRadius() / radius);
        }
        

    }

    private void setZoomTransform(TransformGroup tg, Point3d p) {

        Transform3D t = new Transform3D();
        tg.getTransform(t);
        viewingPlatform.getViewPlatformTransform().setTransform(t);
        Vector3d origTrans = new Vector3d();
        Matrix3d origRot = new Matrix3d();
        t.get(origRot, origTrans);

        Vector3d trans = new Vector3d();
        trans.x = p.x;
        trans.y = p.y;
        trans.z = p.z;

        Transform3D toMove = new Transform3D();
        toMove.setTranslation(origTrans);

        Transform3D toRotate = new Transform3D();
        toRotate.setRotation(origRot);

        t.set(toRotate);
        t.setTranslation(trans);

        Transform3D toTrans = new Transform3D();

        Vector3d zv = new Vector3d(0, 0, viewDistance);
        toTrans.setTranslation(zv);
        t.mul(toTrans);

        getViewingPlatform().getViewPlatformTransform().setTransform(t);

    }

    private double distanceFromCenter(Transform3D t) {

        Vector3d centerToView = new Vector3d();
        Matrix3d rotMatrix = new Matrix3d();
        Vector3d transVector = new Vector3d();
        Point3d rotationCenter = new Point3d();
        t.get(rotMatrix, transVector);
        centerToView.sub(transVector, rotationCenter);
        if (sb != null) {
            return centerToView.length() * (getCoordinateSpecificBounds()).getRadius() / radius;
        } else {
            return 1;
        }
    }

    /**
     * Allows the center view to appear moved about a point of interest.
     * Includes rotation, translation and zoomimg. The viewving platform is in
     * fact being acted upon.
     */
    public void setOrbitBehavior() {

        orbit = new MyOrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
        orbit.setRotXFactor(0.5);
        orbit.setRotYFactor(0.5);
        orbit.setRotationCenter(origin);
        orbit.setZoomFactor(originalZoomFactor);
        // orbit.setTransFactors(10, 10);

        orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));

        viewingPlatform.setViewPlatformBehavior(orbit);
    }

    public void setPlanarOrbitBehavior() {

        orbit = new MyOrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);

        orbit.setZoomFactor(10);
        orbit.setRotateEnable(false);

        orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));

        viewingPlatform.setViewPlatformBehavior(orbit);
    }

    /**
     * Handles the projection of the orbit on respectively, the ZY plane, the XZ
     * plane and the XY plane.
     *
     * @param axis designates the axis of rotation
     */
    public Transform3D doTransform(int axis) {

        Transform3D t = new Transform3D();

        switch (axis) {
            case Axis.xAxis:
                viewVector.x += viewDistance;
                t.set(viewVector);
                Transform3D rotateAboutY = new Transform3D();
                rotateAboutY.rotY((Math.PI / 2.0));
                t.mul(rotateAboutY);
                Transform3D rotateAboutZ = new Transform3D();
                rotateAboutZ.rotZ(Math.PI / 2.0);
                t.mul(rotateAboutZ);

                break;
            case Axis.yAxis:
                viewVector.y += viewDistance;
                t.set(viewVector);
                Transform3D rotateAboutX = new Transform3D();
                rotateAboutX.rotX(-Math.PI / 2.0);
                t.mul(rotateAboutX);
                Transform3D rotateZ = new Transform3D();
                rotateZ.rotZ(Math.PI);
                t.mul(rotateZ);
                break;
            case Axis.zAxis:
                viewVector.z += viewDistance;
                t.set(viewVector);
                break;
            default:
                break;
        }
        return t;
    }

    /**
     * Some of the parameters needed to draw the center view with maximizing the
     * real estate need to be set originally.
     *
     * @param satBranch the branch with the content that needs to be displayed
     */
    public void setSatBranch(SatBranch satBranch) {

        sb = satBranch;
        if (sb != null) {

            radius = getCoordinateSpecificBounds().getRadius();
            getCoordinateSpecificBounds().getCenter(center);
        }
    }

    public BoundingSphere getCoordinateSpecificBounds() {

        return sb.getCoordinateSystem() == CoordinateSystem.GEO
                ? ((GeoSatBranch) sb).getGEOBounds() : sb.getBounds(2);
    }

    /**
     * Returns the canvas associated with the view branch.
     *
     * @return the canvas for that view
     */
    public Canvas3D getCanvas() {
        return canvas3D;
    }

    /**
     * Returns the viewing platform associated with the view branch.
     *
     * @return the viewing platform
     */
    public ViewingPlatform getViewingPlatform() {
        return viewingPlatform;
    }

    /**
     * Returns theView object.
     *
     * @return the view
     */
    public View getView() {
        return view;
    }

    public void transformChanged(Transform3D transform) {

        Matrix3d rotMatrix = new Matrix3d();
        transform.get(rotMatrix);

        Transform3D rotTransform = new Transform3D();
        rotTransform.set(rotMatrix);

        Transform3D viewTransform = new Transform3D();
        viewingPlatform.getViewPlatformTransform().getTransform(viewTransform);

        Matrix3d viewRotMatrix = new Matrix3d();
        viewTransform.get(viewRotMatrix);
        viewRotMatrix.invert();

        Transform3D invertViewRot = new Transform3D();
        invertViewRot.set(viewRotMatrix);

        viewTransform.mul(invertViewRot, viewTransform);
        viewTransform.mul(rotTransform, viewTransform);

        viewingPlatform.getViewPlatformTransform().setTransform(viewTransform);
    }

    public class MyOrbitBehavior extends OrbitBehavior {

        Vector< ViewBranch> callbacks = new Vector<ViewBranch>();
        Vector< MyOrbitBehavior> planarOrbitBehavior = new Vector<MyOrbitBehavior>();
        boolean locked = false;
        public int last_y;
        public int current_y;
        private int zoomCount = 0;

        public void setZoomCount(int zoomCount) {
            this.zoomCount = zoomCount;
        }

        public int getZoomCount() {
            return zoomCount;
        }

        public MyOrbitBehavior(javax.media.j3d.Canvas3D canvas, int reverse) {

            super(canvas, reverse);
        }

        public void setLocked(boolean locked) {

            this.locked = locked;
        }

        public boolean isLocked() {
            return locked;
        }

        public void addCallback(ViewBranch callback) {

            callbacks.addElement(callback);
        }

        public void removeCallback(ViewBranch callback) {

            callbacks.removeElement(callback);
        }

        public void register(MyOrbitBehavior planarOrbitBehaviorElement) {
            planarOrbitBehavior.add(planarOrbitBehaviorElement);

        }

        @Override
        protected void processMouseEvent(final MouseEvent evt) {

            super.processMouseEvent(evt);

            if (zoom(evt)) {

                switch (view.getProjectionPolicy()) {

                    case View.PARALLEL_PROJECTION:
                        if ((evt.getID() == MouseEvent.MOUSE_DRAGGED) || (evt.getID() == MouseEvent.MOUSE_WHEEL)) {

                            current_y = evt.getY();
                            if (last_y == 0) {
                                last_y = current_y;
                            }
                            int diff_y = (current_y - last_y); //if positive, zoom out

                            TransformGroup tg = getViewingPlatform().getViewPlatformTransform();
                            Transform3D t = new Transform3D();
                            tg.getTransform(t);

                            t.setScale(1.05);

                            tg.setTransform(t);

                            double currentScreenScale = view.getScreenScale();

                            double c = currentScreenScale * 10;
                            if (c < 1) {
                                c = 1;
                            }

                            double transfactor;

                            switch (evt.getID()) {

                                case MouseEvent.MOUSE_DRAGGED:
                                    view.setScreenScale(diff_y < 0
                                            ? currentScreenScale - (Math.abs(diff_y) * (0.0001 * c))
                                            : currentScreenScale + (Math.abs(diff_y) * (0.0001 * c)));

                                    transfactor = diff_y < 0
                                            ? getTransXFactor() * (1d + (0.006d / c))
                                            : getTransXFactor() * (1d - (0.006d / c));

                                    if (getTransXFactor() < 0.3d) {
                                        zoomCount = diff_y < 0
                                                ? zoomCount - 1 : zoomCount + 1;
                                    }
                                    if (zoomCount == 0) {
                                        setTransFactors(transfactor, transfactor);
                                    }
                                    break;

                                case MouseEvent.MOUSE_WHEEL:
                                    view.setScreenScale(((java.awt.event.MouseWheelEvent) evt).getWheelRotation() < 0
                                            ? currentScreenScale - (0.0004 * c)
                                            : currentScreenScale + (0.0004 * c));

                                    transfactor = ((java.awt.event.MouseWheelEvent) evt).getWheelRotation() < 0
                                            ? getTransXFactor() * (1d + (0.006d / c))
                                            : getTransXFactor() * (1d - (0.006d / c));

                                    if (getTransXFactor() < 0.3d) {
                                        zoomCount = ((java.awt.event.MouseWheelEvent) evt).getWheelRotation() < 0
                                                ? zoomCount - 1 : zoomCount + 1;
                                    }
                                    if (zoomCount == 0) {
                                        setTransFactors(transfactor, transfactor);
                                    }
                                    break;

                            }

                            last_y = current_y;
                        } else if (evt.getID() == MouseEvent.MOUSE_PRESSED) {

                            last_y = evt.getY();
                        }
                        break;
                }

                if (zoom(evt) && !planarOrbitBehavior.isEmpty()) {
                    
                    java.util.Enumeration e = planarOrbitBehavior.elements();

                    while (e.hasMoreElements()) {

                        ((MyOrbitBehavior) e.nextElement()).processMouseEvent(evt);
                    }

                }
            }
        }

        boolean zoom(MouseEvent evt) {

            if (evt instanceof java.awt.event.MouseWheelEvent) {

                return true;
            }
            return evt.isAltDown() && !evt.isMetaDown();
        }

        @Override
        public synchronized void integrateTransforms() {

            super.integrateTransforms();

            if (!callbacks.isEmpty()) {

                Transform3D transform = new Transform3D();
                targetTG.getTransform(transform);

                java.util.Enumeration e = callbacks.elements();
                while (e.hasMoreElements()) {

                    ((ViewBranch) e.nextElement()).transformChanged(transform);
                }
            }
        }
    }
}
