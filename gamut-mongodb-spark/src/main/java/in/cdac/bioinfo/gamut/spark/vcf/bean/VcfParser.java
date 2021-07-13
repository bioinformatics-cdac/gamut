/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.spark.vcf.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author sandeep
 */
public class VcfParser implements Serializable {

	List<String> listOfHeaders;
	Vcf vcfb = new Vcf();

	public VcfParser(List<String> listOfHeaders) {
		this.listOfHeaders = listOfHeaders;
	}

	public Vcf processVCFLine(String line) {
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
						vcfb.getMapAltLines().put(listOfHeaders.get(sampleIndex), listOfAlts);
					}
					if (listOfToken.size() > 0) {
//                System.out.println("Sample index : "+vcfb.getMapAltLines());
						vcfb.getMapTokenLines().put(listOfHeaders.get(sampleIndex), listOfToken);
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

}
