package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import Algo.DetectCarByColor;
import util.Slider;
import util.VideoReader;


public class ColorObjectDetection {

	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/";
	static final String videoname = "vid_0005.mp4";
	static final String vidName = videoname.substring(0, videoname.indexOf("."));

	static Scalar sMin = new Scalar(88, 88, 88);
	static Scalar sMax = new Scalar(168, 168, 168);	
	
	
	public static void main(String[] args) {
		// load opencv
	    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	    
	    try {
			VideoReader.initalizeVideoReader(viddir + videoname);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	    
	    //setup frames
	    String[] frameNames = {"original", "threshold", "licenseplate"};
	    VideoReader.initalizeVideoPlayer(frameNames);
	    
	    //set filesize
	    Size readSize = new Size(480,320);
	    Mat mat = VideoReader.getNextFrame(readSize);
	    
	    //find contour setup
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	    List<MatOfPoint> shadows;    
	    List<MatOfPoint> tailLights;    
	    Mat hierarchy = new Mat();
	    
	    //UI
	    Slider.createAndShowGUI();
	    Slider.setInitalPositions(sMin, sMax);
	    Scanner scanner = new Scanner(System.in);
	    
	    //perf debug
	    long start;
	    long stop;
	    
	    //infinite loop video
	    while(true){
		    while( !mat.empty() ){
		    	Mat display = new Mat();
		    	mat.copyTo(display);
		    	start = System.currentTimeMillis();
		    	/* Look for Shadow  */
		    	shadows = DetectCarByColor.carShadow(mat);
		    	/* Look for Tail Light */
		    	tailLights = DetectCarByColor.carTailLight(mat);
		    	/* Find License Plate based on shadow and tail light */
		    	Mat licensePlate = DetectCarByColor.cropToLicensePlate(shadows, tailLights);
		    	
		    	
		    	/* User Defined */
		    	//rgb --> hsv
		    	Mat hsv = new Mat();
		        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV);
		        //Threshold Matrix
		        Mat threshold = new Mat();
		        sMin = Slider.getMin();
		        sMax = Slider.getMax();
		        Core.inRange(hsv, sMin, sMax, threshold);
		        Imgproc.erode(threshold, threshold, new Mat(2,2, CvType.CV_8U));
		        Imgproc.dilate(threshold, threshold, new Mat(2,2, CvType.CV_8U));
		        //Find car in threshold img
		        Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
		        stop = System.currentTimeMillis();
		        
		        //log on frame
		        String text = Long.toString(stop - start) + " ms";
		        Imgproc.putText(display, text, new Point(20,20), 1, 1.2, new Scalar(20,200,20));
		        Imgproc.putText(display, text, new Point(22,22), 1, 1.2, new Scalar(200,20,200));
		        Imgproc.putText(display, text, new Point(24,24), 1, 1.2, new Scalar(200,200,20));
		        
		        //Red for shadows
//		        drawBoxs(display, shadows, new Scalar(0,0,255));
//		        shadows.clear();
		        
		        //Blue tail lights
		        drawBoxs(display, tailLights, new Scalar(255,0,0));
		        tailLights.clear();
		        
		        //purple
		        //drawBoxs(display, contours, new Scalar(160,32,240));
		        //contours.clear();

		        
		        //next frame
		        VideoReader.displayImage(display, 0);
		        VideoReader.displayImage(threshold, 1);
		        //VideoReader.displayImage(licensePlate, 2);
		        
		        mat = VideoReader.getNextFrame(readSize);
		        //blocking on input
		        //while( !scanner.hasNext());
		        //scanner.next();
		    }
	        mat = VideoReader.getNextFrame(readSize);
	        //Check for input here
	    }
	    
	}

	public static void drawBoxs(Mat mat, List<MatOfPoint> contours, Scalar color ){
		//draw boxs 
        for(int i=0; i< contours.size();i++){
            if (Imgproc.contourArea(contours.get(i)) > 50 ){
                Rect rect = Imgproc.boundingRect(contours.get(i));
              	Imgproc.rectangle(mat, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), color);
                
            }
        }		
	}
	
	public static void setMaxHSV(Scalar max){
		sMax = max;
	}
	
	public static void setMinHSV(Scalar min){
		sMin = min;
	}
}
