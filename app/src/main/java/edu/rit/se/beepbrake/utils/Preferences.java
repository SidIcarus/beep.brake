package edu.rit.se.beepbrake.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import edu.rit.se.beepbrake.R;
import edu.rit.se.beepbrake.annotations.Preference;
import edu.rit.se.beepbrake.annotations.PreferenceType;

/*
These are to be kept in mem until it has a chance to be written to SP. (the POST return #
100/200/400) the only time it will be saving to the SP will be when the activity has to close and
 there has
been a failure and or something that has not been uploaded yet so as to
try it again when the app starts again

Str[] UploadUID
Str[] UploadStatus

*/

/*
TODO: SharedPreferences - save Array<Str> UploadUID + UploadStatus This will be stored in mem
until it has a chance to be written to SP.


TODO: SP - log when we upload the UID for the device that we are creating uploaded it
 */

@SuppressWarnings("unused")
public class Preferences {

    private boolean initialized = false;
    private WeakReference<Context> wContext;
    private SharedPreferences settings;

    //Singleton implementation
    private Preferences() { }

    private static class LazyInstance {
        private static final Preferences instance = new Preferences();
    }

    public static Preferences getInstance(boolean initialize, Context context)
        throws PackageManager.NameNotFoundException {
        Preferences p = LazyInstance.instance;
        if (initialize) p.initialize(context);
        return p;
    }

    public static Preferences getInstance()
        throws PackageManager.NameNotFoundException { return getInstance(false, null); }

    public void setNewContext(Context context) {
        if(wContext.get() != context) wContext = new WeakReference<>(context);
    }

    /** Where the default values for SharedPreferences get set. */
    @SuppressWarnings("deprecated")
    private void initialize(Context context) throws PackageManager.NameNotFoundException {
        if (!initialized) {
            wContext = new WeakReference<>(context);
            settings = context.getSharedPreferences(Preference.FILE_NAME, Context.MODE_PRIVATE);

            String[] device = context.getResources().getStringArray(R.array.device);
            String prependToName = Utils.resToName(context.getResources(), R.array.device);
            String val;

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
                default:                val = "default value";          break;
                // @formatter:on
                }
                setSetting(prependToName + name, val);
            }

            String aID =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            setSetting("android_id", aID);

            setSetting("app_version", Utils.getAppVersion(context));

            setSetting("install_date", null, null, false);

            setSetting(Utils.resToName(context.getResources(), R.bool.eula_status),
                       context.getResources().getBoolean(R.bool.eula_status));

            // TODO: Check if this actually gets the right write path
            String iWritePath = context.getFilesDir().getPath();
            setSetting("internal_write_path", iWritePath);

            // TODO: Add checks for if there is external storage
            //  then default it to "Unavailable" | iWritePath
            String eWritePath = Environment.getExternalStorageDirectory().getPath();
            setSetting("external_write_path", eWritePath);

            setSetting(Utils.resToName(context.getResources(), R.string.write_directory),
                       context.getString(R.string.write_directory));

            setSetting("write_path", eWritePath);

            setSetting(Utils.resToName(context.getResources(), R.string.web_upload_url),
                       context.getString(R.string.web_upload_url));
        }
    }

    @SuppressWarnings("unchecked")
    private Object get(@PreferenceType int type,
        @Nullable String name, @Nullable Object dVal, @Nullable Object optDVal) {
        Context ctx = this.wContext.get();

        try {
            switch (type) {
                case PreferenceType.ALL:
                    return settings.getAll();
                case PreferenceType.BOOL:
                    return settings.getBoolean(name, (boolean) dVal);
                case PreferenceType.DATE:
                    long date = getSetting(name + "_value", ((Date) dVal).getTime());
                    String zone =
                        getSetting(name + "_zone", ((TimeZone) optDVal).getID());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(date);
                    calendar.setTimeZone(TimeZone.getTimeZone(zone));

                    return calendar.getTime();
                case PreferenceType.FLOAT:
                    return settings.getFloat(name, (float) dVal);
                case PreferenceType.INT:
                    return settings.getInt(name, (int) dVal);
                case PreferenceType.LONG:
                    return settings.getLong(name, (long) dVal);
                case PreferenceType.STR:
                    return settings.getString(name, (String) dVal);
                case PreferenceType.STR_SET:
                    // TODO: Something
                    return settings.getStringSet(name, (Set<String>) dVal);
                case PreferenceType.INVALID:
                    break;
                case PreferenceType.NULL:
                    break;
                default: //Do something here, prob won't have anything for NULL/INVALID not sure
                    break;
            }
        } catch (ClassCastException e) { e.printStackTrace(); }

        return new Object();
    }

    /** Alias for get(Context, @Type int, String, Object, Object) */
    private Object get(@PreferenceType int type) {
        return get(type, null, null, null);
    }

    /** Alias for get(Context, @Type int, String, Object, Object) */
    private Object get(@PreferenceType int type, @NonNull String name,
        @NonNull Object dVal) {
        return get(type, name, dVal, null);
    }

    @SuppressWarnings("unchecked")
    private void set(@PreferenceType int type, @NonNull String name,
        @Nullable Object val, @Nullable Object optVal) {

        SharedPreferences.Editor editor = settings.edit();

        try {
            switch (type) {
                case PreferenceType.ALL:
                    throw new UnsupportedOperationException();
                case PreferenceType.BOOL:
                    editor.putBoolean(name, (boolean) val);
                    break;
                case PreferenceType.DATE:
                    Date date =
                        (val != null) ? (Date) val : new Date(System.currentTimeMillis());

                    TimeZone zone =
                        (optVal != null) ? (TimeZone) optVal : TimeZone.getDefault();

                    editor.putLong(name + "_value", date.getTime());
                    editor.putString(name + "_zone", zone.getID());
                    break;
                case PreferenceType.FLOAT:
                    editor.putFloat(name, (float) val);
                    break;
                case PreferenceType.INT:
                    editor.putInt(name, (int) val);
                    break;
                case PreferenceType.LONG:
                    editor.putLong(name, (long) val);
                    break;
                case PreferenceType.STR:
                    editor.putString(name, (String) val);
                    break;
                case PreferenceType.STR_SET:
                    editor.putStringSet(name, (Set<String>) val);
                    break;
                case PreferenceType.INVALID:
                    break;
                case PreferenceType.NULL:
                    break;
                default: //Do something here, prob won't have anything for NULL/INVALID not sure
                    break;
            }
        } catch (ClassCastException | UnsupportedOperationException | NullPointerException e) {
            String errorMsg = "An error has occurred in Preferences.set()";

            if (e instanceof NullPointerException) {
                errorMsg = "The settings value cannot be null for this operation.";
            }

            // TODO: Put new error messages here
            if (val != null) {
                if (e instanceof ClassCastException)
                    errorMsg = "ClassCastException";
                if (e instanceof UnsupportedOperationException)
                    errorMsg = "UnsupportedOperationException";
            }
            e.printStackTrace();
        }
        editor.apply();
    }

    /** Alias for set(Context, @Type int, String, Object, Object) */
    private void set(@PreferenceType int type, @NonNull String name, @NonNull Object val) {
        set(type, name, val, null);
    }

    /** @return A Map<String, ?> that contains all of the settings */
    @SuppressWarnings("unchecked")
    public Map<String, ?> getSettings() { return (Map<String, ?>) get(PreferenceType.ALL); }

    // -------------------------------------------------------------------------------------------//
    // Aliases for setting.get() for any type of setting
    // See get(Context, @Type int, String, Object, Object)
    // -------------------------------------------------------------------------------------------//

    public Boolean getSetting(String name, Boolean defaultValue) {
        return (Boolean) get(PreferenceType.BOOL, name, defaultValue);
    }

    /** If !useGivenDefaults: defaultDate = today's date, defaultZone = TimeZone.getDefault() */
    public Date getSetting(String name, Date defaultDate, TimeZone defaultZone,
        Boolean useGivenDefaults) {
        if (!useGivenDefaults) {
            defaultDate = new Date(System.currentTimeMillis());
            defaultZone = TimeZone.getDefault();
        }
        return (Date) get(PreferenceType.DATE, name, defaultDate, defaultZone);
    }

    public Float getSetting(String name, Float defaultValue) {
        return (Float) get(PreferenceType.FLOAT, name, defaultValue);
    }

    public int getSetting(String name, int defaultValue) {
        return (Integer) get(PreferenceType.INT, name, defaultValue);
    }

    public Long getSetting(String name, Long defaultValue) {
        return (Long) get(PreferenceType.LONG, name, defaultValue);
    }

    public String getSetting(String name, String defaultValue) {
        return (String) get(PreferenceType.STR, name, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getSetting(String name, Set<String> defaultValue) {
        return (Set<String>) get(PreferenceType.STR_SET, name, defaultValue);
    }

    // -------------------------------------------------------------------------------------------//
    // Aliases for setting.set() for any type of setting
    // See set(Context, @Type int, String, Object)
    // -------------------------------------------------------------------------------------------//

    public void setSetting(String name, Boolean value) { set(PreferenceType.BOOL, name, value); }

    public void setSetting(String name, Date date, TimeZone zone,
        Boolean useGivenDefaults) {
        if (!useGivenDefaults) {
            date = new Date(System.currentTimeMillis());
            zone = TimeZone.getDefault();
        }
        set(PreferenceType.DATE, name, date, zone);
    }

    public void setSetting(String name, Float value) { set(PreferenceType.FLOAT, name, value); }

    public void setSetting(String name, int value) { set(PreferenceType.INT, name, value); }

    public void setSetting(String name, Long value) { set(PreferenceType.LONG, name, value); }

    public void setSetting(String name, String value) { set(PreferenceType.STR, name, value); }

    public void setSetting(String name, Set<String> value) {
        set(PreferenceType.STR_SET, name, value);
    }

}
