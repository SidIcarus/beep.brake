package edu.rit.se.beepbrake.Web;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;

/**
 * Created by richykapadia on 3/22/16.
 */
public class WebError implements Response.ErrorListener {

    private WebListener webListener;
    private String filename;

    /**
     *
     * @param webListener - used to requeue the upload request
     * @param filename - file name of the requested
     */
    public WebError(WebListener webListener, String filename){
        this.webListener = webListener;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("WebError", error.getMessage());
        //Something messed up our upload, re-queue the upload
        File f = new File(this.filename);
        if( f != null && f.exists() ){
            this.webListener.queueUpload(this.filename);
        }

    }
}
