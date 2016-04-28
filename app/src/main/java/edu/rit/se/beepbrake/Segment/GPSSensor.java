package edu.rit.se.beepbrake.segment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.HashMap;

// Created by Bradley on 10/17/2015 or on 1/11/2016.
public class GPSSensor implements LocationListener {
    private String provider;
    private SegmentSync segSync;
    private LocationManager locationManager;
    private Context context;

    public GPSSensor(Context context, SegmentSync segSync) {
        this.segSync = segSync;
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        provider = locationManager.getBestProvider(criteria, true);
        if (checkPermission()) locationManager.requestLocationUpdates(provider, 100, 0, this);
    }

    public void send(Float spd, Double lat, Double lng) {
        HashMap<String, Object> d = new HashMap<String, Object>();

        if (lat != null) d.put(Constants.GPS_LAT, lat);
        if (lng != null) d.put(Constants.GPS_LNG, lng);
        if (spd != null) d.put(Constants.GPS_SPD, spd);

        segSync.UpdateDataAgg(d);
    }

    @Override
    public void onLocationChanged(Location location) {
        Float spd = location.getSpeed();
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        send(spd, lat, lng);
    }

    public void onStatusChanged(String x, int y, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }

    public void onResume() {
        if (checkPermission()) locationManager.requestLocationUpdates(provider, 100, 0, this);
    }

    public void onPause() { if (checkPermission()) locationManager.removeUpdates(this); }

    private boolean checkPermission() {
        PackageManager pm = context.getPackageManager();
        int hasPermission = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName());
        if (hasPermission == pm.PERMISSION_GRANTED) {
            return true;
        } else if (hasPermission == pm.PERMISSION_DENIED) {
            //TODO warn the user GPS doesn't work

            return false;
        } else return false;
    }
}
