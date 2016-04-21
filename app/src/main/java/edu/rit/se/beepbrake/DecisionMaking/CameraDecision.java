package edu.rit.se.beepbrake.DecisionMaking;

//created by Ryan 2/8/16


import android.util.Log;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class CameraDecision extends Decision{

    public CameraDecision(DecisionManager decMan, BufferManager bufMan){

        super(decMan, bufMan);
    }

    public void run(){

        Log.d("CameraDec", "Running");

        //Analysis loop
        while(running) {
            requestSegment();

            //Analysis
        }

        Log.d("CameraDec", "Finished");

    }

    private void warn(){
        curSeg.addDataObject("CameraWarning", "true");
        decMan.warn();
    }
}

