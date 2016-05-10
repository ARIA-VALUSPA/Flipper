/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.flipper.mongo.adapters;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Sorts.descending;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.mongo.MongoConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 *
 * @author Siewart
 */
public class InformationStateAdapter {
    MongoConnection connection;
    MongoCollection collection;
    public InformationStateAdapter(MongoConnection connection, String collectionName){
        this.connection = connection;
        this.collection = connection.getCollection(collectionName);
    }
    
    public String PushInformationState(DefaultRecord is){
        return PushInformationState(is, null);
    }
    /**
     * 
     * @param is Record to push
     * @param metatags Meta tags to add to the record, can be used for retrieval
     * @return the _id as added by MongoDB to the database.
     */
    public String PushInformationState(DefaultRecord is, Set<Entry<String, Object>> metatags){
        Document isDoc = new Document("record", DefaultRecordAdapter.toDocument(is));
        Document metaDoc = new Document(); 
        if(metatags != null){
            for(Entry<String, Object> entry : metatags){
                metaDoc.append(entry.getKey(), entry.getValue());
            }
            isDoc.append("meta", metaDoc);
        }
        isDoc.append("date", new Date());
        collection.insertOne(isDoc);
        return ((ObjectId) isDoc.get("_id")).toString();
    }
   
    private List<Document> findIterableToDocs(FindIterable it){
        ArrayList<Document> docs = new ArrayList<>();
        try (MongoCursor<Document> cursor = it.iterator()) {
            
            while (cursor.hasNext()) {
                docs.add(cursor.next());
            }
        }catch(Exception e){
            return null;
        }
        return docs;
    }
       
     /**
     *  Return DefaultRecords by querying mongodb's _id, as return by Push
     * 
     * @param id _id as added by MongoDB
     * @return DefaultRecord with its Meta records
     */
    public MetaAndRecord GetInformationStateByID(String id){
        FindIterable it = collection.find(eq("_id", id)).limit(1);
        List<Document> docs = findIterableToDocs(it);
        if(docs != null && docs.size() > 0){
            Document doc = docs.get(0);
            if(doc != null){
                return new MetaAndRecord(doc);
            }else{
                return null;
            }
            
        }else{
            return null;
        }
    }
     /**
     * Returns Last added DefaultRecord
     * @return List of DefaultRecords with their Meta records
     */
    public MetaAndRecord GetLastInformationState(){
        List<MetaAndRecord> res = GetLastInformationStates(1);
        if(res != null){
            return res.get(0);
        }else{
            return null;
        }
    }
    
    /**
     *  Return DefaultRecords sorted descending by Date.
     * 
     * @param amount How many to return, (-1 = all)
     * @return List of DefaultRecords with their Meta records
     */
    public List<MetaAndRecord> GetLastInformationStates(int amount){
        FindIterable it = collection.find().sort(descending("date"));
        if(amount > -1){
            it.limit(amount);
        } 
        return findIterableToRecordList(it);
    }

    private List<MetaAndRecord> findIterableToRecordList(FindIterable it) {
        List<Document> docs = findIterableToDocs(it);
        if(docs != null && docs.size() > 0){
            ArrayList<MetaAndRecord> result = new ArrayList<>(docs.size());
            for(Document doc : docs){
                if(doc != null){
                    result.add(new MetaAndRecord(doc));
                }else{
                    result.add(null);
                }
            }
            return result;
            
        }else{
            return null;
        }
    }
    
    /**
     *  Return DefaultRecords by querying Meta tags added, ordered  descending
     *  by Date
     * 
     * @param metaTags Meta tags to search for
     * @param amount How many to return, (-1 = all)
     * @return List of DefaultRecords with their Meta records
     */
    public List<MetaAndRecord> GetISByMeta(Set<Entry<String, Object>> metaTags, int amount){
        Bson filter = null;
        for(Entry<String, Object> tag : metaTags){
            if(filter == null){
                filter = eq("meta."+tag.getKey(), tag.getValue());
            }else{
                filter = and(filter, eq("meta."+tag.getKey(), tag.getValue()));
            }
        }
        FindIterable it = collection.find(filter).sort(descending("date"));
        if(amount > -1){
            it.limit(amount);
        } 
        return findIterableToRecordList(it);
    }
    
    /**
     *  Return DefaultRecords by querying using a Bson filter
     * 
     * The default structure is as follows, use if to configure a filter:
     * {
     *  _id : ObjectId(),
     *  date : Date(),
     *  meta : { meta tags ...},
     *  record : { default record ... }
     * }
     * @param filter Bson filter to use
     * @param amount How many to return, (-1 = all)
     * @return List of DefaultRecords with their Meta records
     */
    public List<MetaAndRecord> GetISByFilter(Bson filter, int amount){
        FindIterable it = collection.find(filter).sort(descending("date"));
        if(amount > -1){
            it.limit(amount);
        } 
        return findIterableToRecordList(it);
    }
    
     /**
     *  Return Last DefaultRecord by querying Meta tags added
     * 
     * @param metaTags Meta tags to search for
     * @return DefaultRecord with their Meta records
     */
    public MetaAndRecord GetLastISByMeta(Set<Entry<String, Object>> metaTags){
        return GetISByMeta(metaTags, 1).get(0);
    }
}
