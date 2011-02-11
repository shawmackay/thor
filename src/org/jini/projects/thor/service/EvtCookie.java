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
 * EvtCookie.java
 *
 * Created on 01 March 2002, 15:29
 */

package org.jini.projects.thor.service;

/**
 * Lease place holder object for clients that register for change events
 * @author  calum
 */
public class EvtCookie implements java.io.Serializable {

    private long leaseCounter;
    private String leaseKey;


    /** Creates a new instance of EvtCookie */
    public EvtCookie() {
    }

    public EvtCookie(int inIndex, String path, long InCounter) {
        this.leaseCounter = InCounter;
        this.leaseKey = path + ":" + String.valueOf(inIndex);
    }

    public void setLeaseKey(int inIndex, String path) {
        this.leaseKey = path + ":" + String.valueOf(inIndex);
    }

    public void setLeaseCounter(long inCounter) {
        this.leaseCounter = inCounter;
    }

    public String getLeaseKey() {
        return leaseKey;
    }

    public long getLeaseCounter() {
        return leaseCounter;
    }

}
