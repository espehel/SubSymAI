package ann.problems.tracker;

import ann.problems.ProblemSimulator;
import com.sun.deploy.security.SessionCertStore;
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
        Settings.ann.SCENARIO = Constants.SCENARIO_NO_WRAP;
        Settings.ea.OPERATOR_MUTATION = Constants.MUTATION_BIT_STRING;
        Settings.ann.STEP_COUNT = 600;
        Settings.ann.CTRNN = true;

        switch (Settings.ann.SCENARIO){
            case Constants.SCENARIO_NO_WRAP : initNoWrapANN(); break;
            case Constants.SCENARIO_WRAP : initWrapANN(); break;
            case Constants.SCENARIO_POLL_W_WRAP : initPollWWrapANN(); break;
        }
        Settings.ea.REPRESENTATION_SIZE = 8;
        //addeding timeconstant, gains and biasweight for the number of neurons with weights neruons
        Settings.ea.GENOTYPE_SIZE = (ann.totalNetworkWeights+(3*TrackerPhenotype.neuronCount))*Settings.ea.REPRESENTATION_SIZE;
        ea.initialize(gui);
    }

    private void initNoWrapANN() {
        Settings.ann.INPUT_SIZE = 7;
        Settings.ann.OUTPUT_SIZE = 2;
        TrackerPhenotype.neuronCount = 4;
        Settings.ann.WRAP_AROUND = false;
        ann.buildNetwork(new int[]{Settings.ann.INPUT_SIZE,2,Settings.ann.OUTPUT_SIZE});
    }

    private void initPollWWrapANN() {
        Settings.ann.INPUT_SIZE = 5;
        Settings.ann.OUTPUT_SIZE = 3;
        TrackerPhenotype.neuronCount = 5;
        Settings.ann.WRAP_AROUND = true;
        ann.buildNetwork(new int[]{Settings.ann.INPUT_SIZE,2,Settings.ann.OUTPUT_SIZE});
    }

    private void initWrapANN() {
        Settings.ann.INPUT_SIZE = 5;
        Settings.ann.OUTPUT_SIZE = 2;
        TrackerPhenotype.neuronCount = 4;
        Settings.ann.WRAP_AROUND = true;
        ann.buildNetwork(new int[]{Settings.ann.INPUT_SIZE,2,Settings.ann.OUTPUT_SIZE});
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

                if(bestAction == Constants.TRACKER_POLL){
                    fallingBlock.y=13;
                    tracker.polled=true;
                }
                else
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
                        System.out.println(i+": CRASH");
                    }
                    else if(event == Constants.EVENT_CAPTURE && fallingBlock.size < tracker.size){
                        phenotype.captures++;
                        System.out.println(i+": CAPTURE");
                    }
                    else if(event == Constants.EVENT_AVOIDANCE && fallingBlock.size >= tracker.size) {
                        phenotype.avoided++;
                        System.out.println(i+": AVOID");
                    }

                    resetFallingBlock();
                    tracker.polled=false;
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

                if(bestAction == Constants.TRACKER_POLL){
                    fallingBlock.y = 13;
                }
                //TODO: tweak magnitude limits
                else
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
                    else if(event == Constants.EVENT_AVOIDANCE && fallingBlock.size >= tracker.size) {
                        phenotype.avoided++;
                    }

                    resetFallingBlock();
                }
            }
        //}

    }

    private void resetFallingBlock() {
        int size = rnd.nextInt(6)+1;
        int x;
        if(Settings.ann.WRAP_AROUND)
            x = rnd.nextInt(TrackerSimulator.width);
        else
            x = rnd.nextInt(TrackerSimulator.width-size);

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
        double[] sensors;
        if(Settings.ann.SCENARIO == Constants.SCENARIO_NO_WRAP){
            sensors = new double[tracker.size+2];
            sensors[tracker.size] = tracker.x == 0 ? 1 : 0;
            sensors[tracker.size+1] = tracker.x+tracker.size == TrackerSimulator.width ? 1 : 0;
        }else
            sensors = new double[tracker.size];

        for (int i = 0; i < tracker.size; i++) {
            if(fallingBlock.contains(tracker.x + i))
                sensors[i] = 1d;
        }
        return sensors;
    }

}
