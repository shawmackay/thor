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

/**
 *  Title: <p>
 *
 *  Description: <p>
 *
 *  Copyright: Copyright (c) <p>
 *
 *  Company: <p>
 *
 *
 *
 *@author
 *@version    1.0
 */

package org.jini.projects.thor.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import net.jini.id.UuidFactory;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.jini.projects.thor.configuration.ConfigurationFileWrapper;
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
import org.jini.projects.thor.service.store.xml.XMLStore;



/**
 * InternalHierarchy class for thor implementing a tree like quality
 * 
 * @author calum
 * 
 */
public class InternalHierarchy implements HierarchyHandler, java.io.Serializable, LeaseHandler {
    static final long serialVersionUID = -1646773199092482822L;

    public static void main(String[] args) {
        InternalHierarchy root = new InternalHierarchy();
    }

    private transient Logger log = Logger.getLogger("org.jini.projects.thor.handlers");

    private int currentsize = 0;

    private String description = "";

    private HashMap entity = new HashMap();

    // private transient EventDelivery evDel = new EventDelivery(this);
    private String ID;

    private transient HashMap listeners = new HashMap();

    private transient HashMap ids = new HashMap();

    private HashMap names = new HashMap();

    /**
     * Constructor for the InternalHierarchy object
     * 
     * @since
     */
    public InternalHierarchy() {
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
    }

    public InternalHierarchy(String description) {
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
        this.description = description;
    }

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
        if (obj instanceof ConfigurationFileWrapper) {
            ConfigurationFileWrapper cfw = (ConfigurationFileWrapper) obj;
            Uuid uuid = UuidFactory.generate();
            try {
                File f = new File("data/" + uuid.toString() + ".config");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(cfw.getConfigurationFileData());
                fos.flush();
                fos.close();
                ConfigurationFileHandler filehandler = new ConfigurationFileHandler(f.getAbsolutePath());
                obj = filehandler;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

        if (obj instanceof HierarchyHandler) {
            StorageFactory.getBackend().insertBranch(this, (HierarchyHandler) obj);
            entity.put(name, new BranchIdentifier(((HierarchyHandler) obj).getBranchID()));
        } else {

            log.finest("Adding " + obj.getClass().getName() + " as " + name);
            entity.put(name, obj);
        }

        fireIfListenersOn(name, name, obj, org.jini.projects.thor.service.ChangeConstants.CREATE);

        StorageFactory.getBackend().store(this);
    }

    public void addBranch(String name) {
        checkLog();
        InternalHierarchy branch = new InternalHierarchy();
        StorageFactory.getBackend().insertBranch(this, branch);
        log.finest("Adding " + name + " with ID " + branch.getBranchID());
        this.add(name, new BranchIdentifier(branch.getBranchID()));
        StorageFactory.getBackend().store(this);

        fireIfListenersOn(name, name, "<<BRANCH>>", org.jini.projects.thor.service.ChangeConstants.CREATE);
    }

    public void addBranch(String name, int type, java.util.HashMap attributes) {
        HierarchyHandler handle = null;
        checkLog();
        log.finest("Adding a branch");
        switch (type) {
        case INTERNAL:
            handle = new InternalHierarchy();
            if (attributes.get("DESCRIPTION") != null) {
                handle.setDescription((String) attributes.get("DESCRIPTION"));
            }
            break;
        case FILE:
            if (attributes.get("FILEPATH") != null) {
                handle = new FileHierarchy((String) attributes.get("FILEPATH"));
            }
            break;
        case REMOTE:
            if (attributes.get("SERVICE_NAME") != null) {
                handle = new RemoteThorHandler((String) attributes.get("GROUP"), (String) attributes.get("SERVICE_NAME"), (String) attributes.get("INITIAL_BRANCH"));
            }
            break;
        case PROPERTY:
            if (attributes.get("FILEPATH") != null) {
                handle = new PropertyHandler((String) attributes.get("FILEPATH"));
            } else {
                if (attributes.get("PROPS") != null) {
                    handle = new PropertyHandler((java.util.Properties) attributes.get("PROPS"));
                }
            }
            break;
        case LINK:
            if (attributes.get("LINKPATH") != null) {
                Object x = attributes.get("LINKPATH");
                handle = new LinkHierarchy((String) x);

            }
        case CONFIG:
            if (attributes.get("CONFIGFILEPATH") != null)
                handle = new ConfigurationFileHandler((String) attributes.get("CONFIGFILEPATH"));
            break;

        case LIST:
            handle = new ListHandler();
            break;
        }

        if (handle != null) {
            StorageFactory.getBackend().insertBranch(this, handle);
            this.add(name, new BranchIdentifier(handle.getBranchID()));
            StorageFactory.getBackend().store(this);
            fireIfListenersOn(name, name, "<<BRANCH>>", org.jini.projects.thor.service.ChangeConstants.CREATE);
        }
    }

    public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
        String eventDeliveryKey = null;
        checkLog();
        try {
            ProxyPreparer preparer = (ProxyPreparer) Startup.getConfiguration().getEntry("org.jini.projects.thor", "proxyPreparer", ProxyPreparer.class, new BasicProxyPreparer());
            ChangeEventListener listenProxy = (ChangeEventListener) preparer.prepareProxy(rel);
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

    public void displayTree(int level) {
        for (int i = 0; i < currentsize; i++) {
            for (int j = 0; j < level; j++) {
                System.out.print("  ");
            }
            Object currentItem = entity.get(names.get(new Integer(i)));
            Object viewItem;
            if (currentItem instanceof BranchIdentifier) {
                String ID = ((BranchIdentifier) currentItem).getBranchID();
                viewItem = StorageFactory.getBackend().getBranch(ID);
            } else
                viewItem = currentItem;

            if (viewItem instanceof InternalHierarchy) {
                InternalHierarchy temp = (InternalHierarchy) viewItem;
                System.out.print("+--");
                System.out.println(names.get(new Integer(i)));
                temp.displayTree(level + 1);
            } else {

                System.out.print("|--");
                System.out.println(names.get(new Integer(i)) + " : " + viewItem);
            }
        }
    }

    public HierarchyHandler getBranch(String branchName) {
        checkLog();
        try {
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
     * (non-Javadoc)
     * 
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getBranchID()
     */
    public String getBranchID() {
        // TODO Auto-generated method stub
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
        return ID;
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

    public Object getData() {
        return getDataBlock();
    }

    public Object getDataBlock() {
        java.util.Vector vec = new java.util.Vector();
        // These vectors only return data on this level
        // Any hierarchies are ignored

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
                if (viewOb instanceof ConfigurationFileHandler) {                   
                    tag = "<<configbranch>>";
                }
                if(viewOb instanceof ListHandler){
                    tag= "<<listbranch>>";
                }
                

                vec.add(new NamedItem((String) names.get(new Integer(i)), tag));

            }
        }

        return vec;
    }

    public String getDescription() {
        return this.description;
    }

    public Object getListenerFor(String name, int id) {
        return null;
    }

    public String getName(int idx) {
        return (String) names.get(new Integer(idx));
    }

    public int getNumChildren() {
        return names.size();
    }

    public java.util.Iterator iterator() {
        return null;
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

    public void move(Object child, HierarchyHandler receiver) {
        synchronized (this) {
            for (int i = 0; i < names.size(); i++) {
                if (entity.get(names.get(new Integer(i))).equals(child)) {
                    receiver.add((String) names.get(new Integer(i)), child);
                    return;
                }
            }
        }
    }

    public void overwrite(String name, Object newObj) {
        // Function as an add here
        if (!entity.containsKey(name)) {
            names.put(new Integer(currentsize++), name);
        }
        if (!names.containsValue(name)) {
            names.put(new Integer(currentsize++), name);
        }
        entity.put(name, newObj);

        fireIfListenersOn(name, name, newObj, org.jini.projects.thor.service.ChangeConstants.WRITE);

    }

    private void checkLog() {
        if (log == null)
            log = Logger.getLogger("org.jini.projects.thor.handlers");
    }

    /*
     * @see org.jini.projects.org.jini.projects.thor.service.leasing.LeaseHandler#register(net.jini.id.Uuid,
     *           java.lang.Object)
     */
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

    public void fireIfListenersOn(String path, Object key, Object value, int ChangeType) {
        checkLog();
        int evtSequenceNo = ThorServiceImpl.evtSeqNum++;
        try {

            fireListeningSet(path, key, value, ChangeType, evtSequenceNo);

            fireListeningSet("<default>", key, value, ChangeType, evtSequenceNo);
        } catch (Exception ex) {
            log.severe("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * @param path
     * @param key
     * @param value
     * @param ChangeType
     * @param evtSequenceNo
     * @throws RemoteException
     */
    private void fireListeningSet(String path, Object key, Object value, int ChangeType, int evtSequenceNo) throws RemoteException {
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
                        fireRemoteEvent((RemoteEventListener) listener, this, ChangeType, key, value, evtSequenceNo);

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

    public void removeAll() {
        Collection c = entity.values();
        Object[] obs = c.toArray();
        for (int i = 0; i < obs.length; i++)
            removeChild(obs[i]);
    }

    public void removeChangeEventListener(String name, int id) {
    }

    public void removeChild(Object current) {
        boolean foundWithNullKey = false;
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
                    fireIfListenersOn((String) lookup, lookup, null, org.jini.projects.thor.service.ChangeConstants.DELETE);
                }
            }
        }
    }

    public void removeNullKeyedItems() {
        int originalSize = getNumChildren();
        int numSize = getNumChildren();
        Iterator iter = names.entrySet().iterator();

        System.out.println("Looking to remove");
        System.out.println("Contains null key: " + names.containsValue(null));
        System.out.println("Contains \"null\" key: " + names.containsValue("null"));
        HashMap newMap = new HashMap();
        while (iter.hasNext()) {
            Map.Entry entr = (Map.Entry) iter.next();
            System.out.println(entr.getKey() + ": " + entr.getValue());
            if (entr.getValue() == null || entr.getValue().equals(null)) {
                System.out.println("Calling remove");
                iter.remove();
                newMap.put(entr.getKey(), names.get(new Integer(numSize - 1)));
                numSize--;
            }
        }
        names.putAll(newMap);

        iter = entity.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entr = (Map.Entry) iter.next();
            System.out.println(entr.getKey());
            if (entr.getKey() == null || entr.getKey().equals(null)) {
                System.out.println("Calling remove");
                iter.remove();
            }
        }

    }

    public void removeChild(String name) {
        synchronized (this) {
            boolean removed = false;
            int removedpos = -1;
            checkLog();
            if (name == null || name.equals("null")) {                
                removeNullKeyedItems();
                return;
            }
            log.finest("Removing " + name + " from names");
            try {
                if (names.containsValue(name)) {
                    for (int i = 0; i < currentsize; i++) {
                        String listedname = (String) names.get(new Integer(i));
                        if (listedname != null)
                            if (listedname.equals(name)) {
                                log.finest("Found Name @ " + i);
                                names.remove(new Integer(i));
                                removedpos = i;
                                removed = true;
                                break;
                            }
                    }
                }
                Iterator namesiter = names.entrySet().iterator();
                HashMap newNames = new HashMap();
                int count = 0;
                while (namesiter.hasNext()) {
                    Map.Entry entr = (Map.Entry) namesiter.next();
                    if (entr.getValue() != null) {
                        
                        newNames.put(new Integer(count++), entr.getValue());
                    }
                }

                names = newNames;
                currentsize = count;
                log.finest("Removing " + name + " from entities");
                entity.remove(name);

                fireIfListenersOn(name, name, null, org.jini.projects.thor.service.ChangeConstants.DELETE);

            } catch (Exception ex) {
                log.severe("Err :  " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public Object[] seek(String rootpath, String name) {
        return null;
    }

    public void setDataBlock(Object obj) {
    }

    public void setDescription(String newName) {
        this.description = newName;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("[");
        int i = 0;
        if (names.size() == 0) {
            return "[null]";
        }
        return names.toString();
    }

    public void setBranchID(String ID) {
        // TODO Complete method stub for setBranchID
        this.ID = ID;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addXML(java.lang.String)
     */
    public void addXML(String XMLdata) throws UnsupportedOperationException {
        // TODO Complete method stub for addXML
        XMLStore store = new XMLStore();
        try {
            File f = File.createTempFile("thor", "xmlload");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(XMLdata.getBytes());
            fos.flush();
            fos.close();
            store.loadFromXML(this, f.getAbsolutePath());
            f.delete();
        } catch (FileNotFoundException e) {
            // URGENT Handle FileNotFoundException
            e.printStackTrace();
        } catch (IOException e) {
            // URGENT Handle IOException
            e.printStackTrace();
        }
    }

    public String exportXML() {
        XMLStore store = new XMLStore();
        return "<root>\n" + store.storeRoot(this) + "\n</root>\n";
    }

    private void fireRemoteEvent(RemoteEventListener listen, HierarchyHandler hier, int ChangeType, Object item, Object value, int evtSequenceNo) {
        ChangeEvent cha = null;
        // Point to a branch!!!!!

        try {
            cha = new ChangeEvent(this.getClass().getName(), ChangeType, evtSequenceNo, null, item, value);
            new Thread(new EventNotifier(listen, cha)).start();
        } catch (Exception ex) {
            System.out.println(new Date() + ": Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
