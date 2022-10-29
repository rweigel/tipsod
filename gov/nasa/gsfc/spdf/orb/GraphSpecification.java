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
 *   http://cdaweb.gsfc.nasa.gov/WebServices/NASA_Open_Source_Agreement_1.3.txt.
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
 * Copyright (c) 2008 - 2009 United States Government as represented by
 * the National Aeronautics and Space Administration. No copyright is
 * claimed in the United States under Title 17, U.S.Code. All Other
 * Rights Reserved.
 *
 * $Id: GraphSpecification.java,v 1.7 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.nasa.gsfc.spdf.ssc.client.CoordinateSystem;

/**
 * This class represents a specification for a graph request.
 *
 * @version $Revision: 1.7 $
 * @author B. Harris
 */
public class GraphSpecification {

    /**
     * Beginning of time range to graph.
     */
    private Date beginTime = null;
    /**
     * End of time range to graph.
     */
    private Date endTime = null;
    /**
     * CoordinateSystem of graph.
     */
    private CoordinateSystem coordinateSystem = null;
    /**
     * Resolution factor of trajectory information on graph.
     */
    private int resolution = -1;
    /**
     * Magnetic (B) Field line trace.
     */
    private boolean bFieldTrace = false;
    /**
     * Flag indicating whether only a single instance of a graph should be
     * displayed at a time.
     */
    private boolean singleInstance = false;
    /**
     * Selected satellite trajectories to graph.
     */
    private final ArrayList<String> sats = new ArrayList<String>();

    /**
     * Create a GraphSpecification object initialized with the selection values
     * specified by the given command line argument values.
     *
     * @param args command line argument values that provide graph selection
     * criteria
     * @return GraphSpecification corresponding to that specified by the given
     * command line arguments
     */
    public static GraphSpecification getInstanceFromArgs(
            String[] args) {

        GraphSpecification spec = new GraphSpecification();
        // resulting GraphSpecification
        final SimpleDateFormat dateFormatter;
        // date argument formatter
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        // dateFormatter.setTimeZone(Util.UTC_TIME_ZONE);

        if (args.length == 1) {

            // A dynamically generated signed JNLP can only pass 
            // a single <argument> (to match the template JNLP).
            args = args[0].split(" ");
        }
        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-b")) {

                if (i + 1 < args.length) {

                    try {

                        spec.beginTime
                                = dateFormatter.parse(args[++i]);
                    } catch (ParseException e) {

                        System.out.println("Invalid beginTime ("
                                + args[i] + ") format: " + e.getMessage());
                    }
                } else {

                    System.out.println("-b (beginTime) value missing");
                }
            } else if (args[i].equals("-e")) {

                if (i + 1 < args.length) {

                    try {

                        spec.endTime = dateFormatter.parse(args[++i]);
                    } catch (ParseException e) {

                        System.out.println("Invalid endTime ("
                                + args[i] + ") format: " + e.getMessage());
                    }
                } else {

                    System.out.println("-e (endTime) value missing");
                }
            } else if (args[i].equals("-c")) {

                if (i + 1 < args.length) {

                    try {

                        spec.coordinateSystem
                                = CoordinateSystem.fromValue(
                                        args[++i].toUpperCase());
                    } catch (IllegalArgumentException e) {

                        System.out.println("Invalid coordinateSystem "
                                + "value (" + args[i] + "): "
                                + e.getMessage());
                    }
                } else {

                    System.out.println(
                            "-c (coordinateSystem) value missing");
                }
            } else if (args[i].equals("-r")) {

                if (i + 1 < args.length) {

                    try {

                        spec.resolution = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {

                        System.out.println("Invalid resolution "
                                + "value (" + args[i] + "): "
                                + e.getMessage());
                    }
                } else {

                    System.out.println("-r (resolution) value missing");
                }
            } else if (args[i].equals("-s")) {

                if (i + 1 < args.length) {

                    spec.sats.add(args[++i]);
                } else {

                    System.out.println("-s (satellite) value missing");
                }
            } else if (args[i].equals("-B")) {

                spec.bFieldTrace = true;
            } else if (args[i].equals("-S")) {

                spec.singleInstance = true;
            } else if (args[i].equals("*")) {

                // ignore
            } else {

                System.out.println("Unrecognized option " + args[i]);
            }
        }

        return spec;
    }

    /**
     * Gets the beginning of the time range.
     *
     * @return beginning of time range, null if not set
     */
    public final Date getBeginTime() {

        Date beginDate = null;

        if (beginTime != null) {

            beginDate = (Date) beginTime.clone();
        }

        return beginDate;
    }

    /**
     * Gets the ending of the time range.
     *
     * @return ending of time range, null if not set
     */
    public final Date getEndTime() {

        Date endDate = null;

        if (endTime != null) {

            endDate = (Date) endTime.clone();
        }

        return endDate;
    }

    /**
     * Gets the CoordinateSystem value.
     *
     * @return coordinateSystem value, null if not set
     */
    public final CoordinateSystem getCoordinateSystem() {

        return coordinateSystem;
    }

    /**
     * Gets the resolution value.
     *
     * @return resolution value, -1 if not set
     */
    public final int getResolution() {

        return resolution;
    }

    /**
     * Gets the satellites.
     *
     * <p>
     * This method returns a reference to the live list, not a snapshot.
     * Therefore any modification you make to the returned list will be present
     * inside this object.
     *
     * @return satellites, empty list if no satellites
     */
    public final List<String> getSatellites() {

        return sats;
    }

    /**
     * Gets the B-Field trace value.
     *
     * @return B-Field trace value
     */
    public final boolean getBFieldTrace() {

        return bFieldTrace;
    }

    /**
     * Gets the singlInstance value.
     *
     * @return singleInstance value
     */
    public final boolean getSingleInstance() {

        return singleInstance;
    }
}
