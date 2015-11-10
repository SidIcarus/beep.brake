package util;

public class VideoReader {
	
	private static VideoCapture vc;
	private static JLabel[] displayImg;
	private static JFrame[] frames;
	private static int frameCount; 
	
	
	/**
	 * Call this first
	 * @param numScreens - num jframes to create
	 */
	public static void initalizeVideoPlayer( int numScreens ){
		
		displayImg = new JLabel[numScreens];
		frames = new JFrame[numScreens];

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
		frames[frameIndex].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frames[frameIndex].setSize(500, 500);
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
		return image;
	}
	
	public static Mat getNextFrame(Size s){
		Mat image = new Mat();
		Mat newImage = new Mat();
		vc.read(image);
		if( image.empty() ){
			return image;
		}
		Imgproc.resize(image, newImage, s);
		frameCount++;
		return newImage;
	}
	
	public static void displayImage(Mat m, int i){
	    //display image in gui
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

}
