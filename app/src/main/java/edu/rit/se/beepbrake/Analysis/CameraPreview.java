package edu.rit.se.beepbrake.Analysis;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.TempLogger;

/**
 * Created by richykapadia on 9/21/15
 * <p/>
 * Temporary UI logic used to draw rects over imgs
 */

// TODO: create a function to change the color of the box here
// TODO: look for a more optimal way of having a box drawn to the screen
public class CameraPreview implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final static String TAG = "Camera-Preview";
    private final Rect foundCar = new Rect(0, 0, 0, 0);
    private final Point linePoint1 = new Point();
    private final Point linePoint2 = new Point();
    private final Scalar rectColor = new Scalar(0, 0, 255);
    private final Scalar lineColor = new Scalar(0, 255, 0);
    private final Mat display = new Mat();
    // context used to receive/send frame data
    private final DetectorCallback detectorCallback;

    // Drawing logic variables
    private ReentrantLock drawLock = new ReentrantLock();
    private double[][] foundLines = new double[0][0];

    public CameraPreview(DetectorCallback detectorCallback) { this.detectorCallback = detectorCallback; }

    @Override
    public void onCameraViewStarted(int width, int height) { }

    @Override
    public void onCameraViewStopped() { }

    //Main loop that runs the application
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        TempLogger.incrementCount(TempLogger.TOTAL_FRAMES);

        this.detectorCallback.setCurrentFrame(inputFrame.gray());
        //To show tracking on image
        inputFrame.rgba().copyTo(display);
        drawBox(display);
        drawLines(display);
        return display;
    }

    public void setRectToDraw(Rect r) {
        drawLock.lock();
        if (r != null) {
            this.foundCar.x = r.x;
            this.foundCar.y = r.y;
            this.foundCar.height = r.height;
            this.foundCar.width = r.width;
        } else {
            this.foundCar.x = 0;
            this.foundCar.y = 0;
            this.foundCar.height = 0;
            this.foundCar.width = 0;
        }
        drawLock.unlock();
    }

    public void setLinesToDraw(double[][] lines) {
        drawLock.lock();
        foundLines = lines;
        drawLock.unlock();
    }

    private void drawBox(Mat rgb) {
        drawLock.lock();
        // Draw rectangle around found object
        if (this.foundCar.area() != 0) Imgproc.rectangle(rgb, foundCar.br(), foundCar.tl(), rectColor, 2);

        drawLock.unlock();
    }

    private void drawLines(Mat rgb) {
        drawLock.lock();
        if (foundLines.length > 0) {
            for (double[] data : foundLines) {
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
    }
}

