package ann.problems;

import ann.core.NeuralNetwork;
import ann.problems.flatland.Agent;
import utils.GUIController;

/**
 * Created by espen on 02/04/15.
 */
public abstract class ProblemSimulator {

    protected NeuralNetwork ann;
    public GUIController gui;

    public ProblemSimulator(){
        ann = new NeuralNetwork();
    }

    public abstract void initialize(GUIController gui);
    public abstract void start();
    public abstract void runBestAgent();
    public abstract void generateNewContent();
}
