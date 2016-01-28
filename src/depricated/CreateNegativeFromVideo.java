package depricated;

import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import swing.VideoReader;


public class CreateNegativeFromVideo {

	public static void main(String[] args) {
		// load opencv
	    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

	    //TODO iterate through the video 
	    try {
			VideoReader.initalizeVideoReader("Video/VIDEO0001.mp4");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	    VideoReader.initalizeVideoPlayer(1);
	    
	    Mat image = VideoReader.getNextFrame();
	    
	    ArrayList<Mat> cropped = croppedImage(image, 64, 64);
	    for(int i = 0; i < cropped.size(); i++){
	    	if( i < 10){
	    		Imgcodecs.imwrite("neg/img_000" + i + ".png", cropped.get(i));
	    	}else if( i < 100){
	    		Imgcodecs.imwrite("neg/img_00" + i + ".png", cropped.get(i));
	    	}else if( i < 1000){
	    		Imgcodecs.imwrite("neg/img_0" + i + ".png", cropped.get(i));
	    	}else{
	    		Imgcodecs.imwrite("neg/img_" + i + ".png", cropped.get(i));
	    	}
	    }
	   	    
	}
	
	public static ArrayList<Mat> croppedImage( Mat image, int w, int h){
		
		int imgHeight = image.rows();
		int imgWidth = image.width();
		int numHeight = imgHeight / h;
		int numWidth = imgWidth / w;
		Point tl = new Point(0, 0);
		Point br = new Point(w, h);
		Mat cropped = new Mat();
		ArrayList<Mat> croppedList = new ArrayList<Mat>();
		
		for( int numH = 0; numH < numHeight; numH++){
			for( int numW = 0; numW < numWidth; numW++){
				Rect roi = new Rect(tl, br);
				cropped = new Mat(image, roi);
				croppedList.add(cropped);
				//debuging 
				//System.out.println(tl.toString() + " \n " + br.toString());
				//Imgproc.rectangle(image, tl, br, color);
				tl.x += w;
				br.x += w;
			}
			tl.x = 0;
			br.x = w;
			tl.y += h;
			br.y += h;
		}
		return croppedList;
	}

}

