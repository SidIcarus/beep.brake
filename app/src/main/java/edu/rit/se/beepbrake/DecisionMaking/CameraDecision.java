package edu.rit.se.beepbrake.DecisionMaking;

//created by RyanBega 2/8/16



public class CameraDecision extends Decision{

    public CameraDecision(DecisionManager manager){

        super(manager);
    }

    public void run(){
        while(!this.interrupted()){
            //Analysis loop

            requestSegment();
        }
    }

    private void warn(){
        curSeg.addDataObject("CameraWarning", "true");
        manager.warn();
    }
}

