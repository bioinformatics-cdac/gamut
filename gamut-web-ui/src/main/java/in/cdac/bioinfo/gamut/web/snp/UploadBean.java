/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.web.snp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.mongodb.client.model.Indexes;

import in.cdac.bioinfo.gamut.mongodb.MongoDBLoader;
import in.cdac.bioinfo.gamut.vcf.GeneParser;
import in.cdac.bioinfo.gamut.vcf.VcfParser;
import in.cdac.bioinfo.gamut.web.config.ApplicationConfigurationException;
import in.cdac.bioinfo.gamut.web.config.ConfigurationBean;
import in.cdac.bioinfo.gamut.web.config.PathUtil;

@Named
@SessionScoped
public class UploadBean implements Serializable {

	private Part referenceFile;
	private Part annotationFile;
	private Part annotationtbiFile;
	private Part gtfFile;
	private Part variablevcfFile;

	private String databasename;

	public String getDatabasename() {
		return databasename;
	}

	public void setDatabasename(String databasename) {
		this.databasename = databasename;
	}

	private String vcfcollection;
	private String organismScientificName = "Homo_sapiens";
	private String genecollectionaName;

	public Part getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(Part referenceFile) {
		this.referenceFile = referenceFile;
	}

	public Part getAnnotationFile() {
		return annotationFile;
	}

	public void setAnnotationFile(Part annotationFile) {
		this.annotationFile = annotationFile;
	}

	public Part getAnnotationtbiFile() {
		return annotationtbiFile;
	}

	public void setAnnotationtbiFile(Part annotationtbiFile) {
		this.annotationtbiFile = annotationtbiFile;
	}

	public Part getVariablevcfFile() {
		return variablevcfFile;
	}

	public void setVariablevcfFile(Part variablevcfFile) {
		this.variablevcfFile = variablevcfFile;
	}

	public Part getGtfFile() {
		return gtfFile;
	}

	public void setGtfFile(Part gtfFile) {
		this.gtfFile = gtfFile;
	}

	public String getGenecollectionaName() {
		return genecollectionaName;
	}

	public void setGenecollectionaName(String genecollectionaName) {
		this.genecollectionaName = genecollectionaName;
	}

	public String getOrganismScientificName() {
		return organismScientificName;
	}

	public void setOrganismScientificName(String organismScientificName) {
		this.organismScientificName = organismScientificName;
	}

	@PostConstruct
	public void init() {

	}

	public String getVcfcollection() {
		return vcfcollection;
	}

	public void setVcfcollection(String vcfcollection) {
		this.vcfcollection = vcfcollection;
	}

	FacesContext facesContext = FacesContext.getCurrentInstance();

	public void uploadGtfFile() {
		try {

			String gtfFileName = getFilename(gtfFile);
			File outputFilePath;

			PathUtil pathUtil = new PathUtil();
			Path p = Paths.get(pathUtil.getApplicationUserUploadDirectory().getAbsolutePath(), gtfFileName);
			outputFilePath = new File(pathUtil.getApplicationUserUploadDirectory() + File.separator + gtfFileName);

			InputStream input = gtfFile.getInputStream();
			Files.copy(input, p, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException ex) {
			Logger.getLogger(UploadBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ApplicationConfigurationException ex) {
			Logger.getLogger(UploadBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void uploadVariableFile() {
		try {

			String filename = getFilename(variablevcfFile);
			File outputFilePath;

			PathUtil pathUtil = new PathUtil();
			Path p = Paths.get(pathUtil.getApplicationUserUploadDirectory().getAbsolutePath(), filename);
			
			outputFilePath = new File(pathUtil.getApplicationUserUploadDirectory() + File.separator + filename);

			InputStream input = variablevcfFile.getInputStream();
			Files.copy(input, p, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException ex) {
			Logger.getLogger(UploadBean.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ApplicationConfigurationException ex) {
			Logger.getLogger(UploadBean.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public String uploadFile() throws IOException {
		long start = System.currentTimeMillis();
		uploadGtfFile();
		long end = System.currentTimeMillis();
		System.out.println("uploadGtfFile Total Time " + (end - start) / 1000 + " in Seconds");
		start = System.currentTimeMillis();

		uploadVariableFile();
		end = System.currentTimeMillis();
		System.out.println("uploadVariableFile Time " + (end - start) / 1000 + " in Seconds");
		System.out.println("Succesfully file uploaded ... Upload In DB start ... ");

		new Thread(new Runnable() {

			@Override
			public void run() {
				createCollection();

			}
		}).start();

		return "basicsearchhome.xhtml?faces-redirect=true";

	}

	@Inject
	SNPHomeBean sNPHomeBean;

	@Inject
	ConfigurationBean configurationBean;

	public void createCollection() {

		try {

			MongoDBLoader mongoDBLoader = new MongoDBLoader();

			vcfcollection = getFilename(variablevcfFile);

			if (vcfcollection.indexOf(".") > 0) {
				vcfcollection = vcfcollection.substring(0, vcfcollection.lastIndexOf("."));
			}

			genecollectionaName = vcfcollection.concat("_gtf");

			mongoDBLoader.init(configurationBean.getHost(), Integer.parseInt(configurationBean.getMongoPort()),
					configurationBean.getDatabase(), vcfcollection, genecollectionaName, null);

			boolean isExist = mongoDBLoader.deleteIfExistCollection(vcfcollection);
			PathUtil pathUtil = new PathUtil();

			long start = System.currentTimeMillis();
			String gtffile = getFilename(getGtfFile());
			File gtfoutputFilePath = new File(pathUtil.getApplicationUserUploadDirectory() + File.separator + gtffile);
			GeneParser gtfp = new GeneParser(gtfoutputFilePath, mongoDBLoader);
			gtfp.run();
			System.out.println("Gene Details Upload started...");

			long end = System.currentTimeMillis();
			System.out.println("GTF db upload Total Time " + (end - start) / 1000 + " in Seconds");
			start = System.currentTimeMillis();
			String filename = getFilename(variablevcfFile);
			
			File outputFilePath = new File(pathUtil.getApplicationUserUploadDirectory() + File.separator + filename);
			VcfParser vcfp = new VcfParser(outputFilePath, mongoDBLoader);
			vcfp.run();
			
			System.out.println("VCF Details Upload started...");

			end = System.currentTimeMillis();
			System.out.println("VCF db upload Total Time " + (end - start) / 1000 + " in Seconds");
			
			

			
		} catch (ApplicationConfigurationException ex) {
			Logger.getLogger(UploadBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	

	private static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

}
