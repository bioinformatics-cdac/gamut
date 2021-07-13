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
import org.bson.Document;

/**
 *
 * @author ramki
 */
public class MongoDBCreator {

    private MongoClient mongoClient;

    private MongoDatabase database;

    private MongoCollection<Document> chickenCollection;
    
    private MongoCollection<Document> testCollection;
    
    private MongoCollection<Document> geneidCollection;

    public void init(String host, int port, String mongoDatabase,String testColl) {
    
        mongoClient = new MongoClient(new ServerAddress(host, port));

        database = mongoClient.getDatabase(mongoDatabase);

        database.createCollection(testColl);
         
        testCollection=database.getCollection(testColl);
    }


    public MongoCollection<Document> getChickenCollection() {
        return chickenCollection;
    }
    
     public MongoCollection<Document> getGeneidCollection() {
        return geneidCollection;
    }

    public void display() {
        for (Document doc : chickenCollection.find()) {
        }
    }

    public void close() {
        mongoClient.close();
    }

}
