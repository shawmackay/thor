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
 *  NamedItem.java
 *
 *  Created on 21 January 2002, 13:00
 */
package org.jini.projects.thor.service;

/**
 * Similar to Map.Entry. An item of key-value pair
 *@author     calum
 */
public class NamedItem implements java.io.Serializable {

    /**
     * The name or key of item
     *
     *@since 1.0b
     */
    public String name;
    /**
     *  The data or value of the item pair
     *
     *@since 1.0b
     */
    public Object data;
    /**
     *  The set of attributes on the item
     *
     *@since 1.0b
     */
    private java.util.HashMap attributes;


    /**
     *  Constructor for the NamedItem object
     *
     *@since 1.0b
     */
    public NamedItem() {
    }


    /**
     *  Constructor for the NamedItem object
     *
     *@param  name        Key of the item
     *@param  data        Value of the item
     *@param  attributes  Behavioural attributes of the item
     *@since 1.0b
     */
    public NamedItem(String name, Object data, java.util.HashMap attributes) {
        this.name = name;
        this.data = data;
        this.attributes = attributes;
    }


    /**
     *  Constructor for the NamedItem object
     *
     *@param  name        Key of the item
     *@param  data        Value of the item
     *@since 1.0b
     */
    public NamedItem(String name, Object data) {
        this.name = name;
        this.data = data;
        this.attributes = null;
    }


    /**
     *  Otains the stringified version of the Object . Just the name
     *
     *@return    the name from the pair
     *@since 1.0b
     */
    public String toString() {
        return name;
    }

}

