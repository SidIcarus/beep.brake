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

import edu.rit.se.beepbrake.R;


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

    private final BlockingQueue<Mat> mFrameQueue;

    private long count;
    private long sum;

    private Context mContext;
    private static CascadeClassifier mCascadeClassifier;


    /**
     * Constructor
     * @param cascadeClassifier - cascade loaded
     * @param concurrentQueue - mechanism to pass frame
     * @param context -
     */
    public FrameAnalyzer(CascadeClassifier cascadeClassifier, BlockingQueue<Mat> concurrentQueue, Context context){
        mCascadeClassifier = cascadeClassifier;
        mFrameQueue = concurrentQueue;
        mContext = context;
    }


    /**
     * Analyze the most recent frame
     */
    @Override
    public void run() {
        Log.d(TAG, "Started Running!");
        try {
            // replace with something to pause/kill
            while (true) {
                Mat m = this.mFrameQueue.take();
                long start = System.currentTimeMillis();
                this.haar(m);
                long end = System.currentTimeMillis();
                Log.d(TAG, "Haar Time: " + (end - start) + " ms");
                count++;
                sum += (end - start);
                if( count % 25 == 0){
                    Log.d(TAG, "Avg: " + sum/count);
                }


            }
        }catch(InterruptedException e){
            Log.d(TAG, e.getMessage());
        }

    }


    /**
     * Analyze the most recent mat
     * @param mat - current img
     */
    public void addFrameToAnalyze(Mat mat){
        if(mFrameQueue.peek() == null){
            mFrameQueue.add(mat);
        }else{
            mFrameQueue.remove();
            mFrameQueue.add(mat);
        }
    }

    /**
     * detect img
     * send points to draw to the UI Logic
     * @param mat
     */
    public void haar( Mat mat){
        MatOfRect foundLocations = new MatOfRect();
        //mCascadeClassifier.detectMultiScale2(mat, 1, 1.1, 3, 0, );
        mCascadeClassifier.detectMultiScale(mat, foundLocations, 4, 100, 0, new Size(24,24), new Size(64,64));
        CameraPreview.setPointsToDraw(foundLocations);
    }


}