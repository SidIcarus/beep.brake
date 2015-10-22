package myfirstapp.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


public class AccelerometerSensor implements SensorInterface, SensorEventListener {

  private float x, y, z;
  private SensorManager manager
  private Sensor Accelerometer

  public AccelerometerSensor (SensorManager manager){
    this.manager = manager;
    this.Accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    this.manager.registerListener(this, Accelerometer, Manager.SENSOR_DELAY_NORMAL);
  }

  @Override
  public void send(){

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
    x = event.values[0];
    y = event.values[1];
    z = event.values[2];
  }

}
