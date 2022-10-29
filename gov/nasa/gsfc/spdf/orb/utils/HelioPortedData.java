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
 * $Id: HelioPortedData.java,v 1.2 2015/10/30 14:18:51 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.utils;

import gov.nasa.gsfc.spdf.helio.client.Trajectory;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;

import gov.nasa.gsfc.spdf.orb.gui.SatelliteGraphProperties;

/**
 *
 * @author rchimiak
 */
public class HelioPortedData extends PortedData {

    List< Double> latitude;
    List< Double> longitude;
    List< Double> radius;
    gov.nasa.gsfc.spdf.helio.client.CoordinateSystem CoordinateSystem;

    public HelioPortedData(Trajectory trajectory,
            SatelliteGraphProperties properties) {

        super();

        latitude = trajectory.getLatitude();
        longitude = trajectory.getLongitude();
        radius = trajectory.getRadius();

        time = new Calendar[trajectory.getTime().size()];

        for (int i = 0; i < time.length; i++) {

            time[i] = trajectory.getTime().get(i).
                    toGregorianCalendar();
        }

        id = trajectory.getId();

        CoordinateSystem = trajectory.getCoordinateSystem();

        this.properties = properties;

    }

    @Override
    public Trajectory makeSatelliteData() {

        //   Trajectory dat = new Trajectory(id, time, CoordinateSystem,latitude,longitude,radius);
        Trajectory dat = new Trajectory();
        dat.getLatitude().addAll(latitude);
        dat.getLongitude().addAll(longitude);
        dat.getRadius().addAll(radius);

        dat.setId(id);
        dat.setCoordinateSystem(CoordinateSystem);

        DatatypeFactory datatypeFactory;

        try {

            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {

            System.err.println("DatatypeConfigurationExcetpion: "
                    + e.getMessage());
            return null;
        }
        List< XMLGregorianCalendar> satTimes = dat.getTime();
        for (Calendar time1 : time) {
            satTimes.add(datatypeFactory.newXMLGregorianCalendar((GregorianCalendar) time1));
        }

        /*     CoordinateData coor = new CoordinateData();
         Collections.addAll(coor.getX(), X);
         Collections.addAll(coor.getY(), Y);
         Collections.addAll(coor.getZ(), Z);

         dat.getCoordinates().add(coor);

         coor.setCoordinateSystem(
         CoordinateSystem.fromValue(coordinateSystemValue));*/
        return dat;
    }

}
