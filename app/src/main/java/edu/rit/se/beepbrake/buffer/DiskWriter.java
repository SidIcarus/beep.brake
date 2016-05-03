package edu.rit.se.beepbrake.buffer;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.JsonWriter;
import android.util.Log;

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
    private final MatOfByte buf = new MatOfByte();

    /** Application context. Needed to make private app files */
    private Context context;

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

        // String deviceID = Preferences.getSetting(context, "androidid", "42");

        String androidID =
            Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
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

            //Close of segments and json
            json.append("]}");

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
        throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(zip, "UTF-8"));
        // writer.setIndent(getString(R.string.two_space_indent));
        writer.setIndent("  ");

        writer.beginObject();

        writeDeviceObject(writer, appVersion, androidID, false);
        writeConfigurationObject(writer, false);

        writeSegmentArray(writer, );

        writer.endObject();

        writer.beginObject();

        writeDeviceObject(writer, appVersion, androidID, true);
        writeConfigurationObject(writer, true);

        writer.endObject();

        writer.close();
    }

    public void writeDeviceObject(JsonWriter writer, String appVersion, String androidID,
        boolean newJSON) throws IOException {

        if (!newJSON) {
            writer.name("deviceid").value(androidID)
                  .name("hardware").value(Build.DISPLAY)
                  .name("osversion").value(Build.VERSION.RELEASE)
                  .name("appversion").value(appVersion)
                  .name("eventdata").value(String.valueOf(eventID))
                  .name("timezone").value(TimeZone.getDefault().getID());
        } else {
            writer.name("device");

            writer.beginObject()
                  .name("id").value(androidID)
                  .name("hardware").value(Build.DISPLAY)
                  .name("os").value(Build.VERSION.RELEASE)
                  .name("app_version").value(appVersion)
                  .name("event_data").value(String.valueOf(eventID))
                  .name("timezone").value(TimeZone.getDefault().getID())
                  .name("write_path").value(path);

            writer.endObject();
        }
    }

    public void writeConfigurationObject(JsonWriter writer, boolean newJSON) throws IOException {
        writer.name("configuration");

        String wt = newJSON ? "warning_time" : "warningtime";
        writer.beginObject()
              .name("wt").value(10);

        // TODO: add other configuration stuff here

        writer.endObject();
    }

    public void writeSegmentArray(JsonWriter writer, Segment seg, ZipOutputStream zip,
        boolean newJSON) throws IOException {
        writer.name("segments");

        Log.d("bufer system", "Starting to check for segments");
        //segment contents
        boolean first = true;
        Segment s;
        while (!endReached) {
            s = segments.poll();
            //If the list is currently empty, wait, then try again
            if (s == null) sleep(1000);
            else {
                if (first) first = false;
                else json.append(",");

                writeSegment(s, json, zos);
            }
        }
        Log.d("bufer system", "Diskwriter checking queue after being notified endReached");
        //We've been notified that all segments are in the queue, but we may not have
        // written all of them to disk yet
        while ((s = segments.poll()) != null) {
            json.append(",");
            writeSegment(s, json, zos);
        }

        writer.beginArray()
              .name("time").value(String.valueOf(seg.getCreatedAt()));

    }

    /*
        String[] uglyJson = getResources().getStringArray(R.array.uglyJson);

        for (String name : uglyJson) {
            Class<?> type = String.class;
            Object val = "default value";
            switch (name) {
                case "deviceid":
                    type = String.class;
                    val = Settings.Secure.getString(getContentResolver(), Settings.Secure
                    .ANDROID_ID);
                    break;
                case "hardware":
                    break;
                case "osversion":
                    break;
                case "appversion":
                    break;
                case "timezone":
                    break;
            }
        }

     */

    public void writeSegmentObject(JsonWriter writer) {

    }

    /**
     * Writes a single segment to the given file
     *
     * @param seg - the segment to read
     */
    private void writeSegment(Segment seg, StringBuilder json, ZipOutputStream zip)
        throws IOException {
        Log.d("bufer system", "Start of writeSegment");
        //Segment header
        // TODO: change to pull from string resources
        json.append("{\"segtime\":" + String.valueOf(seg.getCreatedAt()));
        json.append(",\"sensordata\": [");

        //Segment content
        if (seg.getImg() != null) {
            String imgName = String.valueOf(seg.getCreatedAt()) + ".png";
            String filepath = path + imgName;
            MatOfInt param = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
            //Imgcodecs.imwrite(filepath, seg.getImg(), param);

            Imgcodecs.imencode(filepath, seg.getImg(), buf, param);

            ZipEntry ze = new ZipEntry(imgName);
            zip.putNextEntry(ze);
            zip.write(buf.toArray());

            json.append("{\"key\":\"imagename\",\"value\":\"" + filepath + "\"}");
            Log.d("bufer system", "Wrote image to zip");
        }

        String keyValue;
        for (String k : seg.getKeys()) {
            json.append(",{\"key\":\"" + k + "\",\"value\":");
            Object data = seg.getDataObject(k);
            //Need to add Quotation marks around Strings
            keyValue =
                (data.getClass() == String.class) ? "\"" + data.toString() + "\"" : data.toString();

            json.append(keyValue);
            json.append("}");
        }

        //Segment closing
        json.append("]}");
    }
}
