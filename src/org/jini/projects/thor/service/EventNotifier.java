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
 * thor.jini.org : org.jini.projects.thor.service
 * 
 * 
 * EventNotifier.java
 * Created on 21-Apr-2004
 * 
 * EventNotifier
 *
 */
package org.jini.projects.thor.service;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;

/**
 * @author calum
 */
public class EventNotifier implements Runnable {
    RemoteEventListener listenerToFire;
    RemoteEvent event;

    public EventNotifier(RemoteEventListener inLis, RemoteEvent inEvt) {
        listenerToFire = inLis;
        event = inEvt;
    }

    /*
     * Performs the callback to the client to handle the event
     */
    public void run() {
        try {
            listenerToFire.notify(event);
        } catch (Exception e) {
            System.out.println(new java.util.Date() + ": Err: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
