package ann.problems.tracker;

import ann.problems.ProblemSimulator;
import ea.core.Phenotype;
import ea.core.State;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.Constants;
import utils.GUIController;
import utils.Settings;

import java.util.Random;

/**
 * Created by espen on 14/04/15.
 */
@SuppressWarnings("AccessStaticViaInstance")
public class TrackerSimulator extends ProblemSimulator {

    public static final int width = 30;
    public static final int height = 15;

    TrackerBlock tracker;
    FallingBlock fallingBlock;
    Random rnd;
    TrackerLoop ea;



    @Override
    public void initialize(GUIController gui) {
        rnd = new Random();
        this.gui = gui;
        //TODO: fix like it is in the project desription
        ann.buildNetwork(new int[]{Settings.ann.INPUT_SIZE,(Settings.ann.INPUT_SIZE+Settings.ann.OUTPUT_SIZE)/2,Settings.ann.OUTPUT_SIZE});
        Settings.ea.REPRESENTATION_SIZE = 32;
        Settings.ea.GENOTYPE_SIZE = ann.totalNetworkWeights*Settings.ea.REPRESENTATION_SIZE;
        ea.initialize(gui);
    }

    @Override
    public void start() {

        for (Phenotype phenotype : ea.getPopulation()) {
            testFitness((TrackerPhenotype) phenotype);
            phenotype.calculateFitness();
            System.out.println(phenotype.fitness);
        }


        while (!ea.goalAccomplished()) {

            ea.step();
            gui.updateGraph(State.bestFitness);
            //ea.testFitness(ea.getPopulation());
            ea.logState();
            //System.out.println("end of loop");
        }
    }

    @Override
    public void runBestAgent() {

    }

    @Override
    public void generateNewContent() {
        throw new NotImplementedException();
    }

    public void testFitness(TrackerPhenotype phenotype) {

        ann.setWeights(phenotype.data);
        phenotype.biggerBlocks = 0;
        phenotype.smallerBlocks  = 0;
        phenotype.crashes = 0;
        phenotype.captures = 0;

        for (int c = 0; c< Settings.ann.SERIES_COUNT;c++) {

            tracker.reset();

            for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
                double[] input = getInput();
                //Print.array("input", input);
                double[] output = ann.feedInput(input);
                //Print.array("output" , output);
                //get action from output
                int bestAction = phenotype.getAction(output);

                tracker.move(bestAction,output[bestAction]);
                //TODO: figure out when tracker should move adn when fallingblock should move when relateing to the visualization


                fallingBlock.y++;

                if(fallingBlock.y == 14){
                    int event = getEvent(fallingBlock,tracker);
                    if(event != Constants.EVENT_AVOIDANCE && fallingBlock.size>=tracker.size){
                        phenotype.crashes++;
                    }
                    else if(event == Constants.EVENT_CAPTURE && fallingBlock.size < tracker.size){
                        phenotype.captures++;
                    }

                    fallingBlock = generateNewFallingBlock();
                }
            }
        }

    }

    private FallingBlock generateNewFallingBlock() {
        int x = rnd.nextInt(30);
        int size = rnd.nextInt(6)+1;
        return new FallingBlock(x,0,size);
    }

    private int getEvent(FallingBlock fallingBlock, TrackerBlock tracker) {

        boolean capture = true;
        for (int i = 0; i < fallingBlock.size; i++) {
            if(!tracker.contains(fallingBlock.x+i)){
                capture = false;
                break;
            }
        }
        if(capture)
            return Constants.EVENT_CAPTURE;

        boolean avoided = true;
        for (int i = 0; i < tracker.size; i++) {
            if(fallingBlock.contains(tracker.x+i)) {
                avoided = false;
                break;
            }
        }
        if(avoided)
            return Constants.EVENT_AVOIDANCE;
        else
            return Constants.EVENT_NONE;
    }

    private double[] getInput() {
        double[] sensors = new double[tracker.size];

        for (int i = 0; i < sensors.length; i++) {
            //TODO: fix wrap around
            if(fallingBlock.contains(tracker.x + i))
                sensors[i] = 1d;
        }
        return sensors;
    }

}
