package rl.core;

/**
 * Created by espen on 11/05/15.
 */
public class Step {

    public State state;
    public Action action;

    public Step(State state, Action action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (!action.equals(step.action))
            return false;
        if (!state.equals(step.state))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }
}
