package edu.rit.se.beepbrake.Web;

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
 *
 * Implemented as a singleton since only one instance is needed to listen for wifi
 *
 */
public class WebManager extends BroadcastReceiver {

    private static WebManager instance;

    // set once instead of allocating on callback
    private ConnectivityManager connectionManager;
    private String upload_url = "http://magikarpets.se.rit.edu:3000/api/newFile";

    static private String SEGMENT_DIR = Environment.getExternalStorageDirectory() + "/write_segments/";

    //Singleton implementation
    private WebManager(){}

    public static WebManager getInstance(){
        if(instance == null){
            instance = new WebManager();
        }
        return instance;
    }

    /**
     * Set this in the Main Activity before setting the listeners
     * @param connectionManager
     */
    public void setConnectionManager(ConnectivityManager connectionManager){
        this.connectionManager = connectionManager;
    }

    public boolean hasWifi(){
        NetworkInfo activeNetwork = connectionManager.getActiveNetworkInfo();
        boolean wifiConnected = (activeNetwork != null) && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
        return wifiConnected;
    }

    /**
     * Callback function listening to wifi
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Web", "onReceive");

        if( hasWifi() ){
            Log.d("Web", "Connection!");
            uploadFiles();
        }
        else{
            Log.d("Web", "No Connection!");

        }

    }

    /**
     * starts a low priority thread to scan for zip events to upload
     * and sends them to the server
     */
    private void uploadFiles() {
        try {
            URL url = new URL(upload_url);
            Thread t = new Thread(new UploadThread(url, SEGMENT_DIR));
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
        } catch (IOException e) {
            Log.e("Web", e.getMessage());
        }
    }


}
