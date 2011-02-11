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
 * ThorClient.java
 *
 * Created on 18 September 2001, 12:15
 */

package org.jini.projects.thor.client;

import java.rmi.Remote;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.event.EventRegistration;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.ThorService;



/**
 * @author  calum
 * @version
 */


public class ThorClient implements net.jini.discovery.DiscoveryListener {
    private net.jini.discovery.LookupDiscoveryManager ldm;
    private net.jini.lease.LeaseRenewalManager lrm;
    private final String[] GROUPS = {"uk.co.cwa.jini2"};
    private final Configuration config;

    /** Creates new ThorClient 
     * @throws ConfigurationException*/
    public ThorClient(String[] args) throws ConfigurationException {
    	config = ConfigurationProvider.getInstance(args);
		LoginContext lc = (LoginContext) config.getEntry("org.jini.projects.thor","loginContext", LoginContext.class, null);
		
		if(lc!=null){
			try {
			lc.login();
			
				Subject.doAsPrivileged(lc.getSubject(), new PrivilegedExceptionAction() {
					/*
					 * @see java.security.PrivilegedExceptionAction#run()
					 */
					public Object run() throws Exception {
						// TODO Complete method stub for run
						init(config);
						return null;
					}
				}, null);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else
			init(config);		        
    }
    
    public void init(Configuration config){
    	
    	if (System.getSecurityManager() == null) {
            System.setSecurityManager(new java.rmi.RMISecurityManager());
            try {
                System.out.println("Looking");
                lrm =   new net.jini.lease.LeaseRenewalManager();
                ldm = new net.jini.discovery.LookupDiscoveryManager(GROUPS, null, this);
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

    public void discovered(net.jini.discovery.DiscoveryEvent discoveryEvent) {
        try {
            org.jini.projects.thor.service.ThorService thorproxy = null;

            /*
             *Set up ChangeEventRegistrations
             * First object will be notified on any changes,updates or deletes
             * Second will only be notified of additions to the existing data set
             */
            org.jini.projects.thor.service.ChangeEventListenerImpl handleAllMods = new org.jini.projects.thor.service.ChangeEventListenerImpl();
            
            Exporter exp = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory());
            
            // Obtain an instance of Thor if one exists
            net.jini.core.lookup.ServiceRegistrar[] registrars = discoveryEvent.getRegistrars();
            Class[] classType = {org.jini.projects.thor.service.ThorService.class};
            net.jini.core.entry.Entry[] attrs = {new net.jini.lookup.entry.Name("master")};
            net.jini.core.lookup.ServiceTemplate svctmp = new net.jini.core.lookup.ServiceTemplate(null, classType, attrs);
            for (int i = 0; i < registrars.length; i++) {
                thorproxy = (org.jini.projects.thor.service.ThorService) registrars[0].lookup(svctmp);
                if (thorproxy != null)
                    break;
            }
            ProxyPreparer preparer = (ProxyPreparer) config.getEntry("org.jini.projects.thor.client", "proxyPreparer", ProxyPreparer.class, new BasicProxyPreparer());
            
            ThorService thor = (ThorService) preparer.prepareProxy(thorproxy); 
            if (thor != null) {

                //Obtain a session
                org.jini.projects.thor.service.ThorSession session = thor.getSession();

                //Get a root and then a specific branches off that root
                Branch sessionRoot = session.getRoot();
                Branch athenaBranch = sessionRoot.getBranch("Properties/Athena");
                Branch configBranch = sessionRoot.getBranch("Properties");

                //Add the change listeners to Thor
                Remote proxy =exp.export(handleAllMods); 
               EventRegistration evReg = configBranch.trackChanges(20000L, (ChangeEventListener) proxy, "");
                //EventRegistration evReg2 = configBranch.trackChanges(20000L, (ChangeEventRegistration) exp.export(handleOnlyAdditions), "");

                //Ensure the leases on the registrations are renewed
                lrm.renewFor(evReg.getLease(), 180000L, null);
               // lrm.renewFor(evReg2.getLease(), 180000L, null);
//
//                //Add a named item to a branch
//                athenaBranch.add("athena.connection.adhoctimeout", "500");
//
//                //Find an item on a given branch with a given name
//                String timeout = (String) sessionRoot.locate("Configuration/Athena", "athena.connection.adhoctimeout");
//
//                //Query a property handler for a single item
//                Branch propHandler = sessionRoot.getBranch("Properties/Athena/SALESUPP");
//                String x = (String) sessionRoot.locate("Properties/Athena/SALESUPP/athena.service.name");
//                System.out.println("Direct Access of Properties/Athena/SALESUPP/athena.service.name yields " + x);
//
//                //Query a property handler for all items - sent back as a java.util.Properties object
//                java.util.Properties props = (java.util.Properties) propHandler.getData();
//                props.list(System.out);
//                System.out.println("ROOT DATA:");
//
//                //Get the root Data Block
//                java.util.Vector vec = (java.util.Vector) sessionRoot.getDataBlock();
//                System.out.println(vec);
//
//                //Obtaining a file from from a FileHierarchy
//
//                //Get a byte array representing the file
//                byte[] barr = (byte[]) sessionRoot.locate("Software Base/Sun/J2ME/HandheldExpressBeta.zip");
//
//                //Store file to local disk
//                if (barr != null) {
//                    System.out.println("Location works saving remotely");
//                    try {
//                        java.io.FileOutputStream fout = new java.io.FileOutputStream("/home/calum/hhex.zip");
//                        fout.write(barr);
//                        fout.flush();
//                        fout.close();
//                        System.out.println("File saved!");
//                    } catch (Exception ex) {
//                        System.out.println("Err: " + ex.getMessage());
//                        ex.printStackTrace();
//                    }
//                }
//
//                /*
//                 *  Using Remote handlers -- this is transparent to the client
//                 * We only use the sub-branch remote/.... as a convenience
//                 */
//
//
//                System.out.println("Attempting to use redirected Thor hooks");
//                Branch remoteMod2 = sessionRoot.getBranch("remote/Jinistuff/Misc");
//
//                /*
//                 * Pass Thru adding
//                 * Important - this will store all instances that the add passes through to disk,
//                  *  even though the data only changes in the last instance in the chain
//                 */
//
//                remoteMod2.add("Odin Codebase", "http://e0052sts3s:8081/odin-dl.jar");
//                remoteMod2.add("Services", "Hello");
//                remoteMod2.addBranch("remoted Branch");
//                System.out.println("Object add  (redirected)");

                //Waiting for the events to be called
                while (!handleAllMods.called) {
                    Thread.sleep(2000);
                    System.out.println("Sleeping");
                }
            }
        } catch (Exception ex) {
            System.out.println("ex: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void discarded(net.jini.discovery.DiscoveryEvent discoveryEvent) {

    }

    public static void main(String[] args) {
        System.getProperties().put("java.security.policy", "/home/calum/policy.all");
        try {
			ThorClient client = new ThorClient(args);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
    }
}
