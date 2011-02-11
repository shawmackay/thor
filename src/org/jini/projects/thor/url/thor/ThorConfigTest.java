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
 * thor.jini.org : org.jini.projects.thor.url.thor
 * 
 * 
 * ThorConfigTest.java
 * Created on 14-Apr-2004
 * 
 * ThorConfigTest
 *
 */
package org.jini.projects.thor.url.thor;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

/**
 * @author calum
 */
public class ThorConfigTest {
    public static void main(String[] args) throws ConfigurationException{
    	Configuration config = ConfigurationProvider.getInstance(args);
       String[] defs = (String[])config.getEntry("ExportManager", "mgrDefs", String[].class);
       for(int i=0;i<defs.length;i++)
       	System.out.println("Definition: " + defs[i]);
    }
}
