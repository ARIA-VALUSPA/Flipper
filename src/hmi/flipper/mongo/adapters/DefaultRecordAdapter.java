package hmi.flipper.mongo.adapters;
/**
 *
 * @author Siewart
 * requires flipper v0.5
 */
import hmi.flipper.defaultInformationstate.DefaultItem;
import hmi.flipper.defaultInformationstate.DefaultList;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.informationstate.Item;
import hmi.flipper.informationstate.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
public class DefaultRecordAdapter{
    
    public static Document toDocument(DefaultRecord record){
        HashMap<String, Item> items = record.getItems();
        
        Document doc = new Document();
        for(Map.Entry<String, Item> entry : items.entrySet()){
            Item item = entry.getValue();
            Object value = getObjectValue(item);
            doc.append(entry.getKey(), value);     
        }
        return doc;
    }
    
    public static DefaultRecord toRecord(Document doc){
        DefaultRecord r = new DefaultRecord();
         for(Map.Entry<String, Object> entry : doc.entrySet()){
            Object item = entry.getValue();
            Item value = getItemValue(item);
            r.set(entry.getKey(), value);
        }
         return r;
    }
    
    private static java.util.List toJavaList(List recordList){
        ArrayList<Object> javaList = new ArrayList<>(recordList.size());
        for(int i = 0; i < recordList.size(); i++){
            javaList.add(getObjectValue(recordList.getItem(i)));
        }
        return javaList;
    }
    
    private static List toRecordList(java.util.List javaList){
        List list = new DefaultList();
        
        for(int i = 0; i < javaList.size(); i++){
            Object obj = javaList.get(i);
            if( obj instanceof String ) {
                String str = (String)obj;
                if( isInt(str) ) {
                    list.addItemEnd(Integer.parseInt(str));

                } else if( isDouble(str) ) {
                    list.addItemEnd(Double.parseDouble(str));
                } else {
                    list.addItemEnd((String)obj);
                }
            }
            else if( obj instanceof Integer ) {
               list.addItemEnd((Integer)obj);
            }
            else if( obj instanceof Double ) {
                list.addItemEnd((Double)obj);
            }else if( obj instanceof java.util.List ) {
                list.addItemEnd(toRecordList((java.util.List)obj));
            }else if( obj instanceof Document ) {
                list.addItemEnd(toRecord((Document)obj));
            }else{
                list.addItemEnd("Invalid Mongo Entry");
            }
        }
        return list;
    }
    
    private static Object getObjectValue(Item item){
        Object value;
        switch (item.getType()) {
            case Double:  value = item.getDouble();
                     break;
            case Integer:  value = item.getInteger();
                     break;   
            case List:  value = toJavaList(item.getList()); //possible recursion
                     break;   
            case Record:  value = toDocument((DefaultRecord)item.getRecord()); //recursion
                     break;
            case String:  value = item.getString();
                     break;   
            default: value = "Invalid Record Entry";
                     break;
        }
        return value;
    }
    
     private static Item getItemValue(Object obj){
        Item value;
        if( obj instanceof String ) {
            String str = (String)obj;
            if( isInt(str) ) {
                value = new DefaultItem(Integer.parseInt(str));
                
            } else if( isDouble(str) ) {
                value = new DefaultItem(Double.parseDouble(str));
            } else {
                value = new DefaultItem(str);
            }
        }
        else if( obj instanceof Integer ) {
            value = new DefaultItem((Integer)obj);
        }
        else if( obj instanceof Double ) {
            value = new DefaultItem((Double) obj);
        }else if( obj instanceof java.util.List ) {
            value = new DefaultItem(toRecordList((java.util.List)obj));
        }else if( obj instanceof Document ) {
            value = new DefaultItem(toRecord((Document)obj));
        }else{
            value = new DefaultItem("Invalid Mongo Entry");
        }
        return value;
    }
     
    private static boolean isInt( String str )
    {
        if( str.length() == 0 ) return false;
        boolean isInt = true;
        for( int i=0; i<str.length(); i++ ) {
            Character c = str.charAt(i);
            if( i == 0 ) {
                if( !Character.isDigit(c) && c != '-' ) {
                    isInt = false;
                }
            } else {
                if( !Character.isDigit(c) ) {
                    isInt = false;
                }
            }
        }
        return isInt;
    }

    private static boolean isDouble( String str )
    {
        if( str.length() == 0 ) return false;
        boolean isDouble = true;
        for( int i=0; i<str.length(); i++ ) {
            Character c = str.charAt(i);
            if( i == 0 ) {
                if( !Character.isDigit(c) && c != '.' && c != '-' ) {
                    isDouble = false;
                }
            } else {
                if( !Character.isDigit(c) && c != '.' ) {
                    isDouble = false;
                }
            }
        }
        return isDouble;
    }
}
