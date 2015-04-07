package ann.problems;

import ann.problems.flatland.Agent;
import utils.GUIController;

/**
 * Created by espen on 02/04/15.
 */
public interface ProblemSimulator {

    public void initialize(GUIController gui);
    public void start();
    public void runBestAgent();
}
