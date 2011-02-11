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

package org.jini.projects.thor.service.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jini.projects.thor.handlers.HierarchyHandler;

/**
 * 
 * Handles all issues relating to the in memory storage of branches. <BR>
 * Technically, all bracnhes are actually stored as a flat structure, with this
 * class caching them in a Map indexed on the branchID.
 */

public class FlatBackend implements Backend {
    String filename;

    private HashMap branches = new HashMap();

    Logger log = Logger.getLogger("org.jini.projects.thor.service");

    public FlatBackend() {

    }

    public void init(Map params) {
        filename = (String) params.get("filename");
        if (filename == null)
            filename = "backstore";
        try {
            File f = new File(filename);           
            if (f.exists()) {
                log.info("Loading Backstore");
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
                branches = (HashMap) ois.readObject();
                ois.close();
            } else {
            	log.warning("Executing fallbackLoader: " + params.get("fallbackSource"));
            	BackendLoader bel = (BackendLoader) params.get("fallbackLoader");
            	String filename = (String) params.get("fallbackSource");
            	bel.initialise(filename);
            }
            	
        } catch (IOException e) {
            log.severe("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.severe("Err: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void insertBranch(HierarchyHandler parent, HierarchyHandler child) {
        // if (parent != null)
        // System.out.println("Adding a branch: " + child.getBranchID() + " (" +
        // parent.getBranchID() + ")");
        // else
        // System.out.println("Adding a branch: " + child.getBranchID());
        branches.put(child.getBranchID(), child);
    }

    public void store() {
        try {
            File f = new File(filename);
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            oos.writeObject(branches);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            log.severe("Err: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void store(HierarchyHandler branch) {
        // As we don't store items individually, we store all the branches in a
        // single file
        store();
    }

    // Link this into the storage mechanism
    public HierarchyHandler getBranch(String branchID) {
        //System.out.println("Getting a branch for:" + branchID);
        return (HierarchyHandler) branches.get(branchID);
    }
    /* (non-Javadoc)
     * @see org.jini.projects.thor.service.store.Backend#getAllIds()
     */
    public String[] getAllIds() {
        String[] arr = (String[])branches.keySet().toArray(new String[]{});
        return arr;
    }
    /* (non-Javadoc)
     * @see org.jini.projects.thor.service.store.Backend#removeBranch(java.lang.String)
     */
    public boolean removeBranch(String ID) {
        // TODO Auto-generated method stub
        return (branches.remove(ID)!=null?true:false);
    }
}
