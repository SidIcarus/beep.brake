package edu.rit.se.beepbrake.Analysis;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.HashMap;

import edu.rit.se.beepbrake.Analysis.Detector.CarDetector;
import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.Analysis.Detector.HaarLoader;
import edu.rit.se.beepbrake.Analysis.Detector.LaneDetector;
import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.Segment.Constants;
import edu.rit.se.beepbrake.Segment.SegmentSync;

/**
 * Created by richykapadia on 4/26/16.
 */
public class AnalysisManager implements DetectorCallback  {

    private static final String TAG = "Main-Activity";

    // Image Analysis Stuff
    private BaseLoaderCallback mLoaderCallback;
    private JavaCameraView mCameraView;
    private CameraPreview mCameraPreview;
    private FrameAnalyzer mCarAnalyzer;
    private FrameAnalyzer mLaneAnalyzer;

    private SegmentSync segSync;

    private Activity activity;

    public AnalysisManager(Activity activity, SegmentSync segSync){
        this.activity = activity;
        this.segSync = segSync;
    }



    public void initialize(){
        // UI Element
        mCameraView = (JavaCameraView) this.activity.findViewById(R.id.CameraPreview); //find by ID then CAST into the actual object
        mCameraView.setMaxFrameSize(352, 288); // magic
        mCameraView.setVisibility(SurfaceView.VISIBLE); // ?

        //Set listener and callback
        mCameraPreview = new CameraPreview(this); //this drives the app to connect to camera
        mCameraView.setCvCameraViewListener(mCameraPreview); //any change to camera view object triggers call
        mLoaderCallback = new LoaderCallback( this.activity, mCameraView); // load OpenCv lib (checks device for OpenCv

        //load cascade
        HaarLoader loader = HaarLoader.getInstance(); // get xml resource file and put in HAAR object
        CascadeClassifier cascade = loader.loadHaar(this.activity, HaarLoader.cascades.CAR_3);

        //construct frame analyzer and start thread
        Detector carDetect = new CarDetector(cascade, this);
        mCarAnalyzer = new FrameAnalyzer(carDetect);

        //construct lane detector
        Detector laneDetector = new LaneDetector();
        mLaneAnalyzer = new FrameAnalyzer(laneDetector);
    }

    /**
     * Feeds the frame into the analyzers
     * @param currentFrame
     */
    public void setCurrentFrame(Mat currentFrame){
        this.mCarAnalyzer.addFrameToAnalyze(currentFrame);
        this.mLaneAnalyzer.addFrameToAnalyze(currentFrame);
    }

    /**
     * Car detector calls this method to set the position of the car
     * @param m -
     * @param r
     */
    public void setCurrentFoundRect(Mat m, Rect r){
        this.mCameraPreview.setRectToDraw(r);
        HashMap<String, Object> data = new HashMap<String, Object>();
        if( r != null) {
            data.put(Constants.CAR_POS_X, r.x);
            data.put(Constants.CAR_POS_Y, r.y);
            data.put(Constants.CAR_POS_WIDTH, r.width);
            data.put(Constants.CAR_POS_HEIGHT, r.height);
        }
        if(this.segSync.isRunning()) {
            this.segSync.makeSegment(m, data);
        }
    }

    /**
     * Lane detector calls this method to set the lane positions
     * @param lanesCoord
     */
    public void setCurrentFoundLanes(double[][] lanesCoord){
        this.mCameraPreview.setLinesToDraw(lanesCoord);
    }

    public void onResume(){
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.activity, mLoaderCallback);
        mCarAnalyzer.resumeDetection();
        mLaneAnalyzer.resumeDetection();
    }

    public void onPause(){
        mCarAnalyzer.pauseDetection();
        mLaneAnalyzer.pauseDetection();
    }


}
