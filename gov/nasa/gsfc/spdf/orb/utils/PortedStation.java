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
 * $Id: PortedStation.java,v 1.6 2015/10/30 14:18:51 rchimiak Exp $
 * Created on December 1, 2007, 4:20 PM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.ssc.client.GroundStationDescription;

/**
 *
 * @author rachimiak
 */
public class PortedStation implements java.io.Serializable {

    String id = null;
    String name = null;
    float latitude = 0;
    float longitude = 0;
    private static final long serialVersionUID = 3229826493071258379L;

    /**
     * Creates a new instance of PortedStation.
     */
    public PortedStation(GroundStationDescription station) {

        id = station.getId();
        name = station.getName();
        latitude = station.getLatitude();
        longitude = station.getLongitude();
    }

    public GroundStationDescription makeGroundStation() {

        GroundStationDescription station = new GroundStationDescription();
        station.setId(id);
        station.setName(name);
        station.setLatitude(latitude);
        station.setLongitude(longitude);
        return station;
    }
}
