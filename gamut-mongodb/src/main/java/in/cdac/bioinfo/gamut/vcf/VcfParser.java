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
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.model.Indexes;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.vcf.bean.Vcf;

/**
 *
 * @author sandeep
 */
public class VcfParser implements Runnable {

	public static List<String> listOfSampleNames = new ArrayList<>();

	private static String HEADER_MARKER = "#";
	private static int RECORD_COUNT = 5000;
	private MongoDBLoader mongoDBLoader;
	private File vcfFile;
	private MongoDbDAO mongoDbDAO;

	public VcfParser(File vcfFile, MongoDBLoader mongoDBLoader) {
		this.vcfFile = vcfFile;
		this.mongoDBLoader = mongoDBLoader;
		mongoDbDAO = new MongoDbDAO(mongoDBLoader);
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

	
	private Vcf processVCFLine(String line) {
		// System.out.println(line);
		Vcf vcfb = new Vcf();

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
			// System.out.println(vcfb);
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

		return vcfb;
	}

	private void processVCFFile(File vcfFile) throws FileNotFoundException, IOException {

		FileReader fileReader = new FileReader(vcfFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuilder sb = new StringBuilder();

		int totalLineprocessed = 0;
		int totalDocsAdded = 0;

		String line = null;
		int count = 0;
		long start, end;
		start = System.currentTimeMillis();
		int linesDuplicate = 0;
		boolean flag = true;

		List<Vcf> listOfVcfBeans = new ArrayList<>();
		while ((line = bufferedReader.readLine()) != null) {
			try {
				totalLineprocessed++;
				if (line.startsWith("#CHROM")) {
					flag = false;
					processHeader(line);
					continue;
				} else {
					if (flag) {
						sb.append(line + "\n");
					}

					if (line.length() > 0 && line.startsWith("#")) {
						continue;
					}
				}
				if (!line.contains("*")) {
					Vcf vcfb = processVCFLine(line);

//                SNPChickenQuery snpcq = new SNPChickenQuery(mongoDBLoader);
//                List ls=snpcq.retriveGeneRecords(vcfb.getChromosome(),vcfb.getPosition());
					// System.out.println("Gene Size : "+ls.size());
//                vcfb.setGeneName(ls);
					if (vcfb.getMapAltLines().size() > 0) {
						// System.out.println(vcfb);
						// System.out.println(vcfb.getLineToProcess());
//                    listOfVcfBeans.add(vcfb);
//
//                    // mongoDbDAO.insert(listOfVcfBeans);
//                    totalDocsAdded++;
//                    if (totalDocsAdded % 1000 == 0) {
//                        //   try {
//                        //      mongoDbDAO.insert(listOfVcfBeans);
//                        // } catch (Throwable throwable) {
//                        //   System.out.println("ERROR : " + totalLineprocessed + " \t " + throwable.getMessage());
//                        for (VCFBean vCFBean : listOfVcfBeans) {
						try {
							// System.out.println("in INSERT");
							mongoDbDAO.insert(vcfb);
						} catch (Throwable throwable1) {
							linesDuplicate++;
//                        System.out.println("ERROR : " + totalLineprocessed + " \t " + throwable1.getMessage() + "\t " + vCFBean.toString());
							System.out.println("LINE_DUPLICATE=>" + vcfb.getLineToProcess());
						}

						count++;
						if (count % 100000 == 0) {

							end = System.currentTimeMillis();
							System.out.println();
							System.out.println(new Date() + " :\t" + totalDocsAdded + "\t"
									+ count / ((end - start) / 1000) + " Docs/Sec");
							count = 0;
							start = System.currentTimeMillis();
						}
					}
				} else {
					// System.out.println("" + line);
					continue;
				}

			} catch (Throwable throwable) {
				System.out.println(
						"Exception Down : " + throwable.getMessage() + " " + throwable.toString() + " " + line);
				throwable.printStackTrace();
			}
		}

		System.out.println("0.0:0sample add---");
		mongoDbDAO.insertSampleNames(listOfSampleNames, sb);
		mongoDbDAO.createIndices();
		System.out.println("totalDocsAdded : " + totalDocsAdded);
		System.out.println("totalLineprocessed : " + totalLineprocessed);
		System.out.println("totalDuplicate : " + linesDuplicate);

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
