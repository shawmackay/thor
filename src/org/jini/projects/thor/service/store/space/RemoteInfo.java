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
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.jini.projects.thor.service.store.space;

import java.io.Serializable;

/**
 * @author calum
 */
public class RemoteInfo implements Serializable {
	String group;
	String name;
	String initialBranch;
	String ID;
	public RemoteInfo(){}
	
	public RemoteInfo( String ID,String group, String name, String initialBranch){
		this.group =group;
		this.name = name;
		this.initialBranch= initialBranch;
		this.ID = ID;	
	}
	
	
	/**
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @return
	 */
	public String getInitialBranch() {
		return initialBranch;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string
	 */
	public void setGroup(String string) {
		group = string;
	}

	/**
	 * @param string
	 */
	public void setInitialBranch(String string) {
		initialBranch = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param string
	 */
	public void setID(String string) {
		ID = string;
	}

}
