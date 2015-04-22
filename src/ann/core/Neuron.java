package ann.core;

import com.sun.javafx.geom.Vec2d;
import utils.Calculate;

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
    double internalState;
    double derivative;
    double output;
    public int neighbourCount;

    public Neuron(double[] weights, double threshold) {
        this.weights = weights;
        this.threshold = threshold;
        internalState = 0;
        lastActivation = 0;
        derivative = 0;
    }

    public double activate(double[] inputs, double[] neighbourActivation){

        double activation = 0;

        for (int i = 0; i < inputs.length; i++) {
            activation += inputs[i]*weights[i];
        }
        //Print.array("weights" , weights);
        if(!Settings.CTRNN) {
            return activation;
        }

        //System.out.println(inputs.length);
        //System.out.println(neighbourActivation.length);
        for (int i = 0; i < neighbourCount; i++) {
            //System.out.println(i);
            activation += neighbourActivation[i] * weights[i+inputs.length];
        }
        activation += lastActivation * weights[inputs.length+neighbourCount];



        derivative = (1 / timeConstant) * (-internalState + activation + biasWeight);

        internalState += derivative;

        output = Calculate.sigmoid(internalState,gain);

        return output;
    }
}
