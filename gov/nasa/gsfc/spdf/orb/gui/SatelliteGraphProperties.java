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
 * $Id: SatelliteGraphProperties.java,v 1.19 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Color;
import java.io.Serializable;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class represents a satellite's graph properties.
 *
 * @version $Revision: 1.19 $
 * @author B. Harris
 */
public class SatelliteGraphProperties
        implements Cloneable, Comparable< SatelliteGraphProperties>, Serializable {

    /**
     * The satellite's name.
     */
    private String name = null;
    /**
     * The satellite's display name.
     */
    private String displayName = null;
    /**
     * The satellite's available time.
     */
    private String tooltip = null;
    /**
     * The color used to graph this satellite.
     */
    private Color color;
    /**
     * The shape of the object representing this satellite on a graph.
     */
    private SatelliteGraphShape shape;
    /**
     * The style of line used to depict this satellite's tragectory.
     */
    private LineStyle style;

    private XMLGregorianCalendar startTime;

    private XMLGregorianCalendar stopTime;

    private Boolean selected = false;

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean isSelected() {
        return selected;
    }

    /**
     * Constructs a SatelliteGraphProperties object with the given attribute
     * values.
     *
     * @param name satellite name
     * @param displayName satellite display name
     * @param tooltip satellite available time
     * @param startTime
     * @param stopTime
     * @param color satellite color
     * @param shape satellite shape
     * @param style line style
     */
    public SatelliteGraphProperties(String name, String displayName,
            String tooltip,
            XMLGregorianCalendar startTime,
            XMLGregorianCalendar stopTime,
            Color color,
            SatelliteGraphShape shape,
            LineStyle style) {
        this.name = name;
        this.displayName = displayName;
        this.tooltip = tooltip;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.color = color;
        this.shape = shape;
        this.style = style;

    }

    /**
     * Copy Constructor of SatelliteGraphProperties object.
     *
     * @param satGraphProperties object whose property values are to be copied
     */
    public SatelliteGraphProperties(
            SatelliteGraphProperties satGraphProperties) {

        this(satGraphProperties.getName(),
                satGraphProperties.getDisplayName(),
                satGraphProperties.getTooltip(),
                satGraphProperties.getStartTime(),
                satGraphProperties.getStopTime(),
                satGraphProperties.getColor(),
                satGraphProperties.getShape(),
                satGraphProperties.getLineStyle());
        satGraphProperties.isSelected();

    }

    /**
     * Constructs a SatelliteGraphProperties object for the given satellite with
     * default property values.
     *
     * @param name satellite name
     * @param displayName satellite display name
     * @param tooltip satellite tooltip ( name and available time range)
     * @param startTime satellite start availability at startTime
     * @param stopTime satellite stops availability at stopTime
     */
    public SatelliteGraphProperties(String name, String displayName, String tooltip,
            XMLGregorianCalendar startTime, XMLGregorianCalendar stopTime) {

        this(name, displayName, tooltip, startTime, stopTime, new Color(204, 204, 255), SatelliteGraphShape.SPHERE,
                LineStyle.SOLID);
    }

    /**
     * Constructs a SatelliteGraphProperties object for the given satellite with
     * default property values.
     *
     * @param name satellite name
     */
    public SatelliteGraphProperties(String name) {

        this(name, name, "", null, null);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        try {

            // all of the properties are immutable objects so
            return (SatelliteGraphProperties) super.clone();
        } catch (CloneNotSupportedException e) {

            // can't happen since we are clonable
            return null;
        }
    }

    @Override
    public int compareTo(SatelliteGraphProperties o) {

        return displayName.compareTo(o.getDisplayName());
    }

    /**
     * Provides the satellite's name.
     *
     * @return name of satellite
     */
    public String getName() {

        return name;
    }

    /**
     * Sets the satellite's name.
     *
     * @param name name of satellite
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * Provides the satellite's display name.
     *
     * @return display name of satellite
     */
    public String getDisplayName() {

        return displayName;
    }

    /**
     * Sets the satellite's display name.
     *
     * @param name display name of satellite
     */
    public void setDisplayName(String name) {

        this.displayName = name;
    }

    /**
     * Provides the satellite's available time.
     *
     * @return available time of satellite
     */
    public String getTooltip() {

        return tooltip;
    }

    /**
     * Sets the satellite's available time.
     *
     * @param tooltip
     */
    public void setTooltip(String tooltip) {

        this.tooltip = tooltip;
    }

    /**
     * Provides the value of the color property.
     *
     * @return value of color property
     */
    public Color getColor() {

        return color;
    }

    /**
     * Sets the value of the color property.
     *
     * @param color value of color property
     */
    public void setColor(Color color) {

        this.color = color;
    }

    /**
     * Provides the value of the shape property.
     *
     * @return value of shape property
     */
    public SatelliteGraphShape getShape() {

        return shape;
    }

    /**
     * Sets the value of the shape property.
     *
     * @param shape value of shape property
     */
    public void setShape(SatelliteGraphShape shape) {

        this.shape = shape;
    }

    /**
     * Provides the value of the line style property.
     *
     * @return value of line style property
     */
    public LineStyle getLineStyle() {

        return style;
    }

    /**
     * Sets the value of the line style property.
     *
     * @param style value of line style property
     */
    public void setLineStyle(LineStyle style) {

        this.style = style;
    }

    /**
     * get the time at which the spacecraft start being available
     *
     * @return
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * get the time at which the spacecraft stop being available
     *
     * @return
     */
    public XMLGregorianCalendar getStopTime() {
        return stopTime;
    }

    /**
     * set the time at which the spacecraft start being available
     *
     * @param startTime
     */
    public void setStartTime(XMLGregorianCalendar startTime) {
        this.startTime = startTime;
    }

    /**
     * set the time at which the spacecraft stop being available
     *
     * @param stopTime
     */
    public void setStopTime(XMLGregorianCalendar stopTime) {
        this.stopTime = stopTime;
    }

}
