package myfirstapp.app;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by Bradley on 10/17/2015.
 */
public class GPSSensor implements SensorInterface, LocationListener {

    private double lat;
    private double lng;
    private double speed;
    private String provider;
    private TextView longitudeField;

    private LocationManager locationManager;

    public GPSSensor(Context context, View x) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        /////
        List<String> prov = locationManager.getProviders(false);
        Log.d("Bird", String.valueOf(prov.size()));

        provider = locationManager.getBestProvider(criteria, true);
        Log.d("Bird", provider);
    }

    @Override
    public void send() {

    }

    @Override
    public void onLocationChanged(Location location) {

        speed = location.getSpeed();
        lat = location.getLatitude();
        lng = location.getLongitude();


        longitudeField.setText(String.valueOf(lng));
        Log.d("GPSSensor", String.valueOf(lat));

        System.out.println(lat);
        System.out.println(lng);
        System.out.println(speed);

    }

    public void onStatusChanged(String x, int y, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
