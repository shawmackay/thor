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
 *  AttributeConstants.java
 *
 *  Created on 23 January 2002, 13:58
 */
package org.jini.projects.thor.service;

/**
 * Branch type constants
 *@author     calum
 */
public interface AttributeConstants {

    /**
     *  Constant to reference an InternalHierarchy Class
     * @see org.jini.projects.thor.handlers.InternalHierarchy
     *@since 1.0.b
     */
    public final static int INTERNAL = 1;
    /**
     * Indicates a branch should be added as a Properties branch, holding all data as String,String maps 
     * @see org.jini.projects.thor.handlers.PropertyHandler
     *
     *@since 1.0b
     */
    public final static int PROPERTY = 2;
    /**
     * Indicates a branch should be added as a File link
     * @see org.jini.projects.thor.handlers.FileHierarchy
     *@since 1.0b
     */
    public final static int FILE = 3;
    /**
     *Indicates a branch should be added as a Link to another part of the tree
     *
     * @see org.jini.projects.thor.handlers.LinkHierarchy
     *@since 1.0b
     */
    public final static int LINK = 4;
    /**
     *  Indicates a branch should be added as a Remote Link
     * @see org.jini.projects.thor.handlers.RemoteThorHandler
     *@since 1.0.b
     */
    public final static int REMOTE = 5;

    /**
     * 
     * Indicates a branch should be added as a Config handler that links to a Jini configuration file
     * @see org.jini.projects.thor.handlers.ConfigurationFileHandler
     * @since 1.0b3
     */
    public final static int CONFIG = 6;
    
    /** Indicates a branch should be added as a ListHandler, allowing index-named items.
     * The names of items in a ListHandler, are based upon the index of the object in the list
     * at the current point in time, cosnequently
     * ListHandlers are non-deterministic. I.e. an object A at index I, is not guaranteed to
     * remain at index I, and conversely, the lack of an item at index I does not imply that
     * the object A has been removed.
     */
    public final static int LIST = 7;
}

