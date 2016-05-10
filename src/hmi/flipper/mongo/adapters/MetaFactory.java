/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.flipper.mongo.adapters;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Siewart
 */
public class MetaFactory {
    private final Set<Map.Entry<String, Object>> meta = new HashSet<>();
    /**
     * Create a factory for fabricating a new Meta set
     * 
     * @param string
     * @param object
     * @return 
     */
    public static MetaFactory create(String string, Object object){
        return new MetaFactory(string, object);
    }
    private MetaFactory(String string, Object object){
        meta.add(new AbstractMap.SimpleEntry<>(string, object));
    }
    
    /**
     * Add a new entry
     * 
     * @param string Key for the meta entry
     * @param object Value for the meta entry
     * @return 
     */
    public MetaFactory add(String string, Object object){
        meta.add(new AbstractMap.SimpleEntry<>(string, object));
        return this;
    }
    
    /**
     * Builds the Meta object
     * @return the Set with meta entries
     */
    public Set<Map.Entry<String, Object>> build(){
        return meta;
    }       
}
