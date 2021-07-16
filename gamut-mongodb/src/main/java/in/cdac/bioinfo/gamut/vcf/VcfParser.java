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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.vcf.bean.Vcf;

/**
 *
 * @author sandeep
 */
public class VcfParser implements Runnable {

	public static List<String> listOfSampleNames = new ArrayList<>();

	private static String HEADER_MARKER = "#";
	private static int BATCH_RECORD_COUNT = 1000;
	private MongoDBLoader mongoDBLoader;
	private File vcfFile;
	private MongoDbDAO mongoDbDAO;

	public VcfParser(File vcfFile, MongoDBLoader mongoDBLoader) {
		this.vcfFile = vcfFile;
		this.mongoDBLoader = mongoDBLoader;
		mongoDbDAO = new MongoDbDAO(mongoDBLoader, BATCH_RECORD_COUNT);
	}

	public VcfParser(File vcfFile, MongoDBLoader mongoDBLoader, int batchRecordCount) {
		this.vcfFile = vcfFile;
		this.mongoDBLoader = mongoDBLoader;
		mongoDbDAO = new MongoDbDAO(mongoDBLoader, batchRecordCount);
	}

	@Override
	public void run() {

		try {
			processVCFFile(vcfFile);
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(VcfParser.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void processVCFLine(String line) {
		Vcf vcfb = new Vcf();
		this.processVCFLine(line, vcfb);
	}

	private void processVCFLine(String line, Vcf vcfb) {

		try {

			Scanner scanner = new Scanner(line);
//  if(line.indexOf("1|")>=0 || line.indexOf("|1")>=0) 
//  System.out.println("ok");
			vcfb.setChromosome(scanner.next());
			vcfb.setPosition(scanner.nextInt());
			vcfb.setId(scanner.next());

			vcfb.setRef(scanner.next());
			vcfb.setLineToProcess(line);

			String alts = scanner.next();
			String[] altSplit = alts.split(",");

			scanner.next(); // QUAL
			scanner.next(); // FILTER
			scanner.next(); // INFO
			scanner.next(); // FORMAT

			List<String> listOfAlts = new ArrayList<>();
			List<String> listOfToken = new ArrayList<>();

			if (!line.contains("*")) {

				int sampleIndex = -1;
				while (scanner.hasNext()) {
					listOfAlts = new ArrayList<>();
					listOfToken = new ArrayList<>();
					sampleIndex++;

					int key = -1;
					String token = scanner.next(); // 0|0 or 1

					// System.out.println("---96---"+token);
					if (token.contains(":")) {

						int idx = token.indexOf(':');
						int l = token.length();

						StringBuilder sb = new StringBuilder(token);
						sb.replace(idx, l, "|0");

						token = sb.toString();

					}

					if (token.indexOf('|') > 0) { // 0|0 format
						// System.out.println("--" + token);
						if (token.contains(".")) {

							int idx = token.indexOf('.');

							StringBuilder sb = new StringBuilder(token);
							sb.replace(idx, idx + 1, "0");
							token = sb.toString();
							// token = token.replace(".","0");
						}

						String[] split = token.split("|");

						if (token.indexOf('0') >= 0) { // 0/0 , 1/0, 0/1 formats

							if (split[0].equals(split[2])) {
								continue;
							}
							if (split[0].equals("0")) {
								key = Integer.parseInt(split[2]);
							} else if (split[2].equals("0")) {
								key = Integer.parseInt(split[0]);
							}
							if (key > 0) {
								listOfAlts.add(altSplit[key - 1]);
								listOfToken.add(token);
								// System.out.println("Token -:"+token);
							}

						} else { // two non 0 elements like 1/2 1/1

							if (split[0].equals(split[2])) { // 1/1 2/2 3/3
								key = Integer.parseInt(split[0]);
								listOfAlts.add(altSplit[key - 1]);
								listOfToken.add(token);
								// System.out.println("Token --:"+token);
							} else { // remianing like 1|2 3|2
								// System.out.println(token+"---"+split[0]+" "+split[2]);

								key = Integer.parseInt(split[0]);
								listOfAlts.add(altSplit[key - 1]);
								listOfToken.add(token);
								// System.out.println("Token ---:"+token);
								key = Integer.parseInt(split[2]);
								listOfAlts.add(altSplit[key - 1]);
								listOfToken.add(token);
								// System.out.println("Token ----:"+token);

							}
						}

					} else { // 1 format
						// System.out.println("------" + token);
						key = Integer.parseInt(token);
						if (key == 0) {
							continue;
						}

						try {

							listOfAlts.add(altSplit[key - 1]);
							listOfToken.add(token);
							// System.out.println("Token -----:"+token);

						} catch (Throwable throwable) {
							System.out.println(
									"Exception : " + throwable.getMessage() + " " + throwable.toString() + " " + line);
							System.out.println(token + " " + key + " " + altSplit[0] + "  " + altSplit.length);
							throwable.printStackTrace();

						}
					}

					if (listOfAlts.size() > 0) {
						// System.out.println("Sample index : "+vcfb.getMapAltLines());
						vcfb.getMapAltLines().put(listOfSampleNames.get(sampleIndex), listOfAlts);
					}
					if (listOfToken.size() > 0) {
//                System.out.println("Sample index : "+vcfb.getMapAltLines());
						vcfb.getMapTokenLines().put(listOfSampleNames.get(sampleIndex), listOfToken);
					}
					// System.out.println("Bean : "+vcfb);

				}
			} else {
				System.out.println("" + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("" + line);
		}

	}

	private void processVCFFile(File vcfFile) throws FileNotFoundException, IOException {

		FileReader fileReader = new FileReader(vcfFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		List<String> vcfHeaders = new ArrayList<String>();

		String line = null;

		long start, end;
		start = System.currentTimeMillis();

		boolean flag = true;

		List<Vcf> vcfBeans = new ArrayList<>(BATCH_RECORD_COUNT);

		for (int i = 0; i < BATCH_RECORD_COUNT; i++) {
			Vcf vcfb = new Vcf();
			vcfBeans.add(vcfb);
		}
		int index = 0;
		long recordsInserted = 0;
		start = System.currentTimeMillis();
		long processTime = 0;
		long totalTimeForInsertion = 0;
		while ((line = bufferedReader.readLine()) != null) {
			try {

				if (line.startsWith("#CHROM")) {
					flag = false;
					processHeader(line);
					continue;
				} else {
					if (flag) {
						vcfHeaders.add(line);
					}

					if (line.length() > 0 && line.startsWith("#")) {
						continue;
					}
				}
				if (!line.contains("*")) {
					Vcf vcfb = vcfBeans.get(index);
					vcfb.clear();

					processVCFLine(line, vcfb);
					if (vcfb.getMapAltLines().size() == 0)
						continue;
					
					index++;

				}
				if ((index) % BATCH_RECORD_COUNT == 0) {

					mongoDbDAO.insertOptimized(vcfBeans, index);
					end = System.currentTimeMillis();
					processTime = end - start;
					totalTimeForInsertion = totalTimeForInsertion + processTime;
					recordsInserted = recordsInserted + index;
					System.out.println("Time for inserting " + index + " records is = " + processTime + " ms "
							+ "\n Total Records Inserted Till Now : " + recordsInserted);

					start = System.currentTimeMillis();
					index = 0;

				}

			} catch (Throwable throwable) {
//				System.out.println(
//						"Exception Down : " + throwable.getMessage() + " " + throwable.toString() + " " + line);
				throwable.printStackTrace();
				throw throwable;
			}
		}

		if (index > 0) {
			mongoDbDAO.insertOptimized(vcfBeans, index);
			end = System.currentTimeMillis();
			processTime = end - start;
			totalTimeForInsertion = totalTimeForInsertion + processTime;
			recordsInserted = recordsInserted + index;
			System.out.println("Time for inserting  " + index + "records  is = " + processTime + " ms "
					+ "\n Total Records Inserted Till Now : " + recordsInserted);

		}
		System.out.println("Inserting records is completed, total records inserted = " + recordsInserted);
		System.out.println("Total Time for Inserting = " + totalTimeForInsertion);

		mongoDbDAO.insertSampleNames(listOfSampleNames, vcfHeaders);
		System.out.println("Creating index started ...");
		start = System.currentTimeMillis();
		mongoDbDAO.createIndices();
		end = System.currentTimeMillis();
		processTime = end - start;
		System.out.println("Time for creating index is =  " + processTime);
		System.out.println("Creating index completed successfully ...");

		bufferedReader.close();

	}

	private void processHeader(String line) {
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
			listOfSampleNames.add(scanner.next());
		}
		// System.out.println(listOfSampleNames.size());
	}

}
