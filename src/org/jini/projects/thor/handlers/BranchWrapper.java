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


package org.jini.projects.thor.handlers;

import org.jini.projects.thor.service.ChangeEventListener;

/*
 * BranchWrapper.java
 * 
 * Created on 08 October 2001, 12:50
 */
/**
 * @author calum
 * @version 1.0b
 */
public class BranchWrapper implements HierarchyHandler, java.io.Serializable {
	private Branch handle = null;

	/**
	 * Creates new BranchWrapper
	 * 
	 * @param remoteThor
	 *                   Description of Parameter
	 * @since
	 */
	public BranchWrapper(Branch remoteThor) {
		handle = remoteThor;
	}

	public void setDescription(String newName) {
		try {
			handle.setDescription(newName);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void setDataBlock(Object obj) {
		try {
			handle.setDataBlock(obj);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public Object getDataBlock() {
		try {
			return handle.getDataBlock();
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public int getNumChildren() {
		try {
			return handle.getNumChildren();
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return 0;
	}

	public String getDescription() {
		try {
			return handle.getDescription();
		} catch (Exception ex) {
			return "";
		}
	}

	public HierarchyHandler getBranch(String branchName) {
		try {
			Branch hand = handle.getBranch(branchName);
			if (hand != null)
				return new BranchWrapper(hand);
			else
				return null;
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public String getName(int idx) {
		try {
			return handle.getName(idx);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public Object getChild(String name, boolean withData) {
		try {
			return handle.getChild(name, withData);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public Object getChild(int Index, boolean withData) {
		try {
			return handle.getChild(Index, withData);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public Object getData() {
		try {
			return handle.getData();
		} catch (Exception ex) {
			return null;
		}
	}

	public Object getListenerFor(String name, int id) {
		return null;
	}

	public Object locate(String path) {
		try {
			Object x = null;
			x = handle.locate(path);
			System.out.println("Handle Class: " + x.getClass().getName());
			if (x instanceof Branch) {
				return new BranchWrapper((Branch) x);
			}
			if (x instanceof HierarchyHandler) {
				System.out.println("Wrappering <<HierarchyHandler>>");
				return getBranch(path);
			}
			return x;
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public void removeChild(String name) {
		try {
			handle.removeChild(name);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void removeChild(Object obj) {
		try {
			handle.removeChild(obj);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void add(String name, Object obj) {
		try {
			handle.add(name, obj);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public Object locate(String path, Object index) {
		try {
			return handle.locate(path, index);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public Object locate(String path, int index) {
		try {
			return handle.locate(path, index);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public void addBranch(String name) {
		try {
			handle.addBranch(name);
		} catch (Exception ex) {
		}
	}

	public Object locate(String path, String index) {
		try {
			return handle.locate(path, index);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

	public void overwrite(String name, Object newObj) {
		try {
			handle.overwrite(name, newObj);
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void removeAll() {
		try {
			handle.removeAll();
		} catch (Exception ex) {
			System.out.println("Err: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void addBranch(String name, int type, java.util.HashMap attributes) {
		try {
			handle.addBranch(name, type, attributes);
		} catch (Exception ex) {
		}
	}

	public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
		System.out.println("BranchWrapper: = Rel == null? " + (rel == null));
        System.out.println("Why is this being called!!!!");
		return null;
	}

	public void removeChangeEventListener(String name, int id) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.thor.handlers.HierarchyHandler#getBranchID()
	 */
	public String getBranchID() {
		// TODO Auto-generated method stub
		return "";
	}

	public void setBranchID(String ID) {
		// TODO Complete method stub for setBranchID
	}

	public void addXML(String XMLdata) throws UnsupportedOperationException {
		try {
			handle.addXML(XMLdata);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
    public String exportXML(){
    	try {
    		return handle.exportXML();
        } catch (Exception ex){
        	throw new RuntimeException(ex);
        }
    }
}
