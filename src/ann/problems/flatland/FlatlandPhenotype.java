package ann.problems.flatland;

import ann.core.Settings;
import com.sun.tools.internal.xjc.model.nav.EagerNClass;
import ea.core.Phenotype;
import utils.Calculate;

import java.util.ArrayList;

/**
 * Created by espen on 01/04/15.
 */
public class FlatlandPhenotype extends Phenotype{

    public double[] data;
    public int foodCount;
    public int poisonCount;
    public int maxFood;

    public FlatlandPhenotype(double[] data) {
        this.data = data;
    }
    public FlatlandPhenotype(){

    }


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
    }

    @Override
    protected String getDataString() {
        return data.toString();
    }

    @Override
    public void calculateFitness() {
        int score = foodCount - (poisonCount*Settings.POISON_PENALTY);
        fitness = (double)score/(double)maxFood;
        fitness = fitness>0.0001 ? fitness : 0.0001;
    }
}
