package edu.rit.se.beepbrake.Web;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by richykapadia on 4/4/16.
 */
public class UploadThread implements Runnable {

    private File file;
    private URL url;

    public UploadThread(File f, URL url){
        this.file = f;
        this.url = url;

    }


    @Override
    public void run() {

        byte[] fileData = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
            fileInputStream.close();
        }catch( IOException e){
            e.printStackTrace();
        }

        // minetype
        String boundary = "apiclient-" + System.currentTimeMillis();
        String mimeType = "multipart/form-data;charset=utf-8;boundary=" + boundary;


        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                    "filename=\"" + file.getName() + "\"\r\n");
            outputStream.writeBytes("Content-Type: application/octet-stream\r\nContent-Length: "
                    + file.length() + "\r\n\r\n");
            outputStream.writeBytes("\r\n");

            // create a buffer of maximum size
            FileInputStream fileInputStream = new FileInputStream(this.file);
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

            Log.d("Web", "File Sent, Response: "+String.valueOf(connection.getResponseCode()));

            InputStream is = connection.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
            String s=b.toString();
            Log.d("Response", s);
            outputStream.close();



        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
