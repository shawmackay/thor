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
 * Created on 18-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jini.projects.thor.configuration;

import net.jini.config.Configuration;

/**
 * Interface representing an instance of a Configuration provider that supports
 * Dynamic changes and propagating those changes to the client.
 * @author Calum
 */
public interface DynamicConfiguration extends Configuration {
    /**
     * Add a listener and filter so that you can be informed of changes to the configuration
     * @param listener
     * @param filter
     */
    public abstract void addConfigurationListener(DynamicConfigurationListener listener, DynamicConfigurationFilter filter);
    /**
     * Remove a listener and all filters associated with it.
     * @param toRemove
     */
    public abstract void removeConfigurationListener(DynamicConfigurationListener toRemove);
}
    
