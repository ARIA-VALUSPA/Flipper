/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor;

import hmi.flipper.behaviourselection.template.Template;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * 
 * @author Mark ter Maat
 *
 */
public class TemplateModel implements TableModel
{
    private ArrayList<Template> templates;
    private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
    
    public TemplateModel( ArrayList<Template> templates )
    {
        this.templates = templates;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return "Template";
    }

    @Override
    public int getRowCount() {
        return templates.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return templates.get(rowIndex).getId() + " ("+templates.get(rowIndex).getName()+")";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        templates.set(rowIndex, (Template)aValue);
    }
    
    public ArrayList<Template> getTemplates()
    {
        return templates;
    }
    
}
