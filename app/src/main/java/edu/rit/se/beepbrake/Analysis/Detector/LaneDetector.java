package edu.rit.se.beepbrake.Analysis.Detector;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import edu.rit.se.beepbrake.Analysis.DetectorCallback;

/**
 * Created by richykapadia on 4/13/16.
 *
 *
 * Canny and Hough on the whole image, take the largest left lines and right lines
 */
public class LaneDetector implements Detector {


    @Override
    public void detect(Mat m) {

    }
}
