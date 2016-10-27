/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection.template.value;

import hmi.flipper.defaultInformationstate.DefaultList;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.exceptions.TemplateParseException;
import hmi.flipper.exceptions.TemplateRunException;
import hmi.flipper.informationstate.Item;
import hmi.flipper.informationstate.List;
import hmi.flipper.informationstate.Record;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * An AbstractValue is a description of a value (the value-string from the XML), rather than the value itself.
 * If this is an atomic value (an Integer, a Double or a String), then the real Value can be calculated immediately.
 * However, if it is a reference to an InformationState variable, then the real Value can only be determined
 * at runtime, using the current version of the InformationState.
 * 
 * Therefore, use the function getValue() to get the real Value of this AbstractValue.
 * 
 * @version 0.1.1
 * Added support for more elaborate Strings (with spaces, and the following characters ,.?!_
 * 
 * @version 0.1
 * Basic version
 *
 * @author Mark ter Maat
 */

public class AbstractValue
{
    private static ScriptEngine jsEngine;
    private static ScriptEngine getJSEngine(){
        if(jsEngine == null){
            ScriptEngineManager manager = new ScriptEngineManager();
            jsEngine = manager.getEngineByName("js");
        }
        return jsEngine;
    }
    /* A valueString can be 1 value, or 2 values combined with an arithmetic sign (+-/*). This int keeps track of the number of components (1 or 2) */
    int numberOfComponents;

    /* The string-representation of the first value, and the real Value (NULL if the string is an IS-reference) */
    private String valueString;
    private Value value;
    /* The string-representation of the second value (if existing), and the real Value (NULL if the string is an IS-reference) */
    private String valueString2;
    private Value value2;

    /* If there are 2 components, this stores the mathematical operator (+-/*) */
    private String operator;

    /**
     * Creates a new AbstractValue given a String-representation of the value.
     * 
     * @param valueString - the String-representation of the value
     * @throws TemplateParseException
     */
    public AbstractValue( String valueString ) throws TemplateParseException
    {
        if(valueString.contains("@{")){
            this.valueString = valueString;
            numberOfComponents = 1;
        }else{
            /* Check if the string is only 1 component, or 2 components divided by an arithmetic sign */
            if( valueString.length() > 0 && (valueString.contains("+") 
                    || valueString.substring(1).contains("-") || valueString.contains("/") || valueString.contains("*")) ) {
                /* Set the arithmetic sign */
                if( valueString.contains("+") ) {
                    operator = "+";
                } else if( valueString.contains("-") ) {
                    operator = "-";
                } else if( valueString.contains("/") ) {
                    operator = "/";
                } else {
                    operator = "*";
                }

                int operatorIndex = valueString.indexOf(operator);
                String tempV1 = valueString.substring(0,operatorIndex).trim();
                String tempV2 = valueString.substring(operatorIndex+1).trim();

                if( tempV1 == null ){ 
                    throw new TemplateParseException("Empty valueString (first)");
                }
                if( tempV2 == null ){ 
                    throw new TemplateParseException("Empty valueString (second)");
                }

                /* Check if neither component is a reference to the InformationState */
                if( !tempV1.contains("$") && !tempV2.contains("$") ) {
                    /* Calculate the atomic values and combine them */
                    Value v1 = getStaticValue(valueString.substring(0, valueString.indexOf(operator)).replaceAll(" ", ""));
                    Value v2 = getStaticValue(valueString.substring(valueString.indexOf(operator)+1).replaceAll(" ", ""));

                    this.valueString = valueString;
                    numberOfComponents = 1;
                    if(v1 != null && v2 != null){
                    /* Calculate the new value */
                        value = calcValues(v1,v2);
                    }else{
                        value = new Value(valueString);
                    }
                } else {
                    Value v1 = null, v2 = null;
                    String vs1, vs2;
                    /* Calculate the atomic value if 1 component is not an IS-reference */
                    vs1 = valueString.substring(0, valueString.indexOf(operator)).replaceAll(" ", "");
                    boolean returnWholeStringValue = false;
                    if( !vs1.contains("$") ) {     
                        v1 = getStaticValue( vs1 );
                        if(v1 == null){
                            returnWholeStringValue = true;
                        }
                    }
                    vs2 = valueString.substring(valueString.indexOf(operator)+1).replaceAll(" ", "");
                    if( !vs2.contains("$") ) {
                        v2 = getStaticValue( vs2 );
                        if(v2 == null){
                            returnWholeStringValue = true;
                        }
                    }
                    if(!returnWholeStringValue){
                        this.valueString = vs1;
                        valueString2 = vs2;
                        numberOfComponents = 2;
                        value = v1;
                        value2 = v2;
                    }else{
                        numberOfComponents = 1;
                        this.valueString = valueString;
                        value = new Value(valueString);
                    }
                }
            } else {
                //this.valueString = valueString.replaceAll(" ", "");

                this.valueString = valueString;
                if( !valueString.contains("$") ) {
                    value = getStaticValue( valueString );
                    if(value == null)
                    {
                        value = new Value(valueString);
                    }
                }
                numberOfComponents = 1;
            }
        }
    }

    /**
     * If the value is atomic, calculate the Value and return it.
     * 
     * @param str - the String-representation of the Value
     * @return the Value
     */
    //TODO: Fix this, doesnt always classify as string properly
    private Value getStaticValue( String str )
    {
        if( str.length() == 0 ) return new Value("");

        /* First, try to determine the type of the value. */
        boolean isInt = true;
        boolean isDouble = true;
        boolean isString = true;
        if( !str.contains(".") ) {
            isDouble = false;
        }

        /* Check the string for characters that rule out a certain type */
        for( int i=0; i<str.length(); i++ ) {
            Character c = str.charAt(i);
            if( i == 0 ) {
                if( !Character.isLetter(c) && c != '!' && c != '?' && c != '#' && c != '\'' ) {
                    isString = false;
                }
                if( !Character.isDigit(c) && c != '-' ) {
                    isInt = false;
                }
                if( !Character.isDigit(c) && c != '.' && c != '-' ) {
                    isDouble = false;
                }
                if( !Character.isDigit(c) && !Character.isLetter(c) && c != '_' && c != ' ' && c != '.' && c != ',' && c != '!' && c != '?' ) {
                    isString = false;
                }
            } else {
                if( !Character.isDigit(c) ) {
                    isInt = false;
                }
                if( !Character.isDigit(c) && c != '.' ) {
                    isDouble = false;
                }
                if( !Character.isDigit(c) && !Character.isLetter(c) && c != '\'' && c != '_' && c != ' ' && c != '.' && c != ',' && c != '!' && c != '?' ) {
                    isString = false;
                }
            }
        }

        /* Depending on the type of the value, create a new Value */
        if( isInt && !isDouble && !isString ) {
            return new Value( Integer.parseInt(str) );
        } else if( isDouble && !isInt && !isString ) {
            return new Value( Double.parseDouble(str) );
        } else if( isString && !isInt && !isDouble ) {
            return new Value(str);
        }
        //System.out.println("Could not parse \"" + str + "\" to string, double or integer!");
        return null;
    }

    /**
     * Using the given String-representation of the value and the current InformationState, calculate the
     * current Value
     * 
     * @param str - the String-representation of the value
     * @param is - the current InformationState
     * @return Value
     * @throws TemplateRunException
     */
    private Value getDynamicValue( String str, Record is ) throws TemplateRunException
    {
        if(isJavaScript(str)){
            return parseJSString(str, is);
        }
        /* Determine the start-index and the end-index of the IS-reference, and create the IS-path */
        int startindex = str.indexOf("$");
        if( startindex == -1 ) {
            throw new TemplateRunException("Missing $-sign in referenced value ("+str+").");
        }
        int endindex = -1;
        for( int i=startindex+1; i<str.length(); i++ ) {
            char ch = str.charAt(i);
            if( !(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_' || ch == '.') ) {
                endindex = i;
            }
        }
        if( endindex == -1 ) {
            endindex = str.length();
        }
        String path = str.substring(startindex,endindex);

        /* Use the reference-path, get the InformationState-Item of the corresponding IS-variable */
        Item item = is.getValueOfPath(path);
        if( item == null ) {
            return null;
        } else {
            return new Value(item);
        }
    }

    /**
     * Based on the current InformationState, calculate the current Value of this AbstractValue and return it.
     * 
     * @param is - the current InformationState
     * @return Value
     * @throws TemplateRunException
     */
    public Value getValue( Record is ) throws TemplateRunException
    {
        if( numberOfComponents == 1 ) {
            /* If there is only 1 component, check if the Value has been already calculated (atomic variable). 
             * If not, then get the variable from the IS. */
            if( value != null ) {
                return value;
            } else {
                return getDynamicValue(valueString, is);
            }
        } else if( numberOfComponents == 2 ) {
            /* If there are 2 components, calculate the Value of both components, and combine them */
            /* Get the first Value */
            Value v1;
            if( value != null ) {
                v1 = value;
            } else {
                v1 = getDynamicValue(valueString,is);
            }

            /* Get the second value */
            Value v2;
            if( value2 != null ) {
                v2 = value2;
            } else {
                v2 = getDynamicValue(valueString2,is);
            }

            /* Combine the Values */
            if( v1 == null || v2 == null ) {
                return null;
            }
            try {
                return calcValues(v1, v2);
            }catch( TemplateParseException e ) {
                throw new TemplateRunException("v1_static:"+(value!=null)+",v2_static:"+(value2!=null)+e.getMessage());
            }
        } else {
            throw new TemplateRunException("Illegal number of arithmetic operations.");
        }	
    }

    /**
     * Combine the 2 given Values into 1 Value, depending on the types of the 2 Values
     * 
     * @param v1 - the first Value
     * @param v2 - the second Value
     * @return Value
     * @throws TemplateParseException
     */
    private Value calcValues( Value v1, Value v2 ) throws TemplateParseException
    {
        Value v = null;
        if( v1.getType() == Value.Type.String && v2.getType() == Value.Type.String ) {
            v = new Value(v1.getStringValue() + v2.getStringValue());
        } else if( v1.getType() == Value.Type.Double && v2.getType() == Value.Type.Double ) {
            v = new Value(calc(v1.getDoubleValue(),v2.getDoubleValue(),operator));
        } else if( v1.getType() == Value.Type.Double && v2.getType() == Value.Type.Integer ) {
            v = new Value(calc(v1.getDoubleValue(),v2.getIntegerValue(),operator));
        } else if( v1.getType() == Value.Type.Integer && v2.getType() == Value.Type.Double ) {
            v = new Value(calc(v1.getIntegerValue(),v2.getDoubleValue(),operator));
        } else if( v1.getType() == Value.Type.Integer && v2.getType() == Value.Type.Integer ) {
            if( operator.equals("/") ) {
                v = new Value(calcDivision(v1.getIntegerValue(),v2.getIntegerValue()));
            } else {
                v = new Value(calc(v1.getIntegerValue(),v2.getIntegerValue(),operator));
            }
        } else {
            throw new TemplateParseException("Unable to calculate the 2 values '"+v1.toString()+"' ("+v1.getType()+") and '"+v2.toString()+"' ("+v1.getType()+").");
        }
        return v;
    }

    /**
     * Uses the operator to calculate 2 integers.
     * 
     * @param i1 - integer 1
     * @param i2 - integer 2
     * @param operator - the arithmetic operator
     * @return the resulting integer
     * @throws TemplateParseException
     */
    private Integer calc( Integer i1, Integer i2, String operator ) throws TemplateParseException
    {
        return calc(i1.doubleValue(),i2.doubleValue(),operator).intValue();
    }

    /**
     * Uses the operator to calculate a double and an integer.
     * 
     * @param i1 - double 1
     * @param i2 - integer 1
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    private Double calc( Double d1, Integer i1, String operator ) throws TemplateParseException
    {
        return calc(d1,i1.doubleValue(),operator);
    }

    /**
     * Uses the operator to calculate an integer and a double.
     * 
     * @param i1 - ingteger 1
     * @param i2 - double 2
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    private Double calc( Integer i1, Double d1, String operator ) throws TemplateParseException
    {
        return calc(i1.doubleValue(),d1,operator);
    }

    /**
     * Uses the operator to calculate 2 doubles.
     * 
     * @param i1 - double 1
     * @param i2 - double 2
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    private Double calc( Double d1, Double d2, String operator ) throws TemplateParseException
    {
        if( operator.equals("+") ) {
            return d1+d2;
        } else if( operator.equals("-") ) {
            return d1-d2;
        } else if( operator.equals("*") ) {
            return d1*d2;
        } else if( operator.equals("/") ) {
            return d1/d2;
        } else {
            throw new TemplateParseException("Illegal arithmetic operation.");
        }
    }

    /**
     * Calculates a division of 2 integers.
     * this is a separate method because this is the only case in which the calculation of 2 integers result in a double.
     * 
     * @param i1 - integer 1
     * @param i2 - integer 2
     * @param operator - the arithmetic operator
     * @return the resulting double
     * @throws TemplateParseException
     */
    private Double calcDivision( Integer i1, Integer i2 )
    {
        return i1.doubleValue()/i2.doubleValue();
    }
    
    private boolean isJavaScript(String str){
        str = str.trim();
        return str.startsWith("@{") && str.endsWith("}");
    }
    
    private Value parseJSString(String str, Record is){
        String unmarkedString;
        StringBuilder staticStringBuilder = new StringBuilder();
        if(isJavaScript(str)){
            str = str.trim();
            unmarkedString = str.substring(2, str.length()-1);
        }else{
            System.out.println("Could not parse to JS: "+str );
            return new Value("");
        }
        int prevIndex = -1;
        int prevMarker = 0;
        
        unmarkedString = unmarkedString.replace("$$greater_than$$", ">");
        unmarkedString = unmarkedString.replace("$$greater_equals$$", ">=");
        unmarkedString = unmarkedString.replace("$$smaller_than$$", "<");
        unmarkedString = unmarkedString.replace("$$smaller_equals$$", ">=");
        unmarkedString = unmarkedString.replace("$$equals$$", "==");
        unmarkedString = unmarkedString.replace("$$not_equals$$", "!=");
        
        for (int index = unmarkedString.indexOf("$");
            index >= 0;
            index = unmarkedString.indexOf("$", index + 1))
        {
            if(prevIndex == -1 && index-1 > -1 && unmarkedString.charAt(index-1) == '\\'){
               
                staticStringBuilder.append(unmarkedString.substring(prevMarker, index-1));
                prevMarker = index;
                continue; // '\' allows to escape the $ 
            }
            if(prevIndex == -1){
                //@{$pizza.order$+25+$pizza.eat$*5}
                //               ____
                staticStringBuilder.append(unmarkedString.substring(prevMarker,index));
                prevIndex = index;
            }else{
                //@{$pizza.eat$*5}
                //  __________ 
                String isPath = unmarkedString.substring(prevIndex, index);
                Item val = is.getValueOfPath(isPath);
                String stringVar = "null";
                if(val != null){
                    stringVar = valueToJS(val);
                }
                staticStringBuilder.append( " " + stringVar + " ");
                
                //@{$pizza.eat$*5}
                //             ^
                prevMarker = index+1;
                prevIndex = -1;
            }
        }
        if(prevIndex != -1){
            System.out.println("Could not parse an information state to javascript");
        }
        if(prevMarker < unmarkedString.length()){
            staticStringBuilder.append(unmarkedString.substring(prevMarker));
        }
        try{
            //System.out.println(staticStringBuilder.toString());
            return js2Value(getJSEngine().eval(staticStringBuilder.toString()));
        }catch(Exception e){
            System.out.println("Could not execute script, make sure to return a single value (e.g. myValue;) : \n" + unmarkedString);
            return new Value("");
        }
    }

    private String valueToJS(Item val) {
       switch(val.getType()){
           case Double: return val.getDouble().toString();
           case Integer: return val.getInteger().toString();
           case List: return list2JS(val.getList());
           case Record: return record2JS(val.getRecord());
           case String: return "'"+val.getString()+"'";
           default: return "null";
       }
    }
    
    private String list2JS(List list){
        StringBuilder builder = new StringBuilder("[");
        for(int i = 0; i < list.size(); i ++)
        {
            Item it = list.getItem(i);

            switch (it.getType()) {
                case List:
                    builder.append(list2JS(it.getList()));
                    break;
                case Record:
                    builder.append(record2JS(it.getRecord()));
                    break;
                case Double : builder.append(it.getDouble()); 
                    break;
                case Integer: builder.append(it.getInteger()); 
                    break;
                default:
                    builder.append("'"+it.getString()+"'");
                    break;
            }
            
            if(i != list.size()-1){
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }
    
    private String record2JS(Record record){
        StringBuilder builder = new StringBuilder("{");
        Iterator<Entry<String, Item>> recs = record.getItems().entrySet().iterator();
        while(recs.hasNext()){
            Entry<String, Item> ent = recs.next();
            builder.append(ent.getKey());
            builder.append(":");
            Item it = ent.getValue();
            switch (it.getType()) {
                case List:
                    builder.append(list2JS(it.getList()));
                    break;
                case Record:
                    builder.append(record2JS(it.getRecord()));
                    break;
                case Double : builder.append(it.getDouble()); 
                    break;
                case Integer: builder.append(it.getInteger()); 
                    break;
                default:
                    builder.append("'"+it.getString()+"'");
                    break;
            }
            if(recs.hasNext()){
                builder.append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }
    private Value js2Value(Object jsResult){
        Object result = js2Object(jsResult);
        if(result instanceof Double){
           return new Value( (Double) result );
        }
        else if(result instanceof Integer){
            return new Value( (Integer) result );
        }
        else if(result instanceof List){
            return new Value(  (List) result );
        }
        else if(result instanceof Record){
            return new Value( (Record) result );
        }
        else{
            return new Value( result.toString() );
        }
    }
    private Object js2Object(Object jsResult) {
        if(jsResult instanceof ScriptObjectMirror){
            ScriptObjectMirror jsObject = (ScriptObjectMirror) jsResult;
            if(jsObject.size() > 0 && jsObject.containsKey("0")){
                List list = new DefaultList();
                for(int i = 0; i < jsObject.size(); i++){
                    Object toAdd = js2Object(jsObject.get(""+i));
                    if(toAdd == null){
                        break; // not a integer valued list
                    }
                    if(toAdd instanceof Double){
                        list.addItemEnd((Double) toAdd);
                    }
                    else if(toAdd instanceof Integer){
                        list.addItemEnd((Integer) toAdd);
                    }
                    else if(toAdd instanceof List){
                        list.addItemEnd((List)toAdd);
                    }
                    else if(toAdd instanceof Record){
                        list.addItemEnd((Record) toAdd);
                    }
                    else{
                        list.addItemEnd(toAdd.toString());
                    }
                }
                if(list.size() == ((ScriptObjectMirror) jsObject).size()){
                    return list;
                }
            }
            Record record = new DefaultRecord();
            for(Entry<String, Object> ent : jsObject.entrySet()){
                record.set(ent.getKey(), js2Object(ent.getValue()));
            }
            return record;
        }
        else if(jsResult instanceof Long || jsResult instanceof Double || jsResult instanceof Float)
        {
            if(jsResult instanceof Long){
                return ((Long)jsResult).doubleValue();
            }else if(jsResult instanceof Float){
                return ((Long)jsResult).floatValue();
            }else{
                return ((Double) jsResult);
            }
        }else if(jsResult instanceof Integer){
            return (Integer) jsResult;
        }
        else if(jsResult instanceof String){
            return (String) jsResult;
        }
        System.err.println("Could not parse JavaScript result to eligible DefaultRecord type! Type was: " + jsResult.getClass());
        return jsResult.toString();
    }  
}
