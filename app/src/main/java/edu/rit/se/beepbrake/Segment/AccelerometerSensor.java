package edu.rit.se.beepbrake.Segment;

/**
 * Created by Bradley on 1/11/2016.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

import java.util.Date;
import java.util.HashMap;


public class AccelerometerSensor implements SensorEventListener {

    private Context context;
    private SensorManager manager;
    private Sensor Accelerometer;
    private SegmentSync segSync;

    public AccelerometerSensor(Context context, SensorManager manager, SegmentSync segSync){
        this.context = context;
        this.manager = manager;

        if(checkAvailability()) {
            this.Accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.manager.registerListener(this, Accelerometer, manager.SENSOR_DELAY_NORMAL);
            this.segSync = segSync;
        }
    }

    public void send(Float x, Float y, Float z){
        HashMap<String, Object> data = new HashMap<String, Object>();

        if(x != null){
            data.put(Constants.ACCEL_X, x);
        }
        if(y != null){
            data.put(Constants.ACCEL_Y, y);
        }
        if(z != null){
            data.put(Constants.ACCEL_Z, z);
        }

        segSync.UpdateDataAgg(data);
    }

    public void onResume(){
        if(checkAvailability()) {
            manager.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause() {
        if (checkAvailability()) {
            manager.unregisterListener(this);
        }
    }

    public void onSensorChanged(SensorEvent event){

        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];

        send(x, y, z);
    }

    public void onAccuracyChanged(Sensor s, int i) {

    }

    private boolean checkAvailability(){
        PackageManager pm = context.getPackageManager();
        if(pm.hasSystemFeature(pm.FEATURE_SENSOR_ACCELEROMETER)){
            return true;
        }else{
            return false;
        }
    }
}
