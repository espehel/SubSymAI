package rl.problems.flatland;

import rl.core.State;

import java.util.ArrayList;

/**
 * Created by espen on 07/05/15.
 */
public class FlatlandState extends State{
    int x;
    int y;
    ArrayList<Integer> foodLeft;

    public FlatlandState(int x, int y, ArrayList<Integer> foodLeft) {
        this.x = x;
        this.y = y;
        this.foodLeft = foodLeft;
    }

    @Override
    public boolean equals(Object other){
        FlatlandState otherState = (FlatlandState) other;
        if(x != otherState.x || y != otherState.y)
            return false;
        if(foodLeft.size() != otherState.foodLeft.size())
            return false;

        for (Integer food :foodLeft){
            if(!otherState.foodLeft.contains(food))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + foodLeft.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "(" + x +
                "," + y +
                "), food=" + foodLeft +
                '}';
    }
}
