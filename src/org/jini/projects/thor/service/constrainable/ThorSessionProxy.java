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
 * thor : org.jini.projects.org.jini.projects.thor.service.constrainable
 * 
 * 
 * ThorServiceProxy.java
 * Created on 22-Dec-2003
 * 
 * ThorServiceProxy
 *
 */
package org.jini.projects.thor.service.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.Uuid;

import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ThorSession;

/**
 * @author calum
 */
public class ThorSessionProxy implements ThorSession, Serializable {
    public static final long serialVersionUID=15686868123423L;
    /**
     * 
     */
    
    final ThorSession backend;
    final Uuid ID;
    
    public static final ThorSession create(ThorSession server, Uuid id) {
        if (server instanceof RemoteMethodControl) {            
            return new ThorSessionProxy.ConstrainableProxy(server,  id, null);
        }
        else
            return new ThorSessionProxy(server,  id);
    }

    
    private ThorSessionProxy(ThorSession backend, Uuid ID) {
        
        super();
        this.backend = backend;
        this.ID = ID;        
        // URGENT Complete constructor stub for ThorServiceProxy
    }


   
    
    final static class ConstrainableProxy extends ThorSessionProxy implements RemoteMethodControl{
        private static final long serialVersionUID = 4L;
        private ConstrainableProxy(ThorSession server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server,methodConstraints), id);
            //l.fine("Creating a secure proxy");
        }
        public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new ThorSessionProxy.ConstrainableProxy(backend, ID,
                    constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


        private static ThorSession constrainServer(ThorSession server, MethodConstraints methodConstraints)
        {
            return (ThorSession)
            ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }




    }
    /* @see org.jini.projects.thor.service.ThorSession#getRoot()
     */
    public Branch getRoot() throws RemoteException {
        // TODO Complete method stub for getRoot
        return backend.getRoot();
    }
}
