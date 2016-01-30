package edu.rit.se.beepbrake.buffer;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.TimeZone;

import edu.rit.se.beepbrake.Segment;

public class DiskWriter extends Thread implements Runnable{
    private Segment head;

    /**
     * Constructor
     *
     */
    public DiskWriter(Segment head) {
        this.head = head;
    }

    /**
     * Starts the thread running a diskwriter
     * Parses backwards through the segments passed to it saving each's contents to disk
     * Dereferences as it goes to cut down on memory usage
     */
    public void start() {
        String fileName = Calendar.getInstance().getTime().toString();
        FileOutputStream fos = null;//TODO actually get a fileStream (needs Context)

        while(head != null) {
            head.setNextSeg(null);
            writeSegment(head, fos);
            head = head.getPrevSeg();
        }
    }

    /**
     * Writes a single segment to the given file
     * @param seg - the segment to read
     * @param file - the file to write to
     */
    private void writeSegment(Segment seg, FileOutputStream file) {

    }

}
