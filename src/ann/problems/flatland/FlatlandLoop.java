package ann.problems.flatland;

import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceNodeSetData;
import ea.core.*;
import ea.core.Settings;
import ea.problems.SuprisingSequence.SuprisingSequencePhenotype;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by espen on 02/04/15.
 */
public class FlatlandLoop extends EvolutionaryLoop {

    private FlatlandSimulator simulator;

    public FlatlandLoop(FlatlandSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    protected Phenotype generatePhenotype() {
        return new FlatlandPhenotype();
    }

    /*@Override
    protected double calculateFitness(Phenotype pheno) {
        simulator.testFitness((FlatlandPhenotype) pheno);

        return pheno.calculateFitness();
    }*/

    @Override
    protected boolean goalAccomplished() {
        return State.generationNumber >= Settings.MAX_GENERATIONS;
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
            Phenotype pheno = new FlatlandPhenotype();
            pheno.genotype = new Genotype(data);
            pheno.develop();
            population.add(pheno);
        }
    }
    public List<Phenotype> getPopulation(){
        return population;
    }
}
