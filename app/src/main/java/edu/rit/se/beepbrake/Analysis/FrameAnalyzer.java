package edu.rit.se.beepbrake.Analysis;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.TempLogger;


/**
 * Created by richykapadia on 9/24/15.
 *
 * All image analyze is performed in this thread
 * New images are feed into this method via the priority queue
 *
 *
 */
public class FrameAnalyzer implements Runnable {

    private static final String TAG = "Frame Analyzer Thread";
    private boolean bDetecting;
    private boolean bRunning;
    // haar classifier
    private static CascadeClassifier mCascadeClassifier;
    // lock to protect the current frame
    private ReentrantLock mFrameLock;
    //current frame
    private Mat mCurrentFrame;
    //detector
    private Detector mDetector;


    /**
     * Constructor
     * @param detector - decriptor how the frame is detected
     */
    public FrameAnalyzer(Detector detector){
        mDetector = detector;
        mFrameLock = new ReentrantLock();
        bDetecting = true;
        bRunning = true;
    }


    /**
     * Analyze the most recent frame
     */
    @Override
    public void run() {
        Log.d(TAG, "Started Running!");
        while (bRunning ) {
            if( bDetecting ) {
                mFrameLock.lock();
                TempLogger.addMarkTime(TempLogger.SLACK_TIME);
                mDetector.detect(mCurrentFrame);
                mFrameLock.unlock();
            }
        }
        Log.d(TAG, "Finished Running!");
    }


    /**
     * Analyze the most recent mat
     * @param mat - current img
     */
    public void addFrameToAnalyze(Mat mat){
        //if not being analyzed
        if(mFrameLock.isLocked()){
            //then set
            TempLogger.addMarkTime(TempLogger.SLACK_TIME);
            this.mCurrentFrame = mat;
        }
    }

    public void pauseDetection(){
        bDetecting = false;
    }

    public void resumeDetection(){
        bDetecting = true;
    }

    public void destroy(){
        bRunning = false;
    }
}