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
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 12-Jun-02
 * Time: 11:07:40
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.thor.handlers;

import java.util.Date;
import java.util.HashMap;

import net.jini.core.event.RemoteEventListener;

import org.jini.projects.thor.service.ChangeEvent;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.EventNotifier;
import org.jini.projects.thor.service.ThorServiceImpl;
import org.jini.projects.thor.service.store.StorageFactory;

public class LinkHierarchy implements HierarchyHandler, java.io.Serializable {

	static final long serialVersionUID = -1302951892921155191L;
	private String ID;
    private transient HierarchyHandler link = null;

    private String linkName;


    public LinkHierarchy(String branch) {
		if (ID == null)
	ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());

        linkName = branch;
        if(branch.startsWith("/"))
        	branch=branch.substring(1);
        this.link = StorageFactory.getBackend().getBranch("ROOT").getBranch(branch);
    }

    private void checkLink() {
        if (link == null) {
            this.link = StorageFactory.getBackend().getBranch("ROOT").getBranch(linkName);
        }
    }

    /**
     *  Locates an node via it's fully qualified name
     *
     *@param  path  Hierarchical path to node
     *@return       object held by the requested node
     *@since 1.0b
     */
    public Object locate(String path) {
        checkLink();
        return link.locate(path);
    }

    /**
     *  Locates an node via it's hierarchy path, and then by it's local name
     *
     *@param  path   Hierarchical path to branch
     *@param  index  local name of node
     *@return        object held by the requested node
     *@since 1.0b
     */
    public Object locate(String path, String index) {
        checkLink();
        return link.locate(path, index);
    }

    /**
     *  Locates an node via it's hierarchy path, and then by it's numerical index
     *
     *@param  path   Hierarchical path to branch
     *@param  index  Positional index of node
     *@return        object held by the requested node
     *@since 1.0b
     */
    public Object locate(String path, int index) {
        checkLink();
        return link.locate(path, index);
    }

    /**
     *  Locates an exact 'equal' of the node within the hierarchical path
     *
     *@param  path   Hierarchical path to node
     *@param  index  Object to compare
     *@return        object held by the requested node
     *@since 1.0b
     */

    public Object locate(String path, Object index) {
        checkLink();
        return link.locate(path, index);
    }

    /**
     *  Returns the node at the given position <i>Index</i> from the current branch
     *
     *@param  Index     Positional index of the object
     *@param  withData  Boolean representing whether the data should be returned,
     *      or an indicator of the type of data
     *@return           object held by the requested node
     *@since 1.0b
     */

    public Object getChild(int Index, boolean withData) {
        checkLink();
        return link.getChild(Index, withData);
    }

    /**
     *  Returns the names node from the
     *  current branch
     *
     *@param  withData  Boolean representing whether the data should be returned,
     *      or an indicator of the type of data
     *@param  name      Positional Index of Node
     *@return           name of the object at the given position
     *@since 1.0b
     */
    public Object getChild(String name, boolean withData) {
        checkLink();
        return link.getChild(name, withData);
    }

    /**
     *  Returns the local name of the node at the Index <i>index</i> from the
     *  current branch
     *
     *@param  idx  Positional Index of Node
     *@return      name of the object at the given position
     *@since 1.0b
     */

    public String getName(int idx) {
        checkLink();
        return link.getName(idx);
    }

    /**
     *  Returns the branch node with the given name from the current branch
     *
     *@param  branchName  Local name of branch node
     *@return             branch node on the current branch
     *@since 1.0b
     */
    public HierarchyHandler getBranch(String branchName) {
        checkLink();
        return link.getBranch(branchName);
    }

    /**
     *  Returns the number of nodes held within this branch
     *
     *@return    count of nodes on this branch
     *@since 1.0b
     */

    public int getNumChildren() {
        checkLink();
        return link.getNumChildren();
    }

    /**
     *  Returns a representation of the branch, useful for obtaining all
     *  nodes in one call. This will only work at a single-level, sub branches will
     *  be indicated, but will not be deep-copied. This will usually be returned as a
     * <code>Collection</code> class such as a Vector; It is suggested that Vectors
     * contain instances of <code>NamedItem</code>s
     *
     *@return    Collection of items representing the branch
     *@see org.jini.projects.thor.service.NamedItem
     *@since 1.0b
     */
    public Object getDataBlock() {
        checkLink();
        return link.getDataBlock();
    }

    /**
     *  Sets the data block (i.e.throws internal representation of the branch).
     *  <strong>Use with caution on Internal Hierarchies</strong>
     *
     *@param  obj  Internal representation
     *@since 1.0b
     */
    public void setDataBlock(Object obj) {
        checkLink();
        link.setDataBlock(obj);
    }

    /**
     *  Sets the description of the branch
     *
     *@param  newName  description of the branch
     *@since 1.0b
     */

    public void setDescription(String newName) {
        checkLink();
        link.setDescription(newName);
    }

    /**
     *  Gets the description of the branch
     *
     *@return    description of the branch
     *@since 1.0b
     */
    public String getDescription() {
    	return linkName;
        
    }

    /**
     *  Adds a node to the current branch with the name <name>. Do not use for
     *  adding branches, instead use the <b>addBranch(String name)</b> method
     *
     *@param  name  Local name of the node
     *@param  obj   Data represented by the node
     *@since 1.0b
     */
    public void add(String name, Object obj) {
        checkLink();
        link.add(name, obj);
    }

    /**
     *  inserts a sub-branch into the current branch
     *
     *@param  name  Local name of the node to make a branch for
     *@since 1.0b
     */
    public void addBranch(String name) {
        checkLink();
        link.addBranch(name);
    }

    /**
     *  inserts a sub-branch into the current branch
     *
     *@param  name        Local name of the node to make a branch for
     *@param  type        The feature to be added to the Branch attribute
     *@param  attributes  The feature to be added to the Branch attribute
     *@since 1.0b
     */
    public void addBranch(String name, int type, HashMap attributes) {
        checkLink();
        link.addBranch(name, type, attributes);
    }

    /**
     *  Overwrites the object with the name given to this new object
     *
     *@param  name    Local name of the node
     *@param  newObj  Data to be represented by the node
     *@since 1.0b
     */
    public void overwrite(String name, Object newObj) {
        checkLink();
        link.overwrite(name, newObj);
    }

    /**
     *  Removes any and all occurences of the object <i>obj</i> where a given node
     *  in the branch 'equals' the object passed
     *
     *@param  obj  Object to comapre branch nodes against
     *@since 1.0b
     */
    public void removeChild(Object obj) {
        checkLink();
        link.removeChild(obj);
    }

    /**
     *  Removes the node denoted by the given name
     *
     *@param  name  Local name of the node to remove
     *@since 1.0b
     */
    public void removeChild(String name) {
        checkLink();
        link.removeChild(name);
    }

    /**
     *  Clears the current branch of nodes.
     *
     *@since 1.0b
     */
    public void removeAll() {
        checkLink();
        link.removeAll();
    }

    /**
     *  Returns the data in a handler-specific format.
     * For instance in a <code><a href = "PropertyHandler.html">PropertyHandler</a></code>
     *
     *@return    The data value
     *@since 1.0b
     */
    public Object getData() {
        checkLink();
        return link.getData();
    }

    /**
     *  Adds a remote event listener into this branch, which allows clients to be informed of changes.
     *
     *@param  rel   Client-supplied EventListener implementing <code>ChangeEventListener</code>
     *@param  name  The item to listen on within the branch, Use "" to listen to all items
     *@return       Client Registration including Lease
     *@see org.jini.projects.thor.service.ChangeEventListener
     *@since 1.0b
     */
    public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
        checkLink();
        return link.addChangeEventListener(rel, name);
    }

    /**
     *  Remove a change event
     *
     *@param  name  Description of Parameter
     *@param  id    Description of Parameter
     *@since 1.0b
     */
    public void removeChangeEventListener(String name, int id) {
        checkLink();
        link.removeChangeEventListener(name, id);
    }

    /**
     *  Gets the listenerFor attribute of the HierarchyHandler object
     *
     *@param  name  Description of Parameter
     *@param  id    Description of Parameter
     *@return       The listenerFor value
     *@since 1.0b
     */
    public Object getListenerFor(String name, int id) {
        checkLink();
        return link.getListenerFor(name, id);
    }

	/* (non-Javadoc)
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getBranchID()
	 */
	public String getBranchID() {
		// TODO Auto-generated method stub
		if (ID == null)
		ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
		return ID;
	}

    public void setBranchID(String ID) {
        // TODO Complete method stub for setBranchID
        this.ID = ID;
    }
    
    /* @see org.jini.projects.thor.handlers.HierarchyHandler#addXML(java.lang.String)
     */
    public void addXML(String XMLdata) throws UnsupportedOperationException {
        // TODO Complete method stub for addXML
        throw new UnsupportedOperationException("This branch does not support subBranches via XML");
    }
    
    public String exportXML() {
              
        return "<root></root>";
    }
    
    private void fireRemoteEvent(RemoteEventListener listen, HierarchyHandler hier, int ChangeType, Object item, Object value) {
        ChangeEvent cha = null;
        //Point to a branch!!!!!
        try {
            cha = new ChangeEvent(this.getClass().getName(), ChangeType, ThorServiceImpl.evtSeqNum++, null, item, value);
            new Thread(new EventNotifier(listen, cha)).start();
        } catch (Exception ex) {
            System.out.println(new Date() + ": Err: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
}
