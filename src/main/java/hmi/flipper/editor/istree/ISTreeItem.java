/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor.istree;

import hmi.flipper.informationstate.Item;

/**
 * 
 * @author Mark ter Maat
 *
 */
public class ISTreeItem
{
    private String name;
    private Item item;
    
    public ISTreeItem( String name, Item item )
    {
        this.name = name;
        this.item = item;
    }
    
    public Item getItem()
    {
        return item;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String toString()
    {
        String str = name + "("+item.getType()+"): ";  
        if( item.getType() == Item.Type.Double || item.getType() == Item.Type.Integer 
                || item.getType() == Item.Type.String ){
            str = str + item.getValue();
        }
        return str;
    }
    
}
