package ea.problems.onemax;

import ea.core.Phenotype;
import ea.core.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by espen on 09/03/15.
 */
public class OneMaxPhenotype extends Phenotype {

    public List<Integer> data;

    @Override
    public void develop() {
        data = new ArrayList<>();
        for (boolean bool : genotype.geno){
            data.add(bool ? 1 : 0);
        }
    }
    @Override
    protected String getDataString() {
        return data.toString();
    }

    @Override
    public void calculateFitness() {
        int f = 0;
        for (Integer n : data){
            f+=n;
        }
        fitness = f / (double) Settings.GENOTYPE_SIZE;
    }
}
