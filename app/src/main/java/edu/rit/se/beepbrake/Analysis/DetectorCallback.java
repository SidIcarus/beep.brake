package edu.rit.se.beepbrake.Analysis;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

// Created by richykapadia on 4/11/16.
public interface DetectorCallback {

    void setCurrentFrame(Mat currentFrame);

    void setCurrentFoundRect(Mat m, Rect r);

    void setCurrentFoundLanes(double[][] lanesCoord);
}
