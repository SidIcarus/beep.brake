package main;

import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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
		String[] frameNames = { "Display", "Left", "Right"};
		VideoReader.initalizeVideoPlayer(frameNames);

		// set filesize
		Size readSize = new Size(480, 320);
		Mat mat = VideoReader.getNextFrame(readSize);

		String[] sliderNames = { "Canny Min", "Canny Max", "Rho", "Hough Thresh", "MinLineLen", "MaxLineGap"};
		Slider.createAndShowGUI(sliderNames);
		//Slider.setInitalPositions(sMin, sMax);

		// canny
		double rho = 100;
		double theta = (5 * Math.PI)/180;
		Mat grey = new Mat();
		Mat cannyLeft = new Mat();
		Mat cannyRight = new Mat();
		Mat linesLeft = new Mat();
		Mat linesRight = new Mat();

		/*
		 * 		0,0							width, 0 
		 * 		
		 * 					
		 * 
		 * 		tlleft 			tlright				
		 * 
		 * 		0,height		bmid		width, height
		 */
		Point tlLeft = new Point(0, (3 * readSize.height)/4);
		Point bmid = new Point(readSize.width/2, readSize.height);
		Point tlRight = new Point(readSize.width/2, (3 * readSize.height)/4);
		Point br = new Point(readSize.width, readSize.height);
		
		
		Rect cropSizeLeft = new Rect(tlLeft, bmid );
		Rect cropSizeRight = new Rect(tlRight, br);
		

		while (true) {
			while (!mat.empty()) {
				Mat display = new Mat();
				Imgproc.resize(mat, display, readSize);
				long start = System.currentTimeMillis();
				Mat roiLeft = new Mat(mat, cropSizeLeft);
				Mat roiRight = new Mat(mat, cropSizeRight);
				Imgproc.cvtColor(roiLeft, roiLeft, Imgproc.COLOR_BGR2GRAY);
				Imgproc.cvtColor(roiRight, roiRight, Imgproc.COLOR_BGR2GRAY);				
				Imgproc.GaussianBlur(roiLeft, roiLeft, new Size(5,5), theta);
				Imgproc.GaussianBlur(roiRight, roiRight, new Size(5,5), theta);
				//get slider values
				int[] input = Slider.getSliderValues();
				int cannymin = input[0];
				int cannymax = input[1];
				double rhoInput = input[2];
				int houghThreshold = input[3];
				int minLineLen = input[4];
				int maxLineGap = input[5];
				for(int i = 0; i < input.length; i++){
					System.out.print(input[i] + " ");
				}
				System.out.println();
				Imgproc.Canny(roiLeft, cannyLeft, cannymin, cannymax);
				Imgproc.Canny(roiRight, cannyRight, cannymin, cannymax);
				Imgproc.HoughLinesP(cannyLeft, linesLeft, rhoInput, theta, houghThreshold, minLineLen, maxLineGap);
				Imgproc.HoughLinesP(cannyRight, linesRight, rhoInput, theta, houghThreshold, minLineLen, maxLineGap);
				int leftOffsetX = (int) (readSize.width - (readSize.width - tlLeft.x));
				int leftOffsetY = (int) (readSize.height - (readSize.height - tlLeft.y));
				int rightOffsetX = (int) (readSize.width - (readSize.width - tlRight.x));
				int rightOffsetY = (int) (readSize.height - (readSize.height - tlRight.y));

				drawLines(display, linesLeft, new Scalar(40, 200, 50), leftOffsetX, leftOffsetY);
				drawLines(display, linesRight, new Scalar(40, 200, 50), rightOffsetX, rightOffsetY);

				long stop = System.currentTimeMillis();

		        //log on frame
		        String text = Long.toString(stop - start) + " ms";
		        Imgproc.putText(display, text, new Point(20,20), 1, 1.2, new Scalar(20,200,20));
		        Imgproc.putText(display, text, new Point(22,22), 1, 1.2, new Scalar(200,20,200));
		        Imgproc.putText(display, text, new Point(24,24), 1, 1.2, new Scalar(200,200,20));
				
				// display
				VideoReader.displayImage(display, 0);
				VideoReader.displayImage(cannyLeft, 1);
				VideoReader.displayImage(cannyRight, 2);
		        
				// next frame
				mat = VideoReader.getNextFrame(readSize);
			}
			mat = VideoReader.getNextFrame(readSize);
		}

	}

	public static void drawLines(Mat mat, Mat lines, Scalar color, int offsetx, int offsety) {
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
				pt1 = new Point(offsetx + data[0], offsety + data[1]);
				pt2 = new Point(offsetx + data[2], offsety + data[3]);
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
}
