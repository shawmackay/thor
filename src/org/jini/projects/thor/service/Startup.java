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
 * thor : org.jini.projects.thor.service
 * 
 * 
 * Startup.java Created on 22-Dec-2003
 * 
 * Startup
 *  
 */

package org.jini.projects.thor.service;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;

/**
 * @author calum
 */
public class Startup {
	
	

	
	private static Configuration CONFIG;
	

	public static void main(String[] args) {
		try {
		     		
			CONFIG = ConfigurationProvider.getInstance(args);    
			new ThorServiceImpl(args, null);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println("Finished");
		System.exit(0);
		
	}

	public Startup(String[] args) throws Exception {
		
			
	}

	
	public static Configuration getConfiguration() {
		return CONFIG;
	}

	
	

	
}
