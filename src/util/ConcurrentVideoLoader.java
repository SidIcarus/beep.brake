package util;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import swing.VideoReader;

public class ConcurrentVideoLoader implements Runnable {
	
	private final Size readSize;
	private ReentrantLock listLock = new ReentrantLock();
	private ArrayList<Mat> matList = new ArrayList<Mat>();
	private int listIndex = 0;

	public ConcurrentVideoLoader(Size readSize){
		this.readSize = readSize;
	}
	
	@Override
	/**
	 * precondition - VideoReader is already loaded with video
	 */
	public void run() {
	    //load up video
		Mat mat = VideoReader.getNextFrame(readSize);
	    while( !mat.empty() ) {			
	    	if( VideoReader.getFrameCount() % 32 == 0){
	    		listLock.lock();
	    		System.out.println("adding");
	    		matList.add(mat);
	    		listIndex++;
	    		listLock.unlock();
	    	}
			mat = VideoReader.getNextFrame(readSize);
	    }
		
	}
	
	public Mat getFrame(int index){
		if( 0 <= index && index < matList.size() ){
			listLock.lock();
    		System.out.println("getting");
			Mat mat = matList.get(index);
			listLock.unlock();
			return mat;
		}
		return new Mat();
	}

}
