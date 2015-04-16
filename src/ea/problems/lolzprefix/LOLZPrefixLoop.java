package ea.problems.lolzprefix;

import ea.core.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by espen on 11/03/15.
 */
public class LOLZPrefixLoop extends EvolutionaryLoop{

    @Override
    protected Phenotype onePointPhenoSpecificCrossover(Phenotype partner1, Phenotype partner2, int crossPointS) {
        throw new NotImplementedException();
    }

    @Override
    protected void phenoSpecificMutation(List<Phenotype> children) {
        throw new NotImplementedException();
    }

    @Override
    protected Phenotype generatePhenotype() {
        return new LOLZPrefixPhenotype();
    }

    @Override
    protected void calculateFitness(Phenotype pheno) {
        pheno.calculateFitness();
    }

    @Override
    protected boolean goalAccomplished() {
        if(State.generationNumber>= Settings.MAX_GENERATIONS)
            return true;
        for (Phenotype phenotype : population){
            if(phenotype.fitness == 1)
                return true;
        }
        return false;
    }

    @Override
    protected void initializePopulation() {
        population = new ArrayList<>();
        Random rnd = new Random();

        for (int i = 0; i < Settings.ADULT_POOL_SIZE; i++) {
            boolean[] data = new boolean[Settings.GENOTYPE_SIZE];
            for (int j = 0; j < data.length; j++) {
                data[j] = rnd.nextBoolean();
            }

            Phenotype pheno = new LOLZPrefixPhenotype();
            pheno.genotype = new Genotype(data);
            pheno.develop();
            population.add(pheno);
        }
    }
}
