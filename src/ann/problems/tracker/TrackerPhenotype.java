package ann.problems.tracker;

import ann.core.ANNPhenotype;
import ea.core.Phenotype;

/**
 * Created by espen on 14/04/15.
 */
public class TrackerPhenotype extends ANNPhenotype {
    public double[] data;
    public int biggerBlocks;
    public int smallerBlocks;
    public int crashes;
    public int captures;

    @Override
    public void develop() {

    }

    @Override
    protected String getDataString() {
        return null;
    }

    @Override
    public void calculateFitness() {

    }
}
