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
 * $Id: FootpointShape.java,v 1.30 2015/10/30 14:18:50 rchimiak Exp $
 * Created on May 16, 2007, 11:05 AM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.LineAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

import javax.xml.datatype.XMLGregorianCalendar;

import gov.nasa.gsfc.spdf.ssc.client.BTraceData;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import gov.nasa.gsfc.spdf.orb.content.behaviors.SwitchGroup;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphShape;
import gov.nasa.gsfc.spdf.orb.content.behaviors.FootpointAnimInterpolator;
import gov.nasa.gsfc.spdf.orb.content.FootpointsGroup;

/**
 *
 * @author rachimiak
 */
public class FootpointShape extends PositionShape {

    private static final double MIN_DISTANCE = 1.006d; //RE
    private final SatelliteGraphChangeListener sgChangeListnr
            = new SatelliteGraphChangeListener();
    private final Transform3D trans = new Transform3D();
    private final TransformGroup tg = new TransformGroup();
    private FootpointAnimInterpolator positionPath;
    private final SwitchGroup switchGroup = new SwitchGroup();
    private final Point3d[] shape;
    private int size = 0;
    private boolean[] changeHemisphere = null;
    private static TransformGroup parent = null;
    private FootpointsGroup.HemisphericTypes hemisphericType;

    public FootpointShape(BTraceData data, List<XMLGregorianCalendar> time,
            SatelliteGraphProperties sgp, SatelliteGraphTableModel graphModl,
            InfoPanel infoPane, boolean[] change, int order) {

        super();

        this.sgp = sgp;
        this.changeHemisphere = change;
        size = time.size();

        shape = new Point3d[size];
        // as = new AnimatedShape(sgp.getShape().toString(), ((SatBranch.getSymbolSize()) * infoPane.getSatWidth()) * 0.60f, new Color3f(sgp.getColor()), "footpoint");
        as = new AnimatedShape(sgp.getShape().toString(), new Color3f(sgp.getColor()), "footpoint");
        as.setUserData("footpoint");
        switchGroup.setCapability(SwitchGroup.ALLOW_SWITCH_READ);
        switchGroup.setCapability(SwitchGroup.ALLOW_SWITCH_WRITE);
        switchGroup.setCapability(SwitchGroup.ALLOW_CHILDREN_EXTEND);

        switchGroup.addChild(as);

        graphModel = graphModl;

        if (graphModel != null) {

            graphModel.addTableModelListener(sgChangeListnr);
        }

        for (int i = 0; i < size; i++) {

            shape[i] = new Point3d(Footpoint.sphericalToCartesian(new double[]{MIN_DISTANCE,
                data.getLatitude().get(i).doubleValue(),
                data.getLongitude().get(i).doubleValue()}));
        }

        buildShape(time);

        if (shape.length > 0 && !isNaN(shape[0])) {
            trans.set(new Vector3d(shape[0]));
        }

        tg.setTransform(trans);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        tg.addChild(switchGroup);

        float[] knots = new float[shape.length];
        for (int i = 0; i < shape.length; i++) {
            knots[i] = (float) i / (float) (shape.length - 1);
        }

        Point3f[] positions = new Point3f[shape.length];

        for (int i = 0; i < shape.length; i++) {
            positions[i] = new Point3f();
            if (!isNaN(shape[i])) {

                positions[i].x = (float) shape[i].x;
                positions[i].y = (float) shape[i].y;
                positions[i].z = (float) shape[i].z;
            }
        }

        positionPath = new FootpointAnimInterpolator(new Alpha(-1, -1), tg,
                new Transform3D(), knots,
                positions, this, order);

        positionPath.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));

        tg.addChild(positionPath);

        appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

        setAppearance(infoPane.getLineWidth());

    }

    @Override
    public String getName() {

        return sgp.getDisplayName();
    }

    public int getSize() {

        return size;
    }

    public void setHemisphere(FootpointsGroup.HemisphericTypes type) {

        this.hemisphericType = type;
    }

    public FootpointsGroup.HemisphericTypes getHemisphere() {

        return hemisphericType;
    }

    public void clear() {

        positionPath.clear();
        as = null;
        positionPath = null;
    }

    public void setShapeParent(TransformGroup parent) {

        FootpointShape.parent = parent;
    }

    public static TransformGroup getShapeParent() {

        return parent;
    }

    public FootpointAnimInterpolator getPositionPath() {

        return positionPath;
    }

    public void setAnimEnable(boolean enable) {

        if (positionPath != null) {
            positionPath.setEnable(enable);
        }
    }

    public void buildShape(List<XMLGregorianCalendar> time) {

        removeAllGeometries();

        int i = 0;

        Long[] t = new Long[]{time.get(0).
            toGregorianCalendar().getTimeInMillis(),
            time.get(0).
            toGregorianCalendar().getTimeInMillis()};

        Color3f color = new Color3f(sgp.getColor());

        while (i < shape.length) {

            if (isNaN(shape[i])) {
                i++;
            } else if (changeHemisphere != null && changeHemisphere[i]) {

                changeHemisphere[i] = false;
            } else {
                if (i < shape.length) {
                    t[0] = time.get(i).
                            toGregorianCalendar().getTimeInMillis();

                }

                java.util.Vector<Point3d> v = new java.util.Vector<Point3d>();

                while (i < shape.length && !isNaN(shape[i]) && (changeHemisphere == null || !changeHemisphere[i])) {

                    v.add(shape[i]);

                    t[1] = time.get(i).
                            toGregorianCalendar().getTimeInMillis();

                    i++;

                }

                if (t[0].compareTo(t[1]) != 0) {
                    timeList.add(t);

                }

                GeometryArray geo = v.size() == 1
                        ? new LineStripArray(v.size() + 1,
                                LineArray.COORDINATES
                                | LineArray.COLOR_3,
                                new int[]{v.size() + 1})
                        : new LineStripArray(v.size(),
                                LineArray.COORDINATES
                                | LineArray.COLOR_3,
                                new int[]{v.size()});

                geo.setCapability(Geometry.ALLOW_INTERSECT);
                geo.setCapability(GeometryArray.ALLOW_COLOR_READ);
                geo.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
                geo.setCapability(GeometryArray.ALLOW_FORMAT_READ);
                geo.setCapability(GeometryArray.ALLOW_COUNT_READ);
                geo.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
                geo.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
                geo.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
                geo.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
                this.addGeometry(geo);

                java.util.Enumeration e = v.elements();
                if (v.size() == 1) {

                    Point3d p = v.get(0);
                    v.add(new Point3d(p.x, p.y, p.z));
                }
                int count = 0;

                while (e.hasMoreElements()) {

                    Point3d point3d = (Point3d) e.nextElement();

                    geo.setCoordinate(count, point3d);
                    geo.setColor(count, color);

                    count++;
                }
            }
            if (!timeList.isEmpty()) {

                t = new Long[]{timeList.get(timeList.size() - 1)[1],
                    timeList.get(timeList.size() - 1)[1]};
            }
        }
    }

    public AnimatedShape getAnimatedShape() {

        return as;
    }

    public void setAppearance(float lineWidth) {

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setCapability(LineAttributes.ALLOW_PATTERN_READ);
        lineAttributes.setCapability(LineAttributes.ALLOW_PATTERN_WRITE);

        int width = (int) lineWidth;

        switch (width) {

            case 0:

                lineAttributes.setLinePattern(LineAttributes.PATTERN_USER_DEFINED);
                lineAttributes.setPatternMask(0x0000);
                break;

            case 1:

                lineAttributes.setLineWidth(width);
                lineAttributes.setLinePattern(Util.getLinePattern(sgp.getLineStyle().toString()));
                break;

            default:

                lineAttributes.setLineWidth(width - 1);
                lineAttributes.setLinePattern(Util.getLinePattern(sgp.getLineStyle().toString()));
        }

        appearance.setLineAttributes(lineAttributes);
        setAppearance(appearance);
    }

    public void symbolWidthChanged(float width) {

        as.setRadius(width);
        as.setGeometry(as.getShape());
    }

    public TransformGroup getTransformGroup() {

        return tg;
    }

    public boolean isNaN(Point3d point) {

        return (new Double(point.x).compareTo(Double.NaN) == 0
                || new Double(point.y).compareTo(Double.NaN) == 0
                || new Double(point.z).compareTo(Double.NaN) == 0);

    }

    public boolean isNaN(int index) {

        return isNaN(shape[index]);
    }

    public boolean isChangingHemisphere(int index) {

        return (changeHemisphere != null && changeHemisphere[index] == true);
    }

    public boolean isVisible() {

        return (switchGroup.getChildMask().get(0));
    }

    public void setVisible(boolean visible) {

        switchGroup.setChildMask(visible ? switchGroup.getOptions()[0] : switchGroup.getOptions()[1]);
    }

    public void rotateAnimatedShape(float percentCompleted) {

        positionPath.processManual(percentCompleted);

        if (getPositionPath().getIndex(percentCompleted) < shape.length
                && !isNaN(shape[getPositionPath().getIndex(percentCompleted)])) {

            if (!isVisible()) {
                setVisible(true);
            }
        } else if (isVisible()) {
            setVisible(false);
        }
    }

    public void processFootpointManually() {

        positionPath.processFootpointManually();

    }

    public void removeSatelliteGraphChangeListener() {

        if (sgChangeListnr != null && graphModel != null) {

            graphModel.removeTableModelListener(sgChangeListnr);
        }
    }

    private class SatelliteGraphChangeListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {

            int row = e.getFirstRow();

            if (((Boolean) graphModel.getValueAt(row, 0))
                    && sgp.getDisplayName().equalsIgnoreCase((String) graphModel.getValueAt(row, 1))) {

                switch (e.getColumn()) {

                    case 2:

                        java.util.Enumeration enumeration = getAllGeometries();

                        while (enumeration != null && enumeration.hasMoreElements()) {

                            Geometry geo = (Geometry) enumeration.nextElement();

                            if (geo instanceof GeometryArray) {

                                for (int i = 0; i < ((GeometryArray) geo).getVertexCount(); i++) {
                                    ((GeometryArray) geo).setColor(i, new Color3f(sgp.getColor()));
                                }
                            }
                        }

                        as.setColor(new Color3f(sgp.getColor()));
                        break;

                    case 3:

                        as.setShape((SatelliteGraphShape) graphModel.getValueAt(row, 3));
                        break;

                    case 4:

                        appearance.getLineAttributes().setLinePattern(Util.getLinePattern(sgp.getLineStyle().toString()));
                        break;

                    default:

                        break;
                }
            }
        }
    }
}
