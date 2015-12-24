/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor.istree;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import java.awt.Insets;
import java.awt.Dimension;

/**
 * 
 * @author Mark ter Maat
 *
 */

public class AddValueDialog extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField nameField = null;
    private JTextField valueField = null;
    private JLabel nameLabel = null;
    private JLabel valueLabel = null;
    private JPanel jPanel = null;
    private JPanel buttonPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    
    private ISTreePanel treePanel;
    private TreePath path;
    private JFrame parentFrame;
    private JLabel typeLabel = null;
    private JComboBox typeComboBox = null;
    
    /**
     * This is the default constructor
     */
    public AddValueDialog( JFrame parentFrame, ISTreePanel treePanel, TreePath path ) {
        super();
        setPreferredSize(new Dimension(250,100));
        this.treePanel = treePanel;
        this.path = path;
        this.parentFrame = parentFrame; 
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new Insets(5, 10, 5, 10);
        gridBagConstraints4.gridheight = 1;
        gridBagConstraints4.gridwidth = 1;
        gridBagConstraints4.gridx = 2;
        GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
        gridBagConstraints31.gridx = 0;
        gridBagConstraints31.gridy = 0;
        typeLabel = new JLabel();
        typeLabel.setText("Type");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.ipadx = 200;
        gridBagConstraints1.ipady = 20;
        gridBagConstraints1.gridy = 5;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        gridBagConstraints10.gridx = 0;
        gridBagConstraints10.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints10.gridy = 3;
        valueLabel = new JLabel();
        valueLabel.setText("Value");
        GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        gridBagConstraints9.gridx = 0;
        gridBagConstraints9.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints9.gridy = 2;
        nameLabel = new JLabel();
        nameLabel.setText("Name");
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints8.gridy = 3;
        gridBagConstraints8.weightx = 1.0;
        gridBagConstraints8.ipadx = 200;
        gridBagConstraints8.insets = new Insets(2, 3, 2, 3);
        gridBagConstraints8.gridx = 2;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints7.gridy = 2;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.ipadx = 200;
        gridBagConstraints7.insets = new Insets(0, 3, 2, 3);
        gridBagConstraints7.gridx = 2;
        this.setSize(250, 120);
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(250, 120));
        this.add(getNameField(), gridBagConstraints7);
        this.add(getValueField(), gridBagConstraints8);
        this.add(nameLabel, gridBagConstraints9);
        this.add(valueLabel, gridBagConstraints10);
        this.add(getJPanel(), gridBagConstraints);
        this.add(getButtonPanel(), gridBagConstraints1);
        this.add(typeLabel, gridBagConstraints31);
        this.add(getTypeComboBox(), gridBagConstraints4);
    }

    /**
     * This method initializes nameField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNameField() {
        if (nameField == null) {
            nameField = new JTextField();
            nameField.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    treePanel.addISValue(path, typeComboBox.getSelectedItem().toString(), nameField.getText(), valueField.getText());
                    parentFrame.dispose();
                }
            });
        }
        return nameField;
    }

    /**
     * This method initializes valueField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getValueField() {
        if (valueField == null) {
            valueField = new JTextField();
            valueField.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    treePanel.addISValue(path, typeComboBox.getSelectedItem().toString(), nameField.getText(), valueField.getText());
                    parentFrame.dispose();
                }
            });
        }
        return valueField;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
        }
        return jPanel;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.ipadx = 0;
            gridBagConstraints3.ipady = 0;
            gridBagConstraints3.insets = new Insets(0, 10, 0, 1);
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.ipadx = 0;
            gridBagConstraints2.ipady = 0;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 20);
            gridBagConstraints2.gridy = 0;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(getOkButton(), gridBagConstraints2);
            buttonPanel.add(getCancelButton(), gridBagConstraints3);
        }
        return buttonPanel;
    }

    /**
     * This method initializes okButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("Ok");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    treePanel.addISValue(path, typeComboBox.getSelectedItem().toString(), nameField.getText(), valueField.getText());
                    parentFrame.dispose();
                }
            });
        }
        return okButton;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    parentFrame.dispose();
                }
            });
        }
        return cancelButton;
    }

    /**
     * This method initializes typeComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getTypeComboBox() {
        String[] types = { "String", "Integer", "Double", "Record", "List" };
        if (typeComboBox == null) {
            typeComboBox = new JComboBox(types);
            typeComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if( e.getItem().toString().equals("Record") ) {
                        nameField.setEnabled(true);
                        valueField.setEnabled(false);
                    } else if( e.getItem().toString().equals("List") ) {
                        nameField.setEnabled(false);
                        valueField.setEnabled(false);
                    } else {
                        nameField.setEnabled(true);
                        valueField.setEnabled(true);
                    }
                }
            });
        }
        return typeComboBox;
    }

}  //  @jve:decl-index=0:visual-constraint="436,108"
