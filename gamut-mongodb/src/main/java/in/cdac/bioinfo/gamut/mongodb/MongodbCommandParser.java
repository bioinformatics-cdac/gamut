/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.mongodb;

import java.util.List;

import com.beust.jcommander.JCommander;

import in.cdac.bioinfo.gamut.mongodb.cmd.MongoDBInfo;
import in.cdac.bioinfo.gamut.mongodb.cmd.MongodbDumpCommand;
import in.cdac.bioinfo.gamut.mongodb.cmd.MongodbQueryCommand;
import in.cdac.bioinfo.gamut.mongodb.query.SnpQuery;
import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpBean;
import in.cdac.bioinfo.gamut.vcf.StoreVcfToMongoDb;

/**
 *
 * @author ramki
 */
public class MongodbCommandParser {

	public static void main(String[] args) throws InterruptedException {
       
      

        MongodbDumpCommand store = new MongodbDumpCommand();
        MongodbQueryCommand query = new MongodbQueryCommand();
        
        JCommander jc = JCommander.newBuilder()
        		.addCommand(MongodbCommand.STORE_COMMAND, store)
        		.addCommand(MongodbCommand.QUERY_COMMAND, query).build();

        try {

            jc.parse(args);


//            jc.parse(args);
//              jc.parse("store", "-i", "/home/", "-e");
//              
              jc.parse("query","-h","ramki","-d","gamut","--collection","v_1GB","-ch","chr1","-s","450703","-e","451697","-left","HG03072","-right","NA19755");
              
              
              System.out.println("Chromw : "+query.getChromosome());
              //System.out.println("Chromw : "+query.());
              

            if (MongodbCommand.STORE_COMMAND.equals(jc.getParsedCommand())) {
                MongoDBLoader mongoDBLoader = getMongoDBLoader(store.getMongoDBInfo());

                StoreVcfToMongoDb parserMain = new StoreVcfToMongoDb();
                parserMain.submit(store, mongoDBLoader);

            } else if (MongodbCommand.QUERY_COMMAND.equals(jc.getParsedCommand())) {
                MongoDBLoader mongoDBLoader = getMongoDBLoader(store.getMongoDBInfo());
                SnpQuery chickenQuery = new SnpQuery(mongoDBLoader);

                List<String> leftList = query.getLeft();
                List<String> rightList = query.getRight();

   
                long startTime = System.currentTimeMillis();
                List<OutputSnpBean> retriveVCFRecords = chickenQuery.retriveVCFRecords(query.getChromosome(), query.getStart(), query.getEnd(), leftList, rightList);
                for (OutputSnpBean retriveVCFRecord : retriveVCFRecords) {
//                    System.out.println(retriveVCFRecord);
                }
//                System.out.println(retriveVCFRecords.size());
                long endTime = System.currentTimeMillis();

                System.out.println("Time taken : " + (endTime - startTime) + " ms");

            }
        } catch (Exception e) {
          System.err.println(e.getMessage());

            jc.usage();
        }

    }


	private static MongoDBLoader getMongoDBLoader(MongoDBInfo mongoDBInfo) {
		System.out.println("MongoDB Database Host   : " + mongoDBInfo.getHost());
		System.out.println("MongoDB Database Port   : " + mongoDBInfo.getPort());
		System.out.println("MongoDB Database Name   : " + mongoDBInfo.getDatabase());
		System.out.println("MongoDB Collection Name : " + mongoDBInfo.getCollection() + "\n\n");

		MongoDBLoader mongoDBLoader = new MongoDBLoader();
		mongoDBLoader.init(mongoDBInfo.getHost(), mongoDBInfo.getPort(), mongoDBInfo.getDatabase(),
				mongoDBInfo.getCollection(), mongoDBInfo.getGenecollection(), null);
		return mongoDBLoader;
	}
}
