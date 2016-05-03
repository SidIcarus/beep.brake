package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonWriter;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedOutputStream;
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
import edu.rit.se.beepbrake.utils.PreferencesHelper;
import edu.rit.se.beepbrake.utils.Utils;
import edu.rit.se.beepbrake.web.WebManager;

// TODO: Have diskwriter pull from SharedPreferences for writing
public class DiskWriter extends Thread implements Runnable {
    private final String path;

    /** Bytes to write */
    @Nullable private final MatOfByte buf = new MatOfByte();

    /** Application context. Needed to make private app files */
    @NonNull private Context context;

    /** Signal that all segments for this event have been put into the queue */
    private boolean endReached;

    /** ID for the event being written - same as timestamp of first segment */
    private long eventID;

    /** Queue of segments to write to disk */
    private ConcurrentLinkedQueue<Segment> segments;

    public DiskWriter(long eventID, Context context) {
        this.eventID = eventID;
        // TODO: Make this is a soft or weak reference
        this.context = context;
        this.path = PreferencesHelper.getWritePath(context);
        this.segments = new ConcurrentLinkedQueue<>();
        this.endReached = false;
    }

    /** Signal that the event is over and all segments have been added */
    public void signalEnd() { endReached = true; }

    /** Add a segment to the queue to be written to disk */
    public void addSegment(Segment seg) { segments.add(seg); }

    /**
     * Starts the thread running a diskwriter
     * Parses backwards through the segments passed to it saving each's contents to disk
     * Dereferences as it goes to cut down on memory usage
     */
    public void run() {
        Log.d("bufer System", "DiskWriter starting");
        //File name format: 'androidID'_'eventID'

        String androidID = Preferences.getSetting(context, "androidid", "42");

        String baseName = androidID + "_" + String.valueOf(eventID);
        String fileName = baseName + ".json";
        FileOutputStream fos;
        ZipOutputStream zos;
        StringBuilder json = new StringBuilder();

        try {
            File writeDir = new File(path);
            if (!writeDir.exists()) writeDir.mkdirs();

            //open the Zip File
            FileOutputStream zip_file = new FileOutputStream(path + baseName + ".zip");
            zos = new ZipOutputStream(new BufferedOutputStream(zip_file));

            // TODO: Change here to pull from shared prefs
            String appVersion =
                context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

            writeJson(zos, appVersion, androidID);


            ZipEntry jsonfile = new ZipEntry(fileName);
            zos.putNextEntry(jsonfile);
            zos.write(json.toString().getBytes());
            zos.close();

            //TODO: implement producer consumer pattern with webmanager
            // This is the producer

            //Queue Upload
            // TODO: make this not get called here but have it webmanager listen for when  all
            // the paths have been all written. Use mutex.
            WebManager.getInstance().triggerUpload();

        } catch (Exception e) {
            Log.d("bufer System", "Error in main DiskWriter loop");
            e.printStackTrace();
        }
    }

    public void writeJson(ZipOutputStream zip, String appVersion, String androidID)
        throws IOException, InterruptedException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(zip, "UTF-8"));

        writer.setIndent("  ");
        writer.beginObject();

        writeDeviceInfo(writer, appVersion, androidID);
        writeConfigObject(writer);
        writeSegmentArray(writer, zip);

        writer.endObject()
              .close();

    }

    public void writeDeviceInfo(JsonWriter writer, String appVersion, String androidID)
        throws IOException {
        writer.name("deviceid").value(androidID)
              .name("hardware").value(Build.DISPLAY)
              .name("osversion").value(Build.VERSION.RELEASE)
              .name("appversion").value(appVersion)
              .name("eventdata").value(String.valueOf(eventID))
              .name("timezone").value(TimeZone.getDefault().getID());
    }

    public void writeConfigObject(JsonWriter writer) throws IOException {
        Resources res = context.getResources();
        int wtID = R.integer.warningtime;
        writer.name("configuration").beginObject()
              .name(Utils.resToName(res, wtID)).value(res.getInteger(wtID))
              //TODO: add other configuration stuff here
              .endObject();
    }

    public void writeSegmentArray(JsonWriter writer, ZipOutputStream zip)
        throws IOException, InterruptedException {
        writer.name("segments").beginArray();

        Log.d("bufer system", "Starting to check for segments");
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

                writeSegmentObject(writer, seg, zip);
            }
        }
        Log.d("bufer system", "Diskwriter checking queue after being notified endReached");
        //We've been notified that all segments are in the queue, but we may not have written all of
        // them to disk yet
        while ((seg = segments.poll()) != null) writeSegmentObject(writer, seg, zip);
    }

    /**
     * Writes a single segment to the given file
     *
     * @param seg - the segment to read
     */
    public void writeSegmentObject(JsonWriter writer, Segment seg, ZipOutputStream zip)
        throws IOException {
        Log.d("System.Buffer", "Writing segmentObject");

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

            imgToZip(zip, imgName, filepath, img);
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
    }

    private void imgToZip(ZipOutputStream zip, String imgName, String filepath, Mat img)
        throws IOException {
        MatOfInt param = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);

        Imgcodecs.imencode(filepath, img, buf, param);

        ZipEntry ze = new ZipEntry(imgName);
        zip.putNextEntry(ze);
        zip.write(buf.toArray());

        Log.d("System.Buffer", "Wrote image to zip");
    }
}
