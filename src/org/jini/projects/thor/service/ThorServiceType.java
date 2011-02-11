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
 * ThorServiceDetails.java
 *
 * Created on 17 December 2001, 16:38
 */

package org.jini.projects.thor.service;

/**
 * Jini support class for displaying extra oinformation via a suitable client
 * such as Finder
 * @author  calum
 */

import java.awt.Image;

import javax.swing.ImageIcon;



public class ThorServiceType extends net.jini.lookup.entry.ServiceType {
    private String DisplayName = "Thor  v.0.1";
    private String Description = "Hierarchical Object Repository";


    /** Creates new ThorServiceDetails */
    public ThorServiceType() {
    }

    public java.lang.String getDisplayName() {
        return DisplayName;
    }

    public Image getIcon(int param) {

        ImageIcon imic = new ImageIcon(this.getClass().getResource("thor.gif"));
        ImageIcon imicmono = new ImageIcon(this.getClass().getResource("thormono.gif"));
        if (param == java.beans.BeanInfo.ICON_COLOR_16x16)
            return imic.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        if (param == java.beans.BeanInfo.ICON_COLOR_32x32)
            return imic.getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT);

        if (param == java.beans.BeanInfo.ICON_MONO_16x16)
            return imicmono.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        if (param == java.beans.BeanInfo.ICON_MONO_32x32)
            return imicmono.getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT);
        return imic.getImage();
    }

    public java.lang.String getShortDescription() {
        return Description;
    }

}
