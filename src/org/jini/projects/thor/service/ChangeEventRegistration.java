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
 * thor : org.jini.projects.thor.service
 * 
 * 
 * ChangeEventRegistration.java
 * Created on 31-Dec-2003
 * 
 * ChangeEventRegistration
 *
 */
package org.jini.projects.thor.service;

import net.jini.core.event.RemoteEventListener;

/**
 * @author calum
 */
public interface ChangeEventRegistration extends RemoteEventListener{
    public abstract void notify(net.jini.core.event.RemoteEvent remoteEvent) throws net.jini.core.event.UnknownEventException, java.rmi.RemoteException;
    public abstract int getChangeType() throws java.rmi.RemoteException;
}
