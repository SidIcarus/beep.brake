package main;

import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import util.Slider;
import util.VideoReader;

public class LaneDetection {

	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/";
	static final String videoname = "vid_0002.mp4";
	static final String vidName = videoname.substring(0, videoname.indexOf("."));

	static Scalar sMin = new Scalar(88, 88, 88);
	static Scalar sMax = new Scalar(168, 168, 168);
	
	//Canny constants
	static final int 		CANNY_MIN = 1;	  			// edge detector minimum hysteresis threshold
	static final int 		CANNY_MAX = 100;		// edge detector maximum hysteresis threshold
	static final int 		HOUGH_TRESHOLD = 100;			// line approval vote threshold
	static final int 		HOUGH_MIN_LINE_LENGTH = 50;		// remove lines shorter than this treshold
	static final int 		HOUGH_MAX_LINE_GAP = 100;		// join lines to one with smaller than this gaps
	static final int 		LINE_REJECT_DEGREES = 10;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		try {
			VideoReader.initalizeVideoReader(viddir + videoname);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// setup frames
		String[] frameNames = { "Display", "Grey", "Canny"};
		VideoReader.initalizeVideoPlayer(frameNames);

		// set filesize
		Size readSize = new Size(480, 320);
		Mat mat = VideoReader.getNextFrame(readSize);

		String[] sliderNames = { "Canny Min", "Canny Max", "Rho", "Theta", "Hough Thresh", "MinLineLen", "MaxLineGap"};
		Slider.createAndShowGUI(sliderNames);
		//Slider.setInitalPositions(sMin, sMax);

		// canny
		double rho = 100;
		double theta = (5 * Math.PI)/180;
		Mat grey = new Mat();
		Mat canny = new Mat();
		Mat lines = new Mat();


		while (true) {
			while (!mat.empty()) {
				Mat display = new Mat();
				Imgproc.resize(mat, display, readSize);
				
				long start = System.currentTimeMillis();
				Imgproc.cvtColor(mat, grey, Imgproc.COLOR_BGR2GRAY);
				Imgproc.GaussianBlur(grey, grey, new Size(5,5), theta);
				//get slider values
				int[] input = Slider.getSliderValues();
				int cannymin = input[0];
				int cannymax = input[1];
				double rhoInput = input[2];
				double thetaInput = input[3];
				int houghThreshold = input[4];
				int minLineLen = input[5];
				int maxLineGap = input[6];
				for(int i = 0; i < input.length; i++){
					System.out.print(input[i] + " ");
				}
				System.out.println();
				
				Imgproc.Canny(grey, canny, cannymin, cannymax);
				Imgproc.HoughLinesP(canny, lines, rhoInput, thetaInput, houghThreshold,minLineLen,maxLineGap);
				drawLines(display, lines, new Scalar(40, 200, 50));
				long stop = System.currentTimeMillis();

		        //log on frame
		        String text = Long.toString(stop - start) + " ms";
		        Imgproc.putText(display, text, new Point(20,20), 1, 1.2, new Scalar(20,200,20));
		        Imgproc.putText(display, text, new Point(22,22), 1, 1.2, new Scalar(200,20,200));
		        Imgproc.putText(display, text, new Point(24,24), 1, 1.2, new Scalar(200,200,20));
				
				// display
				VideoReader.displayImage(display, 0);
				VideoReader.displayImage(grey, 1);
				VideoReader.displayImage(canny, 2);
		        
				// next frame
				mat = VideoReader.getNextFrame(readSize);
			}
			mat = VideoReader.getNextFrame(readSize);
		}

	}
/*
	public static void drawLines(Mat mat, Mat lines, Scalar color) {
		double[] data;
		double rho, theta;
		Point pt1 = new Point();
		Point pt2 = new Point();
		double a, b;
		double x0, y0;
		for (int i = 0; i < lines.cols(); i++) {
			data = lines.get(0, i);
			rho = data[0];
			theta = data[1];
			a = Math.cos(theta);
			b = Math.sin(theta);
			x0 = a * rho;
			y0 = b * rho;
			pt1.x = Math.round(x0 + 1000 * (-b));
			pt1.y = Math.round(y0 + 1000 * a);
			pt2.x = Math.round(x0 - 1000 * (-b));
			pt2.y = Math.round(y0 - 1000 * a);
			Imgproc.line(mat, pt1, pt2, color, 1);
		}

	}
*/
	public static void drawLines(Mat mat, Mat lines, Scalar color) {
		double[] data;
		double rho, theta;
		Point pt1 = new Point();
		Point pt2 = new Point();
		double a, b;
		double x0, y0;
		System.out.println("row: " + lines.rows());
		System.out.println("col: " + lines.cols());
		for( int i = 0; i < lines.rows(); i++){
			data = lines.get(i, 0);
			if( data.length == 4){
				//x1,y1,x2,y2					
				double dx = data[1] - data[0];
				double dy = data[3] - data[2];
				float angle = (float) (Math.atan2(dy, dx) * 180/Math.PI);
				if( Math.abs(angle) <= LINE_REJECT_DEGREES){
					continue;
				}
				pt1 = new Point(data[0], data[1]);
				pt2 = new Point(data[2], data[3]);
				Imgproc.line(mat, pt1, pt2, color);
				VideoReader.displayImage(mat, 0);

			}else if( data.length == 2){
				// r theta 
				rho = data[0];
				theta = data[1];
				double angle = theta * 180 / Math.PI;

				a = Math.cos(theta);
				b = Math.sin(theta);
				x0 = a * rho;
				y0 = b * rho;
				pt1.x = Math.round(x0 + 1000 * (-b));
				pt1.y = Math.round(y0 + 1000 * a);
				pt2.x = Math.round(x0 - 1000 * (-b));
				pt2.y = Math.round(y0 - 1000 * a);
				Imgproc.line(mat, pt1, pt2, color, 1);
				VideoReader.displayImage(mat, 0);

			}
		}
	
	}
	
	public static void oldDetectLines(){
		/*long start = System.currentTimeMillis();
		// Threshold Matrix on color
		int widthMidPt = mat.width() / 2;
		//crop left and right
		Rect leftRegion = new Rect(new Point(0,0), new Point(widthMidPt, mat.height()));
		left = new Mat(mat, leftRegion);
		
		Rect rightRegion = new Rect(new Point(widthMidPt,0), new Point(widthMidPt * 2, mat.height()));
		right = new Mat(mat, rightRegion);
		
		//look for lanes in left img
		Imgproc.cvtColor(left, left, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(left, cannyLeft, cannyThreshOne, cannyThreshTwo);
		Imgproc.HoughLines(cannyLeft, linesLeft, 2, Math.PI / 180, 100);
		//look for lanes in right img
		Imgproc.cvtColor(right, right, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(right, cannyRight, cannyThreshOne, cannyThreshTwo);
		Imgproc.HoughLines(cannyRight, linesRight, 2, Math.PI / 180, 100);
		long stop = System.currentTimeMillis();
		
		drawLines(mat, linesLeft, new Scalar(40, 200, 50));
		drawLines(mat, linesRight, new Scalar(40, 200, 50));*/
	}

}
