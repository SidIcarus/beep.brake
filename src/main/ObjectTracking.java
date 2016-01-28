package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import swing.Tagger;
import swing.VideoReader;
import util.LiveSource;
import util.RegionSelector;

public class ObjectTracking {

	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/day/";
	static final String videoname = "day_0007.mp4";
	static final String vidName = videoname.substring(0, videoname.indexOf("."));

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);		
	    try {
			VideoReader.initalizeVideoReader( viddir + videoname );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

	    String[] frames = {"tracking", "backproj"};
	    VideoReader.initalizeVideoPlayer( frames );
	    LiveSource source = new LiveSource();
	    VideoReader.enableRegionSelector(0, source);
	    

	    //set filesize
	    Size readSize = new Size(640,360);
	    
	    Mat mat = VideoReader.getNextFrame(readSize);
	    source.setCurrentFrame(mat);
	    VideoReader.displayImage(mat, 0);
	    while(RegionSelector.selected.size() == 0){
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    Rect roi = RegionSelector.selected.get(0);
		TermCriteria criteria = new TermCriteria();
		criteria.type = TermCriteria.EPS;
		criteria.epsilon = 1;
		criteria.maxCount = 10;
		

	    int h_bins = 30; 
	    int s_bins = 32;

	    MatOfInt histSize = new MatOfInt (h_bins, s_bins);

	    MatOfFloat ranges = new MatOfFloat(0, 179, 0, 255);
	    MatOfInt channels = new MatOfInt(0, 1);
	    Scalar color = new Scalar(255,10, 10);
	    
	    while (true) {
			while (!mat.empty()) {
				Mat display = new Mat();
				mat.copyTo(display);
				Mat hsv = new Mat();
				
				//hsv
				Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV);
				ArrayList<Mat> images = new ArrayList<Mat>();
				images.add(hsv);
				//histogram
				Mat hist = new Mat();
		        Imgproc.calcHist(Arrays.asList(hsv), channels, new Mat(), hist, histSize, ranges);
		        //Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX, -1, new Mat());
		        Mat backproj = new Mat();
				Imgproc.calcBackProject(images, channels, hist, backproj, ranges, 1);
				RotatedRect tracked = Video.CamShift(backproj, roi, criteria);
				Point pt1 = tracked.boundingRect().tl();
				Point pt2 = tracked.boundingRect().br();
				Imgproc.rectangle(display, pt1, pt2, color);
				
				// display
				VideoReader.displayImage(display, 0);
				VideoReader.displayImage(backproj, 1);
		        
				// next frame
				mat = VideoReader.getNextFrame(readSize);
				
				// Block with scanner input per frame
//				while(!in.hasNext());
//				in.nextLine();
			}
			mat = VideoReader.getNextFrame(readSize);
		}
	}

}
