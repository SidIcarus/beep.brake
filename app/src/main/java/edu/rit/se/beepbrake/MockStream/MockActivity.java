package edu.rit.se.beepbrake.MockStream;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;

import edu.rit.se.beepbrake.Analysis.LoaderCallback;
import edu.rit.se.beepbrake.R;

public class MockActivity extends AppCompatActivity {

    //load opencv
    static{ System.loadLibrary("opencv_java3"); }

    private BaseLoaderCallback mLoaderCallback;
    private JavaCameraView mCameraView;

    private final String PATH = Environment.getExternalStorageDirectory() + "/mock_stream/mock_0001/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);

        //initialize mock stream
        ArrayList<Mat> tempMatStream = initializeMockStream();

        // UI Element
        mCameraView = (JavaCameraView) findViewById(R.id.MockCameraPreview);
        mCameraView.setVisibility(SurfaceView.VISIBLE);

        //Set listener and callback
        MockCameraPreview mockCameraPreview = new MockCameraPreview(tempMatStream);
        mCameraView.setCvCameraViewListener(mockCameraPreview);
        mLoaderCallback = new LoaderCallback(this, mCameraView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }

   private ArrayList<Mat> initializeMockStream(){
       // grab files from dir
       ArrayList mockStream = new ArrayList<Mat>();
       File file = new File(PATH);
       if( file.isDirectory()){
           for( int i = 0; i < file.listFiles().length; i++){
               Mat m = Imgcodecs.imread( file.listFiles()[i].toString() );
               mockStream.add(m);
           }
       }

        return mockStream;
   }


}
