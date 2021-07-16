/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.vcf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.vcf.bean.Gene;

/**
 *
 * @author renu
 */
public class GeneParser implements Runnable{

    private static String HEADER_MARKER = "#";
    private static int RECORD_COUNT = 5000;
    private MongoDBLoader mongoDBLoader;
    private File geneFile;
    private MongoDbDAO mongoDbDAO;
    
    public GeneParser(File geneFile, MongoDBLoader mongoDBLoader) {
        this.geneFile = geneFile;
        this.mongoDBLoader = mongoDBLoader; 
        mongoDbDAO = new MongoDbDAO(mongoDBLoader);
    }
   
     @Override
    public void run() {

        try {
            parse();
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(VcfParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void parse() throws IOException {

        FileReader genefr = null;
        BufferedReader geneReader = null;
        try {
            genefr = new FileReader(geneFile);
            geneReader = new BufferedReader(genefr);
            String geneLine;
            int counter = 0;

            List<Gene> geneBeans = new ArrayList<>();
          
            int index = 0;
            long start = System.currentTimeMillis();
            long localEnd, localStart;
            localStart = start;
            
            while ((geneLine = geneReader.readLine()) != null) {
                if (!geneLine.startsWith(HEADER_MARKER)) {

                    String[] geneFileds = geneLine.split("\t");
                    String geneExonCDS = geneFileds[2].trim();

                    if (!"gene".equals(geneExonCDS)) {
                        continue;
                    }

                    String chromosome = geneFileds[0];
                    if (".1|".equalsIgnoreCase(chromosome)) {
                        chromosome = "MT";
                    }

                    if (!isValidChromosome(chromosome)) {
                        continue;
                    }

                    String startPositionString = geneFileds[3].trim();
                    String endPositionString = geneFileds[4].trim();
                    long startPosition = Long.parseLong(startPositionString);
                    long endPosition = Long.parseLong(endPositionString);

                    String geneRecord = geneFileds[8];
                    String geneId = extractGeneId(geneRecord);

                    Gene geneb=new Gene();
                    counter++;
                  
                    index = index + 1;

                    geneb.setGeneId(geneId);
                    geneb.setChromosome(chromosome);
                    geneb.setStartPosition(startPosition);
                    geneb.setEndPosition(endPosition);

                    geneBeans.add(geneb);
                    
                    if(geneBeans.size()==2000){
                        storeGeneBean(geneBeans, geneBeans.size());
                        geneBeans.clear();
                        System.out.println("List cleared after "+geneBeans.size());
                    }
                    //else{
//                        continue;
//                    }
//                        
                    
//                    if ((index) % RECORD_COUNT == 0) {
//
//                        storeGeneBean(geneBeans, index);
//
//                        localEnd = System.currentTimeMillis();
//                        //System.out.println("File Name : " + geneFile.getName() + "\tCounter : " + counter + "\t" + (localEnd - localStart) + " MilliSeconds");
//                        localStart = System.currentTimeMillis();
//
//                        for (int i = 0; i < RECORD_COUNT; i++) {
//                            geneBeans.get(i).reset();
//                        }
//
//                        index = 0;
//                    }

                }
            }

            storeGeneBean(geneBeans, geneBeans.size());
//            for (int i = 0; i < RECORD_COUNT; i++) {
//                geneBeans.get(i).reset();
//            }
            long end = System.currentTimeMillis();
            System.out.println("Total Time " + (end - start) / 1000 + " in Seconds");
            // System.out.println("File Name : " + geneFile.getName());
            // System.out.println("Counter : " + counter);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneParser.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } 
    }

    private void storeGeneBean(List<Gene> beans, int count) {

        mongoDbDAO.insert(beans, beans.size());
        //mongoDBLoader.insert(beans, count);
       // System.out.println("Count=" + count + "\t" + "GeneCount=" + beans.size());
    }

    private String extractGeneId(String geneRecord) {

        String geneId = null;
        //gene_id "ENSGALG00000009771"; gene_source "ensembl"; gene_biotype "protein_coding"
        String[] records = geneRecord.split(";");

        String targetRecord = records[0];
        geneId = targetRecord.substring(targetRecord.indexOf("\"") + 1, (targetRecord.lastIndexOf("\"")));
        return geneId;
    }

    private boolean isValidChromosome(String chromosome) {
        boolean valid = false;

// chromosome for all string based parameter
        //if (chromosome.equals("MT") || chromosome.equals("W") || chromosome.equals("Z") || chromosome.equals("Chromosome")) {
        if (true) {    
            valid = true;
        } else {

            try {
                int chNumber = Integer.parseInt(chromosome);
                if (chNumber >= 1 && chNumber <= 32) {
                    valid = true;
                } else {
                    valid = false;
                }
            } catch (NumberFormatException nfe) {
                valid = false;
            }

        }
        return valid;
    }

    public static void main(String[] args) throws IOException {
//        String test = "1	protein_coding	gene	1735	16308	.	+	.	gene_id \"ENSGALG00000009771\"; gene_source \"ensembl\"; gene_biotype \"protein_coding\"";
//
//        String[] sa = test.split("\t");
//        for (String s : sa) {
//            System.out.println(s);
//        }

        //String geneId = extractGeneId(sa[8]);
        //System.out.println(geneId);
        String geneFilePath = "/home/sandeep/Desktop/Gallus_gallus.Galgal4.76.gtf";
        File geneFile = new File(geneFilePath);
        GeneParser geneParser = new GeneParser(geneFile, null);
        geneParser.parse();

    }

}
