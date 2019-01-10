package com.company;

public class Population {
    private Individual[] individuals;
    private static final long maxGeneValue = (long)Math.pow(2, Individual.defaultGeneLength);
    private static final long maxGeneValueStepSize = maxGeneValue / Main.populationSize;
    private static final String b48Sol = "010110000011101111010111000010111010101011100100";
    private static final String b42Sol = "010110000111101101011000011010101001110010";

    /*
     * Constructor
     */
    // Δημιουργία πληθυσμού
    public Population(int populationSize, boolean initialise) {
        individuals = new Individual[populationSize];
        // Αρχικοποίηση πληθυσμού
        if (initialise) {
            // Δημιουργία ατόμων
            for (int i = 0; i < size(); i++) {
                Individual newIndividual = new Individual();
                newIndividual.generateIndividual();
                saveIndividual(i, newIndividual);
            }
        }
    }

    public Population(int populationSize) { individuals = new Individual[populationSize]; }

    public Population(int populationSize, boolean initialise, int a) {
        individuals = new Individual[populationSize];
        // Αρχικοποίηση πληθυσμού
        if (initialise) {
            // Δημιουργία ατόμων
            for (int i = 0; i < size(); i++) {
                long randNum = (long)(Math.random() * (maxGeneValueStepSize * (i+1) - maxGeneValueStepSize * i) + 1) + maxGeneValueStepSize * i;
                //System.out.println(Long.valueOf(Long.toBinaryString(randNum)));
                Individual newIndividual = new Individual();
                //String a = String.format("%048d", Integer.valueOf(Long.toBinaryString(randNum)));
                newIndividual.setGenes(Long.toBinaryString(randNum));
                saveIndividual(i, newIndividual);
            }
            /*Individual newIndividual = new Individual();
            newIndividual.setGenes("00111111100001110101100001100001010001000101011000011110");
            saveIndividual(size()-1, newIndividual);*/
        }
    }

    /* Getters */
    public Individual getIndividual(int index) {
        return individuals[index];
    }

    // Επιστρέφει το ποιοτικότερο άτομο
    public Individual getFittest() throws CloneNotSupportedException {
        Individual fittest = individuals[0]; // Αρχικοποίηση του πρώτου ατόμου ως το ποιοτικότερο
        // αναζήτηση του ποιοτικότερου στον πίνακα
        for (int i = 0; i < individuals.length; i++) {
            // σύγκριση του τρέχοντος ποιοτικότερου με το τρέχον εξεταζόμενο στοιχείο του πίνακα και ανάθεση αν είναι ποιοτικότερο
            if (fittest.getFitness() < getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    // επιστρέφει το μέγεθος του πληθυσμού
    public int size() {
        return individuals.length;
    }

    // Αποθήκευση ατόμου στον πίνακα
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }
}
