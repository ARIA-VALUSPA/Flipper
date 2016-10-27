/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor.istree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

class ISTreeMenu extends JPopupMenu implements ActionListener
{
    private final static String REMOVE = "Remove";
    private final static String ADD = "Add";
    
    private ISTreePanel panel;
    private TreePath path;
    
    public ISTreeMenu( ISTreePanel panel, TreePath path )
    {
        this.panel = panel;
        this.path = path;

        JMenuItem removeItem = new JMenuItem(REMOVE);
        removeItem.addActionListener(this);
        add(removeItem);

        JMenuItem addItem = new JMenuItem(ADD);
        addItem.addActionListener(this);
        add(addItem);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem item = (JMenuItem)e.getSource();
        if( item.getText().equals(REMOVE) ) {
            panel.removeISElement(path);
        } else if( item.getText().equals(ADD) ) {
            panel.popupAddPanel(path);
        }
    }
}
