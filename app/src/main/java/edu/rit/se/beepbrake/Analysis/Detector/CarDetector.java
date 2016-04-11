package edu.rit.se.beepbrake.Analysis.Detector;

import android.graphics.Camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;

import edu.rit.se.beepbrake.Analysis.AnalysisActivity;
import edu.rit.se.beepbrake.Analysis.CameraPreview;
import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.TempLogger;
import edu.rit.se.beepbrake.TempMainActivity;

/**
 * Created by richykapadia on 1/11/16.
 */
public class CarDetector implements Detector {

    private static final String TAG = "Car-Detector";
    private final CascadeClassifier mCascade;
    private Size imgSize;
    private TempMainActivity activity;

    public CarDetector(CascadeClassifier cascade, TempMainActivity activity){
        this.mCascade = cascade;
        this.activity = activity;
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
     * @param mat - greyscale image
     */
    public void haar( Mat mat){
        if(imgSize == null){
            imgSize = mat.size();
        }
        MatOfRect foundLocations = new MatOfRect();
        //TODO break out into constants
        // param def           Img,  Locations, scaleFactor, MinNeighbor, flag, minSize, maxSize
        mCascade.detectMultiScale(mat, foundLocations, 1.4, 50, 0, new Size(24, 24), new Size(258, 258));
        Rect r = this.filterLocationsFound(foundLocations);
        activity.setCurrentFoundRect(mat, r);

    }

    private Rect filterLocationsFound(MatOfRect loc){
        //TODO replace with actual driving pt (between lanes)
        Point pt = new Point(imgSize.width / 2 , imgSize.height / 2);

        //find the closest largest rect to the 'drive pt'
        double minDist = Double.MAX_VALUE;
        List<Rect> rectList = loc.toList();
        Rect currRect = null;
        for(Rect r : rectList){
           //calc dist to mid pt
            double delta_x = Math.pow(Math.abs(pt.x - r.x), 2);
            double delta_y = Math.pow(Math.abs(pt.y - r.y), 2);
            double dist = Math.sqrt(delta_x + delta_y);
            if( dist < minDist){
                minDist = dist;
                currRect = r;
            }
        }

        return currRect;

    }

}
