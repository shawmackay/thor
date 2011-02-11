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
 *  Branch_Impl.java
 *
 *  Created on 18 September 2001, 11:43
 */
package org.jini.projects.thor.handlers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

import net.jini.core.event.EventRegistration;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.id.UuidFactory;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.store.StorageFactory;


/**
 *@author     calum

 *@version
 */
public class Branch_Impl implements Branch, Remote {

    private HierarchyHandler branch = null;
    
    static HashMap exportedBranches = new HashMap();
    static HashMap builtBranches= new HashMap();
    //private Exporter exp;
    
    /**
     *  Creates new Branch_Impl
     *
     *@param  internalBranch       Description of Parameter
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public Branch_Impl(HierarchyHandler internalBranch) throws RemoteException {
        this.branch = internalBranch;             
    }

    private void setBranch(HierarchyHandler internalBranch){
           this.branch = internalBranch;
    }
    
    public void setDescription(String newName) throws RemoteException {
        branch.setDescription(newName);
        StorageFactory.getBackend().store(branch);
    }

    public void setDataBlock(Object obj) throws RemoteException {
        branch.setDataBlock(obj);
        StorageFactory.getBackend().store(branch);
    }

    public int getNumChildren() throws RemoteException {
        return branch.getNumChildren();
    }

    public Branch getBranch(String branchName) throws RemoteException {
        HierarchyHandler aBranch = branch.getBranch(branchName);
        if (aBranch == null) {
            return null;
        }
        Remote r;
        Branch c;
        Branch p;
       
       /*
        * Although we re-use the existing exported refrence, we make sure
        * we refresh the internal reference so that the changes can be seen
        */
        if(exportedBranches.containsKey(branchName)){
            Branch_Impl impl = (Branch_Impl) builtBranches.get(branchName);
            impl.setBranch(aBranch);
            p = (Branch) exportedBranches.get(branchName);                 
          //  System.out.println("Returning existing reference");
        }else {
            
            //Exporter myExporter =new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());
            c = new Branch_Impl(aBranch);
            //r= myExporter.export(c);
            //p =ClientHandlerProxy.create((Branch) r, UuidFactory.generate());
            p = (Branch) DefaultExporterManager.getManager().exportProxy(c, "ClientHandlers", UuidFactory.generate());
            exportedBranches.put(branchName,p);
            builtBranches.put(branchName, c);
            //System.out.println("Returning new reference");
        }
        return p; 
    }

    public Object getChild(int idx) throws RemoteException {
        return branch.getChild(idx, true);
    }

    public String getName(int idx) throws RemoteException {
        return branch.getName(idx);
    }

    public Object getChild(String name, boolean withData) throws RemoteException {
        return branch.getChild(name, withData);
    }

    public Object getChild(int Index, boolean withData) throws RemoteException {
        return branch.getChild(Index, withData);
    }

    public Object getDataBlock() throws RemoteException {
        return branch.getDataBlock();
    }

    public String getDescription() throws RemoteException {
        return branch.getDescription();
    }

    public boolean isBranch(String name) throws RemoteException {
        Object x = branch.locate(name);

        if (x instanceof HierarchyHandler) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBranch(int idx) throws RemoteException {
        Object x = branch.getChild(idx, true);
        if (x instanceof HierarchyHandler) {
            return true;
        } else {
            return false;
        }
    }

    public Object getData() throws RemoteException {
        return branch.getData();
    }

    public Object locate(String path) throws RemoteException {
        return branch.locate(path);
    }

    public Object locate(String path, Object index) throws RemoteException {
        return branch.locate(path, index);
    }

    public Object locate(String path, int index) throws RemoteException {
        return branch.locate(path, index);
    }

    public Object locate(String path, String index) throws RemoteException {
        return branch.locate(path, index);
    }

    public void removeAll() throws RemoteException {
        branch.removeAll();
        StorageFactory.getBackend().store(branch);
    }

    public void overwrite(String name, Object newObj) throws RemoteException {
        branch.overwrite(name, newObj);
        StorageFactory.getBackend().store(branch);
    }

    public void removeChild(Object obj) throws RemoteException {
        branch.removeChild(obj);
        StorageFactory.getBackend().store(branch);
    }

    public void removeChild(String name) throws RemoteException {
        branch.removeChild(name);
        StorageFactory.getBackend().store(branch);
    }

    public void add(String name, Object obj) throws RemoteException {
        branch.add(name, obj);
        StorageFactory.getBackend().store(branch);
    }

    public void addBranch(String name) throws RemoteException {

        branch.addBranch(name);
        StorageFactory.getBackend().store(branch);
    }

    public void addBranch(String name, int type, java.util.HashMap attributes) throws RemoteException {
        branch.addBranch(name, type, attributes);
        StorageFactory.getBackend().store(branch);
        
    }

    public EventRegistration trackChanges(long duration, ChangeEventListener rel, String changeOn) throws RemoteException, LeaseDeniedException {
       
        EventRegistration reg = branch.addChangeEventListener(rel, changeOn);
        return reg;
        
    }

    HierarchyHandler getHandler() {
        return branch;
    }
    
   

    /* @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        
        System.out.println("Branch_Impl being finalized");
    }
	/* @see org.jini.projects.thor.handlers.Branch#addXML(java.lang.String)
	 */
	public void addXML(String XMLdata) throws UnsupportedOperationException, RemoteException {
		
        branch.addXML(XMLdata);
        StorageFactory.getBackend().store(branch);
	}

	/* @see org.jini.projects.thor.handlers.Branch#exportXML()
	 */
	public String exportXML() throws RemoteException {
		return branch.exportXML();
	}
}

