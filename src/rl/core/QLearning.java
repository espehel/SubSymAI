package rl.core;


import rl.problems.flatland.FlatlandAction;
import utils.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by espen on 05/05/15.
 */
public abstract class QLearning {


    protected Map<State,Map<Action,Double>> qValues = new HashMap<>();
    protected Random random = new Random();
    protected State initialState;

    protected abstract void init();

    public void runLearning(long k){
        //TODO state does not seem to change when food is eaten

        for (int i = 0; i < k; i++) {
            if(i % (k/10) == 0) {
                System.out.println(Math.ceil(i / (double) k * 100) + "%");
            }
            resetScenario();
            State prevState = initialState;
            while(!isFinished()){
                Action action = selectAction();
                performAction(action);
                State state = getState();
                double reward = getReward();
                updateQValues(state,prevState,action,reward);
                prevState = state;

                if(Settings.SIMULATED_ANNEALING)
                    deIncrementExplorationRate(i);
            }
        }
    }

    private void deIncrementExplorationRate(int i) {
        Settings.EXPLORE_RATE = 1d-(i/(double)Settings.REPETITIONS);
    }

    public void step(){
        State prevState = getState();
        Action action = selectAction();
        performAction(action);
        State state = getState();
        double reward = getReward();
        updateQValues(state, prevState, action,reward);
    }

    protected abstract boolean isFinished();

    protected double getQValue(State flatlandState, Action flatlandAction) {
        if(qValues.containsKey(flatlandState))
            if(qValues.get(flatlandState).containsKey(flatlandAction))
                return qValues.get(flatlandState).get(flatlandAction);
        return 0;
    }

    protected void updateQValues(State state, State prevState, Action action, double reward) {
        double oldQ = getQValue(prevState,action);
        double maxValue = getMaxValue(state);

        double newQ = oldQ + Settings.LEARNING_RATE * (reward + Settings.DISCOUNT_RATE*maxValue - oldQ);


        if(!qValues.containsKey(prevState))
            qValues.put(prevState,new HashMap<>());
        //if(!qValues.get(state).containsKey(action))
        qValues.get(prevState).put(action,newQ);

    }
    public double getMaxValue(State state){
        ArrayList<Double> values = new ArrayList<>();
        values.add(getQValue(state,new FlatlandAction(Direction.NORTH)));
        values.add(getQValue(state,new FlatlandAction(Direction.EAST)));
        values.add(getQValue(state,new FlatlandAction(Direction.SOUTH)));
        values.add(getQValue(state,new FlatlandAction(Direction.WEST)));

        double bestValue = 0;

        for (Double d : values) {
            if (d > bestValue) {
                bestValue = d;
            }
        }
        return bestValue;
    }
    protected abstract double getReward();

    protected abstract State getState();

    protected abstract void performAction(Action action);

    protected abstract Action selectAction();

    protected abstract void resetScenario();

}
