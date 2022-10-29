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
 * $Id: T5.java,v 1.8 2015/10/30 14:18:51 rchimiak Exp $
 *
 * Created on October 17, 2002, 12:26 PM
 */
package gov.nasa.gsfc.spdf.orb.utils.Math;

import javax.media.j3d.Transform3D;

/**
 * The T5 class implements the transformation between the GEO and MAG geocentric
 * coordinate systems.
 *
 * @author rchimiak
 * @version $Revision: 1.8 $
 */
public class T5 extends Transform3D {

    /**
     * Creates a new instance of T5.
     */
    public T5() {

        this.rotY(Math.toRadians(349.3));

        Transform3D longiRot = new Transform3D();
        longiRot.rotZ(-Qe.longi);

        this.mul(longiRot);
    }
}
