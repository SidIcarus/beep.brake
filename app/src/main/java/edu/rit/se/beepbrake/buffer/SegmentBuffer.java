package edu.rit.se.beepbrake.buffer;

import android.content.Context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
     * Whether or not a warning has been triggered: used for saving a full buffer after a warning event
     */
    private boolean warningTriggered;

    /**
     * Maximum amount of time (in millis) between segments to allow before removing old ones
     */
    private final int timediff = 6000;

    /**
     * Lock to ensure the buffer is not saved while a Segment is being added
     */
    private Lock bufferLock;

    /**
     * Constructor
     * Start with empty newest and oldest segments
     */
    public SegmentBuffer(Context con) {
        this.context = con;
        this.newest = null;
        this.oldest = null;
        warningTriggered = false;
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
     * Save the current buffer to file by spawning a DiskWriter thread and passing it the current buffer
     */
    public void save() {
        bufferLock.lock();
        DiskWriter dw = new DiskWriter(newest, context);
        newest = null;
        oldest = null;
        dw.start();
        bufferLock.unlock();
    }

    /**
     * Schedule saving of the next full buffer
     */
    public void triggerSaveAfterWarning() {
        warningTriggered = true;
    }

    /**
     * Go through the segments starting from the oldest and remove any that are not needed anymore
     * Will also handle saving the buffer to disk immediately following a warning event by reading
     * the warningTriggered flag.
     */
    private void prune() {
        while(oldest.getDataObject("time") == null) {//TODO update to use timestamp (needs getter)
            if(warningTriggered) {
                warningTriggered = false;
                save();
                return;
            } else {
                bufferLock.lock();
                oldest.getNextSeg().setPrevSeg(null);
                //TODO delete oldest if possible
                oldest = oldest.getNextSeg();
                bufferLock.unlock();
            }
        }
    }
}
