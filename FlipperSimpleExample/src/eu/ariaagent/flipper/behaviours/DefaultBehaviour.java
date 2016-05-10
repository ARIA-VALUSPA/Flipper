/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper.behaviours;

import hmi.flipper.behaviourselection.behaviours.BehaviourClass;
import java.util.ArrayList;

/**
 * This is an example class that contains behaviours that can be used in templates.
 * 
 * @author Mark ter Maat
 *
 */
public class DefaultBehaviour implements BehaviourClass{
    @Override
    public void execute( ArrayList<String> argNames, ArrayList<String> argValues )
    {

        System.out.println("Agent: " + argValues.get(0));
    }
    @Override
    public void prepare( ArrayList<String> argNames, ArrayList<String> argValues )
    {
        
    } 
}
