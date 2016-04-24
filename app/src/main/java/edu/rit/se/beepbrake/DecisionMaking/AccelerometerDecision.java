package edu.rit.se.beepbrake.DecisionMaking;

// Created by Ryan on 3/3/16.
import android.util.Log;

import edu.rit.se.beepbrake.Segment.Constants;
import edu.rit.se.beepbrake.buffer.BufferManager;

public class AccelerometerDecision extends Decision {

    public AccelerometerDecision(DecisionManager decMan, BufferManager bufMan) {
        super(decMan, bufMan);
    }

    public void run() {
        double zVal, oldzVal, diffzVal;

        /*
         * Analysis loop
         *
         * Each run through the loop is analyzing the data in a new segment
         * Trying to determine whether or not the condition is met for that period
         * of time.
         */

        Log.d("AccelDec", "running");

        while (running) {
            if (!requestSegment()) continue;

            if (curSeg.getDataObject(Constants.ACCEL_Z) != null) {
                zVal = (Double) curSeg.getDataObject(Constants.ACCEL_Z);
                //oldzVal = (Double)curSeg.getPrevSeg().getDataObject(Constants.ACCEL_Z);
                //diffzVal = oldzVal - zVal;

                if (zVal < -18 || zVal > 18) warn();
            }
        }

        Log.d("AccelDec", "Finished");
    }

    private void warn() {
        curSeg.addDataObject("CrashDetected", "true");
        decMan.warn();
    }
}
