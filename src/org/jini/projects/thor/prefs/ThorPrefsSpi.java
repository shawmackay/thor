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
 * ThorPrefsSpi.java
 *
 * Created on May 17, 2002, 12:04 PM
 */

package org.jini.projects.thor.prefs;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import org.jini.projects.thor.handlers.Branch;

/**
 *
 * @author  calum
 */
public class ThorPrefsSpi extends java.util.prefs.AbstractPreferences {

    private String myNodeName = "";
    private Branch handle;

    /** Creates a new instance of ThorPrefsSpi */

    public ThorPrefsSpi() {
        super(null, null);

    }

    public ThorPrefsSpi(AbstractPreferences parent, String name) {
        super(parent, name);
    }

    public ThorPrefsSpi(Branch handle, AbstractPreferences parent, String name) {
        super(parent, name);
        this.handle = handle;
    }

/*      public ThorPrefsSpi(Branch handle, String name) {
        this.handle=handle;
        this.name = name;
    }*/

    protected java.util.prefs.AbstractPreferences childSpi(String str) {
        //return new ThorPrefsSpi(
        try {
            ThorPrefsSpi childNode;
            System.out.println("Getting " + str);
            Branch branchhandle = handle.getBranch(str);
            if (branchhandle == null) {
                System.out.println("Adding a branch " + str);
                handle.addBranch(str);
            }
            childNode = new ThorPrefsSpi(handle.getBranch(str), this, str);
            return childNode;
        } catch (Exception ex) {
            System.out.println("Err in childSpi :" + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    protected String[] childrenNamesSpi() throws java.util.prefs.BackingStoreException {
        try {
            String[] ret = new String[handle.getNumChildren()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = handle.getName(i);
            }
            return ret;
        } catch (Exception ex) {
            System.out.println("Err in childrenNamesSpi: " + ex.getMessage());
            ex.printStackTrace();
            throw new BackingStoreException("Backing Store not available");
        }
    }

    protected void flushSpi() throws java.util.prefs.BackingStoreException {
    }

    protected String getSpi(String str) {
        try {
            return (String) handle.getChild(str, false);
        } catch (Exception ex) {
            System.out.println("Err in getSpi: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    protected String[] keysSpi() throws java.util.prefs.BackingStoreException {
        try {
            String[] ret = new String[handle.getNumChildren()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = handle.getName(i);
            }
            return ret;
        } catch (Exception ex) {
            System.out.println("Err in childrenNamesSpi: " + ex.getMessage());
            ex.printStackTrace();
            throw new BackingStoreException("Backing Store not available");
        }
    }

    protected void putSpi(String str, String str1) {
        try {
            handle.overwrite(str, str1);
        } catch (Exception ex) {
            System.out.println("Error in putSpi: " + ex.getMessage());
        }
    }

    protected void removeNodeSpi() throws java.util.prefs.BackingStoreException {
        try {
            this.parent().remove(this.name());
        } catch (Exception ex) {
            System.out.println("Error in removeNodeSpi: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    protected void removeSpi(String str) {
        try {
            handle.removeChild(str);
        } catch (Exception ex) {
            System.out.println("Error in removeSpi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected void syncSpi() throws java.util.prefs.BackingStoreException {

    }

}
