/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.vcf.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sandeep
 */
public class Vcf {

    
    private String chromosome;
    private int position;
    private String id;
    private String ref;
    private Map<String,List<String>> mapAltLines;
    private Map<String,List<String>> mapTokenLines;
    private String lineToProcess;
    private List<String> geneName;
        
    public Vcf() {
        mapAltLines = new HashMap<>();
        mapTokenLines= new HashMap<>();
        geneName=new ArrayList<String>();
    }
    
    public Vcf(String chromosome, int position, String id, String ref, String lineToProcess) {
        this.chromosome = chromosome;
        this.position = position;
        this.id = id;
        this.ref = ref;
        mapAltLines = new HashMap<>();
         mapTokenLines= new HashMap<>();
        this.lineToProcess = lineToProcess;
       geneName=new ArrayList<String>();
    }

    public List<String> getGeneName() {
        return geneName;
    }

    public void setGeneName(List<String> geneName) {
        this.geneName = geneName;
    }


    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Map<String, List<String>> getMapAltLines() {
        return mapAltLines;
    }

//    public void setMapAltLines(Map<String, String> mapAltLines) {
//        this.mapAltLines = mapAltLines;
//    }

    public String getLineToProcess() {
        return lineToProcess;
    }

    public void setLineToProcess(String lineToProcess) {
        this.lineToProcess = lineToProcess;
    }

    public Map<String, List<String>> getMapTokenLines() {
        return mapTokenLines;
    }

    public void setMapTokenLines(Map<String, List<String>> mapTokenLines) {
        this.mapTokenLines = mapTokenLines;
    }

    
   

    void reset() {
        this.chromosome = null;
        this.position = 0;
        this.id = null;
        this.ref = null;
        this.mapAltLines = null;
        this.lineToProcess = null;
        this.geneName=new ArrayList<String>();
    }

    @Override
    public String toString() {
        return "VCFBean{" + "chromosome=" + chromosome + ", position=" + position + ", id=" + id + ", ref=" + ref + ", mapAltLines=" + mapAltLines + '}';
    }

    
    
    
    
}
   