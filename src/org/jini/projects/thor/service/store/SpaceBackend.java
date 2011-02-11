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

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.entry.Name;
import net.jini.space.JavaSpace;

import org.jini.projects.thor.handlers.HierarchyHandler;
import org.jini.projects.thor.service.store.space.BranchEntry;

/**
 * User: calum
 * Date: 05-Jun-2003
 * Time: 14:19:32
 */
public class SpaceBackend implements Backend, DiscoveryListener {
    /* (non-Javadoc)
     * @see org.jini.projects.thor.service.store.Backend#getAllIds()
     */
    public String[] getAllIds() {
        // TODO Auto-generated method stub
        return null;
    }
    JavaSpace space;
    TransactionManager tm;
    boolean finishedLookup = false;

    /* (non-Javadoc)
     * @see org.jini.projects.thor.service.store.Backend#removeBranch(java.lang.String)
     */
    public boolean removeBranch(String ID) {
        // TODO Auto-generated method stub
        return false;
    }
    HashMap branches = new HashMap();

    public void init(Map params) {

        try {
            LookupDiscoveryManager ldm = new LookupDiscoveryManager(new String[]{"dev.jini2"},null,this);
            System.out.println("Beginning lookup");
            synchronized(this){

                try {
                    while(!finishedLookup)
                        wait(100);
                    if(space==null || tm ==null){
                        System.out.println("System cannot find a suitable backing store.....exiting");
                        System.exit(1);

                    }
                } catch (InterruptedException e) {
                    System.out.println("Err: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void insertBranch(HierarchyHandler parent, HierarchyHandler child) {
        Transaction.Created tx = null;
        try {
            tx = TransactionFactory.create(tm, 20000);
            BranchEntry be = new BranchEntry(child.getBranchID(), child);
            space.write(be, tx.transaction, Lease.FOREVER);
            System.out.println("Storing a branch into space:" + child.getBranchID());
            tx.transaction.commit();
            branches.put(be.ID, be.branch);
        } catch (LeaseDeniedException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (CannotCommitException e) {
            if (tx != null)
                try {
                    tx.transaction.abort();
                } catch (UnknownTransactionException e1) {
                    System.out.println("Err: " + e1.getMessage());
                    e1.printStackTrace();
                } catch (CannotAbortException e1) {
                    System.out.println("Err: " + e1.getMessage());
                    e1.printStackTrace();
                } catch (RemoteException e1) {
                    System.out.println("Err: " + e1.getMessage());
                    e1.printStackTrace();
                }
        } catch (TransactionException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }


    }

    //Link this into the storage mechanism
    public HierarchyHandler getBranch(String branchID) {

        if (branches.get(branchID) == null) {
            System.out.println("Querying space for:" + branchID);
            BranchEntry betmpl = new BranchEntry(branchID, null);
            BranchEntry be = null;
            try {
                be = (BranchEntry) space.readIfExists(betmpl, null, 50);
            } catch (UnusableEntryException e) {
                System.out.println("Err: " + e.getMessage());
                e.printStackTrace();
            } catch (TransactionException e) {
                System.out.println("Err: " + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("Err: " + e.getMessage());
                e.printStackTrace();
            } catch (RemoteException e) {
                System.out.println("Err: " + e.getMessage());
                e.printStackTrace();
            }
            branches.put(be.ID, be.branch);
        }
        return (HierarchyHandler) branches.get(branchID);
    }

    public void store() {
        System.out.println("UNSUPPORTED OPERATION!");
    }

    public void store(HierarchyHandler ob) {

        Transaction.Created tx = null;
        try {
            tx = TransactionFactory.create(tm, 20000);
            BranchEntry be = new BranchEntry(ob.getBranchID(), ob);
            BranchEntry betake = new BranchEntry(ob.getBranchID(), null);
            //kill the current entry in the space
            space.takeIfExists(betake, tx.transaction, 100);
            //relacing it with this one if necessary
            System.out.println("Storing/Updating a branch into space:" + ob.getBranchID());
            space.write(be, tx.transaction, Lease.FOREVER);
            tx.transaction.commit();
        } catch (LeaseDeniedException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (CannotCommitException e) {
            if (tx != null)
                try {
                    tx.transaction.abort();
                } catch (UnknownTransactionException e1) {
                    System.out.println("Err: " + e1.getMessage());
                    e1.printStackTrace();
                } catch (CannotAbortException e1) {
                    System.out.println("Err: " + e1.getMessage());
                    e1.printStackTrace();
                } catch (RemoteException e1) {
                    System.out.println("Err: " + e1.getMessage());
                    e1.printStackTrace();
                }
        } catch (TransactionException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (UnusableEntryException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void discovered(DiscoveryEvent e) {
        ServiceRegistrar[] regs = e.getRegistrars();
        Entry[] spaceAttr = new Entry[] {new Name("TestSpace")};
        Class[] spaceClazz = new Class[] {JavaSpace.class};
        Class[] txnClazz = new Class[] {TransactionManager.class};
        try {
            space = (JavaSpace) regs[0].lookup(new ServiceTemplate(null,spaceClazz,spaceAttr));
            tm = (TransactionManager) regs[0].lookup(new ServiceTemplate(null,txnClazz,null));
        } catch (RemoteException e1) {
            System.out.println("Err: " + e1.getMessage());
            e1.printStackTrace();
        }
        if (space!=null && tm !=null){
            System.out.println("Discovery complete");
            finishedLookup = true;
        }


    }

    public void discarded(DiscoveryEvent e) {
    }


}
