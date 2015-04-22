package ann.problems.tracker;

import utils.Constants;

/**
 * Created by espen on 14/04/15.
 */
public class TrackerBlock extends BlockObject{

    public boolean polled;

    public TrackerBlock() {
        x=0;
        y=TrackerSimulator.height;
        size=5;
        polled = false;
    }

    public void reset() {
        x=0;
        y=TrackerSimulator.height-1;
    }

    public int getSensorIndex(int i) {
        return 0;
    }

    public void move(int action, double magnitude) {
        int speed = 0;
        if(action == Constants.TRACKER_STRAFE_LEFT)
            speed = -1 * getMagnitude(magnitude);
        if(action == Constants.TRACKER_STRAFE_RIGHT)
            speed = 1 * getMagnitude(magnitude);

        x += speed;
        if(ann.core.Settings.WRAP_AROUND) {
            if (x < 0)
                x += TrackerSimulator.width;
            else if (x >=TrackerSimulator.width)
                x-=TrackerSimulator.width;
        }
        else{
            if (x < 0)
                x = 0;
            else if (x > TrackerSimulator.width - size -1)
                x = TrackerSimulator.width - size - 1;
        }
    }

    public int getMagnitude(double magnitude) {

        //TODO: this can be tweaked
        if(magnitude<0.0)
            return 0;
        if(magnitude<0.25)
            return 1;
        if(magnitude<0.5)
            return 2;
        if(magnitude<0.75)
            return 3;
        return 4;

    }
}
