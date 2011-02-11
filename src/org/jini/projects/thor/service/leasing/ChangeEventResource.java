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
 * thor : org.jini.projects.org.jini.projects.thor.service.leasing
 * 
 * 
 * ChangeEventResource.java
 * Created on 22-Dec-2003
 * 
 * ChangeEventResource
 *
 */
package org.jini.projects.thor.service.leasing;

import java.rmi.RemoteException;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.id.Uuid;

import com.sun.jini.landlord.LeasedResource;

/**
 * @author calum
 */
public class ChangeEventResource implements RemoteEventListener,LeasedResource {
    
    private Uuid cookie;
    private RemoteEventListener resource;
    private long expiryTime = 0L;
    /**
     * 
     */
    public ChangeEventResource(RemoteEventListener resource, Uuid cookie) {
        this.resource = resource;
        this.cookie = cookie;
        // URGENT Complete constructor stub for ChangeEventResource
    }

    /* @see com.sun.jini.landlord.LeasedResource#setExpiration(long)
     */
    public void setExpiration(long newExpiration) {
        // TODO Complete method stub for setExpiration
        expiryTime = newExpiration;
        
    }

    /* @see com.sun.jini.landlord.LeasedResource#getExpiration()
     */
    public long getExpiration() {
        // TODO Complete method stub for getExpiration
        return expiryTime;
    }

    /* @see com.sun.jini.landlord.LeasedResource#getCookie()
     */
    public Uuid getCookie() {
        // TODO Complete method stub for getCookie
        return cookie;
    }

    /* @see net.jini.core.event.RemoteEventListener#notify(net.jini.core.event.RemoteEvent)
     */
    public void notify(RemoteEvent theEvent) throws UnknownEventException, RemoteException {
        // TODO Complete method stub for notify
        resource.notify(theEvent);
    }
}
