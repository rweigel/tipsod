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
 * $Id: PlanarLabel.java,v 1.11 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on May 1, 2002, 12:13 PM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * The PlanarLabel class implements the labels (XYView, ZYView, XZView) for a
 * description of the planar views
 *
 * @author rchimiak
 * @version $Revision: 1.11 $
 */
public class PlanarLabel extends JLabel {

    /**
     * Creates a new PlanarLabel.
     *
     * @param i which planar view (XYView, ZYView, XZView) is being referenced.
     */
    public PlanarLabel(int i) {

        super();
        String[] textArray = {"xy View", "yz View", "xz View"};
        setText(textArray[i - 1]);
        setBackground(new Color(245, 128, 128));
        setBorder(BorderFactory.createEtchedBorder());
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
