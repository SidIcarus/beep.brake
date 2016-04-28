package edu.rit.se.beepbrake.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;


/*
bool eula_status
string device_id



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

public class SharedPreferencesDirector {

    @IntDef({
                SharedPreferenceTypes.INT, SharedPreferenceTypes.STRING,
                SharedPreferenceTypes.FLOAT, SharedPreferenceTypes.BOOLEAN,
                SharedPreferenceTypes.LONG, SharedPreferenceTypes.STRING_SET})
    @Retention(RetentionPolicy.RUNTIME) public @interface SharedPreferenceTypes {
        int INT = 0, STRING = 1, FLOAT = 2, BOOLEAN = 3, LONG = 4, STRING_SET = 5;
    }

    private static final String PREFERENCES_FILE = "beep_brake_settings";
    private static SharedPreferences settings;

    /**
     * Will instantiate the SharedPreferences singleton if it hasn't done so already.
     *
     * @param ctx The main activity.
     */
    private static void instantiateSharedPreferences(Context ctx) {
        if (settings == null)
            settings = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    private static Object getSetting(Context ctx, String settingName, Object defaultValue,
        @SharedPreferenceTypes int settingType) {
        instantiateSharedPreferences(ctx);

        try {
            switch (settingType) {
                case SharedPreferenceTypes.INT:
                    return settings.getInt(settingName, (int) defaultValue);
                case SharedPreferenceTypes.STRING:
                    return settings.getString(settingName, (String) defaultValue);
                case SharedPreferenceTypes.FLOAT:
                    return settings.getFloat(settingName, (float) defaultValue);
                case SharedPreferenceTypes.BOOLEAN:
                    return settings.getBoolean(settingName, (boolean) defaultValue);
                case SharedPreferenceTypes.LONG:
                    return settings.getLong(settingName, (long) defaultValue);
                case SharedPreferenceTypes.STRING_SET:
                    return settings.getStringSet(settingName, (Set<String>) defaultValue);
            }
        } catch (ClassCastException e) { }

        return new Object();
    }

    private static void setSetting(Context ctx, String settingName, Object settingValue,
        @SharedPreferenceTypes int settingType) {
        instantiateSharedPreferences(ctx);

        SharedPreferences.Editor editor = settings.edit();

        try {
            switch (settingType) {
                case SharedPreferenceTypes.INT:
                    editor.putInt(settingName, (int) settingValue);
                    break;
                case SharedPreferenceTypes.STRING:
                    editor.putString(settingName, (String) settingValue);
                    break;
                case SharedPreferenceTypes.FLOAT:
                    editor.putFloat(settingName, (float) settingValue);
                    break;
                case SharedPreferenceTypes.BOOLEAN:
                    editor.putBoolean(settingName, (boolean) settingValue);
                    break;
                case SharedPreferenceTypes.LONG:
                    editor.putLong(settingName, (long) settingValue);
                    break;
                case SharedPreferenceTypes.STRING_SET:
                    editor.putStringSet(settingName, (Set<String>) settingValue);
                    break;
            }
        } catch (ClassCastException e) { }

        editor.apply();
    }

    public static Map<String, ?> getAllPreferences(Context ctx) {
        instantiateSharedPreferences(ctx);
        return settings.getAll();
    }

    public static void setSetting(Context ctx, String settingName, Boolean settingValue) {
        setSetting(ctx, settingName, settingValue, SharedPreferenceTypes.BOOLEAN);
    }

    public static void setSetting(Context ctx, String settingName, Float settingValue) {
        setSetting(ctx, settingName, settingValue, SharedPreferenceTypes.FLOAT);
    }

    public static void setSetting(Context ctx, String settingName, int settingValue) {
        setSetting(ctx, settingName, settingValue, SharedPreferenceTypes.INT);
    }

    public static void setSetting(Context ctx, String settingName, Long settingValue) {
        setSetting(ctx, settingName, settingValue, SharedPreferenceTypes.LONG);
    }

    public static void setSetting(Context ctx, String settingName, String settingValue) {
        setSetting(ctx, settingName, settingValue, SharedPreferenceTypes.STRING);
    }

    public static void setSetting(Context ctx, String settingName, String[] settingValue) {
        setSetting(ctx, settingName, settingValue, SharedPreferenceTypes.STRING_SET);
    }

    public static String getSetting(Context ctx, String settingName, String defaultValue) {
        return (String) getSetting(ctx, settingName, defaultValue,
            SharedPreferenceTypes.STRING);
    }

    public static Boolean getSetting(Context ctx, String settingName, Boolean defaultValue) {
        return (Boolean) getSetting(ctx, settingName, defaultValue,
            SharedPreferenceTypes.BOOLEAN);
    }

    public static Long getSetting(Context ctx, String settingName, Long defaultValue) {
        return (Long) getSetting(ctx, settingName, defaultValue, SharedPreferenceTypes.LONG);
    }

    public static Integer getSetting(Context ctx, String settingName, Integer defaultValue) {
        return (Integer) getSetting(ctx, settingName, defaultValue,
            SharedPreferenceTypes.INT);
    }

    public static Float getSetting(Context ctx, String settingName, Float defaultValue) {
        return (Float) getSetting(ctx, settingName, defaultValue,
            SharedPreferenceTypes.FLOAT);
    }

    public static Set<String> getSetting(Context ctx, String settingName,
        Set<String> defaultValue) {
        return (Set<String>) getSetting(ctx, settingName, defaultValue,
            SharedPreferenceTypes.STRING_SET);
    }

    public static void setDateSetting(Context ctx, String settingName) {
        //getting the current time in milliseconds, and creating a Date object from it:
        Date date = new Date(System.currentTimeMillis());

        setSetting(ctx, settingName + "_value", date.getTime());
        setSetting(ctx, settingName + "_zone", TimeZone.getDefault().getID());
    }

    public static Date getDateSetting(Context ctx, String settingName) {
        Calendar calendar = Calendar.getInstance();
        long date = getSetting(ctx, settingName + "_value", 0);
        String zone = getSetting(ctx, settingName + "_zone", TimeZone.getDefault().getID());

        calendar.setTimeInMillis(date);
        calendar.setTimeZone(TimeZone.getTimeZone(zone));
        return calendar.getTime();
    }

    public static Boolean getEULAStatus(Context ctx) {
        return getSetting(ctx, "EULA_STATUS", false);
    }

    public static void showEULASnack(Context ctx, CoordinatorLayout cLayout) {
        String eStatus = getEULAStatus(ctx).toString();
        Snackbar snackbar = Snackbar.make(cLayout, "EULA_STATUS: " + eStatus, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showSettingSnack(CoordinatorLayout cLayout, String settingName) {
        Snackbar snackbar = Snackbar.make(cLayout, settingName + ": ", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void setEULAStatus(Context ctx, boolean status) {
        //        setSetting(ctx, "EULA_STATUS", SharedPreferenceTypes.BOOLEAN, status);
    }

}
