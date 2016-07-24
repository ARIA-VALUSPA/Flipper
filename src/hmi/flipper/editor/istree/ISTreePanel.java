/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor.istree;

import hmi.flipper.defaultInformationstate.DefaultList;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.editor.Editor;
import hmi.flipper.editor.RunnerPanel;
import hmi.flipper.informationstate.Item;
import hmi.flipper.informationstate.Item.Type;
import hmi.flipper.informationstate.List;
import hmi.flipper.informationstate.Record;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * 
 * @author Mark ter Maat
 */
public class ISTreePanel extends JPanel implements MouseListener, ActionListener
{
    private final static String SAVE = "Save";
    private final static String SAVEAS = "Save As";
    private final static String LOAD = "Load";
    
    private Editor editor;
    private RunnerPanel runnerPanel;
    private Record is;
    private JTree isTree;
    
    private String currISPath = null;
    private File currISFile = null;
    
    public ISTreePanel(Editor editor, RunnerPanel runnerPanel, Record is)
    {
        this.editor = editor;
        this.runnerPanel = runnerPanel;
        this.is = is;
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(Editor.IS_BLOCK_WIDTH,Editor.IS_BLOCK_HEIGHT));
        setLayout(new BorderLayout());
        
        isTree = new JTree(new ISTreeModel(is));
        isTree.setLargeModel(true);
        isTree.setPreferredSize(new Dimension(Editor.IS_BLOCK_WIDTH,Editor.IS_BLOCK_HEIGHT));
        isTree.addMouseListener(this);

        expandAll(isTree);
        add(isTree, BorderLayout.WEST);

        JPanel isButtonPanel = new JPanel();
        isButtonPanel.setLayout(new BoxLayout(isButtonPanel,BoxLayout.X_AXIS));
        isButtonPanel.setBackground(Color.WHITE);
        isButtonPanel.setPreferredSize(new Dimension(Editor.IS_BLOCK_WIDTH,Editor.BUTTON_PANEL_HEIGHT));

        JButton saveButton = new JButton(SAVE);
        saveButton.addActionListener(this);
        isButtonPanel.add(saveButton);
        JButton saveAsButton = new JButton(SAVEAS);
        saveAsButton.addActionListener(this);
        isButtonPanel.add(saveAsButton);
        JButton loadButton = new JButton(LOAD);
        loadButton.addActionListener(this);
        isButtonPanel.add(loadButton);

        add(isButtonPanel, BorderLayout.SOUTH);
    }
    
    public void expandAll(JTree tree) {
        ISTreeModel model = (ISTreeModel)tree.getModel();
        for( int i=0; i<model.getChildCount(model.getRoot()); i++ ) {
            expandNode(tree, model, new TreePath(model.getRoot()), model.getChild(model.getRoot(), i));
        }
    }
    
    public void expandNode(JTree tree, ISTreeModel model, TreePath parent, Object item)
    {
        TreePath path = parent.pathByAddingChild(item);
        if( model.isLeaf(item) ) {
            tree.collapsePath(path);
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
            tree.expandPath(path);
            for( int i=0; i<model.getChildCount(item); i++ ) {
                expandNode(tree, model, parent.pathByAddingChild(item), model.getChild(item, i));
            }
        }
    }
    
    private void changeValueDialog( ISTreeItem item, TreePath path )
    {
        String newValue = JOptionPane.showInputDialog(null, "New Value");
        if( newValue == null ) return;
        item.getItem().set(null, newValue);
        runnerPanel.checkTemplates();
        this.repaint();
    }
    
    public void popupAddPanel( TreePath path )
    {
        JFrame addValueFrame = new JFrame("New Value");
        addValueFrame.add(new AddValueDialog(addValueFrame, this, path));
        
        /* Finishes the frame */
        Point mouseLoc = getMousePosition();
        Point containerLocation = editor.jframe.getLocation();
        Point p = new Point(mouseLoc.x+containerLocation.x, mouseLoc.y+containerLocation.y);
        addValueFrame.setLocation(p);
        addValueFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addValueFrame.pack();
        addValueFrame.setResizable(false);
        addValueFrame.setVisible(true);
    }
    
    public void addISValue( TreePath path, String type, String name, String value )
    {
        ISTreeModel model = (ISTreeModel)isTree.getModel();
        ISTreeItem treeItem = (ISTreeItem)path.getLastPathComponent();
        ISTreeItem oldRoot = (ISTreeItem)isTree.getModel().getRoot();
        if( model.isLeaf(treeItem) ){
            treeItem = (ISTreeItem)path.getParentPath().getLastPathComponent();
        }
        Item item = treeItem.getItem();
        if( item.getType() == Type.List ) {
            if( type.equals(Type.String.toString()) ) {
                item.getList().addItemEnd(value);
            } else if( type.equals(Type.Integer.toString()) ) {
                long intValue = Long.parseLong(value);
                item.getList().addItemEnd(intValue);
            } else if( type.equals(Type.Double.toString()) ) {
                double doubleValue = Double.parseDouble(value);
                item.getList().addItemEnd(doubleValue);
            } else if( type.equals(Type.Record.toString()) ) {
                item.getList().addItemEnd(new DefaultRecord());
            } else if( type.equals(Type.Record.toString()) ) {
                item.getList().addItemEnd(new DefaultList());
            }
        } else if( item.getType() == Type.Record ) {
            if( type.equals(Type.String.toString()) ) {
                item.getRecord().set(name, value);
            } else if( type.equals(Type.Integer.toString()) ) {
                long intValue = Long.parseLong(value);
                item.getRecord().set(name, intValue);
            }  else if( type.equals(Type.Double.toString()) ) {
                double doubleValue = Double.parseDouble(value);
                item.getRecord().set(name, doubleValue);
            } else if( type.equals(Type.Record.toString()) ) {
                item.getRecord().set(name, new DefaultRecord());
            } else if( type.equals(Type.Record.toString()) ) {
                item.getRecord().set(name, new DefaultList());
            }
        }
        ((ISTreeModel)isTree.getModel()).fireTreeStructureChanged(oldRoot);
        this.repaint();
        expandAll(isTree);
    }
    
    public void removeISElement( TreePath path )
    {
        ISTreeItem item = (ISTreeItem)path.getLastPathComponent();
        ISTreeItem parentItem = (ISTreeItem)path.getParentPath().getLastPathComponent();
        ISTreeItem oldRoot = (ISTreeItem)isTree.getModel().getRoot();
        if( parentItem.getItem().getType() == Type.List ) {
            List list = parentItem.getItem().getList();
            list.remove(""+isTree.getModel().getIndexOfChild(parentItem, item));
        } else if( parentItem.getItem().getType() == Type.Record ) {
            Record record = parentItem.getItem().getRecord();
            record.remove(item.getName());
        }
        ((ISTreeModel)isTree.getModel()).fireTreeStructureChanged(oldRoot);
        isTree.collapsePath(path.getParentPath());
        isTree.expandPath(path.getParentPath());
        expandAll(isTree);
    }
    
    public void actionPerformed( ActionEvent e )
    {
        JButton button = (JButton)e.getSource();
        if( button.getText().equals(SAVEAS) || (button.getText().equals(SAVE) && currISFile == null) ) {
            File file = getFile();
            if( file != null ) {
                saveIS(file);
            }
        } else if( button.getText().equals(SAVE) ) {
            if( currISFile != null ) {
                saveIS(currISFile);
            }
        } else if( button.getText().equals(LOAD) ) {
            File file = getFile();
            if( file != null ) {
                loadIS(file);
            }
        }
    }
    
    public File getFile()
    {
        JFileChooser fileChooser;
        if( currISPath != null ) {
            fileChooser = new JFileChooser(currISPath);
        } else {
            fileChooser = new JFileChooser();
        }
        int returnVal = fileChooser.showOpenDialog(editor.jframe);
        if( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fileChooser.getSelectedFile();
            currISFile = file;
            currISPath = file.getAbsolutePath();
            return file;
        }
        return null;
    }
    
    public void saveIS( File file )
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(is);
            outputStream.flush();
            outputStream.close();
        }catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    public void loadIS( File file )
    {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            is = (Record)inputStream.readObject();
            isTree.setModel(new ISTreeModel(is));
            expandAll(isTree);
        }catch( IOException e ) {
            e.printStackTrace();
        }catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void mousePressed(MouseEvent e) {
        int selRow = isTree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = isTree.getPathForLocation(e.getX(), e.getY());
        if(selRow != -1) {
            if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                ISTreeItem item = (ISTreeItem)selPath.getLastPathComponent();
                if( isTree.getModel().isLeaf(item) ) {
                    changeValueDialog(item, selPath);
                }
            }
            if( e.getButton() == MouseEvent.BUTTON3 ) {
                isTree.setSelectionPath(selPath);
                ISTreeMenu menu = new ISTreeMenu(this, selPath);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0)
    {
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        
    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
        
    }

    @Override
    public void mouseReleased(MouseEvent arg0)
    {
        
    }
}
