package edu.rit.se.beepbrake.Web;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by richykapadia on 4/4/16.
 */
public class UploadThread implements Runnable {

    private List<File> fileList;
    private URL url;

    public UploadThread(List<File> fList, URL url){
        this.fileList = fList;
        this.url = url;

    }


    @Override
    public void run() {
        for(File f : this.fileList ){
            if( WebManager.getInstance().hasWifi() ) {
                uploadFile(f);
            }
        }
    }


    private void uploadFile(File f){
        // minetype
        String boundary = "apiclient-" + System.currentTimeMillis();
        String mimeType = "multipart/form-data;charset=utf-8;boundary=" + boundary;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", mimeType);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            outputStream.writeBytes("--" + boundary + "\r\n");

            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";" +
                    "filename=\"" + f.getName() + "\"\r\n");
            outputStream.writeBytes("Content-Type: application/octet-stream\r\nContent-Length: "
                    + f.length() + "\r\n\r\n");

            // create a buffer of maximum size
            FileInputStream fileInputStream = new FileInputStream(f);
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[ ] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0,bufferSize);
            }
            outputStream.writeBytes("\r\n");
            outputStream.writeBytes("--" + boundary + "--\r\n");

            // close streams
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();


            Log.d("Web", "File Sent");
            int code = connection.getResponseCode();
            Log.d("Web", "Response: " + code);

            connection.disconnect();
            connection = null;

            if (200 <= code && code <= 299){
                // remove file from dir
                Log.d("Web", "Here is where i would delete the file");
                //f.delete();
            }else{
                //re-queue event upload
                WebManager.getInstance().queueUpload(f);
            }

        }catch (IOException e){
            Log.e("Web", e.getMessage());
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }
}
