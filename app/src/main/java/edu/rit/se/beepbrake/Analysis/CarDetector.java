package edu.rit.se.beepbrake.Analysis;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import edu.rit.se.beepbrake.TempLogger;

/**
 * Created by richykapadia on 1/11/16.
 */
public class CarDetector implements Detector {

    private static final String TAG = "Car-Detector";
    private CascadeClassifier mCascade;

    public CarDetector(CascadeClassifier cascade){
        this.mCascade = cascade;
    }

    public void detect(Mat m){
        if( m == null || m.empty()){
            return;
        }

        TempLogger.addMarkTime(TempLogger.HAAR_TIME);
        this.haar(m);
        TempLogger.addMarkTime(TempLogger.HAAR_TIME);
        TempLogger.incrementCount(TempLogger.ANALYZED_FRAMES);
    }

    /**
     * detect img
     * send points to draw to the UI Logic
     * @param mat
     */
    public void haar( Mat mat){
        MatOfRect foundLocations = new MatOfRect();
        //TODO break out into constants and determine correct parameters
        // param def           Img,  Locations, scaleFactor, MinNeighbor, flag, minSize, maxSize
        mCascade.detectMultiScale(mat, foundLocations, 1.4, 50, 0, new Size(24,24), new Size(258,258));
        //TODO tell actual UI where to draw squares
        CameraPreview.setPointsToDraw(foundLocations);
    }
}
