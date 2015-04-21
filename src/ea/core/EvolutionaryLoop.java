package ea.core;

import ea.core.selection.AdultSelection;
import ea.core.selection.ParentSelection;
import utils.Calculate;
import utils.Constants;
import utils.GUIController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by espen on 03/03/15.
 */
public abstract class EvolutionaryLoop {

    protected List<Phenotype> population;
    GUIController gui;

    public void initialize(GUIController gui) {
        this.gui = gui;
        initializePopulation();

    }

    public void run() {
        List<MatingPartners> matingPool;
        List<Phenotype> children;

        testFitness(population);
        logState();
        gui.updateGraph(State.bestFitness);

        while(!goalAccomplished() && Settings.RUNNING) {
            matingPool = performParentSelection();
            //System.out.println("1: "+matingPool.size());
            children = reproduction(matingPool);
            //System.out.println("2: "+children.size());
            testFitness(children);
            population = performAdultSelection(population, children);
            //System.out.println("3: "+population.size());
            logState();
            gui.updateGraph(State.bestFitness);
            try {
                Thread.sleep(Settings.LOOP_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * population must be initiated and tested before performing a step
     *
     * @return if the current step acomplished the goal to finish
     */
    public boolean step(){
        List<MatingPartners> matingPool;
        List<Phenotype> children;

            matingPool = performParentSelection();
            //System.out.println("1: "+matingPool.size());
            children = reproduction(matingPool);
            //System.out.println("2: "+children.size());
            testFitness(children);
            population = performAdultSelection(population, children);
            //System.out.println("3: "+population.size());

        return goalAccomplished();
    }

    public void logState() {
        State.generationNumber++;
        State.averageFitness = Calculate.averageFitness(population);
        State.standardDeviation = Calculate.standardDeviation(population);

        //find best
        Phenotype bestPhenom = null;
        double bestFitness = -1;
        for (int i = 0; i < population.size(); i++) {
            if(population.get(i).fitness>bestFitness){
                bestPhenom = population.get(i);
                bestFitness = bestPhenom.fitness;
            }
        }
        State.bestFitness = bestFitness;
        State.bestIndividual = bestPhenom;

        System.out.println(State.log());
    }

    private List<Phenotype> reproduction(List<MatingPartners> matingPool) {
        List<Phenotype> children = new ArrayList<>();

        for (int i = 0; i < matingPool.size(); i++) {

            /*
            if(matingPool.get(i).samePartner() || Settings.CROSSOVER_RATE > Math.random()){
                Phenotype baby1 = generatePhenotype();
                Phenotype baby2 = generatePhenotype();
                baby1.genotype = new Genotype(matingPool.get(i).partner1.genotype.geno.clone());
                baby2.genotype = new Genotype(matingPool.get(i).partner2.genotype.geno.clone());
                baby1.develop();
                baby2.develop();
                children.add(baby1);
                children.add(baby2);
                continue;
            }
            */


            switch (Settings.OPERATOR_CROSSOVER) {
                case Constants.CROSSOVER_ONE_POINT:
                    int crossPoint = new Random().nextInt(Settings.GENOTYPE_SIZE);
                    children.add(onePointCrossover(matingPool.get(i).partner1,matingPool.get(i).partner2,crossPoint));
                    children.add(onePointCrossover(matingPool.get(i).partner2,matingPool.get(i).partner1,crossPoint));
                    break;
                case Constants.CROSSOVER_TWO_POINT:
                    int crossPoint1 = new Random().nextInt(Settings.GENOTYPE_SIZE);
                    int crossPoint2 = (new Random().nextInt(Settings.GENOTYPE_SIZE-crossPoint1) + crossPoint1);
                    children.add(twoPointCrossover(matingPool.get(i).partner1,matingPool.get(i).partner2,crossPoint1,crossPoint2));
                    children.add(twoPointCrossover(matingPool.get(i).partner2,matingPool.get(i).partner1, crossPoint1, crossPoint2));
                    break;
                case Constants.CROSSOVER_ONE_POINT_PHENO_SPECIFIC:
                    int crossPointS = new Random().nextInt(Settings.GENOTYPE_SIZE/Settings.REPRESENTATION_SIZE);
                    children.add(onePointPhenoSpecificCrossover(matingPool.get(i).partner1, matingPool.get(i).partner2, crossPointS));
                    children.add(onePointPhenoSpecificCrossover(matingPool.get(i).partner2, matingPool.get(i).partner1, crossPointS));
                    break;
            }
        }

        switch (Settings.OPERATOR_MUTATION){
            case Constants.MUTATION_BIT_STRING:
                bitStringMutation(children);
                break;
            case Constants.MUTATION_PROBABILITY:
                singleProbabilityMutation(children);
                break;
            case Constants.MUTATION_PHENOSPECIFIC:
                phenoSpecificMutation(children);
                break;
        }

    return children;
    }

    protected abstract Phenotype onePointPhenoSpecificCrossover(Phenotype partner1, Phenotype partner2, int crossPointS);


    protected abstract void phenoSpecificMutation(List<Phenotype> children);

    private void singleProbabilityMutation(List<Phenotype> children) {
        for (Phenotype child : children){
            if(Math.random()<Settings.MUTATION_RATE){
                int i = new Random().nextInt(Settings.GENOTYPE_SIZE);
                child.genotype.geno[i] = !child.genotype.geno[i];
            }
            child.develop();
        }
    }

    private void bitStringMutation(List<Phenotype> children) {
        for (Phenotype child : children){
            if(Math.random() < Settings.MUTATION_RATE) {
                child.develop();
                continue;
            }
            double probability = (1/(double)Settings.GENOTYPE_SIZE);
            for (int i = 0; i < Settings.GENOTYPE_SIZE; i++) {
                if(Math.random()<probability)
                    child.genotype.geno[i] = !child.genotype.geno[i];
            }
            child.develop();
        }
    }

    private Phenotype twoPointCrossover(Phenotype topParent, Phenotype botParent, int crossPoint1, int crossPoint2) {


        Phenotype child = generatePhenotype();
        boolean[] data = new boolean[Settings.GENOTYPE_SIZE];
        for (int i = 0; i < Settings.GENOTYPE_SIZE; i++) {
            if(i < crossPoint1)
                data[i] = topParent.genotype.geno[i];
            else if(i < crossPoint2)
                data[i] = botParent.genotype.geno[i];
            else
                data[i] = topParent.genotype.geno[i];
        }
        child.genotype = new Genotype(data);
        return child;
    }

    protected Phenotype onePointCrossover(Phenotype topParent, Phenotype botParent, int crossPoint) {

        Phenotype child = generatePhenotype();
        boolean[] data = new boolean[Settings.GENOTYPE_SIZE];
        boolean crossover = Math.random()<Settings.CROSSOVER_RATE && topParent!=botParent;
        for (int i = 0; i < Settings.GENOTYPE_SIZE; i++) {
            if(crossover)
                data[i] = i < crossPoint ? topParent.genotype.geno[i] : botParent.genotype.geno[i];
            else
                data[i] = topParent.genotype.geno[i];
        }
        child.genotype = new Genotype(data);
        return child;
    }

    /**
     * Lets the subclasses creates phenotypess that fits to their specific problem
     * @return a new instance which is a subclass of Phenotype
     */
    protected abstract Phenotype generatePhenotype();

    public void testFitness(List<Phenotype> phenotypes) {
        for (Phenotype pheno : phenotypes){
            calculateFitness(pheno);
            //pheno.calculateFitness();
        }
    }

    protected abstract void calculateFitness(Phenotype pheno);

    private List<MatingPartners> performParentSelection() {

        if(Settings.PARENT_SELECTION_SIGMASCALING)
            return ParentSelection.sigmaScaling(population);
        else if(Settings.PARENT_SELECTION_TOURNAMENT)
            return ParentSelection.tournamentSelection(population);
        else if(Settings.PARENT_SELECTION_BOLTZMANN) {
            List<MatingPartners> parents = ParentSelection.boltzmannSelection(population);
            Settings.TEMPERATURE = Settings.TEMPERATURE>=1 ? Settings.TEMPERATURE - Settings.EPSILON : 1;
            return parents;
        }
        else
            return ParentSelection.fitnessProportinate(population);
    }
    private List<Phenotype> performAdultSelection(List<Phenotype> parents, List<Phenotype> children) {
        if(Settings.ADULT_SELECTION_GENERATIONMIXING)
            return AdultSelection.GenerationalMixing(parents,children);
        else if(Settings.ADULT_SELECTION_OVERPRODUCTION)
            return AdultSelection.overProduction(parents, children);
        else
            return AdultSelection.fullGenerationaleReplacement(parents, children);
    }


    protected abstract boolean goalAccomplished();


    protected abstract void initializePopulation();
}
