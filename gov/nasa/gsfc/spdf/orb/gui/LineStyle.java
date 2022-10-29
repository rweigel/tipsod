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
 * $Id: LineStyle.java,v 1.9 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

/**
 * This class represents varies styles of lines. It's an implementation of the
 * "typesafe enum" pattern or idiom.
 *
 * @version $Revision: 1.9 $
 * @author B. Harris
 */
public class LineStyle implements java.io.Serializable {

    /**
     * Value representing a solid line.
     */
    public static final LineStyle SOLID = new LineStyle("Solid");
    /**
     * Value representing a solid line.
     */
    public static final LineStyle DASH = new LineStyle("dash");
    /**
     * Value representing a dotted line.
     */
    public static final LineStyle DOT = new LineStyle("dot");
    /**
     * Value representing a mixed line.
     */
    public static final LineStyle DASH_DOT = new LineStyle("dash_dot");
    /**
     * Value representing a mixed line.
     */
    public static final LineStyle DASH_DOT_DOT_DOT = new LineStyle("dash_dot_dot_dot");

    /**
     * Provides the string representation of the LineStyle.
     *
     * @returns the string representation of the LineStyle
     */
    @Override
    public String toString() {

        return style;
    }

    /**
     * Provides a LineStyle instance of the style identified by the given value.
     *
     * @param value line style identifier
     * @returns an instance of the LineStyle identified by the given value
     * @throws java.lang.IllegalArgumentException if the given value doesn't
     * identify a valid LineStyle
     */
    public static LineStyle getInstance(String value)
            throws IllegalArgumentException {

        if (value.equalsIgnoreCase(SOLID.toString())) {

            return SOLID;
        } else if (value.equalsIgnoreCase(DASH.toString())) {

            return DASH;
        } else if (value.equalsIgnoreCase(DOT.toString())) {

            return DOT;
        } else if (value.equalsIgnoreCase(DASH_DOT.toString())) {

            return DASH_DOT;
        } else if (value.equalsIgnoreCase(DASH_DOT_DOT_DOT.toString())) {

            return DASH_DOT_DOT_DOT;
        }

        throw new IllegalArgumentException(value
                + " is not a valid LineStyle");
    }
    /**
     * LineStyle value.
     */
    private final String style;

    /**
     * Constructs a LineStyle object of the specified style.
     *
     * @param style style of line
     */
    private LineStyle(String style) {

        this.style = style;
    }
}
