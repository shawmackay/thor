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
 * Created on 16-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jini.projects.thor.configuration;

/**
 * Used to determine whether a thread should be created and used to notify a listener of a change
 * in the configuration. Returning true, the DynamicConfigurationImpl should proceed to start a thread and notify
 * the listener. Returning false, the DynamicConfigurationImpl can assume the listener does nto want to be notified of the particular
 * event, and it can be skipped over.
 * @author Calum
 *
 */
public interface DynamicConfigurationFilter {

	/**
	Determine whether an event is to be accepted for notification
	* @param evt the event as given to the DynamicConfiguration instance
	* @returns whether the filter accept this evt for forward processing
	*/
    public boolean accept(ConfigurationChangeEvent evt);
}
