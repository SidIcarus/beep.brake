package edu.rit.se.beepbrake.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Target;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.utils.Utilities;

public class LaunchScreenActivity extends AppCompatActivity {

    static { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO); }

    /** The duration (milliseconds) of the launch screen. */
    private static final int SPLASH_TIME = 3000;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeTransparentStatusBar();

        setContentView(R.layout.activity_launch_screen);

        setLaunchAppVersionText();

        new BackgroundTask().execute();
    }

    private void makeTransparentStatusBar() {
        // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        }

    }

    // TODO: Figure out why it is giving me issues here
    // unhandled exception android.content.pm.PackageManager
    private void setLaunchAppVersionText() {
        TextView appVer = (TextView) findViewById(R.id.launch_text_app_version);

//        String versionName = this.getPackageManager().getPackageInfo("edu.rit.se.beepbrake",
//            PackageManager.GET_CONFIGURATIONS).versionName;
//        appVer.setText(versionName);
    }

    private class BackgroundTask extends AsyncTask {
        Intent intent;

        @Override protected void onPreExecute() {
            super.onPreExecute();
            intent = new Intent(LaunchScreenActivity.this, MainActivity.class);
//            intent.setAction(Intent.);
        }

        // Use this method to load background data that the app needs.
        @Override protected Object doInBackground(Object[] params) {
            try {
                Thread.sleep(SPLASH_TIME);
                initializePreferences();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Set the default values for the preferences here
        private void initializePreferences() {

            String ANDROID = Build.VERSION.RELEASE;
            String BOARD = Build.BOARD;
            String BOOTLOADER = Build.BOOTLOADER;
            String BRAND = Build.BRAND;
            String DEVICE = Build.DEVICE;
            String DISPLAY = Build.DISPLAY;
            String FINGERPRINT = Build.FINGERPRINT;
            String HARDWARE = Build.HARDWARE;
            String HOST = Build.HOST;
            String ID = Build.ID;
            String MANUFACTURER = Build.MANUFACTURER;
            String MODEL = Build.MODEL;
            String PRODUCT = Build.PRODUCT;
            String RADIO = Build.getRadioVersion();
            String TAGS = Build.TAGS;
            String TYPE = Build.TYPE;
            String USER = Build.USER;

            /*
            if(Utilities.mSDKVersion > Build.VERSION_CODES.LOLLIPOP) {
                String[] CPU_ABI = Build.SUPPORTED_32_BIT_ABIS;
                String[] CPU_ABI2 = Build.SUPPORTED_64_BIT_ABIS;
            } else {
                String CPU_ABI = Build.CPU_ABI;
                String CPU_ABI2 = Build.CPU_ABI2;
            }
            */

            String androidID =
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        }

        @Override protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            // Pass your loaded data here using Intent

            // intent.putExtra("data_key", "");
            startActivity(intent);
            finish();
        }
    }
}
