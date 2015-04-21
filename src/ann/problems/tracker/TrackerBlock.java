package ann.problems.tracker;

import ann.core.ANNPhenotype;
import ea.core.Settings;
import utils.Constants;

/**
 * Created by espen on 14/04/15.
 */
public class TrackerBlock extends BlockObject{

    public TrackerBlock() {
        x=0;
        y=TrackerSimulator.height;
        size=5;
    }

    public void reset() {
        x=0;
        y=TrackerSimulator.height-1;
    }

    public int getSensorIndex(int i) {
        return 0;
    }

    public void move(int action, double magnitude) {
        //TODO: fix wrap around
        int speed = 0;
        if(action == Constants.STRAFE_LEFT)
            speed = -1 * getMagnitude(magnitude);
        if(action == Constants.STRAFE_RIGHT)
            speed = 1 * getMagnitude(magnitude);

        x += speed;
        if(ann.core.Settings.WRAP_AROUND) {
            if (x < 0)
                x += 30;
            else if (x >=30)
                x-=30;
        }
        else{
            if (x < 0)
                x = 0;
            else if (x >=30)
                x = 29;
        }
    }

    public int getMagnitude(double magnitude) {

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
