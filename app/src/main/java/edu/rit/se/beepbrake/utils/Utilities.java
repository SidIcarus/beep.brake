package edu.rit.se.beepbrake.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.Resources;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import edu.rit.se.beepbrake.R;

public class Utilities {

    public static TextView mStatusTextView;
    public static ImageView mCpuAniImageView;
    public static int mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;

    public static Resources mResources;
    public static boolean isStatusBarHidden = false;
    public static boolean isToolbarHidden = false;
    public static int mSDKVersion = Build.VERSION.SDK_INT;
    public static boolean isOlderThan16 = mSDKVersion < Build.VERSION_CODES.JELLY_BEAN;
    public static int OLD_OS_SBAR_HIDE = WindowManager.LayoutParams.FLAG_FULLSCREEN;
    public static int NEW_OS_SBAR_HIDE =
        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_FULLSCREEN;

    public static int OLD_OS_SBAR_SHOW = 0; //don't know what it is
    public static int NEW_OS_SBAR_SHOW = 0;// don't know what it is
    /*
    View.getContext(): Returns the context the view is currently running in. Usually the
    currently active Activity.

    Activity.getApplicationContext(): Returns the context for the entire application (the process
     all the Activities are running inside of). Use this instead of the current Activity context
     if you need a context tied to the lifecycle of the entire application, not just the current
     Activity.

    ContextWrapper.getBaseContext(): If you need access to a Context from within another context,
     you use a ContextWrapper. The Context referred to from inside that ContextWrapper is
     accessed via getBaseContext().
    */

    /** Detects and toggles immersive mode (also known as "hidey bar" mode). */
    public static void toggleHideyBar(View decorView) {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = decorView.getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
            ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        // String isImmersiveModeEnabledStr = "Turning immersive mode mode ";
        // isImmersiveModeEnabledStr += (isImmersiveModeEnabled) ? "off." : "on.";
        // Log.i(TAG, isImmersiveModeEnabledStr);

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (mSDKVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        // Status bar hiding: Backwards compatible to Jellybean
        if (mSDKVersion >= Build.VERSION_CODES.JELLY_BEAN)
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (mSDKVersion >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        decorView.setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    @TargetApi(21) public static void setToolbarTransparent(Window window) {
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @TargetApi(19)
    protected static void setStatusBarTranslucent(Window window, boolean makeTranslucent) {
        int translucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (makeTranslucent) window.addFlags(translucentStatus);
        else window.clearFlags(translucentStatus);
    }

    public static void hideStatusBar(Window window) { hideStatusBar(window, null); }

    // should be done before setting the content view
    public static void hideStatusBar(Window window, Toolbar toolbar) {
        if (isOlderThan16) window.setFlags(OLD_OS_SBAR_HIDE, OLD_OS_SBAR_HIDE);
        else window.getDecorView().setSystemUiVisibility(NEW_OS_SBAR_HIDE);

        if (toolbar != null) toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // needs a check to see if it is currently visible
    public static void resumeAnimatable() {
        Drawable drawable = mCpuAniImageView.getDrawable();
        if (drawable instanceof Animatable) { ((Animatable) drawable).start(); }
    }

    // needs a check to see if it is currently visible
    public static void resumeNightMode(Resources res) {

        int uiMode = res.getConfiguration().uiMode;
        int dayNightUiMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (dayNightUiMode == Configuration.UI_MODE_NIGHT_NO) {
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_NO;
            mStatusTextView.setText(R.string.text_for_day_night_mode_night_no);
        } else if (dayNightUiMode == Configuration.UI_MODE_NIGHT_YES) {
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_YES;
            mStatusTextView.setText(R.string.text_for_day_night_mode_night_yes);
        } else {
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;
            mStatusTextView.setText(R.string.text_for_day_night_mode_night_auto);
        }
    }

    private static final String PREFERENCES_FILE = "materialsample_settings";

    public static int getToolbarHeight(Context context) {
        int height = (int) context.getResources()
                                  .getDimension(R.dimen.abc_action_bar_default_height_material);
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int height = (int) context.getResources().getDimension(R.dimen.statusbar_size);
        return height;
    }

    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    private static Object readSharedSetting(Context ctx, String settingName,
        @SharedPreferenceTypes int settingType, Object defaultValue) {
        SharedPreferences sharedPref =
            ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        try {
            switch (settingType) {
                case SharedPreferenceTypes.INT:
                    return sharedPref.getInt(settingName, (int) defaultValue);
                case SharedPreferenceTypes.STRING:
                    return sharedPref.getString(settingName, (String) defaultValue);
                case SharedPreferenceTypes.FLOAT:
                    return sharedPref.getFloat(settingName, (float) defaultValue);
                case SharedPreferenceTypes.BOOLEAN:
                    return sharedPref.getBoolean(settingName, (boolean) defaultValue);
                case SharedPreferenceTypes.LONG:
                    return sharedPref.getLong(settingName, (long) defaultValue);
                case SharedPreferenceTypes.STRING_SET:
                    return sharedPref.getStringSet(settingName, (Set<String>) defaultValue);
            }
        } catch (ClassCastException e) { }

        return new Object();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        return (String) readSharedSetting(ctx, settingName, SharedPreferenceTypes.STRING,
            defaultValue);
    }

    public static Boolean readSharedSetting(Context ctx, String settingName, Boolean defaultValue) {
        return (Boolean) readSharedSetting(ctx, settingName, SharedPreferenceTypes.BOOLEAN,
            defaultValue);
    }

    public static Long readSharedSetting(Context ctx, String settingName, Long defaultValue) {
        return (Long) readSharedSetting(ctx, settingName, SharedPreferenceTypes.LONG, defaultValue);
    }

    public static Integer readSharedSetting(Context ctx, String settingName, Integer defaultValue) {
        return (Integer) readSharedSetting(ctx, settingName, SharedPreferenceTypes.INT,
            defaultValue);
    }

    public static Float readSharedSetting(Context ctx, String settingName, Float defaultValue) {
        return (Float) readSharedSetting(ctx, settingName, SharedPreferenceTypes.FLOAT,
            defaultValue);
    }

    public static Set<String> readSharedSetting(Context ctx, String settingName,
        Set<String> defaultValue) {
        return (Set<String>) readSharedSetting(ctx, settingName, SharedPreferenceTypes.STRING_SET,
            defaultValue);
    }

    public static Boolean getEULAStatus(Context ctx) {
        return readSharedSetting(ctx, "EULA_STATUS", false);
    }

    @IntDef({
                SharedPreferenceTypes.INT, SharedPreferenceTypes.STRING,
                SharedPreferenceTypes.FLOAT, SharedPreferenceTypes.BOOLEAN,
                SharedPreferenceTypes.LONG, SharedPreferenceTypes.STRING_SET})
    @Retention(RetentionPolicy.SOURCE) public @interface SharedPreferenceTypes {
        int INT = 0, STRING = 1, FLOAT = 2, BOOLEAN = 3, LONG = 4, STRING_SET = 5;
    }

    public static void showEULASnack(Context ctx, CoordinatorLayout cLayout){
        String eStatus = getEULAStatus(ctx).toString();
        Snackbar snackbar = Snackbar.make(cLayout, "EULA_STATUS: " +eStatus, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showSettingSnack(CoordinatorLayout cLayout, String settingName) {
        Snackbar snackbar = Snackbar.make(cLayout, settingName+ ": ", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void setEULAStatus(Context ctx, boolean status) {
        Utilities.saveSharedSetting(ctx, "EULA_STATUS", Utilities.SharedPreferenceTypes.BOOLEAN, status);
    }

    public static void saveSharedSetting(Context ctx, String settingName,
        @SharedPreferenceTypes int settingType, Object settingValue) {
        SharedPreferences sharedPref =
            ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

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
}
