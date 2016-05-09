package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.constants.SegmentConstants;

import edu.rit.se.beepbrake.segment.Segment;

public class SegmentBuffer {

    /** App context, used for getting file locations */
    private Context context;

    /** The most recently added segment to the buffer */
    @Nullable private Segment newestSeg;

    /** The oldest segment still in the buffer */
    @Nullable private Segment oldestSegment;

    /** Timestamp of the last issued warning, for determining when to stop saving segs to disk */
    private long lastWarningTime;

    /** The timestamp of the first warning for this event */
    private long firstWarningTime;

    /** Lock to ensure the buffer is not saved while a Segment is being added */
    private Lock bufferLock;

    /** Tracking if we are currently saving segments to disk or not */
    private boolean activeWarning;

    @Nullable private DiskWriter activeWriter;

    private final String logTag = "System.Buffer";

    /** Start with empty newestSeg and oldestSegment segments */
    public SegmentBuffer(Context context) {
        this.context = context;
        this.newestSeg = null;
        this.oldestSegment = null;
        activeWarning = false;
        activeWriter = null;
        bufferLock = new ReentrantLock();
    }

    /**
     * Add this segment to the buffer
     * Trims old segments after adding
     *
     * @param seg - The segment to add
     */
    public void addSegment(Segment seg) {
        bufferLock.lock();
        try {
            if (newestSeg == null) {
                newestSeg = seg;
                oldestSegment = seg;
            } else {
                newestSeg.setNextSeg(seg);
                seg.setPrevSeg(newestSeg);
                newestSeg = seg;
                prune();
            }
        } catch (RuntimeException e) {
            e.printStackTrace(); //TODO: Add actual debugging statements
        } finally {
            bufferLock.unlock();
        }
    }

    /** Clear the current buffer. Used to remove stale data when the app is paused. */
    public void clear() {
        bufferLock.lock();
        try {
            if (activeWarning && activeWriter != null) {
                //If a warning is active, flush the buffer into the DiskWriter, then close the event
                while (oldestSegment != newestSeg) {

                    activeWriter.addSegment(oldestSegment);
                    oldestSegment = oldestSegment.getNextSeg();
                    oldestSegment.setPrevSeg(null);
                }
                //Send the last segment
                activeWriter.addSegment(oldestSegment);
                activeWriter.signalEnd();
                activeWarning = false;
            }

            //Reset values to defaults
            oldestSegment = null;
            newestSeg = null;
            activeWriter = null;
        } catch (RuntimeException e) {
            e.printStackTrace(); //TODO: Add actual debugging statements
        } finally { bufferLock.unlock(); }
    }

    /**
     * Get the latest segment from the buffer
     *
     * @return newestSeg - the latest segment
     */
    public Segment getNewestSeg() { return newestSeg; }

    /**
     * Go through the segments starting from the oldestSegment and remove any that are not needed
     * anymore
     * If a warning is active, will send the pruned segments to the active diskwriter
     */
    private void prune() {
        if (newestSeg != null && oldestSegment != null) {
            // Declared for clarity, & declared outside of loop for better performance
            long nCreated, oCreated;
            while (newestSeg.getCreatedAt() - oldestSegment.getCreatedAt() >
                SegmentConstants.timeDiff) {
                bufferLock.lock();
                try {
                    nCreated = newestSeg.getCreatedAt();
                    oCreated = oldestSegment.getCreatedAt();
                    if (activeWarning && activeWriter != null) {
                        //End the warning state if it's been long enough since the last warning
                        // segment, or is too long of an event

                        if (oCreated - lastWarningTime > SegmentConstants.timeDiff ||
                            oCreated - firstWarningTime > SegmentConstants.maxTime)
                        {
                            Log.d(logTag,
                                  "Prune is ending the event: o: " + oCreated + " n: " + nCreated);
                            activeWarning = false;
                            activeWriter.signalEnd();
                            activeWriter = null;
                        } else activeWriter.addSegment(oldestSegment);
                    }
                    //Remove the oldestSegment segment from the list
                    oldestSegment.getNextSeg().setPrevSeg(null);
                    oldestSegment = oldestSegment.getNextSeg();
                } catch (RuntimeException e) {
                    e.printStackTrace(); //TODO: Add actual debugging statements
                } finally { bufferLock.unlock(); }
            }
        }
    }

    /**
     * A warning was triggered, so write event to disk
     * Uses lock to ensure a segment is not pruned while the DiskWriter is being set up
     */
    public void triggerWarning() {
        //Ignore warning if we don't have any segments yet
        if (newestSeg != null) {
            bufferLock.lock();
            try {
                lastWarningTime = newestSeg.getCreatedAt();
                if (!activeWarning) {
                    firstWarningTime = lastWarningTime;
                    if (oldestSegment != null) {
                        activeWriter = new DiskWriter(oldestSegment.getCreatedAt(), context);
                        activeWriter.start();
                    }
                }
                activeWarning = true;
            } catch (RuntimeException e) {
                e.printStackTrace(); //TODO: Add actual debugging statements
            } finally { bufferLock.unlock(); }
        }
    }
}
