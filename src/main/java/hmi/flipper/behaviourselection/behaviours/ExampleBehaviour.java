/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.behaviours;

import java.util.ArrayList;

/**
 * An example implementation of BehaviourClass
 * 
 * @author Mark ter Maat
 *
 */
public class ExampleBehaviour implements BehaviourClass
{

    public void execute( ArrayList<String> argNames, ArrayList<String> argValues )
    {
        System.out.println("It works!");
    }

    public void prepare( ArrayList<String> argNames, ArrayList<String> argValues )
    {
        System.out.println("Preparing");
    }
}
