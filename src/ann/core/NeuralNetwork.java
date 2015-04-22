package ann.core;

/**
 * Created by espen on 01/04/15.
 */
public class NeuralNetwork {

    NeuronLayer[] layers;
    double[] weights;
    public int totalNetworkWeights;
    //public Neuron biasNeuron;

    /*public NeuralNetwork(int[] layerSizes, boolean biasNode) {
        buildNetwork(layerSizes);
    }*/
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
                int connections;
                int neighbours;
                if(i == 0) {
                    connections = 0;
                    neighbours = 0;
                }else {
                    connections =layerSizes[i-1];
                    neighbours = layerSizes[i] - 1;
                }
                Neuron neuron = new Neuron(generateWeights(connections,neighbours),0.0);
                neuron.neighbourCount = neighbours;
                layer.neurons[j] = neuron;
            }
            layers[i] = layer;
        }

        totalNetworkWeights = 0;
        for (int i = 0; i < layers.length; i++) {
            for (int j = 0; j < layers[i].neurons.length; j++) {
                totalNetworkWeights +=layers[i].neurons[j].weights.length;
                totalNetworkWeights += layers[i].neurons[j].neighbourCount;
            }
        }

        //biasNeuron = new Neuron(new double[totalNetworkWeights],0.0);
    }

    private double[] generateWeights(int inputSize, int neighbourCount) {

        if(inputSize == 0)
            return new double[0];

        if(Settings.CTRNN)
            inputSize +=neighbourCount + 1;//adds for all neigbours and one for itself
        double[] weights = new double[inputSize];
        /*for (int i = 0; i <inputSize; i++) {
            weights[i] = Math.random()-Math.random();
        }*/
        return weights;
    }

    public double[] getWeights(){
       return weights;
    }

    public void setWeights(double[] weights, double[] biasWeights, double[] gains, double[] timeConstants){
        this.weights = weights;

        int w = 0;
        int n = 0;
        //skips the first layer since it does not have input weights
        for (int i = 1; i < layers.length; i++) {
            for (int j = 0; j < layers[i].neurons.length; j++) {
                for (int k = 0; k < layers[i].neurons[j].weights.length; k++) {
                    layers[i].neurons[j].weights[k] = weights[w++];
                }
                if(Settings.CTRNN) {
                    layers[i].neurons[j].gain = gains[n];
                    layers[i].neurons[j].timeConstant = timeConstants[n];
                    layers[i].neurons[j].biasWeight = biasWeights[n++];
                }
            }
        }
    }

    public double[] feedInput(double[] input, boolean biasNode){

        double[] output = null;
        double bias = biasNode ? 1.0 : 0.0;

        //for each layer, skips the first input layer
        for (int i = 1; i < layers.length; i++) {

            if(i > 1)
                input = output.clone();

            int layerSize = layers[i].neurons.length;

            output = new double[layerSize];
            //System.out.println();
            //for each neuron j in the layer i
            for (int j = 0; j < layerSize; j++) {

                double[] neighbourActivations = new double[layerSize-1];
                int offset = 0;
                for (int k = 0; k < layerSize; k++) {
                    if(k==j) {
                        offset++;
                        continue;
                    }
                    neighbourActivations[k-offset] += layers[i].neurons[k].lastActivation;
                }

                Neuron neuron = layers[i].neurons[j];
                double activation = neuron.activate(input,neighbourActivations);
                //double out = Calculate.sigmoid(activation,neuron.gain);
                output[j] = activation;
                //neuron.lastActivation = activation;
                //neuron.addDerivative();
                /*if(i == 1 && j ==1)
                    System.out.println(neuron.internalState);*/
                //Print.array("sigmoid" , output);
            }
            //System.out.println();
            //add last activation
            for (int j = 0; j < layerSize; j++) {
                layers[i].neurons[j].lastActivation = output[j];
            }
        }



        return output;
    }
}
