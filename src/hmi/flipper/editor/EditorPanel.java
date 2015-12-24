/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * 
 * @author Mark ter Maat
 *
 */
public class EditorPanel extends JPanel implements DocumentListener
{
    private Editor editor;
    
    private File openFile;
    
    private JButton saveButton;
    protected JTextArea openFileArea;
    private JTextArea lines;
    private JTextArea consoleArea;
    
    public EditorPanel( Editor editor )
    {
        this.editor = editor;
        
        setLayout(new BorderLayout());
        add(createFilePanel(),BorderLayout.WEST);
        add(createEditorPanel(), BorderLayout.CENTER);
        add(createConsolePanel(), BorderLayout.SOUTH);
    }
    
    public JPanel createFilePanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setPreferredSize(new Dimension(Editor.FILE_BLOCK_WIDTH,Editor.EDITOR_BLOCK_HEIGHT));
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        editor.fileTable = new JTable(0,1){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell
            }
        };
        editor.fileTable.setGridColor(Color.WHITE);
        DefaultTableCellRenderer tableCellRenderer = (DefaultTableCellRenderer)editor.fileTable.getDefaultRenderer(String.class);
        tableCellRenderer.setBackground(Color.WHITE);
        editor.fileTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2){
                    //System.out.println("Opening " + openFiles.get(((JTable)e.getSource()).getSelectedRow()).getName());
                    openFile(editor.openFiles.get(((JTable)e.getSource()).getSelectedRow()));
                    checkTemplate();
                }
            }
        });

        panel.add(editor.fileTable);

        panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalGlue());
        panel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(Editor.BUTTON_PANEL_WIDTH,Editor.BUTTON_PANEL_HEIGHT));

        /* Save button */
        saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(Editor.BUTTON_WIDTH,Editor.BUTTON_HEIGHT));
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(openFile != null) {
                    saveFile(openFile);
                } else {
                    editor.error("No file selected.");
                }
            }
        });
        buttonPanel.add(saveButton);

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
                    checkTemplate();
                }

            }
        });
        buttonPanel.add(loadButton);

        panel.add(buttonPanel);
        return panel;
    }

    public JPanel createEditorPanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setLayout(new BorderLayout());

        openFileArea = new JTextArea();
        openFileArea.setBackground(Color.WHITE);
        openFileArea.setLineWrap(false);
        openFileArea.getDocument().addDocumentListener(this);

        /* Set up line numbers */
        lines = new JTextArea("1");
        lines.setBackground(Color.LIGHT_GRAY);
        lines.setEditable(false);

        /* Set up scroll pane */
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(openFileArea);
        scrollPane.setRowHeaderView(lines);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane,BorderLayout.CENTER);

        return panel;
    }

    public JPanel createConsolePanel()
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setLayout(new BorderLayout());

        consoleArea = new JTextArea();
        consoleLog("Welcome to the Flipper Template Editor.");
        consoleArea.setBackground(Color.WHITE);
        consoleArea.setEditable(false);
        consoleArea.setLineWrap(true);

        /* Set up scroll pane */
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(consoleArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(Editor.FILE_BLOCK_WIDTH+Editor.EDITOR_BLOCK_WIDTH-15,Editor.CONSOLE_BLOCK_HEIGHT-10));

        panel.add(scrollPane,BorderLayout.CENTER);

        return panel;
    }
    
    public void checkTemplate()
    {
        consoleArea.setText("");
        String str = openFileArea.getText();
        try {
            editor.templateParser.parseString(str);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String,Integer> errors = editor.templateParser.getErrors();
        openFileArea.getHighlighter().removeAllHighlights();
        for( String error : errors.keySet() ) {
            Integer line = errors.get(error);
            if( line != -1 ) {
                highlightLine(line);
                consoleLog(line + ": " + error);
            } else {
                consoleLog(error);
            }
        }
    }
    
    private void consoleLog( String str )
    {
        consoleArea.append(str + System.getProperty("line.separator") );
    }
    
    public void setLineText()
    {
        lines.setText(getText());
    }
    
    /* Document Listener Events */
    public void changedUpdate(DocumentEvent de) {
        if( !editor.openingFile ) {
            checkTemplate();
            lines.setText(getText());
            if( !saveButton.isEnabled() ) saveButton.setEnabled(true);
        }
    }
    public void insertUpdate(DocumentEvent de) {
        if( !editor.openingFile ) {
            checkTemplate();
            lines.setText(getText());
            if( !saveButton.isEnabled() ) saveButton.setEnabled(true);
        }
    }
    public void removeUpdate(DocumentEvent de) {
        if( !editor.openingFile ) {
            checkTemplate();
            lines.setText(getText());
            if( !saveButton.isEnabled() ) saveButton.setEnabled(true);
        }
    }
    
    public String getText(){
        int caretPosition = openFileArea.getDocument().getLength();
        Element root = openFileArea.getDocument().getDefaultRootElement();
        String text = "1" + System.getProperty("line.separator");
        for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
            text += i + System.getProperty("line.separator");
        }
        return text;
    }
    
    public void openFile( File file )
    {
        String content = editor.openFile(file);
        openFileArea.setText(content);
        setLineText();
        openFile = file;
    }
    
    public void saveFile( File file )
    {
        String content = openFileArea.getText();
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveButton.setEnabled(false);
    }
    
    public void highlightLine( int line )
    {
        try {
            int startI = openFileArea.getLineStartOffset(line-1);
            int endI = openFileArea.getLineEndOffset(line-1);
            openFileArea.getHighlighter().addHighlight(startI, endI, new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE));
        } catch (BadLocationException e) {
            //  System.out.println(line);
            e.printStackTrace();
        }
    }
}
