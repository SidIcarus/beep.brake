package util;

import org.opencv.core.Mat;

public interface FrameSource {
	
	public Mat getCurrentFrame();
	
}
