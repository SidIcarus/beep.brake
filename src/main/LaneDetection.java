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
		String[] frameNames = { "original", "threshold", "canny" };
		VideoReader.initalizeVideoPlayer(frameNames);

		// set filesize
		Size readSize = new Size(480, 320);
		Mat mat = VideoReader.getNextFrame(readSize);

		Slider.createAndShowGUI();
		Slider.setInitalPositions(sMin, sMax);

		// canny
		Mat canny = new Mat();
		Mat grey = new Mat();
		Mat threshold = new Mat();
		Mat lines = new Mat();
		Mat left = new Mat();
		Mat right = new Mat();

		while (true) {
			while (!mat.empty()) {
				long start = System.currentTimeMillis();
				// Threshold Matrix on color
				int widthMidPt = mat.width() / 2;
				//crop left and right
				Rect leftRegion = new Rect(0,0, widthMidPt, mat.height());
				left = new Mat();
				Imgproc.cvtColor(mat, grey, Imgproc.COLOR_BGR2GRAY);

				Imgproc.Canny(grey, canny, 50, 200);
				Imgproc.HoughLines(canny, lines, 2, Math.PI / 180, 100);
				long stop = System.currentTimeMillis();
				drawLines(mat, lines, new Scalar(40, 200, 50));

		        //log on frame
		        String text = Long.toString(stop - start) + " ms";
		        Imgproc.putText(mat, text, new Point(20,20), 1, 1.2, new Scalar(20,200,20));
		        Imgproc.putText(mat, text, new Point(22,22), 1, 1.2, new Scalar(200,20,200));
		        Imgproc.putText(mat, text, new Point(24,24), 1, 1.2, new Scalar(200,200,20));
				
				// display
				VideoReader.displayImage(mat, 0);
				VideoReader.displayImage(grey, 1);
				VideoReader.displayImage(canny, 2);
		        
				// next frame
				mat = VideoReader.getNextFrame(readSize);
			}
			mat = VideoReader.getNextFrame(readSize);
		}

	}

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

}
