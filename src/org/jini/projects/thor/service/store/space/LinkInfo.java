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
public class LinkInfo implements Serializable {
	String path;
	String ID;
	public LinkInfo(){}
	public LinkInfo(String ID,String link){
		this.path = link;
		this.ID = ID;
	}
	/**
	 * @return
	 */
	public String getLink() {
		return path;
	}

	/**
	 * @param string
	 */
	public void setLink(String linkName) {
		path = linkName;
	}

}
