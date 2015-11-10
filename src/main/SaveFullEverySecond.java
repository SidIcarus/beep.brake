package main;

public class SaveFullEverySecond {

	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/";
	static final String videoname = "vid_0005.mp4";
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
	    
	    VideoReader.initalizeVideoPlayer(1);
	    
	    //set filesize
	    Size readSize = new Size(480,320);
	    Mat mat = VideoReader.getNextFrame(readSize);

	    while( !mat.empty() ) {			
	    	if( VideoReader.getFrameCount() % 32 == 0){
				saveImage( mat );
	    	}
			VideoReader.displayImage(mat, 0);
			mat = VideoReader.getNextFrame(readSize);
	    }
	}

	
	private static void saveImage(Mat mat){
		String filename = "CarTracking/full/" + vidName + "_"  + (VideoReader.getFrameCount() / 32 + 1) + ".png";
		System.out.println("writing file: " + filename);
    	Imgcodecs.imwrite(filename, mat);
	}
}
