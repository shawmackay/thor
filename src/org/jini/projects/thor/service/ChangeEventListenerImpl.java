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
 * ChangeEventHandler.java
 *
 * Created on 28 February 2002, 14:52
 */

package org.jini.projects.thor.service;

import net.jini.core.event.UnknownEventException;

/**
 *
 * @author  calum
 */
public class ChangeEventListenerImpl implements ChangeEventListener ,ChangeConstants{

    public boolean called = false;
    private int changeType = 0;


    /** Creates a new instance of ChangeEventHandler */
    public ChangeEventListenerImpl() throws java.rmi.RemoteException {
        
    }

    
    public void notify(net.jini.core.event.RemoteEvent remoteEvent) throws net.jini.core.event.UnknownEventException, java.rmi.RemoteException {
        ChangeEvent cev;
        if (!(remoteEvent instanceof ChangeEvent))
            throw new UnknownEventException("Unexpected Event Type");
        cev = (ChangeEvent) remoteEvent;
        long seq = cev.getSequenceNumber();
        int change = cev.getChangeType();
        System.out.println("Change Event #" + seq + ": " + change);
        System.out.println("Occured on:\n\t Key:" + cev.getItem().toString() + "\n\t Value: " + (cev.getValue() != null ? cev.getValue().toString(): "<<null>>"));
        called = true;
    }

    
}
