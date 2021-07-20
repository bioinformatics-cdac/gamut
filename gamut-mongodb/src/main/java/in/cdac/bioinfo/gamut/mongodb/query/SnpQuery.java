/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.mongodb.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpBean;
import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpStatisticsBean;

/**
 *
 * @author ramki
 */
public class SnpQuery {

	private MongoCollection<Document> collection;
	private MongoCollection<Document> geneCollection;

	private String tab = "\t";
	String nbsp = "&amp;nbsp";

	List<String> tempLeftList = new ArrayList<>();
	List<String> tempRightList = new ArrayList<>();
	List<String> tempLeftRawList = new ArrayList<>();
	List<String> tempRightRawList = new ArrayList<>();

	public SnpQuery(MongoDBLoader mongoDBLoader) {
		collection = mongoDBLoader.getCollection();
		geneCollection = mongoDBLoader.getGenecollection();

	}

	public List<OutputSnpBean> retriveVCFIdRecords(String Id, List<String> leftList, List<String> rightList) {
		Collections.sort(leftList);
		Collections.sort(rightList);

		Set<String> unionLineSet = new HashSet<>();

		unionLineSet.addAll(leftList);
		unionLineSet.addAll(rightList);

		Set<String> leftSet = new HashSet<>();
		Set<String> rightSet = new HashSet<>();

		Bson filter = Filters.eq("ID", Id);
		FindIterable<Document> result = this.collection.find(filter);

		List<OutputSnpBean> listOfVCFStrings = new ArrayList<>();

//		StringBuilder sb = new StringBuilder();
//		sb.append("Chromo").append(tab).append("Position").append(tab).append("dbSNPID").append(tab).append("REF")
//				.append(tab);
//		sb.append(String.join(",", leftList));
//		sb.append("#");
//
//		sb.append(String.join(",", rightList));

		for (Document document : result) {

			OutputSnpBean resultSNPBean = this.processDocument(document, leftList, rightList, unionLineSet, leftSet,
					rightSet);

			if (resultSNPBean != null) {
				listOfVCFStrings.add(resultSNPBean);
			}

		}
		return listOfVCFStrings;

	}

	public List<String> getHeaders() {
		Bson filter = Filters.eq("_id", "0.0:0");

		FindIterable<Document> result = this.collection.find(filter);

		Document doc = result.first();

		List<String> format = doc.getList("Headers", String.class);
		return format;
	}

	public Document getSampleDocument() {
		Bson filter = Filters.eq("_id", "0.0:0");

		FindIterable<Document> result = this.collection.find(filter);

		Document doc = result.first();

		// String list = doc.getString("Line");
		return doc;
	}

	public List<String> getSampleList() {
		Bson filter = Filters.eq("_id", "0.0:0");

		FindIterable<Document> result = this.collection.find(filter);

		Document doc = result.first();

		List<String> list = doc.getList("Samples", String.class);

		return list;
	}

	List<OutputSnpBean> listOfVCFStrings = new ArrayList<>();

	public List<OutputSnpBean> retriveGeneRecords(String geneId, List<String> leftList, List<String> rightList) {

		Collections.sort(leftList);
		Collections.sort(rightList);

		String gene = geneId;

		Set<String> unionLineSet = new HashSet<>();

		unionLineSet.addAll(leftList);
		unionLineSet.addAll(rightList);

		Set<String> leftSet = new HashSet<>();
		Set<String> rightSet = new HashSet<>();

		Bson filter = Filters.eq("_id", gene);

		FindIterable<Document> result = this.geneCollection.find(filter);
		listOfVCFStrings = new ArrayList<>();

		Document doc = result.first();
		if (doc == null) {
			setListStatisticBeans(null);
			return listOfVCFStrings;
		} else {
			String chromosome = doc.getString("chromosome");
			Long start = doc.getLong("start");
			Long end = doc.getLong("end");

			listOfVCFStrings = retriveVCFRecords(chromosome, start, end, leftList, rightList);

		}

		return listOfVCFStrings;
	}

	public List<String> retriveChromosomeRecords() {

		DistinctIterable<String> distinctChromosome = this.collection.distinct("Chromosome", String.class);
		List<String> chromosomeList = new ArrayList<String>();

		for (String chr : distinctChromosome) {
			String chrmosome = chr;
			if (chr != null) {
				chromosomeList.add(chrmosome);
			}
		}

		return chromosomeList;
	}

	public List<String> retriveGeneRecords(String chromosome, long position) {

		Bson filter = Filters.and(Filters.eq("chromosome", chromosome), Filters.lte("start", position),
				Filters.gte("end", position));
//		Bson filter = Filters.and(Filters.eq("chromosome", chromosome), Filters.lte("start", position),
//				Filters.gte("end", position));
		FindIterable<Document> result = this.geneCollection.find(filter);

		List<String> genenameList = new ArrayList<String>();

		for (Document document : result) {
			String id = document.getString("_id");
			genenameList.add(id);
		}

		if (genenameList.size() > 0) {

		} else {
			genenameList.add("");
		}

		return genenameList;
	}

	public List<OutputSnpBean> retriveVCFRecords(String chromosomeName, long position1, long position2,
			List<String> leftList, List<String> rightList) {
		Collections.sort(leftList);
		Collections.sort(rightList);

		long start;
		long end;
		maxcounter = 0;

		if (position1 > position2) {
			end = position1;
			start = position2;
		} else {
			start = position1;
			end = position2;
		}

		String chromosome = chromosomeName;

		Set<String> unionLineSet = new HashSet<>();

		unionLineSet.addAll(leftList);
		unionLineSet.addAll(rightList);

		Set<String> leftSet = new HashSet<>();
		Set<String> rightSet = new HashSet<>();

		Bson filter;

		if (chromosomeName.equals("ALL")) {
			filter = Filters.and(Filters.gte("Position", start), Filters.lte("Position", end));
		} else {
			filter = Filters.and(Filters.eq("Chromosome", chromosome), Filters.gte("Position", start),
					Filters.lte("Position", end));
		}

		System.out.println("Filter : " + filter.toString());
		long startTime = System.currentTimeMillis();

		FindIterable<Document> result = this.collection.find(filter);
		
		long endTime = System.currentTimeMillis();

		System.out.println("Query Execution taken : " + (endTime - startTime) + " ms");

		List<OutputSnpBean> listOfVCFStrings = new ArrayList<>();

		for (Document document : result) {

			OutputSnpBean resultSNPBean = this.processDocument(document, leftList, rightList, unionLineSet, leftSet,
					rightSet);

			if (resultSNPBean != null) {
//
//				List<String> retriveGeneRecords = retriveGeneRecords(resultSNPBean.getChromosome(),
//						resultSNPBean.getChromosome_Position());
//				resultSNPBean.setGeneList(retriveGeneRecords);

				listOfVCFStrings.add(resultSNPBean);

			}
		}

		if (counter > 0) {
			OutputSnpStatisticsBean nPStatisticBean = new OutputSnpStatisticsBean();
			nPStatisticBean.setChromosome(currentChrmosome);
			nPStatisticBean.setA_A(A_A);
			nPStatisticBean.setA_C(A_C);
			nPStatisticBean.setA_G(A_G);
			nPStatisticBean.setA_T(A_T);

			nPStatisticBean.setT_A(T_A);
			nPStatisticBean.setT_C(T_C);
			nPStatisticBean.setT_G(T_G);
			nPStatisticBean.setT_T(T_T);

			nPStatisticBean.setC_A(C_A);
			nPStatisticBean.setC_C(C_C);
			nPStatisticBean.setC_G(C_G);
			nPStatisticBean.setC_T(C_T);

			nPStatisticBean.setG_A(G_A);
			nPStatisticBean.setG_C(G_C);
			nPStatisticBean.setG_G(G_G);
			nPStatisticBean.setG_T(G_T);
			if (counter > maxcounter) {
				maxcounter = counter;
			}
			nPStatisticBean.setCounter(counter);

			nPStatisticBean.setListbean(oldbeanlist);

			nPStatisticBean.setMap1(oldmap);

			listStatisticBeans.add(nPStatisticBean);

			A_A = 0;
			A_T = 0;
			A_C = 0;
			A_G = 0;
			T_A = 0;
			T_T = 0;
			T_C = 0;
			T_G = 0;
			C_A = 0;
			C_T = 0;
			C_C = 0;
			C_G = 0;
			G_A = 0;
			G_T = 0;
			G_C = 0;
			G_G = 0;
			counter = 0;

			currentChrmosome = chromosome;
		}
		return listOfVCFStrings;

	}

	public static int getMaxcounter() {
		return maxcounter;
	}

	public static void setMaxcounter(int maxcounter) {
		SnpQuery.maxcounter = maxcounter;
	}

	int[][] matrix = new int[4][4];
	String currentChrmosome;
	Set<OutputSnpBean> setlist = new HashSet<>();
	List<OutputSnpBean> oldbeanlist = new ArrayList<OutputSnpBean>();

	int A_A = 0, A_T = 0, A_C = 0, A_G = 0, T_A = 0, T_T = 0, T_C = 0, T_G = 0, C_A = 0, C_T = 0, C_C = 0, C_G = 0,
			G_A = 0, G_T = 0, G_C = 0, G_G = 0;
	static int maxcounter = 0;

	List<OutputSnpStatisticsBean> listStatisticBeans = new ArrayList<>();
	int counter = 0;

	public List<OutputSnpStatisticsBean> getListStatisticBeans() {
		return listStatisticBeans;
	}

	public void setListStatisticBeans(List<OutputSnpStatisticsBean> listStatisticBeans) {
		this.listStatisticBeans = listStatisticBeans;
	}

	OutputSnpBean previousbean = new OutputSnpBean();
	Map<String, Set<OutputSnpBean>> oldmap = new LinkedHashMap<String, Set<OutputSnpBean>>();

	private OutputSnpBean processDocument(Document document, List<String> leftList, List<String> rightList,
			Set<String> unionLineSet, Set<String> leftSet, Set<String> rightSet) {

		String chromosome = document.getString("Chromosome");
		long position = document.getInteger("Position");
		String id = document.getString("ID");
		String ref = document.getString("REF");
		List<String> leftAltRawSet = new ArrayList<>();
		List<String> rightAltRawSet = new ArrayList<>();

		OutputSnpBean bean = new OutputSnpBean();

		if (currentChrmosome != null) {
			if (!currentChrmosome.equals(chromosome)) {
				if (counter > 0) {
					OutputSnpStatisticsBean nPStatisticBean = new OutputSnpStatisticsBean();

					nPStatisticBean.setChromosome(currentChrmosome);
					nPStatisticBean.setA_A(A_A);
					nPStatisticBean.setA_C(A_C);
					nPStatisticBean.setA_G(A_G);
					nPStatisticBean.setA_T(A_T);

					nPStatisticBean.setT_A(T_A);
					nPStatisticBean.setT_C(T_C);
					nPStatisticBean.setT_G(T_G);
					nPStatisticBean.setT_T(T_T);

					nPStatisticBean.setC_A(C_A);
					nPStatisticBean.setC_C(C_C);
					nPStatisticBean.setC_G(C_G);
					nPStatisticBean.setC_T(C_T);

					nPStatisticBean.setG_A(G_A);
					nPStatisticBean.setG_C(G_C);
					nPStatisticBean.setG_G(G_G);
					nPStatisticBean.setG_T(G_T);

					if (counter > maxcounter) {
						maxcounter = counter;
					}
					nPStatisticBean.setCounter(counter);
					nPStatisticBean.setPosition(position);
					nPStatisticBean.setListbean(oldbeanlist);
					nPStatisticBean.getMap1().putAll(oldmap);

					listStatisticBeans.add(nPStatisticBean);
				}

				A_A = 0;
				A_T = 0;
				A_C = 0;
				A_G = 0;
				T_A = 0;
				T_T = 0;
				T_C = 0;
				T_G = 0;
				C_A = 0;
				C_T = 0;
				C_C = 0;
				C_G = 0;
				G_A = 0;
				G_T = 0;
				G_C = 0;
				G_G = 0;
				counter = 0;
				oldbeanlist = new ArrayList<>();
				setlist = new HashSet<>();
				oldmap = new LinkedHashMap<String, Set<OutputSnpBean>>();
				currentChrmosome = chromosome;

			} else {
			}
		} else {
			currentChrmosome = chromosome;
		}

		bean.setChromosome(chromosome);
		bean.setChromosome_Position(position);
		bean.setRecordID(id);
		bean.setRef(ref);

		leftSet.clear();
		rightSet.clear();
		List<Document> linesDocuments = (List<Document>) document.get("Lines");

		List<Document> lines_rawDocuments = (List<Document>) document.get("Lines_RAW");

		List<String> genename = (List<String>) document.get("GeneName");
		bean.setGeneList(genename);
		for (String linesString : leftList) {
			boolean exist = false;
			boolean exist1 = false;

			for (Document lineDocument : linesDocuments) {
				Document altDocument = (Document) lineDocument.get(linesString);
				if (altDocument != null) {
					exist = true;
					leftSet.addAll((List<String>) altDocument.get("ALT"));
					break;
				}
			}
			for (Document lineDocument : lines_rawDocuments) {
				Document altrawDocument = (Document) lineDocument.get(linesString);
				if (altrawDocument != null) {
					exist1 = true;
					leftAltRawSet.addAll((List<String>) altrawDocument.get("ALT_RAW"));
					break;
				}
			}
			if (exist == false) {
				leftSet.add(ref);
			}

			if (exist1 == false) {
			}
		}

		for (String linesString : rightList) {
			boolean exist = false;
			boolean exist1 = false;

			for (Document lineDocument : linesDocuments) {

				Document altDocument = (Document) lineDocument.get(linesString.trim());
				if (altDocument != null) {
					exist = true;
					rightSet.addAll((List<String>) altDocument.get("ALT"));
					break;
				}
			}
			for (Document lineDocument : lines_rawDocuments) {
				Document altrawDocument = (Document) lineDocument.get(linesString.trim());
				if (altrawDocument != null) {
					exist1 = true;
					rightAltRawSet.addAll((List<String>) altrawDocument.get("ALT_RAW"));
					break;
				}
			}
			if (exist == false) {
				rightSet.add(ref);
			}
			if (exist1 == false) {
			}
		}

		boolean dnaExists = false;
		for (String dnaChar : leftSet) {
			if (rightSet.contains(dnaChar)) {
				dnaExists = true;
				break;
			} else {

			}

		}
		if (!dnaExists) {

			if (currentChrmosome.equals(chromosome)) {
				counter++;
			}

			Map<String, String> map = new HashMap<>();
			StringBuilder right = new StringBuilder();

			OutputSnpBean pe = new OutputSnpBean();

			pe.setChromosome(chromosome);
			pe.setChromosome_Position(position);
			pe.setRecordID(id);
			pe.setRef(ref);

			pe.setGeneList(retriveGeneRecords(chromosome, position));

			for (Document lineDocument : linesDocuments) {
				Set lineSet = lineDocument.keySet();
				String lineNameString = (String) lineSet.iterator().next();

				boolean containsLinesString = unionLineSet.contains(lineNameString);

				if (containsLinesString) {
					Document altDocument = (Document) lineDocument.get(lineNameString);
					List<String> altStringList = (List<String>) altDocument.get("ALT");
					String altString = String.join(",", altStringList);
					map.put(lineNameString, altString);
				}

			}

			tempLeftList.clear();
			tempRightList.clear();

			tempLeftRawList.clear();
			tempRightRawList.clear();

			for (String key : leftList) {
				String value = map.get(key);
				if (value == null) {
					value = ref;
				}
				if (ref.concat("/").concat(value).equals("A/A")) {
					A_A++;
					Set<OutputSnpBean> set = oldmap.get("A/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/A", set);
				}
				if (ref.concat("/").concat(value).equals("A/T")) {
					A_T++;
					Set<OutputSnpBean> set = oldmap.get("A/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/T", set);
				}
				if (ref.concat("/").concat(value).equals("A/C")) {
					A_C++;
					Set<OutputSnpBean> set = oldmap.get("A/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/C", set);
				}
				if (ref.concat("/").concat(value).equals("A/G")) {
					A_G++;
					Set<OutputSnpBean> set = oldmap.get("A/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/G", set);
				}
				if (ref.concat("/").concat(value).equals("T/A")) {
					T_A++;
					Set<OutputSnpBean> set = oldmap.get("T/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/A", set);
				}
				if (ref.concat("/").concat(value).equals("T/T")) {
					T_T++;
					Set<OutputSnpBean> set = oldmap.get("T/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/T", set);
				}
				if (ref.concat("/").concat(value).equals("T/C")) {
					T_C++;
					Set<OutputSnpBean> set = oldmap.get("T/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/C", set);
				}
				if (ref.concat("/").concat(value).equals("T/G")) {
					T_G++;
					Set<OutputSnpBean> set = oldmap.get("T/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/G", set);
				}
				if (ref.concat("/").concat(value).equals("C/A")) {
					C_A++;
					Set<OutputSnpBean> set = oldmap.get("C/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/A", set);
				}
				if (ref.concat("/").concat(value).equals("C/T")) {
					C_T++;
					Set<OutputSnpBean> set = oldmap.get("C/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/T", set);
				}
				if (ref.concat("/").concat(value).equals("C/C")) {
					C_C++;
					Set<OutputSnpBean> set = oldmap.get("C/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/C", set);
				}
				if (ref.concat("/").concat(value).equals("C/G")) {
					C_G++;
					Set<OutputSnpBean> set = oldmap.get("C/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/G", set);
				}
				if (ref.concat("/").concat(value).equals("G/A")) {
					G_A++;
					Set<OutputSnpBean> set = oldmap.get("G/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/A", set);
				}
				if (ref.concat("/").concat(value).equals("G/T")) {
					G_T++;
					Set<OutputSnpBean> set = oldmap.get("G/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/T", set);
				}
				if (ref.concat("/").concat(value).equals("G/C")) {
					G_C++;
					Set<OutputSnpBean> set = oldmap.get("G/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/C", set);
				}
				if (ref.concat("/").concat(value).equals("G/G")) {
					G_G++;
					Set<OutputSnpBean> set = oldmap.get("G/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/G", set);
				}

				tempLeftList.add(value);
			}
			bean.setSetOne(String.join(",", tempLeftList));
			bean.setSetOneRaw(String.join(",", leftAltRawSet));

			pe.setSetOne(String.join(",", tempLeftList));

			for (String key : rightList) {
				String value = map.get(key);
				if (value == null) {
					value = ref;
				}

				if (ref.concat("/").concat(value).equals("A/A")) {
					A_A++;
					Set<OutputSnpBean> set = oldmap.get("A/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/A", set);
				}
				if (ref.concat("/").concat(value).equals("A/T")) {
					A_T++;
					Set<OutputSnpBean> set = oldmap.get("A/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/T", set);
				}
				if (ref.concat("/").concat(value).equals("A/C")) {
					A_C++;
					Set<OutputSnpBean> set = oldmap.get("A/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/C", set);
				}
				if (ref.concat("/").concat(value).equals("A/G")) {
					A_G++;
					Set<OutputSnpBean> set = oldmap.get("A/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("A/G", set);
				}
				if (ref.concat("/").concat(value).equals("T/A")) {
					T_A++;
					Set<OutputSnpBean> set = oldmap.get("T/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/A", set);
				}
				if (ref.concat("/").concat(value).equals("T/T")) {
					T_T++;
					Set<OutputSnpBean> set = oldmap.get("T/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/T", set);
				}
				if (ref.concat("/").concat(value).equals("T/C")) {
					T_C++;
					Set<OutputSnpBean> set = oldmap.get("T/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/C", set);
				}
				if (ref.concat("/").concat(value).equals("T/G")) {
					T_G++;
					Set<OutputSnpBean> set = oldmap.get("T/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("T/G", set);
				}
				if (ref.concat("/").concat(value).equals("C/A")) {
					C_A++;
					Set<OutputSnpBean> set = oldmap.get("C/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/A", set);
				}
				if (ref.concat("/").concat(value).equals("C/T")) {
					C_T++;
					Set<OutputSnpBean> set = oldmap.get("C/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/T", set);
				}
				if (ref.concat("/").concat(value).equals("C/C")) {
					C_C++;
					Set<OutputSnpBean> set = oldmap.get("C/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/C", set);
				}
				if (ref.concat("/").concat(value).equals("C/G")) {
					C_G++;
					Set<OutputSnpBean> set = oldmap.get("C/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("C/G", set);
				}
				if (ref.concat("/").concat(value).equals("G/A")) {
					G_A++;
					Set<OutputSnpBean> set = oldmap.get("G/A");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/A", set);
				}
				if (ref.concat("/").concat(value).equals("G/T")) {
					G_T++;
					Set<OutputSnpBean> set = oldmap.get("G/T");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/T", set);
				}
				if (ref.concat("/").concat(value).equals("G/C")) {
					G_C++;
					Set<OutputSnpBean> set = oldmap.get("G/C");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/C", set);
				}
				if (ref.concat("/").concat(value).equals("G/G")) {
					G_G++;
					Set<OutputSnpBean> set = oldmap.get("G/G");
					if (set == null) {
						set = new HashSet<>();
					}
					set.add(pe);
					oldmap.put("G/G", set);
				}

				tempRightList.add(value);
			}

			bean.setSetTwo(String.join(",", tempRightList));

			bean.setSetTwoRaw(String.join(",", rightAltRawSet));

			pe.setSetTwo(String.join(",", tempRightList));

			return bean;

		} else {
		}
		return null;
	}

	public void storeInVCF() {

	}

	public static void main(String[] args) {
		MongoDBLoader mongoDBLoader = new MongoDBLoader();
		mongoDBLoader.init("biograph", 27017, "pcsnp1", "chicken", "", null);
		SnpQuery main = new SnpQuery(mongoDBLoader);

		List<String> leftList = new ArrayList<>();
		List<String> rightList = new ArrayList<>();

		leftList.add("Line15");

		rightList.add("Line7");

		long startTime = System.currentTimeMillis();
		List<OutputSnpBean> retriveVCFRecords = main.retriveVCFRecords("6", 18882796, 18931965, leftList, rightList);
		long endTime = System.currentTimeMillis();

		for (OutputSnpBean retriveVCFRecord : retriveVCFRecords) {
		}

	}

}
