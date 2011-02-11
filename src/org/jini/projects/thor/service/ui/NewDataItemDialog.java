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
 * NewDataItemDialog.java
 *
 * Created on April 22, 2002, 3:47 PM
 */

package org.jini.projects.thor.service.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

/**
 * 
 * @author calum
 */
public class NewDataItemDialog extends javax.swing.JDialog {
    boolean doMultiLine = false;

    /** Creates new form NewDataItemDialog */
    public NewDataItemDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        label = new GradientLabel(" Add an Item");
        label.setBackgroundTo(new Color(211, 211, 255));
        initComponents();

    }

    private java.lang.Process editProc;

    private String tFileName;

    private boolean addAsXML = false;

    public NewDataItemDialog(java.awt.Frame parent, String existingName, Object existingValue, boolean modal) {
        super(parent, modal);
        label = new GradientLabel(" Add an Item");

        label.setBackgroundTo(new Color(211, 211, 255));

        if (existingValue.toString().indexOf("\n") != -1) {
            System.out.println("Setting multiline");
            doMultiLine = true;
        } else
            doMultiLine = false;

        initComponents();

        setTitle("Modify Dialog");
        itemName.setText(existingName);
        itemName.setEnabled(false);
        setExistingValueType(existingValue);
        if (existingValue != null) {
            label.setText(" Modify Existing Value");
            if (existingValue instanceof byte[]) {
                int conf = javax.swing.JOptionPane.showConfirmDialog(this, "Cannot show the current value due to it's datatype\nSave to file?", "Message", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    try {
                        File file = File.createTempFile("thor", "bfile");
                        tFileName = file.getAbsolutePath();

                        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
                        fos.write((byte[]) existingValue);
                        fos.close();
                        int open = javax.swing.JOptionPane.showConfirmDialog(this, "File Saved - would you like to open it?", "Message", JOptionPane.YES_NO_OPTION);
                        if (open == JOptionPane.YES_OPTION) {
                            String cmd = javax.swing.JOptionPane.showInputDialog(this, "Please enter command to execute the data with\nThe  filename will be appended.");
                            if (cmd != null) {
                                editProc = Runtime.getRuntime().exec(cmd + " " + tFileName);
                                editProc.waitFor();
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Err: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            } else
                editingComponent.setText(existingValue.toString());
        }

    }

    private void setExistingValueType(Object existingValue) {
        if (existingValue instanceof String)
            stringRadio.setSelected(true);
        if (existingValue instanceof Integer)
            integerRadio.setSelected(true);
        if (existingValue instanceof Double)
            doubleRadio.setSelected(true);
        if (existingValue instanceof Boolean)
            booleanRadio.setSelected(true);
        if (existingValue instanceof Long)
            longRadio.setSelected(true);
        if (existingValue instanceof byte[])
            byteRadio.setSelected(true);
    }

    private static final int FILE = 1;

    private static final int DIRECT = 2;

    private Object returnObject;

    private String returnName;

    private int bytesource = 0;

    private boolean validclose = false;

    private boolean addAsConfiguration;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() { // GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        itemValue = new javax.swing.JTextField();
        itemValueMultiLine = new JTextPane();
        jPanel2 = new javax.swing.JPanel();
        stringRadio = new javax.swing.JRadioButton();
        doubleRadio = new javax.swing.JRadioButton();
        byteRadio = new javax.swing.JRadioButton();
        configurationRadio = new javax.swing.JRadioButton();
        integerRadio = new javax.swing.JRadioButton();
        booleanRadio = new javax.swing.JRadioButton();
        longRadio = new javax.swing.JRadioButton();
        XMLRadio = new javax.swing.JRadioButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        itemName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        getContentPane().setLayout(new GridBagLayout());

        setTitle("Add Dialog");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        JPanel titlePanel = new JPanel();
        label.setFont(new java.awt.Font("Dialog", Font.BOLD, 18));
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        getContentPane().add(label, gridBagConstraints);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText("Initial Value");
        jPanel1.add(jLabel2, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        if (doMultiLine){
            editingComponent = itemValueMultiLine;
            gridBagConstraints.ipady = 60;
            jPanel1.add(new JScrollPane(editingComponent), gridBagConstraints);  
        }else{
            editingComponent = itemValue;
            jPanel1.add(editingComponent, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridLayout(2, 2));

        jPanel2.setBorder(new javax.swing.border.EtchedBorder());
        stringRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        stringRadio.setMnemonic('S');
        stringRadio.setSelected(true);
        stringRadio.setText("String");
        buttonGroup1.add(stringRadio);
        jPanel2.add(stringRadio);

        doubleRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        doubleRadio.setMnemonic('D');
        doubleRadio.setText("Double");
        buttonGroup1.add(doubleRadio);
        jPanel2.add(doubleRadio);

        byteRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        byteRadio.setMnemonic('t');
        byteRadio.setText("Byte[]");
        buttonGroup1.add(byteRadio);
        byteRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jPanel2.add(byteRadio);

        configurationRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        configurationRadio.setMnemonic('c');
        configurationRadio.setText("Configuration");
        buttonGroup1.add(configurationRadio);
        configurationRadio.setEnabled(true);
        jPanel2.add(configurationRadio);

        integerRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        integerRadio.setMnemonic('I');
        integerRadio.setText("Integer");
        buttonGroup1.add(integerRadio);
        jPanel2.add(integerRadio);

        booleanRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        booleanRadio.setMnemonic('B');
        booleanRadio.setText("Boolean");
        buttonGroup1.add(booleanRadio);
        jPanel2.add(booleanRadio);

        longRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        longRadio.setMnemonic('L');
        longRadio.setText("Long");
        buttonGroup1.add(longRadio);
        jPanel2.add(longRadio);

        XMLRadio.setFont(new java.awt.Font("Dialog", 0, 12));
        XMLRadio.setMnemonic('X');
        XMLRadio.setText("XML");
        buttonGroup1.add(XMLRadio);
        XMLRadio.setEnabled(true);
        jPanel2.add(XMLRadio);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 80;
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBorder(new javax.swing.border.EtchedBorder());
        jLabel21.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel21.setText("Item Name");
        jPanel11.add(jLabel21, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel11.add(itemName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel11, gridBagConstraints);

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton1.setMnemonic('O');
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.add(jButton1);

        jButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton2.setMnemonic('C');
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        XMLRadio.addActionListener(new ActionListener() {/*
                                                                                             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                                                                                             */
            public void actionPerformed(ActionEvent e) {
                // TODO Complete method stub for actionPerformed
                XMLRadioAction();
            }
        });

        configurationRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configurationRadioAction();
            }
        });
        jPanel3.add(jButton2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel3, gridBagConstraints);
        stringRadio.setSelected(true);
        pack();
        this.setFont(new java.awt.Font("Dialog", 0, 12));
    } // GEN-END:initComponents

    private void XMLRadioAction() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                // TODO Complete method stub for accept
                boolean accept = false;

                if (f.getAbsolutePath().endsWith("xml"))
                    accept = true;
                if (f.isDirectory())
                    accept = true;
                return accept;
            }

            public String getDescription() {
                // TODO Complete method stub for getDescription
                return "XML Files";
            }
        });
        chooser.showOpenDialog(this);
        if (chooser.getSelectedFile() != null)
            editingComponent.setText(chooser.getSelectedFile().getAbsolutePath());
    }

    private void configurationRadioAction() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                // TODO Complete method stub for accept
                boolean accept = false;

                if (f.getAbsolutePath().endsWith("config"))
                    accept = true;
                if (f.isDirectory())
                    accept = true;
                return accept;
            }

            public String getDescription() {
                // TODO Complete method stub for getDescription
                return "Configuration files";
            }
        });
        chooser.showOpenDialog(this);
        if (chooser.getSelectedFile() != null)
            editingComponent.setText(chooser.getSelectedFile().getAbsolutePath());
    }

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jRadioButton3ActionPerformed
        // Add your handling code here:
        Object[] possibleValues = { "File", "Direct" };
        Object selectedValue = JOptionPane.showInputDialog(this, "Where is the source for the array?", "Choose Source", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
        if (selectedValue != null) {
            if (selectedValue.equals("File")) {
                JFileChooser fdlg = new JFileChooser(System.getProperty("user.dir"));
                fdlg.showOpenDialog(this);
                if (fdlg.getSelectedFile() != null)
                    editingComponent.setText(fdlg.getSelectedFile().getAbsolutePath());
                bytesource = FILE;
            }
            if (selectedValue.equals("Direct")) {
                bytesource = DIRECT;
            }
        }
    } // GEN-LAST:event_jRadioButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jButton2ActionPerformed
        // Add your handling code here:
        closeDialog(null);

    } // GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jButton1ActionPerformed
        // Add your handling code here:

        radioEnum = buttonGroup1.getElements();
        returnName = itemName.getText();
        while (radioEnum.hasMoreElements()) {
            JRadioButton rad = (JRadioButton) radioEnum.nextElement();
            String name = rad.getText();
            if (rad.isSelected()) {
                addAsXML = false;
                System.out.println("Itemtype: " + name);
                try {
                    if (name.equals("String"))
                        returnObject = new String(editingComponent.getText());
                    if (name.equals("Integer"))
                        returnObject = Integer.valueOf(editingComponent.getText());
                    if (name.equals("Double"))
                        returnObject = Double.valueOf(editingComponent.getText());
                    if (name.equals("Boolean")) {
                        if (!(editingComponent.getText().toLowerCase().equals("true") || editingComponent.getText().toLowerCase().equals("false")))
                            throw new NumberFormatException("Booleans may only be true or false");
                        returnObject = Boolean.valueOf(editingComponent.getText());
                        System.out.println(returnObject);
                    }
                    if (name.equals("Byte[]")) {
                        boolean useTempFile = false;
                        if (tFileName != null) {
                            int conf = javax.swing.JOptionPane.showConfirmDialog(this, "Use the downloaded file?", "Message", JOptionPane.YES_NO_OPTION);
                            if (conf == JOptionPane.YES_OPTION)
                                useTempFile = true;
                        }
                        if (bytesource == 0) {
                            JOptionPane.showMessageDialog(this, "Please choose a source for the byte array first");
                            return;
                        }
                        if (bytesource == DIRECT) {
                            returnObject = editingComponent.getText().getBytes();
                        } else if (bytesource == FILE) {
                            try {
                                File retval;
                                if (useTempFile)
                                    retval = new File(tFileName);
                                else
                                    retval = new File(editingComponent.getText());

                                FileInputStream fis = new FileInputStream(retval);
                                byte[] array = new byte[(int) retval.length()];
                                fis.read(array);
                                returnObject = array;
                            } catch (Exception ex) {
                                System.out.println("Err: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (name.equals("Configuration")) {
                        returnObject = new String(editingComponent.getText());
                    }

                    if (name.equals("Long"))
                        returnObject = Long.valueOf(editingComponent.getText());
                    // if (name.equals("Boole"))
                    // returnObject=Boolean.valueOf(editingComponent.getText());
                } catch (NumberFormatException numfex) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Number format is not acceptable.\n" + numfex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (name.equals("XML")) {
                    returnObject = new String(editingComponent.getText());
                }
                StringBuffer message = new StringBuffer(64);
                message.append("Are you sure you\n want to add this item?\n");
                message.append("Name: ");
                message.append(returnName);
                message.append("\n");
                if (name.equals("Configuration")) {
                    addAsConfiguration = true;
                    message.append("Sourced from: " + returnObject);
                } else if (name.equals("XML")) {
                    addAsXML = true;
                    message.append("Sourced from: " + returnObject);
                } else {
                    message.append("Class: ");
                    message.append(bytesource == 0 ? returnObject.getClass().getName() : "Byte Array");
                    message.append("\n");
                    message.append("Value: ");
                    message.append(bytesource == 0 ? returnObject.toString() : "Byte Array");
                }
                int dlgret = javax.swing.JOptionPane.showConfirmDialog(this, message.toString(), "Confirm", JOptionPane.INFORMATION_MESSAGE);
                if (dlgret == javax.swing.JOptionPane.OK_OPTION) {
                    System.out.println("Will add now");
                }
            }

        }
        validclose = true;
        closeDialog(null);
    } // GEN-LAST:event_jButton1ActionPerformed

    public boolean isValid() {
        return validclose;
    }

    public Object getObject() {
        return returnObject;
    }

    public String getObjectName() {
        return returnName;
    }

    public boolean isXML() {
        return addAsXML;
    }

    public boolean isConfiguration() {
        return addAsConfiguration;
    }

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) { // GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    } // GEN-LAST:event_closeDialog

    /**
     * @param args
     *                   the command line arguments
     */
    public static void main(String args[]) {
        String x = "Test";
        NewDataItemDialog ndidlg = new NewDataItemDialog(new javax.swing.JFrame(), "Name", x, true);
        ndidlg.setVisible(true);
        if (ndidlg.isValid())
            System.out.println("New Item to be added: " + ndidlg.getObjectName() + ":" + ndidlg.getObject().getClass().getName());
        System.exit(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField itemName;

    private javax.swing.JPanel jPanel3;

    private javax.swing.JPanel jPanel2;

    private javax.swing.JPanel jPanel1;

    private javax.swing.JButton jButton2;

    private javax.swing.JButton jButton1;

    private javax.swing.JLabel jLabel21;

    private javax.swing.ButtonGroup buttonGroup1;

    private javax.swing.JTextField itemValue;

    private JTextPane itemValueMultiLine;

    private javax.swing.JPanel jPanel11;

    private javax.swing.JRadioButton XMLRadio;

    private javax.swing.JRadioButton longRadio;

    private javax.swing.JRadioButton booleanRadio;

    private javax.swing.JRadioButton integerRadio;

    private javax.swing.JRadioButton configurationRadio;

    private javax.swing.JRadioButton byteRadio;

    private javax.swing.JRadioButton doubleRadio;

    private javax.swing.JRadioButton stringRadio;

    private javax.swing.JLabel jLabel2;

    // End of variables declaration//GEN-END:variables
    private GradientLabel label;

    private java.util.Enumeration radioEnum;

    private JTextComponent editingComponent = null;

}
