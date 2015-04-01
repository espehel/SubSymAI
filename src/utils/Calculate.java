package utils;

import evolutionairyalgorithm.core.Phenotype;

import java.util.List;

/**
 * Created by espen on 10/03/15.
 */
public class Calculate {

    public static double sumFitness(List<Phenotype> population) {
        double sum = 0;
        for (Phenotype pheno : population){
            sum += pheno.fitness;
        }
        return sum;
    }

    public static double standardDeviation(List<Phenotype> population) {

        double average = averageFitness(population);

        double devSum = 0;
        for (Phenotype pheno : population){
            devSum += Math.pow((pheno.fitness - average), 2);
        }
        return Math.sqrt(devSum/(double)population.size());
    }
    public static double averageFitness(List<Phenotype> population) {
        return sumFitness(population) / (double)population.size();
    }
}
