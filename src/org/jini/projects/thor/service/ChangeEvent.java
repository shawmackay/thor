/*
   Copyright 2006 thor.jini.org Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

/*
 * ChangeEvent.java
 *
 * Created on 28 February 2002, 14:24
 */

package org.jini.projects.thor.service;


import java.rmi.MarshalledObject;

/**
 *  Represents an event that has occured within Thor. In order for clients to be performant
 * Thor hands back to the client this object, which contains both the change type, the name of the item in the event context and
 * the new value. This reduces the need for a client to make another call to Thor in order to find out the updated values or items.
 * @author  calum
 */
public class ChangeEvent extends net.jini.core.event.RemoteEvent implements ChangeConstants {
    //Next two line in a remoteEvent Listener!!!!
    /*
    protected String branch=null;
    protected String item = null;
     **/
    static final long serialVersionUID = -1454416271189190003L;

    private int changeType = 0;
    public final static int ID = 3001;
    private Object item;
    private Object value;


    /** Creates a new instance of ChangeEvent, initialising all the data required to send back to the client */
    public ChangeEvent(Object source, int changeType, long seqno, MarshalledObject key, Object item, Object value) {
        super(source, ID, seqno, key);
        this.changeType = changeType;
        this.item = item;
        this.value = value;
    }


    /**
     * Allows the client to obtain information about the change which occured.
     */
    public int getChangeType() {
        return changeType;
    }

    /**
     * Returns the item name concerned
     */

    public Object getItem() {
        return item;
    }

    /**
     * Returns the current value as held in Thor.
     * if the item in question is a branch addition the value will be the "<<BRANCH>>"
     *if the change is a DELETE event, the value will be null.
     */
    public Object getValue() {
        return value;
    }

}
