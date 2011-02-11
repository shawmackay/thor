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
 * EventDelivery.java
 *
 * Created on 28 February 2002, 14:56
 */

package org.jini.projects.thor.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;

import org.jini.projects.thor.handlers.HierarchyHandler;

/**
 * Handles the repsonsibility of firing events to clients. Delegated to by all
 * <i>change-aware </i> classes implementing <code>HierarchyHandler</code>
 * This is not recursive, events logged on a higher branch will not be fiored
 * for changes on anf of it's sub-branches.
 * 
 * @see org.jini.projects.thor.handlers.HierarchyHandler
 * @author calum
 */
public class EventDelivery {

    private HashMap listeners = null;

    private HashMap ids = null;

    public static long evtSeqNum = 1L;

    private int hashmapKey = 0;

    Logger log = Logger.getLogger("org.jini.projects.thor.service");

    /** Creates a new instance of EventDelivery */
    private HierarchyHandler handle;

    public EventDelivery(HierarchyHandler handle) {
        this.handle = handle;
    }

    // public void addChangeListenerFor(String path, String NOOP) {
    //
    // if (listeners == null)
    // listeners = new HashMap();
    // if (listeners.get(path) == null) {
    // HashMap arr = new HashMap();
    // arr.put(new Integer(hashmapKey++), NOOP);
    // listeners.put(path, arr);
    // } else {
    // HashMap arr = (HashMap) listeners.get(path);
    // arr.put(new Integer(hashmapKey++), NOOP);
    // }
    // }

    /**
     * Adds a change listener for a client to the managed set if the path is
     * null or "" it will add it to the branch, i.e. changes to any items on
     * this branch level will be linked on here
     */

    public String addChangeListenerFor(String path, RemoteEventListener rel, Uuid reference) {

        if (listeners == null) {
            listeners = new HashMap();
            ids = new HashMap();
        }
        if (path == null) {
            path = "<default>";
        }
        if (path.equals(""))
            path = "<default>";

        // todo:Builds up the set of marshalled objects for recovery purposes

        if (listeners.get(path) == null) {
            HashMap arr = new HashMap();
            arr.put(new Integer(hashmapKey), rel);
            listeners.put(path, arr);
        } else {
            HashMap arr = (HashMap) listeners.get(path);
            arr.put(new Integer(hashmapKey), rel);
        }
        String evDelKey = new String(path + ":" + String.valueOf(hashmapKey));
        hashmapKey++;
        return evDelKey;
    }

    /**
     * Fires all listeners on this branch given certain criteria.
     */
    public void fireIfListenersOn(String path, Object key, Object value, int ChangeType) {
        try {
            if (listeners != null) {

                if (listeners.get(path) != null) {

                    Object obj = listeners.get(path);

                    HashMap map = (HashMap) listeners.get(path);
                    Iterator iter = map.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry ent = (Map.Entry) iter.next();
                        fireRemoteEvent((RemoteEventListener) ent.getValue(), handle, ChangeType, key, value);

                    }
                }

            }
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    /*
     * Actually fires the event. Instantiates a new Thread to handle the
     * communication to the client
     */

    private void fireRemoteEvent(RemoteEventListener listen, HierarchyHandler hier, int ChangeType, Object item, Object value) {

        ChangeEvent cha = null;
        // Point to a branch!!!!!
        try {
            cha = new ChangeEvent("Hello", ChangeType, evtSeqNum++, null, item, value);
            new Thread(new EventNotifier(listen, cha)).start();
        } catch (Exception ex) {
            log.severe(ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Removes a change event with the given id from the managed set. <br>
     * Mainly used in the lease management process
     */
    public void removeChangeEvent(String name, int id) {
        if (name == null)
            name = "<default>";
        if (name.equals(""))
            name = "<default>";
        if (listeners != null) {
            try {
                /*
                 * System.out.println("Name: " + name + " : ID: " + id);
                 * System.out.println("Getting the list of listeners");
                 */
                HashMap map = (HashMap) listeners.get(name);
                // System.out.println("removing the key");
                map.remove(new Integer(id));
            } catch (Exception ex) {
                log.severe("Err: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Obtains a reference to a given listener on a given item
     */

    public RemoteEventListener getListenerFor(String name, int id) {
        if (listeners != null) {
            HashMap map = (HashMap) listeners.get(name);
            return (RemoteEventListener) map.get(new Integer(id));
        }
        return null;
    }
}
