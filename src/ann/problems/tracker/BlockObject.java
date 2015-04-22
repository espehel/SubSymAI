package ann.problems.tracker;

/**
 * Created by espen on 14/04/15.
 */
public class BlockObject {
    public int size;
    public int x;
    public int y;

    public boolean contains(int i) {

        if(i < 0)
            i += TrackerSimulator.width;
        else if(i >= TrackerSimulator.width)
            i -= TrackerSimulator.width;

        //if(i >= x && i <= x+size)
        if(i >= x && i < x+size)
            return true;
        //if(x+size >= TrackerSimulator.width)
        if(x+size-1 >= TrackerSimulator.width)
            //return x+size - TrackerSimulator.width >= i;
            return x+size-1 - TrackerSimulator.width >= i;
        return false;
    }
}