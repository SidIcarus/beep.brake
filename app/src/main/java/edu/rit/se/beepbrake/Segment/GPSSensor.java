package edu.rit.se.beepbrake.segment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import edu.rit.se.beepbrake.constants.SegmentConstants;

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

    private boolean checkPermission() {
        PackageManager pm = context.getPackageManager();
        int hasPermission = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName());
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (hasPermission == PackageManager.PERMISSION_DENIED) {
            //TODO warn the user GPS doesn't work

            return false;
        } else return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Float spd = location.getSpeed();
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        send(spd, lat, lng);
    }

    public void onPause() { if (checkPermission()) locationManager.removeUpdates(this); }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    public void onResume() {
        if (checkPermission()) locationManager.requestLocationUpdates(provider, 100, 0, this);
    }

    public void onStatusChanged(String x, int y, Bundle bundle) { }

    public void send(Float spd, Double lat, Double lng) {
        HashMap<String, Object> d = new HashMap<String, Object>();

        if (lat != null) d.put(SegmentConstants.GPS_LAT, lat);
        if (lng != null) d.put(SegmentConstants.GPS_LNG, lng);
        if (spd != null) d.put(SegmentConstants.GPS_SPD, spd);

        segSync.UpdateDataAgg(d);
    }
}
