package edu.rit.se.beepbrake.Analysis;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.Analysis.Detector.Detector;
import edu.rit.se.beepbrake.TempLogger;


/**
 * Created by richykapadia on 9/24/15.
 * <p/>
 * All image analyze is performed in this thread
 * New images are feed into this method via the priority queue
 */
public class FrameAnalyzer implements Runnable {

    private static final String TAG = "Frame Analyzer Thread";
    static int numAnalyzer = 0;
    // haar classifier
    private static CascadeClassifier mCascadeClassifier;
    private volatile boolean bRunning;
    // lock to protect the current frame
    private ReentrantLock mFrameLock;
    //current frame
    private volatile Mat mCurrentFrame;
    //detector
    private Detector mDetector;
    //for logging
    private int analyzerId = 0;

    /**
     * Constructor
     *
     * @param detector - descriptor how the frame is detected
     */
    public FrameAnalyzer(Detector detector) {
        mDetector = detector;
        mFrameLock = new ReentrantLock();
        bRunning = true;
        analyzerId = numAnalyzer;
        numAnalyzer++;
    }


    /**
     * Analyze the most recent frame
     */
    @Override
    public void run() {
        Log.d(TAG, "Started Running!");
        while (bRunning) {
            if (mCurrentFrame != null) {
                mFrameLock.lock();
                TempLogger.addMarkTime(TempLogger.SLACK_TIME + this.toString());
                mDetector.detect(mCurrentFrame);
                mCurrentFrame = null;
                mFrameLock.unlock();
            }
        }
        Log.d(TAG, "Finished Running!");
    }


    /**
     * Analyze the most recent mat
     *
     * @param mat - current img
     */
    public void addFrameToAnalyze(Mat mat) {
        //if not being analyzed
        if (!mFrameLock.isLocked()) {
            //then set
            TempLogger.addMarkTime(TempLogger.SLACK_TIME + this.toString());
            this.mCurrentFrame = mat;
        }
    }

    public void pauseDetection() {
        bRunning = false;
    }

    public void resumeDetection() {
        bRunning = true;
        (new Thread(this)).start();
    }


    public String toString() {
        switch (this.analyzerId) {
            case 0:
                return "CarDetector";
            case 1:
                return "LaneDetector";
            default:
                return "Unknown";
        }

    }
}