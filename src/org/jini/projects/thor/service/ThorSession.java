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
 *  ThorSession.java
 *
 *  Created on 18 September 2001, 11:33
 */
package org.jini.projects.thor.service;

import java.rmi.RemoteException;

/**
 *  Remote Interface for handling a given session.
 * In future version support will be given to various sessions instead of just
 * the public ROOT, such as user sessions, shared mutual sessions etc
 *@author     calum
 */
public interface ThorSession extends java.rmi.Remote {
    /**
     *  Returns the root of the hierarchy
     *
     *@return                               a remote connection to the root of the
     *      hierarchy
     *@exception  java.rmi.RemoteException  Description of Exception
     *@since
     *@throws  RemoteException              if an error occurs during the call
     */
    public org.jini.projects.thor.handlers.Branch getRoot() throws java.rmi.RemoteException;
}

