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
 * ConfigurationComponentHandler.java
 * Created on 14-Dec-2004
 * 
 * ConfigurationComponentHandler
 *
 */

package org.jini.projects.thor.handlers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.StringTokenizer;

import net.jini.core.event.EventRegistration;

import org.jini.projects.thor.handlers.support.ConfigurationComponent;
import org.jini.projects.thor.handlers.support.ConfigurationEntry;
import org.jini.projects.thor.service.ChangeConstants;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.NamedItem;
import org.jini.projects.thor.service.ThorServiceImpl;
import org.jini.projects.thor.service.store.BranchIdentifier;
import org.jini.projects.thor.service.store.StorageFactory;

/**
 * This represents a Configuration Component as a branch in Thor. We use a
 * specialised handler because we want to enable changes to propagate up to the
 * configuration file level and because we also want to be able to reconstitute
 * the configuration as well
 * 
 * @author calum
 */
public class ConfigurationComponentHandler implements HierarchyHandler, Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3258407344076699960L;

	ConfigurationComponent holder;
	
	ConfigurationFileHandler configFileHandler;
	private String ID;

	public ConfigurationComponentHandler(ConfigurationComponent c, ConfigurationFileHandler configFileHandler) {
		holder = c;
	
		this.configFileHandler = configFileHandler;
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#locate(java.lang.String)
	 */
	public Object locate(String path) {
		StringTokenizer strtok = new StringTokenizer(path, "/");
		// System.out.println("Path:" + path);
		// System.out.println(strtok.countTokens());
		if (strtok.countTokens() == 1) {
			Object ret = holder.getEntry(path);
			if (ret instanceof BranchIdentifier) {
				String ID = ((BranchIdentifier) ret).getBranchID();
				ret = StorageFactory.getBackend().getBranch(ID);
			}
			return ret;
		}
		return null;
	}

	public Object locate(String path, int index) {
		StringTokenizer strtok = new StringTokenizer(path, "/");
		if (strtok.countTokens() == 0) {
			if (index <= holder.getEntryNames().length) {
				return holder.getEntry(holder.getEntryNames()[index]);
			}
		}
		return null;
	}

	public Object locate(String path, Object index) {

		return null;
	}

	public Object locate(String path, String index) {
		return null;
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getChild(int,
	 *           boolean)
	 */
	public Object getChild(int Index, boolean withData) {
		// TODO Complete method stub for getChild
		return holder.getEntry(holder.getEntryNames()[Index]);
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getChild(java.lang.String,
	 *           boolean)
	 */
	public Object getChild(String name, boolean withData) {
		// TODO Complete method stub for getChild
		return holder.getEntry(name);
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getName(int)
	 */
	public String getName(int idx) {
		// TODO Complete method stub for getName
		return holder.getEntryNames()[idx];
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
		return holder.getEntryNames().length;
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getDataBlock()
	 */
	public Object getDataBlock() {
		// TODO Complete method stub for getDataBlock
		java.util.Vector vec = new java.util.Vector();
		// These vectors only return data on this level
		// Any hierarchies are ignored

		for (int i = 0; i < this.holder.getEntryNames().length; i++) {
			Object ob = holder.getEntry(holder.getEntryNames()[i]);
			Object viewOb;
			if (ob instanceof BranchIdentifier) {
				String ID = ((BranchIdentifier) ob).getBranchID();
				viewOb = StorageFactory.getBackend().getBranch(ID);
			} else {
				viewOb = ob;
			}

			if (!(viewOb instanceof HierarchyHandler)) {
				vec.add(new NamedItem(holder.getEntryNames()[i], viewOb));
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
					System.out.println("Getting config branch");
					tag = "<<configbranch>>";
				}
				if (viewOb instanceof ListHandler) {
					System.out.println("Getting list branch");
					tag = "<<listbranch>>";
				}
				vec.add(new NamedItem((String) holder.getEntryNames()[i], tag));

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
		// TODO Complete method stub for add
		overwrite(name, obj);
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
		ConfigurationEntry entry;
		entry = holder.getEntry(name);
		if (entry != null) {
			entry.setAssignedexpression((String) newObj);
		} else {
			entry = new ConfigurationEntry();
			entry.setComment("Gen. comment: Entry for " + name + "<br/>Type: Object");
			entry.setVariable(name);
			entry.setAssignedexpression((String) newObj);
			holder.addEntry(entry);
		}
		configFileHandler.fireGeneralListeners(ChangeConstants.WRITE,holder.getComponentName(),name, newObj.toString());
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#removeChild(java.lang.Object)
	 */
	public void removeChild(Object obj) {
		// TODO Complete method stub for removeChild

	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#removeChild(java.lang.String)
	 */
	public void removeChild(String name) {
		// TODO Complete method stub for removeChild

	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#removeAll()
	 */
	public void removeAll() {
		// TODO Complete method stub for removeAll

	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getData()
	 */
	public Object getData() {
		// TODO Complete method stub for getData
		return null;
	}

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#addChangeEventListener(org.jini.projects.thor.service.ChangeEventListener,
	 *           java.lang.String)
	 */
	public EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
		// TODO Complete method stub for addChangeEventListener
		return configFileHandler.addChangeEventListener(rel, name);
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

	/*
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#setBranchID(java.lang.String)
	 */
	public void setBranchID(String ID) {
		// TODO Complete method stub for setBranchID
		this.ID = ID;
	}

	public String getBranchID() {
		// TODO Auto-generated method stub
		if (ID == null)
			ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
		return ID;
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

}
