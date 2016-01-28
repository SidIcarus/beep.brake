package util;

import org.opencv.core.Mat;

public class PreloadSource implements FrameSource {

	private int currIndex = 0;
	private ConcurrentVideoLoader loader;
	
	public PreloadSource(ConcurrentVideoLoader loader){
		this.loader = loader;
	}
	
	/**
	 * for region select redraws the correct img
	 */
	@Override
	public Mat getCurrentFrame() {
		// TODO Auto-generated method stub
		Mat m = loader.getFrame(currIndex);
		return m;
	}

	/**
	 * for video tagger to click through frames
	 * @return
	 */
	public Mat getPrev() {
		currIndex--;
		Mat m = loader.getFrame(currIndex);
		if( m == null || m.empty()){
			//didn't get anything so increment back
			currIndex++;
			m = new Mat();
		}
		return m;
	}
	
	/**
	 * for video tagger to click through frames
	 * @return
	 */
	public Mat getNext() {
		currIndex++;
		Mat m = loader.getFrame(currIndex);
		if( m == null || m.empty()){
			//didnt get anything so roll back
			currIndex--;			
			m = new Mat();
		}
		return m;
	}
	
	public int getCurrentIndex(){
		return currIndex;
	}
	
}
