package edu.rit.se.beepbrake.Web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
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

    private static WebManager instance;

    // set once instead of allocating on callback
    private ConnectivityManager connectionManager;
    private ArrayList<File> uploadQueue = new ArrayList<>();
    private ReentrantLock qLock = new ReentrantLock();
    private String upload_url = "http://magikarpets.se.rit.edu:3000/api/newFile";

    private WebManager(){}

    public static WebManager getInstance(){
        if(instance == null){
            instance = new WebManager();
        }
        return instance;
    }

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
            qLock.lock();
            Log.d("Web", "Preparing to upload " + this.uploadQueue.size() + " file(s)");
            uploadFiles();
            qLock.unlock();
        }
        else{
            Log.d("Web", "No Connection!");

        }

    }

    public void queueUpload(String filename){
        qLock.lock();
        File f = new File(filename);
        if( f.exists() ) {
            uploadQueue.add(f);
        }
        qLock.unlock();
    }

    public void queueUpload(File file){
        qLock.lock();
        if( file.exists() ) {
            uploadQueue.add(file);
        }
        qLock.unlock();
    }


    private void uploadFiles() {
        try {
            URL url = new URL(upload_url);
            Thread t = new Thread(new UploadThread(uploadQueue, url));
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
        } catch (IOException e) {
            Log.e("Web", e.getMessage());
        }
    }


}
