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

package org.jini.projects.thor.service.store.space;

import net.jini.entry.AbstractEntry;

import org.jini.projects.thor.handlers.HierarchyHandler;

/**
 * User: calum
 * Date: 05-Jun-2003
 * Time: 14:26:45 
 */
public class BranchEntry extends AbstractEntry {
    public String ID;
    public HierarchyHandler branch;

    public BranchEntry() {
    }

    public BranchEntry(String ID, HierarchyHandler branch) {
        this.ID = ID;
        this.branch = branch;
    }

}
