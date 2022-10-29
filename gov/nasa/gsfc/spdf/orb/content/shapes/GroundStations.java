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
 * $Id: GroundStations.java,v 1.13 2015/10/30 14:18:50 rchimiak Exp $
 * Created on July 19, 2007, 10:46 AM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import java.util.Hashtable;

//import javax.vecmath.*;
//import javax.media.j3d.*;
import javax.vecmath.Point3f;

//import gov.nasa.gsfc.spdf.orb.utils.*;
import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;
import gov.nasa.gsfc.spdf.orb.gui.GroundStationsTableModel;

import gov.nasa.gsfc.spdf.orb.content.behaviors.SwitchGroup;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 *
 * @author rachimiak
 */
public class GroundStations extends EarthShapeExtension {

    protected static GroundStationsTableModel model = null;
    private Point3f[] pointArray = null;
    private int alignment = Text3D.ALIGN_FIRST;
    private final Hashtable< String, Integer> hashtable;

    /**
     * Creates a new instance of GeoGrid
     */
    public GroundStations(GroundStationsTableModel model) {

        super();
        this.hashtable = new Hashtable< String, Integer>();

        GroundStations.model = model;

        if (model == null || model.getRowCount() == 0) {
            return;
        }

        pointArray = new Point3f[model.getRowCount()];

        for (int i = 0; i < model.getRowCount(); i++) {

            double[] spher = {1.0d, ((Float) model.getValueAt(i, 3)).doubleValue(), ((Float) model.getValueAt(i, 4)).doubleValue()};

            Point3d po = new Point3d(Footpoint.sphericalToCartesian(spher));

            pointArray[i] = new Point3f(po);
        }
        setLabels();
    }

    public void setLabels() {

        getEarthTransform().setScale(getAdjust());
        getTransformG().setTransform(getEarthTransform());
        addLabels();
    }

    @Override
    public void resize() {

        setEarthTransform(new Transform3D());
        getEarthTransform().setScale(getAdjust());

        getTransformG().setTransform(getEarthTransform());

        for (int i = 0; i < model.getRowCount(); i++) {

            int j = (hashtable.get((String) model.getValueAt(i, 2)));

            OrientedShape3D orientedShape = (OrientedShape3D) ((SwitchGroup) getTransformG().getChild(j)).getChild(0);
            orientedShape.removeAllGeometries();
            Point3f pt = new Point3f(pointArray[j].x / getAdjust(), pointArray[j].y / getAdjust(), pointArray[j].z / getAdjust());
            orientedShape.addGeometry(
                    new Text3D(getFont3d(), " " + ((String) model.getValueAt(i, 2)).toLowerCase() + " ", pt, alignment, Text3D.PATH_RIGHT));
            orientedShape.addGeometry(new Text3D(getCrossFont(), ".", pt, Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT));
            orientedShape.setRotationPoint(pt);
        }
    }

    public void reside(int alignment) {

        this.alignment = alignment;

        for (int i = 0; i < model.getRowCount(); i++) {

            int j = (hashtable.get((String) model.getValueAt(i, 2)));

            OrientedShape3D orientedShape = (OrientedShape3D) ((SwitchGroup) getTransformG().getChild(j)).getChild(0);
            orientedShape.removeAllGeometries();
            Point3f pt = new Point3f(pointArray[j].x / getAdjust(), pointArray[j].y / getAdjust(), pointArray[j].z / getAdjust());

            orientedShape.addGeometry(
                    new Text3D(getFont3d(), " " + ((String) model.getValueAt(i, 2)).toLowerCase() + " ", pt, alignment, Text3D.PATH_RIGHT));
            orientedShape.addGeometry(new Text3D(getCrossFont(), ".", pt, Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT));
            orientedShape.setRotationPoint(pt);
        }
    }

    @Override
    public void color(Color3f color) {

        for (int i = 0; i < model.getRowCount(); i++) {

            int j = (hashtable.get((String) model.getValueAt(i, 2)));

            OrientedShape3D orientedShape = (OrientedShape3D) ((SwitchGroup) getTransformG().getChild(j)).getChild(0);
            orientedShape.getAppearance().getColoringAttributes().setColor(
                    color);
        }
    }

    public SwitchGroup getSwitch(Object acr) {

        int i = (hashtable.get(acr));

        return (SwitchGroup) getTransformG().getChild(i);
    }

    public void selectAll() {

        for (int i = 0; i < hashtable.size(); i++) {

            SwitchGroup sg = ((SwitchGroup) getTransformG().getChild(i));

            sg.setChildMask(sg.getOptions()[0]);
        }
    }

    public void deselectAll() {

        for (int i = 0; i < hashtable.size(); i++) {

            SwitchGroup sg = ((SwitchGroup) getTransformG().getChild(i));

            sg.setChildMask(sg.getOptions()[1]);
        }
    }

    @Override
    public void clear() {

        java.util.Enumeration enumeration = getTransformG().getAllChildren();

        while (enumeration != null && enumeration.hasMoreElements()) {

            Object object = enumeration.nextElement();

            if (object instanceof SwitchGroup) {

                Object firstChild = ((SwitchGroup) object).getChild(0);

                ((OrientedShape3D) firstChild).removeAllGeometries();

                ((SwitchGroup) object).removeAllChildren();

            }
        }
        getTransformG().removeAllChildren();
    }

    public void addLabels() {

        for (int i = 0; i < model.getRowCount(); i++) {

            Point3f pt = new Point3f(pointArray[i].x / getAdjust(), pointArray[i].y / getAdjust(), pointArray[i].z / getAdjust());

            Text3D textGeom
                    = new Text3D(getFont3d(), " " + ((String) model.getValueAt(i, 2)).toLowerCase() + " ", pt, alignment, Text3D.PATH_RIGHT);

            OrientedShape3D textShape = new OrientedShape3D(textGeom, new Appearance(), OrientedShape3D.ROTATE_ABOUT_POINT, pt);
            ColoringAttributes c = new ColoringAttributes();
            c.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
            c.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

            textShape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
            textShape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

            c.setColor(226f / 255f, 26f / 255f, 26f / 255f);

            textShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            textShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

            textShape.getAppearance().setColoringAttributes(c);
            textShape.addGeometry(new Text3D(getCrossFont(), ".", pt, Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT));
            SwitchGroup switchg = new SwitchGroup();
            textShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
            textShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
            textShape.setCapability(OrientedShape3D.ALLOW_POINT_READ);
            textShape.setCapability(OrientedShape3D.ALLOW_POINT_WRITE);
            switchg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            switchg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            switchg.setCapability(SwitchGroup.ALLOW_CHILDREN_EXTEND);
            switchg.addChild(textShape);
            hashtable.put(((String) model.getValueAt(i, 2)), i);
            getTransformG().addChild(switchg);
        }
    }

    public void rotateStations(double mjd, double hours, CoordinateSystem displayCoord) {

        rotate(mjd, hours, displayCoord);
    }
}
