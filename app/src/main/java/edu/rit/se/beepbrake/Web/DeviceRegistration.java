package edu.rit.se.beepbrake.Web;

import android.provider.Settings;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by richykapadia on 4/23/16.
 */
public class DeviceRegistration implements Runnable {

    static private final String TAG = "DEVICE-REG";

    private String deviceId;
    private URL url;

    public DeviceRegistration(URL url, String deviceId){
        this.url = url;
        this.deviceId = deviceId;
    }

    @Override
    public void run() {

        String boundary = "apiclient-" + System.currentTimeMillis();
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");

            String body = "{\"deviceid\": \"" + deviceId + "\"}";
            connection.setRequestProperty( "Content-Length", Integer.toString(body.length()));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body);
            outputStream.flush();
            outputStream.close();


            Log.d("Web", "File Sent");
            int code = connection.getResponseCode();
            Log.d("Web", "Response: " + code);

            connection.disconnect();
            connection = null;

            if (200 <= code && code <= 299) {
                //TODO: Set shared pref


            }

        }catch (IOException e){
            Log.e(TAG, e.getMessage());
        }


    }
}
