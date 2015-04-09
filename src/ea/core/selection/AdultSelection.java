package ea.core.selection;

import ea.core.Phenotype;
import ea.core.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by espen on 03/03/15.
 */
public class AdultSelection {

    public static List<Phenotype> fullGenerationaleReplacement(List<Phenotype> parents, List<Phenotype> children){
        List<Phenotype> survivors = new ArrayList<>();
        survivors.addAll(children);
        appendElitism(parents,survivors);

        return survivors;
    }
    public static List<Phenotype> overProduction(List<Phenotype> parents, List<Phenotype> children){
        List<Phenotype> survivors = new ArrayList<>();

        Collections.sort(children);
        for (int i = 0; i < Settings.ADULT_POOL_SIZE; i++) {
            if(i>children.size())
                break;
            survivors.add(children.get(i));
        }

        appendElitism(parents,survivors);

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

    private static void appendElitism(List<Phenotype> parents, List<Phenotype> survivors){
        Collections.sort(parents);
        for (int i = 0; i < Settings.ELITISM_COUNT; i++) {
            survivors.add(parents.get(i));
        }
    }

}
