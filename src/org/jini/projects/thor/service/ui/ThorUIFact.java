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
 * ThorUIFact.java
 *
 * Created on 18 January 2002, 15:15
 */

package org.jini.projects.thor.service.ui;

import javax.swing.JComponent;

import net.jini.core.lookup.ServiceItem;

/**
 *
 * @author  calum
 */

public class ThorUIFact implements net.jini.lookup.ui.factory.JComponentFactory, java.io.Serializable {

    /**
     *  Creates new AthenaUIFact
     */
    public ThorUIFact() {
    }


    /**
     *  Returns a <CODE>JComponent</CODE> .
     *
     *@param  roleObject  Description of Parameter
     *@return             The JComponent value
     */
    public JComponent getJComponent(Object roleObject) {
        ServiceItem svItem = (ServiceItem) roleObject;
        ThorPanel thorpanel = new ThorPanel((org.jini.projects.thor.service.ThorService) svItem.service);
        return thorpanel;
    }

}
