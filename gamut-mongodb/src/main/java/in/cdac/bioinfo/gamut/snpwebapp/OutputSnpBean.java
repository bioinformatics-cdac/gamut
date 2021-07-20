/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.snpwebapp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author renu
 */

public class OutputSnpBean {

	private String recordID;

	private String Chromosome;

	private long Chromosome_Position;

	private String Ref;

	private String SetOne;

	private String SetTwo;

	private String SetOneRaw = "0|0";
	private String SetTwoRaw = "0|0";

	private List<String> geneList;

	public String getSetOneRaw() {
		return SetOneRaw;
	}

	public void setSetOneRaw(String SetOneRaw) {
		this.SetOneRaw = SetOneRaw;
	}

	public String getSetTwoRaw() {
		return SetTwoRaw;
	}

	public void setSetTwoRaw(String SetTwoRaw) {
		this.SetTwoRaw = SetTwoRaw;
	}

	public String getSetOne() {
		return SetOne;
	}

	public void setSetOne(String SetOne) {
		this.SetOne = SetOne;
	}

	public String getSetTwo() {
		return SetTwo;
	}

	public void setSetTwo(String SetTwo) {
		this.SetTwo = SetTwo;
	}

	public OutputSnpBean() {
	}

	public OutputSnpBean(String Chromosome, long Chromosome_Position, String Ref, String SetOne, String SetTwo) {
		this.Chromosome = Chromosome;
		this.Chromosome_Position = Chromosome_Position;
		this.Ref = Ref;
		this.SetOne = SetOne;
		this.SetTwo = SetTwo;
		this.geneList = new ArrayList();
	}

	public String getRecordID() {
		return recordID;
	}

	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}

	public String getChromosome() {
		return Chromosome;
	}

	public void setChromosome(String Chromosome) {
		this.Chromosome = Chromosome;
	}

	public long getChromosome_Position() {
		return Chromosome_Position;
	}

	public List<String> getGeneList() {
		return geneList;
	}

	public void setGeneList(List<String> geneList) {
		this.geneList = geneList;
	}

	public void setChromosome_Position(long Chromosome_Position) {
		this.Chromosome_Position = Chromosome_Position;
	}

	public String getRef() {
		return Ref;
	}

	public void setRef(String Ref) {
		this.Ref = Ref;
	}

	@Override
	public String toString() {
		return "OutputSNPBean{" + "recordID=" + recordID + ", Chromosome=" + Chromosome + ", Chromosome_Position="
				+ Chromosome_Position + ", Ref=" + Ref + ", SetOne=" + SetOne + ", SetTwo=" + SetTwo + ", SetOneRaw="
				+ SetOneRaw + ", SetTwoRaw=" + SetTwoRaw + '}';
	}
	
	public String csvFormat() {
		return Chromosome+","+Chromosome_Position+","+Ref+","+SetOne+","+SetTwo+","+geneList;
	}
	
	public String tsvFormat() {
		return Chromosome+"\t"+Chromosome_Position+"\t"+Ref+"\t"+SetOne+"\t"+SetTwo+"\t"+geneList;
	}
	

}
