package edu.rit.se.beepbrake.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import edu.rit.se.beepbrake.BuildConfig;
import edu.rit.se.beepbrake.annotations.Preference;
import edu.rit.se.beepbrake.annotations.PreferenceType;
import edu.rit.se.beepbrake.constants.PreferenceConstants;

/**
 * @author Sid
 * @date 04.01.2016
 */
@SuppressWarnings("unused")
@Keep
public final class Preferences implements SharedPreferences {

    private SharedPreferences mPreferences = null;
    private HashMap<String, Preference> mMap = null;

    /** Lazy Singleton implementation */
    private static final class Lazy {
        @SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
        static volatile Preferences singleton = new Preferences();
        static volatile Context mContext = null;
        static boolean isInitialized = false;
    }

    /** Constructor */
    private Preferences() {
        mPreferences =
            Lazy.mContext.getSharedPreferences(Preference.FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean contains(String key) { return mPreferences.contains(key); }

    @Override
    public Editor edit() { return mPreferences.edit(); }

    /**
     * Retrieve any type of preference from the SharedPreferences. Catches a ClassCastException if
     * there is a preference with this name that is not of the passed type.
     *
     * @param type        {@link PreferenceType}
     * @param key         The name of the preference to retrieve.
     * @param defValue    Value to return if this preference does not exist.
     * @param optDefValue Secondary Value to return if this preference does not exist.
     *                    Currently only used for TimeZone
     *
     * @return Returns the preference value if it exists, or defValue. Throws ClassCastException if
     * there is a preference with this name that is not of the passed type.
     */
    @SuppressWarnings({
                          "unchecked", "FieldRepeatedlyAccessedInMethod", "OverlyComplexMethod",
                          "OverlyLongMethod", "AssignmentToMethodParameter"})
    private Object get(@PreferenceType int type, String key, Object defValue, Object optDefValue) {
        try {
            switch (type) {
                //@formatter:off
                case PreferenceType.ALL:    return mPreferences.getAll();
                case PreferenceType.BOOL:   return mPreferences.getBoolean(key, (boolean) defValue);
                case PreferenceType.DATE:
                    long time = mPreferences.getLong(key + "_time", ((Date)defValue).getTime());
                    TimeZone zone = TimeZone.getTimeZone(
                        mPreferences.getString(key + "_tZone",((TimeZone)optDefValue).getID()));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    calendar.setTimeZone(zone);

                    return calendar.getTime();
                case PreferenceType.FLOAT:  return mPreferences.getFloat(key, (float) defValue);
                case PreferenceType.INT:    return mPreferences.getInt(key, (int) defValue);
                case PreferenceType.LONG:   return mPreferences.getLong(key, (long) defValue);
                case PreferenceType.STR:    return mPreferences.getString(key, (String) defValue);
                case PreferenceType.STR_SET:
                    return mPreferences.getStringSet(key, (Set<String>) defValue);
                case PreferenceType.TIME_ZONE:
                    return mPreferences.getString(key +"_tZone", ((TimeZone)optDefValue).getID());
                case PreferenceType.INVALID:break; // TODO: Prefs.get INVALID action
                case PreferenceType.NULL:   break; // TODO: Prefs.get NULL action

                default: break; // TODO: Prefs.get default action
                //@formatter:on
            }
        } catch (ClassCastException e) {
            if (BuildConfig.DEBUG) {
                @SuppressLint("DefaultLocale")
                String paramsToString =
                    String.format("type:%1d, name:%1s, defValue:%1s, optDefValue:%1s", type, key,
                                  defValue != null ? defValue.toString() : null,
                                  optDefValue != null ? optDefValue.toString() : null);

                Log.w(PreferenceConstants.logTAG, e);
                Log.d(PreferenceConstants.logTAG, paramsToString);
            }
            mPreferences.edit().remove(key).apply();
            Log.d(PreferenceConstants.logTAG, e.getMessage());
            e.printStackTrace();
            return defValue;
        }

        return new Object();
    }

    @Override
    public Map<String, ?> getAll() { return mPreferences.getAll(); }

    /**
     * Retrieve the default boolean value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or default value.
     */
    private boolean getBoolDef(String key) {
        return (boolean) getDefault(PreferenceType.BOOL, key, false);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return (boolean) get(PreferenceType.BOOL, key, defValue, null);
    }

    /**
     * Alias's {@link #getBoolean(String, boolean)} w/ defValue = {@link #getBoolDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public boolean getBoolean(String key) { return getBoolean(key, getBoolDef(key)); }

    /** @return The context held. Should be the AppContext. */
    @SuppressWarnings("MethodMayBeStatic") private Context getContext() { return Lazy.mContext; }

    /**
     *
     * @param key
     * @param defValue
     * @return
     */
    public Date getDate(String key, Date defValue) {
        return (Date) get(PreferenceType.DATE, key, defValue, null);
    }

    /**
     * Alias's {@link #getDate(String, Date)} w/ defValue = {@link #getDateDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public Date getDate(String key) { return getDate(key, getDateDef(key)); }

    /**
     * Retrieve the default Date value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or default value.
     */
    @SuppressWarnings("MethodMayBeStatic")
    private Date getDateDef(String key) { return Utils.getNow(); }

    /**
     * Retrieves the default value as defined by the given fallback, a resource, or an annotation
     *
     * @param type     See {@link PreferenceType}
     * @param key      The name of the preference to modify.
     * @param fallback The fallback default value.
     *
     * @return The default value for the given key.
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    private Object getDefault(@PreferenceType int type, String key, Object fallback) {
        Preference annotation = mMap.get(key);
        if (annotation == null || !annotation.hasDefault()) return fallback;
        int resId = annotation.defaultResource();
        boolean hasRes = resId != 0;
        Resources r = getContext().getResources();
        Object rtn;
        switch (type) {
            //@formatter:off
            case PreferenceType.BOOL:
                rtn = hasRes ? r.getBoolean(resId) : annotation.defaultBoolean(); break;
            case PreferenceType.INT:
                rtn = hasRes ? r.getInteger(resId) : annotation.defaultInt(); break;
            case PreferenceType.FLOAT:
                rtn = hasRes ? (float) r.getInteger(resId) : annotation.defaultFloat(); break;
            case PreferenceType.STR:
                rtn = hasRes ? r.getString(resId) : annotation.defaultString(); break;
            case PreferenceType.LONG:
                rtn = hasRes ? (long) r.getInteger(resId) : annotation.defaultLong(); break;
            case PreferenceType.ALL:
            case PreferenceType.DATE:
            case PreferenceType.INVALID:
            case PreferenceType.NULL:
            case PreferenceType.STR_SET:
            default: rtn = new Object();
            //@formatter:on
        }
        return rtn;
    }

    /**
     * getDefault() via reflection. Haven't figured out how to pass the method in... >.>
     *
     * @param key      The name of the preference to modify.
     * @param fallback The fallback default value.
     * @param getRes   The default value set by a resource.
     * @param aNote    The default value set by the annotation.
     *
     * @return The default value: either the given fallback, resource defined, or annotation defined
     */
    private Object getDefault(String key, Object fallback, Method getRes, Method aNote) {
        try {
            Preference annotation = mMap.get(key);
            if (annotation == null || !annotation.hasDefault()) return fallback;
            int resId = annotation.defaultResource();
            if (resId != 0) return getRes.invoke(getContext().getResources(), resId);
            return aNote.invoke(annotation);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new Object();
    }

    @Override
    public float getFloat(String key, float defValue) {
        return (float) get(PreferenceType.FLOAT, key, defValue, null);
    }

    /**
     * Alias's {@link #getFloat(String, float)} w/ defValue = {@link #getFloatDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public float getFloat(String key) { return getFloat(key, getFloatDef(key)); }

    /**
     * Retrieve the default float value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or defValue.
     */
    private float getFloatDef(String key) {
        return (float) getDefault(PreferenceType.FLOAT, key, 0);
    }

    /** @return A SharedPreferences singleton. */
    public static Preferences getInstance() { return Lazy.singleton; }

    /**
     * Sets the context for the Preference to the AppContext. </p>
     * It says that the initialization of Lazy.mContext is not thread safe but I can't see why it
     * isn't.
     *
     * @param context The new context to reference.
     *
     * @return A SharedPreferences singleton.
     */
    public static Preferences getInstance(Context context) {
        if (Lazy.mContext == null) Lazy.mContext = context.getApplicationContext();
        return getInstance();
    }

    @Override
    public int getInt(String key, int defValue) {
        return (int) get(PreferenceType.INT, key, defValue, null);
    }

    /**
     * Alias's {@link #getInt(String, int)} w/ defValue = {@link #getIntDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public int getInt(String key) { return getInt(key, getIntDef(key)); }

    /**
     * Retrieve the default int value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or defValue.
     */
    private int getIntDef(String key) { return (int) getDefault(PreferenceType.INT, key, 0); }

    @Override
    public long getLong(String key, long defValue) {
        return (long) get(PreferenceType.LONG, key, defValue, null);
    }

    /**
     * Alias's {@link #getLong(String, long)} w/ defValue = {@link #getLongDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public long getLong(String key) {
        return (long) get(PreferenceType.LONG, key, getLongDef(key), null);
    }

    /**
     * Retrieve the default long value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or default value.
     */
    private long getLongDef(String key) { return (long) getDefault(PreferenceType.LONG, key, 0L); }

    /** @return Le map. */
    private HashMap<String, Preference> getMap() { return mMap; }

    /**
     * Will set the map once.
     *
     * @param map The map it was destined to be.
     */
    private void setMap(HashMap<String, Preference> map) { if (mMap == null) mMap = map; }

    @Override
    public String getString(String key, String defValue) {
        return (String) get(PreferenceType.STR, key, defValue, null);
    }

    /**
     * Alias's {@link #getString(String, String)} w/ defValue = {@link #getStringDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public String getString(String key) { return getString(key, getStringDef(key)); }

    /**
     * Retrieve the default string value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or defValue.
     */
    private String getStringDef(String key) {
        return (String) getDefault(PreferenceType.STR, key, "");
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        //noinspection unchecked
        return (Set<String>) get(PreferenceType.STR_SET, key, defValues, null);
    }

    /**
     * Alias's {@link #getStringSet(String, Set<String>)} w/
     * defValue = {@link #getStringSetDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public Set<String> getStringSet(String key) { return getStringSet(key, getStringSetDef(key)); }

    /**
     * Retrieve the default int value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or defValue.
     */
    private Set<String> getStringSetDef(String key) {
        //noinspection unchecked
        return (Set<String>) getDefault(PreferenceType.STR_SET, key, 0);
    }

    /**
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     */
    public TimeZone getTimeZone(String key, TimeZone defValue) {
        return (TimeZone) get(PreferenceType.TIME_ZONE, key, defValue, null);
    }

    /**
     * Alias's {@link #getTimeZone(String, TimeZone)} w/ defValue = {@link #getTimeZoneDef(String)}.
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or the predefined default value.
     */
    public TimeZone getTimeZone(String key) { return getTimeZone(key, getTimeZoneDef(key)); }

    /**
     * Retrieve the default TimeZone value from the preferences. See {@link Preference}
     *
     * @param key The name of the preference to retrieve.
     *
     * @return Returns the preference value if it exists, or default value.
     */
    @SuppressWarnings("MethodMayBeStatic")
    private TimeZone getTimeZoneDef(String key) { return TimeZone.getDefault(); }

    /**
     * Reflexively sets up the SharedPreferences from a constants class.
     *
     * @param keysClass The class which holds the default values to insert into Preferences.
     */
    private static void initialize(@NonNull Class<?> keysClass) {
        if (!Lazy.isInitialized) {
            HashMap<String, Preference> map;
            boolean erred = false;
            try {
                Preferences p = getInstance();
                map = new HashMap<>();
                for (Field field : keysClass.getFields()) {
                    Preference pref = field.getAnnotation(Preference.class);
                    if (pref == null) continue;
                    try {
                        map.put((String) field.get(null), pref);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        if (BuildConfig.DEBUG)
                            Log.d(PreferenceConstants.logTAG, "initialize: " + e.getMessage());
                        e.printStackTrace();
                        erred = true;
                    } catch (RuntimeException ignore) {
                        erred = true;
                    }
                }
                // TODO: still set it if it has erred? How to verify Map integrity
                p.setMap(map);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                Lazy.isInitialized = true;
            }
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
        OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
        OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /*
    private interface SharedPreferencesProcessStrategy {
        boolean importValue(JsonParser jsonParser, String key, SharedPreferences.Editor editor)
            throws IOException;
        boolean exportValue(JsonGenerator jsonGenerator, String key, SharedPreferences preferences)
            throws IOException;
    }

    private static void importSharedPreferencesData(@NonNull final ZipFile zipFile,
        @NonNull final Context context,
        @NonNull final String preferencesName, @NonNull final String entryName,
        @NonNull final SharedPreferencesProcessStrategy strategy) throws IOException {
        final ZipEntry entry = zipFile.getEntry(entryName);
        if (entry == null) return;
        final JsonParser jsonParser =
            LoganSquare.JSON_FACTORY.createParser(zipFile.getInputStream(entry));
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
            return;
        }
        final SharedPreferences preferences =
            context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String key = jsonParser.getCurrentName();
            strategy.importValue(jsonParser, key, editor);
        }
        editor.apply();
    }

    private static void exportSharedPreferencesData(@NonNull final ZipOutputStream zos,
        final Context context,
        @NonNull final String preferencesName, @NonNull final String entryName,
        @NonNull final SharedPreferencesProcessStrategy strategy) throws IOException {
        final SharedPreferences preferences =
            context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        final Map<String, ?> map = preferences.getAll();
        zos.putNextEntry(new ZipEntry(entryName));
        final JsonGenerator jsonGenerator = LoganSquare.JSON_FACTORY.createGenerator(zos);
        jsonGenerator.writeStartObject();
        for (String key : map.keySet()) {
            strategy.exportValue(jsonGenerator, key, preferences);
        }
        jsonGenerator.writeEndObject();
        jsonGenerator.flush();
        zos.closeEntry();
    }
    */
}

