package edu.rit.se.beepbrake.decisionMaking;

//created by RyanBega 2/8/16

import android.media.AudioManager;
import android.media.ToneGenerator;

import java.util.ArrayList;
import java.util.Date;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class DecisionManager {
    private ArrayList<Decision> decisions;
    private Date lastWarn;
    private BufferManager bufMan;

    public DecisionManager(BufferManager bufMan) {
        this.bufMan = bufMan;

        decisions = new ArrayList<Decision>();

        //Add decisions to the list
        decisions.add(new CameraDecision(this, this.bufMan));
        decisions.add(new AccelerometerDecision(this, this.bufMan));

    }

    // TODO: have the call to change the color of the square to red here
    public void warn() {
        Date curTime = new Date();

        /*
        Magic number needs to be removed and replaced with config value declaring
        time between auditory warnings
        */

        if (lastWarn == null || curTime.after(new Date(lastWarn.getTime() + 1000))) {
            //Driver alert beep
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            tone.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 200);
        }
        bufMan.warningTriggered();
        lastWarn = curTime;
    }

    public void onResume() {
        for (int i = 0; i < decisions.size(); i++) {
            decisions.get(i).setRunning(true);
            (new Thread(decisions.get(i))).start();
        }
    }

    public void onPause() {
        for (int i = 0; i < decisions.size(); i++) {
            decisions.get(i).setRunning(false);
        }
    }
}
