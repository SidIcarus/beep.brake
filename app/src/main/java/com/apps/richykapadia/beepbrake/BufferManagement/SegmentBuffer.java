package com.apps.richykapadia.beepbrake.BufferManagement;


import myfirstapp.app.Segment;

public class SegmentBuffer {
    private Segment newest;
    private Segment oldest;

    private final int timediff = 6000;//max milliseconds to keep old segments

    /**
     * Constructor
     * Start with empty newest and oldest segments
     */
    public SegmentBuffer() {
        this.newest = null;
        this.oldest = null;
    }

    /**
     * Add this segment to the buffer
     * Trims old segments after adding
     * @param seg - The segment to add
     */
    public void addSegment(Segment seg) {
        if(newest == null) {
            newest = seg;
            oldest = seg;
        } else {
            newest.setNextSeg(seg);
            newest = seg;
            prune();
        }
    }

    /**
     * Get the latest segment from the buffer
     * @return newest - the latest segment
     */
    public Segment getNewest() {
        return newest;
    }

    /**
     * Save the current buffer to file using the given diskwriter
     * @param dw - diskwriter to send the contents to
     */
    public void save(DiskWriter dw) {

    }

    /**
     * Go through the segments starting from the oldest and remove any that are not needed anymore
     */
    private void prune() {

    }

}
