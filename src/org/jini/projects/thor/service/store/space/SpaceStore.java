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
 * Created on 13-May-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.jini.projects.thor.service.store.space;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.TransactionException;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.entry.Name;
import net.jini.space.JavaSpace;

import org.jini.projects.thor.handlers.FileHierarchy;
import org.jini.projects.thor.handlers.HierarchyHandler;
import org.jini.projects.thor.handlers.InternalHierarchy;
import org.jini.projects.thor.handlers.LinkHierarchy;
import org.jini.projects.thor.handlers.PropertyHandler;
import org.jini.projects.thor.handlers.RemoteThorHandler;
import org.jini.projects.thor.service.store.Store;

/**
 * @author calum
 */
public class SpaceStore implements Store, DiscoveryListener {
	JavaSpace space;
    
	private Configuration config;
	/**
	 * 
	 */
	public SpaceStore() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see thor.service.store.Store#storeRoot(thor.handlers.HierarchyHandler)
	 */
	public String storeRoot(HierarchyHandler root) {
		// TODO Auto-generated method stub

		System.out.println("Storing the root.....");
		storeNode(root);
		return "....root stored.";
	}
	public void storeNode(HierarchyHandler handle) {
		ArrayList nodeEntries = new ArrayList();
		for (int i = 0; i < handle.getNumChildren(); i++) {
			Object ob = handle.getChild(i, true);

			Object valueToStore = null;
			if (ob == null)
				System.out.println("Null ITEM...skipping");
			else if (ob instanceof HierarchyHandler) {
				//Check the type for what to do with it!
				HierarchyHandler childhandle = (HierarchyHandler) ob;
				if (ob instanceof InternalHierarchy) {
					BranchInfo inf = new BranchInfo(childhandle.getBranchID(), handle.getName(i));
					valueToStore = inf;
					
					storeNode((InternalHierarchy) ob);
				}
				if (ob instanceof PropertyHandler) {
					PropsInfo inf = new PropsInfo(childhandle.getBranchID(), handle.getName(i));

					storeNode((PropertyHandler) ob);
				}
				if (ob instanceof RemoteThorHandler) {
					String connect = childhandle.getDescription();
					String[] items = connect.split(":");
					RemoteInfo inf = new RemoteInfo(handle.getBranchID(), items[0], items[1], items[2]);
					valueToStore = inf;
				}
				if (ob instanceof FileHierarchy) {
					FileInfo inf = new FileInfo(childhandle.getBranchID(), childhandle.getDescription());
					valueToStore = inf;
				}
				if (ob instanceof LinkHierarchy) {
					LinkInfo inf = new LinkInfo(childhandle.getBranchID(), childhandle.getDescription());
					valueToStore = inf;
				}
				if (valueToStore != null)
					nodeEntries.add(valueToStore);
			} else {
				valueToStore = new ItemInfo(handle.getName(i), ob);
				nodeEntries.add(valueToStore);
			}

		}
		HierarchyEntry ent = new HierarchyEntry(handle.getBranchID(), "Branch", nodeEntries);
		try {
			space.write(ent, null, net.jini.core.lease.Lease.FOREVER);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see thor.service.store.Store#init()
	 */
	public void init(Configuration config) {
		// TODO Auto-generated method stub
		try {
            this.config = config;
			String[] groups=null;
			try {
				groups = (String[]) config.getEntry("org.jini.projects.thor.store.space","jiniGroups" , String[].class);
			} catch (ConfigurationException e1) {
				// URGENT Handle ConfigurationException
				e1.printStackTrace();
			}
			LookupDiscoveryManager ldm = new LookupDiscoveryManager(groups, null, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see net.jini.discovery.DiscoveryListener#discarded(net.jini.discovery.DiscoveryEvent)
	 */
	public void discarded(DiscoveryEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.jini.discovery.DiscoveryListener#discovered(net.jini.discovery.DiscoveryEvent)
	 */
	public void discovered(DiscoveryEvent e) {
		// TODO Auto-generated method stub
		ServiceRegistrar[] regs = e.getRegistrars();
		boolean gotSpace = false;
		for (int i = 0; i < regs.length; i++) {
			if (!gotSpace) {
				ServiceRegistrar reg = regs[i];
                String spaceName=null;
				try {
					spaceName = (String) config.getEntry("org.jini.projects.thor.store.space","spaceName" , String.class,"cluster");
				} catch (ConfigurationException e2) {
					// URGENT Handle ConfigurationException
					e2.printStackTrace();
				}
				ServiceTemplate tmp = new ServiceTemplate(null, new Class[] { JavaSpace.class }, new Entry[] { new Name(spaceName)});
				Object svc = null;
				try {
					svc = reg.lookup(tmp);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (svc != null) {

					space = (JavaSpace) svc;
					gotSpace = true;
					System.out.println("Space for Tree storage found");
				}
			}
		}
		if (!gotSpace) {
			System.out.println("Storage is not available......exiting");
			System.exit(1);
		}

	}

	/* @see org.jini.projects.thor.service.store.Store#loadRoot(java.lang.String)
	 */
	public HierarchyHandler loadRoot(String name) {
		// TODO Complete method stub for loadRoot
		return null;
	}
}
