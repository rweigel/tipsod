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
 * $Id: ContentBranch.java,v 1.41 2017/03/06 20:05:00 rchimiak Exp $
 */
/*
 * ContentBranch.java
 *
 * Created on March 15, 2002, 11:29 AM
 */
package gov.nasa.gsfc.spdf.orb.content;

import com.sun.j3d.utils.image.TextureLoader;
import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import gov.nasa.gsfc.spdf.orb.content.behaviors.MouseHandler;
import gov.nasa.gsfc.spdf.orb.content.shapes.Moon;
import gov.nasa.gsfc.spdf.orb.content.shapes.Sun;
import gov.nasa.gsfc.spdf.orb.gui.ControlPanel;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.utils.EarthSurfaces;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.awt.Toolkit;
import java.util.List;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * The ContentBranch class represents the first node of a Java 3D content
 * branch, linked to a locale. Creates and places the other content elements in
 * the tree.
 *
 * @author rchimiak
 * @version $Revision: 1.41 $
 */
public class ContentBranch extends BranchGroup {

    private static SatBranch satBranch = null;
    private static Background bgNode;

    private static final EarthGroup earthGroup = new EarthGroup();
    private static final EarthSurfaces earthSurfaces = new EarthSurfaces();
    private static final Sun sun = new Sun(9f);
    private static final Moon moon = new Moon(3.8f);
    private boolean picking = false;
    private final float backgroundRed = 0.00f;
    private final float backgroundGreen = 0.00f;
    private final float backgroundBlue = 0.20f;
    private final ImageComponent2D stars = new TextureLoader(Toolkit.getDefaultToolkit().
            getImage(ContentBranch.class.getResource("/images/back.jpg")), null).getImage();

    /**
     * Create a new contentBranch node. It sets required branch group
     * capabilities and sets the background color.
     */
    public ContentBranch() {

        setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        setCapability(BranchGroup.ALLOW_DETACH);

        //     Set up the background Color
        BoundingSphere bounds
                = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                        Double.POSITIVE_INFINITY);

        bgNode = new Background();
        //   bgNode.setImage(stars);

        bgNode.setColor(new Color3f(backgroundRed,
                backgroundGreen, backgroundBlue));
        bgNode.setImageScaleMode(Background.SCALE_FIT_ALL);

        bgNode.setApplicationBounds(bounds);

        addChild(bgNode);
        bgNode.setCapability(Background.ALLOW_COLOR_READ);
        bgNode.setCapability(Background.ALLOW_COLOR_WRITE);
        bgNode.setCapability(Background.ALLOW_IMAGE_READ);
        bgNode.setCapability(Background.ALLOW_IMAGE_WRITE);

        compile();
    }

    public void changeBackground(int i) {

        switch (i) {

            case 0:
                bgNode.setImage(null);
               // bgNode.setColor(new Color3f(backgroundRed,
              //          backgroundGreen, backgroundBlue));
                bgNode.setColor(getBackgroundColor());
                
                break;
            case 1:
                bgNode.setImage(stars);
                break;
            default:
                break;
        }

    }

    /**
     * remove the main branch display node before re-displaying a new selection
     * of satellites orbit.
     */
    public void removeSatBranch() {

        if (satBranch != null) {

            satBranch.detach();

            if (satBranch.zoomExist()) {
                satBranch.removeZoom();
            }

            satBranch.removeSatelliteGraphChangeListener();

            for (BranchGroup bg : MouseHandler.getPickList()) {
                bg.detach();
            }
            MouseHandler.getPickList().clear();

            if (OrbitViewer.getTipsodMenuBar().coordActivated()) {
                OrbitViewer.getTipsodMenuBar().resetCoordinatesActivation();
                picking = true;
            } else {

                picking = false;
            }
            satBranch.clear();

            satBranch = null;
        }
    }

    /**
     * get the main node that deals with all of the earth transformation
     *
     * @return the earth branch group
     */
    public static EarthGroup getEarthGroup() {

        return earthGroup;
    }

    public static Sun getSun() {

        return sun;
    }

    public static Moon getMoon() {

        return moon;
    }

    /**
     * get the surfaces objects (bow shock, magnetopause, and neutral sheet).
     *
     * @return the earth surfaces elements.
     */
    public static EarthSurfaces getEarthSurfaces() {

        return earthSurfaces;
    }

    /**
     * The satellite branch group is removed and added at run time when a new
     * data file is generated.
     *
     * @param location array of SatelliteLocation instances containing location
     * information.
     * @throws java.lang.CloneNotSupportedException
     */
    public void addSatBranch(final List<? extends Object> location) throws CloneNotSupportedException {

        OrbitViewer.getSlider().resetMax();
        OrbitViewer.setPositionWindow(ControlPanel.getCentralBody().ordinal());

        switch (ControlPanel.getCentralBody()) {
            case SUN:
                satBranch = new HelioSatBranch((List<Trajectory>) location);
                break;

            case MOON:
                satBranch = new SelenoSatBranch((List<SatelliteData>) location);
                break;
            default:
                satBranch = new GeoSatBranch((List<SatelliteData>) location);
                break;
        }

        addChild(satBranch);

        if (picking) {

            OrbitViewer.getTipsodMenuBar().resetCoordinatesActivation();
        }
    }

    /**
     * Called when the application wants to use a previous saved file through
     * the import menu function. Such a case could be when the application is
     * run off-line.
     *
     * @param location list of selected satellite data such as coordinates ...
     * @param properties array of selected satellite properties such as names,
     * display names...
     * @param footpoint are footprints part of the scene
     */
    public void addSatBranch(final List<? extends Object> location,
            final SatelliteGraphProperties[] properties,
            final boolean footpoint, ControlPanel.Body body) {

        OrbitViewer.getSlider().resetMax();

        switch (body) {
            case SUN:
                satBranch = new HelioSatBranch((List<Trajectory>) location, properties);
                break;

            case MOON:
                satBranch = new SelenoSatBranch((List<SatelliteData>) location, properties);
                break;
            default:
                satBranch = new GeoSatBranch((List<SatelliteData>) location, properties, footpoint);
                break;
        }

        addChild(satBranch);

        if (picking) {

            OrbitViewer.getTipsodMenuBar().resetCoordinatesActivation();
        }
    }

    /**
     * Returns satBranch the first node of this branch.
     *
     * @return the first node on this branch
     */
    public static SatBranch getSatBranch() {
        return satBranch;
    }

    /**
     * Returns the background node. That access is required during printing to
     * inverse colors.
     *
     * @return the background node
     */
    public static Background getBackground() {
        return bgNode;
    }
    
     /**Returns the background color stored in the background node.
    * @return the background color
    */
    public Color3f getBackgroundColor(){
        Color3f backgroundColor = new Color3f();
        bgNode.getColor(backgroundColor);
        return backgroundColor;
    }
    /**Sets the background color.
     * @param backgroundColor the color used in the background
     */
    public void setBackgroundColor(Color3f backgroundColor){
        
        bgNode.setColor(backgroundColor);
        
    }    

}
