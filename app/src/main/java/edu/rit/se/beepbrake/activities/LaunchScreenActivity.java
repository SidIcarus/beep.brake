package edu.rit.se.beepbrake.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.TextView;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.utils.Preferences;
import edu.rit.se.beepbrake.utils.Utils;
import edu.rit.se.beepbrake.web.WebManager;

public class LaunchScreenActivity extends AppCompatActivity {

    static { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO); }

    private static final int SPLASH_TIME = 3000;

    @SuppressWarnings("unchecked")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.makeTransparentStatusBar(this);

        setContentView(R.layout.activity_launch_screen);

        // Set the app version text on the launch screen
        TextView appVer = (TextView) findViewById(R.id.launch_text_app_version);

        if (appVer != null) appVer.setText(Utils.getAppVersion(this));

        new BackgroundTask().execute();
    }

    private void initWifiListener(Preferences p) {
        ConnectivityManager
            connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        WebManager webMan = WebManager.getInstance();

        String uURLRes = Utils.resToName(getResources(), R.string.web_upload_url);
        String uURLDefault = getString(R.string.web_upload_url);
        String uURL = p.getSetting(uURLRes, uURLDefault);

        WebManager.initWebManager(Utils.getWritePath(this), uURL);
        webMan.setConnectionManager(connectionManager);

        // Listen on intent filter
        IntentFilter connectionIntent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(webMan, connectionIntent);
    }

    private class BackgroundTask extends AsyncTask {
        Intent intent;

        @Override protected void onPreExecute() {
            super.onPreExecute();
            intent = new Intent(LaunchScreenActivity.this, MainActivity.class);

            Preferences p = Preferences.getInstance(true, LaunchScreenActivity.this);
            initWifiListener(p);
        }

        // Use this method to load background data that the app needs.
        @Override protected Object doInBackground(Object[] params) {
            try {
                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            // Pass your loaded data here using Intent

            // intent.putExtra("data_key", "");
            startActivity(intent);
            finish();
        }
    }
}
