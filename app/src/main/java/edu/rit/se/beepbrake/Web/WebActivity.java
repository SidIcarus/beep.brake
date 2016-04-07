package edu.rit.se.beepbrake.Web;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.rit.se.beepbrake.R;

/**
 * testing wifi listener and upload threads
 */
public class WebActivity extends AppCompatActivity {

    //Kevin's
    static private String UPLOAD_1 = Environment.getExternalStorageDirectory() +
            "/write_segments/17958013dc1ff915_1459805565287.zip" ;

    static private String UPLOAD_2 = Environment.getExternalStorageDirectory() +
            "/write_segments/1458250230609/e9bb32c0c705a93d_1458250230609.zip" ;

    static private String UPLOAD_3 = Environment.getExternalStorageDirectory() +
            "/write_segments/1458250162024/e9bb32c0c705a93d_1458250162024.zip";

    static private String UPLOAD_4 = Environment.getExternalStorageDirectory() +
            "/write_segments/1458250357530/e9bb32c0c705a93d_1458250357530.zip";

    static private String UPLOAD_5 = Environment.getExternalStorageDirectory() +
            "/write_segments/1458250388619/e9bb32c0c705a93d_1458250388619.zip";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //wifi listener
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        WebManager webManager = WebManager.getInstance();
        webManager.setConnectionManager(connectionManager);

        //try to push test file
//        webManager.queueUpload(UPLOAD_2);
//        webManager.queueUpload(UPLOAD_3);
//        webManager.queueUpload(UPLOAD_4);
//        webManager.queueUpload(UPLOAD_5);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(webManager, filter);

    }

}
