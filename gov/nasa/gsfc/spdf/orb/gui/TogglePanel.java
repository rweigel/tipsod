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
 * $Id: TogglePanel.java,v 1.41 2015/10/30 14:18:50 rchimiak Exp $
 *
 * Created on April 30, 2002, 8:53 AM
 */
package gov.nasa.gsfc.spdf.orb.gui;

import gov.nasa.gsfc.spdf.orb.OrbitViewer;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 * The TogglePanel class handles the check boxes, displays and behaviors to
 * switch the earth, axis, and tick mark on and off.
 *
 * @author rchimiak
 * @version $Revision: 1.41 $
 */
public abstract class TogglePanel extends JPanel {

    //  private OrbitViewer orbitViewer;
    /**
     * Creates a new Check box Panel.
     *
     */
    public TogglePanel(int rows, int columns) {

        setLayout(new GridLayout(rows, columns));

        this.setPreferredSize(new Dimension(getPreferredSize().width, 40));
        this.setBackground(OrbitViewer.BEIGE);

    }
}
