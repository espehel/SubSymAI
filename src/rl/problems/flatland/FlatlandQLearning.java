package rl.problems.flatland;

import rl.core.Action;
import rl.core.QLearning;
import rl.core.Settings;
import rl.core.State;
import utils.Direction;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by espen on 07/05/15.
 */
public class FlatlandQLearning extends QLearning {

    int[][] scenario;
    FlatlandSimulator simulator;
    Agent agent;
    ArrayList<Integer> foodLeft;
    Integer cellContent;

    public FlatlandQLearning(FlatlandSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    protected void init() {
        qValues = new HashMap<>();
        agent = new Agent();
        foodLeft = new ArrayList<>();
        agent.x = simulator.startPosition.x;
        agent.y = simulator.startPosition.y;
        for (int i = 0; i < simulator.food.size(); i++) {
            foodLeft.add(simulator.food.get(i));
        }
        initialState = new FlatlandState(agent.x,agent.y,getStateFood());
    }

    @Override
    protected boolean isFinished() {
        return (foodLeft.isEmpty() && agent.x == simulator.startPosition.x && agent.y == simulator.startPosition.y)|| agent.poisonCount > 0;
    }

    @Override
    protected double getReward() {
        if(cellContent==-1){
            agent.poisonCount++;
            return -10d;
        }
        if(cellContent>0){
            agent.foodCount++;
            return 10d;
        }
        if(agent.x==simulator.startPosition.x && agent.y == simulator.startPosition.y && foodLeft.isEmpty())
            return 10d;
        return Settings.STEP_PENALTY;
    }

    @Override
    protected State getState() {
        return new FlatlandState(agent.x,agent.y,getStateFood());
    }

    @Override
    protected void performAction(Action action) {
        scenario[agent.x][agent.y] = 0;
        agent.move(((FlatlandAction)action).action);
        cellContent = scenario[agent.x][agent.y];
        scenario[agent.x][agent.y] = -2;

        if(foodLeft.contains(cellContent))
            foodLeft.remove(cellContent);
    }

    @Override
    protected Action selectAction() {
        if(Math.random()<Settings.EXPLORE_RATE)
            return new FlatlandAction(Direction.getRandom());

        List<Direction> actions = getBestAction(agent.x, agent.y, getStateFood());


        return new FlatlandAction(actions.get(random.nextInt(actions.size())));
    }
    public Map<Direction,Double> getActions(){
        return getActions(agent.x,agent.y,foodLeft);
    }

    public Map<Direction,Double> getActions(int x, int y, ArrayList<Integer> foodLeft){
        Map<Direction,Double> actions = new HashMap<>();
        actions.put(Direction.NORTH, getQValue(new FlatlandState(x, y, foodLeft), new FlatlandAction(Direction.NORTH)));
        actions.put(Direction.EAST, getQValue(new FlatlandState(x, y, foodLeft), new FlatlandAction(Direction.EAST)));
        actions.put(Direction.SOUTH, getQValue(new FlatlandState(x, y, foodLeft), new FlatlandAction(Direction.SOUTH)));
        actions.put(Direction.WEST, getQValue(new FlatlandState(x, y, foodLeft), new FlatlandAction(Direction.WEST)));
        return actions;
    }

    public List<Direction> getBestAction(int x, int y, ArrayList<Integer> foodLeft){

        Map<Direction,Double> actions = getActions(x,y,foodLeft);
        double bestValue = -Double.MAX_VALUE;
        List<Direction> bestActions = new ArrayList<>();

        for (Direction d : actions.keySet()) {
            if(actions.get(d)==bestValue)
                bestActions.add(d);
            if (actions.get(d) > bestValue) {
                bestActions.clear();
                bestActions.add(d);
                bestValue = actions.get(d);
            }
        }
        if(bestActions.isEmpty())
            System.out.println();
        return bestActions;
    }

    @Override
    protected void resetScenario() {
        scenario = new int[simulator.flatland.length][simulator.flatland[0].length];
        for (int i = 0; i < scenario.length; i++) {
            scenario[i] = simulator.flatland[i].clone();
        }
        agent.x = simulator.startPosition.x;
        agent.y = simulator.startPosition.y;
        agent.poisonCount = 0;
        agent.foodCount = 0;
        foodLeft.clear();
        for (int i = 0; i < simulator.food.size(); i++) {
            foodLeft.add(simulator.food.get(i));
        }
        SAPs.clear();
        eligibilities.clear();
    }

    public ArrayList<Integer> getStateFood(){
        /*ArrayList<Integer> stateFood = new ArrayList<>();
        for (Integer n: foodLeft){
            stateFood.add(n);
        }
        return stateFood;*/
        return new ArrayList<>(foodLeft);
    }
    public String[][] getScenarioPolicy(){
        String[][] policies = new String[scenario.length][scenario[0].length];

        for (int i = 0; i < scenario.length; i++) {
            for (int j = 0; j < scenario[i].length; j++) {
                if(scenario[i][j] == 0) {
                    List<Direction> actions = getBestAction(i, j, getStateFood());
                    if(actions.size()==1)
                        policies[i][j] = actions.get(0).toString();
                    else
                        policies[i][j] = "";
                }
                else
                    policies[i][j] = "Filled";
            }
        }
        return policies;
    }

}
