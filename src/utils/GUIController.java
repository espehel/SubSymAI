package utils;

import ann.problems.flatland.Agent;

/**
 * Created by espen on 02/04/15.
 */
public interface GUIController {
    public void updateGraph(double bestFitness);

    void updateGrid(Agent agent);
}
