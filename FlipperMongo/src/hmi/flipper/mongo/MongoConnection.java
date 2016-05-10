/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.flipper.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;

/**
 *
 * @author Siewart
 */
public class MongoConnection {
    MongoClient mongoClient;
    MongoDatabase db;
    
    public MongoConnection(String database){
        mongoClient = new MongoClient();
        db =  mongoClient.getDatabase(database);
    }
    
    public MongoConnection(String database, String host, int port){  
        mongoClient = new MongoClient(new ServerAddress(host, port));
        db = mongoClient.getDatabase(database);
    }
    
    public MongoConnection(String userName, String password, String database, String host, int port){  
        MongoCredential cred = MongoCredential.createCredential(userName, database, password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(cred));
        db = mongoClient.getDatabase(database);
    }
    
    public MongoCollection getCollection(String collectionName){
        return db.getCollection(collectionName);
    }
    
}
