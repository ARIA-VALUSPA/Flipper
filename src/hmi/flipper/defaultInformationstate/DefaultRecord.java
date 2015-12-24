/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.defaultInformationstate;

import hmi.flipper.informationstate.*;

import java.io.Serializable;
import java.util.HashMap;

/**
 * An InformationState contains variables with a name, a type and a value.
 * It can have be an atomic value (the type is a String, an Integer, or a Double), but it can also
 * be a substructure, namely:
 * - a List- a list of elements of the same type which don't have a name, only an order.
 * - a Record - a substructure which is basically an inner-InformationState (or, the InformationState is basically a Record).
 * 
 * If you know what type a certain variable is, you can directly ask for that type (for example: getString(name)).
 * However, if you do not, then you get an Item, which includes the value and the type.
 * 
 * It is also possible to specify a path to a variable (for example if it is inside a record or a list, or even deeper).
 * 
 * @version 0.1.3
 * getType() methods now accept Paths too.
 * 
 * @version 0.1.2
 * Changed the way setting a new variable works, it will now accept and create a path too.
 * 
 * @version 0.1.1
 * Added support for the removal of elements given a Path.
 * Fixed a crash when asking the value of a non-existing item.
 * 
 * @version 0.1
 * First version
 * 
 * @author Mark ter Maat
 */

public class DefaultRecord implements Serializable, Record
{
    /* This record, programmed as a HashMap where the key is the name of the variable, and the value the Item-object of the value. */
    private HashMap<String,Item> is = new HashMap<String,Item>();

    /**
     * Creates a new InformationState
     */
    public DefaultRecord()
    {

    }

    /**
     * Returns the Item at the place of the given path.
     * The path specifies the name of the variable you want. Substructures can be used by using a dot.
     * For example, an element var1 of the record r1 can be called by 'r1.var1'.
     * A list does not use indices, you can only get the first or the last element (thereby using it as a queue or a stack).
     * This is done with 'list1._first' or 'list1._last'.
     * 
     * @param path - the path of the variable in the InformationState you want.
     * @return the Item at the wanted place, or NULL if it does not exist.
     */
    public Item getValueOfPath( String path )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        /* Determine the name (the name of the variable of this InformationState) and the valuePath 
         * (the path of the substructure of the first Name, only used if the value of the first Name is a Record or a List) */
        String name;
        String valuePath;
        if( path.contains(".") ) {
            name = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            name = path;
            valuePath = null;
        }

        /* Process the remaining ValuePath in the Item at the found place, and return the resulting Item */
        Item i = is.get(name);
        if( i != null ) {
            return i.getValueOfPath(valuePath);
        } else {
            return null;
        }
    }

    public Item.Type getTypeOfPath( String path )
    {
        path = path.replaceAll(DefaultList.ADDFIRST, DefaultList.FIRST);
        path = path.replaceAll(DefaultList.ADDLAST, DefaultList.LAST);
        Item i = getValueOfPath(path);
        if( i != null ) {
            return i.getType();
        } else {
            return null;
        }
    }

    public void set( String path, Object value )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        if( path.contains(".") ) {
            String p1 = path.substring(0, path.indexOf("."));
            String p2 = path.substring(path.indexOf(".")+1, path.length());
            Item i = is.get(p1);
            if( i == null ) {
                if( p2.startsWith(DefaultList.FIRST) || p2.startsWith(DefaultList.LAST) 
                        || p2.startsWith(DefaultList.ADDFIRST) || p2.startsWith(DefaultList.ADDLAST) ) {
                    i = new DefaultItem(new DefaultList());
                } else {
                    i = new DefaultItem(new DefaultRecord());
                }
            }
            i.set( p2, value );
            is.put(p1,i);
        } else {
            is.put(path, new DefaultItem(value));
        }
    }

    /**
     * Set a new variable with the given name and the given String as value.
     * @param name - the name of the new variable
     * @param value - the new String value
     */
    public void set( String name, String value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given Integer as value.
     * @param name - the name of the new variable
     * @param value - the new Integer value
     */
    public void set( String name, Integer value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given Double as value.
     * @param name - the name of the new variable
     * @param value - the new Double value
     */
    public void set( String name, Double value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given InformationState (Record) as value.
     * @param name - the name of the new variable
     * @param value - the new InformationState value
     */
    public void set( String name, Record value )
    {
        set( name,(Object)value );
    }

    /**
     * Set a new variable with the given name and the given List as value.
     * @param name - the name of the new variable
     * @param value - the new List value
     */
    public void set( String name, List value )
    {
        set( name,(Object)value );
    }

    /**
     * Returns the String-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the String value
     */
    public String getString( String name )
    {
        Item i = getValueOfPath(name);
        if( i != null ) {
            return i.getString();
        } else {
            return null;
        }
    }

    /**
     * Returns the Integer-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Integer value
     */
    public Integer getInteger( String name )
    {
        Item i = getValueOfPath(name);
        if( i != null ) {
            return i.getInteger();
        } else {
            return null;
        }
    }

    /**
     * Returns the Double-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Double value
     */
    public Double getDouble( String name )
    {
        Item i = getValueOfPath(name);
        if( i != null ) {
            return i.getDouble();
        } else {
            return null;
        }
    }

    /**
     * Returns the Record-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Record value
     */
    public Record getRecord( String name )
    {
        Item i = getValueOfPath(name);
        if( i != null ) {
            return i.getRecord();
        } else {
            return null;
        }
    }

    /**
     * Returns the List-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the List value
     */
    public List getList( String name )
    {
        Item i = getValueOfPath(name);
        if( i != null ) {
            return i.getList();
        } else {
            return null;
        }
    }

    /**
     * Returns the Item-value of the variable with the given name.
     * @param name - the name of the wanted variable
     * @return the Item value
     */
    public Item getItem( String name )
    {
        Item i = is.get(name);
        return i;
    }

    /**
     * Removes the variable with the given path.
     * @param path - the name of the variable to delete
     */
    public void remove( String path )
    {
        /* Check if the path starts with a $ */
        if( path.charAt(0) == '$' ) {
            path = path.substring(1);
        }

        /* Determine the name (the name of the variable of this InformationState) 
         * and the valuePath (the path of the substructure of the first Name, only used if the value of the first Name is a Record or a List) */
        String name;
        String valuePath;
        if( path.contains(".") ) {
            name = path.substring(0, path.indexOf("."));
            valuePath = path.substring(path.indexOf(".")+1, path.length());
        } else {
            name = path;
            valuePath = null;
        }

        /* Process the remaining ValuePath in the Item at the found place, and return the resulting Item */
        Item i = is.get(name);
        if( i == null ) {
            // Done, nothing to remove.
            return;
        } else if( valuePath == null ) {
            is.remove(name);
        } else if( i.getType() == Item.Type.List ) {
            i.getList().remove(valuePath);
        } else if( i.getType() == Item.Type.Record ) {
            i.getRecord().remove(valuePath);
        } else {
            is.remove(name);
        }
    }

    /**
     * Prints the InformationState as a textual representation.
     */
    public void print()
    {
        print("");
    }

    /**
     * Prints the InformationState as a textual representation with the given String before each print (used for structuring).
     * @param pre - the String to put in front of each line
     */
    public void print( String pre )
    {
        for( String key : is.keySet() ) {
            Item i = is.get(key);
            if( i.getType() == Item.Type.String || i.getType() == Item.Type.Integer || i.getType() == Item.Type.Double ) {
                System.out.println(pre + "-" + key + " = " + i.getValue().toString());
            } else if( i.getType() == Item.Type.Record ) {
                System.out.println(pre + "-Record:" + key + " [");
                ((DefaultRecord)i.getRecord()).print(pre + "  ");
            } else if( i.getType() == Item.Type.List ) {
                System.out.println(pre + "-List:" + key + " [");
                ((DefaultList)i.getList()).print(pre + "  ");
            }
        }
    }

    public HashMap<String,Item> getItems()
    {
        return is;
    }
}
