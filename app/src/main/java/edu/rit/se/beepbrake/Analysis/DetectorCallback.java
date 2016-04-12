package edu.rit.se.beepbrake.Analysis;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.HashMap;

/**
 * Created by richykapadia on 4/11/16.
 */
public interface DetectorCallback {

    public void setCurrentFrame(Mat currentFrame);
    public void setCurrentFoundRect(Mat m, Rect r);
    public void setCurrentFoundLanes(double[][] lanesCoord);
}
