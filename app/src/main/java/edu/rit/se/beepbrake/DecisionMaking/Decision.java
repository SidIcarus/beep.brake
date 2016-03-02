/**
 * Created by Ryan Bega on 2/10/16.
 */

package edu.rit.se.beepbrake.DecisionMaking;

import edu.rit.se.beepbrake.Segment.Segment;

public class Decision extends Thread{

    protected DecisionManager manager;
    protected Segment curSeg;

    public Decision(DecisionManager manager){
        this.manager = manager;
    }

    protected boolean requestSegment(){
        Segment requested;

        try {
            while (true) {
                //TODO: Request segment from BufferManager
                if (curSeg.getTimestamp() < requested.getTimestamp()) {
                    curSeg = requested;
                    return true;
                }
                this.wait(50);
            }
        }catch(InterruptedException e){
            return false;
        }
    }
}
