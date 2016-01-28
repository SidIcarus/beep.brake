package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import swing.Tagger;
import swing.VideoReader;
import util.ConcurrentVideoLoader;
import util.FrameSource;
import util.PreloadSource;
import util.RegionSelector;
import util.TagIO;

/**
 * Go through video second by second
 * manually mark the rear of any car 
 * 
 * @author richykapadia
 *
 */
public class VideoTagging {

	/* ***************** VIDEO ***************** */
	static final String viddir = "Video/day/";
	static final String videoname = "day_0001.mp4";
	static final String vidName = videoname.substring(0, videoname.indexOf("."));
	private static ConcurrentVideoLoader loader;
	private static Tagger tagger;
	private static HashMap<Integer, ArrayList<Rect>> tags = new HashMap<Integer, ArrayList<Rect>>();
	private static PreloadSource source;
	
	
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
	    tagger = new Tagger();
	    
	    //set filesize
	    Size readSize = new Size(480,320);
	    
	    //Load up video
	    loader = new ConcurrentVideoLoader(readSize);
	    (new Thread(loader)).start();
	    
	    //enables regions selector 
	    source = new PreloadSource(loader);
	    VideoReader.enableRegionSelector(0, source);
	    
	    //display first frame
	    Mat m = new Mat();
	    while( m.empty() ){
	    	m = loader.getFrame(0);
	    }
	    System.out.println("displaying first frame");
	    tagger.setStatus("Frame : " + source.getCurrentIndex());
	    VideoReader.displayImage(m, 0);
	}

	
	private static void saveImage(Mat mat){
		String filename = "CarTracking/full/" + vidName + "_"  + (VideoReader.getFrameCount() / 32 + 1) + ".png";
		System.out.println("writing file: " + filename);
    	Imgcodecs.imwrite(filename, mat);
	}


	public static void next() {
		ArrayList<Rect> rects = new ArrayList<Rect>();
		for(Rect r : RegionSelector.selected){
			rects.add(r);
		}
		int index = source.getCurrentIndex();
		tags.put(index, rects);
		RegionSelector.selected.clear();
    	Mat m = source.getNext();
		index = source.getCurrentIndex();
    	if( m != null || !m.empty()){
    		VideoReader.displayImage(m, 0);
    		if(tags.containsKey(index)){
    			ArrayList<Rect> curr = tags.get(index);
    			RegionSelector.selected.clear();
    			//populate selected 
    			for( Rect r : curr){
    				RegionSelector.selected.add(r);
    			}
    		}
    		VideoReader.getRegionSelector(0).updateRect(0);
    	}
	    tagger.setStatus("Frame : " + source.getCurrentIndex());
	}
	
	public static void prev() {
		int index = source.getCurrentIndex();
    	Mat m = source.getPrev();
		index = source.getCurrentIndex();
    	if( m != null || !m.empty()){
    		VideoReader.displayImage(m, 0);
    		if(tags.containsKey(index)){
    			ArrayList<Rect> curr = tags.get(index);
    			RegionSelector.selected.clear();
    			//populate selected 
    			for( Rect r : curr){
    				RegionSelector.selected.add(r);
    			}
    		}
    		VideoReader.getRegionSelector(0).updateRect(0);
    	}
    	
	    tagger.setStatus("Frame : " + source.getCurrentIndex());
	}
	
	public static void clearFrame(){
		int index = source.getCurrentIndex();
		if( tags.containsKey(index) ){
			tags.remove(index);
		}
		RegionSelector.selected.clear();
		VideoReader.getRegionSelector(0).updateRect(0);
	}


	public static Mat getCurrMat() {
		return loader.getFrame(source.getCurrentIndex());
	}
	
	public static void saveTags(){
		TagIO tagOutput = new TagIO(viddir + videoname );
		tagOutput.writeTags(tags);
	}
}
