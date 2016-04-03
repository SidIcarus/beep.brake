package edu.rit.se.beepbrake.Web;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.util.Map;


public class Upload extends Request<String> {

    private Response.Listener success;


    public Upload(int method, String url, Response.Listener success, Response.ErrorListener error){
        super(method, url, error);
        this.success = success;
    }

    public Upload(String url, Map<String, String> header, String mimeType, FileBody zip, Response.Listener success, Response.ErrorListener error){
        super(Method.POST, url, error);
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntity.addPart("file", zip);

    }



    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(String response) {

    }

}