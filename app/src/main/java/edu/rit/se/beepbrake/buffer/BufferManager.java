package edu.rit.se.beepbrake.buffer;

import myfirstapp.app.Segment;

public class BufferManager {
    private SegmentBuffer buffer;

    /**
     * Constructor
     * Creates an empty buffer
     */
    public BufferManager() {
        buffer = new SegmentBuffer();
    }

    /**
     * Request the latest segment for analyzation (Called by DecisionMaker)
     * @return segment - the most recent segment
     */
    public Segment getNewestSegment() {
        return buffer.getNewest();
    }

    /**
     * Add a segment (Sent from synchronizer)
     * @param seg - new segment
     */
    public void addSegment(Segment seg) {
        buffer.addSegment(seg);
    }

    /**
     * A warning was triggered, so save the buffer to disk, and prepare to save again (need
     * before and after buffers)
     */
    public void warningTriggered() {
        buffer.save();
        buffer.triggerSaveAfterWarning();
    }
}
