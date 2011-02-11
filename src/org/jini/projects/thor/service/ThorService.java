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
 *  ThorService.java
 *
 *  Created on 17 September 2001, 13:19
 */
package org.jini.projects.thor.service;

import java.rmi.RemoteException;

import net.jini.security.proxytrust.ServerProxyTrust;

/**
 *  Remote interface for initially looking up an instance of Thor
 *
 *@author     calum
 *@created    05 March 2002
 *@version    1
 */
public interface ThorService extends java.rmi.Remote, net.jini.admin.Administrable, ServerProxyTrust{

    /**
     *  Obtains a session object to the public root.<br>
     * This session is effectively a link to the public Thor tree.
     * All users can see this tree although in future versions portions of the tree could
     * be restricted
     *
     *@return                   An active session with which to interact with the
     *      Hierarchy
     *@since 1.0b
     *@throws  RemoteException  if an error happens during the call
     */
    public ThorSession getSession() throws RemoteException;


   
}

