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
 * thor.jini.org : org.jini.projects.thor.client
 * 
 * 
 * ThorLoader.java
 * Created on 28-Sep-2004
 * 
 * ThorLoader
 *
 */

package org.jini.projects.thor.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import net.jini.config.ConfigurationException;
import net.jini.discovery.DiscoveryEvent;

import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ThorSession;

/**
 * @author calum
 */
public class ThorLoader implements net.jini.discovery.DiscoveryListener {

	private String xmlfilename;
	private String thorname;
	private String group;
	private String parent_branch;

	private net.jini.discovery.LookupDiscoveryManager ldm;
	private net.jini.lease.LeaseRenewalManager lrm;
	

	/**
	 * Creates new ThorClient
	 * 
	 * @throws ConfigurationException
	 */
	public ThorLoader(String[] args) throws ConfigurationException {
		if (args.length != 4) {
			help();
		} else {
			group = args[0];
			thorname=args[1];
			parent_branch=args[2];
			xmlfilename = args[3];
			init();
		}
	}

	private void help() {
		System.out.println("Thor XML Loader Utility:");
		System.out.println("\tUsage:");
		System.out.println("\tjava org.jini.projects.thor.client.ThorLoader <jini group> <thor 'Name'> <parent branch> <location of xml file>");
		System.out.println("\tNOTE: if you specify creation of branches within your XML and those branches already exist, they will be overwritten.");
		System.out.println("\t\tIf required export your parent branch to XML first thrugh the Service UI");
	}

	public void init() {

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new java.rmi.RMISecurityManager());
			try {
				System.out.println("Looking at groups: ");
				lrm = new net.jini.lease.LeaseRenewalManager();
				ldm = new net.jini.discovery.LookupDiscoveryManager(new String[]{group}, null, this);
				synchronized (this) {
					wait(0);
				}
			} catch (Exception ex) {
				System.out.println("Err:" + ex.getMessage());
				ex.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Finished cleanly");
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// URGENT Handle InterruptedException
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	//	            org.jini.projects.thor.service.ThorService thorproxy = null;
	//
	//	            /*
	//				 * Set up ChangeEventRegistrations First object will be notified
	//				 * on any changes,updates or deletes Second will only be
	//				 * notified of additions to the existing data set
	//				 */
	//	            org.jini.projects.thor.service.ChangeEventListenerImpl handleAllMods =
	// new
	// org.jini.projects.thor.service.ChangeEventListenerImpl();
	//	            org.jini.projects.thor.service.ChangeEventListenerImpl
	// handleOnlyAdditions =
	// new
	// org.jini.projects.thor.service.ChangeEventListenerImpl(ChangeConstants.CREATE);
	//	            Exporter exp = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
	// new
	// BasicILFactory());
	//	            
	//	            // Obtain an instance of Thor if one exists
	//	            net.jini.core.lookup.ServiceRegistrar[] registrars =
	// discoveryEvent.getRegistrars();
	//	            Class[] classType = {org.jini.projects.thor.service.ThorService.class};
	//	            net.jini.core.entry.Entry[] attrs = {new
	// net.jini.lookup.entry.Name("master")};
	//	            net.jini.core.lookup.ServiceTemplate svctmp = new
	// net.jini.core.lookup.ServiceTemplate(null, classType, attrs);
	//	            for (int i = 0; i < registrars.length; i++) {
	//	                thorproxy = (org.jini.projects.thor.service.ThorService)
	// registrars[0].lookup(svctmp);
	//	                if (thorproxy != null)
	//	                    break;
	//	            }
	//	            ProxyPreparer preparer = (ProxyPreparer)
	// config.getEntry("org.jini.projects.thor.client", "proxyPreparer",
	// ProxyPreparer.class, new BasicProxyPreparer());
	//	            
	//	            ThorService thor = (ThorService) preparer.prepareProxy(thorproxy);
	//	            if (thor != null){
	//	            }

	public void discarded(net.jini.discovery.DiscoveryEvent discoveryEvent) {

	}

	public static void main(String[] args) {
		System.getProperties().put("java.security.policy", "/home/calum/policy.all");
		try {
			ThorLoader client = new ThorLoader(args);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
	}

	public void discovered(DiscoveryEvent e) {
		// TODO Complete method stub for discovered
		try {
			org.jini.projects.thor.service.ThorService thorproxy = null;

			// Obtain an instance of Thor if one exists
			net.jini.core.lookup.ServiceRegistrar[] registrars = e.getRegistrars();
			Class[] classType = {org.jini.projects.thor.service.ThorService.class};
			net.jini.core.entry.Entry[] attrs = {new net.jini.lookup.entry.Name(thorname)};
			net.jini.core.lookup.ServiceTemplate svctmp = new net.jini.core.lookup.ServiceTemplate(null, classType, attrs);
			for (int i = 0; i < registrars.length; i++) {
				thorproxy = (org.jini.projects.thor.service.ThorService) registrars[0].lookup(svctmp);
				if (thorproxy != null)
					break;
			}
			ThorSession thor = thorproxy.getSession();
			if (thor != null) {
				Branch br = thor.getRoot().getBranch(parent_branch);
				if(br!=null){
				System.out.println("Adding XML!");
				File xmlFile = new File(xmlfilename);
				FileInputStream fis = new FileInputStream(xmlFile);
				byte[] myfilecontents = new byte[(int) xmlFile.length()];
				fis.read(myfilecontents);
				String xml = new String(myfilecontents);
				System.out.println("XML is:" + xml);
				br.addXML(xml);
				} else 
					System.out.println(parent_branch + " could not be found in " + thorname);
			}
		} catch (RemoteException e1) {
			// TODO Handle RemoteException
			e1.printStackTrace();

		} catch (FileNotFoundException e1) {
			// TODO Handle FileNotFoundException
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Handle IOException
			e1.printStackTrace();
		}
	}
}
