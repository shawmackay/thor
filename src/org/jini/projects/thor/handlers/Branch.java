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
 *  Branch.java
 *
 *  Created on 18 September 2001, 11:23
 */
package org.jini.projects.thor.handlers;

import java.rmi.RemoteException;

import net.jini.core.event.EventRegistration;
import net.jini.core.lease.LeaseDeniedException;

import org.jini.projects.thor.service.ChangeEventListener;

/**
 *  Allows access to thor over an RMI connection. This class is used by direct
 *  clients, as well as by <i>RemoteHandler</i>
 *@author     calum
 *@version
 */


public interface Branch extends java.rmi.Remote {

    /**
     *  Locates an node via it's fully qualified name
     *
     *@param  path              Hierarchical path to node
     *@return                   object held by the requested node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */
    public Object locate(String path) throws RemoteException;


    /**
     *  Locates an node via it's hierarchy path, and then by it's local name
     *
     *@param  path              Hierarchical path to branch
     *@param  index             local name of node
     *@return                   object held by the requested node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */
    public Object locate(String path, String index) throws RemoteException;


    /**
     *  Locates an node via it's hierarchy path, and then by it's numerical index
     *
     *@param  path              Hierarchical path to branch
     *@param  index             Positional index of node
     *@return                   object held by the requested node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */
    public Object locate(String path, int index) throws RemoteException;


    /**
     *  Locates an exact 'equal' of the node within the hierarchical path
     *
     *@param  path              Hierarchical path to node
     *@param  index             Object to compare
     *@return                   object held by the requested node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public Object locate(String path, Object index) throws RemoteException;


    /**
     *  Returns the node at the Index <i>index</i> from the current branch
     *
     *@param  Index             Positional Index of Node
     *@param  withData          Boolean representing whether the data should be
     *      returned, or an indicator of the type of data
     *@return                   object held by the requested node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public Object getChild(int Index, boolean withData) throws RemoteException;


    /**
     *  Returns the node with the given name <i>name</i> from the current branch
     *
     *@param  name              Local name of node
     *@param  withData          Boolean representing whether the data should be
     *      returned, or an indicator of the type of data
     *@return                   object held by the requested node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public Object getChild(String name, boolean withData) throws RemoteException;


    /**
     *  Returns the local name of the node at the Index <i>index</i> from the
     *  current branch
     *
     *@param  idx               Positional Index of Node
     *@return                   name of the object at the given position
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public String getName(int idx) throws RemoteException;


    /**
     *  Returns the branch node with the given name from the current branch
     *
     *@param  branchName        Local name of branch node
     *@return                   branch node on the current branch
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public Branch getBranch(String branchName) throws RemoteException;


    /**
     *  Returns the number of nodes held within this branch
     *
     *@return                   count of nodes on this branch
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public int getNumChildren() throws RemoteException;


    /**
     *  Sets the data block (i.e.internal representation of the branch). <strong>
     *  Use with caution on Internal Hierarchies</strong>
     *
     *@param  name              Description of Parameter
     *@return                   The branch value
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public boolean isBranch(String name) throws RemoteException;


    /**
     *  Gets the branch attribute of the Branch object
     *
     *@param  idx                  Description of Parameter
     *@return                      The branch value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public boolean isBranch(int idx) throws RemoteException;


    /**
     *  Sets the dataBlock attribute of the Branch object
     *
     *@param  obj                  The new dataBlock value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public void setDataBlock(Object obj) throws RemoteException;


    /**
     *  Returns the internal representation of the branch, useful for obtaining all
     *  nodes in one call. This will only work at a single-level, sub branches will
     *  be indicated, but will not be deep-copied
     *
     *@return                   Collection of items representing the branch
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public Object getDataBlock() throws RemoteException;


    /**
     *  Sets the description of the branch
     *
     *@param  newName           description of the branch
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void setDescription(String newName) throws RemoteException;


    /**
     *  Gets the description of the branch
     *
     *@return                   description of the branch
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public String getDescription() throws RemoteException;


    //All these methods refer to Objects Children

    /**
     *  Adds a node to the current branch with the name <name>. Do not use for
     *  adding branches, instead use the <b>addBranch(String name)</b> method
     *
     *@param  name              Local name of the node
     *@param  obj               Data represented by the node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void add(String name, Object obj) throws RemoteException;
    
    /**
     *  Adds a set of sub-branches and items connected to this branch
     *
     *@param  XMLData               Data represented by the node
     *@since 0.9community
     *@throws UnsupportedOperationException if the current branch does not support sub-branches
     *@throws  RemoteException  if an error happens during the call
     */
    public void addXML(String XMLdata) throws UnsupportedOperationException, RemoteException;
    
    /**
     *  Exports this nodes children and sub-branches into an XML String.
     *
     *@since 0.9community
     *@return A XML formatted string
     *@throws  RemoteException  if an error happens during the call
     */
    public String exportXML() throws RemoteException;
    
    /**
     *  inserts a sub-branch into the current branch
     *
     *@param  name              Local name of the node to make a branch for
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void addBranch(String name) throws RemoteException;


    /**
     *  inserts a sub-branch into the current branch
     *
     *@param  name              Local name of the node to make a branch for
     *@param  type              The feature to be added to the Branch attribute
     *@param  attributes        The feature to be added to the Branch attribute
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void addBranch(String name, int type, java.util.HashMap attributes) throws RemoteException;


    /**
     *  Overwrites the object with the name given to this new object
     *
     *@param  name              Local name of the node
     *@param  newObj            Data to be represented by the node
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void overwrite(String name, Object newObj) throws RemoteException;


    /**
     *  Removes any and all occurences of the object <i>obj</i> where a given node
     *  in the branch 'equals' the object passed
     *
     *@param  obj               Object to comapre branch nodes against
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void removeChild(Object obj) throws RemoteException;


    /**
     *  Removes the node denoted by the given name
     *
     *@param  name              Local name of the node to remove
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void removeChild(String name) throws RemoteException;


    /**
     *  Clears the current branch of nodes.
     *
     *@since
     *@throws  RemoteException  if an error happens during the call
     */

    public void removeAll() throws RemoteException;


    /**
     *  Gets the data attribute of the Branch object
     *
     *@return                      The data value
     *@exception  RemoteException  Description of Exception
     *@since
     */
    public Object getData() throws RemoteException;


    /**
     *  Description of the Method
     *
     *@param  duration                  Description of Parameter
     *@param  rel                       Description of Parameter
     *@param  changeOn                  Description of Parameter
     *@return                           Description of the Returned Value
     *@exception  RemoteException       Description of Exception
     *@exception  LeaseDeniedException  Description of Exception
     *@since
     */
    public EventRegistration trackChanges(long duration, ChangeEventListener rel, String changeOn) throws RemoteException, LeaseDeniedException;
}

