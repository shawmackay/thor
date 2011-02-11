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
 * thor.jini.org : org.jini.projects.thor.service
 * 
 * 
 * ConfigurationEventListenerImpl.java
 * Created on 14-Dec-2004
 * 
 * ConfigurationEventListenerImpl
 *
 */
package org.jini.projects.thor.service;

import java.rmi.RemoteException;

import net.jini.core.event.UnknownEventException;

import org.jini.projects.thor.configuration.ConfigurationChangeEvent;
import org.jini.projects.thor.configuration.ConfigurationEventDelegate;

/**
 * A remote event listener that, upon receiving an event from Thor, passes it on to the registered client listener
 * @author calum
 */
public class ConfigurationEventListenerImpl extends ChangeEventListenerImpl {

	 private ConfigurationEventDelegate listener;
	
	/**
	 * @throws java.rmi.RemoteExceptionvf
	 */
	public ConfigurationEventListenerImpl() throws RemoteException {
		super();
		// TODO Complete constructor stub for ConfigurationEventListenerImpl
	}

	
	public void notify(net.jini.core.event.RemoteEvent remoteEvent) throws net.jini.core.event.UnknownEventException, java.rmi.RemoteException {
        
        if (!(remoteEvent instanceof ConfigurationChangeEvent))
            throw new UnknownEventException("Unexpected Event Type");        
        listener.configurationChanged(remoteEvent);
    }

	public void setConfigurationChangeListener(ConfigurationEventDelegate l){
		listener = l;
	}
   
}
