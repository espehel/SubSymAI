package ea.core.selection;

import ea.core.MatingPartners;
import ea.core.Settings;
import ea.core.Phenotype;
import utils.Calculate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

/**
 * Created by espen on 03/03/15.
 */
public class ParentSelection {
    public static Random random = new Random();

    public static List<MatingPartners> fitnessProportinate(List<Phenotype> population){
        List<MatingPartners> matingPool = new ArrayList<>();

        while(matingPool.size()<Settings.CHILD_POOL_SIZE / 2) {

            double totalFitness = Calculate.sumFitness(population);

            normalizePopulation(population, x -> x / totalFitness);

            MatingPartners chosenMates = chooseParent(population);
            matingPool.add(chosenMates);
        }
        return matingPool;
    }

    public static List<MatingPartners> sigmaScaling(List<Phenotype> population){
        List<MatingPartners> matingPool = new ArrayList<>();

        double avgFitness = Calculate.averageFitness(population);
        double stdDeviation = Calculate.standardDeviation(population);

        while(matingPool.size()<Settings.CHILD_POOL_SIZE /2) {

            double epsilon = 0.0000001;
            if  ( stdDeviation <= ( 0 - epsilon ) ) {
                normalizePopulation(population,x->(1 + ((x-avgFitness)/(2*stdDeviation)))/(double)population.size());
            }
            else if ( stdDeviation >= ( 0 + epsilon ) ) {
                normalizePopulation(population,x->(1 + ((x-avgFitness)/(2*stdDeviation)))/(double)population.size());
            }
            else {
                normalizePopulation(population,x->1/(double)population.size());

            }

            MatingPartners chosenMates = chooseParent(population);
            matingPool.add(chosenMates);
        }
        return matingPool;
    }

    public static List<MatingPartners> boltzmannSelection(List<Phenotype> population){
        List<MatingPartners> matingPool = new ArrayList<>();

        double avgFitness = Calculate.averageFitness(population);

        while(matingPool.size()<Settings.CHILD_POOL_SIZE /2) {

            normalizePopulation(population,x->((Math.exp(x)/Settings.TEMPERATURE) / (Math.exp(avgFitness)/Settings.TEMPERATURE) / (double)population.size()));
            MatingPartners chosenMates = chooseParent(population);
            matingPool.add(chosenMates);
        }
        return matingPool;
    }

    public static List<MatingPartners> tournamentSelection(List<Phenotype> population){
        List<MatingPartners> matingPool = new ArrayList<>();

        while(matingPool.size()<Settings.CHILD_POOL_SIZE/2) {

            MatingPartners matingPartners = new MatingPartners();

            Collections.shuffle(population);
            matingPartners.partner1 = runTournament(population);

            do {
                Collections.shuffle(population);
                matingPartners.partner2 = runTournament(population);
            } while (matingPartners.partner1 == matingPartners.partner2);

            matingPool.add(matingPartners);
        }

    return matingPool;
    }
    private static Phenotype runTournament(List<Phenotype> population) {
        Phenotype bestPhenom = null;
        for (int i = 0; i < Settings.K_VALUE; i++) {
            //group - get the best in  a group
            double bestFitness = -1;
            if (population.get(i).fitness > bestFitness) {
                bestPhenom = population.get(i);
                bestFitness = bestPhenom.fitness;
            }
            //Z values acts as epsilon and decides if the first element(ranomdly placed first) gets to win instead of winnner
            if (Math.random() < Settings.EPSILON)
                bestPhenom = population.get(random.nextInt(Settings.K_VALUE));
        }
        return bestPhenom;
    }


    private static void normalizePopulation(List<Phenotype> population, DoubleUnaryOperator normalizeFunction){

        double lower = 0;
        double upper = 0;
        for(int i = 0; i < population.size(); i++) {

            Phenotype parent = population.get(i);
            parent.normalizedFitness = normalizeFunction.applyAsDouble(parent.fitness);
            upper+= parent.normalizedFitness;
            parent.setRange(upper,lower);
            lower+= parent.normalizedFitness;
        }
    }
    private static MatingPartners chooseParent(List<Phenotype> population) {
        MatingPartners chosenMates = new MatingPartners();
        double randomValue = Math.random();
        for (Phenotype parent : population){
            if(parent.inRange(randomValue)){
                chosenMates.partner1 = parent;
            }
        }
        do{
            randomValue = Math.random();
            for (Phenotype parent : population){
                if(parent.inRange(randomValue)){
                    chosenMates.partner2 = parent;
                }
            }
        }while(chosenMates.partner1 == chosenMates.partner2);
        return chosenMates;
    }
}
