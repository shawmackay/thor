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
 * PrefRunner.java
 *
 * Created on May 17, 2002, 1:56 PM
 */

package org.jini.projects.thor.prefs;

import java.util.prefs.Preferences;

/**
 *
 * @author calum
 */
public class PrefRunner {

    /** Creates a new instance of PrefRunner */
    public PrefRunner() {
        System.getProperties().setProperty("thor.prefs.group", "uk.co.cwa.jini2");
        System.getProperties().setProperty("thor.prefs.name", "xmltest");
        System.getProperties().setProperty("java.util.prefs.PreferencesFactory", "org.jini.projects.thor.prefs.ThorPreferencesFactory");
        long start = System.currentTimeMillis();
        Preferences pf = Preferences.systemRoot().node("/the/first/node");
        System.out.println("obtaining took: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        pf.parent().put("MyData", "data");
        System.out.println("Putting MyData took: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        Preferences uf = Preferences.userRoot();
        System.out.println("obtaining took: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        uf.put("Hello", "there");
        System.out.println("obtaining took: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        uf.node("my/first/user/node");
        System.out.println("obtaining took: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        Preferences sf = Preferences.systemNodeForPackage(getClass());
        Preferences uuf = Preferences.userNodeForPackage(getClass());
        sf.put("Packaged up nicely", "hmmm");
        System.out.println("obtaining took: " + (System.currentTimeMillis() - start));
        try {
            System.out.println("Completed");
        } catch (Exception ex) {
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PrefRunner();

    }

}
