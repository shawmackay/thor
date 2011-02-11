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
 * Created on 20-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.jini.projects.thor.service.store;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jini.projects.thor.handlers.ConfigurationFileHandler;
import org.jini.projects.thor.handlers.HierarchyHandler;
import org.jini.projects.thor.handlers.InternalHierarchy;
import org.jini.projects.thor.handlers.LinkHierarchy;

/**
 * Compacts a Thor tree by traversing all branches and ensuring that orphaned
 * branches are removed. Any branches with null as a key must also be removed.
 * Must take into account Link Handlers so that branches that are linked to are
 * not removed, and break the links.
 * 
 * @author Calum
 * 
 */
public class Compactor {

    Logger log = Logger.getLogger("org.jini.projects.thor");

    List activebranches = new ArrayList();

    List inactivebranches = new ArrayList();

    HierarchyHandler root = null;

    Backend theBackend;

    private String[] branches;

    public Compactor(HierarchyHandler root) {
        if (root != null) {
            Backend backend = StorageFactory.getBackend();
            theBackend = backend;
            HierarchyHandler h = root;
            this.root = root;
            activebranches.add("ROOT");
            branches = backend.getAllIds();
            // Traverse the tree
            traverse(h, 0);
        } else
            log.info("Compactor passed no data - continuing");
    }

    public void traverse(HierarchyHandler branch, int level) {
        List removeItemNames = new ArrayList();
        int numChildren = branch.getNumChildren();
        List removeItemindexes = new ArrayList();
        for (int i = 0; i < numChildren; i++) {
            String name = branch.getName(i);
            if (branch.getName(i) == null || branch.getName(i).equals("")) {
                for (int lev = 0; lev < level * 4; lev++)
                    System.out.print(" ");
                log.fine("Found null branch/item : " + name);
                Object item = branch.getChild(i, true);
                if (item instanceof HierarchyHandler) {
                    String branchID = ((HierarchyHandler) item).getBranchID();
                    log.fine("Adding branch: " + branchID + ": " + name + " to inactive branches");
                    inactivebranches.add(branchID);
                } else {
                    log.fine("Removing null Item[ " + name + "]");
                    removeItemindexes.add(new Integer(i));
                }

            }
            Object o = branch.getChild(i, true);
            if (o instanceof HierarchyHandler) {
                if (branch.getName(i) != null && branch.getName(i).indexOf("/") != -1) {
                    inactivebranches.add(((HierarchyHandler) o).getBranchID());
                    removeItemNames.add(branch.getName(i));
                } else {
                    if (!activebranches.contains(((HierarchyHandler) o).getBranchID()))
                        activebranches.add(((HierarchyHandler) o).getBranchID());
                    if (o instanceof LinkHierarchy) {
                        LinkHierarchy l = (LinkHierarchy) o;
                        log.fine("Linked branch " + name + " points to: " + l.getDescription());
                        HierarchyHandler linkedBranch = root.getBranch(l.getDescription());
                        if (linkedBranch == null) {

                            log.fine("Link broken....removing");
                            activebranches.remove(l.getBranchID());
                            removeItemNames.add(branch.getName(i));
                            inactivebranches.add(l.getBranchID());
                        } else {
                            if (!activebranches.contains(linkedBranch.getBranchID()))
                                activebranches.add(linkedBranch.getBranchID());
                        }
                    }
                    if (o instanceof InternalHierarchy || o instanceof ConfigurationFileHandler) {
                        log.finer("Looking at branch: " + branch.getName(i));
                        traverse((HierarchyHandler) o, level + 1);
                    }
                }
            }
        }
        for (int i = 0; i < removeItemindexes.size(); i++) {
            log.fine("Removing item indexed @ " + removeItemindexes.get(i));
            branch.removeChild(branch.getName(((Integer) removeItemindexes.get(i)).intValue()));
        }
        for (int i = 0; i < removeItemNames.size(); i++) {
            log.fine("Removing item denoted @ " + removeItemNames.get(i));
            branch.removeChild((String) removeItemNames.get(i));
        }
    }

    public void display() {
        int num = inactivebranches.size();
        log.fine("Tree has " + activebranches.size() + " active branches");
        log.fine("Tree has " + inactivebranches.size() + " inactive branches");
        log.fine("Backend reports: " + branches.length + " stored.");
        log.finer("Removing inactive branches");
        for (int i = 0; i < num; i++) {
            log.finest("InactiveBranch: " + inactivebranches.get(i) + " removed");
            theBackend.removeBranch((String) inactivebranches.get(i));
        }

        log.finer("Removing orphaned branches");
        int orphanremovecount = 0;
        for (int i = 0; i < branches.length; i++) {
            if (activebranches.contains(branches[i])) {
                log.finest("Activebranch: " + branches[i] + " retained");
            } else {
                log.finest("OrphanBranch: " + branches[i] + " removed");
                orphanremovecount++;
                theBackend.removeBranch(branches[i]);
            }
        }
        log.fine("Removed " + orphanremovecount + " orphaned branches");
        theBackend.store();
        log.info("Compacted Tree Stored");

    }

    public static void main(String[] args) {

    }
}
