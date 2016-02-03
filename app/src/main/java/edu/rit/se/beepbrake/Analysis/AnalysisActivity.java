package edu.rit.se.beepbrake.Analysis;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.rit.se.beepbrake.AccelerometerSensor;
import edu.rit.se.beepbrake.Analysis.Detector.CarDetector;
import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.Analysis.Detector.SimpleLaneDetector;
import edu.rit.se.beepbrake.GPSSensor;
import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.SegmentSync;
import edu.rit.se.beepbrake.TempLogger;

/**
 * Created by richykapadia on 1/7/16.
 *
 *
 * 1) Connect to camera
 * 2) load cascade
 * 3) start analyzer thread
 * 4) setup callback
 *
 */

public class AnalysisActivity extends AppCompatActivity {

    static{ System.loadLibrary("opencv_java3"); }

    private static final String TAG = "Analysis-Activity";

    private BaseLoaderCallback mLoaderCallback;
    private JavaCameraView mCameraView;
    private FrameAnalyzer mCarAnalyzer;
    private FrameAnalyzer mLaneAnalyzer;

    //Cascade to be loaded
    private String CASCADE_XML = "cascade_5.xml";
    private int CASCADE_ID = R.raw.cascade_5;

    //Data Acquisition Objects
    private SegmentSync segSync;
    private GPSSensor gpsSen;
    private AccelerometerSensor aSen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        //Data Acquisition init
        segSync = new SegmentSync();
        gpsSen = new GPSSensor(this, segSync);
        aSen = new AccelerometerSensor((SensorManager) getSystemService(SENSOR_SERVICE), segSync);

        // UI Element
        mCameraView = (JavaCameraView) findViewById(R.id.CameraPreview);
        mCameraView.setVisibility(SurfaceView.VISIBLE);

        //load cascade
        CascadeClassifier cascade = loadCascade();

        //construct frame analyzer and start thread
        Detector carDetect = new CarDetector(cascade);
        mCarAnalyzer = new FrameAnalyzer(carDetect);
        (new Thread(mCarAnalyzer)).start();

        //construct lane detector
        Detector laneDetector = new SimpleLaneDetector();
        mLaneAnalyzer = new FrameAnalyzer(laneDetector);
        (new Thread(mLaneAnalyzer)).start();

        //Set listener and callback
        CameraPreview cameraPreview = new CameraPreview();
        cameraPreview.addAnalyzer(mCarAnalyzer);
        cameraPreview.addAnalyzer(mLaneAnalyzer);
        mCameraView.setCvCameraViewListener(cameraPreview);
        mLoaderCallback = new LoaderCallback(this, mCameraView);

        //setup button
        Button statsButton = (Button) findViewById(R.id.statsButton);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prevent double taps
                if(!TempLogger.isPrintingLogs()) {
                    TempLogger.printLogs();
                }
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

    @Override
    protected void onPause() {
        super.onPause();
        if(mCarAnalyzer != null) {
            mCarAnalyzer.pauseDetection();
        }

        if(mLaneAnalyzer != null){
            mLaneAnalyzer.pauseDetection();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        if(mCarAnalyzer != null) {
            mCarAnalyzer.resumeDetection();
        }

        if(mLaneAnalyzer != null){
            mLaneAnalyzer.resumeDetection();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCarAnalyzer != null){
            mCarAnalyzer.destroy();
        }
        if(mLaneAnalyzer != null){
            mCarAnalyzer.destroy();
        }
        if(mCameraView != null){
            mCameraView.disableView();
        }
    }

    public static void sendSegment(Mat m){

    }

    public CascadeClassifier loadCascade(){
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

