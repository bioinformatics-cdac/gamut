/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.vcf.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author renu
 */
public class Gene implements Comparable<Gene>, Serializable {

    private static final long serialVersionUID = 1L;

    private String geneId;
    private String chromosome;
    private long startPosition;
    private long endPosition;

    public Gene() {
    }

    public Gene(String geneId, String chromosome, long startPosition, long endPosition) {
        this.geneId = geneId;
        this.chromosome = chromosome;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.geneId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Gene other = (Gene) obj;
        if (!Objects.equals(this.geneId, other.geneId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return geneId + "\t" + chromosome + "\t" + startPosition + "\t" + endPosition;
    }

    @Override
    public int compareTo(Gene o) {

        int i = this.geneId.compareTo(o.geneId);
        int j = this.chromosome.compareTo(o.chromosome);
        int k = Long.valueOf(this.startPosition).compareTo(o.startPosition);
        int l = Long.valueOf(this.endPosition).compareTo(o.endPosition);

        int compare = 0;

        if (i == 0) {

            if (j == 0) {

                if (k == 0) {

                    compare = l;
                } else {
                    compare = k;
                }
            } else {
                compare = j;
            }
        } else {
            compare = i;
        }

        return compare;

    }

    void reset() {
        this.chromosome = null;
        this.geneId = null;
        this.startPosition = 0l;
        this.endPosition = 0l;
    }

}
