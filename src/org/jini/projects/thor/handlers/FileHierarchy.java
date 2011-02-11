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
* FileHierarchy.java
*
* Created on 07 September 2001, 14:09
*/

package org.jini.projects.thor.handlers;

import java.io.File;
import java.io.FileInputStream;

import org.jini.projects.thor.service.AttributeConstants;
import org.jini.projects.thor.service.ChangeEventListener;
import org.jini.projects.thor.service.NamedItem;
import org.jini.projects.thor.service.ThorServiceImpl;

/**
* File Hierarchy Handler locate recursively
*
* @author calum
* @version
*/
public class FileHierarchy implements HierarchyHandler, java.io.Serializable {
	static final long serialVersionUID = -2644432001606536179L;
	private String ID;
	private String rootpath;
	private File root;
	
	/**
	* Creates new FileHierarchy
	*
	* @param Filepath
	*                   Description of Parameter
	* @since
	*/
	public FileHierarchy(String Filepath) {
		if (ID == null)
			ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
		rootpath = Filepath;
		root = new File(Filepath);
	}
	
	/**
	* Constructor for the FileHierarchy object
	*
	* @since
	*/
	public FileHierarchy() {
		if (ID == null)
			ID = String.valueOf(System.currentTimeMillis()) + String.valueOf(ThorServiceImpl.getRandomID());
		if (rootpath != null) {
			root = new File(rootpath);
		}
	}
	
	public void setDescription(String newName) {
	}
	
	public void setDataBlock(Object obj) {
	}
	
	public int getNumChildren() {
		if (root.isDirectory()) {
			return root.listFiles().length;
		}
		return 0;
	}
	
	public HierarchyHandler getBranch(String branchName) {
		return new FileHierarchy(rootpath + System.getProperty("file.separator") + branchName);
	}
	
	public String getName(int idx) {
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			return files[idx].getName();
		}
		return null;
	}
	
	public Object getChild(int Index, boolean withData) {
		System.out.println("Getting child");
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			File retval = files[Index];
			if (retval.exists()) {
				if (retval.isDirectory()) {
					return new FileHierarchy(retval.getPath());
				} else {
					try {
						if (withData) {
							FileInputStream fis = new FileInputStream(retval);
							byte[] array = new byte[(int) retval.length()];
							fis.read(array);
							return array;
						} else {
							return null;
						}
					} catch (Exception ex) {
						return null;
					}
				}
			}
		}
		System.out.println("Returning null");
		return null;
	}
	
	public Object getDataBlock() {
		java.util.Vector data = new java.util.Vector();
		File[] flist = root.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				NamedItem item = new NamedItem(flist[i].getName(), "<<dir>>");
				data.add(item);
			} else {
				NamedItem item = new NamedItem(flist[i].getName(), "<<file>>");
				data.add(item);
			}
		}
		return data;
	}
	
	public Object getChild(String name, boolean withData) {
		File retval = new File(rootpath + System.getProperty("file.separator") + name);
		System.out.println("Getting child");
		if (retval.exists()) {
			if (retval.isDirectory()) {
				return new FileHierarchy(rootpath + System.getProperty("file.separator") + name);
			} else {
				try {
					if (withData) {
						FileInputStream fis = new FileInputStream(retval);
						byte[] array = new byte[(int) retval.length()];
						fis.read(array);
						return array;
					} else {
						return null;
					}
				} catch (Exception ex) {
					return null;
				}
			}
		}
		return null;
	}
	
	public String getDescription() {
		return this.rootpath;
	}
	
	public Object getData() {
		return getDataBlock();
	}
	
	public Object getListenerFor(String name, int id) {
		return null;
	}
	
	public Object locate(String path) {
		File retval;
		retval = new File(root, path);
		if (retval.exists()) {
			if (retval.isDirectory()) {
				return new FileHierarchy(rootpath + System.getProperty("file.separator") + path);
			} else {
				try {
					FileInputStream fis = new FileInputStream(retval);
					byte[] array = new byte[(int) retval.length()];
					fis.read(array);
					return array;
				} catch (Exception ex) {
					return null;
				}
			}
		} else {
			return null;
		}
	}
	
	public Object locate(String path, Object index) {
		File locale = new File(root + System.getProperty("file.separator") + path);
		//we assume the Object is a filename
		if (locale.isDirectory()) {
			File[] files = locale.listFiles();
			File retval = null;
			;
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals((String) index)) {
					retval = files[i];
				}
			}
			if (retval != null) {
				if (retval.exists()) {
					if (retval.isDirectory()) {
						return new FileHierarchy(retval.getPath());
					} else {
						try {
							FileInputStream fis = new FileInputStream(retval);
							byte[] array = new byte[(int) retval.length()];
							fis.read(array);
							return array;
						} catch (Exception ex) {
							return null;
						}
					}
				}
			}
		}
		System.out.println("Returning null");
		return null;
	}
	
	public Object locate(String path, int index) {
		System.out.println("Locating within your path");
		File locale = new File(root + System.getProperty("file.separator") + path);
		//we assume the Object is a filename
		if (locale.isDirectory()) {
			File[] files = locale.listFiles();
			File retval;
			retval = files[index];
			if (retval != null) {
				if (retval.exists()) {
					if (retval.isDirectory()) {
						return new FileHierarchy(rootpath + System.getProperty("file.separator") + path);
					} else {
						try {
							FileInputStream fis = new FileInputStream(retval);
							byte[] array = new byte[(int) retval.length()];
							fis.read(array);
							return array;
						} catch (Exception ex) {
							return null;
						}
					}
				}
			}
		}
		System.out.println("Returning null");
		return null;
	}
	
	public Object locate(String path, String index) {
		File locale = new File(root + System.getProperty("file.separator") + path);
		//we assume the Object is a filename
		System.out.println("Locating within your path: " + index);
		if (locale.isDirectory()) {
			File[] files = locale.listFiles();
			File retval = null;
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals(index)) {
					retval = files[i];
				}
			}
			if (retval != null) {
				if (retval.exists()) {
					if (retval.isDirectory()) {
						return new FileHierarchy(retval.getPath());
					} else {
						try {
							FileInputStream fis = new FileInputStream(retval);
							byte[] array = new byte[(int) retval.length()];
							fis.read(array);
							return array;
						} catch (Exception ex) {
							return null;
						}
					}
				}
			}
		}
		System.out.println("Returning null");
		return null;
	}
	
	public java.util.Iterator iterator() {
		return null;
	}
	
	public Object[] seek(String rootpath, String name) {
		return null;
	}
	public void removeChild(String name) {
		File f = new File(rootpath + File.separatorChar + name);
		if(f.exists()){
			if(f.isDirectory()){
				//recursively delete all children
				deleteDir(f);
				return;
			}
			//delete the root file or dir
			f.delete();
		}
	}
	
	private void deleteDir(File dir) {
		if(dir.isFile()){
			dir.delete();
			return;
		}		
		String[] children = dir.list();
		for (int i = 0; i< children.length; i++) {
			deleteDir(new File(dir, children[i]));
		}
		dir.delete();
	}
	
	public void add(String name, Object obj) {
		try {
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(rootpath + File.pathSeparatorChar + name)));
			oos.writeObject(obj);
		} catch (Exception ex) {
			System.out.println("Error:  " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void removeChild(Object obj) {
	}
	
	public void removeAll() {
	}
	
	public void addBranch(String name) {
		try {
			File newDir = new File(rootpath + File.pathSeparatorChar + name);
			newDir.mkdir();
		} catch (Exception ex) {
			System.out.println("Error:  " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void overwrite(String name, Object newObj) {
	}
	
	public void addBranch(String name, int type, java.util.HashMap attributes) {
		if (type == AttributeConstants.FILE) {
			addBranch(name);
		}
	}
	
	public net.jini.core.event.EventRegistration addChangeEventListener(ChangeEventListener rel, String name) {
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
}
