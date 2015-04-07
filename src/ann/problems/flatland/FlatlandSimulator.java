package ann.problems.flatland;

import ann.core.NeuralNetwork;
import ann.problems.ProblemSimulator;
import ea.core.Phenotype;
import ea.core.State;
import utils.*;

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
            if(Math.random()< Settings.ann.FOOD_DISTRIBUTION){
                flatlandContent[i] = Constants.FLATLAND_CELLTYPE_FOOD;

                continue;
            }
            if(Math.random()<Settings.ann.POISON_DISTRIBUTION){
                flatlandContent[i] = Constants.FLATLAND_CELLTYPE_POISON;

                continue;
            }
            flatlandContent[i] = Constants.FLATLAND_CELLTYPE_EMPTY;

        }
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

        while (Settings.ea.RUNNING) {

            ea.step();
            ea.logState();
            gui.updateGraph(State.bestFitness);
            if(Settings.ann.DYNAMIC)
                initiateFlatlandContent();
            //System.out.println("end of loop");
        }

    }

    @Override
    public int[][] getBoardData() {
        initiateFlatlandContent();
        generateFlatland();
        return flatland;
    }

    @Override
    public void testAgent(Agent agent) {
        ann.setWeights(((FlatlandPhenotype)State.bestIndividual).data);
        generateFlatland();

        for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
            double[] input = getInput(agent);
            double[] output = ann.feedInput(input);
            //get action from output
            double bestValue = Settings.ann.MOVE_HOLD_THRESHOLD;
            int bestAction = Constants.MOVE_HOLD;
            for (int j = 0; j < output.length; j++) {
                if(output[i]>bestValue){
                    bestValue = output[i];
                    bestAction = i;
                }

            }
            agent.move(bestAction);
            agent.eat(flatland[agent.x][agent.y]);
            flatland[agent.x][agent.y] = Constants.FLATLAND_CELLTYPE_EMPTY;
            gui.updateGrid(agent);
        }
    }

    public void testFitness(FlatlandPhenotype phenotype) {
        ann.setWeights(phenotype.data);
        generateFlatland();
        phenotype.maxFood = getFoodCount();

        Agent agent = new Agent();
        for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
            double[] input = getInput(agent);
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
            agent.move(bestAction);
            agent.eat(flatland[agent.x][agent.y]);
            flatland[agent.x][agent.y] = Constants.FLATLAND_CELLTYPE_EMPTY;
        }
        phenotype.poisonCount = agent.poisonCount;
        phenotype.foodCount = agent.foodCount;

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
