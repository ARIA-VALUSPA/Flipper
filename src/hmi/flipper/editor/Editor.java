/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.editor;

import hmi.flipper.behaviourselection.TemplateParser;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultHighlighter;

//import org.fife.plaf.Office2003.Office2003LookAndFeel;
//import org.fife.plaf.OfficeXP.OfficeXPLookAndFeel;
import org.fife.plaf.VisualStudio2005.VisualStudio2005LookAndFeel;


/**
 * 
 * @author Mark ter Maat
 *
 */
public class Editor
{
    public final static int FILE_BLOCK_WIDTH = 150;
    public final static int EDITOR_BLOCK_WIDTH = 650;
    public final static int EDITOR_BLOCK_HEIGHT = 400;
    public final static int CONSOLE_BLOCK_HEIGHT = 100;
    public final static int RFILE_BLOCK_WIDTH = 150;
    public final static int TEMPLATE_BLOCK_WIDTH = 200;
    public final static int IS_BLOCK_WIDTH = 450;
    public final static int RFILE_BLOCK_HEIGHT = 400;
    public final static int TEMPLATE_BLOCK_HEIGHT = 400;
    public final static int IS_BLOCK_HEIGHT = 400;
    
    public final static int BUTTON_PANEL_WIDTH = 150;
    public final static int BUTTON_PANEL_HEIGHT = 20;
    public final static int BUTTON_WIDTH = 65;
    public final static int BUTTON_HEIGHT = 25;

    protected Editor editor;

    public JFrame jframe;
    protected JTable fileTable;
    protected JTable runnerFileTable;

    protected ArrayList<File> openFiles = new ArrayList<File>();

    protected boolean openingFile = false;

    protected TemplateParser templateParser;
    private EditorPanel editorPanel;
    private RunnerPanel runnerPanel;

    public Editor()
    {
        editor = this;
        templateParser = new TemplateParser();
        initGui();
    }

    public void initGui()
    {
        jframe = new JFrame("Flipper Template Editor");
        jframe.setPreferredSize(new Dimension(FILE_BLOCK_WIDTH+EDITOR_BLOCK_WIDTH,EDITOR_BLOCK_HEIGHT+CONSOLE_BLOCK_HEIGHT));

        if( System.getProperty("os.name").toLowerCase().indexOf("win") >= 0 ) {
            /* We're on a windows-machine */
            try {
//                OfficeXPLookAndFeel officeLAF = new OfficeXPLookAndFeel();
//                Office2003LookAndFeel officeLAF = new Office2003LookAndFeel();
                VisualStudio2005LookAndFeel officeLAF = new VisualStudio2005LookAndFeel();
                UIManager.setLookAndFeel(officeLAF);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        JTabbedPane tabbedPane = new JTabbedPane();
        editorPanel = new EditorPanel(this);
        tabbedPane.addTab("Editor", null, editorPanel, "The Flipper template editor");
        runnerPanel = new RunnerPanel(this);
        tabbedPane.addTab("Runner", null, runnerPanel, "The Flipper template runner");

        jframe.add(tabbedPane);

        /* Finishes the frame */
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(true);
        jframe.setVisible(true);
    }
    
    
    public String openFile( File file )
    {
        String content = "";
        Scanner s = null;
        try {
            s = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return content;
        }
        openingFile = true;
        editorPanel.openFileArea.setText("");
        while( s.hasNextLine() ) {
            content = content + s.nextLine() + System.getProperty("line.separator");
//            editorPanel.openFileArea.setText(editorPanel.openFileArea.getText() + s.nextLine() + System.getProperty("line.separator"));
        }
        openingFile = false;
        fileTable.setRowSelectionInterval(openFiles.indexOf(file), openFiles.indexOf(file));
        runnerFileTable.setRowSelectionInterval(openFiles.indexOf(file), openFiles.indexOf(file));

        return content;
//        checkTemplate();
    }

    public void loadFile( File file )
    {
        if( openFiles.contains(file) ) {
            return;
        }
        openFiles.add(file);
        while( fileTable.getRowCount() < openFiles.size() ) {
            ((DefaultTableModel)fileTable.getModel()).addRow(new Vector<Object>());
        }
        fileTable.setValueAt(file.getName(), openFiles.size()-1, 0);
        while( runnerFileTable.getRowCount() < openFiles.size() ) {
            ((DefaultTableModel)runnerFileTable.getModel()).addRow(new Vector<Object>());
        }
        runnerFileTable.setValueAt(file.getName(), openFiles.size()-1, 0);
    }

    protected void error( String errorText )
    {
        JOptionPane.showMessageDialog(jframe, errorText);
    }
    

//    private void consoleLog( String str )
//    {
//        consoleArea.append(str + System.getProperty("line.separator") );
//    }

    


    /* Document Listener Events */
//    public void changedUpdate(DocumentEvent de) {
//        if( !openingFile ) {
//            checkTemplate();
//            lines.setText(getText());
//            if( !saveButton.isEnabled() ) saveButton.setEnabled(true);
//        }
//    }
//    public void insertUpdate(DocumentEvent de) {
//        if( !openingFile ) {
//            checkTemplate();
//            lines.setText(getText());
//            if( !saveButton.isEnabled() ) saveButton.setEnabled(true);
//        }
//    }
//    public void removeUpdate(DocumentEvent de) {
//        if( !openingFile ) {
//            checkTemplate();
//            lines.setText(getText());
//            if( !saveButton.isEnabled() ) saveButton.setEnabled(true);
//        }
//    }

//    public String getText(){
//        int caretPosition = openFileArea.getDocument().getLength();
//        Element root = openFileArea.getDocument().getDefaultRootElement();
//        String text = "1" + System.getProperty("line.separator");
//        for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
//            text += i + System.getProperty("line.separator");
//        }
//        return text;
//    }

//    public void highlightLine( int line )
//    {
//        try {
//            int startI = openFileArea.getLineStartOffset(line-1);
//            int endI = openFileArea.getLineEndOffset(line-1);
//            openFileArea.getHighlighter().addHighlight(startI, endI, new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE));
//        } catch (BadLocationException e) {
//            //  System.out.println(line);
//            e.printStackTrace();
//        }
//    }

//    public void checkTemplate()
//    {
//        consoleArea.setText("");
//        String str = openFileArea.getText();
//        try {
//            templateParser.parseString(str);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        HashMap<String,Integer> errors = templateParser.getErrors();
//        openFileArea.getHighlighter().removeAllHighlights();
//        for( String error : errors.keySet() ) {
//            Integer line = errors.get(error);
//            if( line != -1 ) {
//                highlightLine(line);
//                consoleLog(line + ": " + error);
//            } else {
//                consoleLog(error);
//            }
//        }
//    }

    public static void main( String args[] )
    {
        new Editor();
    }
}

class MyHighlighter extends DefaultHighlighter.DefaultHighlightPainter {
    public MyHighlighter(Color c) {
        super(c);
    }
}