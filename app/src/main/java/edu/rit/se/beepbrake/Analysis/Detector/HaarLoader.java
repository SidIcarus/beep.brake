package edu.rit.se.beepbrake.Analysis.Detector;

import android.content.Context;
import android.util.Log;

import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.rit.se.beepbrake.R;

/**
 * Created by richykapadia on 4/13/16.
 * <p/>
 * Helper class used to load the Haar onCreation
 */
public class HaarLoader {

    private static String TAG = "HaarLoader";
    private static HaarLoader instance;

    // haar params
    private Size trainingSize;
    private double scaleFactor;
    private int minNeighbor, flag;

    private HaarLoader() { }

    public static HaarLoader getInstance() {
        if (instance == null) instance = new HaarLoader();

        return instance;
    }

    private static CascadeClassifier loadCascade(Context context, String xml, int resourceId) {
        CascadeClassifier cascadeClassifier = null;
        // overall, what this class does is load the xml file and puts it in memory
        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(resourceId);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, xml);
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (cascadeClassifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                cascadeClassifier = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
        return cascadeClassifier;

    }

    public CascadeClassifier loadHaar(Context context, cascades haar) {
        String xml;
        int id; //tells where the file is (used by Android)
        switch (haar) {
            case VISIONARY_CAR_TRUCK:
                xml = "visionarynet_cars_and_truck_cascade_web_haar.xml";
                id = R.raw.visionarynet_cars_and_truck_cascade_web_haar;
                this.trainingSize = new Size(32, 22);
                // Doesn't find anything!
                this.scaleFactor = 1.2;
                this.minNeighbor = 5;
                this.flag = 1;

                return loadCascade(context, xml, id);

            case OPENCV_FULLBODY:
                xml = "haarcascade_fullbody.xml";
                id = R.raw.haarcascade_fullbody;
                this.trainingSize = new Size(14, 28);
                // TODO Run test results (if we want to use it to demp)
                this.scaleFactor = 1.2;
                this.minNeighbor = 5;
                this.flag = 0;

                return loadCascade(context, xml, id);

            case OPENCV_UPPERBODY:
                xml = "haarcascade_upperbody.xml";
                id = R.raw.haarcascade_upperbody;
                this.trainingSize = new Size(22, 18);
                // TODO Run test results (if we want to use it to demp)
                this.scaleFactor = 1.2;
                this.minNeighbor = 5;
                this.flag = 0;

                return loadCascade(context, xml, id);

            case CAR_3:
                xml = "cars3.xml";
                id = R.raw.cars3;
                this.trainingSize = new Size(20, 20);
                //Works well ~14 size iterations
                // min 98 ms     Max 491 ms      med: 190 ms    avg: 189 ms
                this.scaleFactor = 1.2; // this is the step size in scaling 1.2x per step
                this.minNeighbor = 5; // neighbors can't be closer than 5 px
                this.flag = 0;

                return loadCascade(context, xml, id); // need to return param because they are different for different cases.
            default:
                return null;

        }
    }

    // Haar params when running

    public Size getTrainingSize() {
        return this.trainingSize;
    }

    public int getMinNeighbor() {
        return this.minNeighbor;
    }

    public double getScaleFactor() {
        return this.scaleFactor;
    }

    public int getFlag() {
        return flag;
    }

    public enum cascades {VISIONARY_CAR_TRUCK, OPENCV_FULLBODY, OPENCV_UPPERBODY, CAR_3}
}
