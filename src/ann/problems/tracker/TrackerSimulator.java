package ann.problems.tracker;

import ann.problems.ProblemSimulator;
import ea.core.Phenotype;
import ea.core.State;
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

    public TrackerSimulator(){
        ea = new TrackerLoop(this);
        tracker = new TrackerBlock();
        fallingBlock = new FallingBlock();
    }



    @Override
    public void initialize(GUIController gui) {
        System.out.println("init");
        rnd = new Random();
        this.gui = gui;
        Settings.ea.OPERATOR_MUTATION = Constants.MUTATION_BIT_STRING;
        Settings.ann.STEP_COUNT = 600;
        Settings.ann.INPUT_SIZE = 5;
        Settings.ann.OUTPUT_SIZE = 2;
        Settings.ann.CTRNN = true;
        ann.buildNetwork(new int[]{Settings.ann.INPUT_SIZE,2,Settings.ann.OUTPUT_SIZE});
        Settings.ea.REPRESENTATION_SIZE = 8;
        //addeding timeconstant, gains and biasweight for 4 neruons
        Settings.ea.GENOTYPE_SIZE = (ann.totalNetworkWeights+(3*4))*Settings.ea.REPRESENTATION_SIZE;
        ea.initialize(gui);
    }

    @Override
    public void start() {
        System.out.println("start");

        for (Phenotype phenotype : ea.getPopulation()) {
            testFitness((TrackerPhenotype) phenotype);
            phenotype.calculateFitness();
            System.out.println(phenotype.fitness);
        }


        while (!ea.goalAccomplished()) {
            //System.out.println("step");

            ea.step();
            ea.logState();
            gui.updateGraph(State.bestFitness);
            //ea.testFitness(ea.getPopulation());
            //System.out.println("end of loop");
        }
    }

    @Override
    public void runBestAgent() {
        TrackerPhenotype phenotype = (TrackerPhenotype) State.bestIndividual;

        ann.setWeights(phenotype.getconnectionWeights(), phenotype.getBiasWeights(),phenotype.getGains(),phenotype.getTimeConstants());
        phenotype.crashes = 0;
        phenotype.captures = 0;
        resetFallingBlock();
        tracker.reset();


            for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
                double[] input = getInput();
                //Print.array("input", input);
                double[] output = ann.feedInput(input,true);
                //Print.array("output" , output);
                //get action from output
                int bestAction = phenotype.getAction(output);

                tracker.move(bestAction,output[bestAction]);
                gui.updateGrid(tracker, fallingBlock);
                try {
                    Thread.sleep(Settings.ea.LOOP_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                fallingBlock.y++;

                if(fallingBlock.y == 14){
                    int event = getEvent(fallingBlock,tracker);
                    if(event != Constants.EVENT_AVOIDANCE && fallingBlock.size>=tracker.size){
                        phenotype.crashes++;
                    }
                    else if(event == Constants.EVENT_CAPTURE && fallingBlock.size < tracker.size){
                        phenotype.captures++;
                    }
                    else if(event == Constants.EVENT_AVOIDANCE)
                        phenotype.avoided++;

                    resetFallingBlock();
                }
            }
        System.out.println("crashes: " + phenotype.crashes + ", Captrues: " + phenotype.captures);
    }

    @Override
    public void generateNewContent() {
    }

    public void testFitness(TrackerPhenotype phenotype) {
        //System.out.println("test fitness");

        ann.setWeights(phenotype.getconnectionWeights(), phenotype.getBiasWeights(),phenotype.getGains(),phenotype.getTimeConstants());
        phenotype.biggerBlocks = 0;
        phenotype.smallerBlocks  = 0;
        phenotype.crashes = 0;
        phenotype.captures = 0;
        //tracker = new TrackerBlock();

        //for (int c = 0; c< Settings.ann.SERIES_COUNT;c++) {
        tracker.reset();
        resetFallingBlock();

            for (int i = 0; i < Settings.ann.STEP_COUNT; i++) {
                double[] input = getInput();
                //Print.array("input", input);
                double[] output = ann.feedInput(input,true);
                //Print.array("output" , output);
                //get action from output
                int bestAction = phenotype.getAction(output);

                tracker.move(bestAction,output[bestAction]);


                fallingBlock.y++;

                if(fallingBlock.y == 14){

                    if(fallingBlock.size<4)
                        phenotype.smallerBlocks++;
                    else
                    phenotype.biggerBlocks++;

                    int event = getEvent(fallingBlock,tracker);
                    if(event != Constants.EVENT_AVOIDANCE && fallingBlock.size>=tracker.size){
                        phenotype.crashes++;
                    }
                    else if(event == Constants.EVENT_CAPTURE && fallingBlock.size < tracker.size){
                        phenotype.captures++;
                    }
                    else if(event == Constants.EVENT_AVOIDANCE)
                        phenotype.avoided++;

                    resetFallingBlock();
                }
            }
        //}

    }

    private void resetFallingBlock() {
        int x = rnd.nextInt(30);
        int size = rnd.nextInt(6)+1;
        fallingBlock.reset(x, 0, size);
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
            if(fallingBlock.contains(tracker.x + i))
                sensors[i] = 1d;
        }
        return sensors;
    }

}
