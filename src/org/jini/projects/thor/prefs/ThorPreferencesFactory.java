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
 * ThorPreferencesFactory.java
 *
 * Created on May 17, 2002, 2:04 PM
 */

package org.jini.projects.thor.prefs;


import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.entry.Name;

import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ThorService;

/**
 *  Preferences Factory for interfacing the prefs API to Thor
 * @author  calum
 */
public class ThorPreferencesFactory implements java.util.prefs.PreferencesFactory, DiscoveryListener {
    private static LookupDiscoveryManager ldm;
    private final int NOTLOOKING = 1;
    private final int DISCOVERED = 2;
    private final int FOUND = 3;
    private final int NOTFOUND = 4;
    private int status = NOTLOOKING;
    private Branch userhandler = null;
    private Branch Systemhandler = null;

    private ThorService thor = null;
    private static String[] GROUPS = null;
    private static String THORNAME = null;
    private static String HOSTNAME;


    static {
        try {
            String host = java.net.InetAddress.getLocalHost().getHostName();
            host = host.substring(0, host.indexOf("."));
            System.out.println("host: " + host);
            HOSTNAME = host.toLowerCase();
            if(System.getProperty("thor.prefs.group")==null)
                throw new RuntimeException("Please specify the Thor group for preferences");
            if(System.getProperty("thor.prefs.name")==null)
                throw new RuntimeException("Please specify the Thor group for preferences");
            GROUPS= new String[] {System.getProperty("thor.prefs.group")};
            THORNAME = System.getProperty("thor.prefs.name");
        } catch (Exception ex) {
            System.out.println("Cannot get Hostname");
            ex.printStackTrace();
        }

    }

    /** Creates a new instance of ThorPreferencesFactory.
     * Sets up a security manager if required and begins discovery
     */
    public ThorPreferencesFactory() {

        try {
            if (System.getSecurityManager() == null) {

                System.setSecurityManager(new java.rmi.RMISecurityManager());
            }
            try {

                ldm = new LookupDiscoveryManager(GROUPS, null, this);
            } catch (java.io.IOException ioex) {
                System.out.println("Err in lookup: " + ioex.getMessage());
                ioex.printStackTrace();
            }
            while (status == NOTLOOKING || status == DISCOVERED) {
                Thread.sleep(250);

            }
        } catch (InterruptedException interruptEx) {
            System.out.println("Interrupted in wait call");
        }

    }

    /**
     * Obtains the system root,
     */
    public java.util.prefs.Preferences systemRoot() {
        try {
            if (thor != null) {
                Branch chandle = thor.getSession().getRoot().getBranch("Prefs/Systems/" + HOSTNAME);
                if (chandle == null) {
                    thor.getSession().getRoot().getBranch("Prefs/Systems").addBranch(HOSTNAME);
                    chandle = thor.getSession().getRoot().getBranch("Prefs/Systems/" + HOSTNAME);
                }
                return new ThorPrefsSpi(chandle, null, "");
            }

        } catch (Exception ex) {
            System.out.println("Error getting System Root: " + ex.getMessage());
        }
        System.out.println("returning null");
        return null;
    }

    public java.util.prefs.Preferences userRoot() {

        try {
            System.out.println("getting root");
            if (thor != null) {
                String user = System.getProperty("user.name");
                Branch chandle = thor.getSession().getRoot().getBranch("Prefs/User/" + user);
                if (chandle == null) {
                    System.out.println("The handle is null!");
                    thor.getSession().getRoot().getBranch("Prefs/User").addBranch(user);
                    chandle = thor.getSession().getRoot().getBranch("Prefs/User/" + user);
                }
                return new ThorPrefsSpi(chandle, null, "");
            }
        } catch (Exception ex) {
            System.out.println("Error getting user Root: " + ex.getMessage());
        }
        return null;
    }

    public void discarded(net.jini.discovery.DiscoveryEvent discoveryEvent) {
        System.out.println("Discarded");
    }

    public void discovered(net.jini.discovery.DiscoveryEvent discoveryEvent) {
        try {
            status = DISCOVERED;
            ServiceRegistrar[] registrars = discoveryEvent.getRegistrars();
            
            Entry[] attr = new Entry[]{new Name(THORNAME)};
            Class[] clazz = new Class[]{org.jini.projects.thor.service.ThorService.class};

            if (status != FOUND) {

                for (int i = 0; i < registrars.length; i++) {

                    ServiceRegistrar reg = registrars[i];

                    thor = (ThorService) reg.lookup(new ServiceTemplate(null, clazz, attr));
                    if (thor != null) {

                        break;
                    }
                }
                // If we exit the loop normally
                if (thor == null)
                    status = NOTFOUND;
                else
                    status = FOUND;
            }

        } catch (Exception ex) {
            System.out.println("Err in discovered() : " + ex.getMessage());
            ex.printStackTrace();
        }

    }
}
