package main;

public class TestCarTrackingAlgorithm {
	
	/* ***************** CASCADES *****************	
	 * Kevin stole frome somewhere online
	 * It works better for door stop */
	//static final String cascadePath = "cascade/cars3.xml";
	
	/* Trained from http://www.gti.ssr.upm.es/data/Vehicle_database.html */
	/* 200 Positive */
	//static final String cascadePath = "cascade/cascade200.xml";
	/* 500 Positive */ 
	//static final String cascadePath = "cascade/cascade500.xml";
	/* 360 Positive  950 Negative and removed shitty images */
	//static final String cascadePath = "cascade/cascade_360_950_withremoval.xml";
	//static final String cascadePath = "cascade/cascade_iteration_2.xml";
	static final String cascadePath = "cascade/cascade_iteration_3.xml";
	
	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/";
	static final String videoname = "vid_0005.mp4";
	static final String vidName = videoname.substring(0, videoname.indexOf("."));

	
	static Scalar color = new Scalar(0, 250, 0);
	
	public static void main(String[] args) {		
		// load opencv
	    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);		
		
		long start;
		long end;
		String detectionTime;
		MatOfRect found;

		//TODO switch algorithm here if needed
		DetectCar.initializeHaar(cascadePath);
		
	    try {
			VideoReader.initalizeVideoReader(viddir + videoname );
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	    
	    //Opens 1 JFrame
	    VideoReader.initalizeVideoPlayer(1);
	    
	    //set filesize
	    Size readSize = new Size(480,320);
	    //read video and converts to given size
	    Mat mat = VideoReader.getNextFrame(readSize);
	    
	    while( !mat.empty() ) {			
			//Look for a car

	    	start = System.currentTimeMillis();
			//TODO switch algorithm here if needed
			found = DetectCar.haar(mat);
			end = System.currentTimeMillis();
			detectionTime = Long.toString(end - start);
			
			//debug output
			System.out.println( mat.toString() + " col: " + mat.cols() + " rows: " + mat.rows());
			System.out.println("Detection Time: " + detectionTime + "ms");
			System.out.println("Num of objects found: " + found.toArray().length);
			
			//save found images (should be about once a second)
	    	if(VideoReader.getFrameCount() % 32 == 0){
				saveFoundImages( mat, found );
	    	}
	    	
			drawSquares(mat, found);

			
			
			
			VideoReader.displayImage(mat, 0);
			mat = VideoReader.getNextFrame(readSize);
	    }


	}
	
	private static void drawSquares(Mat img, MatOfRect found){
		Rect[] array = found.toArray();
		for( Rect r : array){
			Imgproc.rectangle(img, r.br(), r.tl(), color);
		}
		
	}
	
	// saves images found by the algorithm
	// use for training 
	private static void saveFoundImages(Mat mat, MatOfRect found){
		Rect[] foundRect = found.toArray();
		Mat imgCropped = new Mat();
		
		// No Detected 
		if(foundRect.length == 0){
			//write the whole image
			String filename = "CarTracking/full/" + vidName + "_"  + (VideoReader.getFrameCount() / 32 + 1) + ".png";
			System.out.println("writing file: " + filename);
	    	Imgcodecs.imwrite(filename, mat);
	    }
		
		// Manually split these up between positive and negative images
		for( int i = 0; i < foundRect.length; i++){
			imgCropped = new Mat (mat, foundRect[i]);
			String filename = "CarTracking/found/" + vidName + "_"  + (VideoReader.getFrameCount() / 32 + 1) + "_" + i + ".png";
			System.out.println("writing file: " + filename);
	    	Imgcodecs.imwrite(filename, imgCropped);
	 
	   	}
		
	}

}
