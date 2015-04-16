package ann.core;

import utils.Calculate;
import utils.Print;

/**
 * Created by espen on 01/04/15.
 */
public class NeuralNetwork {

    NeuronLayer[] layers;
    double[] weights;
    public int totalNetworkWeights;

    public NeuralNetwork(int[] layerSizes) {
        buildNetwork(layerSizes);
    }
    public NeuralNetwork(){

    }

    public void buildNetwork(int[] layerSizes) {
        layers = new NeuronLayer[layerSizes.length];

        //creates the layers
        for (int i = 0; i < layerSizes.length; i++) {
            NeuronLayer layer = new NeuronLayer();
            layer.neurons = new Neuron[layerSizes[i]];
            //creates the units
            for (int j = 0; j < layerSizes[i]; j++) {
                //the first layer will have no inputweights, so the array will become null
                Neuron neuron = new Neuron(generateRandomWeights(i==0?0:layerSizes[i-1]),Math.random());
                layer.neurons[j] = neuron;
            }
            layers[i] = layer;
        }

        totalNetworkWeights = 0;
        for (int i = 0; i < layers.length; i++) {
            for (int j = 0; j < layers[i].neurons.length; j++) {
                totalNetworkWeights +=layers[i].neurons[j].weights.length;
            }
        }
    }

    private double[] generateRandomWeights(int inputSize) {
        if(inputSize == 0)
            return new double[0];
        double[] weights = new double[inputSize];
        for (int i = 0; i <inputSize; i++) {
            weights[i] = Math.random()-Math.random();
        }
        return weights;
    }

    public double[] getWeights(){
       return weights;
    }

    public void setWeights(double[] weights){
        this.weights = weights;

        int n = 0;
        //skips the first layer since it does not have input weights
        for (int i = 1; i < layers.length; i++) {
            for (int j = 0; j < layers[i].neurons.length; j++) {
                for (int k = 0; k < layers[i].neurons[j].weights.length; k++) {
                    layers[i].neurons[j].weights[k] = weights[n++];
                }
            }
        }
    }

    public double[] feedInput(double[] input){

        double[] output = null;

        //for each layer, skips the first input layer
        for (int i = 1; i < layers.length; i++) {

            if(i > 1)
                input = output;

            int layersSize = layers[i].neurons.length;

            output = new double[layersSize];

            //for each neuron in the layer
            for (int j = 0; j < layersSize; j++) {
                double activation = layers[i].neurons[j].activate(input);
                output[j] = Calculate.sigmoid(activation);
                //Print.array("sigmoid" , output);
            }
        }
        return output;
    }
}
