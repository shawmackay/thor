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
 * TestPanel.java
 * 
 * Created on May 1, 2002, 10:33 AM
 */

package org.jini.projects.thor.service.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.jini.projects.thor.configuration.ConfigurationFileWrapper;
import org.jini.projects.thor.handlers.Branch;
import org.jini.projects.thor.service.AttributeConstants;
import org.jini.projects.thor.service.NamedItem;
import org.jini.projects.thor.service.ThorService;
import org.jini.projects.thor.service.ThorSession;

/**
 * @author Internet
 */
public class ThorPanel
                extends
                javax.swing.JPanel
                implements
                java.io.Serializable,
                net.jini.lookup.ui.MainUI,
                org.jini.projects.thor.service.AttributeConstants {
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JLabel branchDescription;

        private javax.swing.JButton rootButton;

        private javax.swing.JLabel jLabel1;

        private javax.swing.JPanel mainPanel;

        private javax.swing.JPanel buttonPanel;

        private javax.swing.JPanel commandPanel;

        private javax.swing.JPopupMenu jPopupMenu1;

        private javax.swing.JScrollPane jScrollPane1;

        private javax.swing.JSeparator jSeparator1;

        private javax.swing.JTree branchTree;

        private javax.swing.JMenuItem menuAddFileSystem;

        private javax.swing.JMenuItem menuAddLink;

        private javax.swing.JMenuItem menuAddProperties;

        private javax.swing.JMenuItem menuAddRemote;

        private javax.swing.JMenuItem menuBranch;

        private javax.swing.JMenu menuHandler;

        private javax.swing.JMenuItem menuItemAdd;

        private javax.swing.JMenuItem menuItemExportXML;

        private javax.swing.JMenuItem menuItemDel;

        private javax.swing.JMenuItem menuItemModify;

        private Branch root;

        private ThorSession session;

        private ThorService thorReg;

        private DefaultTreeModel treemodel;

        private javax.swing.JPanel treePanel;

        private DefaultMutableTreeNode treeroot = new DefaultMutableTreeNode("Root");

        private JOptionPane optionPane;

        /** Creates new form TestPanel */
        public ThorPanel() {
                this.setFont(new java.awt.Font("Dialog", 0, 12));
                initComponents();
                optionPane = new JOptionPane();
                optionPane.setFont(new Font("Dialog", 0, 12));
        }

        public ThorPanel(ThorService reg) {
                this();
                thorReg = reg;
                loadFromRoot();
        }

        private void forceReload(TreePath path, String separatedPath) {
                try {
                        DefaultMutableTreeNode selnode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        NamedItem selitem = (NamedItem) selnode.getUserObject();
                        if (selitem.data instanceof String) {
                                String datatype = (String) selitem.data;
                                if (datatype.startsWith("<<") && !(datatype.equals("<<file>>"))) {
                                        System.out.println(separatedPath.toString());
                                        Branch rbranch = root.getBranch(separatedPath.toString());
                                        if (rbranch != null) {
                                                // DefaultMutableTreeNode
                                                // parentnode =
                                                // (DefaultMutableTreeNode)
                                                // path.getLastPathComponent();
                                                while (selnode.getChildCount() != 0) {
                                                        // System.out.print("removing,
                                                        // ");
                                                        treemodel.removeNodeFromParent((MutableTreeNode) selnode.getChildAt(0));
                                                }
                                                java.util.Vector data = (java.util.Vector) rbranch.getDataBlock();
                                                // System.out.println(rbranch.getClass().getName());
                                                branchDescription.setText(rbranch.getDescription());
                                                for (int i = 0; i < data.size(); i++) {
                                                        NamedItem item = (NamedItem) data.get(i);
                                                        // System.out.println("Item
                                                        // " + i + " : " +
                                                        // item.name);
                                                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
                                                        treemodel.insertNodeInto(node, selnode, selnode.getChildCount());
                                                }
                                                // treemodel.reload();
                                        }
                                }
                                // jTree1.updateUI();
                        }
                } catch (Exception ex) {
                        System.out.println("Err: " + ex.getMessage());
                        ex.printStackTrace();
                }
                // System.out.println(separatedPath.toString());
        }

        /**
         * Gets the thorPath attribute of the ThorPanel object
         * 
         * @param path
         *                Description of Parameter
         * @return The thorPath value
         * @since
         */
        private String getThorPath(TreePath path) {
                Object[] pathcomp = path.getPath();
                StringBuffer separatedPath = new StringBuffer();
                for (int i = 1; i < pathcomp.length - 1; i++) {
                        separatedPath.append(pathcomp[i] + "/");
                }
                String mypath = separatedPath.toString();
                separatedPath.append(path.getLastPathComponent());
                return separatedPath.toString();
        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        private void initComponents() {// GEN-BEGIN:initComponents
                java.awt.GridBagConstraints gridBagConstraints;
                jPopupMenu1 = new javax.swing.JPopupMenu();
                menuItemAdd = new javax.swing.JMenuItem();
                menuItemExportXML = new javax.swing.JMenuItem();
                menuItemDel = new javax.swing.JMenuItem();
                menuItemModify = new javax.swing.JMenuItem();
                menuBranch = new javax.swing.JMenuItem();
                jSeparator1 = new javax.swing.JSeparator();
                menuHandler = new javax.swing.JMenu();
                menuAddProperties = new javax.swing.JMenuItem();
                menuAddFileSystem = new javax.swing.JMenuItem();
                menuAddRemote = new javax.swing.JMenuItem();
                menuAddLink = new javax.swing.JMenuItem();
                mainPanel = new javax.swing.JPanel();
                jScrollPane1 = new javax.swing.JScrollPane();
                branchTree = new JTree(treeroot);
                treePanel = new javax.swing.JPanel();
                jLabel1 = new javax.swing.JLabel();
                branchDescription = new javax.swing.JLabel();
                commandPanel = new javax.swing.JPanel();
                buttonPanel = new javax.swing.JPanel();
                rootButton = new javax.swing.JButton();
                jPopupMenu1.setFont(new java.awt.Font("Dialog", 0, 12));
                jPopupMenu1.setBorder(new javax.swing.border.EtchedBorder());
                jPopupMenu1.setLabel("MyPopup");
                jPopupMenu1.setInvoker(null);
                menuItemAdd.setFont(new java.awt.Font("Dialog", 0, 12));
                menuItemAdd.setMnemonic('A');
                menuItemAdd.setText("Add");
                menuItemAdd.setToolTipText("Adds an object to the node");
                menuItemAdd.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemAddActionPerformed(evt);
                        }
                });
                jPopupMenu1.add(menuItemAdd);

                menuItemDel.setFont(new java.awt.Font("Dialog", 0, 12));
                menuItemDel.setMnemonic('D');
                menuItemDel.setText("Delete");
                menuItemDel.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemDelActionPerformed(evt);
                        }
                });
                jPopupMenu1.add(menuItemDel);
                menuItemModify.setMnemonic('M');
                menuItemModify.setFont(new java.awt.Font("Dialog", 0, 12));
                menuItemModify.setText("Modify");
                menuItemModify.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemModifyActionPerformed(evt);
                        }
                });
                jPopupMenu1.add(menuItemModify);
                menuBranch.setFont(new java.awt.Font("Dialog", 0, 12));
                menuBranch.setMnemonic('B');
                menuBranch.setText("Add Branch");
                menuBranch.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuBranchActionPerformed(evt);
                        }
                });
                jPopupMenu1.add(menuBranch);
                jPopupMenu1.add(jSeparator1);
                menuHandler.setMnemonic('H');
                menuHandler.setText("Add Handler");
                menuHandler.setFont(new java.awt.Font("Dialog", 0, 12));
                menuAddProperties.setFont(new java.awt.Font("Dialog", 0, 12));
                menuAddProperties.setText("Properties File");
                menuAddProperties.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuAddPropertiesActionPerformed(evt);
                        }
                });
                menuHandler.add(menuAddProperties);
                menuAddFileSystem.setFont(new java.awt.Font("Dialog", 0, 12));
                menuAddFileSystem.setText("FileSystem");
                menuAddFileSystem.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuAddFileSystemActionPerformed(evt);
                        }
                });
                menuHandler.add(menuAddFileSystem);
                menuAddLink.setFont(new java.awt.Font("Dialog", 0, 12));
                menuAddLink.setText("Link");
                menuAddLink.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuAddLinkActionPerformed(evt);
                        }
                });
                menuHandler.add(menuAddLink);
                menuAddRemote.setFont(new java.awt.Font("Dialog", 0, 12));
                menuAddRemote.setText("Remote");
                menuAddRemote.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuAddRemoteActionPerformed(evt);
                        }
                });
                menuHandler.add(menuAddRemote);
                JMenuItem menuAddListHandler = new JMenuItem();
                menuAddListHandler.setFont(new java.awt.Font("Dialog", 0, 12));
                menuAddListHandler.setText("List");
                menuAddListHandler.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuAddListHandlerActionPerformed(evt);
                        }
                });
                //menuHandler.add(menuAddListHandler);

                jPopupMenu1.add(menuHandler);
                menuItemExportXML.setText("Export to XML");
                menuItemExportXML.setFont(new java.awt.Font("Dialog", 0, 12));
                menuItemExportXML.setToolTipText("Export the branch to XML");
                menuItemExportXML.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                menuItemExportXMLActionPerformed(evt);
                        }
                });
                jPopupMenu1.add(menuItemExportXML);
                setLayout(new java.awt.BorderLayout());
                mainPanel.setLayout(new java.awt.GridBagLayout());
                mainPanel.setMinimumSize(new java.awt.Dimension(201, 402));
                jScrollPane1.setPreferredSize(new java.awt.Dimension(150, 363));
                // branchTree.setBackground(new java.awt.Color(204, 204, 255));
                branchTree.setCellRenderer(new ThorTreeRenderer());
                branchTree.setRowHeight(20);
                setAntiAliasing(branchTree);
                branchTree.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mousePressed(java.awt.event.MouseEvent evt) {
                                jTree1MousePressed(evt);
                        }
                });
                branchTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                        public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                                jTree1ValueChanged(evt);
                        }
                });
                jScrollPane1.setViewportView(branchTree);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.ipadx = 200;
                // gridBagConstraints.ipady = 200;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
                mainPanel.add(jScrollPane1, gridBagConstraints);
                treePanel.setLayout(new java.awt.GridBagLayout());
                jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
                jLabel1.setForeground(java.awt.Color.black);
                jLabel1.setText("Description:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.ipadx = 12;
                gridBagConstraints.ipady = 12;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 0);
                treePanel.add(jLabel1, gridBagConstraints);
                branchDescription.setFont(new java.awt.Font("Dialog", 0, 12));
                branchDescription.setForeground(java.awt.Color.black);
                branchDescription.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 12);
                gridBagConstraints.weightx = 0.7;
                gridBagConstraints.weighty = 1.0;
                treePanel.add(branchDescription, gridBagConstraints);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                mainPanel.add(treePanel, gridBagConstraints);
                commandPanel.setLayout(new java.awt.GridBagLayout());
                commandPanel.setBorder(new javax.swing.border.EtchedBorder());
                buttonPanel.setLayout(new java.awt.GridBagLayout());
                rootButton.setMnemonic('R');
                rootButton.setText("Reload Root");
                rootButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                rootButtonActionPerformed(evt);
                        }
                });
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.insets = new java.awt.Insets(6, 5, 12, 0);
                buttonPanel.add(rootButton, gridBagConstraints);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.ipadx = 60;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
                gridBagConstraints.weightx = 1.0;
                commandPanel.add(buttonPanel, gridBagConstraints);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                mainPanel.add(commandPanel, gridBagConstraints);
                // gridBagConstraints = new java.awt.GridBagConstraints();
                // gridBagConstraints.gridx = 1;
                // gridBagConstraints.gridy = 0;
                // gridBagConstraints.gridheight=2;
                // gridBagConstraints.ipadx = 60;
                // gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
                // gridBagConstraints.weightx = 1.0;
                // gridBagConstraints.weighty = 1.0;
                // JPanel iconPanel = new JPanel();
                // iconPanel.setBackground(Color.WHITE);
                // iconPanel.add(new JLabel("I'm in the icon Panel!"));
                // mainPanel.add(iconPanel,gridBagConstraints);
                GradientLabel jl = new GradientLabel(" Thor UI");
                setAntiAliasing(jl);
                jl.setIcon(new ImageIcon(getClass().getResource("images/folder.gif")));
                jl.setHorizontalTextPosition(SwingConstants.LEFT);
                jl.setIconTextGap(48);
                jl.setFont(new java.awt.Font("Dialog", Font.BOLD, 18));
                jl.setBackground(Color.WHITE);
                jl.setBackgroundTo(new Color(211, 211, 255));
                add(jl, BorderLayout.NORTH);
                add(mainPanel, java.awt.BorderLayout.CENTER);
        }// GEN-END:initComponents

        /**
         * @param evt
         */
        protected void menuAddListHandlerActionPerformed(ActionEvent evt) {
                // TODO Auto-generated method stub

                try {
                        String name = javax.swing.JOptionPane.showInputDialog("Enter name of branch to add");
                        String desc = javax.swing.JOptionPane.showInputDialog("Enter Description of branch");

                        Branch handle;
                        boolean isroot = false;
                        if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                handle = root;
                                isroot = true;
                        } else {
                                String pointadd = getThorPath(branchTree.getSelectionPath());
                                handle = root.getBranch(pointadd);
                        }
                        if (handle != null) {
                                System.out.println("Trying to add");
                                handle.addBranch(name, AttributeConstants.LIST, null);
                        }
                        if (!isroot) {
                                branchTree.collapsePath(branchTree.getSelectionPath());
                                forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                branchTree.expandPath(branchTree.getSelectionPath());
                        } else
                                loadFromRoot();
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        /**
         * 
         */
        private void setAntiAliasing(JComponent comp) {
                Graphics2D g2 = (Graphics2D) comp.getGraphics();
                if (g2 != null)
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        private void rootButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
                // Add your handling code here:
                loadFromRoot();
        }// GEN-LAST:event_jButton1ActionPerformed

        private void jTree1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTree1MousePressed
                // Add your handling code here:
                if (SwingUtilities.isRightMouseButton(evt)) {
                        Point point = new Point();
                        // System.out.println("Pos: " +
                        // jScrollPane1.getViewport().getViewPosition().x + " |
                        // " +
                        // jScrollPane1.getViewport().getViewPosition().y);
                        // SwingUtilities.convertPointToScreen(point,
                        // (Component)
                        // jTree1.getSelectionPath().getLastPathComponent());
                        jPopupMenu1.show(this, evt.getX(), evt.getY() - jScrollPane1.getViewport().getViewPosition().y + 50);
                }
        }// GEN-LAST:event_jTree1MousePressed

        private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {// GEN-FIRST:event_jTree1ValueChanged
                // Add your handling code here:
                if (branchTree.getSelectionPath() != null) {
                        if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                return;
                        }
                        TreePath path = evt.getPath();
                        String separatedPath = getThorPath(path);
                        branchTree.collapsePath(evt.getPath());
                        forceReload(path, separatedPath);
                        branchTree.expandPath(evt.getPath());
                }
        }// GEN-LAST:event_jTree1ValueChanged

        // End of variables declaration//GEN-END:variables
        private void loadFromRoot() {
                try {
                        session = thorReg.getSession();
                        root = session.getRoot();
                        java.util.Vector data = (java.util.Vector) root.getDataBlock();
                        treemodel = new DefaultTreeModel(new DefaultMutableTreeNode("Root"));
                        // treemodel.reload();
                        for (int i = 0; i < data.size(); i++) {
                                NamedItem item = (NamedItem) data.get(i);
                                DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
                                treemodel.insertNodeInto(node, (MutableTreeNode) treemodel.getRoot(), i);
                        }
                        branchTree.setModel(treemodel);
                } catch (Exception ex) {
                        System.out.println("err: " + ex.getMessage());
                        ex.printStackTrace();
                }
        }

        private void menuAddFileSystemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuAddFileSystemActionPerformed
                // Add your handling code here:
                if (branchTree.getSelectionPath() != null) {
                        String name = javax.swing.JOptionPane.showInputDialog("Enter name of branch to add");
                        String dir = javax.swing.JOptionPane.showInputDialog("Enter name of directory to add");
                        HashMap attr = new HashMap();
                        attr.put("FILEPATH", dir);
                        try {
                                Branch handle;
                                boolean isroot = false;
                                if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                        handle = root;
                                        isroot = true;
                                } else {
                                        String pointadd = getThorPath(branchTree.getSelectionPath());
                                        handle = root.getBranch(pointadd);
                                }
                                if (handle != null) {
                                        // System.out.println("Trying to add");
                                        handle.addBranch(name, this.FILE, attr);
                                } else {
                                        // System.out.println("This is not a
                                        // branch");
                                }
                                if (!isroot) {
                                        branchTree.collapsePath(branchTree.getSelectionPath());
                                        forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                        branchTree.expandPath(branchTree.getSelectionPath());
                                } else
                                        loadFromRoot();
                        } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                        }
                }
        }// GEN-LAST:event_menuAddFileSystemActionPerformed

        private void menuAddLinkActionPerformed(java.awt.event.ActionEvent evt) {
                // Add your handling code here:
                if (branchTree.getSelectionPath() != null) {
                        String name = javax.swing.JOptionPane.showInputDialog(this, "Enter name of branch to add");
                        String lpath = javax.swing.JOptionPane.showInputDialog(this, "Enter name of branch to link to");
                        HashMap attr = new HashMap();
                        attr.put("LINKPATH", lpath);
                        try {
                                Branch handle;
                                boolean isroot = false;
                                if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                        handle = root;
                                        isroot = true;
                                } else {
                                        String pointadd = getThorPath(branchTree.getSelectionPath());
                                        handle = root.getBranch(pointadd);
                                }
                                if (handle != null) {
                                        // System.out.println("Trying to add");
                                        handle.addBranch(name, this.LINK, attr);
                                } else {
                                        // System.out.println("This is not a
                                        // branch");
                                }
                                if (!isroot) {
                                        branchTree.collapsePath(branchTree.getSelectionPath());
                                        forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                        branchTree.expandPath(branchTree.getSelectionPath());
                                } else
                                        loadFromRoot();
                        } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                        }
                }
        }

        private void menuAddPropertiesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuAddPropertiesActionPerformed
                // Add your handling code here:
                if (branchTree.getSelectionPath() != null) {
                        String name = javax.swing.JOptionPane.showInputDialog("Enter name of branch to add");
                        PropertiesAddDialog dlg = new PropertiesAddDialog(null, true);
                        dlg.setVisible(true);
                        java.util.Properties props = dlg.getProperties();
                        HashMap attr = new HashMap();
                        attr.put("PROPS", props);
                        try {
                                Branch handle;
                                boolean isroot = false;
                                if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                        // JOptionPane.showMessageDialog(this,
                                        // "Hit root");
                                        handle = root;
                                        isroot = true;
                                } else {
                                        // String pointadd =
                                        // getThorPath(jTree1.getSelectionPath());
                                        // handle = root.getBranch(pointadd);
                                        handle = root.getBranch(getThorPath(branchTree.getSelectionPath()));
                                }
                                if (handle != null) {
                                        handle.addBranch(name, AttributeConstants.PROPERTY, attr);
                                }
                                if (!isroot) {
                                        branchTree.collapsePath(branchTree.getSelectionPath());
                                        forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                        branchTree.expandPath(branchTree.getSelectionPath());
                                } else
                                        loadFromRoot();
                        } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                        }
                }
        }// GEN-LAST:event_menuAddPropertiesActionPerformed

        private void menuAddRemoteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuAddRemoteActionPerformed
                // Add your handling code here:
                if (branchTree.getSelectionPath() != null) {
                        String name = javax.swing.JOptionPane.showInputDialog(this, "Enter name of branch to add");
                        // String dir =
                        // javax.swing.JOptionPane.showInputDialog("Enter name
                        // of directory to add");
                        RemoteTreeDialog rtreedlg = new RemoteTreeDialog(null, true);
                        rtreedlg.setVisible(true);

                        if (rtreedlg.isDataValid()) {
                                HashMap attr = new HashMap();
                                attr.put("GROUP", rtreedlg.getGroup());
                                attr.put("SERVICE_NAME", rtreedlg.getName());
                                attr.put("INITIAL_BRANCH", rtreedlg.getInitialBranch());
                                try {
                                        Branch handle;
                                        boolean isroot = false;
                                        if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                                // JOptionPane.showMessageDialog(this,
                                                // "Hit root");
                                                handle = root;
                                                isroot = true;
                                        } else {
                                                String pointadd = getThorPath(branchTree.getSelectionPath());
                                                handle = root.getBranch(pointadd);
                                        }
                                        if (handle != null) {
                                                // System.out.println("Trying to
                                                // add");
                                                handle.addBranch(name, this.REMOTE, attr);
                                        } else {
                                                // System.out.println("This is
                                                // not a branch");
                                        }
                                        if (!isroot) {
                                                branchTree.collapsePath(branchTree.getSelectionPath());
                                                forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                                branchTree.expandPath(branchTree.getSelectionPath());
                                        } else
                                                loadFromRoot();
                                } catch (Exception ex) {
                                        System.out.println("Err: " + ex.getMessage());
                                        ex.printStackTrace();
                                }
                        }
                }
        }// GEN-LAST:event_menuAddRemoteActionPerformed

        private void menuBranchActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuBranchActionPerformed
                // Add your handling code here:
                if (branchTree.getSelectionPath() != null) {
                        try {
                                boolean isroot = false;
                                Branch handle;
                                if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                        JOptionPane.showMessageDialog(this, "Hit root");
                                        handle = root;
                                        isroot = true;
                                } else {
                                        String pointadd = getThorPath(branchTree.getSelectionPath());
                                        handle = root.getBranch(pointadd);
                                }
                                String name = javax.swing.JOptionPane.showInputDialog("Enter name of branch to add");
                                String desc = javax.swing.JOptionPane.showInputDialog("Enter Description of branch");
                                if (handle != null) {
                                        // System.out.println("This IS a
                                        // branch!");
                                        handle.addBranch(name);
                                        handle.getBranch(name).setDescription(desc);
                                } else {
                                        // System.out.println("This is not a
                                        // branch!");
                                }
                                if (!isroot) {
                                        branchTree.collapsePath(branchTree.getSelectionPath());
                                        forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                        branchTree.expandPath(branchTree.getSelectionPath());
                                } else
                                        loadFromRoot();
                        } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                        }
                }
        }// GEN-LAST:event_menuBranchActionPerformed

        private void menuItemExportXMLActionPerformed(java.awt.event.ActionEvent evt) {
                if (branchTree.getSelectionPath() != null) {
                        try {
                                boolean isroot = false;
                                Branch handle;
                                if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                        JOptionPane.showMessageDialog(this, "Hit root");
                                        handle = root;
                                        isroot = true;
                                } else {
                                        String pointadd = getThorPath(branchTree.getSelectionPath());
                                        handle = root.getBranch(pointadd);
                                }
                                String exportedxml = handle.exportXML();
                                JFileChooser chooser = new JFileChooser();
                                chooser.setFileFilter(new FileFilter() {
                                        public boolean accept(File f) {
                                                // TODO Complete
                                                // method stub
                                                // for accept
                                                boolean accept = false;

                                                if (f.getAbsolutePath().endsWith("xml"))
                                                        accept = true;
                                                if (f.isDirectory())
                                                        accept = true;
                                                return accept;
                                        }

                                        public String getDescription() {
                                                // TODO Complete
                                                // method stub
                                                // for
                                                // getDescription
                                                return "XML Files";
                                        }
                                });
                                chooser.showSaveDialog(this);
                                if (chooser.getSelectedFile() != null) {
                                        FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                                        fos.write(exportedxml.getBytes());
                                        fos.flush();
                                        fos.close();
                                }
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }

                }
        }

        private void menuItemAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuItemAddActionPerformed
                // Add your handling code here:
                // String name = javax.swing.JOptionPane.showInputDialog("Enter
                // name of
                // data to add");
                // String data = javax.swing.JOptionPane.showInputDialog("Enter
                // data");
                boolean isroot = false;
                if (branchTree.getSelectionPath() != null) {
                        NewDataItemDialog ndidlg = new NewDataItemDialog(null, true);
                        ndidlg.setLocationRelativeTo(this);
                        ndidlg.setVisible(true);
                        boolean doAddXML = false;
                        if (ndidlg.isValid()) {
                                String name = ndidlg.getObjectName();
                                Object obj = ndidlg.getObject();
                                doAddXML = ndidlg.isXML();
                                boolean doAddConfiguration = ndidlg.isConfiguration();
                                Branch handle;
                                try {
                                        if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                                handle = root;
                                                isroot = true;
                                        } else {
                                                String pointadd = getThorPath(branchTree.getSelectionPath());
                                                // System.out.println("Thorpath:
                                                // " + pointadd);
                                                handle = root.getBranch(pointadd);
                                        }
                                        if (handle != null) {
                                                if (doAddXML) {
                                                        System.out.println("Adding XML!");
                                                        File xmlFile = new File((String) obj);
                                                        FileInputStream fis = new FileInputStream(xmlFile);
                                                        byte[] myfilecontents = new byte[(int) xmlFile.length()];
                                                        fis.read(myfilecontents);
                                                        fis.close();
                                                        handle.addXML(new String(myfilecontents));
                                                } else {
                                                        if (doAddConfiguration)
                                                                addConfiguration(handle, name, obj);
                                                        else
                                                                handle.add(name, obj);
                                                }
                                        } else {
                                                // System.out.println("Not a
                                                // branch");
                                        }
                                        handle = null;
                                        if (!isroot) {
                                                branchTree.collapsePath(branchTree.getSelectionPath());
                                                forceReload(branchTree.getSelectionPath(), getThorPath(branchTree.getSelectionPath()));
                                                branchTree.expandPath(branchTree.getSelectionPath());
                                        } else
                                                loadFromRoot();
                                        // treemodel.
                                } catch (Exception ex) {
                                        // System.out.println("Err: " +
                                        // ex.getMessage());
                                        ex.printStackTrace();
                                }
                        }
                        // Add your handling code here:
                }
        }// GEN-LAST:event_menuItemAddActionPerformed

        /**
         * @param handle
         * @param name
         * @param obj
         */
        private void addConfiguration(Branch handle, String name, Object obj) {
                // TODO Auto-generated method stub
                try {
                        File configFile = new File((String) obj);
                        FileInputStream fis = new FileInputStream(configFile);
                        byte[] myfilecontents = new byte[(int) configFile.length()];
                        fis.read(myfilecontents);
                        ConfigurationFileWrapper wrapper = new ConfigurationFileWrapper(myfilecontents);
                        handle.add(name, wrapper);
                } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        private void menuItemDelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuItemDelActionPerformed
                // Add your handling code here:
                boolean isroot = false;
                if (branchTree.getSelectionPath() != null) {
                        Branch handle;
                        if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                JOptionPane.showMessageDialog(this, "You cannot delete the root node");
                                return;
                        }
                        String name;

                        try {
                                if (branchTree.getSelectionPath().getParentPath().getLastPathComponent().toString().equals("Root")) {
                                        handle = root;
                                        name = branchTree.getSelectionPath().getLastPathComponent().toString();
                                        isroot = true;
                                } else {
                                        String pointadd = getThorPath(branchTree.getSelectionPath());
                                        String thorpath = getThorPath(branchTree.getSelectionPath());
                                        String prevpath = thorpath.substring(0, thorpath.lastIndexOf('/'));
                                        name = thorpath.substring(thorpath.lastIndexOf('/') + 1);
                                        handle = root.getBranch(prevpath);
                                }
                                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the " + name + " node?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                        if (handle != null) {
                                                handle.removeChild(name);
                                        }
                                        if (!isroot) {
                                                branchTree.collapsePath(branchTree.getSelectionPath());
                                                forceReload(branchTree.getSelectionPath().getParentPath(), getThorPath(branchTree.getSelectionPath().getParentPath()));
                                                branchTree.expandPath(branchTree.getSelectionPath());
                                        } else
                                                loadFromRoot();
                                }
                        } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                        }
                }
        }// GEN-LAST:event_menuItemDelActionPerformed

        private void menuItemModifyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuItemModifyActionPerformed
                // Add your handling code here:
                boolean isroot = false;
                if (branchTree.getSelectionPath() != null) {
                        if (branchTree.getSelectionPath().getLastPathComponent().toString().equals("Root")) {
                                JOptionPane.showMessageDialog(this, "You cannot modify the root node");
                                return;
                        }
                        String path = getThorPath(branchTree.getSelectionPath());
                        try {
                                if (root.isBranch(path)) {
                                        JOptionPane.showMessageDialog(this, "A Branch cannot be modified - only the data", "Thor Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                } else {
                                        Object x = root.locate(path);
                                        String name = path.substring(path.lastIndexOf("/") + 1);
                                        // System.out.println("Last Part: " +
                                        // name);
                                        NewDataItemDialog ndidlg = new NewDataItemDialog(null, name, x, true);
                                        ndidlg.setLocationRelativeTo(this);
                                        ndidlg.setVisible(true);
                                        if (ndidlg.isValid()) {
                                                String newname = ndidlg.getObjectName();
                                                Object newobj = ndidlg.getObject();
                                                // If item is directly under
                                                // root handle this
                                                // differently
                                                Branch parBranch;
                                                if (branchTree.getSelectionPath().getParentPath().getLastPathComponent().toString().equals("Root")) {
                                                        parBranch = root;
                                                        isroot = true;
                                                } else {
                                                        parBranch = root.getBranch(getThorPath(branchTree.getSelectionPath().getParentPath()));
                                                }
                                                if (parBranch != null) {
                                                        parBranch.overwrite(newname, newobj);
                                                        if (!isroot) {
                                                                branchTree.collapsePath(branchTree.getSelectionPath().getParentPath());
                                                                String MyPath = getThorPath(branchTree.getSelectionPath());
                                                                // System.out.println("MYPATH
                                                                // is: " +
                                                                // MyPath);
                                                                forceReload(branchTree.getSelectionPath(), MyPath);
                                                                branchTree.expandPath(branchTree.getSelectionPath());
                                                        } else
                                                                loadFromRoot();
                                                }
                                        }
                                }
                        } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                        }
                }
        }// GEN-LAST:event_menuItemModifyActionPerformed
}
