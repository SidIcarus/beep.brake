package edu.rit.se.beepbrake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.widget.TextView;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class MainActivity extends AppCompatActivity {

    //Buffer
    private BufferManager bufMan;

    //Data Acquisition Objects
    private SegmentSync segSync;
    private GPSSensor gpsSen;
    private AccelerometerSensor aSen;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("beepbrake");
    }

    //public native static String changeText();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Initialize() {
        //Buffer init
        bufMan = new BufferManager(this);
        //Data Acquisition init
        segSync = new SegmentSync(bufMan);
        gpsSen = new GPSSensor(this, segSync);
        aSen = new AccelerometerSensor((SensorManager) getSystemService(SENSOR_SERVICE), segSync);
    }

    protected void onResume(){
        super.onResume();

        //Data Acquisition onResume
        segSync.onResume();
        gpsSen.onResume();
        aSen.onResume();
    }

    protected void onPause(){
        super.onPause();

        //Data Acquisition onPause
        segSync.onPause();
        gpsSen.onPause();
        aSen.onPause();
    }
}
