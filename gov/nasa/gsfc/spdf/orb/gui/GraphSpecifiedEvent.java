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
 * $Id: GraphSpecifiedEvent.java,v 1.13 2016/11/16 15:39:24 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.util.EventObject;
import java.util.Date;

import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;

/**
 * This class represents the event associated with the completion of a graph
 * specification.
 *
 * @version $Revision: 1.13 $
 * @author B. Harris
 */
public class GraphSpecifiedEvent extends EventObject {

    /**
     * The specified satellite graph properties.
     */
    private final SatelliteGraphProperties[] satGraphProperties;
    /**
     * The specified begining date.
     */
    private final Date begin;
    /**
     * The specified ending date.
     */
    private final Date end;
    /**
     * The specified resolution.
     */
    private final int resolution;
    /**
     * The specified coordinate system.
     */
    private final Object coordSystem;

    private final boolean tracing;

    /**
     * Construct a GraphSpecifiedEvent.
     *
     * @param source the source of the event
     * @param satGraphProps the specified satellite graph properties
     * @param beginDate the specified begining date
     * @param endDate the specified ending date
     * @param resolution the specified resolution
     * @param coordSystem the specified coordinate system
     */
    public GraphSpecifiedEvent(Object source,
            final SatelliteGraphProperties[] satGraphProps,
            Date beginDate, Date endDate, int resolution,
            CoordinateSystem coordSystem,
            boolean tracing) {

        super(source);

        this.satGraphProperties = satGraphProps;
        begin = beginDate;
        end = endDate;
        this.resolution = resolution;
        this.coordSystem = coordSystem;
        this.tracing = tracing;
    }

    public GraphSpecifiedEvent(Object source,
            final SatelliteGraphProperties[] satGraphProps,
            Date beginDate, Date endDate, int resolution,
            gov.nasa.gsfc.spdf.helio.client.CoordinateSystem coordSystem,
            boolean tracing) {

        super(source);

        this.satGraphProperties = satGraphProps;
        begin = beginDate;
        end = endDate;
        this.resolution = resolution;
        this.coordSystem = coordSystem;
        this.tracing = tracing;
    }

    /**
     * Provides the specified SatelliteGraphProperties.
     *
     * @return the specified SatelliteGraphProperties
     * @throws java.lang.CloneNotSupportedException
     */
    public SatelliteGraphProperties[] getSatelliteGraphProperties() throws CloneNotSupportedException {

        SatelliteGraphProperties[] properties
                = new SatelliteGraphProperties[satGraphProperties.length];
        // a copy of the properties to return

        for (int i = 0; i < properties.length; i++) {

            properties[i] = (SatelliteGraphProperties) satGraphProperties[i].clone();
        }

        return properties;
    }

    /**
     * Provides the specified begining date.
     *
     * @return the specified begining date
     */
    public Date getBeginDate() {

        return (Date) begin.clone();
    }

    /**
     * Provides the specified ending date.
     *
     * @return the specified ending date
     */
    public Date getEndDate() {

        return (Date) end.clone();
    }

    /**
     * Provides the specified resolution.
     *
     * @return the specified resolution
     */
    public int getResolution() {

        return resolution;
    }

    /**
     * Provides the specified CoordinateSystem.
     *
     * @return the specified CoordinateSystem
     */
  //  public CoordinateSystem getCoordinateSystem() {
    //      return coordSystem;
    //  }
    public Object getCoordinateSystem() {

        return coordSystem;
    }

    public boolean getTracing() {

        return tracing;
    }

    /**
     * Provides the specified satellite names.
     *
     * @return the specified satellite names
     */
    public String[] getSelectedSatelliteNames() {

        String[] names = new String[satGraphProperties.length];
        // selected names that are to be
        //  returned

        for (int i = 0; i < names.length; i++) {

            names[i] = satGraphProperties[i].getName();
          
        }

        return names;
    }

    /**
     * Provides the selected satellite display names.
     *
     * @return the selected satellite display names
     */
    public String[] getSelectedSatelliteDisplayNames() {

        String[] names = new String[satGraphProperties.length];
        // selected display names that are
        //  to be returned

        for (int i = 0; i < names.length; i++) {

            names[i] = satGraphProperties[i].getDisplayName();
           
        }

        return names;
    }

    /**
     * Provides the selected satellites.
     *
     * @return the selected satellites.
     */
    public String[][] getSelectedSatellites() {

        String[][] satellites = new String[satGraphProperties.length][2];
        // selected satellites that are
        //  to be returned

        for (int i = 0; i < satellites.length; i++) {

            satellites[i][0] = satGraphProperties[i].getName();
            satellites[i][1] = satGraphProperties[i].getDisplayName();
        }

        return satellites;
    }

    /**
     * Sets the additional note that is displayed along with the progress bar.
     *
     * @param value a string specifying the note to display
     */
    public void setProgressNote(String value) {

        ((SelectionWindow) getSource()).setProgressNote(value);
    }

    /**
     * Sets the indeterminate property of the progress bar, which determines
     * whether the progress bar is in determinate or indeterminate mode. By
     * default, the progress bar is determinate.
     *
     * @param newValue true if the progress bar should change to indeterminate
     * mode; false if it should revert to normal.
     */
    public void setProgressIndeterminate(boolean newValue) {

        ((SelectionWindow) getSource()).setProgressIndeterminate(newValue);
    }

    /**
     * Sets the progress bar's maximum value.
     *
     * @param value the new maximum
     */
    public void setProgressMaximum(int value) {

        ((SelectionWindow) getSource()).setProgressMaximum(value);
    }

    /**
     * Sets the progress bar's minimum value.
     *
     * @param value the new minimum
     */
    public void setProgressMinimum(int value) {

        ((SelectionWindow) getSource()).setProgressMinimum(value);
    }

    /**
     * Sets the progress bar's current value.
     *
     * @param value the new value
     */
    public void setProgressValue(int value) {

        ((SelectionWindow) getSource()).setProgressValue(value);
    }

    /**
     * Sets the progress bar's StringPainted value.
     *
     * @param value true if the progress bar should render a string
     */
    public void setProgressStringPainted(boolean value) {

        ((SelectionWindow) getSource()).setProgressStringPainted(value);
    }

    /**
     * Signals the completion of the response to this event back to the source
     * of the event. This method should be called from the main event dispatch
     * thread.
     */
    public void setProgressFinished() {

        ((SelectionWindow) getSource()).setProgressFinished();
    }
}
