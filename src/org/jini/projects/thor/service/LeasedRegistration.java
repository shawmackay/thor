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
 * LeasedRegistration.java
 *
 * Created on 04 March 2002, 11:11
 */

package org.jini.projects.thor.service;

import net.jini.id.Uuid;

import com.sun.jini.landlord.LeasedResource;

/**
 *  Provides an Event Registration with a lease to a client upon successful registration of a Change listener
 * @author  calum
 */
public class LeasedRegistration implements net.jini.core.event.RemoteEventListener, LeasedResource {

    private Uuid myCookie = null;
    private long expiryTime = 0L;
    private String eventDeliveryKey = null;
    private net.jini.core.event.RemoteEventListener rel;
    private EventDelivery evDel;

    /** Creates a new instance of LeasedRegistration */
    public LeasedRegistration(net.jini.core.event.RemoteEventListener rel, String leasePath, int onIndex, long forTime) {
        this.rel = rel;
        eventDeliveryKey = new String(leasePath + ":" + String.valueOf(onIndex));
        myCookie = null;//new EvtCookie(onIndex, leasePath, forTime);
    }

    /** Creates a new instance of LeasedRegistration */

    public LeasedRegistration(net.jini.core.event.RemoteEventListener rel, String eventDeliveryKey, long forTime, EventDelivery evDel, Uuid cookie) {
        this.rel = rel;
        String ev = eventDeliveryKey;
        this.eventDeliveryKey = eventDeliveryKey;
        this.evDel = evDel;
        String acPath = ev.substring(0, ev.lastIndexOf(":"));
        int val = Integer.parseInt(ev.substring(ev.lastIndexOf(":") + 1));
        // System.out.println("Creating cookie of " +val +"," + acPath +" for " + forTime);
        myCookie = cookie;// new EvtCookie(val, acPath, forTime);

    }
    /*
     *Obtains the cookie information relating to the store lease
     */

    public Uuid getCookie() {
        return myCookie;
    }

    /*
     *Gets the expiration time for the client.
     */
    public long getExpiration() {
        return expiryTime;
    }

    /*
     *Used by the server to change the expiration time on the Registration
     */

    public void setExpiration(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    /*
     *Delegates the notification of the client to the client supplied listener
     */
    public void notify(net.jini.core.event.RemoteEvent remoteEvent) throws net.jini.core.event.UnknownEventException, java.rmi.RemoteException {
        System.out.println("going to notify now! " + rel.getClass().getName());
        rel.notify(remoteEvent);
    }

    /* Gets the unique delivery key by which the EventRegistration can be found
     */
    public String getEventDeliveryKey() {
        return this.eventDeliveryKey;
    }

    /* references the instance of the EventDelivery object which holds all the EventRegistration. Used on conjunction with <code>getEventDeliveryKey()</code>
     */
    public EventDelivery getEventDeliverer() {
        return this.evDel;
    }
       

}
