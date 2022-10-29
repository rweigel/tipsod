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
 * $Id: GeoUtil.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.ssc.client.CoordinateData;
import gov.nasa.gsfc.spdf.ssc.client.SatelliteData;
import java.util.List;

/**
 *
 * @author rchimiak
 */
public class GeoUtil {

    public static double[][] getCoordinateData(Object satLocation) {

        List< CoordinateData> data
                = SatelliteData.class.cast(satLocation).getCoordinates();
        List< Double> x = data.get(0).getX();
        List< Double> y = data.get(0).getY();
        List< Double> z = data.get(0).getZ();

        double[][] coords = new double[x.size()][3];

        for (int i = 0; i < x.size(); i++) {

            coords[i][0] = PhysicalConstants.kmToRe(x.get(i));
            coords[i][1] = PhysicalConstants.kmToRe(y.get(i));
            coords[i][2] = PhysicalConstants.kmToRe(z.get(i));
        }
        return coords;
    }

}
