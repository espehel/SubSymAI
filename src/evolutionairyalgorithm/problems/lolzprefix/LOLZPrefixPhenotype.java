package evolutionairyalgorithm.problems.lolzprefix;

import evolutionairyalgorithm.core.Phenotype;
import evolutionairyalgorithm.core.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by espen on 09/03/15.
 */
public class LOLZPrefixPhenotype extends Phenotype {

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
    public double calculateFitness() {
        int f = 0;
        int l = data.get(0);
        for (Integer n : data){
            if(n.intValue() == l)
                f+=n;
            else
                break;
        }
        f = f>(int) Settings.Z_VALUE && l==0 ? (int)Settings.Z_VALUE : f;

        return f / (double) Settings.GENOTYPE_SIZE;
    }

}