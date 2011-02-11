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
 * ThorSession_Impl.java
 * 
 * Created on 18 September 2001, 11:35
 */

package org.jini.projects.thor.service;

import java.rmi.Remote;

import net.jini.id.UuidFactory;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ExporterManager;
import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.handlers.Branch_Impl;
import org.jini.projects.thor.service.store.StorageFactory;


/**
 * Concrete class implementing the Session Interface for the public ROOT
 * 
 * @author calum
 */
public class ThorSession_Impl implements ThorSession, Remote {

    //private Exporter exp;
    private Branch root;
    private Branch_Impl handler;

    /** Creates new ThorSession_Impl */
    public ThorSession_Impl() throws java.rmi.RemoteException {
    }

    public org.jini.projects.thor.handlers.Branch getRoot() throws java.rmi.RemoteException {
        if (root == null) {
            ExporterManager exp = DefaultExporterManager.getManager("default"); 
            //exp = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());

            handler = new Branch_Impl(StorageFactory.getBackend().getBranch("ROOT"));
            //Remote r = exp.export(handler);
            //root = ClientHandlerProxy.create((Branch) r, UuidFactory.generate());
            root = (Branch) exp.exportProxy(handler, "ClientHandlers", UuidFactory.generate());
        }
        return root;
    }

    /* @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        // TODO Complete method stub for finalize
        System.out.println(this.getClass().getName() + " finalized");
    }
}
