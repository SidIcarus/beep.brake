package edu.rit.se.beepbrake.DecisionMaking;

//created by RyanBega 2/8/16

import java.util.Date;
import java.util.ArrayList;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class DecisionManager {
    private ArrayList<Decision> decisions;
    private Date lastWarn;
    private BufferManager bufMan;

    public DecisionManager(BufferManager bufMan){
        this.bufMan = bufMan;

        decisions = new ArrayList<Decision>();

        //Add decisions to the list
        decisions.add(new CameraDecision(this, this.bufMan));
        decisions.add(new AccelerometerDecision(this, this.bufMan));

        //Start all decision threads
        for(int i = 0; i < decisions.size(); i++){
            decisions.get(i).start();
        }
    }

    public void warn(){
        Date curTime = new Date();

        /*
        Magic number needs to be removed and replaced with config value declaring
        time between auditory warnings
        */

        if(lastWarn == null || curTime.after(new Date(lastWarn.getTime() + 1000))) {
            //TODO: Alert UI
        }
        bufMan.warningTriggered();
        lastWarn = curTime;
    }

    public void onResume(){
        for(int i = 0; i < decisions.size(); i++){
            decisions.get(i).start();
        }
    }

    public void onPause(){
        for(int i = 0; i < decisions.size(); i++){
            decisions.get(i).interrupt();
        }
    }
}