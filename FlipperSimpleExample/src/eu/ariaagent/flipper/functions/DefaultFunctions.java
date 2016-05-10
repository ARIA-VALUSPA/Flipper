/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ariaagent.flipper.functions;

/**
 * This is an example class that contains functions that can be used in templates.
 * 
 * @author Mark ter Maat
 *
 */
public class DefaultFunctions
{
    int welcomeCounter = 0;
    int endingCounter = 0;

    public void count_welcomes( String v1 )
    {
        welcomeCounter++;
        System.out.println("User has said 'hi' " + welcomeCounter + " times now.");
        System.out.println("The last time, he said: " + v1);
    }

    public void count_endings( String v1 )
    {
        endingCounter++;
        System.out.println("User has said 'goodbye' " + endingCounter + " times now.");
        System.out.println("The last time, he said: " + v1);
    }
}

