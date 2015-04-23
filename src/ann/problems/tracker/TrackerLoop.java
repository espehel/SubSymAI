package ann.problems.tracker;

import ann.core.*;
import ann.problems.flatland.FlatlandPhenotype;
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
        //return onePointCrossover(topParent, botParent, crossPoint*Settings.REPRESENTATION_SIZE);

        TrackerPhenotype child = new TrackerPhenotype();


        if(topParent == botParent || Settings.CROSSOVER_RATE < Math.random()) {
            child.data = ((TrackerPhenotype)topParent).data.clone();
            child.genotype = new Genotype(null);
            return child;
        }

        double[] data = new double[((TrackerPhenotype)topParent).data.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = i < crossPoint ? ((TrackerPhenotype)topParent).data[i] : ((TrackerPhenotype)botParent).data[i];
        }
        child.data = data;
        child.genotype = new Genotype(null);
        //child.phenoDevelop();
        return child;
    }

    @Override
    protected void phenoSpecificMutation(List<Phenotype> children) {
        for (Phenotype pheno : children){
            TrackerPhenotype child = (TrackerPhenotype) pheno;

            for (int i = 0; i < child.data.length; i++) {
                if(Math.random() < Settings.MUTATION_RATE)
                    child.data[i] += (Math.random()-Math.random())*0.3;
                    if(child.data[i]>1)
                        child.data[i] = 1;
                    else if(child.data[i]<0)
                        child.data[i] =0;
            }
            child.phenoDevelop();
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
