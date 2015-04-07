package utils;

import ann.problems.flatland.Agent;

/**
 * Created by espen on 02/04/15.
 */
public interface GUIController {
    public void updateGraph(double bestFitness);
    public void updateGrid(Agent agent);
    public void updateGrid(int[][] board);

}
