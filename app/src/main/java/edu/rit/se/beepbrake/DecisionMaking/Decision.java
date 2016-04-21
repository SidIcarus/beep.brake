/**
 * Created by Ryan Bega on 2/10/16.
 */

package edu.rit.se.beepbrake.DecisionMaking;

import edu.rit.se.beepbrake.Segment.Segment;
import edu.rit.se.beepbrake.buffer.BufferManager;

abstract class Decision implements Runnable{

    protected DecisionManager decMan;
    protected BufferManager bufMan;
    protected Segment curSeg;
    protected volatile boolean running;

    public Decision(DecisionManager decMan, BufferManager bufMan){
        this.decMan = decMan;
        this.bufMan = bufMan;
        this.running = true;
    }

    protected boolean requestSegment(){  // polls for the next available segment
        Segment requested;

        try {
            while (running) {
                requested = bufMan.getNewestSegment(); //get newest segment from buffer
                if (requested != null && (curSeg == null || curSeg.getCreatedAt() < requested.getCreatedAt())) {
                    curSeg = requested;
                    return true;  // at this point we have a good segment and returns true
                }
                Thread.sleep(50); // don't request another segment for at least 50 ms
            }
        }catch(InterruptedException e){
            return false;
        }
        return false;
    }

    protected void setRunning(boolean running){
        this.running = running;
    }
}
