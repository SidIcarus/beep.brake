package edu.rit.se.beepbrake.Segment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import edu.rit.se.beepbrake.R;

public class SegmentActivity extends AppCompatActivity {

    //Data Acquisition Objects
    private SegmentSync segSync;
    private GPSSensor gpsSen;
    private AccelerometerSensor aSen;
    private TextView accel_x;
    private TextView accel_y;
    private TextView accel_z;
    private TextView gps_lat;
    private TextView gps_lng;
    private TextView gps_spd;

    private final int GREEN = Color.parseColor("#006600");
    private final int ORANGE = Color.parseColor("#FF8000");

    //public native static String changeText();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set up text fields
        accel_x = (TextView) findViewById(R.id.xVal);
        accel_y = (TextView) findViewById(R.id.yVal);
        accel_z = (TextView) findViewById(R.id.zVal);
        gps_lat = (TextView) findViewById(R.id.latVal);
        gps_lng = (TextView) findViewById(R.id.lngVal);
        gps_spd = (TextView) findViewById(R.id.speedVal);

        // set all text value to blank
        accel_x.setText("blank");
        accel_y.setText("blank");
        accel_z.setText("blank");
        gps_lat.setText("blank");
        gps_lng.setText("blank");
        gps_spd.setText("blank");

        Initialize();

        Button b = (Button)findViewById(R.id.sendsegmentbutton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySegmentData(segSync.aggData);
            }
        });
    }

    public synchronized void displaySegmentData(ConcurrentHashMap<String, ArrayList<Object>> data){
        Segment s = segSync.makeSegment();

        updateText(accel_x, Constants.ACCEL_X, s);
        updateText(accel_y, Constants.ACCEL_Y, s);
        updateText(accel_z, Constants.ACCEL_Z, s);
        updateText(gps_lat, Constants.GPS_LAT, s);
        updateText(gps_lng, Constants.GPS_LNG, s);
        updateText(gps_spd, Constants.GPS_SPD, s);
    }

    private void updateText(TextView textView, String key, Segment s){
        if(s.calculatedData.containsKey(key)){
            textView.setText(s.calculatedData.get(key).toString());
            textView.setTextColor(GREEN);
        }else{
            textView.setTextColor(ORANGE);
        }
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
        //Data Acquisition init
        segSync = new SegmentSync();
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
