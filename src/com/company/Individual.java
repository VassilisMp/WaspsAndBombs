package com.company;

public class Individual {

    static final int defaultGeneLength = properGeneLength()*2*Main.bombsNum; // μήκος γονιδίου
    private byte[] genes = new byte[defaultGeneLength]; // γονίδια
    private int fitness = 0; // ποιότητα ατόμου

    // Δημιουργία τυχαίου ατόμου
    public void generateIndividual() {
        for (int i = 0; i < size(); i++)
            this.genes[i] = (byte) Math.round(Math.random());
    }

    /* Getters and setters */

    public byte getGene(int index) {
        return this.genes[index];
    }



    public void setGene(int index, byte value) {
        this.genes[index] = value;
    }

    public void setGenes(String genes) {
        for (int i = 0; i < genes.length(); i++) {
            this.genes[this.genes.length-i-1] = (byte)Character.getNumericValue(genes.charAt(genes.length()-i-1));
        }
    }

    /* Public methods */
    public int size() {
        return this.genes.length;
    }

    // Επιστρέφει την ποιότητα
    public int getFitness()  throws CloneNotSupportedException {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }

    // Εκτύπωση γονιδίων
    @Override
    public String toString() {
        StringBuilder geneString = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            geneString.append(getGene(i));
        }
        return geneString.toString();
    }

    private static int properGeneLength() {
        for (int i = 0;; i++) {
            if (Math.pow(2, i) > Main.x) return i;
        }
    }
}
