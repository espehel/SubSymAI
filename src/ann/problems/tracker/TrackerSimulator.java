package ann.problems.tracker;

import ann.problems.ProblemSimulator;
import com.sun.deploy.security.SessionCertStore;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import ea.core.Phenotype;
import ea.core.State;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.Constants;
import utils.GUIController;
import utils.Settings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

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
        Settings.ann.SCENARIO = Constants.SCENARIO_WRAP;
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
        NumberFormat formatter = new DecimalFormat("#0.0000");

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

                System.out.println(bestAction + ": " + formatter.format(output[bestAction]));
                gui.updateGrid(tracker, fallingBlock);
                try {
                    Thread.sleep(Settings.ea.LOOP_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                fallingBlock.y++;

                if(fallingBlock.y == 14){
                    int event = getEvent(fallingBlock,tracker);
                    String eText = recordEvent(phenotype,event);
                    //System.out.println(eText);

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
                    recordEvent(phenotype,event);


                    resetFallingBlock();
                }
            }
        //}

    }

    public String recordEvent(TrackerPhenotype phenotype, int event){
        if(event != Constants.EVENT_AVOIDANCE && fallingBlock.size>=tracker.size){
            phenotype.crashes++;
            return "CRASH";
        }
        if(event == Constants.EVENT_CAPTURE && fallingBlock.size < tracker.size){
            phenotype.captures++;
            return "CAPTURE";
        }
        if(event == Constants.EVENT_AVOIDANCE && fallingBlock.size >= tracker.size) {
            phenotype.avoided++;
            return "AVOID";
        }if(event != Constants.EVENT_CAPTURE && fallingBlock.size < tracker.size) {
            phenotype.missed++;
            return "MISSED";
        }
        return "";
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
            sensors[tracker.size+1] = (tracker.x == TrackerSimulator.width - tracker.size -1) ? 1 : 0;
        }else
            sensors = new double[tracker.size];

        for (int i = 0; i < tracker.size; i++) {
            if(fallingBlock.contains(tracker.x + i))
                sensors[i] = 1d;
        }
        return sensors;
    }

    public static void main(String[] args) {
        Settings.ann.CRASH_PENALTY = 1;
        Settings.ea.MUTATION_RATE = 0.1;
        Settings.ea.CROSSOVER_RATE = 0.7;

        TrackerSimulator sim = new TrackerSimulator();
        sim.initialize(null);
        sim.ann.setWeights(new double[]{2.65625, 4.2578125, -1.875, -0.3125, 4.6875, -2.4609375, -1.9921875, -0.234375, -1.5625, 4.84375, -0.2734375, -1.8359375, -3.4375, 0.5859375, -3.9453125, 4.8046875, 2.0703125, -2.109375, 3.0078125, -3.203125, 2.4609375, 0.0390625}
                ,new double[]{-7.265625, -1.1328125, -7.265625, -0.078125}
                ,new double[]{4.0, 2.59375, 1.1875, 2.859375}
                ,new double[]{1.73828125, 1.0859375, 1.6015625, 1.0625});


        Scanner scanner = new Scanner(System.in);
        while (true){
            double[] input = new double[5];
            for (int i = 0; i < 5; i++) {
                input[i] = scanner.nextInt();
            }
            System.out.println(Arrays.toString(sim.ann.feedInput(input,true)));

        }

    }

}
