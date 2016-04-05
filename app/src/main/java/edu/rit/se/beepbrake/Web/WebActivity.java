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

    static private String UPLOAD_1 = Environment.getExternalStorageDirectory() +
            "/write_segments/upload/e9bb32c0c705a93d_1458249140140.zip" ;

    static private String UPLOAD_2 = Environment.getExternalStorageDirectory() +
            "/write_segments/1458250230609/e9bb32c0c705a93d_1458250230609.zip" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //wifi listener
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        WebManager webManager = new WebManager(connectionManager);

        //try to push test file
        webManager.queueUpload(UPLOAD_2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(webManager, filter);

    }

}
