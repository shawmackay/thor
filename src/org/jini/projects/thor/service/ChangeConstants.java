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
 *  ChangeConstants.java
 *
 *  Created on 28 February 2002, 14:26
 */
package org.jini.projects.thor.service;

/**
 * Event Listener Constants.
 * These are used in ChangeEventListener. <br>
 By ORing these items together, a client can obtain a combination of runtime event information about an entity within Thor
 *@author     calum

 */
public interface ChangeConstants {
    /**
     *  Client wishes to know of events where a read on an item occurs
     *
     *@since 1.0b
     */
    public final static int READ = 1;
    /**
     *  Client wishes to know of events where an <code>overwrite()</code> on an item occurs
     *
     *@since 1.0b
     */
    public final static int WRITE = 2;
    /**
     *  Client wishes to know of events where an <code>add()</code> on a branch occurs
     *
     *@since
     */
    public final static int CREATE = 4;
    /**
     *  Client wishes to know of events where an <code>remove()</code> on an item occurs
     *
     *@since 1.0b
     */
    public final static int DELETE = 8;
}

