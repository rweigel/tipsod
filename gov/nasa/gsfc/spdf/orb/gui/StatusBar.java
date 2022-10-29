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
 * $Id: StatusBar.java,v 1.10 2015/10/30 14:18:50 rchimiak Exp $
 * Created on September 5, 2007, 3:51 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.media.j3d.Node;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickIntersection;
import gov.nasa.gsfc.spdf.orb.OrbitViewer;

import gov.nasa.gsfc.spdf.orb.content.shapes.OrbitShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.AnimatedShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.FootpointShape;
import gov.nasa.gsfc.spdf.orb.content.shapes.PositionShape;
import gov.nasa.gsfc.spdf.orb.utils.PhysicalConstants;
import gov.nasa.gsfc.spdf.orb.utils.Util;
import gov.nasa.gsfc.spdf.orb.utils.Footpoint;

/**
 *
 * @author rachimiak
 */
public class StatusBar extends JPanel {

    private final JLabel locationLabel = new JLabel(" Location Info:     ");
    private final JLabel nameDisplay = new JLabel("");
    private final JLabel timeDisplay = new JLabel("");
    private final JLabel xDisplay = new JLabel("");
    private final JLabel yDisplay = new JLabel("");
    private final JLabel zDisplay = new JLabel("");
    private final JLabel latDisplay = new JLabel("");
    private final JLabel longitDisplay = new JLabel("");
    private final Color back = this.getBackground();

    public StatusBar() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        locationLabel.setFont(Util.labelFont);
        add(locationLabel);

        nameDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        xDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        yDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        zDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        latDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        longitDisplay.setHorizontalAlignment(SwingConstants.CENTER);

        this.add(nameDisplay);
        this.add(timeDisplay);
        this.add(xDisplay);
        this.add(yDisplay);
        this.add(zDisplay);
        this.add(latDisplay);
        this.add(longitDisplay);

        nameDisplay.setFont(Util.modelFont);
        timeDisplay.setFont(Util.modelFont);
        xDisplay.setFont(Util.modelFont);
        yDisplay.setFont(Util.modelFont);
        zDisplay.setFont(Util.modelFont);
        latDisplay.setFont(Util.modelFont);
        longitDisplay.setFont(Util.modelFont);
    }

    public void handleCursorPositionChange(final PickResult[] result) {
        if (result == null) {

            locationLabel.setText(" Location Info:     ");
            nameDisplay.setText("");
            timeDisplay.setText("");
            xDisplay.setText("");
            yDisplay.setText("");
            zDisplay.setText("");
            latDisplay.setText("");
            longitDisplay.setText("");
            setBackground(back);
        } else {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(Util.UTC_TIME_ZONE);

            PickIntersection pi = result[0].getIntersection(0);
            Node node = result[0].getObject();
            if (node instanceof AnimatedShape) {

                if (result.length == 1 || result[1].getObject() instanceof AnimatedShape) {
                    node = null;
                    nameDisplay.setText("");
                    timeDisplay.setText("");
                    xDisplay.setText("");
                    yDisplay.setText("");
                    zDisplay.setText("");
                    latDisplay.setText("");
                    longitDisplay.setText("");
                } else {
                    node = result[1].getObject();
                    pi = result[1].getIntersection(0);
                }
            }
            if (node instanceof PositionShape) {

                final int decimalPlaces = 3;

                setBackground(Color.white);

                Point3d p = pi.getPointCoordinates();

                long time = Util.interpolateOrbitTime((PositionShape) node, pi);
                String stime = df.format(new Date(time));

                timeDisplay.setText("   " + stime);

                if (node instanceof OrbitShape) {

                    String x = ("X = " + (ControlPanel.isSolenocentric() ? Util.round((p.x) / PhysicalConstants.MOON_TO_EARTH_RADIUS, decimalPlaces) : Util.round(p.x, decimalPlaces)));

                    String y = ("Y = " + (ControlPanel.isSolenocentric() ? Util.round((p.y) / PhysicalConstants.MOON_TO_EARTH_RADIUS, decimalPlaces) : Util.round(p.y, decimalPlaces)));

                    String z = ("Z = " + (ControlPanel.isSolenocentric() ? Util.round((p.z) / PhysicalConstants.MOON_TO_EARTH_RADIUS, decimalPlaces) : Util.round(p.z, decimalPlaces)));

                    if (OrbitViewer.isConnected()) {
                        switch (ControlPanel.getCentralBody()) {

                            case MOON:

                                locationLabel.setText(" Location Info (RM):    ");
                                break;

                            case SUN:

                                locationLabel.setText(" Location Info (au):    ");
                                break;

                            default:

                                locationLabel.setText(" Location Info (RE):    ");
                        }
                    }

                    nameDisplay.setText("   " + ((OrbitShape) node).getName());

                    xDisplay.setText("   " + x);
                    yDisplay.setText("   " + y);
                    zDisplay.setText("   " + z);
                } else if (node instanceof FootpointShape) {

                    double[] cart = {p.x, p.y, p.z};
                    double[] spherical = Footpoint.CartesianToSpherical(cart);

                    String la = ("Latitude = " + Util.round(spherical[1], decimalPlaces) + "\u00B0");
                    String lo = ("Longitude = " + Util.round(spherical[2], decimalPlaces) + "\u00B0");

                    nameDisplay.setText("   " + ((FootpointShape) node).getName() + "  footpoints");

                    latDisplay.setText("   " + la);
                    longitDisplay.setText("   " + lo);
                }
            }
        }
    }
}
