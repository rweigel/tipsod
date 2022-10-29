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
 * $Id: GraphSpecifiedListener.java,v 1.6 2015/10/30 14:18:50 rchimiak Exp $
 */
package gov.nasa.gsfc.spdf.orb.gui;

import java.util.EventListener;

/**
 * The listener interface for receiving GraphSpecifiedEvent events. The class
 * that is interested in processing a GraphSpecifiedEvent implements this
 * interface, and the object created with that class is registered with a
 * component, using the component's addGraphSpecifiedListener method. When the
 * event occurs, that object's graphSpecified method is invoked.
 *
 * @version $Revision: 1.6 $
 * @author B. Harris
 */
public interface GraphSpecifiedListener extends EventListener {

    /**
     * Invoked when a GraphSpecifiedEvent occurs. The implementation of this
     * method (or any thread that may be created to perform the response to this
     * event) must call event.setProgressFinished() (on the main event dispatch
     * thread) when the response to this event is complete. This was made an
     * explicit step to allow lengthy responses to be performed in another
     * thread. No additional events will be generated until this step is
     * performed.
     *
     * @param event the event which occurred
     */
    void graphSpecified(GraphSpecifiedEvent event);

    void graphCancel();
}
