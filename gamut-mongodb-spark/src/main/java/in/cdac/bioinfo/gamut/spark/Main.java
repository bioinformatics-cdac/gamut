package in.cdac.bioinfo.gamut.spark;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import com.beust.jcommander.JCommander;
import com.mongodb.client.MongoCollection;

import in.cdac.bioinfo.gamut.spark.cmd.MongodbDumpCommand;
import in.cdac.bioinfo.gamut.spark.mongodb.MongoDbClient;
import in.cdac.bioinfo.gamut.spark.mongodb.VcfInsertMapFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	private static SparkSession spark;

	public static void main(String[] args) {
		JCommander jCommander = new JCommander();

		MongodbDumpCommand store = new MongodbDumpCommand();
		jCommander.addCommand("store", store);

		try {
			jCommander.parse(args);

		} catch (Exception e) {
			log.error("Parsing error", e);
			jCommander.usage();

		}

		System.out.println("Args Length === : " + args.length);
		System.out.println(store);
		long earlyStartTime = System.currentTimeMillis();
//		Configuration configuration = new Configuration();

		SparkConf conf = new SparkConf();

		SparkSession.Builder builder = SparkSession.builder();

		builder = builder.master("local[*]");

		SparkSession spark = builder.appName("VCF Ingestion Module").config("spark.sql.shuffle.partitions", 15)
				.getOrCreate();
		Configuration configuration = spark.sparkContext().hadoopConfiguration();
		configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		configuration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		
		configuration.set("dfs.block.size", "128m");
		
		long startTime = System.currentTimeMillis();

		String inputPath = store.getPath();

		int numPartition = 20;
		if (store.getProcessors()!=1) {
			numPartition = store.getProcessors();
		}
			
		RDD<String> stringRDD = spark.sparkContext().textFile(inputPath, numPartition);

		Dataset<String> modelDataset = spark.createDataset(stringRDD, Encoders.STRING());

		Dataset<String> filter = modelDataset.filter(new FilterFunction<String>() {

			@Override
			public boolean call(String line) throws Exception {
				return !line.startsWith("##");
			}
		});

		String header = filter.head();
		List<String> headers = getSampleHeaders(header);

		StringBuilder sb = new StringBuilder();

		
		VcfInsertMapFunction vcfInsertMapFunction = new VcfInsertMapFunction(headers, store.getMongoDBInfo());

		Dataset<Integer> map = filter.map(vcfInsertMapFunction, Encoders.INT());

		long count = map.count();

		System.out.println(count);
		MongoCollection<Document> collection = MongoDbClient.getCollection(store.getMongoDBInfo());
		MongoDbClient.insertSampleNames(headers, sb, collection);

		long endTime = System.currentTimeMillis();

		long totalTime = (endTime - startTime) / 1000;

		System.out.println("Spark Time = " + totalTime + " seconds");
		System.out.println("Total End to End Time = " + (endTime - earlyStartTime) / 1000 + " seconds");

		System.exit(0);

	}

	public static List<String> getSampleHeaders(String line) {
		List<String> list = new ArrayList<>();
		Scanner scanner = new Scanner(line);
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();
		scanner.next();

		while (scanner.hasNext()) {
			list.add(scanner.next());

		}
		return list;
	}

}
