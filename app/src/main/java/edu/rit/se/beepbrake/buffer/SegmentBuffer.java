package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.Segment;

public class SegmentBuffer {

    /**
     * Application context, used for getting file locations
     */
    private Context context;

    /**
     * The segment most recently added to the buffer
     */
    private Segment newest;

    /**
     * The oldest segment still in the buffer
     */
    private Segment oldest;

    /**
     * The timestamp of the last issued warning, for determining when to stop saving segments to disk
     */
    private long lastWarningTime;

    /**
     * Maximum amount of time (in millis) between segments to allow before removing old ones
     */
    private final int timediff = 6000;

    /**
     * Lock to ensure the buffer is not saved while a Segment is being added
     */
    private Lock bufferLock;

    /**
     * Tracking if we are currently saving segments to disk or not
     */
    private boolean activeWarning;

    private DiskWriter activeWriter;

    /**
     * Constructor
     * Start with empty newest and oldest segments
     */
    public SegmentBuffer(Context con) {
        this.context = con;
        this.newest = null;
        this.oldest = null;
        activeWarning = false;
        activeWriter = null;
        bufferLock = new ReentrantLock();
    }

    /**
     * Add this segment to the buffer
     * Trims old segments after adding
     * @param seg - The segment to add
     */
    public void addSegment(Segment seg) {
        bufferLock.lock();
        if(newest == null) {
            newest = seg;
            oldest = seg;
        } else {
            newest.setNextSeg(seg);
            seg.setPrevSeg(newest);
            newest = seg;
            prune();
        }
        bufferLock.unlock();
    }

    /**
     * Get the latest segment from the buffer
     * @return newest - the latest segment
     */
    public Segment getNewest() {
        return newest;
    }

    /**
     * A warning was triggered, so write event to disk
     * Uses lock to ensure a segment is not pruned while the DiskWriter is being set up
     */
    public void triggerWarning() {
        bufferLock.lock();
        lastWarningTime = newest.getCreatedAt();
        activeWriter = new DiskWriter(oldest.getCreatedAt(), context);
        activeWriter.start();
        activeWarning = true;
        bufferLock.unlock();
    }

    /**
     * Go through the segments starting from the oldest and remove any that are not needed anymore
     * If a warning is active, will send the pruned segments to the active diskwriter
     */
    private void prune() {
        while(newest.getCreatedAt() - oldest.getCreatedAt() > timediff) {
            bufferLock.lock();
            if(activeWarning) {
                //End the warning state if it's been long enough since the last warning segment
                if(oldest.getCreatedAt() - lastWarningTime > timediff) {
                    activeWarning = false;
                    activeWriter.signalEnd();
                } else {
                    activeWriter.addSegment(oldest);
                }
            }
            //Remove the oldest segment from the list
            oldest.getNextSeg().setPrevSeg(null);
            oldest = oldest.getNextSeg();
            bufferLock.unlock();
        }
    }
}
