package edu.rit.se.beepbrake.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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

public class Preferences {

    /** Contains all the actual functionality. */
    private static class settings {

        private static SharedPreferences settings;

        /** Will instantiate the SharedPreferences singleton if it hasn't done so already. */
        private static void newInstance(Context ctx) {
            if (settings == null)
                settings = ctx.getSharedPreferences(Preference.FILE_NAME, Context.MODE_PRIVATE);
        }

        /**
         * @param type {@link PreferenceType} #Type is
         */
        private static Object get(@NonNull Context ctx, @PreferenceType int type,
            @NonNull String name, @Nullable Object defaultVal, @Nullable Object optDefaultVal) {
            newInstance(ctx);

            try {
                switch (type) {
                    case PreferenceType.ALL:
                        return settings.getAll();
                    case PreferenceType.BOOL:
                        return settings.getBoolean(name, (boolean) defaultVal);
                    case PreferenceType.DATE:
                        long date = getSetting(ctx, name + "_value", ((Date) defaultVal).getTime());
                        String zone =
                            getSetting(ctx, name + "_zone", ((TimeZone) optDefaultVal).getID());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        calendar.setTimeZone(TimeZone.getTimeZone(zone));

                        return calendar.getTime();
                    case PreferenceType.FLOAT:
                        return settings.getFloat(name, (float) defaultVal);
                    case PreferenceType.INT:
                        return settings.getInt(name, (int) defaultVal);
                    case PreferenceType.LONG:
                        return settings.getLong(name, (long) defaultVal);
                    case PreferenceType.STR:
                        return settings.getString(name, (String) defaultVal);
                    case PreferenceType.STR_SET:
                        // TODO: Something
                        return settings.getStringSet(name, (Set<String>) defaultVal);
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
        private static Object get(Context ctx, @PreferenceType int type) {
            return get(ctx, type, null, null, null);
        }

        /** Alias for get(Context, @Type int, String, Object, Object) */
        private static Object get(Context ctx, @PreferenceType int type, String name,
            Object defaultVal) {
            return get(ctx, type, name, defaultVal, null);
        }

        /**
         *
         * @param ctx
         * @param type
         * @param name
         * @param value
         * @param optVal
         */
        @SuppressWarnings("unchecked")
        private static void set(@NonNull Context ctx, @PreferenceType int type, String name,
            Object value, Object optVal) {
            newInstance(ctx);

            SharedPreferences.Editor editor = settings.edit();

            try {
                switch (type) {
                    case PreferenceType.ALL:
                        throw new UnsupportedOperationException();
                    case PreferenceType.BOOL:
                        editor.putBoolean(name, (boolean) value);
                        break;
                    case PreferenceType.DATE:
                        Date date =
                            (value != null) ? (Date) value : new Date(System.currentTimeMillis());

                        TimeZone zone =
                            (optVal != null) ? (TimeZone) optVal : TimeZone.getDefault();

                        editor.putLong(name + "_value", date.getTime());
                        editor.putString(name + "_zone", zone.getID());
                        break;
                    case PreferenceType.FLOAT:
                        editor.putFloat(name, (float) value);
                        break;
                    case PreferenceType.INT:
                        editor.putInt(name, (int) value);
                        break;
                    case PreferenceType.LONG:
                        editor.putLong(name, (long) value);
                        break;
                    case PreferenceType.STR:
                        editor.putString(name, (String) value);
                        break;
                    case PreferenceType.STR_SET:
                        editor.putStringSet(name, (Set<String>) value);
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
                if (value != null) {
                    if (e instanceof ClassCastException) errorMsg = "";
                    if (e instanceof UnsupportedOperationException) errorMsg = "";
                }
                e.printStackTrace();
            }
            editor.apply();
        }

        /** Alias for set(Context, @Type int, String, Object, Object) */
        private static void set(Context ctx, @PreferenceType int type, String name, Object value) {
            set(ctx, type, name, value, null);
        }
    }

    /** @return A Map<String, ?> that contains all of the settings */
    @SuppressWarnings("unchecked")
    public static Map<String, ?> getSettings(Context ctx) {
        return (Map<String, ?>) settings.get(ctx, PreferenceType.ALL);
    }

    // -------------------------------------------------------------------------------------------//
    // Aliases for setting.get() for any type of setting
    // See settings.get(Context, @Type int, String, Object, Object)
    // -------------------------------------------------------------------------------------------//

    public static Boolean getSetting(Context ctx, String name, Boolean defaultValue) {
        return (Boolean) settings.get(ctx, PreferenceType.BOOL, name, defaultValue);
    }

    /** If !useGivenDefaults: defaultDate = today's date, defaultZone = TimeZone.getDefault() */
    public static Date getSetting(Context ctx, String name, Date defaultDate, TimeZone defaultZone,
        Boolean useGivenDefaults) {
        if (!useGivenDefaults) {
            defaultDate = new Date(System.currentTimeMillis());
            defaultZone = TimeZone.getDefault();
        }
        return (Date) settings.get(ctx, PreferenceType.DATE, name, defaultDate, defaultZone);
    }

    public static Float getSetting(Context ctx, String name, Float defaultValue) {
        return (Float) settings.get(ctx, PreferenceType.FLOAT, name, defaultValue);
    }

    public static int getSetting(Context ctx, String name, int defaultValue) {
        return (Integer) settings.get(ctx, PreferenceType.INT, name, defaultValue);
    }

    public static Long getSetting(Context ctx, String name, Long defaultValue) {
        return (Long) settings.get(ctx, PreferenceType.LONG, name, defaultValue);
    }

    public static String getSetting(Context ctx, String name, String defaultValue) {
        return (String) settings.get(ctx, PreferenceType.STR, name, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getSetting(Context ctx, String name, Set<String> defaultValue) {
        return (Set<String>) settings.get(ctx, PreferenceType.STR_SET, name, defaultValue);
    }

    // -------------------------------------------------------------------------------------------//
    // Aliases for setting.set() for any type of setting
    // See settings.set(Context, @Type int, String, Object)
    // -------------------------------------------------------------------------------------------//

    public static void setSetting(Context ctx, String name, Boolean value) {
        settings.set(ctx, PreferenceType.BOOL, name, value);
    }

    public static void setSetting(Context ctx, String name, Date date, TimeZone zone,
        Boolean useGivenDefaults) {
        if (!useGivenDefaults) {
            date = new Date(System.currentTimeMillis());
            zone = TimeZone.getDefault();
        }
        settings.set(ctx, PreferenceType.DATE, name, date, zone);
    }

    public static void setSetting(Context ctx, String name, Float value) {
        settings.set(ctx, PreferenceType.FLOAT, name, value);
    }

    public static void setSetting(Context ctx, String name, int value) {
        settings.set(ctx, PreferenceType.INT, name, value);
    }

    public static void setSetting(Context ctx, String name, Long value) {
        settings.set(ctx, PreferenceType.LONG, name, value);
    }

    public static void setSetting(Context ctx, String name, String value) {
        settings.set(ctx, PreferenceType.STR, name, value);
    }

    public static void setSetting(Context ctx, String name, Set<String> value) {
        settings.set(ctx, PreferenceType.STR_SET, name, value);
    }

}
