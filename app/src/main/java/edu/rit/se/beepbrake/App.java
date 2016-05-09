package edu.rit.se.beepbrake;

import android.app.Application;
import android.util.Log;

import edu.rit.se.beepbrake.utils.TempLogger;

// Created by richykapadia on 4/11/16.
public class App extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        TempLogger.printLogs();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        TempLogger.printLogs();
        Log.d(getString(R.string.app_name), "Low Memory");
    }
}
