package evolutionairyalgorithm.core;

import java.util.Arrays;

/**
 * Created by espen on 03/03/15.
 */
public class Genotype {
    public boolean[] geno;

    public Genotype(boolean[] geno) {
        this.geno = geno;
    }

    @Override
    public String toString() {
        return "Genotype{" +
                "geno=" + Arrays.toString(asIntArray()) +
                '}';
    }

    private int[] asIntArray(){
        int[] ints = new int[geno.length];
        for (int i = 0; i < geno.length; i++) {
            ints[i] = geno[i] ? 1 : 0;
        }
        return ints;
    }
}
