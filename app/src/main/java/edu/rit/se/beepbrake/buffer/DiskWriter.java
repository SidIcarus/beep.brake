package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.rit.se.beepbrake.Segment.Segment;

public class DiskWriter extends Thread implements Runnable{
    private final String path;
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
        this.path = Environment.getExternalStorageDirectory() + "/write_segments/"  + String.valueOf(eventId) + "/";
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
        //File name format: 'androidID'_'eventID'
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String baseName = deviceId + "_" + String.valueOf(eventId);
        String fileName = baseName + ".json";
        ZipOutputStream zos;
        StringBuilder json = new StringBuilder();

        try {
            //fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            File writeDir = new File(path);
            if(!writeDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                writeDir.mkdirs();
            }

            //open the Zip File
            FileOutputStream zip_file = new FileOutputStream(path + baseName + ".zip");
            zos = new ZipOutputStream(new BufferedOutputStream(zip_file));

            //Print file header
            json.append("{\"deviceid\":\"").append(deviceId).append("\",");
            //hardware type
            json.append("\"hardware\":\"").append(Build.DISPLAY).append("\",");
            //OS version
            json.append("\"osversion\":\"").append(Build.VERSION.RELEASE).append("\",");
            //App version
            json.append("\"appversion\":\"").append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName).append("\",");
            //Local event ID
            json.append("\"eventdata\":").append(String.valueOf(eventId)).append(",");
            //Local Timezone
            json.append("\"timezone\":\"").append(TimeZone.getDefault().getID()).append("\",");
            writeConfiguration(json);

            //Open of segments section
            json.append("\"segments\": [");

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
                        json.append(",");
                    }
                    writeSegment(s, json, zos);
                }
            }
            //We've been notified that all segments are in the queue, but we may not have
            // written all of them to disk yet
            while((s = segments.poll()) != null) {
                json.append(",");
                writeSegment(s, json, zos);
            }

            //Close of segments and json
            json.append("]}");

            //Put the JSON file in the zip
            ZipEntry jsonfile = new ZipEntry(fileName);
            zos.putNextEntry(jsonfile);
            zos.write(json.toString().getBytes());

            //Close the zip
            zos.close();

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

    private void writeConfiguration(StringBuilder json) throws IOException {
        //TODO actually read from configuration file
        json.append("\"configuration\": {");

        //Each configuration item
        json.append("\"warningtime\":10");

        //Close of config section
        json.append("},");
    }

    /**
     * Writes a single segment to the given file
     * @param seg - the segment to read
     * @param json - the JSON string to append to
     * @param zip - The zip archive to put images into
     */
    private void writeSegment(Segment seg, StringBuilder json, ZipOutputStream zip) throws IOException{
        //Segment header
        json.append("{\"segtime\":").append(String.valueOf(seg.getCreatedAt()));
        json.append(",\"sensordata\": [");

        //Segment content
        if( seg.getImg() != null){
            String imgName = String.valueOf(seg.getCreatedAt()) + ".png";
            String filepath = path + imgName;
            MatOfInt param = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);

            //Convert image into a ByteArray and put it into the zip
            MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(filepath, seg.getImg(), buf, param);
            ZipEntry ze = new ZipEntry(imgName);
            zip.putNextEntry(ze);
            zip.write(buf.toArray());

            //Add image name to JSON
            json.append("{\"key\":\"imagename\",\"value\":\"").append(filepath).append("\"}");
        }
        for(String k : seg.getKeys()) {
            json.append(",{\"key\":\"").append(k).append("\",\"value\":");
            Object data = seg.getDataObject(k);
            //Need to add Quotation marks around Strings
            if(data.getClass() == String.class) {
                json.append("\"").append(data.toString()).append("\"");
            } else {
                json.append(data.toString());
            }
            json.append("}");
        }

        //Segment closing
        json.append("]}");
    }

}
