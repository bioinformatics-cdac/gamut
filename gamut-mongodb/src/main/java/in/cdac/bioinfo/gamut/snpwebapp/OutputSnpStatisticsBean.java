/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.snpwebapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author renu
 */
public class OutputSnpStatisticsBean {

	private String recordID;
	private Long position;

	private List<String> geneList = new ArrayList<>();

	public List<String> getGeneList() {
		return geneList;
	}

	public void setGeneList(List<String> geneList) {
		this.geneList = geneList;
	}

	private List<OutputSnpBean> listbean;

	Map<String, Set<OutputSnpBean>> map1 = new LinkedHashMap<String, Set<OutputSnpBean>>();

	public Map<String, Set<OutputSnpBean>> getMap1() {
		return map1;
	}

	public void setMap1(Map<String, Set<OutputSnpBean>> map1) {
		this.map1 = map1;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public List<OutputSnpBean> getListbean() {
		return listbean;
	}

	public void setListbean(List<OutputSnpBean> listbean) {
		this.listbean = listbean;
	}

	private String Chromosome;

	int counter = 0;
	int maxcounter = 0;

	public int getMaxcounter() {
		return maxcounter;
	}

	public void setMaxcounter(int maxcounter) {
		this.maxcounter = maxcounter;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int A_A = 0, A_T = 0, A_C = 0, A_G = 0, T_A = 0, T_T = 0, T_C = 0, T_G = 0, C_A = 0, C_T = 0, C_C = 0, C_G = 0,
			G_A = 0, G_T = 0, G_C = 0, G_G = 0;

	public int getA_A() {
		return A_A;
	}

	public void setA_A(int A_A) {
		this.A_A = A_A;
	}

	public int getA_T() {
		return A_T;
	}

	public void setA_T(int A_T) {
		this.A_T = A_T;
	}

	public int getA_C() {
		return A_C;
	}

	public void setA_C(int A_C) {
		this.A_C = A_C;
	}

	public int getA_G() {
		return A_G;
	}

	public void setA_G(int A_G) {
		this.A_G = A_G;
	}

	public int getT_A() {
		return T_A;
	}

	public void setT_A(int T_A) {
		this.T_A = T_A;
	}

	public int getT_T() {
		return T_T;
	}

	public void setT_T(int T_T) {
		this.T_T = T_T;
	}

	public int getT_C() {
		return T_C;
	}

	public void setT_C(int T_C) {
		this.T_C = T_C;
	}

	public int getT_G() {
		return T_G;
	}

	public void setT_G(int T_G) {
		this.T_G = T_G;
	}

	public int getC_A() {
		return C_A;
	}

	public void setC_A(int C_A) {
		this.C_A = C_A;
	}

	public int getC_T() {
		return C_T;
	}

	public void setC_T(int C_T) {
		this.C_T = C_T;
	}

	public int getC_C() {
		return C_C;
	}

	public void setC_C(int C_C) {
		this.C_C = C_C;
	}

	public int getC_G() {
		return C_G;
	}

	public void setC_G(int C_G) {
		this.C_G = C_G;
	}

	public int getG_A() {
		return G_A;
	}

	public void setG_A(int G_A) {
		this.G_A = G_A;
	}

	public int getG_T() {
		return G_T;
	}

	public void setG_T(int G_T) {
		this.G_T = G_T;
	}

	public int getG_C() {
		return G_C;
	}

	public void setG_C(int G_C) {
		this.G_C = G_C;
	}

	public int getG_G() {
		return G_G;
	}

	public void setG_G(int G_G) {
		this.G_G = G_G;
	}

	public OutputSnpStatisticsBean() {

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

	@Override
	public String toString() {
		return "OutputSNPStatisticBean{" + "recordID=" + recordID + ", listbean=" + listbean + ", Chromosome="
				+ Chromosome + ", counter=" + counter + ", maxcounter=" + maxcounter + ", A_A=" + A_A + ", A_T=" + A_T
				+ ", A_C=" + A_C + ", A_G=" + A_G + ", T_A=" + T_A + ", T_T=" + T_T + ", T_C=" + T_C + ", T_G=" + T_G
				+ ", C_A=" + C_A + ", C_T=" + C_T + ", C_C=" + C_C + ", C_G=" + C_G + ", G_A=" + G_A + ", G_T=" + G_T
				+ ", G_C=" + G_C + ", G_G=" + G_G + '}';
	}

}
