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

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");

            String body = "{\"deviceid\": \"" + deviceId + "\"}";
            connection.setRequestProperty( "Content-Length", Integer.toString(body.getBytes().length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body);
            outputStream.flush();
            outputStream.close();


            Log.d("Web", "Device Reg Sent");
            int code = connection.getResponseCode();
            Log.d("Web", "Response: " + code);

            connection.disconnect();
            connection = null;

            //TODO: Set shared pref
            if (200 <= code && code <= 299) {
                //TODO set shared pref to upload successful

            }else if( 409 == code){
                //Duplicate exists on the server meaning device is already registered
                //TODO set shared pref to upload successful

            }else{
                //TODO set shared pref to upload is NOT successful

            }



        }catch (IOException e){
            Log.e(TAG, e.getMessage());
        }


    }
}
