package edu.rit.se.beepbrake.Analysis;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import edu.rit.se.beepbrake.TempLogger;

/**
 * Created by richykapadia on 9/21/15
 *
 * Temporary UI logic used to draw rects over imgs
 *
 */
public class CameraPreview implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final static String TAG = "Camera-Preview";

    public boolean bAnalyzeFrame;
    private FrameAnalyzer mFrameAnalyzer;
    //locations to draw boxes
    static MatOfRect sFoundLocations = new MatOfRect();

    // points reused to draw
    final Point rectPoint1 = new Point();
    final Point rectPoint2 = new Point();

    //color of rects
    final Scalar rectColor = new Scalar(0, 0, 255);

    public CameraPreview( FrameAnalyzer analyzer){
        bAnalyzeFrame = false;
        mFrameAnalyzer = analyzer;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        TempLogger.incrementCount(TempLogger.TOTAL_FRAMES);
        //multiple starts will be logged
        mFrameAnalyzer.addFrameToAnalyze(inputFrame.rgba());
        //To show tracking of an object
        return drawBox(inputFrame.rgba());
    }

    public static void setPointsToDraw(MatOfRect loc ){
        sFoundLocations = loc;
    }

    private Mat drawBox( Mat rgb ){
        if (sFoundLocations.rows() > 0) {
            List<Rect> rectList = sFoundLocations.toList();
            for (Rect rect : rectList) {
                rectPoint1.x = rect.x;
                rectPoint1.y = rect.y;
                rectPoint2.x = rect.x + rect.width;
                rectPoint2.y = rect.y + rect.height;
                // Draw rectangle around found object
                Imgproc.rectangle(rgb, rectPoint1, rectPoint2, rectColor, 2);
            }
        }
        return rgb;
    }

}

