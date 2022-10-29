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
 * $Id: OrbitShape.java,v 1.42 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on March 18, 2002, 10:25 AM
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.gui.InfoPanel;
import gov.nasa.gsfc.spdf.orb.gui.LineStyle;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphShape;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import java.awt.Color;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.vecmath.Color3f;

/**
 * The OrbitShape class creates the geometry and appearance of a satellite orbit
 * path
 *
 * @author rchimiak
 * @version $Revision: 1.42 $
 */
public abstract class OrbitShape extends PositionShape {

    protected SatelliteGraphChangeListener sgChangeListnr
            = new SatelliteGraphChangeListener();
    protected String name;

    public OrbitShape() {

        super();
    }

    /**
     * Creates an OrbitShape based on the data received from the server
     *
     */
    public OrbitShape(SatelliteGraphProperties satellitegp,
            SatelliteGraphTableModel graphModl, InfoPanel infoPane) {

        this();

        sgp = satellitegp;
        graphModel = graphModl;
        if (graphModel != null) {
            graphModel.addTableModelListener(sgChangeListnr);
        }

        appearance.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);
        appearance.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
        appearance.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);
        setAppearance(infoPane.getLineWidth());

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

            default:

                lineAttributes.setLineWidth(lineWidth);
                lineAttributes.setLinePattern(Util.getLinePattern(sgp.getLineStyle().toString()));
        }

        appearance.setLineAttributes(lineAttributes);
        RenderingAttributes ra = new RenderingAttributes();
        ra.setCapability(RenderingAttributes.ALLOW_VISIBLE_READ);
        ra.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
        appearance.setRenderingAttributes(ra);
        setAppearance(appearance);

        setAppearance(appearance);
    }

    public abstract void buildShape(Object satLocation);

    public void removeSatelliteGraphChangeListener() {

        if (sgChangeListnr != null && graphModel != null) {

            graphModel.removeTableModelListener(sgChangeListnr);
        }
    }

    @Override
    public String getName() {

        return name;
    }

    public void setAnimatedShape(AnimatedShape shape, int j) {

        as = shape;
    }

    public AnimatedShape getAnimatedShape() {

        return as;
    }

    public void clear() {

        as = null;
        sgp = null;
    }

    protected class SatelliteGraphChangeListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {

            int row = e.getFirstRow();

            if (((Boolean) graphModel.getValueAt(row, 0))
                    && sgp.getDisplayName().equalsIgnoreCase((String) graphModel.getValueAt(row, 1))) {

                switch (e.getColumn()) {

                    case 2:

                        sgp.setColor((Color) graphModel.getValueAt(row, 2));

                        GeometryArray geo = (GeometryArray) getGeometry();

                        for (int i = 0; i < geo.getVertexCount(); i++) {
                            geo.setColor(i, new Color3f(sgp.getColor()));
                        }

                        as.setColor(new Color3f(sgp.getColor()));
                        break;
                    case 3:
                        as.setShape((SatelliteGraphShape) graphModel.getValueAt(row, 3));
                        break;
                    case 4:
                        sgp.setLineStyle((LineStyle) graphModel.getValueAt(row, 4));
                        appearance.getLineAttributes().setLinePattern(Util.getLinePattern(sgp.getLineStyle().toString()));

                        break;

                    default:
                        break;
                }
            }
        }
    }
}
