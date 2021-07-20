/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.web.snp;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.bson.Document;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.mongodb.query.SnpQuery;
import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpBean;
import in.cdac.bioinfo.gamut.snpwebapp.OutputSnpStatisticsBean;
import in.cdac.bioinfo.gamut.web.config.ConfigurationBean;

/**
 *
 * @author renu
 */
@Named(value = "snpHomeBean")
@SessionScoped
public class SNPHomeBean implements Serializable {

	private String searchOption;
	private boolean value1;
	private boolean advFlag = false;
	private boolean byGeneSearch = false;

	private String searchById;
	private String options = "1000Genome";
	private String header;
	private String referenceFile = "human_g1k_v37_decoy.fasta";
	private String geneAnnotationGzFile = "refGene.hg19.bed.gz";
	private String geneAnnotationGzTbiFile = "refGene.hg19.bed.gz.tbi";
	private String vcfFile = "renu_new.vcf.gz";
	private String vcfTbiFile = "renu_new.gz.tbi";

	private String vcfcollectionaName;
	private String databaseName;

	@Inject
	ConfigurationBean configurationBean;

	@PostConstruct
	public void init() {
		vcfcollectionaName = configurationBean.getVcfcollectioname();
		genecollectionaName = configurationBean.getGanecollectionname();

		mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
				configurationBean.getDatabase(), vcfcollectionaName, genecollectionaName, null);

		ChickenLineInfo = new ArrayList<SelectItem>();
		ChickenLineInfoSecond = new ArrayList<SelectItem>();
		chromosomeList = new ArrayList<SelectItem>();

		selectedStatisticBean = new OutputSnpStatisticsBean();

		osnpsb = new OutputSnpStatisticsBean();

		createBarModel(SnpQuery.getMaxcounter());

		createPieModel1(0);

	}

	public void saveTxt() {

	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	private String genecollectionaName;

	public String getVcfcollectionaName() {
		return vcfcollectionaName;
	}

	public void setVcfcollectionaName(String vcfcollectionaName) {
		this.vcfcollectionaName = vcfcollectionaName;
	}

	public String getGenecollectionaName() {
		return genecollectionaName;
	}

	public void setGenecollectionaName(String genecollectionaName) {
		this.genecollectionaName = genecollectionaName;
	}

	public String getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(String referenceFile) {
		this.referenceFile = referenceFile;
	}

	public String getGeneAnnotationGzFile() {
		return geneAnnotationGzFile;
	}

	public void setGeneAnnotationGzFile(String geneAnnotationGzFile) {
		this.geneAnnotationGzFile = geneAnnotationGzFile;
	}

	public String getGeneAnnotationGzTbiFile() {
		return geneAnnotationGzTbiFile;
	}

	public void setGeneAnnotationGzTbiFile(String geneAnnotationGzTbiFile) {
		this.geneAnnotationGzTbiFile = geneAnnotationGzTbiFile;
	}

	public String getVcfFile() {
		return vcfFile;
	}

	public void setVcfFile(String vcfFile) {
		this.vcfFile = vcfFile;
	}

	public String getVcfTbiFile() {
		return vcfTbiFile;
	}

	public void setVcfTbiFile(String vcfTbiFile) {
		this.vcfTbiFile = vcfTbiFile;
	}

	public boolean isByGeneSearch() {
		return byGeneSearch;
	}

	public void setByGeneSearch(boolean byGeneSearch) {
		this.byGeneSearch = byGeneSearch;
	}

	MongoDBLoader mongoDBLoader = new MongoDBLoader();
	List<String> listOfCollectionsString;

	public List<String> getListOfCollectionsString() {
		return listOfCollectionsString;
	}

	public void setListOfCollectionsString(List<String> listOfCollectionsString) {
		this.listOfCollectionsString = listOfCollectionsString;
	}

	MongoIterable<String> listOfCollections;

	public void searchhomeonload() {
		listOfCollectionsString = new ArrayList<>();
		listOfCollections = mongoDBLoader.getAllCollections();

		MongoCursor<String> cursor = listOfCollections.iterator();
		while (cursor.hasNext()) {
			String table = cursor.next();
			listOfCollectionsString.add(table);
		}

	}

	public void onload() {
		System.out.println("on load called" + vcfcollectionaName + " Gene :" + genecollectionaName);

		if (vcfcollectionaName != null || genecollectionaName != null) {

		} else {
			databaseName = uploadBean.getDatabasename();
			vcfcollectionaName = uploadBean.getVcfcollection();
			genecollectionaName = uploadBean.getGenecollectionaName();

		}

		setStartPosition(0);
		setEndPosition(0);
		setChromosome("1");
	}

	public MongoIterable<String> getListOfCollections() {
		return listOfCollections;
	}

	public void setListOfCollections(MongoIterable<String> listOfCollections) {
		this.listOfCollections = listOfCollections;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

//	public void onloadIGV() {
//
//		ProcessBuilder pb = new ProcessBuilder(
//				"/home/renu/Desktop/snpdatabase/DataInput/IGVinput/renu/myshellScript.sh", "myArg1", "myArg2");
//		try {
//			pb.start();
//		} catch (IOException ex) {
//			Logger.getLogger(SNPHomeBean.class.getName()).log(Level.SEVERE, null, ex);
//		}
//
//	}

	public String getSearchById() {
		return searchById;
	}

	public void setSearchById(String searchById) {
		this.searchById = searchById;
	}

	public String next() {

		return "search";
	}

	public String adv_next() {

//		System.out.println("vcfcollectionaName : "+vcfcollectionaName+" genecollectionaName: "+configurationBean.getGanecollectionname());

		mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
				configurationBean.getDatabase(), configurationBean.getVcfcollectioname(),
				configurationBean.getGanecollectionname(), null);
		List<SelectItem> items = new ArrayList<SelectItem>();

		SnpQuery main = new SnpQuery(mongoDBLoader);

		Document doc = main.getSampleDocument();
		System.out.println(doc);

		if (doc != null) {

			List<String> listofsamples = doc.getList("Samples", String.class);

			for (String sample : listofsamples) {
				items.add(new SelectItem(sample.trim()));
			}

			setChickenLineInfo(items);
			setChickenLineInfoSecond(items);

			chromosomeList.clear();

			List<String> chromosomeListDB = doc.getList("Chromosome", String.class);

			for (String chromosomeItem : chromosomeListDB) {
				chromosomeList.add(new SelectItem(chromosomeItem.trim(), chromosomeItem.trim()));
			}

			if (checkAdvflag) {
				return "advanceSearch";
			} else {
//				System.out.println("forward to search");
				return "search";
			}

		} else {
//			System.out.println("forward to recordnotfound");
			return "recordnotfound";
		}

	}

	public String getForward() {

//		System.out.println(vcfcollectionaName);
		configurationBean.setVcfcollectioname(vcfcollectionaName);
		configurationBean.setGanecollectionname(vcfcollectionaName.concat("gtf"));

		String ret = adv_next();
		return ret;

	}

	boolean checkAdvflag = false;

	public String getAdvForward() {
		checkAdvflag = true;

//		System.out.println("in AdvForward : "+vcfcollectionaName);
		configurationBean.setVcfcollectioname(vcfcollectionaName);
		configurationBean.setGanecollectionname(vcfcollectionaName.concat("gtf"));

		String ret = adv_next();
		return ret;

	}

	public boolean isCheckAdvflag() {
		return checkAdvflag;
	}

	public void setCheckAdvflag(boolean checkAdvflag) {
		this.checkAdvflag = checkAdvflag;
	}

	public boolean isAdvFlag() {
		return advFlag;
	}

	public void setAdvFlag(boolean advFlag) {
		this.advFlag = advFlag;
	}

	private String geneId;

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getGeneId() {
		return geneId;
	}

	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}

	private List resultList = new ArrayList<>();

//    private List<Temp> strList;
	private List<OutputSnpBean> strList;

	public List<OutputSnpBean> getStrList() {
		return strList;
	}

	public void setStrList(List<OutputSnpBean> strList) {
		this.strList = strList;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getSearchOption() {
		return searchOption;
	}

	public boolean isValue1() {
		return value1;
	}

	public void setValue1(boolean value1) {
		this.value1 = value1;
	}

	public void setSearchOption(String searchOption) {
		this.searchOption = searchOption;
	}

	private List<String> selectedChickenLineInfoSetOne;
	List<SelectItem> ChickenLineInfo;
	List<SelectItem> ChickenLineInfoSecond;

	public List<SelectItem> getChickenLineInfoSecond() {
		return ChickenLineInfoSecond;
	}

	public void setChickenLineInfoSecond(List<SelectItem> ChickenLineInfoSecond) {
		this.ChickenLineInfoSecond = ChickenLineInfoSecond;
	}

	List<SelectItem> chromosomeList;

	public List<SelectItem> getChromosomeList() {
		return chromosomeList;
	}

	public void setChromosomeList(List<SelectItem> chromosomeList) {
		this.chromosomeList = chromosomeList;
	}

	private List<String> selectedChickenLineInfoSetTwo;
	private String chromosome = "ALL";
	private long startPosition;

	public long getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(long startPosition) {
		this.startPosition = startPosition;
	}

	public long getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(long endPosition) {
		this.endPosition = endPosition;
	}

	private long endPosition;

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public List<String> getSelectedChickenLineInfoSetOne() {
		return selectedChickenLineInfoSetOne;
	}

	public void setSelectedChickenLineInfoSetOne(List<String> selectedChickenLineInfoSetOne) {
		this.selectedChickenLineInfoSetOne = selectedChickenLineInfoSetOne;
	}

	public List<String> getSelectedChickenLineInfoSetTwo() {
		return selectedChickenLineInfoSetTwo;
	}

	public void setSelectedChickenLineInfoSetTwo(List<String> selectedChickenLineInfoSetTwo) {
		this.selectedChickenLineInfoSetTwo = selectedChickenLineInfoSetTwo;
	}

	public List<SelectItem> getChickenLineInfo() {
		return ChickenLineInfo;
	}

	public void setChickenLineInfo(List<SelectItem> ChickenLineInfo) {
		this.ChickenLineInfo = ChickenLineInfo;
	}

	private OutputSnpStatisticsBean selectedStatisticBean = new OutputSnpStatisticsBean();

	private OutputSnpStatisticsBean osnpsb = new OutputSnpStatisticsBean();

	String strPosition;
	BufferedWriter writer = null;

	boolean tblFlag = false;
	boolean igvFlag = false;
	boolean graphFlag = true;

	public boolean isTblFlag() {
		return tblFlag;
	}

	public void setTblFlag(boolean tblFlag) {
		this.tblFlag = tblFlag;
	}

	public boolean isIgvFlag() {
		return igvFlag;
	}

	public void setIgvFlag(boolean igvFlag) {
		this.igvFlag = igvFlag;
	}

	public boolean isGraphFlag() {
		return graphFlag;
	}

	public void setGraphFlag(boolean graphFlag) {
		this.graphFlag = graphFlag;
	}

	public void setFlagValue(String value) {
//		System.out.println("Value : " + value);

		if (value.equals("table")) {
			tblFlag = true;
			igvFlag = false;
			graphFlag = false;
		}

//        if (value.equals("IGV")) {
//            tblFlag = false;
//            igvFlag = true;
//            graphFlag = false;
//        }
		if (value.equals("graph")) {
			tblFlag = false;
			igvFlag = false;
			graphFlag = true;
		}
	}

	boolean singlechromosomesearchflag = true;
	String setone, setTwo;

	public String submit() {

		if (getSelectedChickenLineInfoSetOne().size() > 0 && getSelectedChickenLineInfoSetTwo().size() > 0) {

			if (!getChromosome().equals("ALL")) {
				singlechromosomesearchflag = true;
			} else {
				singlechromosomesearchflag = false;
			}

			setone = makeCommaSeperatedString(getSelectedChickenLineInfoSetOne());
			setTwo = makeCommaSeperatedString(getSelectedChickenLineInfoSetTwo());

			if (value1) {
				String ch = getChromosome().substring(0, getChromosome().indexOf(':'));
				strPosition = getChromosome().substring(getChromosome().indexOf(':') + 1, getChromosome().length());

				strList = callNOSQLMongoDB(setone, setTwo, ch, strPosition);

			}

			if (!value1) {

				if ((!(getChromosome().equals("")) || getChromosome().length() > 0)
						&& (getStartPosition() > 0 && getEndPosition() > 0)) {
					strPosition = startPosition + "-" + endPosition;
					strList = callNOSQLMongoDB(setone, setTwo, getChromosome(), strPosition);
				}

				if ((getChromosome().equals("") || getChromosome().length() <= 0)
						&& (getStartPosition() > 0 && getEndPosition() > 0)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Enter valid Chromosome!!!"));
				}

				if ((!(getChromosome().equals("")) || getChromosome().length() > 0)
						&& (getStartPosition() <= 0 && getEndPosition() > 0)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Enter valid start and end position!!!"));
				}

				if ((!(getChromosome().equals("")) || getChromosome().length() > 0)
						&& (getStartPosition() > 0 && getEndPosition() <= 0)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Enter valid start and end position!!!"));
				}

				if ((!(getChromosome().equals("")) || getChromosome().length() > 0)
						&& (getStartPosition() <= 0 && getEndPosition() <= 0)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Enter valid start and end position!!!"));
				}

				if ((getChromosome().equals("") || getChromosome().length() <= 0)
						&& (getStartPosition() <= 0 && getEndPosition() <= 0)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Enter valid chromosome and position!!!"));
				}

			}
		}
		return "result?faces-redirect=true";
	}

	public String searchByAdv() {

		// simulate a heavy operation
		if (getSelectedChickenLineInfoSetOne().size() > 0 && getSelectedChickenLineInfoSetTwo().size() > 0) {
			String setone = makeCommaSeperatedString(getSelectedChickenLineInfoSetOne());
			String setTwo = makeCommaSeperatedString(getSelectedChickenLineInfoSetTwo());

			if (!advFlag) {
				strList = callNOSQLMongoDBGene(setone, setTwo, getGeneId());
			} else {
				strList = callNOSQLMongoDB(setone, setTwo, getSearchById());
			}

		}

		return "result?faces-redirect=true";
	}

	public boolean isSinglechromosomesearchflag() {
		return singlechromosomesearchflag;
	}

	public void setSinglechromosomesearchflag(boolean singlechromosomesearchflag) {
		this.singlechromosomesearchflag = singlechromosomesearchflag;
	}

	List<OutputSnpBean> outputSNPBeans = new ArrayList<OutputSnpBean>();
	List<OutputSnpStatisticsBean> retrieveVCFStatisticRecords = new ArrayList<>();

	public List<OutputSnpBean> getOutputSNPBeans() {
		return outputSNPBeans;
	}

	public void setOutputSNPBeans(List<OutputSnpBean> outputSNPBeans) {
		this.outputSNPBeans = outputSNPBeans;
	}

	public List<OutputSnpStatisticsBean> getRetrieveVCFStatisticRecords() {
		return retrieveVCFStatisticRecords;
	}

	public void setRetrieveVCFStatisticRecords(List<OutputSnpStatisticsBean> retrieveVCFStatisticRecords) {
		this.retrieveVCFStatisticRecords = retrieveVCFStatisticRecords;
	}

	public List parseInToBean(List<String> retriveVCFRecords) {
		outputSNPBeans = new ArrayList<OutputSnpBean>();

		for (Iterator<String> iterator = retriveVCFRecords.iterator(); iterator.hasNext();) {
			String next = iterator.next();
			String[] rowdata = next.split("\t");
			String[] dataset = rowdata[4].split("#");
			outputSNPBeans
					.add(new OutputSnpBean(rowdata[0], Long.parseLong(rowdata[1]), rowdata[3], dataset[0], dataset[1]));

		}
		return outputSNPBeans;
	}

	private BarChartModel barModel;
	private PieChartModel pieModel1;

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	List<OutputSnpBean> retriveVCFRecords = new ArrayList<>();

	public List<OutputSnpBean> getRetriveVCFRecords() {
		return retriveVCFRecords;
	}

	public void setRetriveVCFRecords(List<OutputSnpBean> retriveVCFRecords) {
		this.retriveVCFRecords = retriveVCFRecords;
	}

	public List callNOSQLMongoDBGene(String setone, String setTwo, String geneid) {
		String[] sa = setone.split(",");
		String[] sb = setTwo.split(",");
		List<String> leftList = Arrays.asList(sa);
		List<String> rightList = Arrays.asList(sb);

		MongoDBLoader mongoDBLoader = new MongoDBLoader();
		mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
				configurationBean.getDatabase(), vcfcollectionaName, genecollectionaName, null);
		SnpQuery main = new SnpQuery(mongoDBLoader);

		long startTime = System.currentTimeMillis();

		retriveVCFRecords = main.retriveGeneRecords(geneid, leftList, rightList);
		retrieveVCFStatisticRecords = main.getListStatisticBeans();

		if (retriveVCFRecords != null) {
			if (retriveVCFRecords.size() <= 0) {
				retriveVCFRecords.clear();

				if (retrieveVCFStatisticRecords != null) {
					if (retrieveVCFStatisticRecords.size() <= 0) {
						retrieveVCFStatisticRecords.clear();
					}

				}
			} else {

				createBarModel(SnpQuery.getMaxcounter());

				if (retrieveVCFStatisticRecords != null) {
					if (retrieveVCFStatisticRecords.size() > 0) {
						createPieModel1(0);
					}
				}
			}
		}

		long endTime = System.currentTimeMillis();

		System.out.println("Time taken : " + (endTime - startTime) + " ms");
		return retriveVCFRecords;
	}

	public List callNOSQLMongoDB(String setone, String setTwo, String recordId) {

		String[] sa = setone.split(",");
		String[] sb = setTwo.split(",");
		List<String> leftList = Arrays.asList(sa);
		List<String> rightList = Arrays.asList(sb);

		MongoDBLoader mongoDBLoader = new MongoDBLoader();
		mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
				configurationBean.getDatabase(), vcfcollectionaName, genecollectionaName, null);
		SnpQuery main = new SnpQuery(mongoDBLoader);

		List<OutputSnpBean> retriveVCFRecords = new ArrayList<>();

		long startTime = System.currentTimeMillis();

		retriveVCFRecords = main.retriveVCFIdRecords(getSearchById(), leftList, rightList);
		retrieveVCFStatisticRecords = main.getListStatisticBeans();
		long endTime = System.currentTimeMillis();

		System.out.println("Time taken : " + (endTime - startTime) + " ms");

		createBarModel(SnpQuery.getMaxcounter());

		createPieModel1(0);
//		System.out.println(retriveVCFRecords.size());

		return retriveVCFRecords;

	}

	@Inject
	UploadBean uploadBean;

	public List callNOSQLMongoDB(String setone, String setTwo, String chromosome, String position) {
		String[] sa = setone.split(",");
		String[] sb = setTwo.split(",");
		String[] posa = position.split("-");
		int pos1 = Integer.parseInt(posa[0]);
		int pos2 = Integer.parseInt(posa[1]);
		List<String> leftList = Arrays.asList(sa);
		List<String> rightList = Arrays.asList(sb);

		MongoDBLoader mongoDBLoader = new MongoDBLoader();

		if (getOptions().equals("1000Genome")) {
			mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
					configurationBean.getDatabase(), getVcfcollectionaName(), getGenecollectionaName(), null);
		}

		if (getOptions().equals("selfWithSelf")) {
			mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
					configurationBean.getDatabase(), getVcfcollectionaName(), getGenecollectionaName(), null);

		}

		SnpQuery snpQuery = new SnpQuery(mongoDBLoader);

		List<OutputSnpBean> retriveVCFRecords = new ArrayList<>();

		long startTime = System.currentTimeMillis();

		retriveVCFRecords = snpQuery.retriveVCFRecords(chromosome, pos1, pos2, leftList, rightList);

		retrieveVCFStatisticRecords = snpQuery.getListStatisticBeans();

		if (retriveVCFRecords != null) {
			for (OutputSnpStatisticsBean retrieveVCFStatisticRecord : retrieveVCFStatisticRecords) {
				Map<String, Set<OutputSnpBean>> map = retrieveVCFStatisticRecord.getMap1();

				Iterator<Map.Entry<String, Set<OutputSnpBean>>> entries = map.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, Set<OutputSnpBean>> entry = entries.next();
				}
			}
		}

		long endTime = System.currentTimeMillis();

		System.out.println("Time taken : " + (endTime - startTime) + " ms");

		createBarModel(SnpQuery.getMaxcounter());

		createPieModel1(0);

		System.out.println("callNOSQLMongoDB - retriveVCFRecords.size() -" + retriveVCFRecords.size());

		return retriveVCFRecords;
	}

	private String displayoption = "A/A";

	public String getDisplayoption() {
		return displayoption;
	}

	public void setDisplayoption(String displayoption) {
		this.displayoption = displayoption;
	}

	public void xx(String option) {
		displayoption = option;

	}

	private void createBarModel(int maxcounter) {
		barModel = initBarModel();

		barModel.setTitle("Chromosome Chart");
		barModel.setAnimate(true);
		barModel.setShowPointLabels(true);
		barModel.setMouseoverHighlight(true);
		barModel.setZoom(true);
		barModel.setExtender("customExtender");

		Axis xAxis = barModel.getAxis(AxisType.X);
		xAxis.setLabel("Chromosome");

		Axis yAxis = barModel.getAxis(AxisType.Y);

		yAxis.setLabel("SNP's");
		yAxis.setMin(0);
		System.out.println("maxcounter : " + maxcounter);
		yAxis.setMax(maxcounter + 10);
	}

	public OutputSnpStatisticsBean getSelectedStatisticBean() {
		return selectedStatisticBean;
	}

	public void setSelectedStatisticBean(OutputSnpStatisticsBean selectedStatisticBean) {
		this.selectedStatisticBean = selectedStatisticBean;
	}

	private void createPieModel1(int index) {
		pieModel1 = new PieChartModel();

		if (retrieveVCFStatisticRecords.size() > 0) {
			osnpsb = retrieveVCFStatisticRecords.get(index);
			selectedStatisticBean = retrieveVCFStatisticRecords.get(index);

			pieModel1.set("A/A", osnpsb.A_A);
			pieModel1.set("A/T", osnpsb.A_T);
			pieModel1.set("A/C", osnpsb.A_C);
			pieModel1.set("A/G", osnpsb.A_G);

			pieModel1.set("T/A", osnpsb.T_A);
			pieModel1.set("T/T", osnpsb.T_T);
			pieModel1.set("T/C", osnpsb.T_C);
			pieModel1.set("T/G", osnpsb.T_G);

			pieModel1.set("C/A", osnpsb.C_A);
			pieModel1.set("C/T", osnpsb.C_T);
			pieModel1.set("C/C", osnpsb.C_C);
			pieModel1.set("C/G", osnpsb.C_G);

			pieModel1.set("G/A", osnpsb.G_A);
			pieModel1.set("G/T", osnpsb.G_T);
			pieModel1.set("G/C", osnpsb.G_C);
			pieModel1.set("G/G", osnpsb.G_G);

		}

		pieModel1.setExtender("customPieExtender");
		pieModel1.setMouseoverHighlight(true);
		pieModel1.setLegendPosition("e");
		pieModel1.setShowDataLabels(true);
		pieModel1.setSliceMargin(2);
		pieModel1.setLegendRows(8);
	}

	public PieChartModel getPieModel1() {
		return pieModel1;
	}

	public void setPieModel1(PieChartModel pieModel1) {
		this.pieModel1 = pieModel1;
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();
		ChartSeries chromosomeseries = new ChartSeries();
		ChartSeries chromosomeseries2 = new ChartSeries();
		ChartSeries chromosomeseries3 = new ChartSeries();
		ChartSeries chromosomeseries4 = new ChartSeries();

		for (OutputSnpStatisticsBean bean : retrieveVCFStatisticRecords) {
			chromosomeseries.set("Chr" + bean.getChromosome(), bean.getCounter());
		}

		model.addSeries(chromosomeseries);

		if (singlechromosomesearchflag) {
			model.addSeries(chromosomeseries2);
		}
		return model;
	}

	public ArrayList<Character> getCharacterString(String str) {
		ArrayList<Character> al = new ArrayList<Character>();

		StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreTokens()) {
			al.add(st.nextToken().charAt(0));
		}

		return al;
	}

	public void itemSelect(ItemSelectEvent event) {
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
				"Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
		selectedStatisticBean = retrieveVCFStatisticRecords.get(event.getItemIndex());
		createPieModel1(event.getItemIndex());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void compareSecond(String str, String settwo) {

		StringTokenizer st = new StringTokenizer(settwo, "|");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();

		}
	}

	private String makeCommaSeperatedString(List<String> list) {
		String separator = ",";
		int total = list.size() * separator.length();
		for (String s : list) {
			total += s.length();
		}

		StringBuilder sb = new StringBuilder(total);
		for (String s : list) {
			sb.append(separator).append(s);
		}

		String result = sb.substring(separator.length());

		return result;

	}

	StringBuilder sb = new StringBuilder("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t");
	StringBuilder sbtwo = new StringBuilder("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t");

}
