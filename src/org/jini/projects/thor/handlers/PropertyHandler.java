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
 * PropertyHandler.java
 * 
 * Created on 07 September 2001, 13:34
 */

package org.jini.projects.thor.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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
 * Binds a Properties Object into a Branch allowing per item modification
 * 
 * @author calum
 * @version 1.0
 */
public class PropertyHandler implements HierarchyHandler, java.io.Serializable, LeaseHandler {
    static final long serialVersionUID = -3232145111073436819L;

    private String ID;

    private transient HashMap listeners = new HashMap();

    private java.util.Properties props;

    private String description = "";

    // private transient EventDelivery evDel = null;
    private transient HashMap ids = new HashMap();

    /** Creates new PropertyHandler. This will be empty by default */
    public PropertyHandler() {
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ID);
        props = new java.util.Properties();
    }

    /**
     * Creates a new Property Handler loading it with Properties from the given
     * argument
     */
    public PropertyHandler(java.util.Properties props) {
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
        this.props = props;
    }

    /**
     * Creates a new Property Handler loading it with Properties from the
     * filename
     */
    public PropertyHandler(String filename) {
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
        synchronized (this) {
            props = new java.util.Properties();
            try {
                props.load(new java.io.FileInputStream(filename));
                // props.list(System.out);
                int i = 0;
                i++;
            } catch (Exception ex) {
                System.out.println("Err: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public Object locate(String path) {
        return props.getProperty(path);
    }

    public int getNumChildren() {
        return props.size();
    }

    public java.util.Iterator iterator() {
        return null;
    }

    public HierarchyHandler getBranch(String branchName) {
        return null;
    }

    public String getName(int idx) {
        Enumeration propsEnum = props.keys();
        int i = 0;
        while (propsEnum.hasMoreElements()) {
            String retval = (String) propsEnum.nextElement();
            if (i == idx)
                return retval;
            i++;
        }
        return null;
    }

    public Object getChild(int idx) {
        Enumeration propsEnum = props.elements();
        int i = 0;
        while (propsEnum.hasMoreElements()) {
            String retval = (String) propsEnum.nextElement();
            if (i == idx)
                return retval;
            i++;
        }
        return null;
    }

    public Object getChild(int Index, boolean noData) {
        Enumeration propsEnum = props.elements();
        int i = 0;
        while (propsEnum.hasMoreElements()) {
            String retval = (String) propsEnum.nextElement();
            if (i == Index)
                return retval;
            i++;
        }
        return null;
    }

    public Object locate(String path, Object index) {
        // PropertyHandler is a one-level object, no children can exists off
        // this handler
        // so we ignore the path
        Enumeration propsEnum = props.elements();
        int i = 0;
        while (propsEnum.hasMoreElements()) {
            Object retval = propsEnum.nextElement();
            if (retval.equals(index))
                return retval;
            i++;
        }
        return null;
    }

    /**
     * Returns the node data as represented by the internal
     * <code>Properties</code> object.
     */
    public Object getData() {
        return props;
    }

    public Object getChild(String name, boolean noData) {
        return props.get(name);
    }

    public Object locate(String path, int index) {
        Enumeration propsEnum = props.elements();
        int i = 0;
        while (propsEnum.hasMoreElements()) {
            String retval = (String) propsEnum.nextElement();
            if (i == index)
                return retval;
            i++;
        }
        return null;
    }

    public Object locate(String path, String index) {
        return props.getProperty(index);
    }

    public Object[] seek(String rootpath, String name) {
        return null;
    }

    public void removeChild(String name) {
        props.remove(name);
        fireIfListenersOn(name, name, null, org.jini.projects.thor.service.ChangeConstants.DELETE);
        fireIfListenersOn("<default>", name, null, org.jini.projects.thor.service.ChangeConstants.DELETE);
    }

    public void fireIfListenersOn(String path, Object key, Object value, int ChangeType) {
        try {
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
                            if (listener instanceof org.jini.projects.thor.service.ChangeEventListener) {
                                fireRemoteEvent((RemoteEventListener) listener, this, ChangeType, key, value);
                            }
                        } else {
                            System.out.println("Adding to collector");
                            toRemove.add(id);
                        }
                    }
                    Iterator removalIterator = toRemove.iterator();
                    while (removalIterator.hasNext()) {
                        System.out.println("Removing listener");
                        arr.remove(removalIterator.next());
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void fireRemoteEvent(RemoteEventListener listen, HierarchyHandler hier, int ChangeType, Object item, Object value) {
        ChangeEvent cha = null;
        // Point to a branch!!!!!
        try {
            cha = new ChangeEvent("Hello", ChangeType, ThorServiceImpl.evtSeqNum++, null, item, value);
            new Thread(new EventNotifier(listen, cha)).start();
        } catch (Exception ex) {
            System.out.println(new Date() + ": Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void add(String name, Object obj) {
        fireIfListenersOn(name, name, obj, org.jini.projects.thor.service.ChangeConstants.CREATE);
        fireIfListenersOn("<default>", name, obj, org.jini.projects.thor.service.ChangeConstants.CREATE);
        props.put(name, obj);
    }

    public void removeChild(Object obj) {
    }

    public void setDescription(String newName) {
        this.description = newName;
    }

    public void setDataBlock(Object obj) {
        if (obj instanceof java.util.Properties)
            this.props = (java.util.Properties) obj;
    }

    public void removeAll() {
        props.clear();
    }

    public void addBranch(String name) {
    }

    public void overwrite(String name, Object newObj) {
        props.put(name, newObj);
        fireIfListenersOn(name, name, newObj, org.jini.projects.thor.service.ChangeConstants.WRITE);
        fireIfListenersOn("<default>", name, newObj, org.jini.projects.thor.service.ChangeConstants.WRITE);
    }

    public String getDescription() {
        return "Property Handler: " + this.description;
    }

    /**
     * Returns the data as a Block i.e. a Vector.
     */
    public Object getDataBlock() {
        Vector vec = new Vector();
        Set propsSet = props.entrySet();
        Iterator iter = propsSet.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            NamedItem item = new NamedItem((String) entry.getKey(), entry.getValue());
            vec.add(item);
        }
        return vec;
    }

    /**
     * inserts a sub-branch into the current branch
     * 
     * @param name
     *                   Local name of the node to make a branch for
     */
    public void addBranch(String name, int type, HashMap attributes) {
    }

    public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
        String eventDeliveryKey = null;
        try {
            ProxyPreparer preparer = (ProxyPreparer) Startup.getConfiguration().getEntry("org.jini.projects.thor", "proxyPreparer", ProxyPreparer.class, new BasicProxyPreparer());
            ChangeEventListener listenProxy = (ChangeEventListener) preparer.prepareProxy(rel);
            if (name == null | name.equals(""))
                name = "<default>";
            System.out.println("\tAdded listener to listener map");
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
            System.out.println("Err: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public void removeChangeEventListener(String name, int id) {
        // evDel.removeChangeEvent(name, id);
    }

    public Object getListenerFor(String name, int id) {
        return null;
    }

    public String getBranchID() {
        // TODO Auto-generated method stub
        if (ID == null)
            ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
        return ID;
    }

    /*
     * @see org.jini.projects.org.jini.projects.thor.service.leasing.LeaseHandler#remove()
     */
    public void remove(Uuid ID) {
        // TODO Complete method stub for remove
        ids.remove(ID);
    }

    /*
     * @see org.jini.projects.org.jini.projects.thor.service.leasing.LeaseHandler#register(net.jini.id.Uuid,
     *           java.lang.Object)
     */
    public void register(Uuid ID, Object resource) {
        // TODO Complete method stub for register
        if (listeners == null)
            listeners = new HashMap();
        listeners.put(ID, resource);
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#addXML(java.lang.String)
     */
    public void addXML(String XMLdata) throws UnsupportedOperationException {
        // TODO Complete method stub for addXML
        throw new UnsupportedOperationException("This branch does not support subBranches via XML");
    }

    /*
     * @see org.jini.projects.thor.handlers.HierarchyHandler#setBranchID(java.lang.String)
     */
    public void setBranchID(String ID) {
        // TODO Complete method stub for setBranchID
        this.ID = ID;
    }

    public String exportXML() {
        XMLStore store = new XMLStore();
        return "<root>\n" + store.storeRoot(this) + "\n</root>\n";
    }
}
