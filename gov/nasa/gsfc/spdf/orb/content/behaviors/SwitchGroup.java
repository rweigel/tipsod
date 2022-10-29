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
 * $Id: SwitchGroup.java,v 1.9 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on March 21, 2002, 3:29 PM
 */
package gov.nasa.gsfc.spdf.orb.content.behaviors;

//import javax.media.j3d.*;
import java.util.BitSet;
import javax.media.j3d.Switch;

/**
 * The SwitchGroup class implements switching states on and off. Used to make
 * elements of the scenegraph(i.e. earth, grids...)visible or not.
 *
 * @author rchimiak
 * @version $Revision: 1.9 $
 */
public class SwitchGroup extends Switch {

    private final BitSet[] options = {new BitSet(2), new BitSet(2)};

    /**
     * Creates a switch group node with two options
     */
    public SwitchGroup() {

        setCapability(Switch.ALLOW_SWITCH_WRITE);
        setWhichChild(Switch.CHILD_MASK);
        options[0].set(0);
        options[1].set(1);
        setChildMask(options[1]);
    }

    /**
     * Gets the array containing the two possible masks used in the switch
     *
     * @return the possible masks
     */
    public BitSet[] getOptions() {
        return options;
    }
}
