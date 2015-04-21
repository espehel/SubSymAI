package ann.problems.tracker;

import ann.core.*;
import ea.core.*;
import ea.core.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by espen on 14/04/15.
 */
public class TrackerLoop extends EvolutionaryLoop {
    private TrackerSimulator simulator;

    public TrackerLoop(TrackerSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    protected Phenotype onePointPhenoSpecificCrossover(Phenotype topParent, Phenotype botParent, int crossPoint) {

        return onePointCrossover(topParent, botParent, crossPoint*Settings.REPRESENTATION_SIZE);
    }

    @Override
    protected void phenoSpecificMutation(List<Phenotype> children) {
        for (Phenotype pheno : children){
            TrackerPhenotype child = (TrackerPhenotype) pheno;

            for (int i = 0; i < child.data.length; i++) {
                if(Math.random() < Settings.MUTATION_RATE)
                    child.data[i] += (Math.random()-Math.random())*0.3;
            }

        }

    }


    @Override
    protected Phenotype generatePhenotype() {
        return new TrackerPhenotype();
    }

    @Override
    protected void calculateFitness(Phenotype pheno) {
        simulator.testFitness((TrackerPhenotype) pheno);

        pheno.calculateFitness();
    }

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
            Phenotype pheno = new TrackerPhenotype();
            pheno.genotype = new Genotype(data);
            pheno.develop();
            population.add(pheno);
        }
    }
    public List<Phenotype> getPopulation(){
        return population;
    }

}
