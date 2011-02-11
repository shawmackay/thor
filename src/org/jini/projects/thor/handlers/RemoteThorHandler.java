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
 * RemoteThorHandler.java
 * 
 * Created on 05 October 2001, 15:20
 */

package org.jini.projects.thor.handlers;

import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.ThorService;
import org.jini.projects.thor.service.ThorServiceImpl;

/**
 * @author calum
 * @version
 */
public class RemoteThorHandler implements HierarchyHandler, net.jini.discovery.DiscoveryListener, java.io.Serializable {
	static final long serialVersionUID = 668513934692704248L;
	private String ID;
	transient ThorService rThor = null;
	private transient Branch remoteRoot = null;
	private transient BranchWrapper wrap = null;
	private String name = null;
	private String group = null;
	private String initialbranch = null;
	private transient net.jini.discovery.LookupDiscoveryManager ldm = null;
	private int status = 0;
	private int LOOKING = 0;
	private int NOSERVICEFOUND = 1;
	private int SERVICEFOUND = 2;

	/**
	 * Creates new RemoteThorHandler
	 * 
	 * @since
	 */
	public RemoteThorHandler() {
		System.out.println("Searching again");
		if (ID == null)
			ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
		if (name != null && group != null && initialbranch == null) {
			register(group, name, initialbranch);
		} else if (name != null && group != null) {
			register(group, name, null);
		}
	}

	/**
	 * Constructor for the RemoteThorHandler object
	 * 
	 * @param group
	 *                   Description of Parameter
	 * @param name
	 *                   Description of Parameter
	 * @since
	 */
	public RemoteThorHandler(String group, String name) {
		if (ID == null)
			ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
		register(group, name, null);
	}

	/**
	 * Constructor for the RemoteThorHandler object
	 * 
	 * @param group
	 *                   Description of Parameter
	 * @param name
	 *                   Description of Parameter
	 * @param initialBranch
	 *                   Description of Parameter
	 * @since
	 */
	public RemoteThorHandler(String group, String name, String initialBranch) {
		register(group, name, initialBranch);
	}

	/**
	 * Sets the description attribute of the RemoteThorHandler object
	 * 
	 * @param newName
	 *                   The new description value
	 * @since
	 */
	public void setDescription(String newName) {
	}

	/**
	 * Sets the dataBlock attribute of the RemoteThorHandler object
	 * 
	 * @param obj
	 *                   The new dataBlock value
	 * @since
	 */
	public void setDataBlock(Object obj) {
	}

	/**
	 * Gets the numChildren attribute of the RemoteThorHandler object
	 * 
	 * @return The numChildren value
	 * @since
	 */
	public int getNumChildren() {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.getNumChildren();
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * Gets the branch attribute of the RemoteThorHandler object
	 * 
	 * @param branchName
	 *                   Description of Parameter
	 * @return The branch value
	 * @since
	 */
	public HierarchyHandler getBranch(String branchName) {
		try {
			if (wrap == null) {
				reregister();
			}
			if (wrap.getBranch(branchName) == null) {
				System.out.println("You have anull BRANCH!!!!");
			}
			return wrap.getBranch(branchName);
		} catch (Exception ex) {
			System.out.println("Returning null from the remote call");
			return null;
		}
	}

	/**
	 * Gets the name attribute of the RemoteThorHandler object
	 * 
	 * @param idx
	 *                   Description of Parameter
	 * @return The name value
	 * @since
	 */
	public String getName(int idx) {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.getName(idx);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the child attribute of the RemoteThorHandler object
	 * 
	 * @param name
	 *                   Description of Parameter
	 * @param withData
	 *                   Description of Parameter
	 * @return The child value
	 * @since
	 */
	public Object getChild(String name, boolean withData) {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.getChild(name, withData);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the child attribute of the RemoteThorHandler object
	 * 
	 * @param Index
	 *                   Description of Parameter
	 * @param withData
	 *                   Description of Parameter
	 * @return The child value
	 * @since
	 */
	public Object getChild(int Index, boolean withData) {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.getChild(Index, withData);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the dataBlock attribute of the RemoteThorHandler object
	 * 
	 * @return The dataBlock value
	 * @since
	 */
	public Object getDataBlock() {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.getDataBlock();
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Gets the description attribute of the RemoteThorHandler object
	 * 
	 * @return The description value
	 * @since
	 */
	public String getDescription() {
		return this.group + ":" + this.name + ":" + this.initialbranch;
	}

	/**
	 * Gets the data attribute of the RemoteThorHandler object
	 * 
	 * @return The data value
	 * @since
	 */
	public Object getData() {
		return wrap.getData();
	}

	/**
	 * Gets the listenerFor attribute of the RemoteThorHandler object
	 * 
	 * @param name
	 *                   Description of Parameter
	 * @param id
	 *                   Description of Parameter
	 * @return The listenerFor value
	 * @since
	 */
	public Object getListenerFor(String name, int id) {
		return null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param discoveryEvent
	 *                   Description of Parameter
	 * @since
	 */
	public void discovered(net.jini.discovery.DiscoveryEvent discoveryEvent) {
		try {
			System.out.println("Discovered");
			net.jini.core.lookup.ServiceRegistrar[] registrars = discoveryEvent.getRegistrars();
			Class[] classType = {ThorService.class};
			net.jini.lookup.entry.Name name = new net.jini.lookup.entry.Name();
			name.name = this.name;
			net.jini.core.entry.Entry[] attrs = {name};
			net.jini.core.lookup.ServiceTemplate svctmp = new net.jini.core.lookup.ServiceTemplate(null, classType, attrs);
			org.jini.projects.thor.service.ThorService thor = (org.jini.projects.thor.service.ThorService) registrars[0].lookup(svctmp);
			org.jini.projects.thor.service.ThorSession session = thor.getSession();
			Branch remoteRoot = session.getRoot();
			if (this.initialbranch == null || this.initialbranch == "") {
				wrap = new BranchWrapper(remoteRoot);
			} else {
				Branch remoteBranch;
				if (!this.initialbranch.equals(""))
					remoteBranch = remoteRoot.getBranch(this.initialbranch);
				else
					remoteBranch = remoteRoot;
				if (remoteBranch != null) {
					wrap = new BranchWrapper(remoteBranch);
				} else {
					//failsafe
					wrap = new BranchWrapper(remoteRoot);
				}
			}
			System.out.println("Got remote connection!");
			status = SERVICEFOUND;
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
			status = NOSERVICEFOUND;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param discoveryEvent
	 *                   Description of Parameter
	 * @since
	 */
	public void discarded(net.jini.discovery.DiscoveryEvent discoveryEvent) {
		wrap = null;
		remoteRoot = null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param path
	 *                   Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public Object locate(String path) {
		try {
			if (wrap == null) {
				reregister();
			}
			System.out.println("Locating through a wrapper");
			return wrap.locate(path);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Returned Value
	 * @since
	 */
	public java.util.Iterator iterator() {
		try {
			if (wrap == null) {
				reregister();
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param rootpath
	 *                   Description of Parameter
	 * @param name
	 *                   Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public Object[] seek(String rootpath, String name) {
		try {
			if (wrap == null) {
				reregister();
			}
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param path
	 *                   Description of Parameter
	 * @param index
	 *                   Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public Object locate(String path, Object index) {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.locate(path, index);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param path
	 *                   Description of Parameter
	 * @param index
	 *                   Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public Object locate(String path, int index) {
		try {
			if (wrap == null) {
				reregister();
			}
			return wrap.locate(path, index);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param path
	 *                   Description of Parameter
	 * @param index
	 *                   Description of Parameter
	 * @return Description of the Returned Value
	 * @since
	 */
	public Object locate(String path, String index) {
		try {
			if (wrap == null) {
				reregister();
			}
			System.out.println("Looking remotely for: " + index + " in " + path);
			return wrap.locate(path, index);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param name
	 *                   Description of Parameter
	 * @since
	 */
	public void removeChild(String name) {
		try {
			if (wrap == null) {
				reregister();
			}
			wrap.removeChild(name);
		} catch (Exception ex) {
			System.out.println("Err: ");
			ex.printStackTrace();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param name
	 *                   Description of Parameter
	 * @param obj
	 *                   Description of Parameter
	 * @since
	 */
	public void add(String name, Object obj) {
		try {
			if (wrap == null) {
				reregister();
			}
			wrap.add(name, obj);
		} catch (Exception ex) {
			System.out.println("Err: ");
			ex.printStackTrace();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param obj
	 *                   Description of Parameter
	 * @since
	 */
	public void removeChild(Object obj) {
		try {
			if (wrap == null) {
				reregister();
			}
			wrap.removeChild(obj);
		} catch (Exception ex) {
			System.out.println("Err: ");
			ex.printStackTrace();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param name
	 *                   Description of Parameter
	 * @param newObj
	 *                   Description of Parameter
	 * @since
	 */
	public void overwrite(String name, Object newObj) {
		try {
			if (wrap == null) {
				reregister();
			}
			wrap.overwrite(name, newObj);
		} catch (Exception ex) {
			System.out.println("Err: ");
			ex.printStackTrace();
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param child
	 *                   Description of Parameter
	 * @param receiver
	 *                   Description of Parameter
	 * @since
	 */
	public void move(Object child, HierarchyHandler receiver) {
	}

	/**
	 * Description of the Method
	 * 
	 * @since
	 */
	public void removeAll() {
		try {
			if (wrap == null) {
				reregister();
			}
			wrap.removeAll();
		} catch (Exception ex) {
			System.out.println("Err: ");
			ex.printStackTrace();
		}
	}

	/**
	 * Adds a feature to the Branch attribute of the RemoteThorHandler object
	 * 
	 * @param name
	 *                   The feature to be added to the Branch attribute
	 * @since
	 */
	public void addBranch(String name) {
		try {
			if (wrap == null) {
				reregister();
			}
			wrap.addBranch(name);
		} catch (Exception ex) {
			System.out.println("Err: ");
			ex.printStackTrace();
		}
	}

	/**
	 * inserts a sub-branch into the current branch
	 * 
	 * @param name
	 *                   Local name of the node to make a branch for
	 * @param type
	 *                   The feature to be added to the Branch attribute
	 * @param attributes
	 *                   The feature to be added to the Branch attribute
	 * @since
	 */
	public void addBranch(String name, int type, java.util.HashMap attributes) {
		wrap.addBranch(name, type, attributes);
	}

	/**
	 * Adds a feature to the ChangeEventListener attribute of the
	 * RemoteThorHandler object
	 * 
	 * @param rel
	 *                   The feature to be added to the ChangeEventListener attribute
	 * @param name
	 *                   The feature to be added to the ChangeEventListener attribute
	 * @return Description of the Returned Value
	 * @since
	 */
	public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
		return null;
	}

	/**
	 * Description of the Method
	 * 
	 * @param name
	 *                   Description of Parameter
	 * @param id
	 *                   Description of Parameter
	 * @since
	 */
	public void removeChangeEventListener(String name, int id) {
	}

	/**
	 * Description of the Method
	 * 
	 * @since
	 */
	private void reregister() {
		System.out.println("Re-registering");
		register(this.group, this.name, this.initialbranch);
	}

	/**
	 * Description of the Method
	 * 
	 * @param group
	 *                   Description of Parameter
	 * @param name
	 *                   Description of Parameter
	 * @param initialBranch
	 *                   Description of Parameter
	 * @since
	 */
	private void register(String group, String name, String initialBranch) {
		//Assertion: the combination of group and name will yield only
		//                  one service through lookup
		this.group = group;
		this.name = name;
		this.initialbranch = initialBranch;
		String[] GROUPS = {group};
		status = LOOKING;
		try {
			ldm = new net.jini.discovery.LookupDiscoveryManager(GROUPS, null, this);
			while (status == LOOKING) {
				Thread.yield();
			}
			if (status == SERVICEFOUND) {
				System.out.println("Requested Service Found");
			} else {
				System.out.println("No matching Service Found");
			}
			ldm.terminate();
		} catch (Exception ex) {
			System.out.println("Err:" + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
	}

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
        throw new UnsupportedOperationException("This branch does not support subBranches via XML - access the remote tree directly");
    }
	/**
	 * @return Returns the group.
	 */
	public String getGroup() {
		return this.group;
	}
	/**
	 * @return Returns the initialbranch.
	 */
	public String getInitialbranch() {
		return this.initialbranch;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
    
    public String exportXML() {
              
        return "<root></root>";
    }
}
