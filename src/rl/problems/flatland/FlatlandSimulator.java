package rl.problems.flatland;

import rl.gui.Controller;
import sun.text.normalizer.NormalizerBase;
import utils.Direction;
import utils.Settings;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by espen on 02/04/15.
 */
@SuppressWarnings("AccessStaticViaInstance")
public class FlatlandSimulator {

    public int[][] flatland;
    public Agent startPosition;
    private Controller gui;
    int totalFood;
    public ArrayList<Integer> food;
    FlatlandQLearning qLearning;
    long trainingCount;
    String scenarioName;


    public void initialize(Controller gui, File scenario) {
        this.gui = gui;
        startPosition = new Agent();
        food = new ArrayList<>();
        flatland = getFlatlandContent(scenario);
        Settings.ann.FLATLAND_WIDTH = flatland.length;
        Settings.ann.FLATLAND_HEIGHT = flatland[0].length;
        gui.generateGrid(flatland);
        gui.setHomeCell(startPosition.x, startPosition.y);
        scenarioName = scenario.getName();
        trainingCount = 0;
        qLearning = new FlatlandQLearning(this);
        qLearning.init();
    }

    private int[][] getFlatlandContent(File scenario) {
        int[][] flatland = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(scenario));
            String[] init = reader.readLine().split(" ");
            int width = Integer.parseInt(init[0]);
            int height = Integer.parseInt(init[1]);
            startPosition.x = Integer.parseInt(init[2]);
            startPosition.y = Integer.parseInt(init[3]);
            totalFood = Integer.parseInt(init[4]);
            flatland = new int[width][height];
            for (int i = 0; i < height; i++) {
                String[] row = reader.readLine().split(" ");
                for (int j = 0; j < width; j++) {
                    flatland[j][i] = Integer.parseInt(row[j]);
                    if(flatland[j][i]>0)
                        food.add(flatland[j][i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flatland;
    }

    public void train() {
        System.out.println("Training...");
        long reps = qLearning.runLearning(Settings.rl.REPETITIONS);
        System.out.println("Finished training");
        trainingCount += reps;
    }

    public void test() {
        qLearning.resetScenario();
        Settings.rl.EXPLORE_RATE = 0;
        gui.lastPos[0] = startPosition.x;
        gui.lastPos[1] = startPosition.y;
        gui.updateGrid(qLearning.scenario);
        gui.updateGrid(qLearning.getScenarioPolicy());

        int stepCount = 0;
        int foodCount = 0;
        while(!qLearning.isFinished() && Settings.ea.RUNNING){
            try {
                Thread.sleep(Settings.ea.LOOP_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            qLearning.step();
            stepCount++;
            System.out.println(qLearning.getActions());
            String[][] policy = qLearning.getScenarioPolicy();
            gui.updateGrid(qLearning.agent,policy);
            if(foodCount != qLearning.agent.foodCount) {
                gui.updateGrid(policy);
                foodCount = qLearning.agent.foodCount;
            }
        }

        System.out.println("Tested: " + scenarioName);
        System.out.println("Training runs: " + trainingCount);
        System.out.println("Poison eaten: " + qLearning.agent.poisonCount);
        System.out.println("Steps taken: " + stepCount);
    }
    public void reset(){
        qLearning.resetScenario();
        Settings.rl.EXPLORE_RATE = 0;
        gui.lastPos[0] = startPosition.x;
        gui.lastPos[1] = startPosition.y;
        gui.updateGrid(qLearning.scenario);
        gui.updateGrid(qLearning.getScenarioPolicy());
    }
    public void step(){
        qLearning.step();
        System.out.println(qLearning.getActions());
        System.out.println(qLearning.getStateFood());
        /*for (int i = 0; i < qLearning.getScenarioPolicy().length; i++) {
            System.out.println(Arrays.toString(qLearning.getScenarioPolicy()[i]));
        }*/
        gui.updateGrid(qLearning.agent,qLearning.getScenarioPolicy());
        gui.updateGrid(qLearning.getScenarioPolicy());
    }

    public void step(Direction direction) {
        qLearning.step(new FlatlandAction(direction));
        if(qLearning.isFinished())
            qLearning.resetScenario();
        System.out.println(qLearning.getActions());
        System.out.println(qLearning.getStateFood());
        /*for (int i = 0; i < qLearning.getScenarioPolicy().length; i++) {
            System.out.println(Arrays.toString(qLearning.getScenarioPolicy()[i]));
        }*/
        gui.updateGrid(qLearning.agent,qLearning.getScenarioPolicy());
        gui.updateGrid(qLearning.getScenarioPolicy());
    }
}
