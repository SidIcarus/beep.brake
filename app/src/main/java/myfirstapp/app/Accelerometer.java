package myfirstapp.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


public class AccelerometerSensor implements SensorInterface, SensorEventListener {

  private SensorManager manager
  private Sensor Accelerometer

  public AccelerometerSensor (SensorManager manager){
    this.manager = manager;
    this.Accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    this.manager.registerListener(this, Accelerometer, Manager.SENSOR_DELAY_NORMAL);
  }

  @Override
  public void send(float x, float y, float z){
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

    //Send AggData
  }

  protected void onResume(){
    super.onResume();

    manager.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
  }

  protected void onPause(){
    super.onPause();
    manager.unregisterListener(this);
  }

  public void onSensorChanged(SensorEvent event){
    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    send(x, y, z);
  }

}
