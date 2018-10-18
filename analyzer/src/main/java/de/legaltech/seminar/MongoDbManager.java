package de.legaltech.seminar;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;

public class MongoDbManager {

    protected static MongoDbManager db;
    private MongoClient mongoClient;

    protected MongoDbManager(){
        try {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static MongoDbManager Instance(){
        if(db==null){
            db = new MongoDbManager();
        }
        return db;
    }
}
