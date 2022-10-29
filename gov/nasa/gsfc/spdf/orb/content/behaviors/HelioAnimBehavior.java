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
 * $Id: HelioAnimBehavior.java,v 1.2 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import gov.nasa.gsfc.spdf.orb.gui.Slider;
import gov.nasa.gsfc.spdf.orb.utils.HelioUtil;
import java.util.List;
import javax.media.j3d.Alpha;
import javax.media.j3d.TransformGroup;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author rchimiak
 */
public class HelioAnimBehavior extends AnimBehavior {

    public HelioAnimBehavior(final TransformGroup target,
            final Trajectory satLocation, final int j,
            final Alpha alpha, final Slider slider) {

        super(target, satLocation, j, alpha, slider);

    }

    @Override
    protected double[][] getCoordinateData(Object satLocation) {

        return HelioUtil.getCoordinateData(satLocation);
    }

    @Override
    protected List<XMLGregorianCalendar> getTime(Object satLocation) {

        return ((Trajectory) satLocation).getTime();

    }

}
