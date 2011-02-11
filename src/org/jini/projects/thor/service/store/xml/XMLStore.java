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
 * Created on 13-May-2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code Template
 */

package org.jini.projects.thor.service.store.xml;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jini.config.Configuration;

import org.jini.projects.thor.handlers.ConfigurationFileHandler;
import org.jini.projects.thor.handlers.FileHierarchy;
import org.jini.projects.thor.handlers.HierarchyHandler;
import org.jini.projects.thor.handlers.InternalHierarchy;
import org.jini.projects.thor.handlers.LinkHierarchy;
import org.jini.projects.thor.handlers.PropertyHandler;
import org.jini.projects.thor.handlers.RemoteThorHandler;
import org.jini.projects.thor.service.AttributeConstants;
import org.jini.projects.thor.service.store.Store;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Stores a tree to an XML File for reloading after class definitions have been
 * altered or for injecting a tree into another storage format
 * 
 * @author calum
 */
public class XMLStore implements Store {
	private int ID = 0;

	/**
	 *  
	 */
	public XMLStore() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void loadFromXML(HierarchyHandler branch, String filename) {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = dbf.newDocumentBuilder();
			FileInputStream fis = new FileInputStream(filename);
			doc = parser.parse(fis);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//InternalHierarchy h = new InternalHierarchy("TEST");
		buildDoc(branch, doc);
	}

	public static void main(String[] args) {
		new XMLStore().loadFromXML(new InternalHierarchy(),"/home/calum/workspace/thor.jini.org/src/teststore.xml");
	}

	private void pad(StringBuffer buffer,int num) {
		for (int i = 0; i < num; i++)
			buffer.append("    ");
	}

	public String storeRoot(HierarchyHandler root) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<!-- Thor tree exported on " + new Date() +" -->\n");               
		storeNode(buffer,root,1);        
        return buffer.toString();
	}

	public void storeNode(StringBuffer buffer, HierarchyHandler handle, int level) {
		for (int i = 0; i < handle.getNumChildren(); i++) {
			Object ob = handle.getChild(i, true);
			if (ob == null)
				System.out.println("Null ITEM...skipping");
			else if (ob instanceof HierarchyHandler) {
				//Check the type for what to do with it!
				if (ob instanceof InternalHierarchy) {
					pad(buffer,level);
					buffer.append("<branch name=\"" + handle.getName(i) + "\" id=\"" + handle.getBranchID() + "\" description=\"" + handle.getDescription() + "\">\n");
					storeNode(buffer,(InternalHierarchy) ob, level + 1);
					pad(buffer,level);
					buffer.append("</branch>\n");
				}
				if (ob instanceof PropertyHandler) {
					pad(buffer,level);
					PropertyHandler ph = (PropertyHandler) ob;
                    buffer.append("<props name=\"" + handle.getName(i) + "\" id=\"" + ph.getBranchID() + "\">\n");
					storeNode(buffer,(PropertyHandler) ob, level + 1);
					pad(buffer,level);
                    buffer.append("</props>\n");
				}
				if (ob instanceof RemoteThorHandler) {
					pad(buffer,level);
					RemoteThorHandler rhandle = (RemoteThorHandler) ob;
                    buffer.append("<remote name=\"" + handle.getName(i) + "\" servicename=\"" + rhandle.getName() + "\" group=\"" +
                            rhandle.getGroup() + "\" branch=\"" + rhandle.getInitialbranch() + "\" id=\"" + handle.getBranchID() + "\"/>\n");
				}
				if (ob instanceof FileHierarchy) {
					pad(buffer,level);
					FileHierarchy rhandle = (FileHierarchy) ob;
                    buffer.append("<file name=\"" + handle.getName(i) + "\" path=\"" + rhandle.getDescription() + "\" id=\"" + handle.getBranchID() + "\"/>\n");
				}
				if (ob instanceof LinkHierarchy) {
					pad(buffer,level);
					LinkHierarchy rhandle = (LinkHierarchy) ob;
                    buffer.append("<link name=\"" + handle.getName(i) + " \" path=\"" + rhandle.getDescription() + "\" id=\"" + handle.getBranchID() + "\"/>\n");
				}
				if(ob instanceof ConfigurationFileHandler){
					pad(buffer,level);
					ConfigurationFileHandler fh = (ConfigurationFileHandler) ob;
					buffer.append("<config name=\"" + handle.getName(i) + "\" path=\"" + fh.getConfigurationFileName() + "\" id=\"" + handle.getBranchID() + "\"/>\n" );
				}
			} else {
				pad(buffer,level);
                buffer.append("<item name=\"" + handle.getName(i) + "\" type=\"" + ob.getClass().getName() + "\">" + ob + "</item>\n");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.org.jini.projects.thor.service.store.Store#init()
	 */
	public void init(Configuration config) {
		// TODO Auto-generated method stub
	}

	/*
	 * @see org.jini.projects.thor.service.store.Store#loadRoot(java.lang.String)
	 */
	public HierarchyHandler loadRoot(String name) {
		Document d = null;
		HierarchyHandler h = new InternalHierarchy();
		buildDoc(h, d);
		return null;
	}

	private void buildDoc(HierarchyHandler h, Node parent) {
		NodeList list = parent.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = node.getNodeName();
				NamedNodeMap attribs = node.getAttributes();
				if (nodeName.equalsIgnoreCase("branch")) {
					String name = attribs.getNamedItem("name").getNodeValue();
					h.addBranch(name);
					HierarchyHandler handler = h.getBranch(name);
                    if(attribs.getNamedItem("id")!=null)
                    	handler.setBranchID(attribs.getNamedItem("id").getNodeValue());
                    if(attribs.getNamedItem("description")!=null)
					   handler.setDescription(attribs.getNamedItem("description").getNodeValue());
					buildDoc(handler, node);
				}
                if(nodeName.equalsIgnoreCase("root")){
                   
                    buildDoc(h, node);
                   
                }
				if (nodeName.equalsIgnoreCase("props")) {
					String name = attribs.getNamedItem("name").getNodeValue();                  
                    HashMap property_attribs = new HashMap();
                    property_attribs.put("PROPS", new Properties());
					h.addBranch(name, AttributeConstants.PROPERTY, property_attribs);
					HierarchyHandler handler = h.getBranch(name);                   
                    if(attribs.getNamedItem("id")!=null)
                        handler.setBranchID(attribs.getNamedItem("id").getNodeValue());
                    if(attribs.getNamedItem("description")!=null)
                    	handler.setDescription(attribs.getNamedItem("description").getNodeValue());
					buildDoc(handler, node);
				}
                
                if (nodeName.equalsIgnoreCase("file")) {
                    String name = attribs.getNamedItem("name").getNodeValue();                   
                    HashMap file_attribs = new HashMap();
                    file_attribs.put("FILEPATH", attribs.getNamedItem("path").getNodeValue());                    
                    h.addBranch(name, AttributeConstants.FILE, file_attribs);                    
                }
                
                if(nodeName.equalsIgnoreCase("config")){
                	String name = attribs.getNamedItem("name").getNodeValue();                    
                    HashMap file_attribs = new HashMap();
                    file_attribs.put("CONFIGFILEPATH", attribs.getNamedItem("path").getNodeValue());
                    h.addBranch(name, AttributeConstants.CONFIG, file_attribs);  
                }
                
                if (nodeName.equalsIgnoreCase("link")) {
                    String name = attribs.getNamedItem("name").getNodeValue();
                    HashMap file_attribs = new HashMap();
                    file_attribs.put("LINKPATH", attribs.getNamedItem("path").getNodeValue());                    
                    h.addBranch(name, AttributeConstants.LINK, file_attribs);
                    HierarchyHandler handler = h.getBranch(name);
                    if(attribs.getNamedItem("id")!=null)
                        handler.setBranchID(attribs.getNamedItem("id").getNodeValue());
                }
                
                if (nodeName.equalsIgnoreCase("remote")) {
                    String name = attribs.getNamedItem("name").getNodeValue();
                    HashMap remote_attribs = new HashMap();                    
                    remote_attribs.put("GROUP", attribs.getNamedItem("group").getNodeValue());
                    remote_attribs.put("SERVICE_NAME", attribs.getNamedItem("servicename").getNodeValue());
                    remote_attribs.put("INITIAL_BRANCH", attribs.getNamedItem("branch").getNodeValue());
                    h.addBranch(name, AttributeConstants.REMOTE, remote_attribs);
                    HierarchyHandler handler = h.getBranch(name);
                    if(attribs.getNamedItem("id")!=null)
                        handler.setBranchID(attribs.getNamedItem("id").getNodeValue());
                }
                
				if (nodeName.equalsIgnoreCase("item")) {
					String name = attribs.getNamedItem("name").getNodeValue();
					String type = attribs.getNamedItem("type").getNodeValue();
                   
					String value = node.getFirstChild().getNodeValue();                  
					Object o = buildType(value, type);
					if (o != null)
						h.add(name, o);
				}
			}
		}
	}

	private Object buildType(String value, String type) {
		if (type.equalsIgnoreCase("string")  || type.equalsIgnoreCase("java.lang.String"))
			return value;
		if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("java.lang.Integer"))
			return new Integer(value);
		if (type.equalsIgnoreCase("double")|| type.equalsIgnoreCase("java.lang.Double"))
			return new Double(value);
		if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("java.lang.Boolean"))
			return new Boolean(value);
		if (type.equalsIgnoreCase("long") || type.equalsIgnoreCase("java.lang.Long"))
			return new Long(value);
		if (type.equalsIgnoreCase("string") )
			return value;
		System.out.println("Unrecognised type");
		return null;
	}
}
