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
import java.util.HashMap;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.event.EventRegistration;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.id.Uuid;

import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ChangeEventListener;

/**
 * @author calum
 */
public class ClientHandlerProxy implements Branch, Serializable {
    public static final long serialVersionUID=15686868123423L;
    /**
     * 
     */
    
    final Branch backend;
    final Uuid ID;
    
    public static final Branch create(Branch server, Uuid id) {
        if (server instanceof RemoteMethodControl) {            
            return new ClientHandlerProxy.ConstrainableProxy(server,  id, null);
        }
        else
            return new ClientHandlerProxy(server,  id);
    }

    
    private ClientHandlerProxy(Branch backend, Uuid ID) {
        
        super();
        this.backend = backend;
        this.ID = ID;        
        // URGENT Complete constructor stub for ThorServiceProxy
    }


   
    
    final static class ConstrainableProxy extends ClientHandlerProxy implements RemoteMethodControl{
        private static final long serialVersionUID = 4L;
        private ConstrainableProxy(Branch server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server,methodConstraints), id);
            //l.fine("Creating a secure proxy");
        }
        public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new ClientHandlerProxy.ConstrainableProxy(backend, ID,
                    constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


        private static Branch constrainServer(Branch server, MethodConstraints methodConstraints)
        {
            return (Branch)
            ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }




    }
    
    /**
     * @param name
     * @param obj
     * @throws RemoteException
     */
    public void add(String name, Object obj) throws RemoteException {
        backend.add(name, obj);
    }
    /**
     * @param name
     * @throws RemoteException
     */
    public void addBranch(String name) throws RemoteException {
        backend.addBranch(name);
    }
    /**
     * @param name
     * @param type
     * @param attributes
     * @throws RemoteException
     */
    public void addBranch(String name, int type, HashMap attributes) throws RemoteException {
        backend.addBranch(name, type, attributes);
    }
    /* @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return backend.equals(obj);
    }
    /**
     * @param branchName
     * @return
     * @throws RemoteException
     */
    public Branch getBranch(String branchName) throws RemoteException {
        return backend.getBranch(branchName);
    }
    /**
     * @param Index
     * @param withData
     * @return
     * @throws RemoteException
     */
    public Object getChild(int Index, boolean withData) throws RemoteException {
        return backend.getChild(Index, withData);
    }
    /**
     * @param name
     * @param withData
     * @return
     * @throws RemoteException
     */
    public Object getChild(String name, boolean withData) throws RemoteException {
        return backend.getChild(name, withData);
    }
    /**
     * @return
     * @throws RemoteException
     */
    public Object getData() throws RemoteException {
        return backend.getData();
    }
    /**
     * @return
     * @throws RemoteException
     */
    public Object getDataBlock() throws RemoteException {
        return backend.getDataBlock();
    }
    /**
     * @return
     * @throws RemoteException
     */
    public String getDescription() throws RemoteException {
        return backend.getDescription();
    }
    /**
     * @param idx
     * @return
     * @throws RemoteException
     */
    public String getName(int idx) throws RemoteException {
        return backend.getName(idx);
    }
    /**
     * @return
     * @throws RemoteException
     */
    public int getNumChildren() throws RemoteException {
        return backend.getNumChildren();
    }
    /* @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return backend.hashCode();
    }
    /**
     * @param idx
     * @return
     * @throws RemoteException
     */
    public boolean isBranch(int idx) throws RemoteException {
        return backend.isBranch(idx);
    }
    /**
     * @param name
     * @return
     * @throws RemoteException
     */
    public boolean isBranch(String name) throws RemoteException {
        return backend.isBranch(name);
    }
    /**
     * @param path
     * @return
     * @throws RemoteException
     */
    public Object locate(String path) throws RemoteException {
        return backend.locate(path);
    }
    /**
     * @param path
     * @param index
     * @return
     * @throws RemoteException
     */
    public Object locate(String path, int index) throws RemoteException {
        return backend.locate(path, index);
    }
    /**
     * @param path
     * @param index
     * @return
     * @throws RemoteException
     */
    public Object locate(String path, Object index) throws RemoteException {
        return backend.locate(path, index);
    }
    /**
     * @param path
     * @param index
     * @return
     * @throws RemoteException
     */
    public Object locate(String path, String index) throws RemoteException {
        return backend.locate(path, index);
    }
    /**
     * @param name
     * @param newObj
     * @throws RemoteException
     */
    public void overwrite(String name, Object newObj) throws RemoteException {
        backend.overwrite(name, newObj);
    }
    /**
     * @throws RemoteException
     */
    public void removeAll() throws RemoteException {
        backend.removeAll();
    }
    /**
     * @param obj
     * @throws RemoteException
     */
    public void removeChild(Object obj) throws RemoteException {
        backend.removeChild(obj);
    }
    /**
     * @param name
     * @throws RemoteException
     */
    public void removeChild(String name) throws RemoteException {
        backend.removeChild(name);
    }
    /**
     * @param obj
     * @throws RemoteException
     */
    public void setDataBlock(Object obj) throws RemoteException {
        backend.setDataBlock(obj);
    }
    /**
     * @param newName
     * @throws RemoteException
     */
    public void setDescription(String newName) throws RemoteException {
        backend.setDescription(newName);
    }
    /* @see java.lang.Object#toString()
     */
    public String toString() {
        return backend.toString();
    }
    /**
     * @param duration
     * @param rel
     * @param changeOn
     * @return
     * @throws RemoteException
     * @throws LeaseDeniedException
     */
    public EventRegistration trackChanges(long duration, ChangeEventListener rel, String changeOn) throws RemoteException, LeaseDeniedException {
        //System.out.println("Rel == null? " + (rel==null));
        return backend.trackChanges(duration, rel, changeOn);
    }
	/**
	 * @param XMLdata
	 * @throws UnsupportedOperationException
	 * @throws RemoteException
	 */
	public void addXML(String XMLdata) throws UnsupportedOperationException, RemoteException {
		this.backend.addXML(XMLdata);
	}
    
    public String exportXML() throws RemoteException{
    	return this.backend.exportXML();
    }
    
}
