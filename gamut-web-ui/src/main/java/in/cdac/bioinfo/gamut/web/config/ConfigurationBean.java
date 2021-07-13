/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import in.cdac.bioinfo.gamut.web.snp.SNPHomeBean;

/**
 *
 * @author bioinfo
 */
@Named(value = "configurationBean")
@ApplicationScoped
public class ConfigurationBean implements Serializable {

	/**
	 * Creates a new instance of ConfigurationBean
	 */
	public ConfigurationBean() {
		System.out.println("ConfigurationBean is called");
		init();
	}

	private String MongodbIP;
	private String MongoPort;
	private String Database;
	private String Host;
	private String vcfcollectioname;
	private String ganecollectionname;

	@PostConstruct
	public void init() {

		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(
					new File(System.getProperty("user.home") + File.separator + Config.CONFIG_PATH))));
		} catch (IOException ex) {
			Logger.getLogger(SNPHomeBean.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.out.println(properties);

		setHost(properties.getProperty(Config.MONGO_DB_HOST));
		setMongoPort(properties.getProperty(Config.MONGO_DB_PORT));
		setDatabase(properties.getProperty(Config.MONGO_DB_DATABASE_NAME));
		setVcfcollectioname(properties.getProperty(Config.MONGO_DB_COLLECTION_NAME));
		setGanecollectionname(properties.getProperty(Config.MONGO_DB_GENECOLLECTION_NAME));

	}

	public String getHost() {
		return Host;
	}

	public void setHost(String Host) {
		this.Host = Host;
	}

	public String getMongodbIP() {
		return MongodbIP;
	}

	public void setMongodbIP(String MongodbIP) {
		this.MongodbIP = MongodbIP;
	}

	public String getMongoPort() {
		return MongoPort;
	}

	public void setMongoPort(String MongoPort) {
		this.MongoPort = MongoPort;
	}

	public String getDatabase() {
		return Database;
	}

	public void setDatabase(String Database) {
		this.Database = Database;
	}

	public String getVcfcollectioname() {
		return vcfcollectioname;
	}

	public void setVcfcollectioname(String vcfcollectioname) {
		this.vcfcollectioname = vcfcollectioname;
	}

	public String getGanecollectionname() {
		return ganecollectionname;
	}

	public void setGanecollectionname(String ganecollectionname) {
		this.ganecollectionname = ganecollectionname;
	}

	public String submit() {
		System.out.println("Ip : " + getMongodbIP() + " Port : " + getMongoPort() + " Database : " + getDatabase()
				+ " Vcf Collection :" + getVcfcollectioname() + " Gene Collection Name : " + getGanecollectionname());

		String userHome = System.getProperty("user.home");
		String basePath = userHome + File.separator + Config.CONFIG_PATH;

		File file = new File(basePath);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ConfigurationBean.class.getName()).log(Level.SEVERE, null, ex);
		}

		Properties p = new Properties();
		p.setProperty(Config.MONGO_DB_HOST, getMongodbIP());
		p.setProperty(Config.MONGO_DB_PORT, getMongoPort());
		p.setProperty(Config.MONGO_DB_DATABASE_NAME, getDatabase());
		p.setProperty(Config.MONGO_DB_COLLECTION_NAME, getVcfcollectioname());
		p.setProperty(Config.MONGO_DB_GENECOLLECTION_NAME, getGanecollectionname());

		try {
			p.store(fileOutputStream, "");
		} catch (IOException ex) {
			Logger.getLogger(ConfigurationBean.class.getName()).log(Level.SEVERE, null, ex);
		}
		return "home";
	}
}
