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
 * $Id: AxisRadioButton.java,v 1.10 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on June 28, 2002, 1:08 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.content.shapes.Axis;
import javax.swing.JRadioButtonMenuItem;

/**
 * The AxisRadioButton class implements the radio buttons associated with the XY
 * ZY, and XZ Views. Those radio buttons appear at the bottom of the window on
 * the toggle panel.
 *
 * @author rchimiak
 * @version $Revision: 1.10 $
 */
public class AxisRadioButton extends JRadioButtonMenuItem {

    private final String buttonName;

    /**
     * Creates new radio buttons associated with the 3 planar views.
     *
     * @param title the label associated with the radio button (xy View...)
     * @param selected when set to true causes the button to be selected at the
     * exclusion of the other two
     */
    public AxisRadioButton(String title, boolean selected) {

        super(title, selected);
        buttonName = title;
    }

    /**
     * Returns the axis view corresponding to the selected radio button. Called
     * from the OrbitViewer object to select the center view.
     *
     * @return the selected view
     */
    public int Axis() {
        if (buttonName.equalsIgnoreCase("xy View")) {
            return Axis.zAxis;
        } else if (buttonName.equalsIgnoreCase("yz View")) {
            return Axis.xAxis;
        } else {
            return Axis.yAxis;
        }
    }
}
