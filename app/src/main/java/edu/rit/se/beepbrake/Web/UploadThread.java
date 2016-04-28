package edu.rit.se.beepbrake.Web;

import android.content.AsyncTaskLoader;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import edu.rit.se.beepbrake.buffer.DiskWriter;

/**
 * Created by richykapadia on 4/4/16.
 */
public class UploadThread implements Runnable {

    private final URL url;
    private final String eventDir;

    public UploadThread(URL url, String eventDir){
        this.url = url;
        this.eventDir = eventDir;
    }


    @Override
    public void run() {
        //scan for files in event dir
        ArrayList<File> fileList = new ArrayList<File>();
        File uploadDir = new File(eventDir);
        if(!uploadDir.isDirectory()){ return; }
        // write segment (dir) --> timestamp (dir) --> event zip (file)
        for( File ts : uploadDir.listFiles() ){
            if( ts.isDirectory() ){
                for( File event : ts.listFiles()){
                    //zips should be here
                    if( "zip".equals(getFileExtension(event))){
                        fileList.add(event);
                    }
                }
            }
        }


        for(File f : fileList ){
            //double check wifi connection before starting upload
            if( WebManager.getInstance().hasWifi()) {
                uploadFile(f);
            }
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }


    /**
     * multipart post with the zip file (event) in the "file" part
     * expects 204 back - delete event off the phone
     * otherwise - re-queue upload on next wifi connection
     * @param f - zip file to upload
     */
    private void uploadFile(File f){

        if(!isValid(f)){
            f.delete();
            return;
        }

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

            if (200 <= code && code <= 299) {
                // remove file from dir
                // the parent file should be the timestamp dir
                File parent = f.getParentFile();
                f.delete();
                parent.delete();
            }
        }catch (IOException e){
            Log.e("Web", e.getMessage());
        }catch (Exception e){
            Log.e("Web", e.getMessage());
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    private boolean isValid(File file) {
        ZipFile zipfile = null;
        ZipInputStream zis = null;
        try {
            zipfile = new ZipFile(file);
            zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze = zis.getNextEntry();
            if(ze == null) {
                return false;
            }
            while(ze != null) {
                // if it throws an exception fetching any of the following then we know the file is corrupted.
                zipfile.getInputStream(ze);
                ze.getCrc();
                ze.getCompressedSize();
                ze.getName();
                ze = zis.getNextEntry();
            }
            return true;
        } catch (ZipException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (zipfile != null) {
                    zipfile.close();
                    zipfile = null;
                }
            } catch (IOException e) {
                return false;
            } try {
                if (zis != null) {
                    zis.close();
                    zis = null;
                }
            } catch (IOException e) {
                return false;
            }

        }
    }

}
