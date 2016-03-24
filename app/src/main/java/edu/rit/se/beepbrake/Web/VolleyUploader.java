package edu.rit.se.beepbrake.Web;

import android.content.Context;

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
    private static Context context;
    private static TrustManager trustManager;

    private VolleyUploader(Context context) {
        this.context = context;
    }

    public static synchronized VolleyUploader getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyUploader(context);
        }
        return instance;
    }

    /**
     * The broadcast listener can be called by any app
     * therefore it's safer to uses the app context as the key
     *
     * @param context - the "key" to get the corresponding queue
     * @return
     */
    public RequestQueue getRequestQueue(Context context){
        return Volley.newRequestQueue(context.getApplicationContext());
    }

    public void addToRequestQueue(Upload upload){
        getRequestQueue(context).add(upload);
    }

    private void getTrustManager(){
        TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

}
