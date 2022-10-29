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
 * $Id: PortedData.java,v 1.8 2015/10/30 14:18:51 rchimiak Exp $
 * Created on November 30, 2007, 9:14 AM
 */
package gov.nasa.gsfc.spdf.orb.utils;

import java.util.Calendar;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;

/**
 *
 * @author rachimiak
 */
public abstract class PortedData implements java.io.Serializable {

    Calendar[] time;
    String id;
    SatelliteGraphProperties properties = null;
    protected static final long serialVersionUID = -8707599195771912723L;

    public abstract Object makeSatelliteData();

    public SatelliteGraphProperties getProperties() {

        return properties;
    }

}
