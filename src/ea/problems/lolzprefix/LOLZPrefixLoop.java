package ea.problems.lolzprefix;

import ea.core.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by espen on 11/03/15.
 */
public class LOLZPrefixLoop extends EvolutionaryLoop{

    @Override
    protected Phenotype generatePhenotype() {
        return new LOLZPrefixPhenotype();
    }

    @Override
    protected double calculateFitness(Phenotype pheno) {
        int f = 0;
        int l = ((LOLZPrefixPhenotype)pheno).data.get(0);
        for (Integer n : ((LOLZPrefixPhenotype)pheno).data){
            if(n.intValue() == l)
                f+=n;
            else
                break;
        }
        f = f>(int)Settings.Z_VALUE && l==0 ? (int)Settings.Z_VALUE : f;

        return f / (double) Settings.GENOTYPE_SIZE;
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
