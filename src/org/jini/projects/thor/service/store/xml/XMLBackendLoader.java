package org.jini.projects.thor.service.store.xml;

import org.jini.projects.thor.handlers.InternalHierarchy;
import org.jini.projects.thor.service.store.BackendLoader;
import org.jini.projects.thor.service.store.StorageFactory;

public class XMLBackendLoader implements BackendLoader {

	public void initialise(String file) {
        if (!file.equals("")) {
            XMLStore store = new XMLStore();
            InternalHierarchy root = new InternalHierarchy();
            root.setBranchID("ROOT");
            StorageFactory.getBackend().insertBranch(null, root);
            store.loadFromXML(root, file);
        } else {           
            InternalHierarchy root = new InternalHierarchy();
            root.setBranchID("ROOT");
            StorageFactory.getBackend().insertBranch(null, root);
        }
        StorageFactory.getBackend().store();
        // root.displayTree(0);
    }

}
