package ann.core;

import utils.Constants;

/**
 * Created by espen on 02/04/15.
 */
public class Settings {
    public static double CRASH_PENALTY = 0.95;
    public static int STEP_COUNT = 60;
    public static int INPUT_SIZE = 6;
    public static int OUTPUT_SIZE = 3;
    public static boolean CTRNN = false;
    public static boolean DYNAMIC = false;
    public static int FLATLAND_SIZE = 10;
    public static double FOOD_DISTRIBUTION = 0.333;
    public static double POISON_DISTRIBUTION = 0.333;
    public static double NO_ACTION_THRESHOLD = -1.0;
    public static int POISON_PENALTY = 10;
    public static int SERIES_COUNT = 5;
    public static boolean WRAP_AROUND = true;
    public static int SCENARIO = Constants.SCENARIO_WRAP;

}
