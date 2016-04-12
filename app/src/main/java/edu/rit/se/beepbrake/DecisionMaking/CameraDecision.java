package edu.rit.se.beepbrake.DecisionMaking;

//created by Ryan 2/8/16


import edu.rit.se.beepbrake.buffer.BufferManager;

public class CameraDecision extends Decision{

    public CameraDecision(DecisionManager decMan, BufferManager bufMan){

        super(decMan, bufMan);
    }

    public void run(){

        //Analysis loop
        while(!this.interrupted()){
            if(running) {
                requestSegment();

                //Analysis
            }
        }
    }

    private void warn(){
        curSeg.addDataObject("CameraWarning", "true");
        decMan.warn();
    }
}

