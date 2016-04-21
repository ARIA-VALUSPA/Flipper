/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor;

import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.behaviourselection.template.TemplateState;
import hmi.flipper.behaviourselection.template.effects.Effect;
import hmi.flipper.behaviourselection.template.effects.Update;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.editor.istree.ISTreePanel;
import hmi.flipper.exceptions.TemplateRunException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * 
 * @author Mark ter Maat
 *
 */
public class RunnerPanel extends JPanel
{
    private static final String APPLYALL = "Apply all";
    
    private static RunnerPanel runnerPanel;
    private Editor editor;
    private DefaultRecord is;
    private JTable templateTable;

    private JPanel isPanel;

    private JTextArea runnerConsoleArea;

    public RunnerPanel( Editor editor )
    {
        is = new DefaultRecord();
        is.set("a", "cde");
        is.set("b",1);
        is.set("c",0.2);
        is.set("d.a","a");
        is.set("d.b",1);
        is.set("e._addlast","a");
        is.set("e._addlast", "b");

        this.editor = editor;

        isPanel = new ISTreePanel( editor, this, is );

        setLayout(new BorderLayout());
        add(createRunnerFilePanel(),BorderLayout.WEST);
        add(createTemplatePanel(),BorderLayout.CENTER);
        add(isPanel,BorderLayout.EAST);
        add(createRunnerConsolePanel(),BorderLayout.SOUTH);
    }

    public JPanel createRunnerFilePanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setPreferredSize(new Dimension(Editor.RFILE_BLOCK_WIDTH,Editor.RFILE_BLOCK_HEIGHT));
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        editor.runnerFileTable = new JTable(0,1){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell
            }
        };
        editor.runnerFileTable.setGridColor(Color.WHITE);
        DefaultTableCellRenderer tableCellRenderer = (DefaultTableCellRenderer)editor.runnerFileTable.getDefaultRenderer(String.class);
        tableCellRenderer.setBackground(Color.WHITE);
        editor.runnerFileTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 1){
                    openFile(editor.openFiles.get(((JTable)e.getSource()).getSelectedRow()));
                }
            }
        });

        panel.add(editor.runnerFileTable);

        panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(Editor.BUTTON_PANEL_WIDTH,Editor.BUTTON_PANEL_HEIGHT));

        /* Load button */
        JButton loadButton = new JButton("Load");
        loadButton.setPreferredSize(new Dimension(Editor.BUTTON_WIDTH,Editor.BUTTON_HEIGHT));
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File templateDir = null;
                try {
                    URL url = editor.getClass().getClassLoader().getResource("templates");
                    if( url != null ) {
                        templateDir = new File(url.toURI());
                    }
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
                if( templateDir == null ) templateDir = new File(".");
                JFileChooser fileChooser = new JFileChooser(templateDir);
                int returnVal = fileChooser.showOpenDialog(editor.jframe);
                if( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File file = fileChooser.getSelectedFile();
                    editor.loadFile(file);
                    openFile(file);
                    //extractTemplates();
                }

            }
        });
        buttonPanel.add(loadButton);

        panel.add(buttonPanel);
        return panel;
    }

    public JPanel createTemplatePanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setPreferredSize(new Dimension(Editor.TEMPLATE_BLOCK_WIDTH,Editor.TEMPLATE_BLOCK_HEIGHT));
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        templateTable  = new JTable(new TemplateModel(new ArrayList<Template>()));
        templateTable.setGridColor(Color.WHITE);
        templateTable.setDefaultRenderer(templateTable.getColumnClass(0), new ColorCellRenderer());
        DefaultTableCellRenderer tableCellRenderer = (DefaultTableCellRenderer)templateTable.getDefaultRenderer(String.class);
        tableCellRenderer.setBackground(Color.WHITE);
        templateTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2){
                    int row = ((JTable)e.getSource()).getSelectedRow();
                    performTemplate(row);
                }
            }
        });

        panel.add(templateTable,BorderLayout.NORTH);
        
        JPanel isButtonPanel = new JPanel();
        isButtonPanel.setLayout(new BoxLayout(isButtonPanel,BoxLayout.X_AXIS));
        isButtonPanel.setBackground(Color.WHITE);
        isButtonPanel.setPreferredSize(new Dimension(Editor.IS_BLOCK_WIDTH,Editor.BUTTON_PANEL_HEIGHT));

        JButton applyButton = new JButton(APPLYALL);
        applyButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                performAllTemplates();
            }
        });
        isButtonPanel.add(applyButton);
        
        panel.add(isButtonPanel,BorderLayout.SOUTH);

        return panel;
    }

    public JPanel createRunnerConsolePanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setLayout(new BorderLayout());

        runnerConsoleArea = new JTextArea();
        runnerConsoleLog("Welcome to the Flipper Template Editor.");
        runnerConsoleArea.setBackground(Color.WHITE);
        runnerConsoleArea.setEditable(false);
        runnerConsoleArea.setLineWrap(true);

        /* Set up scroll pane */
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(runnerConsoleArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(Editor.FILE_BLOCK_WIDTH+Editor.EDITOR_BLOCK_WIDTH-15,Editor.CONSOLE_BLOCK_HEIGHT-10));

        panel.add(scrollPane,BorderLayout.CENTER);

        return panel;
    }

    private void runnerConsoleLog( String str )
    {
        runnerConsoleArea.append(str + System.getProperty("line.separator") );
    }    

    public String getPathString( TreePath pathTree )
    {
        Object[] path = pathTree.getPath();
        String pathString = "";
        for( int i=1; i<path.length; i++ ) {
            String subPath = path[i].toString();
            subPath = subPath.substring(0, subPath.indexOf("("));
            pathString = pathString + "." + subPath;
        }
        return pathString.substring(1);
    }

    public void openFile( File file )
    {
        String content = editor.openFile(file);
        ArrayList<Template> templates = null;
        try {
            templates = editor.templateParser.parseString(content);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        } catch (SAXException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if( templates == null ) {
            System.err.println("No Templates found.");
            return;
        }
        showTemplates(templates);
    }

    public void showTemplates( ArrayList<Template> templates )
    {
        templateTable.setModel(new TemplateModel(templates));
        checkTemplates();
    }

    public void checkTemplates()
    {
        ArrayList<Template> templates = ((TemplateModel)templateTable.getModel()).getTemplates();
        for( int i=0; i<templates.size(); i++ ) {
            TemplateState tState = templates.get(i).checkTemplate(is);
            TableCellRenderer cellRenderer = templateTable.getCellRenderer(i, 0);
            if( tState.allPreconditionsSatisfied() ) {
                if( cellRenderer instanceof ColorCellRenderer ) {
                    ((ColorCellRenderer)cellRenderer).addSelectionRow(i);
                }
            } else {
                if( cellRenderer instanceof ColorCellRenderer ) {
                    ((ColorCellRenderer)cellRenderer).removeSelectionRow(i);
                }
            }
        }
        templateTable.repaint();
    }
    
    public void performTemplate(int row)
    {
        ArrayList<Template> templates = ((TemplateModel)templateTable.getModel()).getTemplates();
        Template template = templates.get(row);
        TemplateState result = template.checkTemplate(is);
        if( result.allPreconditionsSatisfied() ) {
            for( Effect effect : result.getEffects() ) {
                if( effect instanceof Update ) {
                    try {
                        effect.apply(is, null);
                    }catch( TemplateRunException e ) {
                        System.err.println("Error while applying effect of Template  "
                                +result.getTemplate().getId()+"("+result.getTemplate().getName()+")");
                        e.printStackTrace();
                    }
                }
            }
        }
        isPanel.repaint();
        checkTemplates();
    }
    
    public void performAllTemplates()
    {
        ArrayList<Template> templates = ((TemplateModel)templateTable.getModel()).getTemplates();
        ArrayList<TemplateState> results = new ArrayList<TemplateState>();
        for( Template template : templates ) {
            results.add(template.checkTemplate(is));
        }
        for( TemplateState result : results ) {
            if( result.allPreconditionsSatisfied() ) {
                for( Effect effect : result.getEffects() ) {
                    if( effect instanceof Update ) {
                        try {
                            effect.apply(is, null);
                        }catch( TemplateRunException e ) {
                            System.err.println("Error while applying effect of Template  "
                                    +result.getTemplate().getId()+"("+result.getTemplate().getName()+")");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        isPanel.repaint();
        checkTemplates();
    }

    public static RunnerPanel getRunnerPanel()
    {
        return runnerPanel;
    }

    /**
     * 
     * @author Mark ter Maat
     *
     */
    public class ColorCellRenderer extends DefaultTableCellRenderer
    {
        private Set<Integer> selectedRows = new HashSet<Integer>();
        
        public void addSelectionRow(int row){
            selectedRows.add(row);
        }
        
        public void removeSelectionRow(int row){
            selectedRows.remove(row);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Only for specific cell
            if (selectedRows.contains(row)) {
                c.setBackground(Color.red);
            } else {
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    }
}