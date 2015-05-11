package rl.problems.flatland;

import rl.core.Action;
import utils.Direction;

/**
 * Created by espen on 07/05/15.
 */
public class FlatlandAction extends Action {

    public Direction action;

    public FlatlandAction(Direction action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object obj) {
        return action == ((FlatlandAction)obj).action;
    }

    @Override
    public int hashCode() {
        return action.hashCode();
    }

    @Override
    public String toString() {
        return "Action={" + action +
                '}';
    }
}
