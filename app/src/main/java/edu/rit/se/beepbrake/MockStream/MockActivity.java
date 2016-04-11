package edu.rit.se.beepbrake.MockStream;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import edu.rit.se.beepbrake.Analysis.LoaderCallback;
import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.Segment.Constants;
import edu.rit.se.beepbrake.Segment.Segment;

public class MockActivity extends AppCompatActivity {

    //load opencv
    static{ System.loadLibrary("opencv_java3"); }

    private BaseLoaderCallback mLoaderCallback;
    private JavaCameraView mCameraView;

    private final String STREAM_DIR = "/mock_stream/mock_0001/";
    private final String JSONFILE = "stream_data.json";

    private final String PATH = Environment.getExternalStorageDirectory() + STREAM_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);

        //initialize mock stream
        Segment firstSeg = initializeMockStream();

        // UI Element
        mCameraView = (JavaCameraView) findViewById(R.id.MockCameraPreview);
        mCameraView.setVisibility(SurfaceView.VISIBLE);

        //Set listener and callback
        MockCameraPreview mockCameraPreview = new MockCameraPreview(firstSeg);
        mCameraView.setCvCameraViewListener(mockCameraPreview);
        mLoaderCallback = new LoaderCallback(this, mCameraView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }

   private Segment initializeMockStream(){

       ArrayList<Segment> segments = getSegmentDataFromJsonFile();

       // grab files from dir
       ArrayList mockStream = new ArrayList<Mat>();
       File file = new File(PATH);
       if( file.isDirectory()){
           for( int i = 0; i < file.listFiles().length; i++){
               File currentFile = file.listFiles()[i];
               if(currentFile.toString().contains(".png")){
                   Mat m = Imgcodecs.imread( currentFile.toString() );
                   mockStream.add(m);

                   if(i < segments.size()) {
                       segments.get(i).addDataObject(Constants.FRAME, m);
                   }

               }
           }
       }

       // link all segments together
       if( segments.size() > 1) {
           Segment curr = segments.get(0);
           for(int i = 1; i < segments.size(); i++){
               Segment next = segments.get(i);
               curr.setNextSeg(next);
               next.setPrevSeg(curr);
               curr = next;
           }
           // link last
           Segment first = segments.get(0);
           Segment last = segments.get(segments.size() - 1);
           last.setNextSeg(first);
           first.setPrevSeg(last);
       }

        return segments.get(0);
   }

    private ArrayList<Segment> getSegmentDataFromJsonFile(){
        // read json file
        String jString = "";
        try{
            File jsonFile = new File(PATH + JSONFILE);
            FileInputStream stream = new FileInputStream(jsonFile);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            stream.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        ArrayList<Segment> results = new ArrayList<>();

        try{
            if( !jString.isEmpty() ) {
                JSONObject streamData = new JSONObject(jString);
                JSONArray segmentArray = streamData.getJSONArray(Constants.MOCK_SEGMENT_JSON);
                // for segment in json
                for( int i = 0; i < segmentArray.length(); i++) {
                    JSONObject currJsonSeg = (JSONObject) segmentArray.get(i);
                    ConcurrentHashMap<String, Object> mapData = new ConcurrentHashMap<>();

                    JSONArray positionsFound = currJsonSeg.getJSONArray(Constants.CAR_POSITIONS);
                    //get all the rectangles
                    Rect[] rects = new Rect[positionsFound.length()];
                    for( int j = 0; j < positionsFound.length(); j++ ){
                        JSONObject pos = (JSONObject) positionsFound.get(j);
                        double x1 = pos.getDouble(Constants.CAR_POS_X1_JSON);
                        double y1 = pos.getDouble(Constants.CAR_POS_Y1_JSON);
                        double x2 = pos.getDouble(Constants.CAR_POS_X2_JSON);
                        double y2 = pos.getDouble(Constants.CAR_POS_Y2_JSON);
                        Point br = new Point(x1,y1);
                        Point tl = new Point(x2,y2);
                        rects[j] = new Rect(br, tl);
                    }
                    mapData.put(Constants.CAR_POSITIONS, rects);

                    // parse and put sensor data
                    double accel_x = currJsonSeg.getDouble(Constants.ACCEL_X);
                    mapData.put(Constants.ACCEL_X, accel_x);

                    double accel_y = currJsonSeg.getDouble(Constants.ACCEL_Y);
                    mapData.put(Constants.ACCEL_Y, accel_y);

                    double accel_z = currJsonSeg.getDouble(Constants.ACCEL_Z);
                    mapData.put(Constants.ACCEL_Z, accel_z);

                    double gps_lat = currJsonSeg.getDouble(Constants.GPS_LAT);
                    mapData.put(Constants.GPS_LAT, gps_lat);

                    double gps_lng = currJsonSeg.getDouble(Constants.GPS_LNG);
                    mapData.put(Constants.GPS_LNG, gps_lng);

                    double gps_spd = currJsonSeg.getDouble(Constants.GPS_SPD);
                    mapData.put(Constants.GPS_SPD, gps_spd);

                    Segment s = new Segment(mapData, null);
                    results.add(s);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return results;

    }


}
