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
 * $Id: EarthSurfaces.java,v 1.16 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on November 18, 2002, 9:22 AM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.SurfaceWindow;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.TransparencyAttributes;

/**
 * The EarthSurfaces class implements the bow shock, magnetopause, and neutral
 * sheet objects.
 *
 * @author rchimiak
 * * @version $Revision: 1.16 $
 */
public class EarthSurfaces extends BranchGroup {

    private MHDSurf mhdSurfObj = null;
    private MHDPause mhdPauseObj = null;
    private MHDNeutral mhdNeutralObj = null;

    /**
     * Creates a new instance of Class.
     */
    public EarthSurfaces() {

        mhdSurfObj = new MHDSurf(OrbitViewer.getBowshockWindow());
        mhdPauseObj = new MHDPause(OrbitViewer.getMagnetopauseWindow());
        mhdNeutralObj = new MHDNeutral(OrbitViewer.getNeutralSheetWindow());

        mhdSurfObj.getShape().setAppearance(setAppearance(OrbitViewer.getBowshockWindow()));
        mhdPauseObj.getShape().setAppearance(setAppearance(OrbitViewer.getMagnetopauseWindow()));
        mhdNeutralObj.getShape().setAppearance(setAppearance(OrbitViewer.getNeutralSheetWindow()));

        OrbitViewer.getBowshockWindow().setModel(getBowshock());
        OrbitViewer.getMagnetopauseWindow().setModel(getMagnetopause());

        OrbitViewer.getNeutralSheetWindow().setModel(getNeutralSheet());
    }

    /**
     * Resolve the initial rotation when a graph is rebuild.
     */
    public void doInitialTransform() {

        mhdSurfObj.doInitialTransform();
        mhdPauseObj.doInitialTransform();
        if (!ControlPanel.isSolenocentric()) {
            mhdNeutralObj.doInitialTransform();
        }

    }

    /**
     *
     * @param mjc the modified Julian date calendar
     */
 //   public void setSatParams(final ModifiedJulianCalendar mjc) {
    //     mhdNeutralObj.setCalendar(mjc);
    //}
    private Appearance setAppearance(final SurfaceWindow wind) {

        Appearance ap = new Appearance();

        PolygonAttributes pta = new PolygonAttributes(wind.getPolygonAttributes(),
                PolygonAttributes.CULL_NONE, 0, true, 0);

        TransparencyAttributes trans = new TransparencyAttributes(TransparencyAttributes.NICEST, wind.getOpacity());

        ap.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
        ap.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
        ap.setCapability(Appearance.ALLOW_MATERIAL_READ);
        ap.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        ap.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
        ap.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

        trans.setCapability(TransparencyAttributes.ALLOW_MODE_READ);
        trans.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
        trans.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        trans.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        trans.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_READ);
        trans.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_WRITE);

        RenderingAttributes rendering = new RenderingAttributes();
        rendering.setCapability(RenderingAttributes.ALLOW_VISIBLE_READ);
        rendering.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_READ);
        rendering.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_WRITE);
        rendering.setVisible(true);

        ap.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);

        ap.setRenderingAttributes(rendering);
        ap.setTransparencyAttributes(trans);
        ap.setPolygonAttributes(pta);

        return ap;
    }

    /**
     *
     * @return the bow shock shape to be displayed.
     */
    public final MHDSurf getBowshock() {
        return mhdSurfObj;
    }

    /**
     *
     * @return the magnetopause shape to be displayed.
     */
    public final MHDPause getMagnetopause() {
        return mhdPauseObj;
    }

    /**
     *
     * @return the neutral sheet shape to be displayed.
     */
    public final MHDNeutral getNeutralSheet() {
        return mhdNeutralObj;
    }
}
