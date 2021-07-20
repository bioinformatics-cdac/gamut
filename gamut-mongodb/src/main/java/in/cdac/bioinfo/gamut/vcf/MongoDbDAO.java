/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.vcf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.vcf.bean.Gene;
import in.cdac.bioinfo.gamut.vcf.bean.Vcf;

/**
 *
 * @author ramki
 */
public class MongoDbDAO {

	private MongoClient mongoClient;

	private MongoDatabase database;

	private MongoCollection<Document> collection;

	private MongoCollection<Document> geneCollection;
	List<Document> vcfDocuments;

	private int maxRecordSize;

	public MongoDbDAO(MongoDBLoader mongoDBLoader, int maxRecordSize) {

		mongoClient = mongoDBLoader.getMongoClient();
		database = mongoDBLoader.getDatabase();
		collection = mongoDBLoader.getCollection();
		this.geneCollection = mongoDBLoader.getGenecollection();
		System.out.println("MongoDbDAO mongoCollection:" + collection);
		this.maxRecordSize = maxRecordSize;

		vcfDocuments = new ArrayList<>(maxRecordSize);
		for (int i = 0; i < maxRecordSize; i++) {
			Document vcfBeanDocument = new Document();
			vcfDocuments.add(vcfBeanDocument);
		}

	}

	public MongoDbDAO(MongoDBLoader mongoDBLoader) {
		mongoClient = mongoDBLoader.getMongoClient();

		database = mongoDBLoader.getDatabase();

		collection = mongoDBLoader.getCollection();

		this.geneCollection = mongoDBLoader.getGenecollection();

		System.out.println("MongoDbDAO mongoCollection:" + collection);

	}

	public MongoCollection<Document> getChickenCollection() {
		return collection;
	}

	public void insertHeader(StringBuilder builder) {

	}

	public void geneCreateIndex() {
		// db.geneInfo.createIndex({"chromosome": 1 , "start":1, "end":1})
		geneCollection.createIndex(Indexes.ascending("chromosome", "start", "end"));

	}

	public void createIndices() {
		collection.createIndex(Indexes.ascending("Chromosome", "Position"));
		collection.createIndex(Indexes.ascending("Position"));

	}

	public void insertSampleNames(List<String> listofsamples, List<String> vcfHeaders) {
		Vcf bean = new Vcf();
		bean.setId("0.0");
		bean.setChromosome("0.0");
		bean.setPosition(0);

		bean.setLineToProcess(listofsamples.toString());

		DistinctIterable<String> distinctChromosomes = collection.distinct("Chromosome", String.class);

		List<String> chromosomeList = new ArrayList<String>();

		for (String chr : distinctChromosomes) {
			chromosomeList.add(chr);
		}
		chromosomeList.add("ALL");

		Document document = new Document("_id", new BsonString(bean.getChromosome() + ":" + bean.getPosition()))
				.append("Chromosome", chromosomeList).append("Samples", listofsamples).append("Headers", vcfHeaders);

		collection.insertOne(document);
		System.out.println("inserted");
	}

	public void insertOptimized(List<Vcf> vcfBeans, int index) {
		Document vcfDocument = null;
		Vcf vcfb = null;
		for (int i = 0; i < index; i++) {
			vcfb = vcfBeans.get(i);
			vcfDocument = vcfDocuments.get(i);
			vcfDocument.clear();
			buildVcfDocument(vcfDocument, vcfb);
		}

		if (index == maxRecordSize) {
			try {
				collection.insertMany(vcfDocuments);
			} catch (Throwable throwable) {
				for (int i = 0; i < index; i++) {
					try {
						collection.insertOne(vcfDocuments.get(i));
					} catch (Throwable throwable1) {
						System.out.println("Duplicate record id : " + vcfDocuments.get(i).getObjectId(throwable1));
					}

				}

			}
		} else {
			List<Document> insertDocumentList = new ArrayList<>(index);
			for (int i = 0; i < index; i++) {
				vcfDocument = vcfDocuments.get(i);
				insertDocumentList.add(vcfDocument);
			}
			try {
				collection.insertMany(insertDocumentList);
			} catch (Throwable throwable) {
				for (int i = 0; i < index; i++) {
					try {
						collection.insertOne(insertDocumentList.get(i));
					} catch (Throwable throwable1) {
						System.out
								.println("Duplicate record id : " + insertDocumentList.get(i).getObjectId(throwable1));
					}

				}

			}

		}

	}

	private void buildVcfDocument(Document vcfDocument, Vcf vcfb) {
		vcfDocument.put("_id", new BsonString(vcfb.getChromosome() + ":" + vcfb.getPosition()));
		vcfDocument.put("Chromosome", vcfb.getChromosome());
		vcfDocument.put("Position", vcfb.getPosition());
		vcfDocument.put("ID", vcfb.getId());
		vcfDocument.put("REF", vcfb.getRef());

		List<Document> listLineDocs = new ArrayList<>();
		List<Document> listLineTokenDocs = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry : vcfb.getMapAltLines().entrySet()) {
			Document altDocument = new Document("ALT", entry.getValue());
			Document lineDocument = new Document(entry.getKey(), altDocument);
			listLineDocs.add(lineDocument);

			for (Map.Entry<String, List<String>> tokenEntry : vcfb.getMapTokenLines().entrySet()) {
				if (tokenEntry.getKey().equals(entry.getKey())) {
					Document tokenDocument = new Document("ALT_RAW", tokenEntry.getValue());
					Document tokenlineDocument = new Document(tokenEntry.getKey(), tokenDocument);
					listLineTokenDocs.add(tokenlineDocument);
				}
			}

		}

		vcfDocument.put("GeneName", vcfb.getGeneName());

		vcfDocument.put("Lines", listLineDocs);
		vcfDocument.put("Lines_RAW", listLineTokenDocs);

	}

	public void insert(Vcf vcfb) {
		Document document = new Document();
		document.clear();
		document.put("_id", new BsonString(vcfb.getChromosome() + ":" + vcfb.getPosition()));
		document.put("Chromosome", vcfb.getChromosome());
		document.put("Position", vcfb.getPosition());
		document.put("ID", vcfb.getId());
		document.put("REF", vcfb.getRef());

		List<Document> listLineDocs = new ArrayList<>();
		List<Document> listLineTokenDocs = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry : vcfb.getMapAltLines().entrySet()) {
			Document altDocument = new Document("ALT", entry.getValue());
			Document lineDocument = new Document(entry.getKey(), altDocument);
			listLineDocs.add(lineDocument);

			for (Map.Entry<String, List<String>> tokenEntry : vcfb.getMapTokenLines().entrySet()) {
				if (tokenEntry.getKey().equals(entry.getKey())) {
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
				.append("Chromosome", vcfb.getChromosome()).append("Position", vcfb.getPosition())
				.append("ID", vcfb.getId()).append("REF", vcfb.getRef());

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
					.append("Chromosome", vcfb.getChromosome()).append("Position", vcfb.getPosition())
					.append("ID", vcfb.getId()).append("REF", vcfb.getRef());

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
			// System.out.println(doc.toJson());
		}
	}

	public void close() {
		mongoClient.close();
	}

	void insertOneByOne(List<Vcf> listOfVcfBeans) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	public MongoCollection<Document> getGeneCollection() {
		return geneCollection;
	}

	public void insert(Gene geneb) {

		Document geneDocument = new Document("_id", geneb.getGeneId()).append("chromosome", geneb.getChromosome())
				.append("start", geneb.getStartPosition()).append("end", geneb.getEndPosition());

		geneCollection.insertOne(geneDocument);

	}

	public void displayGenes() {
		for (Document doc : geneCollection.find()) {
			// System.out.println(doc.toJson());
		}
	}

	public void insert(List<Gene> beans, int count) {

		List<Document> listOfGeneDocument = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			Gene geneb = beans.get(i);

			Document geneDocument = new Document("_id", geneb.getGeneId()).append("chromosome", geneb.getChromosome())
					.append("start", geneb.getStartPosition()).append("end", geneb.getEndPosition());
			listOfGeneDocument.add(geneDocument);
		}
		geneCollection.insertMany(listOfGeneDocument);

	}

}
