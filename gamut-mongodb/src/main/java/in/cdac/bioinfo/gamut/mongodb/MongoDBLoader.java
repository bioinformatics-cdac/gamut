/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.mongodb;


import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.bson.Document;

/**
 *
 * @author ramki
 */
public class MongoDBLoader {

    private MongoClient mongoClient;

    private MongoDatabase database;

   private MongoCollection<Document> collection;

   private MongoCollection<Document> genecollection;

    public MongoCollection<Document> getGenecollection() {
        return genecollection;
    }

    public void setGenecollection(MongoCollection<Document> genecollection) {
        this.genecollection = genecollection;
    }

    
   
    
    public MongoIterable<String> getAllCollections() {
    	return database.listCollectionNames();
    }
    public void init(String host, int port, String mongoDatabase, String mongoCollection, String geneCollection,String secondcollection) {

        mongoClient = new MongoClient(new ServerAddress(host, port));

        database = mongoClient.getDatabase(mongoDatabase);
     
        collection=database.getCollection(mongoCollection);
        
        genecollection=database.getCollection(geneCollection);
        
    }
    
    public boolean isCollectionExist(String collectionName) {
    	MongoCollection<Document> collection = database.getCollection(collectionName);
    	boolean isExist=false;
    	if(collection!=null) {
    		isExist =  true;
    	}else {
    		isExist = false;
    	}
    	return isExist;
    }
    
    public boolean deleteIfExistCollection(String collectionName) {
    	if(isCollectionExist(collectionName)) {
    		database.getCollection(collectionName).drop();
    		database.getCollection(collectionName+"_gtf").drop();
    		return true;
    	}
    	else
    		return false;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void setDatabase(MongoDatabase database) {
        this.database = database;
    }

     public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void setCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }
    
    

    public void display() {
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }



    public void close() {
        mongoClient.close();
    }

}
