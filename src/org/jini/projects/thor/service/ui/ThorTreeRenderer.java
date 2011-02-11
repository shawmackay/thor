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
 *  ThorTreeRenderer.java
 *
 *  Created on 21 January 2002, 13:17
 */
package org.jini.projects.thor.service.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *@author     calum
 */

public class ThorTreeRenderer implements javax.swing.tree.TreeCellRenderer {

    private static ImageIcon remote = null;
    private static ImageIcon file = null;
    private static ImageIcon dir = null;
    private static ImageIcon internal = null;
    private static ImageIcon link = null;
    private static ImageIcon item = null;
    private static ImageIcon props = null;
    private static ImageIcon configfile = null;
    private static ImageIcon configcomponent = null;

    static {
        remote = new ImageIcon(ThorTreeRenderer.class.getResource("images/glass.jpg"));
        configcomponent = new ImageIcon(ThorTreeRenderer.class.getResource("images/purple-glass.jpg"));
        props = new ImageIcon(ThorTreeRenderer.class.getResource("images/green-glass.jpg"));
        configfile = new ImageIcon(ThorTreeRenderer.class.getResource("images/blue-glass.jpg"));
        file = new ImageIcon(ThorTreeRenderer.class.getResource("images/yellow-glass.jpg"));
        dir = new ImageIcon(ThorTreeRenderer.class.getResource("images/cyan-glass.jpg"));
        item = new ImageIcon(ThorTreeRenderer.class.getResource("images/chrome.jpg"));
        link = new ImageIcon(ThorTreeRenderer.class.getResource("images/red-glass.jpg"));
        internal = new ImageIcon(ThorTreeRenderer.class.getResource("images/gold.jpg"));
    }

    /**
     *  Creates a new instance of ThorTreeRenderer
     *
     *@since
     */
    public ThorTreeRenderer() {
        //System.out.println("Created ThorTreeRenderer");

    }


    /**
     *  Gets the treeCellRendererComponent attribute of the ThorTreeRenderer object
     *
     *@param  jTree     Description of Parameter
     *@param  tvalue    Description of Parameter
     *@param  selected  Description of Parameter
     *@param  expanded  Description of Parameter
     *@param  leaf      Description of Parameter
     *@param  row       Description of Parameter
     *@param  hasFocus  Description of Parameter
     *@return           The treeCellRendererComponent value
     *@since
     */
    public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree jTree, Object tvalue, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tvalue;
        Object value = node.getUserObject();
        
        if (value instanceof String) {
            JLabel returnComp = new JLabel();
            
            if (selected) {
                returnComp.setBackground(new Color(211, 211, 255));
                returnComp.setForeground(java.awt.Color.white);
                returnComp.setOpaque(true);
            } else {
                returnComp.setBackground(new java.awt.Color(204, 204, 255));
                returnComp.setForeground(java.awt.Color.black);
                returnComp.setOpaque(false);
            }
            //  if(hasFocus)
            //   returnComp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            returnComp.setFont(returnComp.getFont().deriveFont(java.awt.Font.PLAIN));
            returnComp.setText((String) value);
            return returnComp;
        }
        if (value instanceof org.jini.projects.thor.service.NamedItem) {
            org.jini.projects.thor.service.NamedItem item = (org.jini.projects.thor.service.NamedItem) value;
            JLabel returnComp = new JLabel();
            String datatype;
            setAntiAliasing(returnComp);
            if (item.data != null) {
                datatype = item.data.toString();
            } else {
                datatype = "null";
            }
            returnComp.setMinimumSize(new Dimension(24,48));
            
            if (datatype.startsWith("<<")) {

                returnComp.setText(item.name);
                if (datatype.equals("<<branch>>")) {
                    returnComp.setIcon(internal);
                    returnComp.setToolTipText("Internal Branch");
                }

                if (datatype.equals("<<linkbranch>>")) {
                    returnComp.setIcon(link);
                    returnComp.setToolTipText("Linked Branch");
                }

                if (datatype.equals("<<remotebranch>>")) {

                    returnComp.setIcon(remote);
                    returnComp.setToolTipText("Linked to remote instance");
                }
                if (datatype.equals("<<properties>>")) {
                    returnComp.setIcon(props);
                    returnComp.setToolTipText("Properties branch");
                }
                if (datatype.equals("<<file>>")) {
                    returnComp.setIcon(file);
                    returnComp.setToolTipText("Linked to file");
                }
                if (datatype.equals("<<dir>>")) {
                    returnComp.setIcon(dir);
                    returnComp.setToolTipText("Linked to directory");

                }
                if (datatype.equals("<<configbranch>>")) {
                    returnComp.setIcon(configfile);
                    returnComp.setToolTipText("Linked to Jini Configuration Handler");
                }
                if((datatype.equals("<<configcomponentbranch>>"))){
                    returnComp.setIcon(configcomponent);
                    returnComp.setToolTipText("Linked to Jini Configuration Component");
                }
                if (datatype.equals("<<listbranch>>")) {
                    returnComp.setToolTipText("Linked to Internal List");
                    returnComp.setIcon(internal);
                }
            } else {
                returnComp.setIcon(ThorTreeRenderer.item);
                returnComp.setText(item.name + ": " + datatype);
            }
            returnComp.setOpaque(true);
            if (selected) {
                returnComp.setBackground(new java.awt.Color(102, 102, 153));
                returnComp.setForeground(java.awt.Color.white);
                returnComp.setOpaque(true);
            } else {
                returnComp.setBackground(new java.awt.Color(204, 204, 255));
                returnComp.setForeground(java.awt.Color.black);
                returnComp.setOpaque(false);
            }
            //  if(hasFocus)
            //   returnComp.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            if (datatype.equals("<<linkbranch>>"))
                returnComp.setFont(returnComp.getFont().deriveFont(java.awt.Font.ITALIC));
            else
                returnComp.setFont(returnComp.getFont().deriveFont(java.awt.Font.PLAIN));
            return returnComp;
        }
        //System.out.println("Class: " + value.getClass().getName());
        JLabel ret = new JLabel(tvalue.toString());
        ret.setBackground(new java.awt.Color(204, 204, 255));
        return ret;
    }
    private void setAntiAliasing(JComponent comp) {
        Graphics2D g2 = (Graphics2D) comp.getGraphics();
        if(g2!=null)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    }
}

