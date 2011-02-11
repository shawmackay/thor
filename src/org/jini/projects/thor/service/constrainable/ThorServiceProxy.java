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

import javax.security.auth.login.LoginContext;

import net.jini.admin.Administrable;
import net.jini.config.Configuration;
import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;
import net.jini.security.TrustVerifier;

import org.jini.projects.thor.service.ThorService;
import org.jini.projects.thor.service.ThorSession;


/**
 * @author calum
 */
public class ThorServiceProxy implements ThorService, Administrable, Serializable, ReferentUuid {
	/* @see net.jini.export.ProxyAccessor#getProxy()
	 */
	public Object getProxy() {
		// TODO Complete method stub for getProxy
		return backend;
	}
    public static final long serialVersionUID=15686868123423L;
    /**
     * 
     */
    
    final ThorService backend;
    final Uuid ID;
    
    public static final ThorService create(ThorService server, Uuid id) {
        if (server instanceof RemoteMethodControl) {            
            return new ThorServiceProxy.ConstrainableProxy(server,  id, null);
        }
        else
            return new ThorServiceProxy(server,  id);
    }

    
    private ThorServiceProxy(ThorService backend, Uuid ID) {
        
        super();
        this.backend = backend;
        this.ID = ID;        
        // URGENT Complete constructor stub for ThorServiceProxy
    }


    /**
     * @return
     * @throws java.rmi.RemoteException
     */
    public Object getAdmin() throws RemoteException {
        return backend.getAdmin();
    }

    /**
     * @return
     * @throws RemoteException
     */
    public ThorSession getSession() throws RemoteException {
        return backend.getSession();
    }

    /* @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return backend.hashCode();
    }
 
    
    /* @see java.lang.Object#toString()
     */
    public String toString() {
        return backend.toString();
    }
   
    
    public 	Configuration config;
	LoginContext loginContext;
public final static class ConstrainableProxy extends ThorServiceProxy implements RemoteMethodControl{
        private static final long serialVersionUID = 4L;
        private ConstrainableProxy(ThorService server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server,methodConstraints), id);
            //l.fine("Creating a secure proxy");
        }
        public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new ThorServiceProxy.ConstrainableProxy(backend, ID,
                    constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


        private static ThorService constrainServer(ThorService server, MethodConstraints methodConstraints)
        {
            return (ThorService)
            ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }


    }
   
	/* @see net.jini.security.proxytrust.ServerProxyTrust#getProxyVerifier()
	 */
	public TrustVerifier getProxyVerifier() throws RemoteException {
		// TODO Complete method stub for getProxyVerifier
		return backend.getProxyVerifier();
	}
	/* @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return ID;
	}
}
