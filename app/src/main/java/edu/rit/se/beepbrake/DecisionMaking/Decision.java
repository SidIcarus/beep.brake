/**
 * Created by Ryan Bega on 2/10/16.
 */

package edu.rit.se.beepbrake.DecisionMaking;

import edu.rit.se.beepbrake.Segment.Segment;
import edu.rit.se.beepbrake.buffer.BufferManager;

public class Decision extends Thread{

    protected DecisionManager decMan;
    protected BufferManager bufMan;
    protected Segment curSeg;

    public Decision(DecisionManager decMan, BufferManager bufMan){
        this.decMan = decMan;
        this.bufMan = bufMan;
    }

    protected boolean requestSegment(){
        Segment requested;

        try {
            while (true) {
                requested = bufMan.getNewestSegment();
                if (requested != null && (curSeg == null || curSeg.getCreatedAt() < requested.getCreatedAt())) {
                    curSeg = requested;
                    return true;
                }
                this.sleep(50);
            }
        }catch(InterruptedException e){
            return false;
        }
    }
}
