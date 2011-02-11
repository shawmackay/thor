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
package org.jini.projects.thor.service.store;

import java.util.HashMap;
import java.util.logging.Logger;

import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

import org.jini.projects.thor.handlers.HierarchyHandler;

/**
 * @author calum
 */
public class StorageFactory {
    private Store store;
    private static Backend backend;
    Logger log = Logger.getLogger("org.jini.projects.thor.store"); 
    /**
     * 
     */
    public StorageFactory(HashMap options) {
        super();
        // TODO Auto-generated constructor stub
        String storetype = System.getProperty("store.type","XML");
        String backendtype = System.getProperty("backend.type", "Flat");
        try {
            store = (Store) Class.forName("org.jini.projects.thor.service.store." + storetype.toLowerCase() + "." + storetype +"Store").newInstance();
            try {
				store.init(ConfigurationProvider.getInstance(new String[]{"config/store.config"}));
			} catch (ConfigurationException e1) {
				// URGENT Handle ConfigurationException
				log.info("Store configuration not available - using default store");
			}
            backend = (Backend) Class.forName("org.jini.projects.thor.service.store."+ backendtype +"Backend").newInstance();
            backend.init(options);
            log.info("Storage Factory Initialised");
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void StoreTree(HierarchyHandler root){
        store.storeRoot(root);
    }

    public static Backend getBackend(){
        return backend;
    }

    public static Backend getBackend(String backendtype, HashMap options){
        try {
            Backend ret =  (Backend) Class.forName("org.jini.projects.thor.service.store."+ backendtype +"Backend").newInstance();
            ret.init(options);
            return ret;
        } catch (InstantiationException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


	
	

}
