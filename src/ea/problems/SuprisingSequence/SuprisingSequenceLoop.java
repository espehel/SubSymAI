package ea.problems.SuprisingSequence;

import ea.core.*;

import java.util.ArrayList;
import java.util.Random;

public class SuprisingSequenceLoop extends EvolutionaryLoop {

    @Override
    protected Phenotype generatePhenotype() {
        return new SuprisingSequencePhenotype();
    }

    @Override
    protected double calculateFitness(Phenotype pheno) {
        return pheno.calculateFitness();
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
        //genom size = log(2,S)*L
        Settings.REPRESENTATION_SIZE = (int)Math.ceil(Math.log(Settings.Z_VALUE) / Math.log(2));
        Settings.GENOTYPE_SIZE = Settings.REPRESENTATION_SIZE * Settings.GENOTYPE_SIZE;

        for (int i = 0; i < Settings.ADULT_POOL_SIZE; i++) {
            boolean[] data = new boolean[Settings.GENOTYPE_SIZE];
            //BitSet bitSet = new BitSet();
            for (int j = 0; j < data.length; j++) {
                data[j] = rnd.nextBoolean();
                //bitSet.set(j,data[j]);
            }
            /*for (int j = 0; j < data.length; j++) {
                System.out.print(data[j]+", ");
            }
            System.out.println();
            for (int j = 0; j < bitSet.size(); j++) {
                System.out.print(bitSet.get(j)+", ");
            }
            System.out.println();
            System.out.println(bitSet);*/


            Phenotype pheno = new SuprisingSequencePhenotype();
            pheno.genotype = new Genotype(data);
            pheno.develop();
            population.add(pheno);
        }
    }

    public static void main(String[] args) {
        SuprisingSequenceLoop loop = new SuprisingSequenceLoop();
        loop.initializePopulation();
        ;
    }


}