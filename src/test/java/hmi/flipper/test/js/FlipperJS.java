/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.flipper.test.js;

import hmi.flipper.behaviourselection.template.value.AbstractValue;
import hmi.flipper.behaviourselection.template.value.Value;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Siewart
 */
public class FlipperJS {
    
    public FlipperJS() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void returnVal(){
        DefaultRecord is = new DefaultRecord();
        try{
            Value v = new AbstractValue("@{54;}").getValue(is);
            assertEquals(new Integer(54), v.getIntegerValue());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        } 
    }
    @Test
    public void simpleMath(){
        DefaultRecord is = new DefaultRecord();
        try{
            Value v = new AbstractValue("@{(54*34);}").getValue(is);
            assertEquals(new Integer(54*34), v.getIntegerValue());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }

    }
    @Test
    public void jsFunctions(){
        DefaultRecord is = new DefaultRecord();
        try{
            Value v = new AbstractValue("@{parseInt(Math.round(5.6));}").getValue(is);
            assertEquals(new Double(6), v.getDoubleValue());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        } 
    }
    @Test
    public void retrieveFromIS(){
        DefaultRecord is = new DefaultRecord();
        is.set("pizza", "delicious");
        try{
            Value v = new AbstractValue("@{var pizza = $pizza$; pizza;}").getValue(is);
            assertEquals("delicious", v.getStringValue());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    @Test
    public void complexIS(){
        DefaultRecord is = new DefaultRecord();
        is.set("pizza.delicious", "true");
        is.set("pizza.amount", 24);
        try{
            Value v = new AbstractValue("@{var pizza = $pizza$; pizza;}").getValue(is);
            assertEquals("true", v.getRecordValue().getValueOfPath("delicious").getString());
            assertEquals(v.getRecordValue().getValueOfPath("amount").getInteger(), new Integer(24));
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    @Test
    public void list(){
        DefaultRecord is = new DefaultRecord();
        is.set("pizza._addlast", 1);
        is.set("pizza._addlast", 23);
        try{
            Value v = new AbstractValue("@{var pizza = $pizza$; pizza;}").getValue(is);
            assertEquals(true, v.getType() == Value.Type.List);
            assertEquals(new Integer(1), v.getListValue().getItem(0).getInteger());
            assertEquals(new Integer(23), v.getListValue().getItem(1).getInteger());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    @Test
    public void escapeDollar(){
        DefaultRecord is = new DefaultRecord();

        try{
            Value v = new AbstractValue("@{'\\$pizza is an information state variable';}").getValue(is);
           
            assertEquals("$pizza is an information state variable", v.getStringValue());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }
    }
    @Test //no exceptions
    public void nullValue(){
        DefaultRecord is = new DefaultRecord();

        try{
            Value v = new AbstractValue("@{'$pizza$';}").getValue(is); 
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }
    }
    @Test //no exceptions
    public void comparators(){
        DefaultRecord is = new DefaultRecord();

        try{
            Value v = new AbstractValue("@{if(5$$smaller_equals$$6){89;}else{99;}}").getValue(is);
            assertEquals(new Integer(99), v.getIntegerValue());
        }catch(TemplateRunException | TemplateParseException ex){
            System.out.println(ex.getMessage());
        }
    }
}
