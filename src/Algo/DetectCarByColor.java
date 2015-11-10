package Algo;

public class DetectCarByColor {
	//tail lights
	static Scalar sMaxTailLight = new Scalar(134,256, 256);
	static Scalar sMinTailLight = new Scalar(80,175, 210);
	
	//shadow under the car
	static Scalar sMaxShadow = new Scalar(198,256, 85);
	static Scalar sMinShadow = new Scalar(103,149, 0);
	
	public static List<MatOfPoint> carShadow(Mat mat){
		return genericColorDetect( mat, sMinShadow, sMaxShadow );
	}
	
	
	public static List<MatOfPoint> carTailLight(Mat mat){
		return genericColorDetect( mat, sMinTailLight, sMaxTailLight );
	}
	
	
	public static List<MatOfPoint> genericColorDetect(Mat mat, Scalar min, Scalar max){
		//setup
		Mat hierarchy = new Mat();
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	    Mat hsv = new Mat();
		
		Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV);
        //Threshold Matrix
        Mat threshold = new Mat();
        Core.inRange(hsv, min, max, threshold);
        Imgproc.erode(threshold, threshold, new Mat(2,2, CvType.CV_8U));
        Imgproc.dilate(threshold, threshold, new Mat(2,2, CvType.CV_8U));
        //Find car in threshold img
        Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        
        return contours;
	}


	/**
	 * this will probably be expensive 
	 * loop through shadows and look for appropriate ROI (region of intrest) to crop
	 * @param shadows
	 * @param tailLights
	 * @return
	 */
	public static Mat cropToLicensePlate(List<MatOfPoint> shadows, List<MatOfPoint> tailLights) {
		// TODO Auto-generated method stub
		 for(int i=0; i< tailLights.size();i++){
	            //System.out.println(tailLights.get(i));
	        }			
		return null;
	}
}
