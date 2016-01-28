package swing;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import util.FrameSource;
import util.RegionSelector;

public class VideoReader {
	
	private static VideoCapture vc;
	private static JLabel[] displayImg;
	private static Mat[] currMat;
	private static JFrame[] frames;
	private static RegionSelector[] selectors;
	private static int frameCount; 
	
	
	/**
	 * Call this first
	 * @param numScreens - num jframes to create
	 */
	public static void initalizeVideoPlayer( int numScreens ){
		
		displayImg = new JLabel[numScreens];
		frames = new JFrame[numScreens];
		selectors = new RegionSelector[numScreens];
		currMat = new Mat[numScreens];

		for(int i = 0; i < numScreens; i++){
			createJFrame("Frame " + i, i);
		}
	}
	
	
	/**
	 * 
	 * @param names - names of the frames;
	 */
	public static void initalizeVideoPlayer( String[] names ){
		
		int numScreens = names.length;
		displayImg = new JLabel[numScreens];
		frames = new JFrame[numScreens];
		selectors = new RegionSelector[numScreens];
		currMat = new Mat[numScreens];

		for(int i = 0; i < numScreens; i++){
			String n = names[i];
			createJFrame(n, i);
		}
	}
	
	/**
	 * 
	 * @param name - Name of the JFrame
	 * @param frameIndex - Index of the frame
	 */
	private static void createJFrame(String name, int frameIndex){
		//UI
		displayImg[frameIndex] = new JLabel();

		frames[frameIndex] = new JFrame(name);
		frames[frameIndex].setSize(500, 500);
		FlowLayout layout = new FlowLayout();
		layout.setHgap(100);
		layout.setVgap(100);
		frames[frameIndex].setLayout( layout );
		frames[frameIndex].add(displayImg[frameIndex]);
		frames[frameIndex].setVisible(true);
		frames[frameIndex].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Start capturing videos from a source
	 * @param videoPath - path to video i.e. Video/Vid0001.mp4;
	 */
	public static void initalizeVideoReader(String videoPath) throws IOException{
		//grab video
	    vc = new VideoCapture(videoPath);
	    if(vc.isOpened()){
	    	System.out.println("Opened!");
	    }else{
	    	throw new IOException("File could not be found!");
	    }
	}
	
	public static Mat getNextFrame(){
		Mat image = new Mat();
		vc.read(image);
		frameCount++;
		for(int i = 0; i < currMat.length; i++){
			currMat[i] = image;
		}
		return image;
	}
	
	public static Mat getNextFrame(Size s){
		Mat image = getNextFrame();
		Mat newImage = new Mat();
		if( image.empty() ){
			return image;
		}
		Imgproc.resize(image, newImage, s);
		for(int i = 0; i < currMat.length; i++){
			currMat[i] = newImage;
		}
		return newImage;
	}
	
	public static void displayImage(Mat m, int i){
	    //display image in gui
		if(m == null || m.empty()){
			return;
		}
	    BufferedImage bImage = Mat2BufferedImage(m);
	    displayImage(bImage, i);
	}
	
	
	private static BufferedImage Mat2BufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;

	}
	
	/**
	 * 
	 * @param img2 - Image to display
	 * @param i - index of frame
	 */
	private static void displayImage(Image img2, int i) {
		if( displayImg.length < i ){
			return;
		}
		ImageIcon icon = new ImageIcon(img2);
		displayImg[i].setIcon(icon);
	}
	
	public static int getFrameCount(){
		return frameCount;
	}
	
	public static Mat getCurrMat(int index){
		return currMat[index];
	}
	
	public static void enableRegionSelector(int frameIndex, FrameSource source){
		if( 0 <= frameIndex && frameIndex < selectors.length){
			selectors[frameIndex] = new RegionSelector(frameIndex, source);
			displayImg[frameIndex].addMouseMotionListener(selectors[frameIndex]);
			displayImg[frameIndex].addMouseListener(selectors[frameIndex]);
		}
	}
	
	public static RegionSelector getRegionSelector(int frameIndex){
		return selectors[frameIndex];
	}

}
