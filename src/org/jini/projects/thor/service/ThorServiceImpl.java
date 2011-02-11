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
 * ThorService.java
 * 
 * Created on 17 September 2001, 13:16
 */

package org.jini.projects.thor.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationID;
import java.rmi.server.ServerNotActiveException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.export.ServerContext;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.io.context.ClientSubject;
import net.jini.lookup.JoinManager;
import net.jini.lookup.entry.ServiceInfo;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.factory.JComponentFactory;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.ServerProxyTrust;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ExporterManager;
import org.jini.projects.thor.handlers.HierarchyHandler;
import org.jini.projects.thor.handlers.InternalHierarchy;
import org.jini.projects.thor.service.constrainable.ThorServiceProxy;
import org.jini.projects.thor.service.leasing.ChangeLandlord;
import org.jini.projects.thor.service.store.BackendLoader;
import org.jini.projects.thor.service.store.Compactor;
import org.jini.projects.thor.service.store.StorageFactory;
import org.jini.projects.thor.service.store.xml.XMLBackendLoader;
import org.jini.projects.thor.service.store.xml.XMLStore;
import org.jini.projects.thor.service.ui.ThorPanel;
import org.jini.projects.thor.service.ui.ThorUIFact;



import com.sun.jini.config.Config;
import com.sun.jini.proxy.BasicProxyTrustVerifier;
import com.sun.jini.start.LifeCycle;
import com.sun.jini.start.ServiceProxyAccessor;

/**
 * @author calum
 * @version
 */

public class ThorServiceImpl implements ThorService, net.jini.admin.Administrable, ReferentUuid, ServerProxyTrust, ServiceProxyAccessor{
    private String componentName = "org.jini.projects.thor";

    private String[] groups;

    private Uuid serviceUuid;

    private ServiceID serviceID;

    private ThorServiceProxy outsideproxy;

    private LifeCycle lifeCycle = null;

    protected LoginContext loginContext = null;

    private ActivationID activationID = null;

    private JoinManager jm;

    private LookupDiscoveryManager ldm;

    private RemoteTerminationThread tt = new RemoteTerminationThread();

    Logger log = Logger.getLogger("org.jini.projects.thor.service");

    /*
     * @see net.jini.id.ReferentUuid#getReferentUuid()
     */
    public Uuid getReferentUuid() {
        // TODO Complete method stub for getReferentUuid
        return serviceUuid;
    }

    /*
     * @see net.jini.security.proxytrust.ServerProxyTrust#getProxyVerifier()
     */
    public TrustVerifier getProxyVerifier() throws RemoteException {

        // return new ThorProxyVerifier(this, this.getReferentUuid());
        return new BasicProxyTrustVerifier(this);
    }

    private boolean term = false;

    public static int evtSeqNum = 0;;

    // public static InternalHierarchy ROOT = new InternalHierarchy();
    // private EventDelivery evDel = new EventDelivery(ROOT);
    public static ChangeLandlord LANDLORD;

    public StorageFactory sf;

    private transient ThorAdmin admin;

    private transient ThorAdmin remoteAdmin;

    static Random IDGen = new Random();
    static {
        try {
            LANDLORD = new ChangeLandlord();
        } catch (Exception ex) {
            System.err.println("Err: " + ex.getMessage());
        }
    }

    public static long getRandomID() {
        // TODO: Need to ensure that ID's are not generated twice

        if (IDGen == null)
            IDGen = new Random();
        return IDGen.nextLong();
    }

    String name = null;

    // private
    // org.jini.projects.thor.service.ThorServiceImpl.RemoteTerminationThread tt
    // = new
    // org.jini.projects.thor.service.ThorServiceImpl.RemoteTerminationThread();

    /** Creates new ThorService */

    private void setThorName(String name) {

        this.name = name;
        ChangeMonitor.name = name;
    }

    // JoinManager jm;
    // public void setJM(JoinManager jm){
    // this.jm = jm;
    // }
    //     
    // LookupDiscoveryManager ldm;
    // public void setLDM(LookupDiscoveryManager ldm){
    // this.ldm = ldm;
    // }

    /**
     * Creates a non-activatable Thor instance
     */

    ThorServiceImpl(String[] args, LifeCycle lc) throws Exception {
        lifeCycle = lc;
        init(args);
    }

    ThorServiceImpl(ActivationID activationID, MarshalledObject data) throws Exception {
        this.activationID = activationID;
        try {

            init((String[]) data.get());
        } catch (Exception ex) {
            // Cleanup
            // Iniit Failed
        }
    }

    private void init(String[] args) throws Exception {
        final Configuration config = ConfigurationProvider.getInstance(args);
      

        String codebase = (String) Config.getNonNullEntry(config, componentName, "codebase", String.class);
        
        System.getProperties().put("java.rmi.server.codebase", codebase);
        loginContext = (LoginContext) config.getEntry(componentName, "loginContext", LoginContext.class);
        if (System.getSecurityManager() == null){        	
        	System.setSecurityManager(new java.rmi.RMISecurityManager());
        }
            
        Logger l = Logger.getLogger("org.jini.projects.thor");
        l.setUseParentHandlers(false);
        l.addHandler(new ConsoleHandler());
        Handler[] handlers = l.getHandlers();

        String logLevel = (String) config.getEntry("org.jini.projects.thor", "logLevel", String.class, "INFO");
        Level level = Level.parse(logLevel);
        l.setLevel(level);
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].setFormatter(new LogFormatter());
            handlers[i].setLevel(level);
        }
        if (loginContext == null)
            doInit(config);
        else
            initWithLogin(config, loginContext);

    }

    private void doInit(final Configuration config) throws Exception {
        DefaultExporterManager.loadManager("default", "config/exportmgr.config");
        this.name = (String) Config.getNonNullEntry(config, componentName, "name", String.class);
        this.groups = (String[]) Config.getNonNullEntry(config, componentName, "groups", String[].class);
        
        log.finer("Adding shutdown hooks");
        Runtime.getRuntime().addShutdownHook(new ShutdownCtrlCThread());
        outsideproxy = (ThorServiceProxy) DefaultExporterManager.getManager().exportProxy(this, "Service", UuidFactory.generate());
        connectToFederation(config);
        Boolean binit = (Boolean) config.getEntry(componentName, "init", Boolean.class, Boolean.FALSE);
        boolean init = binit.booleanValue();
       
        if (init) {
           
           // log.info("Building tree from XML file: " + xmlfile);
            //new XMLBackendLoader().initialise(xmlfile);
        } else {
            log.info("Loading existing tree");
           
            // ThorServiceImpl.ROOT.displayTree(0);
        }
        // for(int i=0, i<names.
        Thread.sleep(60000);
        synchronized (this) {
            wait(0);
        }
        // thor.terminate();
    }

    public ThorServiceImpl(String name, ThorAdminProxy admin, StorageFactory sf) throws RemoteException {
        this.name = name;
        ChangeMonitor.name = name;
        this.admin = admin;
        this.sf = sf;
    }

    public void setAdmin(ThorAdmin t) {
        this.admin = t;
    }

    public java.lang.Object getAdmin() throws RemoteException {
        if (remoteAdmin == null) {
            ExporterManager exp = DefaultExporterManager.getManager("default");

            // exporter = new
            // BasicJeriExporter(HttpServerEndpoint.getInstance(0), new
            // BasicILFactory());
            // Remote r = exporter.export(admin);
            // ThorAdmin remoteAdmin = AdminProxy.create((ThorAdmin) r,
            // UuidFactory.generate());
            this.remoteAdmin = (ThorAdmin) exp.exportProxy(admin, "Standard", UuidFactory.generate());
            ;
        }
        // System.out.println("Getting admin object");
        return this.remoteAdmin;
    }

    private void initWithLogin(final Configuration config, LoginContext context) throws Exception {
        loginContext.login();
        try {
            Subject.doAsPrivileged(loginContext.getSubject(), new PrivilegedExceptionAction() {
                /*
                 * @see java.security.PrivilegedExceptionAction#run()
                 */
                public Object run() throws Exception {
                    // TODO Complete method stub for run

                    doInit(config);
                    return null;
                }
            }, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    

    private ThorSession session = null;

    private Exporter exp;

    private Exporter exporter;

    private Remote sessref;

    private ThorSession_Impl impl;

    public ThorSession getSession() throws RemoteException {
        ClientSubject cs = null;
        try {
            cs = (ClientSubject) ServerContext.getServerContextElement(ClientSubject.class);
        } catch (ServerNotActiveException e) {
            // TODO Handle ServerNotActiveException

            e.printStackTrace();
        }
        log.finest("Checking client");
        Subject client = null;
        if (cs != null) {

            client = cs.getClientSubject();
            Set s = client.getPrincipals();
            log.finer("An authenticated client has connected");

        }
        log.finest("Client checked");
        if (this.session == null) {
            org.jini.glyph.chalice.ExporterManager exp = org.jini.glyph.chalice.DefaultExporterManager.getManager("default");

            impl = new ThorSession_Impl();
            this.session = (ThorSession) exp.exportProxy(impl, "Standard", UuidFactory.generate());

            // this.session = ThorSessionProxy.create((ThorSession) sessref,
            // UuidFactory.generate());
            // System.out.println("built new session");
        }
        return session;
    }

    public EventRegistration trackChanges(long duration, RemoteEventListener rel, String changeOn) throws RemoteException {
        System.out.println("This doesn't work any more");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.jini.start.ServiceProxyAccessor#getServiceProxy()
     */
    public Object getServiceProxy() throws RemoteException {
        // TODO Auto-generated method stub
        log.finer("Getting outside proxy:" + outsideproxy.getClass().getName());
        return outsideproxy;
    }

    public void connectToFederation(Configuration config) {
        try {
            Uuid proxyID = UuidFactory.generate();
            serviceID = new ServiceID(proxyID.getMostSignificantBits(), proxyID.getLeastSignificantBits());
            Entry[] details = (Entry[]) Config.getNonNullEntry(config, componentName, "initialLookupAttributes", Entry[].class);
            HashMap options = new HashMap();
            File f = new File("data");
            if(!f.exists())
                f.mkdir();
            options.put("filename", "data/" + name + ".backstore");
            options.put("fallbackLoader", new XMLBackendLoader());
            String xmlfile = (String) config.getEntry(componentName, "xmlfile", String.class, "");
            options.put("fallbackSource", xmlfile);
            sf = new StorageFactory(options);

            ServiceInfo info = new ServiceInfo("Thor", "thor.jini.org", null, "0.91c", null, null);

            UIDescriptor thorDesc = new UIDescriptor(ThorPanel.ROLE, ThorUIFact.TOOLKIT, new HashSet(), new java.rmi.MarshalledObject(new ThorUIFact()));
            thorDesc.attributes = new java.util.HashSet();
            thorDesc.attributes.add(new net.jini.lookup.ui.attribute.UIFactoryTypes(java.util.Collections.singleton(JComponentFactory.TYPE_NAME)));
            Object o = StorageFactory.getBackend().getBranch("ROOT");
            if (o != null) {                
                Compactor ctr = new Compactor(StorageFactory.getBackend().getBranch("ROOT"));
                ctr.display();
            }
            // System.exit(0);
            // System.out.println("Thorpanel Role: " + ThorPanel.ROLE);
            // UIDescriptor thorDesc = new UIDescriptor(ThorPanel.ROLE,
            // ThorUIFact.TOOLKIT, new HashSet(), new
            // java.rmi.MarshalledObject(new ThorUIFact()));
            // thorDesc.attributes = new java.util.HashSet();
            // thorDesc.attributes.add(new
            // net.jini.lookup.ui.attribute.UIFactoryTypes(java.util.Collections.singleton(JComponentFactory.TYPE_NAME)));
            // Entry[] details = new Entry[]{svcname, info, new
            // ThorServiceType(), thorDesc};
            //			
            log.info("Registering...");
            for (int i = 0; i < this.groups.length; i++)
                log.info("...in Group " + i + ": " + this.groups[i]);
            ldm = new net.jini.discovery.LookupDiscoveryManager(this.groups, null, null, config);
            File file = new File("data/serviceID_" + this.name + ".ser");
            Entry[] baseAttributes = new Entry[] { info, thorDesc };
            Entry[] attrs = new Entry[details.length + baseAttributes.length];

            System.arraycopy(baseAttributes, 0, attrs, 0, baseAttributes.length);
            System.arraycopy(details, 0, attrs, baseAttributes.length, details.length);
            this.name = (String) config.getEntry(componentName, "name", String.class);

            this.admin = null;
            ThorService svc = (ThorService) getServiceProxy();
            if (svc == null)
                log.severe("Why is my service null?????");
            if (file.exists()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("data/serviceID_" + this.name + ".ser")));
                    serviceID = (net.jini.core.lookup.ServiceID) ois.readObject();
                    ois.close();
                    log.info("Rejoining....");
                    jm = new net.jini.lookup.JoinManager(svc, attrs, serviceID, ldm, null, config);
                } catch (Exception ex) {
                    log.severe("Err: Cannot read ServiceID log");
                    ex.printStackTrace();
                }
            } else {
                log.info("Joining as new Service");
                try {
                    java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("data/serviceID_" + this.name + ".ser")));
                    oos.writeObject(serviceID);
                    oos.flush();
                    oos.close();
                } catch (Exception ex) {
                    log.severe("Err: Cannot save ServiceID");
                }
                jm = new net.jini.lookup.JoinManager(svc, attrs, serviceID, ldm, null, config);
                tt.start();
            }
            setAdmin(new ThorAdminProxy(jm, ldm));
        } catch (Exception ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void buildEmptyTree() {
        HierarchyHandler h = new InternalHierarchy();
        StorageFactory.getBackend().insertBranch(null, h);
        StorageFactory.getBackend().store();
    }

    private void loadinitialtree() {
        // try {
        // java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new
        // java.io.BufferedInputStream(new
        // java.io.FileInputStream("data/thortree_" + this.name.trim() +
        // ".ser")));
        // ThorServiceImpl.ROOT = (InternalHierarchy) ois.readObject();
        // System.out.println("Thor tree read from file!");
        // ois.close();
        // } catch (Exception ex) {
        // System.out.println("Err: " + ex.getMessage());
        // ex.printStackTrace();
        // System.out.println("Defaulting to an empty tree");
        // buildEmptyTree();
        // }
        // XMLStore store = new XMLStore();
        // store.storeRoot(ThorServiceImpl.ROOT);
        // try {
        // Thread.sleep(5000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // sf.StoreTree(ThorServiceImpl.ROOT);
    }

    private class ShutdownCtrlCThread extends Thread {
        public void run() {
            System.out.println("Cleaning Up....");
            if (lifeCycle != null)
                lifeCycle.unregister(this);
            jm.terminate();
            ldm.terminate();
            System.out.println("Terminating.....");
        }
    }

    private class RemoteTerminationThread extends Thread {
        public void run() {
            while (term == false) {
                try {
                    Thread.sleep(5000);
                } catch (Exception ex) {
                }
            }
            System.exit(0);
        }
    }

}
