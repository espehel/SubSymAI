package ann.problems.flatland;

import utils.Constants;
import utils.Direction;
import ann.core.Settings;

/**
 * Created by espen on 02/04/15.
 */
public class Agent {
    public int foodCount;
    public int poisonCount;
    public int x;
    public int y;
    //public int direction;
    public Direction left;
    public Direction front;
    public Direction right;


    public Agent() {
        foodCount = 0;
        poisonCount = 0;
        x = 0;
        y = 0;
        //direction = Constants.DIRECTION_UP;
        left = Direction.WEST;
        front = Direction.NORTH;
        right = Direction.EAST;
    }

    public void move(int action) {

        switch (action){
            case Constants.MOVE_HOLD:
                return;
            case Constants.MOVE_LEFT:
                left = left.left();
                front = front.left();
                right = right.left();
                break;
            case Constants.MOVE_RIGHT:
                left = left.right();
                front = front.right();
                right = right.right();
        }
        move(front);
    }

    private void move(Direction direction) {
        x = x + direction.x;
        y = y + direction.y;

        if(x==-1)
            x= Settings.FLATLAND_SIZE-1;
        else if(x == Settings.FLATLAND_SIZE)
            x=0;

        if(y==-1)
            y=Settings.FLATLAND_SIZE-1;
        else if(y == Settings.FLATLAND_SIZE)
            y=0;
    }

    public void eat(int content) {
        if(content == Constants.FLATLAND_CELLTYPE_FOOD)
            foodCount++;
        else if(content == Constants.FLATLAND_CELLTYPE_POISON)
            poisonCount++;
    }
}
