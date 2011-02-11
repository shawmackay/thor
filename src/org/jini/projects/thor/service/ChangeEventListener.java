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
 * ChangeEventListener.java
 *
 * Created on April 15, 2002, 3:49 PM
 */

package org.jini.projects.thor.service;

/**
 *
 * Represents a basic ChangeListener. Allowing the service discover what events the client is interested in<BR>
 *<b>This is an RMI callback to the client</b>. <br><em>Note:</em>The getChangeType() was removed in 1.0b3 due to the need to call back
 *if the listener was simply exported rather than wrapped in a smart proxy, whcih could cause in event dispatching in the event that the
 * client had died and the listener leased had not yet expired
 * @author  calum
 */
public interface ChangeEventListener extends net.jini.core.event.RemoteEventListener {
}
