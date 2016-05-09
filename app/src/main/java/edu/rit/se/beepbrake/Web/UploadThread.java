package edu.rit.se.beepbrake.web;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import edu.rit.se.beepbrake.utils.Utils;

// Created by richykapadia on 4/4/16.
public class UploadThread implements Runnable {

    private final URL url;
    private final String eventDir;

    public UploadThread(URL url, String eventDir) {
        this.url = url;
        this.eventDir = eventDir;
    }

    @Override public void run() {
        //scan for files in event dir
        ArrayList<File> fileList = new ArrayList<>();
        File uploadDir = new File(eventDir);
        if (!uploadDir.isDirectory()) return;

        //TODO: Call prefs
        //Set<String> paths = Preferences.get(ctx, "upload_paths", new
        // HashSet<String>());

        // TODO: Comment this out once context is passed in above
        // write segment (dir) --> timestamp (dir) --> event zip (file)
        for (File file : uploadDir.listFiles()) {
            if (file.isDirectory()) {
                for (File event : file.listFiles()) {
                    //zips should be here
                    if ("zip".equals(Utils.getFileExtension(event))) fileList.add(event);
                }
            }
        }

        // TODO: Change this for-loop to iterate through the Set<String> paths instead of the
        // current shenanigans

        //double check wifi connection before starting upload
        if (WebManager.getInstance().hasWifi()) {
            for (File file : fileList) uploadFile(file);
        }
    }

    /**
     * multipart post with the zip file (event) in the "file" part
     * expects 204 back - delete event off the phone
     * otherwise - re-queue upload on next wifi connection
     *
     * @param file - zip file to upload
     */
    private void uploadFile(File file) {
        if (!isValidZip(file)) {
            file.delete();
            return;
        }

        HttpURLConnection connection = null;

        try {
            // mimeType
            String boundary = "apiclient-" + System.currentTimeMillis();
            String mimeType = "multipart/form-data;charset=utf-8;boundary=" + boundary;

            String strOUT = String.format(
                "-- %s \r\nContent-Disposition:form-data; name=\"file\";filename=\" %s " +
                "\"\r\nContent-Type: application/octet-stream\r\nContent-Length: %d \r\n\r\n",
                boundary, file.getName(), file.length());


            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", mimeType);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");

            Log.d("Web", "File Sent");
            int responseCode = connection.getResponseCode();
            Log.d("Web", "Response: " + responseCode);

            try (DataOutputStream dataOS = new DataOutputStream(connection.getOutputStream());
                 FileInputStream fileIS = new FileInputStream(file)) {

                dataOS.writeBytes(strOUT);

                // create a buffer of maximum size

                int bytesAvailable = fileIS.available();

                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileIS.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dataOS.write(buffer, 0, bufferSize);
                    bytesAvailable = fileIS.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileIS.read(buffer, 0, bufferSize);
                }
                dataOS.writeBytes("\r\n--" + boundary + "--\r\n");
                dataOS.flush();
                dataOS.close();
            }

            boolean is200to299 = responseCode >= HttpURLConnection.HTTP_OK &&
                                 responseCode < HttpURLConnection.HTTP_MULT_CHOICE;

            if (is200to299) {
                // remove file from dir
                // the parent file should be the timestamp dir
                File parent = file.getParentFile();
                file.delete();
                parent.delete();
            }

            // TODO: Add actual debugging statement
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("System.Web.Upload", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("System.Web.Upload", e.getMessage());
            e.printStackTrace();
        } finally { if (connection != null) connection.disconnect(); }
    }

    private boolean isValidZip(File file) {
        try (ZipFile zFile = new ZipFile(file);
             ZipInputStream zIS = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zEntry = zIS.getNextEntry();

            if (zEntry == null) return false;

            while (zEntry != null) {
                // if(throws exception fetching any of the following)file.is(CORRUPTED)
                //noinspection resource
                zFile.getInputStream(zEntry);
                zEntry.getCrc();
                zEntry.getCompressedSize();
                zEntry.getName();
                zEntry = zIS.getNextEntry();
            }
            return true;
        }
        // TODO: Add actual debugging statement
        //@formatter:off
        catch (FileNotFoundException ignored)   { return false; }
        catch (ZipException ignored)            { return false; }
        catch (IOException ignored)             { return false; }
        //@formatter:on
    }
}
