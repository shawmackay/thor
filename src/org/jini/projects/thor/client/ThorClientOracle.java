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

import net.jini.core.event.EventRegistration;

import org.jini.projects.thor.handlers.Branch;

/**
 * @author  calum
 * @version
 */


public class ThorClientOracle implements net.jini.discovery.DiscoveryListener {
    private net.jini.discovery.LookupDiscoveryManager ldm;
    private net.jini.lease.LeaseRenewalManager lrm = new net.jini.lease.LeaseRenewalManager();
    private final String[] GROUPS = {"debug"};
    private boolean finished = false;


    /** Creates new ThorClient */
    public ThorClientOracle() {

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new java.rmi.RMISecurityManager());
            try {
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
            System.exit(0);
        }
    }

    public void discovered(net.jini.discovery.DiscoveryEvent discoveryEvent) {
        try {
            org.jini.projects.thor.service.ChangeEventListenerImpl ceh = new org.jini.projects.thor.service.ChangeEventListenerImpl();
            net.jini.core.lookup.ServiceRegistrar[] registrars = discoveryEvent.getRegistrars();
            Class[] classType = {org.jini.projects.thor.service.ThorService.class};
            net.jini.core.entry.Entry[] attrs = {new net.jini.lookup.entry.Name("master")};
            net.jini.core.lookup.ServiceTemplate svctmp = new net.jini.core.lookup.ServiceTemplate(null, classType, attrs);
            org.jini.projects.thor.service.ThorService thor = (org.jini.projects.thor.service.ThorService) registrars[0].lookup(svctmp);
            if (thor == null) {
                finished = true;
                return;
            }

            org.jini.projects.thor.service.ThorSession session = thor.getSession();
            Branch sessionRoot = session.getRoot();
            Branch branch2 = sessionRoot.getBranch("Configuration/Athena");
            System.out.println("adding an event listener");
            Branch configBranch = sessionRoot.getBranch("Configuration");
            EventRegistration evReg = configBranch.trackChanges(20000L, ceh, "");
            System.out.println("Lease obtained: " + evReg.getLease().getExpiration());
            lrm.renewFor(evReg.getLease(), 180000L, null);
            branch2.add("athena.connection.adhoctimeout", "500");
            System.out.println("Locating timeout");
            String timeout = (String) sessionRoot.locate("Configuration/Athena", "athena.connection.adhoctimeout");
            System.out.println("The timeout stored in Thor is: " + timeout);
            System.out.println("getting properties");
            Branch propHandler = sessionRoot.getBranch("Properties/SALESUPR");
            String x = (String) sessionRoot.locate("Properties/SALESUPR/athena.service.name");
            System.out.println("Direct Access of Properties/SALESUPR/athena.service.name yields " + x);
            java.util.Properties props = (java.util.Properties) propHandler.getData();
            props.list(System.out);
            System.out.println("ROOT DATA:");
            java.util.Vector vec = (java.util.Vector) sessionRoot.getDataBlock();
            System.out.println(vec);
            byte[] barr = (byte[]) sessionRoot.locate("Software Base/Sun/J2ME/HandheldExpressBeta.zip");
            if (barr != null) {
                System.out.println("Location works saving remotely");
                try {
                    java.io.FileOutputStream fout = new java.io.FileOutputStream("d:\\hhex.zip");
                    fout.write(barr);
                    fout.flush();
                    fout.close();
                    System.out.println("File saved!");
                } catch (Exception ex) {
                    System.out.println("Err: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.out.println("Attempting to use redirected Thor hooks");
            //timeout = ((String) sessionRoot.locate("remote/tag/Systems/e0052sts3s/Operating System")).toString();
            //System.out.println("TImeout (redirected): " + timeout);

            //Branch remoteBranch = sessionRoot.getBranch("remote");
            //ClientModifier remoteMod = remoteBranch.getBranch("tag");
            Branch remoteMod2 = sessionRoot.getBranch("remote/Jinistuff/Misc");
            //Branch remoteMod2 = remoteBranch.getBranch("Jinistuff/Misc");
            remoteMod2.add("Odin Codebase", "http://e0052sts3s:8081/odin-dl.jar");
            remoteMod2.add("Services", "Hello");
            remoteMod2.addBranch("remoted Branch");
            System.out.println("Object add  (redirected)");
            while (!ceh.called) {
                Thread.sleep(2000);
                System.out.println("Sleeping");
            }
            finished = true;
        } catch (Exception ex) {
            System.out.println("ex: " + ex.getMessage());
            ex.printStackTrace();
        }
        finished = true;
    }

    public void discarded(net.jini.discovery.DiscoveryEvent discoveryEvent) {

    }

    public static void main(String[] args) {
        System.getProperties().put("java.security.policy", "/home/calum/policy.all");
        ThorClientOracle client = new ThorClientOracle();
    }

}
