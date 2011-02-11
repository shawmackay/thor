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
 * thor.jini.org : org.jini.projects.thor.configuration
 * 
 * 
 * DynamicConfigurationImpl.java
 * Created on 14-Dec-2004
 * 
 * DynamicConfigurationImpl
 *
 */

package org.jini.projects.thor.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationFile;
import net.jini.core.entry.Entry;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.entry.Name;

import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.ConfigurationEventListenerImpl;
import org.jini.projects.thor.service.ThorService;
import org.jini.projects.thor.service.ThorSession;

/**
 * @author calum
 */
public class DynamicConfigurationImpl implements DynamicConfiguration {

    private ConfigurationFile theConfigurationFile;

    private ThorService service;

    private String host;

    private String file;

    private ThorLookup lookup;

    private String[] passthruoptions;

    private List listeners = new ArrayList();

    private class ThorLookup implements DiscoveryListener {
        LeaseRenewalManager lrm = new LeaseRenewalManager();

        private LookupDiscoveryManager ldm;

        private boolean finished = false;

        private String thorname;

        private ServiceTemplate tmp;

        private ThorService theService = null;

        public ThorLookup(String thorname, String group) throws IOException {
            System.out.println("Looking for " + thorname + " in the " + group + " group");

            this.thorname = thorname;
            Class[] clazz = new Class[] { ThorService.class };
            Entry[] attrs = new Entry[] { new Name(thorname) };
            tmp = new ServiceTemplate(null, clazz, attrs);
            ldm = new LookupDiscoveryManager(new String[] { group }, null, this);
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
                    try {
                        doRegistration(theService);
                    } catch (RemoteException e2) {
                        // TODO Handle RemoteException
                        e2.printStackTrace();
                    }
                    finished = true;
                    break;
                }
            }
            synchronized (this) {
                notifyAll();
            }
        }

        private void doRegistration(ThorService svc) throws RemoteException {
            ConfigurationEventListenerImpl handleAllMods = new ConfigurationEventListenerImpl();
            handleAllMods.setConfigurationChangeListener(new DynamicConfigurationChanger());
            Exporter exp = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory());

            // Obtain an instance of Thor if one exists

            ThorService thor = svc;
            if (thor != null) {

                // Obtain a session
                org.jini.projects.thor.service.ThorSession session = thor.getSession();

                Branch sessionRoot = session.getRoot();
                Branch thorConfigurationBranch = sessionRoot.getBranch(file);
                Remote proxy = exp.export(handleAllMods);
                try {
                    EventRegistration evReg = thorConfigurationBranch.trackChanges(20000L, (ChangeEventListener) proxy, "");
                    lrm.renewFor(evReg.getLease(), Lease.FOREVER, null);
                } catch (RemoteException e) {
                    // TODO Handle RemoteException
                    e.printStackTrace();
                } catch (LeaseDeniedException e) {
                    // TODO Handle LeaseDeniedException
                    e.printStackTrace();
                }

            }
        }

        /*
         * @see net.jini.discovery.DiscoveryListener#discarded(net.jini.discovery.DiscoveryEvent)
         */
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

    private ClassLoader requestClassLoader = null;

    private void startLookup() throws IOException {
        System.out.println("Connecting");
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());
        String name = host.substring(0, host.indexOf('.'));
        String group = host.substring(host.indexOf('.') + 1);
        lookup = new ThorLookup(name, group);
        long start = System.currentTimeMillis();
        synchronized (this) {
            try {
                while (lookup.discoveryFinished() == false)
                    wait(500);
                lookup.cleanup();
            } catch (InterruptedException e) {
                // TODO Handle InterruptedException
                e.printStackTrace();
            }
        }
        if (lookup.getServiceInstance() == null)
            throw new IOException("Thor instance could not be found");

        service = lookup.getServiceInstance();
        System.out.println(service.getClass().getName());
        System.out.println("Connection completed");

    }

    /**
     * @param options
     * @throws net.jini.config.ConfigurationException
     */
    public DynamicConfigurationImpl(String[] options) throws ConfigurationException {
        System.out.println("Calling String[] constructor");
        init(options);
    }

    /**
     * @param options
     * @throws ConfigurationException
     */
    private void init(String[] options) throws ConfigurationException {
        // TODO Complete constructor stub for DynamicConfigurationImpl
        try {
            URI u = new URI(options[0]);
            System.out.println("Host: " + u.getHost());
            System.out.println("File: " + u.getPath());
            System.out.println("Query: " + u.getQuery());
            host = u.getHost();
            file = u.getPath().substring(1);
            startLookup();

            System.out.println("Notified");
            ThorSession session = lookup.getServiceInstance().getSession();
            Branch b = (Branch) session.getRoot().getBranch(file);
            System.out.println("File part: " + file);
            Object data = b.getData();
            passthruoptions = new String[options.length - 1];
            for (int i = 1; i < options.length; i++)
                passthruoptions[i - 1] = options[i];
            if (requestClassLoader != null)
                theConfigurationFile = new ConfigurationFile(new StringReader((String) data), passthruoptions, requestClassLoader);
            else
                theConfigurationFile = new ConfigurationFile(new StringReader((String) data), passthruoptions);
            Thread.sleep(100);
        } catch (URISyntaxException e) {
            // TODO Handle URISyntaxException
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Handle InterruptedException
            e.printStackTrace();
        }
    }

    /**
     * @param options
     * @param cl
     * @throws net.jini.config.ConfigurationException
     */
    public DynamicConfigurationImpl(String[] options, ClassLoader cl) throws ConfigurationException {
        System.out.println("Calling String[] ClassLoader constructor");
        this.requestClassLoader = cl;
        init(options);
    }

    class DynamicConfigurationChanger implements ConfigurationEventDelegate {
        /*
         * @see org.jini.projects.thor.configuration.ConfigurationEventDelegate#configurationChanged(net.jini.core.event.RemoteEvent)
         */
        public void configurationChanged(RemoteEvent evt) {
            // TODO Complete method stub for configurationChanged
            ConfigurationChangeEvent evt2 = (ConfigurationChangeEvent) evt;
            // System.out.println("Event Source: " + evt2.getValue());
            ConfigurationFile oldFile = theConfigurationFile;

            int pos = 0;

            try {
                if (requestClassLoader != null)
                    theConfigurationFile = new ConfigurationFile(new StringReader((String) evt2.getReconstitutedConfig()), passthruoptions, requestClassLoader);
                else
                    theConfigurationFile = new ConfigurationFile(new StringReader((String) evt2.getReconstitutedConfig()), passthruoptions);
                for (Iterator iter = listeners.iterator(); iter.hasNext();) {
                    ListenerFilterTuple  listen = (ListenerFilterTuple) iter.next();
                    if (listen.getFilter().accept(evt2)) {
                        new Thread(new ConfigurationNotifier(listen.getListener(), evt2)).start();
                    }
                }
            } catch (Exception ex) {
                System.err.println("Restoring previous config file:" + ex.getMessage());
                ex.printStackTrace();

                theConfigurationFile = oldFile;
            }
        }
    }

    public static void main(String[] args) throws ConfigurationException {
        DynamicConfigurationImpl app = new DynamicConfigurationImpl(new String[] { "thor://thortest.production/general/thorconfig" });
        for (int i = 0; i < 10; i++) {
            System.out.println(app.getEntry("org.jini.projects.thor", "init", Boolean.class));
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ConfigurationNotifier implements Runnable {
        DynamicConfigurationListener listener;

        ConfigurationChangeEvent evt;

        public ConfigurationNotifier(DynamicConfigurationListener listener, ConfigurationChangeEvent evt) {
            this.listener = listener;
            this.evt = evt;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // TODO Auto-generated method stub
            int pos = 0;

            listener.notify(theConfigurationFile, evt);
        }
    }

    public void addConfigurationListener(DynamicConfigurationListener listener, DynamicConfigurationFilter filter) {
        listeners.add(new ListenerFilterTuple(listener, filter));
    }

    public void removeConfigurationListener(DynamicConfigurationListener toRemove) {
        ListenerFilterTuple tupletoRemove = null;
        synchronized (listeners) {
            Iterator iter = listeners.iterator();
            while (iter.hasNext()) {
                ListenerFilterTuple tuple = (ListenerFilterTuple) iter.next();
                if (tuple.getListener() == toRemove)
                    tupletoRemove = tuple;
            }
        }
        if (tupletoRemove != null)
            listeners.remove(tupletoRemove);
    }

    public Object getEntry(String component, String name, Class type) throws ConfigurationException {
        return theConfigurationFile.getEntry(component, name, type);
    }

    public Object getEntry(String component, String name, Class type, Object defaultValue) throws ConfigurationException {
        return theConfigurationFile.getEntry(component, name, type, defaultValue);
    }

    public Object getEntry(String component, String name, Class type, Object defaultValue, Object data) throws ConfigurationException {
        return theConfigurationFile.getEntry(component, name, type, defaultValue, data);
    }

    private class ListenerFilterTuple {
        DynamicConfigurationListener listener;

        DynamicConfigurationFilter filter;

        public ListenerFilterTuple(DynamicConfigurationListener listener, DynamicConfigurationFilter filter) {
            this.listener = listener;
            this.filter = filter;
        }

        /**
         * @return Returns the filter.
         */
        public DynamicConfigurationFilter getFilter() {
            return filter;
        }

        /**
         * @return Returns the listener.
         */
        public DynamicConfigurationListener getListener() {
            return listener;
        }
    }
}
