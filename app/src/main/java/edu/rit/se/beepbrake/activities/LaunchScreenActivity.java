package edu.rit.se.beepbrake.activities;

import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.TextView;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.utils.Preferences;
import edu.rit.se.beepbrake.utils.Utils;
import edu.rit.se.beepbrake.web.WebManager;

public class LaunchScreenActivity extends AppCompatActivity {

    final long splashTIME = (long) getResources().getInteger(R.integer.SPLASH_TIME);


    /**
     *
     */
    private class BackgroundTask extends AsyncTask {
        Intent mIntent = null;

        //BackgroundTask() {}

        // Use this method to load background data that the app needs.
        @Nullable @Override protected final Object doInBackground(Object... params) {
            try {
                mIntent = new Intent(LaunchScreenActivity.this, MainActivity.class);

                Preferences p = Preferences.getInstance(LaunchScreenActivity.this);
                WebManager.register(LaunchScreenActivity.this, Preferences.getInstance());
                Thread.sleep(splashTIME);
            } catch (InterruptedException e) {
                Log.d("System.Launch", "doInBackground: error");
                e.printStackTrace();
            }
            return null;
        }

        // Pass your loaded data here using Intent
        @Override protected final void onPostExecute(Object result) {
            // mIntent.putExtra("data_key", "");
            startActivity(mIntent);
            finish();
        }

        @Override protected final void onPreExecute() { }
    }

    static { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO); }

    @Override
    @SuppressWarnings("unchecked")
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.makeTransparentStatusBar(this);

        setContentView(R.layout.activity_launch_screen);

        // Set the app version text on the launch screen
        TextView appVer = (TextView) findViewById(R.id.launch_text_app_version);

        if (appVer != null) appVer.setText(Utils.getAppVersion(this));

        new BackgroundTask().execute();
    }
}
