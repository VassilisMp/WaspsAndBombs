package com.company;

import com.google.common.base.Splitter;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;
import io.jenetics.util.IntRange;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;

public class Main {

    //Χρησιμοποιώ πίνακα με των αριθμό των σφηκών και των συντεταγμένων για καλύτερη απόδοση, αντί για πίνακα 100*100
    private static final int N = 12;
    static final Nest[] nests = new Nest[N];
    static final int populationSize = 100;
    private static final int tournamentSampleSize = 5;
    static final int bombsNum = 3;
    public static final int dimensions = 2;
    static final int x = 100, y = 100, z = 0;
    private static final boolean initialise = true;
    private static final int runtime = 10; //seconds

    private static Integer getFitness(final Genotype gt) {

        // Δημιουργία αντιγράφου του πίνακα με τις σφηκοφωλιές για μην αλλοιωθεί ο αρχικός πίνακας
        Nest[] nests = Nest.cloneArray();
        //Nest[] nests = SerializationUtils.clone(Main.nests);

        //Υπολογίζεται η απόσταση των βομβών από τις φωλιές και το πλήθος σφηκών που σκοτώνουν
        double n=nests.length; // Μήκος πίνακα φωλιών
        double d; // απόσταση βόμβας από τη φωλιά
        double K; // πλήθος σφηκών που θα σκοτωθούν σε μια φωλιά
        int T = 0; // πλήθος σφηκών που θα σκοτωθούν σε όλες τις φωλιές, από όλες τις βόμβες μαζί
        // loop από 0 εώς των αριθμό των βομβών για να υπολογίσουμε για όλες τις βόμβες
        for(int i=0; i < Main.bombsNum; i++) {
            for (Nest nest : nests) {
                // αν δεν υπάρχουν άλλες σφήκες στη φωλιά, περνάμε στην επόμενη γιατί σε αυτή δεν θα σκοτωθεί καμία
                if (nest.getHosesNum() == 0)
                    continue;
                // υπολογισμός απόστασης βόμβας i από τη φωλιά
                if (z>0)
                    d = Math.sqrt(Math.pow( ((NumericGene)gt.get(i, 0)).intValue() - nest.getX(), 2) +
                            Math.pow( ((NumericGene)gt.get(i, 1)).intValue() - nest.getY(), 2) +
                            Math.pow( ((NumericGene)gt.get(i, 2)).intValue() - nest.getZ(), 2));
                else
                    d = Math.sqrt(Math.pow( ((NumericGene)gt.get(i, 0)).intValue() - nest.getX(), 2) +
                            Math.pow( ((NumericGene)gt.get(i, 1)).intValue() - nest.getY(), 2));
                // Υπολογισμός πλήθους σφηκών που θα σκοτωθούν στη φωλιά i από τη βόμβα j
                K = n * (FitnessCalc.dMax / (20 * d + 0.00001));
                /* Αν ο αριθμός των σφηκών που σκοτώνει η βόμβα στη φωλιά είναι μεγαλύτερος από το πλήθος των σφηκών που υπάρχουν,
                   προστίθεται στο σύνολο των σφηκών που θα σκοτωθούν η αρνητική διαφορά.
                 */
                if ((nest.getHosesNum() - (int) K) < 0)
                    K += nest.getHosesNum() - (int) K;
                // Αφαιρείται από των αριθμό των σφηκών της φωλιάς ο αριθμός αυτών που θα σκοτωθούν
                nest.setHosesNum(nest.getHosesNum() - (int) K);
                // Προστίθεται στο πλήθος των σφηκών που θα σκοτωθούν σε όλες τις φωλιές ο αριθμός των σφηκών που θα σκοτωθούν στην i φωλιά
                T += (int) K;
            }
        }
        return T;
    }

    public static void main(String[] args) {

        nests[0] = new Nest(100, 25, 65);
        nests[1] = new Nest(200, 23, 28);
        nests[2] = new Nest(327, 7, 13);
        nests[3] = new Nest(440, 95, 53);
        nests[4] = new Nest(450, 3, 3);
        nests[5] = new Nest(639, 54, 56);
        nests[6] = new Nest(650, 67, 78);
        nests[7] = new Nest(678, 32, 4);
        nests[8] = new Nest(750, 24, 76);
        nests[9] = new Nest(801, 66, 89);
        nests[10] = new Nest(945, 84, 4);
        nests[11] = new Nest(967, 34, 23);

        //jeneticsRun();
        try {
            myRun();
        } catch (CloneNotSupportedException | IOException e) {
            e.printStackTrace();
        }

    }

    private static void myRun() throws CloneNotSupportedException, IOException {
        int[][] arrayH = new int[x][y];
        Arrays.stream(nests).forEach(nest -> arrayH[nest.getX()][nest.getY()] = nest.getHosesNum());
        // Δημιουργία αρχικού πληθυσμού
        Population myPop = new Population(populationSize, initialise); // μέγεθος πληθυσμού 100 άτομα

        // Εξέλιξη του πληθυσμού
        int generationCount = 0; // μετρητής τρέχουσας γενιάς
        long startTime = System.currentTimeMillis(); // τρέχουσα ώρα για να μετρήσουμε τον χρόνο που θα τρέξει ο αλγόριθμος
        // το loop τρέχει εφόσον ο αριθμός των σφηκών που σκοτώνει ο καλύτερη τρέχουσα λύση είναι μικρότερος του συνόλου των σφηκών
        //   ή ο χρόνος εκτέλεσης είναι μικρότερος αυτού που δίνουμε(εδώ είναι 60 δευτερόλεπτα)

        while ((myPop.getFittest().getFitness() < FitnessCalc.maxFitness) && ((System.currentTimeMillis()-startTime)/1000)<=runtime) {
            generationCount++; // αύξηση μετρητή τρέχουσας γενιάς
            System.out.println("Γενιά: " + generationCount + " Ποιότητα καλύτερου ατόμου: " + myPop.getFittest().getFitness());
            myPop = Algorithm.evolvePopulation(myPop); // εξέλιξη του πληθυσμού
        }
        System.out.println("\nΛύση!");
        System.out.println("Γενιά: " + generationCount);
        System.out.println("Γονίδια: " + myPop.getFittest());
        System.out.println("Αριθμός σφηκών που σκοτώνει: " + myPop.getFittest().getFitness() + " από τις " + FitnessCalc.maxFitness);
        ArrayToHtml(myPop.getFittest().toString(), arrayH);
    }

    private static void jeneticsRun() {
        final IntRange xRange = IntRange.of(1, x);
        final IntRange yRange = IntRange.of(1, y);
        IntRange zRange = null;
        if (z > 0)  zRange = IntRange.of(1, z);

        final Chromosome<IntegerGene>[] chromosomes = new Chromosome[bombsNum];
        for (int i = 0; i < bombsNum; i++) {
            //chromosomes[i] = IntegerChromosome.of(1, x, dimensions);
            if (zRange!=null) {
                chromosomes[i] = IntegerChromosome.of(IntegerGene.of(xRange), IntegerGene.of(yRange), IntegerGene.of(zRange));
                continue;
            }
            chromosomes[i] = IntegerChromosome.of(IntegerGene.of(xRange), IntegerGene.of(yRange));
        }

        //Iterable<Chromosome<IntegerGene>> iterable = Arrays.asList(chromosomes);
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(Arrays.asList(chromosomes));


        final Selector<IntegerGene, Integer> selector = new EliteSelector<IntegerGene, Integer>(
                // Number of best individuals preserved for next generation: elites
                1, new TournamentSelector<>(tournamentSampleSize));
        Engine<IntegerGene, Integer> engine
                = Engine.builder(Main::getFitness, gtf)
                .populationSize(populationSize)
                .optimize(Optimize.MAXIMUM)
                .alterers(new Mutator<>(0.05), new MeanAlterer<>(0.03))
                .selector(selector)
                .build();

        EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();
        final Phenotype best = engine.stream()
                .limit(Limits.byFitnessThreshold (FitnessCalc.maxFitness))
                .limit(Limits.byExecutionTime ( Duration.ofSeconds(runtime)))
                .peek(statistics)
                .collect(EvolutionResult.toBestPhenotype());
        System.out.println(statistics);
        System.out.println(best);
        System.out.println(FitnessCalc.maxFitness);
    }

    // Φτιάχνει μια σελίδα html με έναν πίνακα των θέσεων των βομβών και των φωλιών
    private static void ArrayToHtml(String genes, int[][] arrayH) throws IOException {
        int[] x1 = new int[3];
        int[] y1 = new int[3];
        int z = 0;

        for(final String token : Splitter.fixedLength(14).split(genes)){
            x1[z] = (int)(((double)Integer.parseInt(token.substring(0, token.length() / 2), 2)) * FitnessCalc.factor);
            y1[z] = (int)(((double)Integer.parseInt(token.substring(token.length() / 2), 2)) * FitnessCalc.factor);
            z++;
        }
        File file = new File("Pinakas.html");
        file.delete();
        file.createNewFile();
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print("<!DOCTYPE html>\n<html>\n\n\t<head>\n\t\t<title>Πίνακας θέσεων</title>\n</head>\n\n\t<body>\n\t\t<table border = \"0\">");
        for (int i=0; i < arrayH.length; i++) {
            writer.print("\n\t\t\t<tr>");
            for (int j = 0; j < arrayH[i].length; j++) {
                /*if ((i==x1[0] && j==y1[0]) || (i==x1[1] && j==y1[1]) || (i==x1[2] && j==y1[2])) {
                    writer.print("\n\t\t\t\t<td>" + "<font color = \"red\">b</font>" + "</td>");
                    continue;
                }*/
                if ((i==x1[0] && j==y1[0])) {
                    writer.print("\n\t\t\t\t<td>" + "<font color = \"red\">1</font>" + "</td>");
                    continue;
                }
                if ((i==x1[1] && j==y1[1])) {
                    writer.print("\n\t\t\t\t<td>" + "<font color = \"red\">2</font>" + "</td>");
                    continue;
                }
                if ((i==x1[2] && j==y1[2])) {
                    writer.print("\n\t\t\t\t<td>" + "<font color = \"red\">3</font>" + "</td>");
                    continue;
                }
                writer.print("\n\t\t\t\t<td>" + ((arrayH[i][j]==0) ? " " : "<font color = \"black\">n</font>") + "</td>");
            }
            writer.print("\n\t\t\t</tr>");
        }
        writer.print("\n\t\t</table>\n\t\t<font color = \"black\">n = nests</font>\n\t\t<font color = \"red\">b = bombs</font>\n\n\t</body>\n</html>");
        writer.close();
    }
}
