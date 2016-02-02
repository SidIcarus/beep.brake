package edu.rit.se.beepbrake.buffer;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import edu.rit.se.beepbrake.Segment;

public class DiskWriter extends Thread implements Runnable{
    private Segment head;
    private Context context;

    /**
     * Constructor
     */
    public DiskWriter(Segment head, Context con) {
        this.context = con;
        this.head = head;
    }

    /**
     * Starts the thread running a diskwriter
     * Parses backwards through the segments passed to it saving each's contents to disk
     * Dereferences as it goes to cut down on memory usage
     */
    public void start() {
        String fileName = Calendar.getInstance().getTime().toString();
        FileOutputStream fos;

        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            while(head != null) {
                head.setNextSeg(null);
                writeSegment(head, fos);
                head = head.getPrevSeg();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a single segment to the given file
     * @param seg - the segment to read
     * @param file - the file to write to
     */
    private void writeSegment(Segment seg, FileOutputStream file) throws IOException{
        //Segment header
        file.write(String.valueOf(seg.getCreatedAt()).getBytes());
        file.write(String.valueOf('\n').getBytes());

        //Segment content
        for(String k : seg.getKeys()) {
            file.write(String.valueOf('\t').getBytes());
            file.write(k.getBytes());
            file.write(String.valueOf(':').getBytes());
            file.write(seg.getDataObject(k).toString().getBytes());
            file.write(String.valueOf('\n').getBytes());
        }
    }

}
