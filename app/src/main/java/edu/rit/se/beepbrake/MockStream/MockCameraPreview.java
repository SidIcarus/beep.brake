package edu.rit.se.beepbrake.MockStream;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;

/**
 * Created by richykapadia on 2/8/16.
 *
 * This class is for dev purposes ONLY
 * A predefined segment steam will feed from the MockCameraPreview from disk
 *
 *
 */
public class MockCameraPreview implements  CameraBridgeViewBase.CvCameraViewListener2 {

    private ArrayList<Mat> stream = new ArrayList<>();
    private int frameCount;

    private static final int FRAMES_PER_SECOND = 24;

    public MockCameraPreview( ArrayList<Mat> matStream){
        this.stream = matStream;
        this.frameCount = 0;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        int index = (frameCount / FRAMES_PER_SECOND) % stream.size();
        if( index == 0 ){
            // TODO reset decision maker algo history and rate variables
        }
        Mat m = this.stream.get(index);
        Mat display = new Mat();
        frameCount++;
        Imgproc.cvtColor(m, display, Imgproc.COLOR_BGR2RGBA);

        // TODO send segment 

        return display;
    }
}
