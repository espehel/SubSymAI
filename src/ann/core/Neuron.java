package ann.core;

import utils.Print;

/**
 * Created by espen on 01/04/15.
 */
public class Neuron {
    double[] weights;
    double threshold;

    public Neuron(double[] weights, double threshold) {
        this.weights = weights;
        this.threshold = threshold;
    }

    public double activate(double[] inputs){

        double activation = 0;

        for (int i = 0; i < inputs.length; i++) {
            activation += inputs[i]*weights[i];
        }
        //Print.array("weights" , weights);

        return activation;
    }
}
