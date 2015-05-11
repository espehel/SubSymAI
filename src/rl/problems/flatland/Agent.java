package rl.problems.flatland;

import ann.core.Settings;
import utils.AbstractAgent;
import utils.Constants;
import utils.Direction;

/**
 * Created by espen on 02/04/15.
 */
public class Agent extends AbstractAgent{
    public int foodCount;
    public int poisonCount;


    public Agent() {
        foodCount = 0;
        poisonCount = 0;
        x = 0;
        y = 0;
    }

    public Agent(int foodCount, int poisonCount) {
        this();
        this.foodCount = foodCount;
        this.poisonCount = poisonCount;


    }

    public void move(Direction direction) {
        front = direction;
        x = x + direction.x;
        y = y + direction.y;

        if(x==-1)
            x= Settings.FLATLAND_WIDTH-1;
        else if(x == Settings.FLATLAND_WIDTH)
            x=0;

        if(y==-1)
            y=Settings.FLATLAND_HEIGHT-1;
        else if(y == Settings.FLATLAND_HEIGHT)
            y=0;
    }

    public void eat(int content) {
        if(content == Constants.FLATLAND_CELLTYPE_FOOD)
            foodCount++;
        else if(content == Constants.FLATLAND_CELLTYPE_POISON)
            poisonCount++;
    }

    @Override
    public String toString() {
        return "Agent{" +
                ", y=" + y +
                ", x=" + x +
                ", poisonCount=" + poisonCount +
                ", foodCount=" + foodCount +
                '}';
    }
}
