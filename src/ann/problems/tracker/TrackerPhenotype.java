package ann.problems.tracker;

import ann.core.ANNPhenotype;
import ea.core.Settings;
import utils.Calculate;
import utils.Constants;

import javax.security.auth.login.CredentialException;
import java.util.Arrays;

/**
 * Created by espen on 14/04/15.
 */
public class TrackerPhenotype extends ANNPhenotype {
    public double[] data;
    public int biggerBlocks;
    public int smallerBlocks;
    public int crashes;
    public int captures;
    public double[] timeConstants;
    public double[] gains;
    public double[] biasWeights;
    public double[] connectionWeights;
    public int avoided;
    public static int neuronCount;
    public int missed;

    @Override
    public void develop() {

        data = new double[ea.core.Settings.GENOTYPE_SIZE/ea.core.Settings.REPRESENTATION_SIZE];

        for (int i = 0; i < data.length; i++) {
            boolean[] symbol = new boolean[ea.core.Settings.REPRESENTATION_SIZE];
            for (int j = 0; j <symbol.length; j++) {
                symbol[j] = genotype.geno[(i*symbol.length)+j];
            }
            data[i] = Calculate.convertToInteger(symbol)/ Math.pow(2,ea.core.Settings.REPRESENTATION_SIZE);
        }
        developTimeConstants();
        developBiasWeights();
        developConnectionWeights();
        developGains();
    }

    @Override
    protected String getDataString() {
        return null;
    }

    @Override
    public void calculateFitness() {


        double capScore = (double)captures/(double)smallerBlocks;
        double avoidedScore = (double)avoided/(double)biggerBlocks;
        double crashScore = Math.pow(ann.core.Settings.CRASH_PENALTY,crashes);
        double missedScore = Math.pow(ann.core.Settings.POISON_PENALTY,missed);

        switch (ann.core.Settings.SCENARIO){
            case Constants.SCENARIO_NO_WRAP :
                fitness = capScore*avoidedScore*crashScore*missedScore;
                break;
            case Constants.SCENARIO_WRAP :
                fitness = capScore*avoidedScore*crashScore*missedScore;
                break;
            case Constants.SCENARIO_POLL_W_WRAP :
                fitness = capScore*avoidedScore*crashScore*missedScore;
                break;
        }
    }
    public void developTimeConstants(){
        timeConstants = getSubData(0,neuronCount);
        for (int i = 0; i < timeConstants.length; i++) {
            timeConstants[i] = timeConstants[i] + 1.0;
        }
    }
    public void developGains(){
        gains = getSubData(neuronCount,neuronCount);
        for (int i = 0; i < gains.length; i++) {
            gains[i] = (gains[i] * 4.0) +1.0;
        }
    }
    public void developBiasWeights(){
        biasWeights = getSubData(neuronCount*2,neuronCount);
        for (int i = 0; i < biasWeights.length; i++) {
            biasWeights[i] = biasWeights[i] * -10.0;
        }
    }
    public void developConnectionWeights(){
         connectionWeights = getSubData(neuronCount*3,data.length-neuronCount*3);
        for (int i = 0; i < connectionWeights.length; i++) {
            connectionWeights[i] = (connectionWeights[i] * 10.0) - 5.0;
        }
    }
    public double[] getTimeConstants(){
        return timeConstants;
    }
    public double[] getGains(){
        return gains;
    }
    public double[] getBiasWeights(){
        return biasWeights;
    }

    public double[] getconnectionWeights(){
        return connectionWeights;
    }
    private double[] getSubData(int i,  int size){
        double[] sub = new double[size];
        for (int j = 0; j < size; j++,i++) {
            sub[j] = data[i];
        }
        return sub;
    }

    @Override
    public String toString() {
        return "TrackerPhenotype{" +
                "generation=" + generation +
                ", fitness=" + fitness +
                "connectionWeights=" + Arrays.toString(connectionWeights) +
                ", biasWeights=" + Arrays.toString(biasWeights) +
                ", gains=" + Arrays.toString(gains) +
                ", timeConstants=" + Arrays.toString(timeConstants) +
                '}';
    }
}
