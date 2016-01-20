package edu.rit.se.beepbrake.buffer;

import java.io.File;

import myfirstapp.app.Segment;

public class DiskWriter extends Thread implements Runnable{

    /**
     * Constructor
     *
     */
    public DiskWriter() {

    }

    /**
     * Starts the thread running a diskwriter
     * Parses backwards through the segments passed to it saving each's contents to disk
     * Dereferences as it goes to cut down on memory usage
     */
    public void start() {

    }

    /**
     * Writes a single segment to the given file
     * @param seg - the segment to read
     * @param file - the file to write to
     */
    private void writeSegment(Segment seg, File file) {

    }

}
