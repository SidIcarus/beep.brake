package Lane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import swing.Slider;
import swing.VideoReader;

public class LaneDetection {

	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/No_car/";
	static final String videoname = "no_car_0002.mp4";
	static final String vidName = videoname.substring(0, videoname.indexOf("."));

	static Scalar sMin = new Scalar(88, 88, 88);
	static Scalar sMax = new Scalar(168, 168, 168);
	
	//Canny constants
	// 150 300 28 0 50 17 

	static final int 		CANNY_MIN = 25;	  				// edge detector minimum hysteresis threshold
	static final int 		CANNY_MAX = 150;				// edge detector maximum hysteresis threshold
	static final int 		HOUGH_TRESHOLD = 40;			// line approval vote threshold
	static final int 		HOUGH_MIN_LINE_LENGTH = 10;		// remove lines shorter than this treshold
	static final int 		HOUGH_MAX_LINE_GAP = 25;		// join lines to one with smaller than this gaps
	static final int 		LINE_REJECT_DEGREES = 10;
	
	static final int 		ANGLE_THRESH = 2;				//angle between two paired edges of a lane
	static final int 		POSITION_THRESH = 15;			//width of the lane in pixels 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		try {
//			VideoReader.initalizeVideoReader(viddir + "testline.jpg");
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

		// canny
		double rho = 100;
		double theta = (5 * Math.PI)/180;
		Mat grey = new Mat();
		Mat cannyLeft = new Mat();
		Mat cannyRight = new Mat();
		// hough lines 
		Mat hLinesLeft = new Mat();
		Mat hLinesRight = new Mat();

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
		Point tlRight = new Point(readSize.width/2, (3 * readSize.height)/4);
		Point bmid = new Point(readSize.width/2, readSize.height);
		Point br = new Point(readSize.width, readSize.height);
		
		
		Rect cropSizeLeft = new Rect(tlLeft, bmid );
		Rect cropSizeRight = new Rect(tlRight, br);
		
		
		Scanner in = new Scanner(System.in);
		
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
				System.out.println();
				Imgproc.Canny(roiLeft, cannyLeft, CANNY_MIN, CANNY_MAX);
				Imgproc.Canny(roiRight, cannyRight, CANNY_MIN, CANNY_MAX);
				Imgproc.HoughLinesP(cannyLeft, hLinesLeft, 1, Math.PI/180, HOUGH_TRESHOLD, HOUGH_MIN_LINE_LENGTH, HOUGH_MAX_LINE_GAP);
				Imgproc.HoughLinesP(cannyRight, hLinesRight, 1, Math.PI/180, HOUGH_TRESHOLD, HOUGH_MIN_LINE_LENGTH, HOUGH_MAX_LINE_GAP);
				int leftOffsetX = (int) (readSize.width - (readSize.width - tlLeft.x));
				int leftOffsetY = (int) (readSize.height - (readSize.height - tlLeft.y));
				int rightOffsetX = (int) (readSize.width - (readSize.width - tlRight.x));
				int rightOffsetY = (int) (readSize.height - (readSize.height - tlRight.y));

				double[][] filteredLinesLeft = filterOutLines(hLinesLeft);
				double[][] filteredLinesRight = filterOutLines(hLinesRight);
				
				drawLines(display, filteredLinesLeft, new Scalar(40, 200, 50), leftOffsetX, leftOffsetY);
				drawLines(display, filteredLinesRight, new Scalar(40, 200, 50), rightOffsetX, rightOffsetY);

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
				
				// Block with scanner input per frame
//				while(!in.hasNext());
//				in.nextLine();
			}
			mat = VideoReader.getNextFrame(readSize);
		}

	}

	public static double[][] filterOutLines(Mat lines){
		
		ArrayList<Integer> leftLinePairsByIndex = new ArrayList<Integer>();
		
		// look for lanes by finding two lines with the similar slope
		// One line on the road creates 2 found edges with the hough transform
		for( int i = 0; i < lines.rows(); i++){
			//line is already paired, keep going
			if( leftLinePairsByIndex.contains(i) ){
				continue;
			}
			
			//calc angle
			double[] data = lines.get(i, 0);
			//x1,y1,x2,y2
			double dx = data[0] - data[2];
			double dy = data[1] - data[3];
			float angleOne = (float) (Math.abs((Math.atan2(dy, dx) * 180/Math.PI)) % 180);
			System.out.println("(" + data[0] + ", " + data[1] + ")" + "(" + data[2] + ", " + data[3] + ")");
			System.out.println("dx: " + dx + " dy: " + dy );
			System.out.println("Angles: " + angleOne);
			//	Small Angle measured 		OR		Nearly Straight angles measured
			if( angleOne <= LINE_REJECT_DEGREES || angleOne > 180 - LINE_REJECT_DEGREES){
				System.out.println("line with " + angleOne + " has been rejected");
				continue;
			}
			for( int j = 0; j < lines.rows(); j++){
				if( i != j){ //different elements
					double[] data2 = lines.get(j, 0);
					dx = data2[0] - data2[2];
					dy = data2[1] - data2[3];
					float angleTwo = (float) (Math.abs((Math.atan2(dy, dx) * 180/Math.PI)) % 180);
					//pair lines if angle is similar and position is close
					if( Math.abs(angleOne - angleTwo) < ANGLE_THRESH && 
						Math.abs(data[0] - data2[0]) < POSITION_THRESH && 
						Math.abs(data[2] - data2[2]) < POSITION_THRESH ){						
						if(!leftLinePairsByIndex.contains(i)){
							leftLinePairsByIndex.add(i);
							leftLinePairsByIndex.add(j);
						}
					}
				}
			}
		}
		
		System.out.println("Should draw " + leftLinePairsByIndex.size() / 2 + " left lanes");
		
		// look for right lanes by finding two lines with the 'same' slope 
		// and sloped towards the left lane
		
		
		
		//package the selected lines
		double[][] filteredLines = new double[leftLinePairsByIndex.size() / 2][4];
		for( int i = 0; i < leftLinePairsByIndex.size()/2; i+=2){
			//pick the inside one of the paired lines 
			double[] data = lines.get(leftLinePairsByIndex.get(i), 0);
			double[] data2 = lines.get(leftLinePairsByIndex.get(i+1), 0);
			//compare x0 x0 and x1 x1
			if( data[0] > data2[0] || data[2] > data2[2]){
				filteredLines[i] = data;
			}else{
				filteredLines[i] = data2;
			}
		}
		
		return filteredLines;
		
		
	}
	
	public static void drawLines(Mat mat, Mat lines, Scalar color, int offsetx, int offsety) {
		double[] data;
		double rho, theta;
		Point pt1 = new Point();
		Point pt2 = new Point();
		double a, b;
		double x0, y0;
		for( int i = 0; i < lines.rows(); i++){
			data = lines.get(i, 0);
			if( data.length == 4){
				//x1,y1,x2,y2							
				pt1 = new Point(offsetx + data[0], offsety + data[1]);
				pt2 = new Point(offsetx + data[2], offsety + data[3]);
				Imgproc.line(mat, pt1, pt2, color);
				VideoReader.displayImage(mat, 0);

			}else if( data.length == 2){
				// r theta 
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
				VideoReader.displayImage(mat, 0);

			}
		}
	}
	
	public static void drawLines(Mat mat, double[][] lines, Scalar color, int offsetx, int offsety) {
		Point pt1 = new Point();
		Point pt2 = new Point();
		for( int i = 0; i < lines.length; i++){
			double[] data = lines[i];
			//x1,y1,x2,y2								
			pt1 = new Point(offsetx + data[0], offsety + data[1]);
			pt2 = new Point(offsetx + data[2], offsety + data[3]);
			Imgproc.line(mat, pt1, pt2, color);
			VideoReader.displayImage(mat, 0);
		}
	}
}
