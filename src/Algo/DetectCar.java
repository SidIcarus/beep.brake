package Algo;

public class DetectCar {

	static CascadeClassifier mCascadeClassifier;
	static Scalar color = new Scalar(0, 250, 0);
	
	public static void initializeHaar(String cascadePath){

        mCascadeClassifier = new CascadeClassifier();
        mCascadeClassifier.load(cascadePath);
        if (mCascadeClassifier.empty()) {
            System.out.println("Failed to load cascade classifier");
            mCascadeClassifier = null;
        } else{
            System.out.println("Loaded cascade classifier from " + mCascadeClassifier.toString());
        }
    }

    public static MatOfRect haar( Mat mat){
        MatOfRect foundLocations = new MatOfRect();
        mCascadeClassifier.detectMultiScale(mat, foundLocations, 1.2, 100, 0, new Size(32, 32), new Size(400,400));
        
        return foundLocations;
    }

}
