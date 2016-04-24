package edu.rit.se.beepbrake;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

import edu.rit.se.beepbrake.Analysis.CameraPreview;
import edu.rit.se.beepbrake.Analysis.Detector.CarDetector;
import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.Analysis.Detector.HaarLoader;
import edu.rit.se.beepbrake.Analysis.Detector.LaneDetector;
import edu.rit.se.beepbrake.Analysis.DetectorCallback;
import edu.rit.se.beepbrake.Analysis.FrameAnalyzer;
import edu.rit.se.beepbrake.Analysis.LoaderCallback;
import edu.rit.se.beepbrake.DecisionMaking.DecisionManager;
import edu.rit.se.beepbrake.Segment.AccelerometerSensor;
import edu.rit.se.beepbrake.Segment.Constants;
import edu.rit.se.beepbrake.Segment.GPSSensor;
import edu.rit.se.beepbrake.Segment.SegmentSync;
import edu.rit.se.beepbrake.buffer.BufferManager;

/*
TODO: SharedPreferences - save Array<Str> UploadUID + UploadStatus (the POST return # 100/200/400) This will be stored in mem until it has a chance to be written to SP.
the only time it will be saving to the SP will be when the activity has to close and there has been a failure and or something that has not been uploaded yet so as to
try it again when the app starts again

TODO: SP - log when we upload the UID for the device that we are creating uploaded it
 */
public class MainActivity extends AppCompatActivity implements DetectorCallback {

    private static final String TAG = "Main-Activity";

    static { System.loadLibrary("opencv_java3"); }

    // Image Analysis Stuff
    private BaseLoaderCallback mLoaderCallback;
    private JavaCameraView mCameraView;
    private CameraPreview mCameraPreview;
    private FrameAnalyzer mCarAnalyzer;
    private FrameAnalyzer mLaneAnalyzer;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Initialize();

        // TODO: Move elsewhere
        // UI Element
        mCameraView = (JavaCameraView) findViewById(R.id.CameraPreview);
        mCameraView.setMaxFrameSize(352, 288);
        mCameraView.setVisibility(SurfaceView.VISIBLE);

        //Set listener and callback
        mCameraPreview = new CameraPreview(this);
        mCameraView.setCvCameraViewListener(mCameraPreview);
        mLoaderCallback = new LoaderCallback(this, mCameraView);

        //load cascade
        HaarLoader loader = HaarLoader.getInstance(); // get xml resource file and put in HAAR object
        CascadeClassifier cascade = loader.loadHaar(this, HaarLoader.cascades.CAR_3);

        //construct frame analyzer and start thread
        Detector carDetect = new CarDetector(cascade, this);
        mCarAnalyzer = new FrameAnalyzer(carDetect);

        //construct lane detector
        Detector laneDetector = new LaneDetector();
        mLaneAnalyzer = new FrameAnalyzer(laneDetector);


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
        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);
    }

    public void Initialize() {
        bufMan = new BufferManager(this);

        //Data Acquisition init
        segSync = new SegmentSync(bufMan);
        gpsSen = new GPSSensor(this, segSync);
        aSen = new AccelerometerSensor(this, (SensorManager) getSystemService(SENSOR_SERVICE), segSync);

        decMan = new DecisionManager(bufMan);
    }

    protected void onResume() {
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

    protected void onPause() {
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

    // Feeds the frame into the analyzers
    public void setCurrentFrame(Mat currentFrame) {
        this.mCarAnalyzer.addFrameToAnalyze(currentFrame);
        this.mLaneAnalyzer.addFrameToAnalyze(currentFrame);
    }

    // Car detector calls this method to set the position of the car
    public void setCurrentFoundRect(Mat m, Rect r) {
        this.mCameraPreview.setRectToDraw(r);
        HashMap<String, Object> data = new HashMap<String, Object>();

        if (r != null) {
            data.put(Constants.CAR_POS_X, r.x);
            data.put(Constants.CAR_POS_Y, r.y);
            data.put(Constants.CAR_POS_WIDTH, r.width);
            data.put(Constants.CAR_POS_HEIGHT, r.height);
        }
        if (this.segSync.isRunning()) this.segSync.makeSegment(m, data);
    }

    // Lane detector calls this method to set the lane positions
    public void setCurrentFoundLanes(double[][] lanesCoord) { this.mCameraPreview.setLinesToDraw(lanesCoord); }

}
