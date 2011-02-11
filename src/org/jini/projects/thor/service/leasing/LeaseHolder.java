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
 * thor.jini.org : org.jini.projects.thor.service.leasing
 * 
 * 
 * LeaseHolder.java
 * Created on 21-Apr-2004
 * 
 * LeaseHolder
 *
 */
package org.jini.projects.thor.service.leasing;

import net.jini.core.lease.Lease;
import net.jini.id.Uuid;

/**
 * @author calum
 */
public class LeaseHolder {
    private Lease l;
    private Uuid cookie;
    
    
	/**
	 * @param l
	 * @param cookie
	 */
	public LeaseHolder(Lease l, Uuid cookie) {
		super();
		this.l = l;
		this.cookie = cookie;
	}
	/**
	 * @return Returns the cookie.
	 */
	public Uuid getCookie() {
		return this.cookie;
	}
	/**
	 * @return Returns the l.
	 */
	public Lease getLease() {
		return this.l;
	}
}
