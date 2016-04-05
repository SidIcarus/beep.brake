package edu.rit.se.beepbrake.Web;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by richykapadia on 3/23/16.
 *
 * Singleton to queue up files to upload
 */
public class VolleyUploader {
    private static VolleyUploader instance;
    private RequestQueue mRequestQueue;

    private VolleyUploader(){
        mRequestQueue = Volley.newRequestQueue(MyApp.getAppContext());
    }


    public static synchronized VolleyUploader getInstance() {
        if (instance == null) {
            instance = new VolleyUploader();
        }
        return instance;
    }

    /**
     * The broadcast listener can be called by any app
     * therefore it's safer to uses the app context as the key
     *
     * @return
     */
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }


}
