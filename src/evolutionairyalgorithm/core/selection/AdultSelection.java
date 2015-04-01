package evolutionairyalgorithm.core.selection;

import evolutionairyalgorithm.core.Phenotype;
import evolutionairyalgorithm.core.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by espen on 03/03/15.
 */
public class AdultSelection {

    public static List<Phenotype> fullGenerationaleReplacement(List<Phenotype> parents, List<Phenotype> children){
        return children;
    }
    public static List<Phenotype> overProduction(List<Phenotype> parents, List<Phenotype> children){
        List<Phenotype> survivors = new ArrayList<>();

        Collections.sort(children);
        for (int i = 0; i < Settings.ADULT_POOL_SIZE; i++) {
            survivors.add(children.get(i));
        }
        return survivors;
    }
    public static List<Phenotype> GenerationalMixing(List<Phenotype> parents, List<Phenotype> children){
        List<Phenotype> survivors = new ArrayList<>();

        children.addAll(parents);

        Collections.sort(children);

        for (int i = 0; i < Settings.ADULT_POOL_SIZE; i++) {
            survivors.add(children.get(i));
        }

        return survivors;
    }

}
