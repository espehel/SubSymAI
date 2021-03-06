package ea.problems.SuprisingSequence;

import ea.core.Genotype;
import ea.core.Phenotype;
import ea.core.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by espen on 09/03/15.
 */
public class SuprisingSequencePhenotype extends Phenotype {

    public List<Integer> data;

    @Override
    public void develop() {
        data = new ArrayList<>();
        for (int i = 0; i < Settings.GENOTYPE_SIZE; i+=Settings.REPRESENTATION_SIZE) {
            boolean[] symbol = new boolean[Settings.REPRESENTATION_SIZE];
            for (int j = 0; j <symbol.length; j++) {
                symbol[j] = genotype.geno[i+j];
            }
            data.add(convertToInteger(symbol)%Settings.Z_VALUE);
        }
    }

    private Integer convertToInteger(boolean[] symbol) {
        int n = 0;
        for (boolean b : symbol)
            n = (n << 1) | (b ? 1 : 0);
        return n;
    }

    @Override
    public void calculateFitness() {
        Map<String,Integer> occurences = new HashMap<>();
        double f = 0;
        for (int i = 0; i < data.size(); i++) {
            String a = data.get(i).toString();
            for (int j = i+1; j < data.size(); j++) {
                String b = data.get(j).toString();
                int d = j-i;
                String key = a+d+b;
                if(occurences.containsKey(key))
                    occurences.put(key,occurences.get(key)+1);
                else
                    occurences.put(key,1);

                if(Settings.LOCAL_SEQUENCE)
                    break;
            }
        }
        int totalOccurences = 0;
        for (Integer o : occurences.values()){
            totalOccurences += o;
        }


        fitness = (double)occurences.size() / (double)totalOccurences;
    }
    @Override
    protected String getDataString() {
        return data.toString();
        /*StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer :data){
            stringBuilder.append(integer);
        }
        return stringBuilder.toString();*/
    }
}