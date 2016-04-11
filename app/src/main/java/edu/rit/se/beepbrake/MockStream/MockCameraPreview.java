package edu.rit.se.beepbrake.MockStream;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

import edu.rit.se.beepbrake.Segment.Constants;
import edu.rit.se.beepbrake.Segment.Segment;

/**
 * Created by richykapadia on 2/8/16.
 *
 * This class is for dev purposes ONLY
 * A predefined segment steam will feed from the MockCameraPreview from disk
 *
 *
 */
public class MockCameraPreview implements  CameraBridgeViewBase.CvCameraViewListener2 {

    private Segment currSeg;
    private int frameCount;

    private static final Scalar BLUE = new Scalar(0,0,255);
    private static final int FRAMES_PER_SECOND = 24;

    public MockCameraPreview( Segment firstSeg ){
        this.currSeg = firstSeg;
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

        if( frameCount++ % FRAMES_PER_SECOND == 0){
            this.currSeg = currSeg.getNextSeg();
        }


        //display Segment data
        Mat frame = (Mat) this.currSeg.getDataObject(Constants.FRAME);
        //draw found locations
        Rect[] pos = (Rect[]) this.currSeg.getDataObject(Constants.CAR_POSITIONS);
        for( Rect r : pos ){
            Imgproc.rectangle(frame, r.br(), r.tl(), BLUE);
        }

        // draw other segment data on the frame
        Double x = (Double) this.currSeg.getDataObject(Constants.ACCEL_X);
        Double y = (Double) this.currSeg.getDataObject(Constants.ACCEL_Y);
        Double z = (Double) this.currSeg.getDataObject(Constants.ACCEL_Z);
        Double lat = (Double) this.currSeg.getDataObject(Constants.GPS_LAT);
        Double lng = (Double) this.currSeg.getDataObject(Constants.GPS_LNG);
        Double spd = (Double) this.currSeg.getDataObject(Constants.GPS_SPD);
        // round values to prevent running off the screen
        x = round(x);
        y = round(y);
        z = round(z);
        lat = round(lat);
        lng = round(lng);
        spd = round(spd);

        String accel_data = "Accel: (" + x + "," + y + "," + z + ")";
        String gps_data = "Gps: (" + lat + "," + lng + ")";
        String speed_data = "Speed: " + spd;
        Imgproc.putText(frame, accel_data, new Point(20,60), 1, 1.2, new Scalar(20,200,20));
        Imgproc.putText(frame, gps_data, new Point(20,80), 1, 1.2, new Scalar(20,200,20));
        Imgproc.putText(frame, speed_data, new Point(20,100), 1, 1.2, new Scalar(20,200,20));

        Mat display = new Mat();
        Imgproc.cvtColor(frame, display, Imgproc.COLOR_BGR2RGBA);

        // TODO send segment to mock decision maker

        return display;
    }

    private double round(double value){
        BigDecimal rounded = new BigDecimal(value);
        rounded = rounded.setScale(2, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }
}
