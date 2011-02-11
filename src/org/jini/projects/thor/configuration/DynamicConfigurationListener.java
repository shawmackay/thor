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

import net.jini.config.Configuration;

/**
* Provides a way to hook into the Dynamic configuration and be notified when changes occur.
* <code>notify(...)</code> will only be called if <code>getFilter().accept()</code> returns true.
* This allows the creation of notification threads to be done only where the interested party actaully wants
* to know about this particular event.
* @author Calum
*
*/
public interface DynamicConfigurationListener {

	/**
	* Inform the listener that a change has occured, providing the event indicating the change
	* and the new <code>Configuration</code> instance that includes that change
	*/
	public void notify(Configuration config, ConfigurationChangeEvent evt);
}
