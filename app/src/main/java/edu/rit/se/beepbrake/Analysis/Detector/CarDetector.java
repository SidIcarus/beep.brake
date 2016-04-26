package edu.rit.se.beepbrake.Analysis.Detector;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import edu.rit.se.beepbrake.Analysis.DetectorCallback;
import edu.rit.se.beepbrake.TempLogger;

// Created by richykapadia on 1/11/16.
public class CarDetector implements Detector {

    private static final String TAG = "Car-Detector";
    private final CascadeClassifier mCascade;

    private final Size haarSize;
    private final double scaleFactor;
    private final int minNeighbor, flag;

    // Any opencv obj being used:
    private final Size blurSize, imgSize, maxDetectSize;
    private final Point midPoint;
    private final Rect detectedCar;
    private final Mat analyze;
    private final MatOfRect foundLocations;

    private DetectorCallback activity;

    public CarDetector(CascadeClassifier cascade, DetectorCallback activity) {
        this.mCascade = cascade;
        this.activity = activity;
        this.haarSize = HaarLoader.getInstance().getTrainingSize();
        this.minNeighbor = HaarLoader.getInstance().getMinNeighbor();
        this.scaleFactor = HaarLoader.getInstance().getScaleFactor();
        this.flag = HaarLoader.getInstance().getFlag();

        // Initalize openCv obj used
        this.detectedCar = new Rect();
        this.analyze = new Mat();
        this.imgSize = new Size();
        this.maxDetectSize = new Size();
        this.foundLocations = new MatOfRect();
        this.midPoint = new Point();
        this.blurSize = new Size(5, 5);
    }

    public void detect(Mat m) {
        if (m == null || m.empty()) return;

        TempLogger.addMarkTime(TempLogger.HAAR_TIME);
        this.haar(m);
        TempLogger.addMarkTime(TempLogger.HAAR_TIME);
        TempLogger.incrementCount(TempLogger.ANALYZED_FRAMES);
    }

    /**
     * detect img
     * send points to draw to the UI Logic
     *
     * @param mat - greyscale image
     */
    private void haar(Mat mat) {
        if (imgSize.area() == 0) {
            //first time haar is called, haar parameters are set
            imgSize.width = mat.size().width;
            imgSize.height = mat.size().height;
            double wfactor = imgSize.width / haarSize.width; // what is the biggest scale factor that will fit screen
            double hfactor = imgSize.height / haarSize.height;
            double factor = Math.min(wfactor, hfactor);

            this.maxDetectSize.width = haarSize.width * factor; //how many image scale factors can be used.
            this.maxDetectSize.height = haarSize.height * factor;
            Log.d(TAG, "MAX Detect size: " + maxDetectSize.toString());

            this.midPoint.x = imgSize.width / 2;
            this.midPoint.y = imgSize.height / 2;
        }
        // Important copy to prevent memory leaks!!
        // buffer holds on to mat object preventing the native copy from being collected
        mat.copyTo(analyze);

        // param def           Img,  Locations, scaleFactor, MinNeighbor, flag, minSize, maxSize
        Imgproc.blur(analyze, analyze, blurSize);
        mCascade.detectMultiScale(analyze, foundLocations, scaleFactor, minNeighbor, flag, haarSize, maxDetectSize);
        //flag tells what version of haar (?)

        Rect r = this.filterLocationsFound(foundLocations);
        activity.setCurrentFoundRect(mat, r);
    }

    private Rect filterLocationsFound(MatOfRect loc) {
        // Pick Largest
        double largest = 0;
        Rect currRect = null;
        for (Rect r : loc.toList()) {
            //calc dist to mid pt
            if (largest < r.area()) {
                largest = r.area();
                currRect = r;
            }
        }

        if (currRect != null) Log.d(TAG, currRect.size().toString());

        return currRect;
    }
}
