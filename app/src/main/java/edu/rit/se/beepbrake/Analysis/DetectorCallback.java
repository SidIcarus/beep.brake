package edu.rit.se.beepbrake.analysis;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

// Created by richykapadia on 4/11/16.
public interface DetectorCallback {

    void setCurrentFoundLanes(double[][] lanesCoord);

    void setCurrentFoundRect(Mat m, Rect r);

    void setCurrentFrame(Mat currentFrame);
}
