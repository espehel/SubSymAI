package utils;

import ea.core.Phenotype;

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
    public static double sigmoid(double x, double gain){
        double result =  1.0 / (1.0 + Math.exp(-gain*x));
        return result;
    }
    public static double sigmoid(double x){
        double result = 1.0 / (1.0 + Math.exp(-x));
        return result;
    }

    public static Integer convertToInteger(boolean[] symbol) {
        int n = 0;
        for (boolean b : symbol)
            n = (n << 1) | (b ? 1 : 0);
        return n;
    }
}
