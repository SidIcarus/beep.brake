package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonWriter;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.segment.Segment;
import edu.rit.se.beepbrake.utils.Preferences;
import edu.rit.se.beepbrake.utils.Utils;
import edu.rit.se.beepbrake.web.WebManager;

public class DiskWriter extends Thread {
    private final String path;

    /** Bytes to write */
    @Nullable private final MatOfByte mBuffer = new MatOfByte();

    /** Application context. Needed to make private app files */
    @NonNull private Context context;

    /** Signal that all segments for this event have been put into the queue */
    private boolean endReached;

    /** ID for the event being written - same as timestamp of first segment */
    private long eventID;

    /** Queue of segments to write to disk */
    private ConcurrentLinkedQueue<Segment> segments;

    private final String logTag = "System.Buffer";

    public DiskWriter(long eventID, @NonNull Context context) {
        this.eventID = eventID;
        // TODO: Make this is a soft or weak reference
        this.context = context;
        this.path = Utils.getWritePath(context);
        this.segments = new ConcurrentLinkedQueue<>();
        this.endReached = false;
    }

    /** Add a segment to the queue to be written to disk */
    public void addSegment(Segment seg) { segments.add(seg); }

    // TODO: Get clarification on the OutputStreams / Writers
    // I feel like I'm doing something redundantly
    private byte[] getJsonBytes(ZipOutputStream zipOut, String appVersion, String androidID)
        throws IOException, InterruptedException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             JsonWriter writer = new JsonWriter(new OutputStreamWriter(byteOut, "UTF-8"))) {

            writer.setIndent("  ");
            writer.beginObject();

            writeDeviceInfo(writer, appVersion, androidID);
            writeConfigObject(writer);
            writeSegmentArray(writer, zipOut);

            writer.endObject()
                  .close();

            byteOut.flush();
            return byteOut.toByteArray();
        }
    }

    private void imgToZip(ZipOutputStream zipOut, String imgName, String filepath, Mat img)
        throws IOException {
        Log.d(logTag, "Writing image to zip");
        MatOfInt param = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);

        Imgcodecs.imencode(filepath, img, mBuffer, param);

        zipOut.putNextEntry(new ZipEntry(imgName));
        if (mBuffer != null) zipOut.write(mBuffer.toArray());

        Log.d(logTag, "Image written to zip.");
    }

    /**
     * Starts the thread running a diskwriter
     * Parses backwards through the segments passed to it saving each's contents to disk
     * Dereferences as it goes to cut down on memory usage
     */
    public void run() {
        Log.d(logTag, "Commencing DiskWriter.run()");
        //File name format: 'androidID'_'eventID'

        Preferences p = Preferences.getInstance();

        String androidID = p.getString("androidid");
        String appVersion = Utils.getAppVersion(context);

        String baseName = androidID + "_" + String.valueOf(eventID);
        String fileName = baseName + ".json";
        ZipOutputStream zipOut;


        File writeDir = new File(path);
        if (!writeDir.exists()) { //noinspection ResultOfMethodCallIgnored
            writeDir.mkdirs();
        }

        try {
            //open the Zip File
            FileOutputStream fileOut = new FileOutputStream(path + baseName + ".zip");
            zipOut = new ZipOutputStream(new BufferedOutputStream(fileOut));


            byte[] jsonBytes = getJsonBytes(zipOut, appVersion, androidID);

            zipOut.putNextEntry(new ZipEntry(fileName));

            zipOut.write(jsonBytes);
            zipOut.flush();
            zipOut.close();

            WebManager.getInstance().triggerUpload();

        } catch (IOException | InterruptedException e) {
            Log.d(logTag, "Error in main DiskWriter loop");
            e.printStackTrace();
        }
    }

    /** Signal that the event is over and all segments have been added */
    public void signalEnd() { endReached = true; }

    private void writeConfigObject(JsonWriter writer) throws IOException {
        Resources res = context.getResources();
        int wtID = R.integer.warningtime;
        writer.name("configuration").beginObject()
              .name(Utils.resToName(res, wtID)).value(res.getInteger(wtID))
              //TODO: add other configuration stuff here
              .endObject();
    }

    private void writeDeviceInfo(JsonWriter writer, String appVer, String androidID)
        throws IOException {
        writer.name("deviceid").value(androidID)
              .name("hardware").value(Build.DISPLAY)
              .name("osversion").value(Build.VERSION.RELEASE)
              .name("appversion").value(appVer)
              .name("eventdata").value(String.valueOf(eventID))
              .name("timezone").value(TimeZone.getDefault().getID());
    }

    private void writeSegmentArray(JsonWriter writer, ZipOutputStream zipOut)
        throws IOException, InterruptedException {
        writer.name("segments").beginArray();

        Log.d(logTag, "Checking for segments");
        //segment contents
        boolean first = true;
        Segment seg;
        while (!endReached) {
            seg = segments.poll();
            //If the list is currently empty, wait, then try again
            if (seg == null) sleep(1000);
            else {
                if (first) first = false;
                // else json.append(",");

                writeSegmentObject(writer, seg, zipOut);
            }
        }
        Log.d(logTag, "Diskwriter checking queue after being notified endReached");
        //We've been notified that all segments are in the queue, but we may not have written all of
        // them to disk yet
        while ((seg = segments.poll()) != null) writeSegmentObject(writer, seg, zipOut);
    }

    /**
     * Writes a single segment to the given file
     *
     * @param seg - the segment to read
     */
    private void writeSegmentObject(JsonWriter writer, Segment seg, ZipOutputStream zipOut)
        throws IOException {
        Log.d(logTag, "Writing segmentObject");

        Long segTime = seg.getCreatedAt();
        writer.beginObject()
              .name("segtime").value(segTime)
              .name("sensordata").beginArray();

        //Segment content
        Mat img = seg.getImg();
        if (img != null) {
            String imgName = String.valueOf(segTime) + ".png";
            String filepath = path + imgName;

            writer.beginObject()
                  .name("key").value("imagename")
                  .name("value").value(filepath)
                  .endObject();

            imgToZip(zipOut, imgName, filepath, img);
        }

        String value;
        for (String key : seg.getKeys()) {
            Object data = seg.getDataObject(key);
            //Need to add Quotation marks around Strings
            value =
                (data.getClass() == String.class) ? "\"" + data.toString() + "\"" : data.toString();

            writer.beginObject()
                  .name("key").value(key)
                  .name("value").value(value)
                  .endObject();
        }
        Log.d(logTag, "segmentObject complete");
    }
}
