package edu.rit.se.beepbrake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.HashMap;

import edu.rit.se.beepbrake.Analysis.AnalysisManager;
import edu.rit.se.beepbrake.Analysis.CameraPreview;
import edu.rit.se.beepbrake.Analysis.Detector.CarDetector;
import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.Analysis.Detector.HaarLoader;
import edu.rit.se.beepbrake.Analysis.Detector.LaneDetector;
import edu.rit.se.beepbrake.Analysis.FrameAnalyzer;
import edu.rit.se.beepbrake.Analysis.DetectorCallback;
import edu.rit.se.beepbrake.Analysis.LoaderCallback;
import edu.rit.se.beepbrake.buffer.BufferManager;
import edu.rit.se.beepbrake.Segment.*;
import edu.rit.se.beepbrake.DecisionMaking.DecisionManager;

public class MainActivity extends AppCompatActivity{

    static{ System.loadLibrary("opencv_java3"); }

    private static final String TAG = "Main-Activity";

    //Buffer
    private BufferManager bufMan;

    //Data Acquisition Objects
    private SegmentSync segSync;
    private GPSSensor gpsSen;
    private AccelerometerSensor aSen;

    //Decision Objects
    private DecisionManager decMan;

    //analysis
    private AnalysisManager analysisManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //keeps screen on


        Initialize(); // Ryan made for segment initialization.


        final Button warning = (Button) findViewById(R.id.triggerWarning);
        warning.setVisibility(View.INVISIBLE);

        final Button printLogs = (Button) findViewById(R.id.printLogs);
        printLogs.setVisibility(View.INVISIBLE);

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

        this.analysisManager = new AnalysisManager(this, segSync);
        this.analysisManager.initialize();
    }

    protected void onResume(){
        super.onResume();

        this.analysisManager.onResume();

        //Data Acquisition onResume
        segSync.onResume();
        gpsSen.onResume();
        aSen.onResume();

        //Decision
        decMan.onResume();

    }

    protected void onPause(){
        super.onPause();

        //Analysis onPause
        this.analysisManager.onPause();

        //Data Acquisition onPause
        segSync.onPause();
        gpsSen.onPause();
        aSen.onPause();

        //Buffer onPause
        bufMan.onPause();

        //Decision
        decMan.onPause();

    }

}
