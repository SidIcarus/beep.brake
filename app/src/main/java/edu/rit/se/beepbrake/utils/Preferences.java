package edu.rit.se.beepbrake.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

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

/**
 *
 */
public class Preferences {

    /** Contains all the actual functionality. */
    private static class settings {

        // @formatter:off
        @IntDef({Type.INT, Type.STR, Type.FLOAT, Type.BOOL, Type.LONG, Type.STR_SET, Type.DATE,
                 Type.ALL})
        @Retention(RetentionPolicy.CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})
        public @interface Type {
            int INT = 0, STR = 1, FLOAT = 2, BOOL = 3, LONG = 4, STR_SET = 5, DATE = 6, ALL = 7;
        }
        // @formatter:on

        private static final String PREFERENCES_FILE = "beep_brake_settings";
        private static SharedPreferences settings;

        /** Will instantiate the SharedPreferences singleton if it hasn't done so already. */
        private static void newInstance(Context ctx) {
            if (settings == null)
                settings = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        }

        /**
         * @param type {@link Type} #Type is
         */
        private static Object get(Context ctx, @Type int type, String name, Object defaultVal,
            Object optDefaultVal) {
            newInstance(ctx);

            try {
                switch (type) {
                    case Type.ALL:
                        return settings.getAll();
                    case Type.BOOL:
                        return settings.getBoolean(name, (boolean) defaultVal);
                    case Type.DATE:
                        long date = getSetting(ctx, name + "_value", ((Date) defaultVal).getTime());
                        String zone =
                            getSetting(ctx, name + "_zone", ((TimeZone) optDefaultVal).getID());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        calendar.setTimeZone(TimeZone.getTimeZone(zone));

                        return calendar.getTime();
                    case Type.FLOAT:
                        return settings.getFloat(name, (float) defaultVal);
                    case Type.INT:
                        return settings.getInt(name, (int) defaultVal);
                    case Type.LONG:
                        return settings.getLong(name, (long) defaultVal);
                    case Type.STR:
                        return settings.getString(name, (String) defaultVal);
                    case Type.STR_SET:
                        return settings.getStringSet(name, (Set<String>) defaultVal);

                }
            } catch (ClassCastException e) {
                // TODO: Put error message here
                e.printStackTrace();
            }

            return new Object();
        }

        /** Alias for get(Context, @Type int, String, Object, Object) */
        private static Object get(Context ctx, @Type int type) {
            return get(ctx, type, null, null, null);
        }

        /** Alias for get(Context, @Type int, String, Object, Object) */
        private static Object get(Context ctx, @Type int type, String name, Object defaultVal) {
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
        private static void set(Context ctx, @Type int type, String name, Object value,
            Object optVal) {
            newInstance(ctx);

            SharedPreferences.Editor editor = settings.edit();

            try {
                switch (type) {
                    case Type.ALL:
                        throw new UnsupportedOperationException();
                    case Type.BOOL:
                        editor.putBoolean(name, (boolean) value);
                        break;
                    case Type.DATE:
                        Date date =
                            (value != null) ? (Date) value : new Date(System.currentTimeMillis());

                        TimeZone zone =
                            (optVal != null) ? (TimeZone) optVal : TimeZone.getDefault();

                        editor.putLong(name + "_value", date.getTime());
                        editor.putString(name + "_zone", zone.getID());
                        break;
                    case Type.FLOAT:
                        editor.putFloat(name, (float) value);
                        break;
                    case Type.INT:
                        editor.putInt(name, (int) value);
                        break;
                    case Type.LONG:
                        editor.putLong(name, (long) value);
                        break;
                    case Type.STR:
                        editor.putString(name, (String) value);
                        break;
                    case Type.STR_SET:
                        editor.putStringSet(name, (Set<String>) value);
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
        private static void set(Context ctx, @Type int type, String name, Object value) {
            set(ctx, type, name, value, null);
        }
    }

    /** @return A Map<String, ?> that contains all of the settings */
    public static Map<String, ?> getSettings(Context ctx) {
        return (Map<String, ?>) settings.get(ctx, settings.Type.ALL);
    }

    // -------------------------------------------------------------------------------------------//
    // Aliases for setting.get() for any type of setting
    // See settings.get(Context, @Type int, String, Object, Object)
    // -------------------------------------------------------------------------------------------//

    public static Boolean getSetting(Context ctx, String name, Boolean defaultValue) {
        return (Boolean) settings.get(ctx, settings.Type.BOOL, name, defaultValue);
    }

    /** If !useGivenDefaults: defaultDate = today's date, defaultZone = TimeZone.getDefault() */
    public static Date getSetting(Context ctx, String name, Date defaultDate, TimeZone defaultZone,
        Boolean useGivenDefaults) {
        if (!useGivenDefaults) {
            defaultDate = new Date(System.currentTimeMillis());
            defaultZone = TimeZone.getDefault();
        }
        return (Date) settings.get(ctx, settings.Type.DATE, name, defaultDate, defaultZone);
    }

    public static Float getSetting(Context ctx, String name, Float defaultValue) {
        return (Float) settings.get(ctx, settings.Type.FLOAT, name, defaultValue);
    }

    public static int getSetting(Context ctx, String name, int defaultValue) {
        return (Integer) settings.get(ctx, settings.Type.INT, name, defaultValue);
    }

    public static Long getSetting(Context ctx, String name, Long defaultValue) {
        return (Long) settings.get(ctx, settings.Type.LONG, name, defaultValue);
    }

    public static String getSetting(Context ctx, String name, String defaultValue) {
        return (String) settings.get(ctx, settings.Type.STR, name, defaultValue);
    }

    public static Set<String> getSetting(Context ctx, String name, Set<String> defaultValue) {
        return (Set<String>) settings.get(ctx, settings.Type.STR_SET, name, defaultValue);
    }

    // -------------------------------------------------------------------------------------------//
    // Aliases for setting.set() for any type of setting
    // See settings.set(Context, @Type int, String, Object)
    // -------------------------------------------------------------------------------------------//

    public static void setSetting(Context ctx, String name, Boolean value) {
        settings.set(ctx, settings.Type.BOOL, name, value);
    }

    public static void setSetting(Context ctx, String name, Date date, TimeZone zone,
        Boolean useGivenDefaults) {
        if (!useGivenDefaults) {
            date = new Date(System.currentTimeMillis());
            zone = TimeZone.getDefault();
        }
        settings.set(ctx, settings.Type.DATE, name, date, zone);
    }

    public static void setSetting(Context ctx, String name, Float value) {
        settings.set(ctx, settings.Type.FLOAT, name, value);
    }

    public static void setSetting(Context ctx, String name, int value) {
        settings.set(ctx, settings.Type.INT, name, value);
    }

    public static void setSetting(Context ctx, String name, Long value) {
        settings.set(ctx, settings.Type.LONG, name, value);
    }

    public static void setSetting(Context ctx, String name, String value) {
        settings.set(ctx, settings.Type.STR, name, value);
    }

    public static void setSetting(Context ctx, String name, Set<String> value) {
        settings.set(ctx, settings.Type.STR_SET, name, value);
    }
}
