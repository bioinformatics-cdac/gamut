/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.vcf;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.mongodb.cmd.MongodbDumpCommand;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ramki
 */
public class StoreVcfToMongoDb {

    public void submit(MongodbDumpCommand command, MongoDBLoader mongoDBLoader) {
        ExecutorService executorService = Executors.newFixedThreadPool(command.getProcessors());

        File basePathFile = new File(command.getPath());

        if (basePathFile.isDirectory()) {
            for (File vcfFile : basePathFile.listFiles()) {
                VcfParser parser = new VcfParser(vcfFile, mongoDBLoader);
                executorService.execute(parser);
            }
        } else {
            VcfParser parser = new VcfParser(basePathFile, mongoDBLoader);
            executorService.execute(parser);
        }

        executorService.shutdown();

        // mongoDBLoader.close();
    }
    
    
    public void submit(MongodbDumpCommand command, MongoDBLoader mongoDBLoader, int batchRecordCount) {
        ExecutorService executorService = Executors.newFixedThreadPool(command.getProcessors());

        File basePathFile = new File(command.getPath());

        if (basePathFile.isDirectory()) {
            for (File vcfFile : basePathFile.listFiles()) {
                VcfParser parser = new VcfParser(vcfFile, mongoDBLoader,batchRecordCount);
                executorService.execute(parser);
            }
        } else {
            VcfParser parser = new VcfParser(basePathFile, mongoDBLoader,batchRecordCount);
            executorService.execute(parser);
        }

        executorService.shutdown();

        // mongoDBLoader.close();
    }
}
