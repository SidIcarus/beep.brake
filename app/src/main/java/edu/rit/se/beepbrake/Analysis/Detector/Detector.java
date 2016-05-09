package edu.rit.se.beepbrake.analysis.Detector;

import org.opencv.core.Mat;

/**
 * @author richykapadia
 * @date 1.11.16
 */
public interface Detector {
    /** @param m Image mat */
    void detect(Mat m);
}
