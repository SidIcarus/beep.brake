package edu.rit.se.beepbrake;

/**
 * Created by Bradley on 1/11/2016.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;


public class AccelerometerSensor implements SensorEventListener {

    private SensorManager manager;
    private Sensor Accelerometer;
    private SegmentSync segSync;
    private Long d;

    public AccelerometerSensor(SensorManager manager, SegmentSync segSync){
        this.manager = manager;
        this.Accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.manager.registerListener(this, Accelerometer, manager.SENSOR_DELAY_NORMAL);
        this.segSync = segSync;
        d = new Date().getTime();
    }

    public void send(Float x, Float y, Float z){
        HashMap<String, Object> data = new HashMap<String, Object>();

        if(x != null){
            data.put("XAcl", x);
        }
        if(y != null){
            data.put("YAcl", y);
        }
        if(z != null){
            data.put("ZAcl", z);
        }

        segSync.UpdateDataAgg(data);
    }
    /*  Activity will need to pause and resume this on it's super.onResume/super.onPause

      @Override
      protected void onResume(){
        super.onResume();
        manager.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
      }

      protected void onPause(){
        super.onPause();
        manager.unregisterListener(this);
      }
    */
    public void onSensorChanged(SensorEvent event){

        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];

        Log.d("Bird", String.valueOf((d - new Date().getTime())));
        d = new Date().getTime();
        Log.d("Bird", String.valueOf(x));
        Log.d("Bird", y.toString());
        Log.d("Bird", z.toString());


        send(x, y, z);
    }

    public void onAccuracyChanged(Sensor s, int i) {

    }
}
