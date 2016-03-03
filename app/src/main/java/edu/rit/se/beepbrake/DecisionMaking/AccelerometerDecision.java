package edu.rit.se.beepbrake.DecisionMaking;

/**
 * Created by Ryan on 3/3/16.
 */

import edu.rit.se.beepbrake.Segment.Constants;

public class AccelerometerDecision extends Decision{

    public AccelerometerDecision(DecisionManager manager){

        super(manager);
    }

    public void run(){
        double zVal;
        double oldzVal;
        double diffzVal;

        while(!this.interrupted()){
            requestSegment();

            zVal = (Double)curSeg.getDataObject(Constants.ACCEL_Z);
            oldzVal = (Double)curSeg.getPrevSeg().getDataObject(Constants.ACCEL_Z);
            diffzVal = oldzVal - zVal;

            if(diffzVal < -7 || diffzVal > 7){
                warn();
            }

        }
    }

    private void warn(){
        curSeg.addDataObject("CrashDetected", "true");
        manager.warn();
    }
}
