package util;

import org.opencv.core.Mat;

public class LiveSource implements FrameSource {

	private Mat currMat = new Mat();
	
	@Override
	public Mat getCurrentFrame() {
		return currMat;
	}
	
	public void setCurrentFrame(Mat mat){
		this.currMat = mat;
	}

}
