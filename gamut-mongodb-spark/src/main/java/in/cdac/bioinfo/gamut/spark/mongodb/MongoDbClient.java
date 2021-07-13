package in.cdac.bioinfo.gamut.spark.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import in.cdac.bioinfo.gamut.spark.cmd.MongoDBInfo;
import in.cdac.bioinfo.gamut.spark.vcf.bean.Vcf;

public class MongoDbClient {
	private static MongoClient mongoClient = null;
	private static MongoDatabase database;

	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection(MongoDBInfo mongoDBInfo) {
		if (mongoClient == null) {
			mongoClient = new MongoClient(new ServerAddress(mongoDBInfo.getHost(), mongoDBInfo.getPort()));

			database = mongoClient.getDatabase(mongoDBInfo.getDatabase());

			collection = database.getCollection(mongoDBInfo.getCollection());
			createIndices();

		}
		return collection;

	}
	
	
	 public static void createIndices() {
    	 collection.createIndex(Indexes.ascending("Chromosome", "Position"));
    	 collection.createIndex(Indexes.ascending("Position"));
    	 
     }

	public static void insertSampleNames(List<String> listofsamples, StringBuilder builder, MongoCollection<Document> localCollection ) {
		Vcf bean = new Vcf();
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
		System.out.println("in insert" + String.join(",", chromosomeList));
		Document document = new Document("_id", new BsonString(bean.getChromosome() + ":" + bean.getPosition()))
				.append("Chromosome", String.join(",", chromosomeList)).append("Position", null)
				.append("ID", bean.getId()).append("REF", bean.getRef()).append("Line", listString)
				.append("Header", builder.toString());

		// document.append("Line", Arrays.asList(listofsamples));

		localCollection.insertOne(document);
		System.out.println("inserted");
	}
	
	public static void insert(Vcf vcfb, MongoCollection<Document> localCollection) {

		Document document = new Document("_id", new BsonString(vcfb.getChromosome() + ":" + vcfb.getPosition()))
				.append("Chromosome", vcfb.getChromosome()).append("Position", vcfb.getPosition())
				.append("ID", vcfb.getId()).append("REF", vcfb.getRef());

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

		localCollection.insertOne(document);
	}
}
