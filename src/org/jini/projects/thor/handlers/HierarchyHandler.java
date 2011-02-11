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
 *  Title: Thor<p>
 *
 *  Description: Properties / Directory Service for Jini Allows decentralisation
 *  of configurations and other objects <p>
 *
 *  Copyright: Copyright (c) calum<p>
 *
 *  Company: CA<p>
 *
 *
 *
 *@author     calum
 *@version    1.0
 */
package org.jini.projects.thor.handlers;

import org.jini.projects.thor.service.ChangeEventListener;


/**
 *  Represents a virtual branch within Thor.<br>
 * This interface should be implemented by all classes representing a branch. It provides support for
 * <ul>
 * <li> Locating an item from this branch <i>downwards</i></li>
 *  <li> Directly obtaining items using indexes or names</li>
 *<li>  Obtaining the entire contents of the branch (one level only) in either a standard format or a handler specific one</li>
 *<li> Modifying the contents of a branch</li>
 *<li> Registering event listsners on  items to be notified of changes</li>
 *<li> Event Management</li>
 *</ul>
 *@author     calum
 *
 */

public interface HierarchyHandler extends org.jini.projects.thor.service.AttributeConstants {
    /**
     *  Locates an node via it's fully qualified name
     *
     *@param  path  Hierarchical path to node
     *@return       object held by the requested node
     *@since 1.0b
     */
    public Object locate(String path);


    /**
     *  Locates an node via it's hierarchy path, and then by it's local name
     *
     *@param  path   Hierarchical path to branch
     *@param  index  local name of node
     *@return        object held by the requested node
     *@since 1.0b
     */
    public Object locate(String path, String index);


    /**
     *  Locates an node via it's hierarchy path, and then by it's numerical index
     *
     *@param  path   Hierarchical path to branch
     *@param  index  Positional index of node
     *@return        object held by the requested node
     *@since 1.0b
     */
    public Object locate(String path, int index);


    /**
     *  Locates an exact 'equal' of the node within the hierarchical path
     *
     *@param  path   Hierarchical path to node
     *@param  index  Object to compare
     *@return        object held by the requested node
     *@since 1.0b
     */

    public Object locate(String path, Object index);


    /**
     *  Returns the node at the given position <i>Index</i> from the current branch
     *
     *@param  Index     Positional index of the object
     *@param  withData  Boolean representing whether the data should be returned,
     *      or an indicator of the type of data
     *@return           object held by the requested node
     *@since 1.0b
     */

    public Object getChild(int Index, boolean withData);


    /**
     *  Returns the local name of the node at the Index <i>index</i> from the
     *  current branch
     *
     *@param  withData  Boolean representing whether the data should be returned,
     *      or an indicator of the type of data
     *@param  name      Positional Index of Node
     *@return           name of the object at the given position
     *@since 1.0b
     */
    public Object getChild(String name, boolean withData);


    /**
     *  Returns the local name of the node at the Index <i>index</i> from the
     *  current branch
     *
     *@param  idx  Positional Index of Node
     *@return      name of the object at the given position
     *@since 1.0b
     */

    public String getName(int idx);


    /**
     *  Returns the branch node with the given name from the current branch
     *
     *@param  branchName  Local name of branch node
     *@return             branch node on the current branch
     *@since 1.0b
     */
    public HierarchyHandler getBranch(String branchName);


    /**
     *  Returns the number of nodes held within this branch
     *
     *@return    count of nodes on this branch
     *@since 1.0b
     */

    public int getNumChildren();


    //Modifier methods
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
    public Object getDataBlock();


    /**
     *  Sets the data block (i.e.throws internal representation of the branch).
     *  <strong>Use with caution on Internal Hierarchies</strong>
     *
     *@param  obj  Internal representation
     *@since 1.0b
     */
    public void setDataBlock(Object obj);


    /**
     *  Sets the description of the branch
     *
     *@param  newName  description of the branch
     *@since 1.0b
     */

    public void setDescription(String newName);


    /**
     *  Gets the description of the branch
     *
     *@return    description of the branch
     *@since 1.0b
     */
    public String getDescription();


    //All these methods refer to Objects Children
    /**
     *  Adds a node to the current branch with the name <name>. Do not use for
     *  adding branches, instead use the <b>addBranch(String name)</b> method
     *
     *@param  name  Local name of the node
     *@param  obj   Data represented by the node
     *@since 1.0b
     */
    public void add(String name, Object obj);


    /**
     *  inserts a sub-branch into the current branch
     *
     *@param  name  Local name of the node to make a branch for
     *@since 1.0b
     */
    public void addBranch(String name);


    /**
     *  inserts a sub-branch into the current branch
     *
     *@param  name        Local name of the node to make a branch for
     *@param  type        The feature to be added to the Branch attribute
     *@param  attributes  The feature to be added to the Branch attribute
     *@since 1.0b
     */
    public void addBranch(String name, int type, java.util.HashMap attributes);


    /**
     *  Overwrites the object with the name given to this new object
     *
     *@param  name    Local name of the node
     *@param  newObj  Data to be represented by the node
     *@since 1.0b
     */
    public void overwrite(String name, Object newObj);


    /**
     *  Removes any and all occurences of the object <i>obj</i> where a given node
     *  in the branch 'equals' the object passed
     *
     *@param  obj  Object to comapre branch nodes against
     *@since 1.0b
     */
    public void removeChild(Object obj);


    /**
     *  Removes the node denoted by the given name
     *
     *@param  name  Local name of the node to remove
     *@since 1.0b
     */
    public void removeChild(String name);


    /**
     *  Clears the current branch of nodes.
     *
     *@since 1.0b
     */
    public void removeAll();


    /**
     *  Returns the data in a handler-specific format.
     * For instance in a <code><a href = "PropertyHandler.html">PropertyHandler</a></code>
     *
     *@return    The data value
     *@since 1.0b
     */
    public Object getData();


    /**
     *  Adds a remote event listener into this branch, which allows clients to be informed of changes.
     *
     *@param  rel   Client-supplied EventListener implementing <code>ChangeEventListener</code>
     *@param  name  The item to listen on within the branch, Use "" to listen to all items
     *@return       Client Registration including Lease
     *@see org.jini.projects.thor.service.ChangeEventListener
     *@since 1.0b
     */
    public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name);


    /**
     *  Remove a change event
     *
     *@param  name  Description of Parameter
     *@param  id    Description of Parameter
     *@since 1.0b
     */
    public void removeChangeEventListener(String name, int id);


    /**
     *  Gets the listenerFor attribute of the HierarchyHandler object
     *
     *@param  name  Description of Parameter
     *@param  id    Description of Parameter
     *@return       The listenerFor value
     *@since 1.0b
     */
    public Object getListenerFor(String name, int id);
    
    public String getBranchID();
    
    public void setBranchID(String ID);
    
    public void addXML(String XMLdata) throws UnsupportedOperationException;


	public String exportXML();
}

