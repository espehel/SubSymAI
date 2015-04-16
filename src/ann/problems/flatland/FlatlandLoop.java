package ann.problems.flatland;

import ea.core.*;
import ea.core.Settings;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    protected Phenotype onePointPhenoSpecificCrossover(Phenotype topParent, Phenotype botParent, int crossPoint) {

        FlatlandPhenotype child = new FlatlandPhenotype();

        if(topParent == botParent || Settings.CROSSOVER_RATE < Math.random()) {
            child.data = ((FlatlandPhenotype)topParent).data.clone();
            child.genotype = new Genotype(null);
            return child;
        }

        double[] data = new double[((FlatlandPhenotype)topParent).data.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = i < crossPoint ? ((FlatlandPhenotype)topParent).data[i] : ((FlatlandPhenotype)botParent).data[i];
        }
        child.data = data;
        child.genotype = new Genotype(null);
        return child;
    }

    @Override
    protected void phenoSpecificMutation(List<Phenotype> children) {
        for (Phenotype pheno : children){
            FlatlandPhenotype child = (FlatlandPhenotype) pheno;


            for (int i = 0; i < child.data.length; i++) {
                if(Math.random() < Settings.MUTATION_RATE) {
                    child.data[i] += (Math.random() - Math.random()) * 0.3;
                    if(child.data[i] > 1)
                        child.data[i] = 1.0;
                    else if(child.data[i] < -1)
                        child.data[i] = -1.0;

                }
            }

        }

    }


    @Override
    protected Phenotype generatePhenotype() {
        return new FlatlandPhenotype();
    }

    @Override
    protected void calculateFitness(Phenotype pheno) {
        simulator.testFitness((FlatlandPhenotype) pheno);

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
            //boolean[] data = new boolean[Settings.GENOTYPE_SIZE];
            double[] data = new double[Settings.GENOTYPE_SIZE];
            for (int j = 0; j < data.length; j++) {
                //data[j] = rnd.nextBoolean();
                data[j] = rnd.nextDouble();
            }
            FlatlandPhenotype pheno = new FlatlandPhenotype();
            //pheno.genotype = new Genotype(data);
            //pheno.develop();
            pheno.data = data;
            population.add(pheno);
        }
    }
    public List<Phenotype> getPopulation(){
        return population;
    }

}
