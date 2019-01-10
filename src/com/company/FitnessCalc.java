package com.company;

import com.google.common.base.Splitter;

public class FitnessCalc {

    public static final int maxFitness= getMaxFitness();
    public static final double dMax= Main.z>0 ? Math.sqrt(Math.pow(Main.x, 2) + Math.pow(Main.y, 2)) :
            Math.sqrt(Math.pow(Main.x, 2) + Math.pow(Main.y, 2) + Math.pow(Main.z, 2));// μέγιστη απόσταση μεταξύ δύο φωλιών
    private static final double xyMaxBitsValue = Math.pow(2, Individual.defaultGeneLength/Main.bombsNum/2);
    public static final double factor = Main.x/(xyMaxBitsValue-1);

    static int getFitness(Individual individual) {

        // Δημιουργία αντιγράφου του πίνακα με τις σφηκοφωλιές για μην αλλοιωθεί ο αρχικός πίνακας
        Nest[] nests = Nest.cloneArray();
        // τα γονίδια διηρημένα σε 6 κομμάτια των 8 bit και με μετατροπή σε δεκαδικό σύστημα μας δίνουν τις συντεταγμένες των βομβών
        /*
            π.χ.   x1       y1       x2       y2       x3       y3
                00010001 00100000 01010010 00001011 11010111 00001001
         */
        // δυο πίνακες με τις συντεταγμένες των βομβών, τα 3 x και τα 3 y
        int [][] coords = new int [Main.bombsNum][2];
        // μετρητής, ταυτίζεται με τον αριθμό των βομβών μετά το for loop
        int z = 0;

        // Σπάει σε 3 ίσα κομμάτια των 16 bit, που περιέχουν τις συντεταγμένες των τριών βομβών
        for(final String token : Splitter.fixedLength(Individual.defaultGeneLength/Main.bombsNum).split(individual.toString())){
            // Σπάει το κομμάτι των 16 bit σε 2 των 8 bit, δηλαδή στα x και y και τα μετατρέπει σε δεκαδικούς
            // Πολλαπλασιασμός με (100/255) γιατί τα 8 bit βγάζουν μέχρι τον αριθμό 255 σε δεκαδικό, ενώ οι συντεταγμένες μπορούν να είναι από 0 μέχρι 100
            coords[z][0] = (int)(((double)Integer.parseInt(token.substring(0, token.length() / 2), 2)) * factor);
            coords[z++][1] = (int)(((double)Integer.parseInt(token.substring(token.length() / 2), 2)) * factor);
            //System.out.println(x1[z] + "\n" + y1[z]);
        }

        //Υπολογίζεται η απόσταση των βομβών από τις φωλιές και το πλήθος σφηκών που σκοτώνουν
        double n=nests.length; // Μήκος πίνακα φωλιών
        double d; // απόσταση βόμβας από τη φωλιά
        double K; // πλήθος σφηκών που θα σκοτωθούν σε μια φωλιά
        int T = 0; // πλήθος σφηκών που θα σκοτωθούν σε όλες τις φωλιές, από όλες τις βόμβες μαζί
        // loop από 0 εώς για να υπολογίσουμε για όλες τις βόμβες
        for(int j=0; j < Main.bombsNum; j++) {
            for (Nest nest : nests) {
                // αν δεν υπάρχουν άλλες σφήκες στη φωλιά, περνάμε στην επόμενη γιατί σε αυτή δεν θα σκοτωθεί καμία
                if (nest.getHosesNum() == 0)
                    continue;
                // υπολογισμός απόστασης βόμβας j από τη φωλιά
                d = Math.sqrt(Math.pow(coords[j][0] - nest.getX(), 2) + Math.pow(coords[j][1] - nest.getY(), 2));
                //System.out.println("d[" + (i+1) + "]: " + d);
                // Υπολογισμός πλήθους σφηκών που θα σκοτωθούν στη φωλιά i από τη βόμβα j
                K = n * (dMax / (20 * d + 0.00001));
                /* Αν ο αριθμός των σφηκών που σκοτώνει η βόμβα στη φωλιά είναι μεγαλύτερος από το πλήθος των σφηκών που υπάρχουν,
                   προστίθεται στο σύνολο των σφηκών που θα σκοτωθούν η αρνητική διαφορά.
                 */
                if ((nest.getHosesNum() - (int) K) < 0)
                    K += nest.getHosesNum() - (int) K;
                // Αφαιρείται από των αριθμό των σφηκών της φωλιάς ο αριθμός αυτών που θα σκοτωθούν
                nest.setHosesNum(nest.getHosesNum() - (int) K);
                //System.out.println("Hoses num: [" + (i+1) + "]: " + nestsC[i].getHosesNum());
                //System.out.println("K[" + (i + 1) + "]: " + (int)K + "\n");
                // Προστίθεται στο πλήθος των σφηκών που θα σκοτωθούν σε όλες τις φωλιές ο αριθμός των σφηκών που θα σκοτωθούν στην i φωλιά
                T += (int) K;
            }
        }
        return T;
    }

    /*static int getFitness(String genes) throws CloneNotSupportedException {

        Nest[] nests = Nest.cloneArray();
        int [][] coords = new int [Main.bombsNum][2];
        int z = 0;

        for(final String token : Splitter.fixedLength(Individual.defaultGeneLength/Main.bombsNum).split(genes)){
            System.out.println(token);
            coords[z][0] = (int)(((double)Integer.parseInt(token.substring(0, token.length() / 2), 2)) * factor);
            coords[z++][1] = (int)(((double)Integer.parseInt(token.substring(token.length() / 2), 2)) * factor);
        }

        double n=nests.length; // Μήκος πίνακα φωλιών
        double d; // απόσταση βόμβας από τη φωλιά
        double K; // πλήθος σφηκών που θα σκοτωθούν σε μια φωλιά
        int T = 0; // πλήθος σφηκών που θα σκοτωθούν σε όλες τις φωλιές, από όλες τις βόμβες μαζί
        for(int j=0; j < Main.bombsNum; j++) {
            for (Nest nest : nests) {
                if (nest.getHosesNum() == 0) continue;
                d = Math.sqrt(Math.pow(coords[j][0] - nest.getX(), 2) + Math.pow(coords[j][1] - nest.getY(), 2));
                K = n * (dMax / (20 * d + 0.00001));
                if ((nest.getHosesNum() - (int) K) < 0) K += nest.getHosesNum() - (int) K;
                nest.setHosesNum(nest.getHosesNum() - (int) K);
                T += (int) K;
            }
        }
        return T;
    }*/

    // επιστρέφει τη μέγιστη ποιότητα
    private static int getMaxFitness() {
        int maxFitness = 0;
        // αθροίζει το σύνολο των σφηκών όλων των φωλιών
        for (Nest nest: Main.nests) {
            maxFitness+=nest.getHosesNum();
        }
        return maxFitness;
    }
}
