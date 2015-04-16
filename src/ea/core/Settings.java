package ea.core;

import utils.Constants;

/**
 * Created by espen on 05/03/15.
 */
public class Settings {

    public static boolean RUNNING = false;
    public static int LOOP_DELAY = 40;
    public static int CHILD_POOL_SIZE = 30;
    public static double TEMPERATURE = 10;
    public static int ADULT_POOL_SIZE = 30;
    public static int GENOTYPE_SIZE = 10;
    public static int Z_VALUE = 4;
    public static double EPSILON = 0.1;
    public static double MUTATION_RATE = 1;
    public static double CROSSOVER_RATE = 1;
    public static int REPRESENTATION_SIZE = 1;
    public static long MAX_GENERATIONS = -1;
    public static boolean LOCAL_SEQUENCE = false;
    public static boolean PARENT_SELECTION_FITNESSPROPORTIONATE = true;
    public static boolean PARENT_SELECTION_SIGMASCALING = false;
    public static boolean PARENT_SELECTION_TOURNAMENT = false;
    public static boolean PARENT_SELECTION_BOLTZMANN = false;
    public static boolean ADULT_SELECTION_FULLREPLACEMENT = true;
    public static boolean ADULT_SELECTION_OVERPRODUCTION = false;
    public static boolean ADULT_SELECTION_GENERATIONMIXING = false;
    public static int OPERATOR_CROSSOVER = Constants.CROSSOVER_ONE_POINT_PHENO_SPECIFIC;
    public static int OPERATOR_MUTATION = Constants.MUTATION_PHENOSPECIFIC;
    //public static int OPERATOR_CROSSOVER = Constants.CROSSOVER_ONE_POINT;
    //public static int OPERATOR_MUTATION = Constants.MUTATION_PROBABILITY;
    public static int K_VALUE = 10;
    public static int ELITISM_COUNT = 5;
}
