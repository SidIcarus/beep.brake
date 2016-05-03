package edu.rit.se.beepbrake.activities;

import android.content.Intent;
import android.content.IntentFilter;
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
        try {
            TextView appVer = (TextView) findViewById(R.id.launch_text_app_version);

            if (appVer != null) appVer.setText(Utils.getAppVersion(this));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new BackgroundTask().execute();
    }

    /** Where the default values for SharedPreferences get set. */
    @SuppressWarnings("deprecated")
    private void initPreferences() throws PackageManager.NameNotFoundException {
        String[] device = getResources().getStringArray(R.array.device);
        String prependToName = Utils.resToName(getResources(), R.array.device);
        String val = "default value";

        for (String name : device) {
            switch (name) {
                // @formatter:off
                case "board":           val = Build.BOARD;              break;
                case "bootloader":      val = Build.BOOTLOADER;         break;
                case "brand":           val = Build.BRAND;              break;
                case "cpu_abi":         val = Build.CPU_ABI;            break;
                case "cpu_abi2":        val = Build.CPU_ABI2;           break;
                /*  // I'm unsure how to integrate the non-deprecated versions of CPU_ABI/2
                    if(Utilities.isOlderThan21) {
                        String[] cpu_abi  = Build.SUPPORTED_32_BIT_ABIS;
                        String[] cpu_abi2 = Build.SUPPORTED_64_BIT_ABIS;
                    }
                */
                case "device":          val = Build.DEVICE;             break;
                case "display":         val = Build.DISPLAY;            break;
                case "fingerprint":     val = Build.FINGERPRINT;        break;
                case "host":            val = Build.HOST;               break;
                case "hardware":        val = Build.HARDWARE;           break;
                case "id":              val = Build.ID;                 break;
                case "manufacturer":    val = Build.MANUFACTURER;       break;
                case "model":           val = Build.MODEL;              break;
                case "product":         val = Build.PRODUCT;            break;
                case "os_version":      val = Build.VERSION.RELEASE;    break;
                case "radio":           val = Build.getRadioVersion();  break;
                case "tags":            val = Build.TAGS;               break;
                case "type":            val = Build.TYPE;               break;
                case "user":            val = Build.USER;               break;
                // @formatter:on
            }
            Preferences.setSetting(this, prependToName + name, val);
        }

        String aID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Preferences.setSetting(this, "android_id", aID);

        Preferences.setSetting(this, "app_version", Utils.getAppVersion(this));

        Preferences.setSetting(this, "install_date", null, null, false);

        Preferences.setSetting(this, Utils.resToName(getResources(), R.bool.eula_status),
                               getResources().getBoolean(R.bool.eula_status));

        // TODO: Check if this actually gets the right write path
        String iWritePath = getFilesDir().getPath();
        Preferences.setSetting(this, "internal_write_path", iWritePath);

        // TODO: Add checks for if there is external storage
        //  then default it to "Unavailable" | iWritePath
        String eWritePath = Environment.getExternalStorageDirectory().getPath();
        Preferences.setSetting(this, "external_write_path", eWritePath);

        Preferences.setSetting(this, Utils.resToName(getResources(), R.string.write_directory),
                               getString(R.string.write_directory));

        Preferences.setSetting(this, "write_path", eWritePath);

        Preferences.setSetting(this, Utils.resToName(getResources(),R.string.web_upload_url),
                               getString(R.string.web_upload_url));
    }

    private void initWifiListener() {
        ConnectivityManager
            connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        WebManager webMan = WebManager.getInstance();

        String uURLRes = Utils.resToName(getResources(), R.string.web_upload_url);
        String uURLDefault = getString(R.string.web_upload_url);
        String uURL = Preferences.getSetting(this, uURLRes, uURLDefault);

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

            try {
                initPreferences();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            intent = new Intent(LaunchScreenActivity.this, MainActivity.class);

            initWifiListener();
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
