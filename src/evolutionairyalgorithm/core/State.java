package evolutionairyalgorithm.core;

/**
 * Created by espen on 09/03/15.
 */
public class State {
    public static int generationNumber = -1;
    public static double bestFitness = -1;
    public static double averageFitness = -1;
    public static double standardDeviation = -1;
    public static Phenotype bestIndividual = null;


    public static String log() {
        return "State{" +
                "generationNumber=" + generationNumber +
                ", bestFitness=" + bestFitness +
                ", averageFitness=" + averageFitness +
                ", standardDeviation=" + standardDeviation +
                ", bestIndividual=" + bestIndividual +
                '}';
    }

    public static void reset(){
        generationNumber = -1;
        bestFitness = -1;
        averageFitness = -1;
        standardDeviation = -1;
        bestIndividual = null;
    }
}
