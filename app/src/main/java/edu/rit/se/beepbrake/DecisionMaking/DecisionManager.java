package edu.rit.se.beepbrake.DecisionMaking;

//created by RyanBega 2/8/16

import java.util.Date;
import java.util.ArrayList;
import android.media.ToneGenerator;
import android.media.AudioManager;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class DecisionManager {
    private ArrayList<Decision> decisions;
    private Date lastWarn;
    private BufferManager bufMan;

    public DecisionManager(BufferManager bufMan){
        this.bufMan = bufMan;

        decisions = new ArrayList<Decision>();

        //Add decisions to the list e.g. for new sensors or sensor fusion
        decisions.add(new CameraDecision(this, this.bufMan));
        decisions.add(new AccelerometerDecision(this, this.bufMan));

    }

    public void warn(){
        Date curTime = new Date();

        /*
        Magic number needs to be removed and replaced with config value declaring
        time between auditory warnings
        */
            // prevent beeping more than once per second
        if(lastWarn == null || curTime.after(new Date(lastWarn.getTime() + 1000))) {
            //Driver alert beep
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            tone.startTone(ToneGenerator.TONE_DTMF_8, 200);
        }
        bufMan.warningTriggered(); // trigger to that the buffer knows to keep adding event frames
        lastWarn = curTime;
    }

    // eaçh subsystem has its on onResume housecleaning

    public void onResume(){
        for(int i = 0; i < decisions.size(); i++){
            decisions.get(i).setRunning(true);
            (new Thread(decisions.get(i))).start();
        }
    }

    public void onPause(){
        for(int i = 0; i < decisions.size(); i++){
            decisions.get(i).setRunning(false);
        }
    }
}
