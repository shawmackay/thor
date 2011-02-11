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
 * AdminProxy.java Created on 22-Dec-2003
 * 
 * AdminProxy
 *  
 */
package org.jini.projects.thor.service.constrainable;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.id.Uuid;

import org.jini.projects.thor.service.ThorAdmin;

/**
 * @author calum
 */
public class AdminProxy implements ThorAdmin, Serializable {
    ThorAdmin backend;
    Uuid ID;

    public static ThorAdmin create(ThorAdmin backend, Uuid ID) {
        if (backend instanceof RemoteMethodControl)
            return new ConstrainableProxy(backend, ID, null);
        else
            return new AdminProxy(backend, ID);
    }
    
    
    final static class ConstrainableProxy extends AdminProxy implements RemoteMethodControl {

        public RemoteMethodControl setConstraints(MethodConstraints constraints) {
            return new AdminProxy.ConstrainableProxy(backend, ID, constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }

        /**
		 * @param server
		 * @param id
		 * @param object
		 */
        public ConstrainableProxy(ThorAdmin server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server, methodConstraints), id);
        }

        private static ThorAdmin constrainServer(ThorAdmin server, MethodConstraints methodConstraints) {
            return (ThorAdmin) ((RemoteMethodControl) server).setConstraints(methodConstraints);
        }

    }
    private static final long serialVersionUID = 1287686345L;

    /**
	 *  
	 */
    private AdminProxy(ThorAdmin px, Uuid id) {
        super();
        this.backend = px;
        this.ID = id;
        // URGENT Complete constructor stub for AdminProxy
    }

    /**
	 * @param attrSets
	 * @throws java.rmi.RemoteException
	 */
    public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
        backend.addLookupAttributes(attrSets);
    }
    /**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
    public void addLookupGroups(String[] groups) throws RemoteException {
        backend.addLookupGroups(groups);
    }
    /**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
    public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
        backend.addLookupLocators(locators);
    }
    /**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
    public void addMemberGroups(String[] groups) throws RemoteException {
        backend.addMemberGroups(groups);
    }
    /**
	 * @throws java.rmi.RemoteException
	 */
    public void destroy() throws RemoteException {
        backend.destroy();
    }
    /*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object obj) {
        return backend.equals(obj);
    }
    /**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
    public Entry[] getLookupAttributes() throws RemoteException {
        return backend.getLookupAttributes();
    }
    /**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
    public String[] getLookupGroups() throws RemoteException {
        return backend.getLookupGroups();
    }
    /**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
    public LookupLocator[] getLookupLocators() throws RemoteException {
        return backend.getLookupLocators();
    }
    /**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
    public String[] getMemberGroups() throws RemoteException {
        return backend.getMemberGroups();
    }
    /**
	 * @return @throws
	 *              java.rmi.RemoteException
	 */
    public int getUnicastPort() throws RemoteException {
        return backend.getUnicastPort();
    }
    /*
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        return backend.hashCode();
    }
    /**
	 * @param attrSetTemplates
	 * @param attrSets
	 * @throws java.rmi.RemoteException
	 */
    public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
        backend.modifyLookupAttributes(attrSetTemplates, attrSets);
    }
    /**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
    public void removeLookupGroups(String[] groups) throws RemoteException {
        backend.removeLookupGroups(groups);
    }
    /**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
    public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
        backend.removeLookupLocators(locators);
    }
    /**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
    public void removeMemberGroups(String[] groups) throws RemoteException {
        backend.removeMemberGroups(groups);
    }
    /**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
    public void setLookupGroups(String[] groups) throws RemoteException {
        backend.setLookupGroups(groups);
    }
    /**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
    public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
        backend.setLookupLocators(locators);
    }
    /**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
    public void setMemberGroups(String[] groups) throws RemoteException {
        backend.setMemberGroups(groups);
    }
    /**
	 * @param port
	 * @throws java.io.IOException
	 * @throws java.rmi.RemoteException
	 */
    public void setUnicastPort(int port) throws IOException, RemoteException {
        backend.setUnicastPort(port);
    }
    /**
	 *  
	 */
  
    /*
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        return backend.toString();
    }
}
