package rl.core;


import rl.problems.flatland.FlatlandAction;
import utils.Direction;

import java.util.*;

/**
 * Created by espen on 05/05/15.
 */
public abstract class QLearning {


    protected Map<State,Map<Action,Double>> qValues = new HashMap<>();
    protected Map<State,Map<Action,Double>> eligibilities = new HashMap<>();
    protected Random random = new Random();
    protected State initialState;
    //protected List<Step> steps = new ArrayList<>();
    protected Set<Step> steps = new HashSet<>();

    protected abstract void init();

    public long runLearning(long k){
        for (long i = 0; i < k; i++) {
            if(i % (k/10) == 0) {
                System.out.println(Math.ceil(i / (double) k * 100) + "%");
            }
            resetScenario();
            //State prevState = initialState;
            while(!isFinished()){
                step();
                /*Action action = selectAction();
                performAction(action);
                updateEligibility(prevState,action);
                State state = getState();
                steps.add(0,new Step(prevState,action));
                double reward = getReward();
                for (int j = 0; j < steps.size(); j++) {
                    Step step = steps.get(j);
                    State futureState;
                    if(j == 0)
                        futureState = state;
                    else
                        futureState = steps.get(j-1).state;
                    updateQValues(futureState,step.state,step.action,reward);
                }
                //updateQValues(state,prevState,action,reward);
                prevState = state;
                if(reward != 0) {
                    steps.clear();
                    eligibilities.clear();
                }*/
                if(Settings.SIMULATED_ANNEALING)
                    deIncrementExplorationRate(i);
            }
            if(!utils.Settings.ea.RUNNING)
                return i;
        }
        return k;
    }

    private void updateEligibility(State state,Action action) {
        if(!eligibilities.containsKey(state)) {
            eligibilities.put(state, new HashMap<>());
            eligibilities.get(state).put(action,0d);
        }else if(!eligibilities.get(state).containsKey(action)){
            eligibilities.get(state).put(action,0d);
        }

        double oldValue = eligibilities.get(state).get(action);
        eligibilities.get(state).put(action, oldValue+1);
    }

    private void deIncrementExplorationRate(long i) {
        Settings.EXPLORE_RATE = 1d-(i/(double)Settings.REPETITIONS);
    }

    public void step(){
        Action action = selectAction();
        step(action);
    }

    public void step(Action action) {
        State prevState = getState();
        steps.add(new Step(prevState,action));
        performAction(action);
        State newState = getState();
        updateEligibility(prevState,action);
        double reward = getReward();
        updateQValues(newState,prevState,action,reward);//;if(true)return;
        /*for (int j = 0; j < steps.size(); j++) {
            Step step = steps.get(j);
            State futureState;
            if(j == 0)
                futureState = newState;
            else
                futureState = steps.get(j-1).state;
            updateQValues(futureState,step.state,step.action,reward);
        }*/
        /*if(reward != 0) {
            steps.clear();
            eligibilities.clear();
        }*/
    }

    protected abstract boolean isFinished();

    protected double getQValue(State state, Action action) {
        if(qValues.containsKey(state))
            if(qValues.get(state).containsKey(action))
                return qValues.get(state).get(action);
        return 0;
    }

    protected void updateQValues(State state, State prevState, Action action, double reward) {
        double oldQ = getQValue(prevState, action);
        //System.out.print(state);
        //System.out.println(": " +oldQ);
        double maxValue = getMaxValue(state);

        double delta = reward + (Settings.DISCOUNT_RATE*maxValue) - oldQ;

        //double newQ = oldQ + Settings.LEARNING_RATE * delta*eligibilities.get(prevState).get(action);


        /*if(!qValues.containsKey(prevState))
            qValues.put(prevState,new HashMap<>());
        qValues.get(prevState).put(action,newQ);
        //if(true)return;
        double oldValue = eligibilities.get(prevState).get(action);
        eligibilities.get(prevState).put(action,oldValue*Settings.DISCOUNT_RATE*Settings.TRACE_DECAY_FACTOR);*/

        for (Step step : steps){
            double eligibility = eligibilities.get(step.state).get(step.action);
            double newQ = getQValue(step.state,step.action) + (Settings.LEARNING_RATE*delta*eligibility);

            if(!qValues.containsKey(step.state))
                qValues.put(step.state,new HashMap<>());
            qValues.get(step.state).put(step.action,newQ);

            eligibilities.get(step.state).put(step.action, Settings.DISCOUNT_RATE*Settings.TRACE_DECAY_FACTOR*eligibility);
            if(Double.isNaN(qValues.get(step.state).get(step.action)))
                System.out.println();
        }




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
