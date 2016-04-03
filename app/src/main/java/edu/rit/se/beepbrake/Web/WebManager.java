package edu.rit.se.beepbrake.Web;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private String upload_url = "http://magikarpets.se.rit.edu:3000/web/api/newFile";


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
                uploadFile( path, context );
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


    private byte[] createMultipart(String path){
        File f = new File(path);
        byte[] bFile = new byte[(int) f.length()];

        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }catch (Exception e){
            Log.e("WebManager", e.getMessage());
        }

        return bFile;
    }

    private void uploadFile(String path, Context context) {
        // file to upload
        File file = new File(path);
        Map<String, File> fileParts = new HashMap<>();
        fileParts.put("file", file);

        // error handler
        WebError errorlistener = new WebError(this, path);

        // headers
        Map<String, String> headers = new HashMap<>();

        // minetype
        String boundary = "apiclient-" + System.currentTimeMillis();
        String mimeType = "multipart/form-data;charset=utf-8;boundary=" + boundary;
        //network reponse
        Response.Listener<NetworkResponse> networkReponse = new Response.Listener<NetworkResponse>(){
            @Override
            public void onResponse(NetworkResponse response) {
                Log.d("WebManager", "Response: " + response.statusCode);
            }
        };

        Log.d("Web", "created upload object for " + path);
        MultipartRequest request = new MultipartRequest(upload_url, headers, mimeType, fileParts, networkReponse, errorlistener);

        VolleyUploader.getInstance(context).addToRequestQueue(request);


    }


}
