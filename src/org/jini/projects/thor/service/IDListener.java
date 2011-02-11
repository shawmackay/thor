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
 * IDListener.java
 * Created on 22-Dec-2003
 * 
 * IDListener
 *
 */
package org.jini.projects.thor.service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 * @author calum
 */
public class IDListener implements net.jini.lookup.ServiceIDListener {

    private final ThorServiceImpl impl;

    /**
     * @param ThorServiceImpl
     */
   public  IDListener(ThorServiceImpl impl) {
        this.impl = impl;
        // URGENT Complete constructor stub for IDListener
    }

    public void serviceIDNotify(net.jini.core.lookup.ServiceID serviceID) {
        System.out.println("Registered as: " + serviceID.toString());
        try {
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("data/serviceID_" + this.impl.name + ".ser")));
            oos.writeObject(serviceID);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            System.out.println("Err: Cannot save ServiceID");
        }
    }

}
