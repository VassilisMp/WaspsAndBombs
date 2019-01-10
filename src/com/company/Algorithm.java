package com.company;

public class Algorithm {

    /* Παράμετροι */
    private static final double uniformRate = 0.5; // βαθμός ομοιότητας των ατόμων που παράγονται με crossover, όσο μεγαλώνει μοιάζει περισσότερο στο πρώτο άτομο
    private static final double mutationRate = 0.1;  // βαθμός μετάλλαξης
    private static final int tournamentSize = 5; // μέγεθος τουρνουά
    private static final int elitism = 1; // 0 = false, 1 = true


    // Εξέλιξη του πληθυσμού
    public static Population evolvePopulation(Population pop) throws CloneNotSupportedException {
        //Δημιουργία νέου πληθυσμού
        Population newPopulation = new Population(pop.size());

        // κρατάμε τον καλύτερο άτομο
        if (elitism == 1) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Δημιουργία νέων ατόμων με crossover
        for (int i = elitism; i < pop.size(); i++) {
            // κάνουμε Tournament Selection για να βρούμε τα άτομα που θα συμμετέχουν στην αναπαραγωγή
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Μετάλλαξη πληθυσμού
        for (int i = elitism; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // Crossover ατόμων
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        //Δημιουργία νέου ατόμου
        Individual newSol = new Individual();
        // loop των γονιδίων
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    // Μετάλλαξη ενός ατόμου
    private static void mutate(Individual indiv) {
        // Loop των γονιδίων
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                // Δημιουργία τυχαίου γονιδίου
                byte gene = (byte) Math.round(Math.random());
                indiv.setGene(i, gene);
            }
        }
    }

    // Υλοποίηση τουρνουά για να επιλεγούν τα άτομα για το crossover
    private static Individual tournamentSelection(Population pop) throws CloneNotSupportedException {
        // Δημιουργία πληθυσμού για το τουρνουά
        Population tournament = new Population(tournamentSize);
        // Επιλογή ενός τυχαίου ατόμου για κάθε θέση στο τουρνουά
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // επιστροφή του καλύτερου
        return tournament.getFittest();
    }
}
