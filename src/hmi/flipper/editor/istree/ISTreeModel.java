/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor.istree;

import hmi.flipper.defaultInformationstate.DefaultItem;
import hmi.flipper.informationstate.Item;
import hmi.flipper.informationstate.List;
import hmi.flipper.informationstate.Record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 * @author Mark ter Maat
 *
 */
public class ISTreeModel implements TreeModel {

    private ISTreeItem root;
    private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private HashMap<String,ISTreeItem> storedISItems = new HashMap<String,ISTreeItem>();
    
    public ISTreeModel( Record is )
    {
        root = new ISTreeItem("InformationState", new DefaultItem(is));
    }
    
    protected void fireTreeStructureChanged(ISTreeItem oldRoot) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[] {oldRoot});
        for (TreeModelListener tml : listeners) {
            tml.treeStructureChanged(e);
        }
    }
    
    @Override
    public void addTreeModelListener(TreeModelListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public Object getChild(Object obj, int index)
    {
        int max = getChildCount(obj);
        if( index >= max ) {
            index = max-1;
        }
        if( index < 0 ) {
            return "";
        }
        if( obj instanceof ISTreeItem ) {
            ISTreeItem isItem = (ISTreeItem)obj;
            Item item = isItem.getItem();
            if( item.getType() == Item.Type.String || item.getType() == Item.Type.Double || 
                    item.getType() == Item.Type.Integer ) {
                return null;
            } else if( item.getType() == Item.Type.Record ) {
                Record record = item.getRecord();
                ArrayList<String> keys = new ArrayList<String>(record.getItems().keySet());
                Collections.sort(keys);
                Item targetItem = record.getItems().get(keys.get(index));
                if( storedISItems.containsKey(targetItem.hashCode()) ) {
                    return storedISItems.get(targetItem.hashCode());
                } else {
                    ISTreeItem i = new ISTreeItem(keys.get(index), targetItem);
                    storedISItems.put(""+targetItem.hashCode(), i);
                    return i;
                }
            } else if( item.getType() == Item.Type.List ) {
                List list = item.getList();
                Item targetItem = list.getItem(index);
                if( storedISItems.containsKey(targetItem.hashCode()) ) {
                    return storedISItems.get(targetItem.hashCode());
                } else {
                    ISTreeItem i = new ISTreeItem("-", targetItem);
                    storedISItems.put(""+targetItem.hashCode(), i);
                    return i;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int getChildCount(Object obj)
    {
        if( obj instanceof ISTreeItem ) {
            ISTreeItem isItem = (ISTreeItem)obj;
            Item item = isItem.getItem();
            if( item.getType() == Item.Type.String || item.getType() == Item.Type.Double 
                    || item.getType() == Item.Type.Integer ) {
                return 0;
            } else if( item.getType() == Item.Type.Record ) {
                Record record = item.getRecord();
                return record.getItems().size();
            } else if( item.getType() == Item.Type.List ) {
                return item.getList().size();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parentObj, Object childObj)
    {
        if( parentObj instanceof ISTreeItem && childObj instanceof ISTreeItem ) {
            ISTreeItem parent = (ISTreeItem)parentObj;
            ISTreeItem child = (ISTreeItem)childObj;
//            System.out.println(child + " (in "+parent+")");
            Item parentItem = parent.getItem();
            Item childItem = child.getItem();
            if( parentItem.getType() == Item.Type.String || parentItem.getType() == Item.Type.Double || 
                    parentItem.getType() == Item.Type.Integer ) {
                return -1;
            } else if( parentItem.getType() == Item.Type.Record ) {
                Record record = parentItem.getRecord();
                ArrayList<String> keys = new ArrayList<String>(record.getItems().keySet());
                Collections.sort(keys);
                for( int i=0; i<keys.size(); i++ ) {
                    String key = keys.get(i);
                    Item targetItem = record.getItems().get(key);
                    if( targetItem == childItem ) {
                        return i;
                    }
                }
                return -1;
            } else if( parentItem.getType() == Item.Type.List ) {
                List list = parentItem.getList();
                for( int i=0; i<list.size(); i++ ) {
                    Item targetItem = list.getItem(i);
                    if( targetItem == childItem ) {
                        return i;
                    }
                }
                return -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public Object getRoot()
    {
        return root;
    }

    @Override
    public boolean isLeaf(Object obj)
    {
        if( obj instanceof ISTreeItem ) {
            ISTreeItem isItem = (ISTreeItem)obj;
            Item item = isItem.getItem();
            return ( item.getType() == Item.Type.String || item.getType() == Item.Type.Double 
                    || item.getType() == Item.Type.Integer );
        } else {
            return false;
        }
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        System.out.println("Method 'valueForPathChanged(). Not implemented yet.'");
    }
    
    public Collection<ISTreeItem> getItems()
    {
        return storedISItems.values();
    }
    
}
