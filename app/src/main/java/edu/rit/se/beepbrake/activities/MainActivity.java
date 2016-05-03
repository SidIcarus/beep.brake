package edu.rit.se.beepbrake.activities;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.HashMap;

import edu.rit.se.beepbrake.analysis.*;
import edu.rit.se.beepbrake.analysis.Detector.*;
import edu.rit.se.beepbrake.decisionMaking.DecisionManager;
import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.segment.*;
import edu.rit.se.beepbrake.buffer.BufferManager;
import edu.rit.se.beepbrake.fragments.*;
import edu.rit.se.beepbrake.utils.Utils;

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

    public FragmentDirector fDirector;
    public static Utils utilities;
    boolean showFAB = true;

    private int mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        setContentView(R.layout.camera_preview);
        Utils.hideStatusBar(getWindow());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initBottomSheet();

        Initialize();

        setUpCameraStuff();

//        final Button warning = (Button) findViewById(R.id.triggerWarning);
//        warning.setVisibility(View.INVISIBLE);
//
//        final Button printLogs = (Button) findViewById(R.id.printLogs);
//        printLogs.setVisibility(View.INVISIBLE);

        fDirector = new FragmentDirector().newInstance(getSupportFragmentManager());
    }

    // TODO: Move elsewhere
    public void setUpCameraStuff() {

        // UI Element
        mCameraView = (JavaCameraView) findViewById(R.id.CameraPreview);
        if(mCameraView != null) {
            mCameraView.setMaxFrameSize(352, 288);
            mCameraView.setVisibility(SurfaceView.VISIBLE);

            // camera listener
            mCameraPreview = new CameraPreview(this);
            mCameraView.setCvCameraViewListener(mCameraPreview);

            // camera callback
            mLoaderCallback = new LoaderCallback(this, mCameraView);

            //load cascade
            HaarLoader loader =
                HaarLoader.getInstance(); // get xml resource file and put in HAAR object
            CascadeClassifier cascade = loader.loadHaar(this, HaarLoader.cascades.CAR_3);

            //construct frame analyzer and start thread
            Detector carDetect = new CarDetector(cascade, this);
            mCarAnalyzer = new FrameAnalyzer(carDetect);

            //construct lane detector
            Detector laneDetector = new LaneDetector();
            mLaneAnalyzer = new FrameAnalyzer(laneDetector);
        }
    }

    public void Initialize() {
        bufMan = new BufferManager(this);

        //Data Acquisition init
        segSync = new SegmentSync(bufMan);
        gpsSen = new GPSSensor(this, segSync);
        aSen = new AccelerometerSensor(this, (SensorManager) getSystemService(SENSOR_SERVICE),
            segSync);

        decMan = new DecisionManager(bufMan);
    }

    protected void onResume() {
        super.onResume();

        Utils.hideStatusBar(getWindow());
        //        utilities.resumeAnimatable();
        //        utilities.resumeNightMode(getResources());


        if(mCameraView != null) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
            mCarAnalyzer.resumeDetection();
            mLaneAnalyzer.resumeDetection();
        }

        //Data Acquisition onResume
        segSync.onResume();
        gpsSen.onResume();
        aSen.onResume();

        //Decision
        decMan.onResume();
    }

    protected void onPause() {
        super.onPause();
        if(mCameraView != null) {
            mCarAnalyzer.pauseDetection();
            mLaneAnalyzer.pauseDetection();
        }

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
        if(mCameraView != null) {
            this.mCarAnalyzer.addFrameToAnalyze(currentFrame);
            this.mLaneAnalyzer.addFrameToAnalyze(currentFrame);
        }
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
    public void setCurrentFoundLanes(double[][] lanesCoord) {
        this.mCameraPreview.setLinesToDraw(lanesCoord);
    }

    public void initBottomSheet() {
        // Bottom Sheet

        // To handle FAB animation upon entrance and exit
        final Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        final Animation shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_shrink);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.gmail_fab);

        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(growAnimation);

        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }

            @Override public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation) { }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.grand_poobah);
        View bottomSheet = coordinatorLayout.findViewById(R.id.g_bottom_sheet);

        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (showFAB) fab.startAnimation(shrinkAnimation);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        showFAB = true;
                        fab.setVisibility(View.VISIBLE);
                        fab.startAnimation(growAnimation);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        showFAB = false;
                        break;
                }
            }

            @Override public void onSlide(View bottomSheet, float slideOffset) { }
        });
    }

    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        int id = item.getItemId();
    //        if (id == R.id.action_day_night_yes) {
    //            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    //            recreate();
    //            return true;
    //        } else if (id == R.id.action_day_night_no) {
    //            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    //            recreate();
    //            return true;
    //        } else {
    //            if (id == R.id.action_bottom_sheet_dialog) {
    //                BottomSheetDialogView.show(this, mDayNightMode);
    //                return true;
    //            }
    //        }
    //        return super.onOptionsItemSelected(item);
    //    }

}
