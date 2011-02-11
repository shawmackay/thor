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
 * ListHandler.java
 * Created on 14-Dec-2004
 * 
 * ListHandler
 *
 */
package org.jini.projects.thor.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;

import org.jini.projects.thor.service.ChangeEvent;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.EventDelivery;
import org.jini.projects.thor.service.EventNotifier;
import org.jini.projects.thor.service.NamedItem;
import org.jini.projects.thor.service.Startup;
import org.jini.projects.thor.service.ThorServiceImpl;
import org.jini.projects.thor.service.leasing.LeaseHandler;
import org.jini.projects.thor.service.leasing.LeaseHolder;
import org.jini.projects.thor.service.store.xml.XMLStore;

/**
 * Implements an ArrayList as a Branch. Names are always the index, so they
 * would be "0","1","2", etc. This means that accessing items is
 * <strong>non-deterministic </strong>. The point behind the ListHandler is to
 * provide a virtual view into the List, ideally you should <strong>not
 * </strong> depend upon an item being ata particular point in the list as the
 * 'name' of the item changes when it's position in the list changes. If you
 * want deterministic naming, use Internal Hierarchy instead.
 * 
 * @author calum
 */
public class ListHandler implements HierarchyHandler, Serializable, LeaseHandler {

    static final long serialVersionUID = -1578784599092482822L;

    private String ID;

    private transient HashMap listeners = new HashMap();

    private transient HashMap ids = new HashMap();

    private List items;

    private String description;

    private transient Logger log = Logger.getLogger("org.jini.projects.thor.handlers");

    public ListHandler() {
        items = new ArrayList();
    }

    public ListHandler(List newItems) {
        items = newItems;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#locate(java.lang.String)
     */
    public Object locate(String path) {
        // TODO Complete method stub for locate
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#locate(java.lang.String,
     *           java.lang.String)
     */
    public Object locate(String path, String index) {
        // TODO Complete method stub for locate
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#locate(java.lang.String,
     *           int)
     */
    public Object locate(String path, int index) {
        // TODO Complete method stub for locate
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#locate(java.lang.String,
     *           java.lang.Object)
     */
    public Object locate(String path, Object index) {
        // TODO Complete method stub for locate
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getChild(int,
     *           boolean)
     */
    public Object getChild(int Index, boolean withData) {
        // TODO Complete method stub for getChild
        return items.get(Index);
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getChild(java.lang.String,
     *           boolean)
     */
    public Object getChild(String name, boolean withData) {
        // TODO Complete method stub for getChild
        try {
            int i = Integer.parseInt(name);
            return items.get(i);
        } catch (NumberFormatException nfex) {
            log.log(Level.WARNING, "Requested name cannot be parsed into a number", nfex);
        }
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getName(int)
     */
    public String getName(int idx) {
        // TODO Complete method stub for getName
        return String.valueOf(idx);
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getBranch(java.lang.String)
     */
    public HierarchyHandler getBranch(String branchName) {
        // TODO Complete method stub for getBranch
        return null;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getNumChildren()
     */
    public int getNumChildren() {
        // TODO Complete method stub for getNumChildren
        return items.size();
    }

    public Object getDataBlock() {
        // TODO Complete method stub for getDataBlock
        Vector vec = new Vector();
        for (int i = 0; i < items.size(); i++) {
            vec.add(new NamedItem(String.valueOf(i), items.get(i)));
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
        this.description = newName;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getDescription()
     */
    public String getDescription() {
        // TODO Complete method stub for getDescription
        return description;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#add(java.lang.String,
     *           java.lang.Object)
     */
    /**
     * Ignores the name, as the name is generated by the index
     * 
     * @see org.jini.projects.thor.handlers.HierarchyHandler#add(java.lang.String,
     *           java.lang.Object)
     */
    public void add(String name, Object obj) {
        // TODO Complete method stub for add
        items.add(obj);
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addBranch(java.lang.String)
     */
    public void addBranch(String name) {
        // TODO Complete method stub for addBranch

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
        try {
            int i = Integer.parseInt(name);
            items.set(i, newObj);
        } catch (NumberFormatException nfex) {
            log.log(Level.WARNING, "Requested name cannot be parsed into a number", nfex);
        }
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#removeChild(java.lang.Object)
     */
    public void removeChild(Object obj) {
        // TODO Complete method stub for removeChild
        items.remove(obj);
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#removeChild(java.lang.String)
     */
    public void removeChild(String name) {
        // TODO Complete method stub for removeChild
        try {
            int i = Integer.parseInt(name);
            items.remove(i);
        } catch (NumberFormatException nfex) {
            log.log(Level.WARNING, "Requested name cannot be parsed into a number", nfex);
        }
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#removeAll()
     */
    public void removeAll() {
        // TODO Complete method stub for removeAll
        items.clear();
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#getData()
     */
    public Object getData() {
        // TODO Complete method stub for getData
        return items;
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addChangeEventListener(org.jini.projects.thor.service.ChangeEventListener,
     *           java.lang.String)
     */
    public EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
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

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#exportXML()
     */
    public String exportXML() {
        XMLStore store = new XMLStore();
        return "<root>\n" + store.storeRoot(this) + "\n</root>\n";
    }

    private void checkLog() {
        if (log == null)
            log = Logger.getLogger("org.jini.projects.thor.handlers");
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
