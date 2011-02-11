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
 * thor.jini.org : org.jini.projects.thor.handlers
 * 
 * 
 * ConfigurationFileHandler.java
 * Created on 14-Dec-2004
 * 
 * ConfigurationFileHandler
 *
 */

package org.jini.projects.thor.handlers;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.jini.projects.thor.configuration.ConfigurationChangeEvent;
import org.jini.projects.thor.handlers.support.ConfigurationComponent;
import org.jini.projects.thor.handlers.support.ConfigurationFileScan;
import org.jini.projects.thor.handlers.support.ConfigurationLoader;
import org.jini.projects.thor.service.ChangeEvent;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.EventDelivery;
import org.jini.projects.thor.service.EventNotifier;
import org.jini.projects.thor.service.NamedItem;
import org.jini.projects.thor.service.Startup;
import org.jini.projects.thor.service.ThorServiceImpl;
import org.jini.projects.thor.service.leasing.LeaseHandler;
import org.jini.projects.thor.service.leasing.LeaseHolder;
import org.jini.projects.thor.service.store.BranchIdentifier;
import org.jini.projects.thor.service.store.StorageFactory;

/**
 * @author calum
 */
public class ConfigurationFileHandler implements HierarchyHandler, Serializable, LeaseHandler {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256718494266896951L;

    private transient Logger log = Logger.getLogger("org.jini.projects.thor.handlers");

    private int currentsize = 0;

    private String description = "";

    private HashMap entity = new HashMap();

    // private transient EventDelivery evDel = new EventDelivery(this);
    private String ID;

    private transient HashMap listeners = new HashMap();

    private transient HashMap ids = new HashMap();

    private HashMap names = new HashMap();

    private ConfigurationLoader cl;

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#locate(java.lang.String)
     */

    private String configurationFile;

    public ConfigurationFileHandler(String configurationFile) {
        this.configurationFile = configurationFile;
        System.out.println("Loading config:" + configurationFile);
        cl = new ConfigurationLoader(this.configurationFile);
        try {
            cl.parse();
            ConfigurationFileScan scan = cl.getScan();

            ListHandler h = new ListHandler(scan.getImportsSet());
            System.out.println("Adding imports");
            addPrebuiltBranch("imports", h);
            Map m = scan.getComponents();
            for (Iterator iter = m.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entr = (Map.Entry) iter.next();
                System.out.println("Adding Branch: " + entr.getKey());
                ConfigurationComponent cc = (ConfigurationComponent) entr.getValue();
                ConfigurationComponentHandler cch = new ConfigurationComponentHandler(cc, this);
                addPrebuiltBranch((String) entr.getKey(), cch);
            }
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
        }

    }

    public Object locate(String path) {
        StringTokenizer strtok = new StringTokenizer(path, "/");
        // System.out.println("Path:" + path);
        // System.out.println(strtok.countTokens());
        if (strtok.countTokens() == 1) {
            Object ret = entity.get(path);
            if (ret instanceof BranchIdentifier) {
                String ID = ((BranchIdentifier) ret).getBranchID();
                ret = StorageFactory.getBackend().getBranch(ID);
            }
            return ret;
        } else {
            String levelname = strtok.nextToken();
            if (entity.get(levelname) != null) {
                BranchIdentifier bID = (BranchIdentifier) entity.get(levelname);
                HierarchyHandler lookinhere = StorageFactory.getBackend().getBranch(bID.getBranchID());
                String newloc = path.substring(levelname.length() + 1);
                return lookinhere.locate(newloc);
            }
        }
        return null;
    }

    public Object locate(String path, int index) {
        StringTokenizer strtok = new StringTokenizer(path, "/");
        if (strtok.countTokens() == 0) {
            if (index <= names.size()) {
                return entity.get(names.get(new Integer(index)));
            }
        } else {
            String levelname = strtok.nextToken();
            if (entity.get(levelname) != null) {
                HierarchyHandler lookinhere = (HierarchyHandler) entity.get(levelname);
                String newloc = path.substring(levelname.length() + 1);
                return lookinhere.locate(newloc, index);
            }
        }
        return null;
    }

    public Object locate(String path, Object index) {
        StringTokenizer strtok = new StringTokenizer(path, "/");
        if (strtok.countTokens() == 1) {
            String levelname = strtok.nextToken();
            if (entity.get(levelname) != null) {
                HierarchyHandler lookinhere = (HierarchyHandler) entity.get(levelname);
                for (int i = 0; i < lookinhere.getNumChildren(); i++) {
                    if (lookinhere.getChild(i, true).equals(index)) {
                        Object ret = lookinhere.getChild(i, true);
                        log.finest(ret.getClass().getName());
                        return ret;
                    }
                }
            }
        } else {
            String levelname = strtok.nextToken();
            if (entity.get(levelname) != null) {
                HierarchyHandler lookinhere = (HierarchyHandler) entity.get(levelname);
                String newloc = path.substring(levelname.length() + 1);
                Object ret = lookinhere.locate(newloc, index);
                return ret;
            }
        }
        return null;
    }

    public Object locate(String path, String index) {
        checkLog();
        StringTokenizer strtok = new StringTokenizer(path, "/");
        if (strtok.countTokens() == 1) {
            String levelname = strtok.nextToken();
            if (entity.get(levelname) != null) {
                HierarchyHandler lookinhere = (HierarchyHandler) entity.get(levelname);
                // System.out.println("Class: " +
                // lookinhere.getClass().getName());
                // System.out.println("path: " + path);
                try {
                    return lookinhere.getChild(index, true);
                } catch (Exception ex) {
                    log.severe("Locate Err: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            String levelname = strtok.nextToken();
            for (int i = 0; i < names.size(); i++) {
                if (entity.get(levelname) != null) {
                    HierarchyHandler lookinhere = (HierarchyHandler) entity.get(levelname);
                    String newloc = path.substring(levelname.length() + 1);
                    Object ret = lookinhere.locate(newloc, index);
                    return ret;
                }
            }
        }
        return null;
    }

    
        public Object getChild(int idx) {
        Object obj = entity.get(names.get(new Integer(idx)));
        if (obj instanceof BranchIdentifier) {
            String ID = ((BranchIdentifier) obj).getBranchID();
            return StorageFactory.getBackend().getBranch(ID);

        }
        return obj;
    }

    public Object getChild(int Index, boolean noData) {
        Object obj = entity.get(names.get(new Integer(Index)));
        if (obj instanceof BranchIdentifier) {
            String ID = ((BranchIdentifier) obj).getBranchID();
            return StorageFactory.getBackend().getBranch(ID);

        }
        return obj;
    }

    public Object getChild(String name, boolean noData) {
        Object obj = entity.get(name);
        if (obj instanceof BranchIdentifier) {
            String ID = ((BranchIdentifier) obj).getBranchID();
            return StorageFactory.getBackend().getBranch(ID);

        }
        return obj;
    }
    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getBranch(java.lang.String)
     */
    public HierarchyHandler getBranch(String branchName) {
        checkLog();
        try {
            System.out.println("Getting branch: " + branchName);
            Object x = locate(branchName);
            return (HierarchyHandler) x;
        } catch (ClassCastException ex) {
            // Not a branch so return null
            return null;
        } catch (Exception ex) {
            log.severe("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getNumChildren()
     */
    public int getNumChildren() {
        return names.size();
    }

    public String getName(int idx) {
        return (String) names.get(new Integer(idx));
    }
    

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getDataBlock()
     */
    public Object getDataBlock() {
        java.util.Vector vec = new java.util.Vector();
        for (int i = 0; i < currentsize; i++) {
            Object ob = entity.get(names.get(new Integer(i)));
            Object viewOb;
            if (ob instanceof BranchIdentifier) {
                String ID = ((BranchIdentifier) ob).getBranchID();
                viewOb = StorageFactory.getBackend().getBranch(ID);
            } else {
                viewOb = ob;
            }

            if (!(viewOb instanceof HierarchyHandler)) {
                vec.add(new NamedItem((String) names.get(new Integer(i)), viewOb));
            } else {

                String tag = "";
                if (viewOb instanceof InternalHierarchy) {
                    tag = "<<branch>>";
                }
                if (viewOb instanceof LinkHierarchy) {
                    tag = "<<linkbranch>>";
                }

                if (viewOb instanceof PropertyHandler) {
                    tag = "<<properties>>";
                }
                if (viewOb instanceof FileHierarchy) {
                    tag = "<<dir>>";
                }
                if (viewOb instanceof RemoteThorHandler) {
                    tag = "<<remotebranch>>";
                }
                if (viewOb instanceof ConfigurationComponentHandler) {
              
                    tag = "<<configcomponentbranch>>";
                }
                if (viewOb instanceof ListHandler) {
              
                    tag = "<<listbranch>>";
                }

                vec.add(new NamedItem((String) names.get(new Integer(i)), tag));

            }
        }

        return vec;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#setDataBlock(java.lang.Object)
     */
    public void setDataBlock(Object obj) {
        // TODO Complete method stub for setDataBlock

    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#setDescription(java.lang.String)
     */
    public void setDescription(String newName) {
        // TODO Complete method stub for setDescription

    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getDescription()
     */
    public String getDescription() {
        // TODO Complete method stub for getDescription
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#add(java.lang.String,
     *           java.lang.Object)
     */
    public void add(String name, Object obj) {
        checkLog();
        if (!entity.containsKey(name)) {
            log.finest("Adding name ");
            names.put(new Integer(currentsize++), name);
        }
        if (!names.containsValue(name)) {
            names.put(new Integer(currentsize++), name);
        }
        // currentsize++;
        if (obj instanceof HierarchyHandler) {
            StorageFactory.getBackend().insertBranch(this, (HierarchyHandler) obj);
            entity.put(name, new BranchIdentifier(((HierarchyHandler) obj).getBranchID()));
        } else {

            log.finest("Adding " + obj.getClass().getName() + " as " + name);
            entity.put(name, obj);
        }

        fireIfListenersOn(name, null, name, obj.toString(), reconstitute(), org.jini.projects.thor.service.ChangeConstants.CREATE);

        StorageFactory.getBackend().store(this);
    }

    private void addPrebuiltBranch(String name, HierarchyHandler branch) {
        checkLog();
        StorageFactory.getBackend().insertBranch(this, branch);
        log.finest("Adding " + name + " with ID " + branch.getBranchID());
        this.add(name, new BranchIdentifier(branch.getBranchID()));
        StorageFactory.getBackend().store(this);

        fireIfListenersOn(name, name, "", "<<BRANCH>>", reconstitute(), org.jini.projects.thor.service.ChangeConstants.CREATE);
    }

    public void addBranch(String name) {
        checkLog();
        ConfigurationComponent cc = new ConfigurationComponent(name);
        cl.getScan().addComponent(cc);
        ConfigurationComponentHandler cch = new ConfigurationComponentHandler(cc, this);
        
       addPrebuiltBranch(name, cch);
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addBranch(java.lang.String,
     *           int, java.util.HashMap)
     */
    public void addBranch(String name, int type, HashMap attributes) {
        // TODO Complete method stub for addBranch

    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#overwrite(java.lang.String,
     *           java.lang.Object)
     */
    public void overwrite(String name, Object newObj) {
        // TODO Complete method stub for overwrite

    }

    public void removeAll() {
        Collection c = entity.values();
        Object[] obs = c.toArray();
        for (int i = 0; i < obs.length; i++)
            removeChild(obs[i]);
    }

    public void removeChild(Object current) {
        synchronized (this) {
            Object lookup = null;
            if (entity.containsValue(current)) {
                java.util.Set entryset = entity.entrySet();
                java.util.Iterator iter = entryset.iterator();
                while (iter.hasNext()) {
                    java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
                    if (entry.getValue().equals(current)) {
                        lookup = entry.getKey();
                    }
                }
                if (lookup != null) {
                    entity.remove(lookup);
                    names.remove(lookup);
                    fireIfListenersOn((String) lookup,  "", (String) lookup, "", reconstitute(), org.jini.projects.thor.service.ChangeConstants.DELETE);
                }
            }
        }
    }

    public void removeChild(String name) {
        synchronized (this) {
            boolean removed = false;
            int removedpos = -1;
            checkLog();
            log.fine("Removing " + name + " from names");
            try {
                if (names.containsValue(name)) {
                    for (int i = 0; i < currentsize; i++) {
                        String listedname = (String) names.get(new Integer(i));
                        if (listedname != null)
                            if (listedname.equals(name)) {
                                log.finest("Found Name @ " + i);
                                if(!name.equals("imports")){
                                    System.out.println("Removing component");
                                    cl.getScan().removeComponent(name);
                                }
                                names.remove(new Integer(i));
                                removedpos = i;
                                removed = true;
                                break;
                            }
                    }
                    if (removed) {
                        for (int i = removedpos + 1; i < currentsize; i++) {
                            names.put(new Integer(i - 1), names.get(new Integer(i)));
                        }
                    }
                    currentsize--;
                }
                log.fine("Removing " + name + " from entities");
                entity.remove(name);

                fireIfListenersOn(name, name, "", "", reconstitute(), org.jini.projects.thor.service.ChangeConstants.DELETE);

            } catch (Exception ex) {
                log.severe("Err :  " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getData()
     */
    public Object getData() {
        // TODO Complete method stub for getData
        return reconstitute();
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addChangeEventListener(org.jini.projects.thor.service.ChangeEventListener,
     *           java.lang.String)
     */
    public EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
        String eventDeliveryKey = null;
        checkLog();
        try {
          ChangeEventListener listenProxy = rel;
          if (name == null | name.equals(""))
                name = "<default>";
            log.fine("\tAdded an event listener to list");
            ArrayList itemSet;
            if (listeners == null) {
                listeners = new HashMap();
                ids = new HashMap();
            }
            if (listeners.containsKey(name))
                itemSet = (ArrayList) listeners.get(name);
            else {
                itemSet = new ArrayList();
                listeners.put(name, itemSet);
            }
            LeaseHolder l = ThorServiceImpl.LANDLORD.newLease(listenProxy, this, 20000L);
            ids.put(l.getCookie(), listenProxy);
            itemSet.add(l.getCookie());
            EventRegistration evReg = new EventRegistration(ChangeEvent.ID, null, l.getLease(), EventDelivery.evtSeqNum);
            return evReg;
        } catch (Exception ex) {
            log.severe("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#removeChangeEventListener(java.lang.String,
     *           int)
     */
    public void removeChangeEventListener(String name, int id) {
        // TODO Complete method stub for removeChangeEventListener

    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getListenerFor(java.lang.String,
     *           int)
     */
    public Object getListenerFor(String name, int id) {
        // TODO Complete method stub for getListenerFor
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getBranchID()
     */
    public String getBranchID() {
        // TODO Complete method stub for getBranchID
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
        return ID;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#setBranchID(java.lang.String)
     */
    public void setBranchID(String ID) {
        // TODO Complete method stub for setBranchID
        this.ID = ID;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addXML(java.lang.String)
     */
    public void addXML(String XMLdata) throws UnsupportedOperationException {
        // TODO Complete method stub for addXML

    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#exportXML()
     */
    public String exportXML() {
        // TODO Complete method stub for exportXML
        return null;
    }

    public void register(Uuid ID, Object resource) {
        // TODO Complete method stub for register
        if (listeners == null)
            listeners = new HashMap();
        this.listeners.put(ID, resource);
    }

    /*
     * @see org.jini.projects.org.jini.projects.thor.service.leasing.LeaseHandler#remove()
     */
    public void remove(Uuid ID) {
        // TODO Complete method stub for remove
        ids.remove(ID);
    }

    private void checkLog() {
        if (log == null)
            log = Logger.getLogger("org.jini.projects.thor.handlers");
    }

    public void fireGeneralListeners(int changeType, String component, String entry, String value) {
        checkLog();
        int evtSequenceNo = ThorServiceImpl.evtSeqNum++;
        try {
            log.finer("Firing reconstituted configuration file");
            fireListeningSet("<default>", component, entry, value, reconstitute(), changeType, evtSequenceNo);
        } catch (Exception ex) {
            log.severe("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void fireIfListenersOn(String path, String component, String entry, String value, String reconstitutedConfig, int ChangeType) {
        checkLog();
        int evtSequenceNo = ThorServiceImpl.evtSeqNum++;
        try {

            fireListeningSet(path, component, entry, value, reconstitutedConfig, ChangeType, evtSequenceNo);

            fireListeningSet("<default>", component, entry, value, reconstitutedConfig, ChangeType, evtSequenceNo);
        } catch (Exception ex) {
            log.severe("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * @param path
     * @param component
     * @param value
     * @param ChangeType
     * @param evtSequenceNo
     * @throws RemoteException
     */
    private void fireListeningSet(String path, String component, String entry, String newValue, String reconstitutedConfig, int ChangeType, int evtSequenceNo) throws RemoteException {
        if (listeners != null) {
            if (listeners.get(path) != null) {
                Object obj = listeners.get(path);
                ArrayList arr = (ArrayList) listeners.get(path);
                ArrayList toRemove = new ArrayList();
                Iterator iter = arr.iterator();

                while (iter.hasNext()) {

                    Uuid id = (Uuid) iter.next();
                    if (ids.containsKey(id)) {
                        Object listener = ids.get(id);

                        fireRemoteEvent((RemoteEventListener) listener, this, ChangeType, component, entry, newValue, reconstitutedConfig, evtSequenceNo);

                    } else {
                        log.finest("Adding to collector");
                        toRemove.add(id);
                    }
                }
                Iterator removalIterator = toRemove.iterator();
                while (removalIterator.hasNext()) {
                    log.finest("Removing listener");
                    arr.remove(removalIterator.next());
                }
            }
        }
    }

    String reconstitute() {
        return cl.getScan().reconstitute();
    }

    private void fireRemoteEvent(RemoteEventListener listen, HierarchyHandler hier, int ChangeType, String component, String entry, String newValue, String reconstitutedConfig, int evtSequenceNo) {
        ConfigurationChangeEvent cha = null;
        // Point to a branch!!!!!

        try {
            cha = new ConfigurationChangeEvent(this.getClass().getName(), ChangeType, evtSequenceNo, null, component, entry, newValue, reconstitutedConfig);
            new Thread(new EventNotifier(listen, cha)).start();
        } catch (Exception ex) {
            System.out.println(new Date() + ": Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String getConfigurationFileName() {
        return this.configurationFile;
    }

    public static void main(String[] args) {
        HashMap options = new HashMap();
        options.put("filename", "src/" + "confHandlerTest" + ".backstore");
        StorageFactory sf = new StorageFactory(options);
        ConfigurationFileHandler app = new ConfigurationFileHandler("src/config/thor.config");
        Object o = app.getBranch("imports");
        System.out.println(o.getClass().getName());
        HierarchyHandler hh = app.getBranch("org.jini.projects.thor");
        System.out.println(hh.getClass().getName());
        System.out.println(hh.getChild("groups", true));
        System.out.println(app.reconstitute());
        hh.add("calum", "\"Shaw-Mackay\"");
        System.out.println("\n\nAfter:\n\n");
        System.out.println(app.reconstitute());
    }
}
