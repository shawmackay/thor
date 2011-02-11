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
 * thor.jini.org : org.jini.projects.thor.url.thor
 * 
 * 
 * ThorURLConnection.java Created on 14-Apr-2004
 * 
 * ThorURLConnection
 *  
 */

package org.jini.projects.thor.url.thor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.entry.Name;

import org.jini.projects.thor.service.ThorService;

/**
 * Thor URL's are of the format thor:// <thorname>. <Jini groupname>/thorpath
 * i.e.
 * thor://master.production/Properties/Athena/SALESUPT/org.jini.projects.athena.connection.type
 * 
 * @author calum
 */
public class ThorURLConnection extends URLConnection {
	//private static Map connections = null;
	private String host;
	private String file;
	private String query;
	private ThorService service;

	public ThorURLConnection(URL u) {
		super(u);
		System.out.println("Protocol: " + u.getProtocol());
		System.out.println("Host: " + u.getHost());
		System.out.println("File: " + u.getFile());
		System.out.println("Query: " + u.getQuery());
		host = u.getHost();
		file = u.getFile();
		query = u.getQuery();
		try {
			connect();
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	/*
	 * @see java.net.URLConnection#connect()
	 */
	public void connect() throws IOException {
		// TODO Complete method stub for connect
		System.out.println("Connecting");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		String name = host.substring(0, host.indexOf('.'));
		String group = host.substring(host.indexOf('.') + 1);
		ThorLookup lookup = new ThorLookup(name, group);
		long start = System.currentTimeMillis();
		while (!lookup.discoveryFinished() && (System.currentTimeMillis() - start < 5000)) {
			try {
				wait(500);
			} catch (Exception e) {
			}
		}
		if (lookup.getServiceInstance() == null)
			throw new IOException("Thor instance could not be found");
		service = lookup.getServiceInstance();
		System.out.println(service.getClass().getName());
		System.out.println("Connection completed");
	}

	private class ThorLookup implements DiscoveryListener {
		private LookupDiscoveryManager ldm;
		private boolean finished = false;
		private String thorname;
		private ServiceTemplate tmp;
		private ThorService theService = null;

		public ThorLookup(String thorname, String group) throws IOException {
			System.out.println("Looking for " + thorname + " in the " + group + " group");
			ldm = new LookupDiscoveryManager(new String[]{group}, null, this);
			this.thorname = thorname;
			Class[] clazz = new Class[]{ThorService.class};
			Entry[] attrs = new Entry[]{new Name(thorname)};
			tmp = new ServiceTemplate(null, clazz, attrs);
		}

		public void discovered(DiscoveryEvent e) {
			// TODO Complete method stub for discovered
			System.out.println("Discovered");
			ServiceRegistrar[] regs = e.getRegistrars();
			for (int i = 0; i < regs.length; i++) {
				ServiceRegistrar reg = regs[i];
				Object obj = null;
				try {
					obj = reg.lookup(tmp);
				} catch (RemoteException e1) {
					// URGENT Handle RemoteException
					e1.printStackTrace();
				}
				if (obj != null) {
					System.out.println("Thor Service instance found");
					theService = (ThorService) obj;
					finished = true;
					break;
				}
			}
		}

		public void discarded(DiscoveryEvent e) {
			// TODO Complete method stub for discarded
		}

		public boolean discoveryFinished() {
			return finished;
		}

		public ThorService getServiceInstance() {
			return theService;
		}

		public void cleanup() {
			ldm.terminate();
		}
	}

	/*
	 * @see java.net.URLConnection#getContent()
	 */
	public Object getContent() throws IOException {
		String thorBranch = file.substring(1, file.lastIndexOf('/'));
		String thorItem = file.substring(file.lastIndexOf('/') + 1);
		System.out.println("ThorBranch = " + thorBranch);
		System.out.println("ThorItem = " + thorItem);
		return service.getSession().getRoot().getBranch(thorBranch).getChild(thorItem, true);
	}

	/*
	 * @see java.net.URLConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		// TODO Complete method stub for getInputStream
		Object o = getContent();
		if (o instanceof byte[])
			return new ByteArrayInputStream((byte[]) o);
		else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oas = new ObjectOutputStream(baos);
			oas.writeObject(o);
			byte[] array = baos.toByteArray();
			oas.close();
			return new ByteArrayInputStream(array);
		}
	}
}
