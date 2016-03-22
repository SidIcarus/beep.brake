package edu.rit.se.beepbrake.Web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by richykapadia on 3/22/16.
 */
public class WebListener extends BroadcastReceiver {

    // set once instead of allocating on callback
    private ConnectivityManager connectionManager;
    // Using a concurrent queue to be safe
    private Queue<String> uploadQueue = new ConcurrentLinkedQueue<>();

    public WebListener( ConnectivityManager connectionManager){
        this.connectionManager = connectionManager;
    }

    /**
     * Callback function listening to wifi
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Web", "onReceive");
        NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();
        boolean wifiConnected = (activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);

        if( wifiConnected ){
            Log.d("Web", "Connection!");
        }
        else{
            Log.d("Web", "No Connection!");
        }

    }

    public void queueUpload(String filename){
        uploadQueue.add(filename);
    }
}
