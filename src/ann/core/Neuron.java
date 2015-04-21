package ann.core;

/**
 * Created by espen on 01/04/15.
 */
public class Neuron {
    double[] weights;
    double threshold;
    double gain = 1;
    double timeConstant;
    double biasWeight;
    double lastActivation;
    double newActivation;
    double internalState;

    public Neuron(double[] weights, double threshold) {
        this.weights = weights;
        this.threshold = threshold;
        internalState = 0;
        lastActivation = 0;
        newActivation = 0;
    }

    public double activate(double[] inputs, double bias, double neighbourActivation){

        double activation = 0;
        bias = bias*biasWeight;

        int i = 0;
        if(Settings.CTRNN) {
            activation += lastActivation * weights[0];
            activation += neighbourActivation * weights[1];
            i = 2;
        }
        for (; i < inputs.length; i++) {
            activation += inputs[i]*weights[i];
        }
        //Print.array("weights" , weights);

        if(!Settings.CTRNN) {
            return activation;
        }

        double derivative = (1 / timeConstant) * (-internalState + activation + bias);
        internalState += derivative;
        return internalState;
    }
}
