package edu.rit.se.beepbrake;

/**
 * Created by Bradley on 1/11/2016.
 */
import android.hardware.SensorManager;
import android.location.*;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Bradley on 10/17/2015.
 */
public class GPSSensor implements LocationListener {
    private String provider;
    private SegmentSync segSync;

    private LocationManager locationManager;

    public GPSSensor(Context context, SegmentSync segSync) {
        this.segSync = segSync;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 100, 0, this);
    }

    public void send(Float spd, Double lat, Double lng) {
        HashMap<String, Object> d = new HashMap<String, Object>();

        if (lat != null) {
            d.put("lat", lat);
        }
        if (lng != null) {
            d.put("lng", lng);
        }
        if (spd != null) {
            d.put("spd", spd);
        }

        segSync.UpdateDataAgg(d);

    }

    @Override
    public void onLocationChanged(Location location) {
        Float spd = location.getSpeed();
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        send(spd, lat, lng);

    }

    public void onStatusChanged(String x, int y, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    protected void onResume(){
        locationManager.requestLocationUpdates(provider, 100, 0, this);
    }

    protected void onPause(){
        locationManager.removeUpdates(this);
    }


}
