package edu.rit.se.beepbrake;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import edu.rit.se.beepbrake.Web.WebManager;

// Created by richykapadia on 4/11/16.
public class BeepBrake extends Application {

    //Web manager
    private WebManager webMan;
    private IntentFilter connectionIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        // wifi listener
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        webMan = WebManager.getInstance();
        webMan.setConnectionManager(connectionManager);

        // Listen on intent filter
        connectionIntent = new IntentFilter();
        connectionIntent.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(webMan, connectionIntent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        TempLogger.printLogs();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        TempLogger.printLogs();
        Log.d("Beep.Brake", "Low Memory");
    }
}
