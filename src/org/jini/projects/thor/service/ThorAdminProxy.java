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
 * ThorAdminProxy.java
 * 
 * Created on 17 September 2001, 14:17
 */

package org.jini.projects.thor.service;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.JoinManager;

/**
 * @author calum
 * @version
 */
public class ThorAdminProxy implements ThorAdmin {
	private JoinManager jm;
	private LookupDiscoveryManager ldm;

	/** Creates new ThorAdminProxy */
	public ThorAdminProxy(JoinManager jm, LookupDiscoveryManager ldm) {
		this.jm = jm;
		this.ldm = ldm;
	}

	public void terminate() {
		System.out.println("Remote Termination commencing......");
		jm.terminate();
		ldm.terminate();
		File file = new File("serviceID_" + "master" + ".ser");
		file.delete();
		Runnable r = new Runnable() {/*
													    * @see java.lang.Runnable#run()
													    */
			public void run() {
				try {
					// TODO Complete method stub for run
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// URGENT Handle InterruptedException
					e.printStackTrace();
				}
                System.out.println("Exiting");
				System.exit(0);
			}
		};
        Thread t = new Thread(r);
        t.start();
	}

	/*
	 * @see com.sun.jini.admin.DestroyAdmin#destroy()
	 */
	public void destroy() throws RemoteException {
		// TODO Complete method stub for destroy
		terminate();
	}

	/*
	 * @see net.jini.lookup.DiscoveryAdmin#addMemberGroups(java.lang.String[])
	 */
	public void addMemberGroups(String[] groups) throws RemoteException {
		try {
			// TODO Complete method stub for addMemberGroups
			ldm.addGroups(groups);
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	/*
	 * @see net.jini.lookup.DiscoveryAdmin#getMemberGroups()
	 */
	public String[] getMemberGroups() throws RemoteException {
		// TODO Complete method stub for getMemberGroups
		return ldm.getGroups();
	}

	/*
	 * @see net.jini.lookup.DiscoveryAdmin#getUnicastPort()
	 */
	public int getUnicastPort() throws RemoteException {
		// TODO Complete method stub for getUnicastPort
		return 0;
	}

	/*
	 * @see net.jini.lookup.DiscoveryAdmin#removeMemberGroups(java.lang.String[])
	 */
	public void removeMemberGroups(String[] groups) throws RemoteException {
		// TODO Complete method stub for removeMemberGroups
		ldm.removeGroups(groups);
	}

	/*
	 * @see net.jini.lookup.DiscoveryAdmin#setMemberGroups(java.lang.String[])
	 */
	public void setMemberGroups(String[] groups) throws RemoteException {
		try {
			// TODO Complete method stub for setMemberGroups
			ldm.setGroups(groups);
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	/*
	 * @see net.jini.lookup.DiscoveryAdmin#setUnicastPort(int)
	 */
	public void setUnicastPort(int port) throws IOException, RemoteException {
		// TODO Complete method stub for setUnicastPort
	}

	/*
	 * @see net.jini.admin.JoinAdmin#addLookupAttributes(net.jini.core.entry.Entry[])
	 */
	public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
		// TODO Complete method stub for addLookupAttributes
		jm.addAttributes(attrSets);
	}

	/*
	 * @see net.jini.admin.JoinAdmin#addLookupGroups(java.lang.String[])
	 */
	public void addLookupGroups(String[] groups) throws RemoteException {
		try {
			// TODO Complete method stub for addLookupGroups
			ldm.addGroups(groups);
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	/*
	 * @see net.jini.admin.JoinAdmin#addLookupLocators(net.jini.core.discovery.LookupLocator[])
	 */
	public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
		// TODO Complete method stub for addLookupLocators
		ldm.addLocators(locators);
	}

	/*
	 * @see net.jini.admin.JoinAdmin#getLookupAttributes()
	 */
	public Entry[] getLookupAttributes() throws RemoteException {
		// TODO Complete method stub for getLookupAttributes
		return jm.getAttributes();
	}

	/*
	 * @see net.jini.admin.JoinAdmin#getLookupGroups()
	 */
	public String[] getLookupGroups() throws RemoteException {
		// TODO Complete method stub for getLookupGroups
		return ldm.getGroups();
	}

	/*
	 * @see net.jini.admin.JoinAdmin#getLookupLocators()
	 */
	public LookupLocator[] getLookupLocators() throws RemoteException {
		// TODO Complete method stub for getLookupLocators
		return ldm.getLocators();
	}

	/*
	 * @see net.jini.admin.JoinAdmin#modifyLookupAttributes(net.jini.core.entry.Entry[],
	 *           net.jini.core.entry.Entry[])
	 */
	public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
		// TODO Complete method stub for modifyLookupAttributes
		jm.modifyAttributes(attrSetTemplates, attrSets);
	}

	/*
	 * @see net.jini.admin.JoinAdmin#removeLookupGroups(java.lang.String[])
	 */
	public void removeLookupGroups(String[] groups) throws RemoteException {
		// TODO Complete method stub for removeLookupGroups
		ldm.removeGroups(groups);
	}

	/*
	 * @see net.jini.admin.JoinAdmin#removeLookupLocators(net.jini.core.discovery.LookupLocator[])
	 */
	public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
		// TODO Complete method stub for removeLookupLocators
		ldm.removeLocators(locators);
	}

	/*
	 * @see net.jini.admin.JoinAdmin#setLookupGroups(java.lang.String[])
	 */
	public void setLookupGroups(String[] groups) throws RemoteException {
		try {
			// TODO Complete method stub for setLookupGroups
			ldm.setGroups(groups);
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	/*
	 * @see net.jini.admin.JoinAdmin#setLookupLocators(net.jini.core.discovery.LookupLocator[])
	 */
	public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
		// TODO Complete method stub for setLookupLocators
		ldm.setLocators(locators);
	}
}
