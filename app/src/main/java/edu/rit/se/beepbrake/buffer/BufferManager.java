package edu.rit.se.beepbrake.buffer;

import android.content.Context;

import edu.rit.se.beepbrake.segment.Segment;

public class BufferManager {
    private SegmentBuffer buffer;

    /** Constructor: Creates an empty buffer */
    public BufferManager(Context context) { buffer = new SegmentBuffer(context); }

    /**
     * Add a segment (Sent from synchronizer)
     *
     * @param seg - new segment
     */
    public void addSegment(Segment seg) { buffer.addSegment(seg); }

    /**
     * Request the latest segment for analyzation (Called by DecisionMaker)
     *
     * @return segment - the most recent segment
     */
    public Segment getNewestSegment() { return buffer.getNewestSeg(); }

    /** Clear the buffer while the app is not in use */
    public void onPause() { buffer.clear(); }

    /**
     * A warning was triggered, so save the buffer to disk, and prepare to save again (need
     * before and after buffers)
     */
    public void warningTriggered() { buffer.triggerWarning(); }

}
