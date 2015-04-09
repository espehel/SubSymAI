package ann.problems.flatland;

import ann.core.NeuralNetwork;
import ann.problems.ProblemSimulator;
import com.sun.codemodel.internal.JForEach;
import ea.core.*;
import utils.*;
import utils.Settings;

/**
 * Created by espen on 02/04/15.
 */
@SuppressWarnings("AccessStaticViaInstance")
public class FlatlandSimulator implements ProblemSimulator {

    NeuralNetwork ann;
    FlatlandLoop ea;
    int[] flatlandContent;
    int[][] flatland;
    GUIController gui;
    private Object foodCount;


    public FlatlandSimulator(){
        ann = new NeuralNetwork();
        ea = new FlatlandLoop(this);
        flatland = new int[Settings.ann.FLATLAND_SIZE][Settings.ann.FLATLAND_SIZE];
        flatlandContent = new int[Settings.ann.FLATLAND_SIZE*Settings.ann.FLATLAND_SIZE];
    }


    @Override
    public void initialize(GUIController gui) {
        this.gui = gui;
        initiateFlatlandContent();
        //assumes three layers total and that the middle layer is the average of input and output size
        ann.buildNetwork(new int[]{Settings.ann.INPUT_SIZE,(Settings.ann.INPUT_SIZE+Settings.ann.INPUT_SIZE)/2,Settings.ann.OUTPUT_SIZE});
        Settings.ea.REPRESENTATION_SIZE = 8;
        Settings.ea.GENOTYPE_SIZE = ann.totalNetworkWeights*Settings.ea.REPRESENTATION_SIZE;
        ea.initialize(gui);
    }

    private void initiateFlatlandContent() {
        //index 0 is where the agent starts
        flatlandContent[0] = Constants.FLATLAND_CELLTYPE_EMPTY;
        for (int i = 1; i < flatlandContent.length; i++) {
            if(Math.random()<= Settings.ann.FOOD_DISTRIBUTION){
                flatlandContent[i] = Constants.FLATLAND_CELLTYPE_FOOD;
                continue;
            }
            if(Math.random()<Settings.ann.POISON_DISTRIBUTION){
                flatlandContent[i] = Constants.FLATLAND_CELLTYPE_POISON;
                continue;
            }
            flatlandContent[i] = Constants.FLATLAND_CELLTYPE_EMPTY;
        }
        /*int f=0;
        int p=0;
        int e = 0;
        for (int integer : flatlandContent){
            if(integer == Constants.FLATLAND_CELLTYPE_EMPTY)
                e++;
            if(integer == Constants.FLATLAND_CELLTYPE_FOOD)
                f++;
            if(integer == Constants.FLATLAND_CELLTYPE_POISON)
                p++;
        }
        double fr = f/100.0;
        double pr = p/(100d-f);
        double er = e/100.0;
        System.out.println(f+p+e);*/
    }
    private void generateFlatland(){
        flatland = new int[Settings.ann.FLATLAND_SIZE][Settings.ann.FLATLAND_SIZE];

        for (int i = 0; i <flatland.length; i++) {
            for (int j = 0; j < flatland.length; j++) {
                flatland[i][j] = flatlandContent[(i*flatland.length)+j];
            }
        }
    }

    @Override
    public void start() {

        for (Phenotype phenotype : ea.getPopulation()) {
            testFitness((FlatlandPhenotype) phenotype);
            phenotype.calculateFitness();
            System.out.println(phenotype.fitness);
        }

        boolean finished = false;

        while (!ea.goalAccomplished()) {

            ea.step();
            gui.updateGraph(State.bestFitness);
            if(Settings.ann.DYNAMIC)
                initiateFlatlandContent();
            ea.testFitness(ea.getPopulation());
            ea.logState();
            //System.out.println("end of loop");
        }

    }

    @Override
    public void runBestAgent() {
        FlatlandPhenotype phenotype = (FlatlandPhenotype) State.bestIndividual;
        ann.setWeights(phenotype.data);
        generateFlatland();
        phenotype.resetAgent();
        gui.updateGrid(flatland);
        gui.updateGrid(phenotype.agent);


        for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
            try {
                Thread.sleep(Settings.ea.LOOP_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            double[] input = getInput(phenotype.agent);
            double[] output = ann.feedInput(input);
            //get action from output
            double bestValue = Settings.ann.MOVE_HOLD_THRESHOLD;
            int bestAction = Constants.MOVE_HOLD;
            for (int j = 0; j < output.length; j++) {
                if(output[j]>bestValue){
                    bestValue = output[j];
                    bestAction = j;
                }

            }
            phenotype.agent.move(bestAction);
            phenotype.agent.eat(flatland[phenotype.agent.x][phenotype.agent.y]);
            flatland[phenotype.agent.x][phenotype.agent.y] = Constants.FLATLAND_CELLTYPE_EMPTY;
            gui.updateGrid(phenotype.agent);
            System.out.println(phenotype.agent);
        }
        System.out.println(phenotype.agent);
    }

    @Override
    public void generateNewContent() {
        initiateFlatlandContent();
    }

    public void testFitness(FlatlandPhenotype phenotype) {
        ann.setWeights(phenotype.data);
        generateFlatland();
        phenotype.maxFood = getFoodCount();
        phenotype.resetAgent();

        for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
            double[] input = getInput(phenotype.agent);
            //Print.array("input", input);
            double[] output = ann.feedInput(input);
            //Print.array("output" , output);
            //get action from output
            double bestValue = Settings.ann.MOVE_HOLD_THRESHOLD;
            int bestAction = Constants.MOVE_HOLD;
            for (int j = 0; j < output.length; j++) {
                if (output[j] > bestValue) {
                    bestValue = output[j];
                    bestAction = j;
                }

            }
            phenotype.agent.move(bestAction);
            phenotype.agent.eat(flatland[phenotype.agent.x][phenotype.agent.y]);
            flatland[phenotype.agent.x][phenotype.agent.y] = Constants.FLATLAND_CELLTYPE_EMPTY;
        }

    }

    private double[] getInput(Agent agent) {
        double[] input =  new double[Settings.ann.INPUT_SIZE];
        int[] proximity = new int[3];
        //front sensor
        proximity[0] = getContent(agent,agent.front);
        //left sensor
        proximity[1] = getContent(agent,agent.left);
        //right sensor
        proximity[2] = getContent(agent,agent.right);

        for (int i = 0; i < proximity.length; i++) {
            if(proximity[i] == Constants.FLATLAND_CELLTYPE_FOOD)
                input[i] = 1.0;
            else if(proximity[i]==Constants.FLATLAND_CELLTYPE_POISON)
                input[i+3] = 1.0;
        }

        return input;
    }

    private int getContent(Agent agent, Direction direction) {
        int x = agent.x + direction.x;
        int y = agent.y + direction.y;

        if(x==-1)
            x=Settings.ann.FLATLAND_SIZE-1;
        else if(x == Settings.ann.FLATLAND_SIZE)
            x=0;

        if(y==-1)
            y=Settings.ann.FLATLAND_SIZE-1;
        else if(y == Settings.ann.FLATLAND_SIZE)
            y=0;

        return flatland[x][y];
    }

    public static void main(String[] args) {
        double[] input =  new double[Settings.ann.INPUT_SIZE];
        System.out.println(input[3]);

    }

    public int getFoodCount() {
        int c = 0;
        for (int i = 0; i < flatland.length; i++) {
            for (int j = 0; j < flatland[0].length; j++) {
                if(flatland[i][j] == Constants.FLATLAND_CELLTYPE_FOOD)
                    c++;
            }
        }
        return c;
    }
}
