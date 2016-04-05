package edu.rit.se.beepbrake.Web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by richykapadia on 3/22/16.
 *
 * Waits for the wifi to connect then
 * runs through the queue of files and
 * packages them into multipart requests
 */
public class WebManager extends BroadcastReceiver {

    // set once instead of allocating on callback
    private ConnectivityManager connectionManager;

    private ArrayList<String> uploadQueue = new ArrayList<String>();
    private ReentrantLock qLock = new ReentrantLock();
    private String upload_url = "http://magikarpets.se.rit.edu:3000/api/newFile";


    public WebManager(ConnectivityManager connectionManager){
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
            qLock.lock();
            Log.d("Web", "Preparing to upload " + this.uploadQueue.size() + " file(s)");
            for( String path : this.uploadQueue ){
                uploadFile(path);
            }
            qLock.unlock();
        }
        else{
            Log.d("Web", "No Connection!");
        }

    }

    public void queueUpload(String filename){
        qLock.lock();
        uploadQueue.add(filename);
        qLock.unlock();
    }


    private void uploadFile(String path) {
        try{
            File file = new File(path);
            URL url = new URL(upload_url);
            (new Thread(new UploadThread(file, url))).start();
        }catch (IOException e){
            Log.e("Web", e.getMessage());
        }


    }


}
