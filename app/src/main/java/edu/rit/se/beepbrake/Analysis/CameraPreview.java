package edu.rit.se.beepbrake.Analysis;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.TempLogger;

/**
 * Created by richykapadia on 9/21/15
 *
 * Temporary UI logic used to draw rects over imgs
 *
 */
public class CameraPreview implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final static String TAG = "Camera-Preview";

    /**
     * Drawing logic variables
     */

    private ReentrantLock drawLock = new ReentrantLock();
    private List<Rect> sFoundCars = new ArrayList<Rect>();
    private double[][] sFoundLines = new double[0][0];

    final Point rectPoint1 = new Point();
    final Point rectPoint2 = new Point();

    final Point linePoint1 = new Point();
    final Point linePoint2 = new Point();

    final Scalar rectColor = new Scalar(0, 0, 255);
    final Scalar lineColor = new Scalar(0, 255, 0);

    // context used to receive/send frame data
    private final DetectorCallback detectorCallback;

    public CameraPreview(DetectorCallback detectorCallback){
        this.detectorCallback = detectorCallback;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    /**
     * Main loop that runs the application
     */
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        TempLogger.incrementCount(TempLogger.TOTAL_FRAMES);

        this.detectorCallback.setCurrentFrame(inputFrame.gray());
        //To show tracking on image
        Mat display = new Mat();
        inputFrame.rgba().copyTo(display);
        display = drawBox(display);
        display = drawLines(display);
        return display;
    }

    public void setPointsToDraw(Rect r){
        drawLock.lock();
        sFoundCars.clear();
        sFoundCars.add(r);
        drawLock.unlock();
    }

    public void setLinesToDraw(double[][] lines){
        drawLock.lock();
        sFoundLines = lines;
        drawLock.unlock();
    }

    private Mat drawBox( Mat rgb ){
        drawLock.lock();
        for (Rect rect : sFoundCars) {
            if(rect != null) {
                rectPoint1.x = rect.x;
                rectPoint1.y = rect.y;
                rectPoint2.x = rect.x + rect.width;
                rectPoint2.y = rect.y + rect.height;
                // Draw rectangle around found object
                Imgproc.rectangle(rgb, rectPoint1, rectPoint2, rectColor, 2);
            }
        }
        drawLock.unlock();
        return rgb;
    }

    private Mat drawLines( Mat rgb ) {
        drawLock.lock();
        if (sFoundLines.length > 0) {
            for (double[] data : sFoundLines) {
                if (data.length == 4) {
                    linePoint1.x = data[0];
                    linePoint1.y = data[1];
                    linePoint2.x = data[2];
                    linePoint2.y = data[3];
                    Imgproc.line(rgb, linePoint1, linePoint2, lineColor, 2);
                }
            }
        }
        drawLock.unlock();
        return rgb;
    }
}

