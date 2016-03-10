package edu.rit.se.beepbrake.DecisionMaking;

//created by RyanBega 2/8/16


import edu.rit.se.beepbrake.buffer.BufferManager;

public class CameraDecision extends Decision{

    public CameraDecision(DecisionManager decMan, BufferManager bufMan){

        super(decMan, bufMan);
    }

    public void run(){
        while(!this.interrupted()){
            requestSegment();

            //Analysis loop
        }
    }

    private void warn(){
        curSeg.addDataObject("CameraWarning", "true");
        decMan.warn();
    }
}

