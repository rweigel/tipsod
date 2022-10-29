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
 * $Id: HelioUtil.java,v 1.2 2015/10/30 14:18:51 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rchimiak
 */
public class HelioUtil {

    public static double[][] getCoordinateData(Object satLocation) {

        Trajectory trajectory = Trajectory.class.cast(satLocation);
        List< Double> latitude = trajectory.getLatitude();
        List< Double> longitude = trajectory.getLongitude();
        List< Double> radius = trajectory.getRadius();

        double[][] coords = new double[latitude.size()][3];

        for (int i = 0; i < latitude.size(); i++) {

            double[] c = Footpoint.sphericalToCartesian(new double[]{radius.get(i),
                latitude.get(i),
                longitude.get(i)});

            coords[i][0] = c[0];
            coords[i][1] = c[1];
            coords[i][2] = c[2];
        }
        return coords;
    }

    public static double[][] getCoordinateData(ArrayList<List<Double>> list) {

        List< Double> latitude = list.get(0);
        List< Double> longitude = list.get(1);
        List< Double> radius = list.get(2);

        double[][] coords = new double[latitude.size()][3];

        for (int i = 0; i < latitude.size(); i++) {

            double[] c = Footpoint.sphericalToCartesian(new double[]{radius.get(i),
                latitude.get(i),
                longitude.get(i)});

            coords[i][0] = c[0];
            coords[i][1] = c[1];
            coords[i][2] = c[2];
        }
        return coords;
    }

}
