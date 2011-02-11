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


package org.jini.projects.thor.configuration;

import net.jini.core.event.RemoteEvent;

/**
 * Used internally by <code>DynamicConfigurationImpl</code> to start the dispatching of the event to
 registered listeners.
 * @author calum
 */
public interface ConfigurationEventDelegate {
	/**
	Invoked by the RemoteEventListener, with the Event
	*/
	public void configurationChanged(RemoteEvent evt);
}
