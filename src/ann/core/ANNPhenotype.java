package ann.core;

import ea.core.Phenotype;
import utils.Constants;

/**
 * Created by espen on 16/04/15.
 */
public abstract class ANNPhenotype extends Phenotype {

    public int getAction(double[] output) {
        double bestValue = Settings.NO_ACTION_THRESHOLD;
        int bestAction = Constants.NO_ACTION;
        for (int j = 0; j < output.length; j++) {
            if (output[j] > bestValue) {
                bestValue = output[j];
                bestAction = j;
            }

        }
        return bestAction;
    }
}
