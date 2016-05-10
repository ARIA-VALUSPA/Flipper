/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper;

import eu.ariaagent.flipper.functions.DefaultFunctions;
import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.Scanner;

public class FlipperSimpleExample {

    public static void main( String args[] )
    {
        new FlipperSimpleExample();
    }
    
    public FlipperSimpleExample(){
        DefaultRecord is = new DefaultRecord();
        is.set("userturn", new DefaultRecord());

        TemplateController controller = new TemplateController();
        controller.processTemplateFile("./templates/example.xml");
        controller.addFunction(new DefaultFunctions());
        String userText = "";
        Scanner in = new Scanner(System.in);
        while( !userText.equals("quit") && !userText.equals("exit") ) {
            System.out.println("You can start speaking.");
            System.out.print(">> ");
            userText = in.nextLine();
            if( userText.toLowerCase().contains("hi") || userText.toLowerCase().contains("hello") ) {
                is.getRecord("userturn").set("intention", "greeting");
            } else if( userText.toLowerCase().contains(" goodbye ") || userText.toLowerCase().contains("bye") ) {
                is.getRecord("userturn").set("intention", "ending");
            } else {
                is.getRecord("userturn").set("intention", "Unknown");
            }
            is.getRecord("userturn").set("text", userText);        
            controller.checkTemplates(is);
        }
    }
    
    
}
