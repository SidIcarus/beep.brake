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

    protected boolean requestSegment(){
        Segment requested;

        try {
            while (running) {
                requested = bufMan.getNewestSegment();
                if (requested != null && (curSeg == null || curSeg.getCreatedAt() < requested.getCreatedAt())) {
                    curSeg = requested;
                    return true;
                }
                Thread.sleep(50);
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
