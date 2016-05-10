/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.flipper.mongo.adapters;

import hmi.flipper.defaultInformationstate.DefaultRecord;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Siewart
 */
public class MetaAndRecord{
        public String _id;
        public Date _insertionDate;
        public Set<Map.Entry<String, Object>> meta;
        public DefaultRecord record;
        public MetaAndRecord(Document isDocument){
            _insertionDate = isDocument.getDate("date");
            _id = ((ObjectId) isDocument.get("_id")).toString();
            record = DefaultRecordAdapter.toRecord((Document)isDocument.get("record"));
            Document metaDoc = (Document) isDocument.get("meta");
            if(metaDoc == null){
                meta = new HashSet<>();
            }else{
                meta = metaDoc.entrySet();
            }
        }
    }
