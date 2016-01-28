package main;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import swing.VideoReader;

public class ScaleCarImg {

	//Img Dir
	static final String IMG_READ_DIR = "RK_Car_DB/cropped";
	static final String IMG_WRITE_DIR = "RK_Car_DB/scaled";
    static final Size scaleSize = new Size(24,24);

	static final String OUT_NAME = "img_";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);		
	    
	    File readDir = new File(IMG_READ_DIR);
        //get all the files from a directory
        File[] fList = readDir.listFiles();	
        
        
        for( int i = 0; i < fList.length; i++){
        	//stupid macs
        	if(fList[i].getName().equals(".DS_Store")){
        		continue;
        	}
        	//read img
        	String readFileName = IMG_READ_DIR + "/" + fList[i].getName();
        	String prefix = fList[i].getName().substring(0, fList[i].getName().indexOf('_'));
        	Mat m = Imgcodecs.imread(readFileName);
        	//scale img
        	Mat scaled = new Mat();
        	Imgproc.resize(m, scaled, scaleSize);
        	//save img
        	saveImage(scaled, i, prefix);
        }
	    
	}
	
	private static void saveImage(Mat mat, int i, String prefix){
		if(prefix.equals("img")){
			prefix = "rk";
		}
//		prefix = "neg";
		String filename = IMG_WRITE_DIR + "/" + prefix;
		if( i < 10 ){
			filename += "_000" + i;
		}else if(i < 100){
			filename += "_00" + i;
		}else if( i < 1000){
			filename += "_0" + i;
		}else{
			filename += "_" + i;
		}
		
		filename += ".jpg";
		System.out.println("writing file: " + filename);
    	Imgcodecs.imwrite(filename, mat);
	}

}
