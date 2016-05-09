package edu.rit.se.beepbrake.analysis.Detector;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import edu.rit.se.beepbrake.analysis.DetectorCallback;

// Created by richykapadia on 1/19/16.
public class CropLaneDetector implements Detector {

    private Size totalSize;
    private Rect cropSizeLeft;
    private Rect cropSizeRight;
    private boolean sizeSet = false;
    private DetectorCallback callback;
    static final int CANNY_MIN = 25;                    // edge detector minimum hysteresis threshold
    static final int CANNY_MAX = 150;                // edge detector maximum hysteresis threshold
    static final int HOUGH_TRESHOLD = 40;            // line approval vote threshold
    static final int HOUGH_MIN_LINE_LENGTH = 10;        // remove lines shorter than this treshold
    static final int HOUGH_MAX_LINE_GAP = 25;        // join lines to one with smaller than this gaps
    static final int LINE_REJECT_DEGREES = 10;
    static final int ANGLE_THRESH = 2;                //angle between two paired edges of a lane
    static final int POSITION_THRESH = 15;            //width of the lane in pixels
    static final double THETA = (5 * Math.PI) / 180;

    public CropLaneDetector(DetectorCallback callback) { this.callback = callback; }

    @Override
    public void detect(Mat m) {
        setSize(m);
        if (!this.sizeSet || m == null || m.empty()) return;

        Mat copy = new Mat();
        m.copyTo(copy);
        //divide img in right/left half
        Mat roiLeft = new Mat(copy, this.cropSizeLeft);
        Mat roiRight = new Mat(copy, this.cropSizeRight);
        //imgproc stuff
        Mat leftLines = findLines(roiLeft);
        Mat rightLines = findLines(roiRight);
        //filter stuff
        double[][] leftCoord = filterOutLines(leftLines);
        double[][] rightCoord = filterOutLines(rightLines);

        //calc offsets
        int leftOffsetX = (int) (this.totalSize.width - (this.totalSize.width - this.cropSizeLeft.tl().x));
        int leftOffsetY = (int) (this.totalSize.height - (this.totalSize.height - this.cropSizeLeft.tl().y));
        int rightOffsetX = (int) (this.totalSize.width - (this.totalSize.width - this.cropSizeRight.tl().x));
        int rightOffsetY = (int) (this.totalSize.height - (this.totalSize.height - this.cropSizeRight.tl().x));

        //consolidate coordnates
        int numLines = leftCoord.length + rightCoord.length;
        double[][] results = new double[numLines][4];
        int i = 0;
        for (double[] data : leftCoord) {
            data[0] += leftOffsetX;
            data[1] += leftOffsetY;
            data[2] += leftOffsetX;
            data[3] += leftOffsetX;
            results[i] = data;
            i++;
        }
        for (double[] data : rightCoord) {
            data[0] += rightOffsetX;
            data[1] += rightOffsetY;
            data[2] += rightOffsetX;
            data[3] += rightOffsetX;
            results[i] = data;
            i++;
        }
        this.callback.setCurrentFoundLanes(results);
    }

    private static double[][] filterOutLines(Mat lines) {

        ArrayList<Integer> leftLinePairsByIndex = new ArrayList<Integer>();

        // look for lanes by finding two lines with the similar slope
        // One line on the road creates 2 found edges with the hough transform
        for (int i = 0; i < lines.rows(); i++) {
            //line is already paired, keep going
            if (leftLinePairsByIndex.contains(i)) continue;

            //calc angle
            double[] data = lines.get(i, 0);
            //x1,y1,x2,y2
            double dx = data[0] - data[2];
            double dy = data[1] - data[3];
            float angleOne = (float) (Math.abs((Math.atan2(dy, dx) * 180 / Math.PI)) % 180);
            /*
            System.out.println("(" + data[0] + ", " + data[1] + ")" + "(" + data[2] + ", " + data[3] + ")");
            System.out.println("dx: " + dx + " dy: " + dy );
            System.out.println("Angles: " + angleOne);
            */
            //	Small Angle measured 		OR		Nearly Straight angles measured
            if (angleOne <= LINE_REJECT_DEGREES || angleOne > 180 - LINE_REJECT_DEGREES) {
                //System.out.println("line with " + angleOne + " has been rejected");
                continue;
            }
            for (int j = 0; j < lines.rows(); j++) {
                if (i != j) { //different elements
                    double[] data2 = lines.get(j, 0);
                    dx = data2[0] - data2[2];
                    dy = data2[1] - data2[3];
                    float angleTwo = (float) (Math.abs((Math.atan2(dy, dx) * 180 / Math.PI)) % 180);
                    //pair lines if angle is similar and position is close
                    if (Math.abs(angleOne - angleTwo) < ANGLE_THRESH &&
                            Math.abs(data[0] - data2[0]) < POSITION_THRESH &&
                            Math.abs(data[2] - data2[2]) < POSITION_THRESH) {
                        if (!leftLinePairsByIndex.contains(i)) {
                            leftLinePairsByIndex.add(i);
                            leftLinePairsByIndex.add(j);
                        }
                    }
                }
            }
        }

        // System.out.println("Should draw " + leftLinePairsByIndex.size() / 2 + " left lanes");

        //package the selected lines
        double[][] filteredLines = new double[leftLinePairsByIndex.size() / 2][4];
        for (int i = 0; i < leftLinePairsByIndex.size() / 2; i += 2) {
            //pick the inside one of the paired lines
            double[] data = lines.get(leftLinePairsByIndex.get(i), 0);
            double[] data2 = lines.get(leftLinePairsByIndex.get(i + 1), 0);
            //compare x0 x0 and x1 x1
            filteredLines[i] = (data[0] > data2[0] || data[2] > data2[2]) ? data : data2;
        }
        return filteredLines;
    }

    /**
     * put this in the ndk if perf is a problem (due to passing obj in/out the jvm)
     *
     * @param img - greyscale image
     * @return
     */
    private Mat findLines(Mat img) {
        Imgproc.GaussianBlur(img, img, new Size(5, 5), THETA);
        Imgproc.Canny(img, img, CANNY_MIN, CANNY_MAX);
        Imgproc.HoughLinesP(img, img, 1, Math.PI / 180, HOUGH_TRESHOLD, HOUGH_MIN_LINE_LENGTH, HOUGH_MAX_LINE_GAP);
        return img;
    }

    public void setSize(Mat mat) {
        if (this.sizeSet || mat == null || mat.empty()) {
            return;
        }

        /*
         * 		0,0							width, 0
		 *
		 *
		 *
		 * 		tlleft 			tlright
		 *
		 * 		0,height		bmid		width, height
		 */
        this.totalSize = mat.size();
        Point tlLeft = new Point(0, (3 * totalSize.height) / 4);
        Point tlRight = new Point(totalSize.width / 2, (3 * totalSize.height) / 4);
        Point bmid = new Point(totalSize.width / 2, totalSize.height);
        Point br = new Point(totalSize.width, totalSize.height);

        this.cropSizeLeft = new Rect(tlLeft, bmid);
        this.cropSizeRight = new Rect(tlRight, br);
        this.sizeSet = true;
    }
}
