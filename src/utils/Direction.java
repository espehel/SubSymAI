package utils;


/**
 * Created by espen on 02/04/15.
 */
public enum Direction {
    NORTH(-1,0), SOUTH(1,0), EAST(0,1), WEST(0,-1);

    public final int x;
    public final int y;


    Direction(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Direction left(){
        switch (this){
            case NORTH: return WEST;
            case WEST: return SOUTH;
            case SOUTH: return EAST;
            case EAST: return NORTH;
        }
        return null;
    }
    public Direction right(){
        switch (this){
            case NORTH: return EAST;
            case WEST: return NORTH;
            case SOUTH: return WEST;
            case EAST: return SOUTH;
        }
        return null;
    }

    @Override
    public String toString() {
        switch (this){
            case NORTH: return "North";
            case WEST: return "West";
            case SOUTH: return "South";
            case EAST: return "East";
        }
        return null;
    }
}
