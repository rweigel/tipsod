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
 * $Id: PositionShape.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.shapes;

import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;
import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphTableModel;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;

/**
 *
 * @author rchimiak
 */
public class PositionShape extends Shape3D {

    protected ArrayList<Long[]> timeList = new ArrayList<Long[]>();
    protected SatelliteGraphTableModel graphModel;
    protected SatelliteGraphProperties sgp = null;
    protected AnimatedShape as = null;
    protected Appearance appearance = new Appearance();

    public PositionShape() {

        super();

        setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    }

    public long[] getTimeBoundaries(int i) {

        long beg = timeList.get(i)[0];
        long end = timeList.get(i)[1];

        return new long[]{beg, end};

    }
}
