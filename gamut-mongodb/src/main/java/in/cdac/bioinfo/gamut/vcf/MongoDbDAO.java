/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.vcf;

import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.vcf.bean.Gene;
import in.cdac.bioinfo.gamut.vcf.bean.Vcf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bson.BsonString;
import org.bson.Document;

/**
 *
 * @author ramki
 */
public class MongoDbDAO {

    private MongoClient mongoClient;

    private MongoDatabase database;

    private MongoCollection<Document> collection;

     private MongoCollection<Document> geneCollection;
     
     
    
    MongoDbDAO(MongoDBLoader mongoDBLoader) {
        mongoClient = mongoDBLoader.getMongoClient();

        database = mongoDBLoader.getDatabase();

        collection =mongoDBLoader.getCollection();
        
        this.geneCollection = mongoDBLoader.getGenecollection();
                
        System.out.println("MongoDbDAO mongoCollection:"+collection);
    }
    
     
    public MongoCollection<Document> getChickenCollection() {
        return collection;
    }
    
     public void insertHeader(StringBuilder builder) {
     
     }
     
     
     public void createIndices() {
    	 collection.createIndex(Indexes.ascending("Chromosome", "Position"));
    	 collection.createIndex(Indexes.ascending("Position"));
    	 
     }
    public void insertSampleNames(List<String> listofsamples,StringBuilder builder) {
         Vcf bean=new Vcf();
        bean.setId("0.0");
        bean.setChromosome("0.0");
        bean.setPosition(0);
        
        bean.setLineToProcess(listofsamples.toString());
        
        String listString = String.join(",", listofsamples);
        DistinctIterable<String> distinctChr = collection.distinct("Chromosome", String.class);
        
        List<String> chromosomeList = new ArrayList<String>();
         
        for (String chr : distinctChr) {
            chromosomeList.add(chr);
        }
        chromosomeList.add("ALL");
        System.out.println("in insert"+String.join(",",chromosomeList));
        Document document = new Document("_id", new BsonString(bean.getChromosome() + ":" + bean.getPosition()))
                .append("Chromosome", String.join(",",chromosomeList))
                .append("Position", null)
                .append("ID", bean.getId())
                .append("REF", bean.getRef())
                .append("Line",listString)
                .append("Header", builder.toString());
        
      //  document.append("Line", Arrays.asList(listofsamples));
       
        collection.insertOne(document);
        System.out.println("inserted");
    }
   
    public void insert(Vcf vcfb) {

        Document document = new Document("_id", new BsonString(vcfb.getChromosome() + ":" + vcfb.getPosition()))
                .append("Chromosome", vcfb.getChromosome())
                .append("Position", vcfb.getPosition())
                .append("ID", vcfb.getId())
                .append("REF", vcfb.getRef());

        List<Document> listLineDocs = new ArrayList<>();
        List<Document> listLineTokenDocs = new ArrayList<>();
         
        for (Map.Entry<String, List<String>> entry : vcfb.getMapAltLines().entrySet()) {
            Document altDocument = new Document("ALT", entry.getValue());
            Document lineDocument = new Document(entry.getKey(), altDocument);
            listLineDocs.add(lineDocument);
            
            for (Map.Entry<String, List<String>> tokenEntry : vcfb.getMapTokenLines().entrySet()) {
                if(tokenEntry.getKey().equals(entry.getKey())){
                    Document tokenDocument = new Document("ALT_RAW", tokenEntry.getValue());
                    Document tokenlineDocument = new Document(tokenEntry.getKey(), tokenDocument);
                    listLineTokenDocs.add(tokenlineDocument);
                }
            }
            
        }
        
        
        document.append("GeneName", vcfb.getGeneName());
        
        document.append("Lines", listLineDocs);
        document.append("Lines_RAW", listLineTokenDocs);
        
//        document.append("Lines", listLineTokenDocs);
//      collection.insertOne(document);

        collection.insertOne(document);
    }

    List<Document> listOfDocuments = new ArrayList<>();
    int count = 0;

    public void insertBatch(Vcf vcfb) {

        Document document = new Document("_id", new BsonString(vcfb.getChromosome() + ":" + vcfb.getPosition()))
                .append("Chromosome", vcfb.getChromosome())
                .append("Position", vcfb.getPosition())
                .append("ID", vcfb.getId())
                .append("REF", vcfb.getRef());

        List<Document> listLineDocs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : vcfb.getMapAltLines().entrySet()) {
            Document altDocument = new Document("ALT", entry.getValue());
            Document lineDocument = new Document(entry.getKey(), altDocument);
            listLineDocs.add(lineDocument);
        }

        document.append("Lines", listLineDocs);
        listOfDocuments.add(document);
        count++;
        if (count == 1000) {
            collection.insertMany(listOfDocuments);
            listOfDocuments.clear();
            count = 0;
        }

    }

    public void insert(List<Vcf> listOfVcfb) {

        List<Document> listOfDocs = new ArrayList<>();
        for (Vcf vcfb : listOfVcfb) {

            Document document = new Document("_id", new BsonString(vcfb.getChromosome() + ":" + vcfb.getPosition()))
                    .append("Chromosome", vcfb.getChromosome())
                    .append("Position", vcfb.getPosition())
                    .append("ID", vcfb.getId())
                    .append("REF", vcfb.getRef());

            List<Document> listLineDocs = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : vcfb.getMapAltLines().entrySet()) {
                Document altDocument = new Document("ALT", entry.getValue());
                Document lineDocument = new Document(entry.getKey(), altDocument);
                listLineDocs.add(lineDocument);
            }

            document.append("Lines", listLineDocs);
            listOfDocs.add(document);
        }
        collection.insertMany(listOfDocs);

    }

    public void display() {
        for (Document doc : collection.find()) {
          //  System.out.println(doc.toJson());
        }
    }

    public void close() {
        mongoClient.close();
    }

    void insertOneByOne(List<Vcf> listOfVcfBeans) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
    public MongoCollection<Document> getGeneCollection() {
        return geneCollection;
    }

    public void insert(Gene geneb) {

        Document geneDocument = new Document("_id", geneb.getGeneId())
                .append("chromosome", geneb.getChromosome())
                .append("start", geneb.getStartPosition())
                .append("end", geneb.getEndPosition());

        geneCollection.insertOne(geneDocument);

    }

    public void displayGenes() {
        for (Document doc : geneCollection.find()) {
            //System.out.println(doc.toJson());
        }
    }

    public void insert(List<Gene> beans, int count) {

        List<Document> listOfGeneDocument = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Gene geneb = beans.get(i);

            Document geneDocument = new Document("_id", geneb.getGeneId())
                    .append("chromosome", geneb.getChromosome())
                    .append("start", geneb.getStartPosition())
                    .append("end", geneb.getEndPosition());
            listOfGeneDocument.add(geneDocument);
        }
        geneCollection.insertMany(listOfGeneDocument);

    }
    
    public void getGeneName(Vcf vcfb){
        
    }
}
