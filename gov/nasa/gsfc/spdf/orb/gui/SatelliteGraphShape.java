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
 * $Id: SatelliteGraphShape.java,v 1.9 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.io.Serializable;

public class SatelliteGraphShape implements Serializable {

    public static SatelliteGraphShape SPHERE
            = new SatelliteGraphShape("Sphere");
    public static SatelliteGraphShape CUBE
            = new SatelliteGraphShape("Cube");
    public static SatelliteGraphShape CONE
            = new SatelliteGraphShape("Cone");
    public static SatelliteGraphShape CYLINDER
            = new SatelliteGraphShape("Cylinder");
    public static SatelliteGraphShape DIAMOND
            = new SatelliteGraphShape("Diamond");

    @Override
    public String toString() {

        return shape;
    }

    public static SatelliteGraphShape getInstance(String value)
            throws IllegalArgumentException {

        if (value.equalsIgnoreCase(SPHERE.toString())) {

            return SPHERE;
        } else if (value.equalsIgnoreCase(CUBE.toString())) {

            return CUBE;
        } else if (value.equalsIgnoreCase(CONE.toString())) {

            return CONE;
        } else if (value.equalsIgnoreCase(CYLINDER.toString())) {

            return CYLINDER;
        } else if (value.equalsIgnoreCase(DIAMOND.toString())) {

            return DIAMOND;
        }

        throw new IllegalArgumentException(value
                + " is not a valid SatelliteGraphShape");
    }
    private final String shape;

    private SatelliteGraphShape(String shape) {

        this.shape = shape;
    }
}
