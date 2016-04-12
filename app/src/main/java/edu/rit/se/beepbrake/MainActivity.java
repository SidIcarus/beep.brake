package edu.rit.se.beepbrake;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import edu.rit.se.beepbrake.Analysis.CameraPreview;
import edu.rit.se.beepbrake.Analysis.Detector.CarDetector;
import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.Analysis.Detector.SimpleLaneDetector;
import edu.rit.se.beepbrake.Analysis.FrameAnalyzer;
import edu.rit.se.beepbrake.Analysis.DetectorCallback;
import edu.rit.se.beepbrake.Analysis.LoaderCallback;
import edu.rit.se.beepbrake.Web.WebManager;
import edu.rit.se.beepbrake.buffer.BufferManager;
import edu.rit.se.beepbrake.Segment.*;
import edu.rit.se.beepbrake.DecisionMaking.DecisionManager;

public class MainActivity extends AppCompatActivity implements DetectorCallback {

    static{ System.loadLibrary("opencv_java3"); }

    private static final String TAG = "Main-Activity";

    // Image Analysis Stuff
    private BaseLoaderCallback mLoaderCallback;
    private JavaCameraView mCameraView;
    private CameraPreview mCameraPreview;
    private FrameAnalyzer mCarAnalyzer;
    private FrameAnalyzer mLaneAnalyzer;

    // Haar cascade
    private String CASCADE_XML = "visionarynet_cars_and_truck_cascade_web_haar.xml";
    private int CASCADE_ID = R.raw.visionarynet_cars_and_truck_cascade_web_haar;

    //Buffer
    private BufferManager bufMan;

    //Data Acquisition Objects
    private SegmentSync segSync;
    private GPSSensor gpsSen;
    private AccelerometerSensor aSen;

    //Decision Objects
    private DecisionManager decMan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        Initialize();

        // UI Element
        mCameraView = (JavaCameraView) findViewById(R.id.CameraPreview);
        mCameraView.setMaxFrameSize(352, 288);
        mCameraView.setVisibility(SurfaceView.VISIBLE);

        //Set listener and callback
        mCameraPreview = new CameraPreview(this);
        mCameraView.setCvCameraViewListener(mCameraPreview);
        mLoaderCallback = new LoaderCallback(this, mCameraView);

        //load cascade
        CascadeClassifier cascade = loadCascade();

        //construct frame analyzer and start thread
        Detector carDetect = new CarDetector(cascade, this);
        mCarAnalyzer = new FrameAnalyzer(carDetect);

        //construct lane detector
        Detector laneDetector = new SimpleLaneDetector(this);
        mLaneAnalyzer = new FrameAnalyzer(laneDetector);

        Button warning = (Button) findViewById(R.id.triggerWarning);
        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decMan.warn();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Initialize() {
        //Buffer init
        bufMan = new BufferManager(this);

        //Data Acquisition init
        segSync = new SegmentSync(bufMan);
        gpsSen = new GPSSensor(this, segSync);
        aSen = new AccelerometerSensor(this, (SensorManager) getSystemService(SENSOR_SERVICE), segSync);

        //Decision init
        decMan = new DecisionManager(bufMan);
    }

    protected void onResume(){
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        mCarAnalyzer.resumeDetection();
        mLaneAnalyzer.resumeDetection();

        //Data Acquisition onResume
        segSync.onResume();
        gpsSen.onResume();
        aSen.onResume();

        //Decision
        decMan.onResume();

    }

    protected void onPause(){
        super.onPause();
        mCarAnalyzer.pauseDetection();
        mLaneAnalyzer.pauseDetection();

        //Data Acquisition onPause
        segSync.onPause();
        gpsSen.onPause();
        aSen.onPause();

        //Buffer onPause
        bufMan.onPause();

        //Decision
        decMan.onPause();

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
        this.mCameraPreview.setPointsToDraw(r);
        HashMap<String, Object> data = new HashMap<String, Object>();
        if( r != null) {
            data.put("br-x", r.br().x);
            data.put("br-y", r.br().y);
            data.put("tl-x", r.tl().x);
            data.put("tl-y", r.tl().y);
        }
        if(this.segSync.isRunning()) {
            this.segSync.makeSegment(m, new HashMap<String, Object>());
        }
    }

    /**
     * Lane detector calls this method to set the lane postions
     * @param lanesCoord
     */
    public void setCurrentFoundLanes(double[][] lanesCoord){
        this.mCameraPreview.setLinesToDraw(lanesCoord);
    }

    /**
     * On create loads the cascade resource to feed into the car detector
     * Must be in the activity to access raw resources
     * @return
     */
    private CascadeClassifier loadCascade(){
        CascadeClassifier cascadeClassifier = null;
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(CASCADE_ID);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, CASCADE_XML);
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
}
