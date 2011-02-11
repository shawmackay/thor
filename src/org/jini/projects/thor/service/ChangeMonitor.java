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
 * ChangeMonitor.java
 *
 * Created on 09 January 2002, 15:12
 */

package org.jini.projects.thor.service;

/**
 * Static class that is called by all methods that change items within the tree.
 *  There is scope here for batching these updates, perhaps using ReliableLog
 *
 * @author  calum
 * @version 1.0b
 */
public class ChangeMonitor {
    public static String name;
	
    /** When called stores the whole tree to disk */
    public static void changeMade() {
  /*      try {
            System.out.println("Trying to Store!!!!");
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream("data/thortree_" + name.trim() + ".ser")));
            oos.writeObject(ThorServiceImpl.ROOT);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            System.out.println("Err: Thor tree could not be written" + ex.getMessage());
            ex.printStackTrace();
        }*/
    }
}
