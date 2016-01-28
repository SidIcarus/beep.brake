package edu.rit.se.beepbrake.Analysis;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import edu.rit.se.beepbrake.TempLogger;

/**
 * Created by richykapadia on 9/21/15
 *
 * Temporary UI logic used to draw rects over imgs
 *
 */
public class CameraPreview implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final static String TAG = "Camera-Preview";

    private ArrayList<FrameAnalyzer> mFrameAnalyzers = new ArrayList<FrameAnalyzer>();
    //locations to draw boxes
    static List<Rect> sFoundCars = new ArrayList<Rect>();
    static double[][] sFoundLines = new double[0][0];

    // points reused to draw
    final Point rectPoint1 = new Point();
    final Point rectPoint2 = new Point();

    final Point linePoint1 = new Point();
    final Point linePoint2 = new Point();

    //color of rects
    final Scalar rectColor = new Scalar(0, 0, 255);
    //color of line
    final Scalar lineColor = new Scalar(0, 255, 0);

    public CameraPreview(){
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        TempLogger.incrementCount(TempLogger.TOTAL_FRAMES);
        //multiple starts will be logged
        for( FrameAnalyzer fa : mFrameAnalyzers){
            fa.addFrameToAnalyze(inputFrame.rgba());
        }
        //To show tracking of an object
        Mat result = inputFrame.rgba();
        result = drawBox(result);
        result = drawLines(result);
        return result;
    }

    public static void setPointsToDraw(MatOfRect loc ){
        sFoundCars = loc.toList();

    }

    public static void setPointsToDraw(Rect r){
        sFoundCars.clear();
        sFoundCars.add(r);
    }

    public static void setLinesToDraw(double[][] lines){
        sFoundLines = lines;
    }

    private Mat drawBox( Mat rgb ){
        for (Rect rect : sFoundCars) {
            if(rect != null) {
                rectPoint1.x = rect.x;
                rectPoint1.y = rect.y;
                rectPoint2.x = rect.x + rect.width;
                rectPoint2.y = rect.y + rect.height;
                // Draw rectangle around found object
                Imgproc.rectangle(rgb, rectPoint1, rectPoint2, rectColor, 2);
            }
        }
        return rgb;
    }

    private Mat drawLines( Mat rgb ){
        if(sFoundLines.length > 0){
            for( double[] data : sFoundLines ){
                if(data.length == 4) {
                    linePoint1.x = data[0];
                    linePoint1.y = data[1];
                    linePoint2.x = data[2];
                    linePoint2.y = data[3];
                    Imgproc.line(rgb, linePoint1, linePoint2, lineColor, 2);
                }
            }
        }
        return rgb;
    }

    public void addAnalyzer(FrameAnalyzer fa){
        this.mFrameAnalyzers.add(fa);
    }

}

