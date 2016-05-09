package edu.rit.se.beepbrake.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import edu.rit.se.beepbrake.R.string;
import edu.rit.se.beepbrake.utils.Preferences;
import edu.rit.se.beepbrake.utils.Utils;

/**
 * @author richykapadia
 * @date 03.22.16
 * Lazy Singleton implementation as only one instance is needed to listen for wifi
 */
public class WebManager extends BroadcastReceiver {

    private String writeDirectory, uploadURL;
    private ConnectivityManager cManager;
    private final String logTag = "System.Web";

    //Singleton implementation
    WebManager() { }

    public String getWriteDirectory() { return writeDirectory; }

    public void setWriteDirectory(String writeDirectory) { this.writeDirectory = writeDirectory; }

    public String getUploadURL() { return uploadURL; }

    public void setUploadURL(String uploadURL) { this.uploadURL = uploadURL; }

    public ConnectivityManager getConnectionManager() { return cManager; }
    // This should only be set once instead being allocated on callback.
    // public void setConnectionManager(ConnectivityManager cManager) { this.cManager = cManager; }

    //--------------------------------------------------------------------------------------------//
    // Lazy instantiation via private static inner class
    private static final class Lazy {
        static final WebManager instance = new WebManager();
        static boolean isRegistered = false;
    }

    public static WebManager getInstance() { return Lazy.instance; }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public static void register(Context ctx, Preferences p) {
        if (!Lazy.isRegistered) {
            Lazy.isRegistered = true;
            String uURLRes = Utils.resToName(ctx.getResources(), string.web_upload_url);

            WebManager instance = getInstance();

            instance.uploadURL = p.getString(uURLRes);
            instance.writeDirectory = Utils.getWritePath(ctx);
            instance.cManager =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            // Listen on intent filter
            IntentFilter connectionIntent =
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            ctx.registerReceiver(instance, connectionIntent);
        }
    }
    //--------------------------------------------------------------------------------------------//

    /** Callback function listening to wifi */
    @Override public void onReceive(Context context, Intent intent) {
        Log.d(logTag, "onReceive");
        triggerUpload();
    }

    public boolean hasWifi() {
        NetworkInfo activeNetwork = getConnectionManager().getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    // TODO: Implement Producer / Consumer pattern here with Upload thread
    // This is the consumer
    public void triggerUpload() {
        if (hasWifi()) {
            Log.d(logTag, "Connection!");
            uploadFiles();
        } else Log.d(logTag, "No Connection!");
    }

    /** starts a low priority thread to scan for zip events to upload & sends them to the server */
    private void uploadFiles() {
        try {
            URL url = new URL(uploadURL);
            Thread t = new Thread(new UploadThread(url, writeDirectory));
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
        } catch (MalformedURLException e) {
            Log.e(logTag, e.getMessage());
        }
    }
}
