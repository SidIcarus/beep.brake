package edu.rit.se.beepbrake.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by richykapadia on 3/22/16.
 * <p/>
 * Implemented as a lazy singleton since only one instance is needed to listen for wifi
 */
public class WebManager extends BroadcastReceiver {

    private String writeTo, uploadURL;

    // set once instead of allocating on callback
    private ConnectivityManager connectionManager;

    private final String logTag = "System.Web";

    //Singleton implementation
    private WebManager() { }

    private static class LazyInstance {
        private static final WebManager instance = new WebManager();
    }

    public static WebManager getInstance() { return LazyInstance.instance; }

    public static void initWebManager(String writeDir, String uploadURL) {
        getInstance().setUploadURL(uploadURL);
        getInstance().setWriteTo(writeDir);
    }

    private void setUploadURL(String uploadURL) { this.uploadURL = uploadURL; }

    private void setWriteTo(String writeTo) {this.writeTo = writeTo; }

    // Callback function listening to wifi
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(logTag, "onReceive");
        triggerUpload();
    }

    // Set this in the Main Activity before setting the listeners
    public void setConnectionManager(ConnectivityManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public boolean hasWifi() {
        NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();
        return (activeNetwork != null) &&
               (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }

    // TODO: Implement Producer / Consumer pattern here with Upload thread
    // This is the consumer
    public void triggerUpload() {
        if (hasWifi()) {
            Log.d(logTag, "Connection!");
            uploadFiles();
        } else Log.d(logTag, "No Connection!");
    }

    // starts a low priority thread to scan for zip events to upload and sends them to the server
    private void uploadFiles() {
        try {
            URL url = new URL(uploadURL);
            Thread t = new Thread(new UploadThread(url, writeTo));
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
        } catch (IOException e) { Log.e(logTag, e.getMessage()); }
    }
}
