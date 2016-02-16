package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.provider.Settings;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.rit.se.beepbrake.Segment;

public class DiskWriter extends Thread implements Runnable{
    /**
     * Application context. Needed to make private app files
     */
    private Context context;

    /**
     * Signal that all segments for this event have been put into the queue
     */
    private boolean endReached;

    /**
     * ID for the event being written - same as timestamp of first segment
     */
    private long eventId;

    /**
     * Queue of segments to write to disk
     */
    private ConcurrentLinkedQueue<Segment> segments;

    /**
     * Constructor
     */
    public DiskWriter(long id, Context con) {
        this.eventId = id;
        this.context = con;
        this.segments = new ConcurrentLinkedQueue<>();
        this.endReached = false;
    }

    /**
     * Starts the thread running a diskwriter
     * Parses backwards through the segments passed to it saving each's contents to disk
     * Dereferences as it goes to cut down on memory usage
     */
    public void run() {
        //File name = 'androidID'_'eventID'
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String fileName = deviceId + "_" + String.valueOf(eventId);
        FileOutputStream fos;

        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            //Print file header
            fos.write(String.valueOf("{\"deviceid\":\"" + deviceId + "\",").getBytes());
            //TODO:
            //hardware type
            //OS version
            //App version
            fos.write(String.valueOf("\"eventdata\":" + String.valueOf(eventId) + ",").getBytes());
            writeConfiguration(fos);

            //Open of segments section
            fos.write(String.valueOf("\"segments\": [").getBytes());

            //segment contents
            boolean first = true;
            Segment s;
            while(!endReached) {
                s = segments.poll();
                if(s == null) {
                    //If the list is currently empty, wait, then try again
                    sleep(1000);
                } else {
                    if(first) {
                        first = false;
                    } else {
                        fos.write(String.valueOf(',').getBytes());
                    }
                    writeSegment(s, fos);
                }
            }
            //We've been notified that all segments are in the queue, but we may not have
            // written all of them to disk yet
            while((s = segments.poll()) != null) {
                fos.write(String.valueOf(',').getBytes());
                writeSegment(s, fos);
            }

            //Close of segments and json
            fos.write(String.valueOf("]}").getBytes());

            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Signal that the event is over and all segments have been added
     */
    public void signalEnd() {
        endReached = true;
    }

    /**
     * Add a segment to the queue to be written to disk
     * @param seg - segment to add
     */
    public void addSegment(Segment seg) {
        segments.add(seg);
    }

    private void writeConfiguration(FileOutputStream fos) throws IOException {
        //TODO actually read from configuration file
        fos.write(String.valueOf("\"configuration\": {").getBytes());
        //Each configuration item
        fos.write(String.valueOf("\"warningtime\":10").getBytes());

        //Close of config section
        fos.write(String.valueOf("},").getBytes());
    }

    /**
     * Writes a single segment to the given file
     * @param seg - the segment to read
     * @param file - the file to write to
     */
    private void writeSegment(Segment seg, FileOutputStream file) throws IOException{
        //Segment header
        file.write(String.valueOf("{\"segtime\":" + String.valueOf(seg.getCreatedAt())).getBytes());
        file.write(String.valueOf(",\"sensordata\": [").getBytes());

        //Segment content
        //TODO actually read image
        file.write(String.valueOf("{\"key\":\"imagename\",\"value\":\"/test/img.png\"}").getBytes());
        for(String k : seg.getKeys()) {
            file.write(String.valueOf(",{\"key\":\"" + k + "\",\"value\":").getBytes());
            Object data = seg.getDataObject(k);
            //Need to add Quotation marks around Strings
            if(data.getClass() == String.class) {
                file.write(String.valueOf("\"" + data.toString() + "\"").getBytes());
            } else {
                file.write(data.toString().getBytes());
            }
            file.write(String.valueOf("}").getBytes());
        }

        //Segment closing
        file.write(String.valueOf("]}").getBytes());
    }

}
