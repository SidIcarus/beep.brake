package edu.rit.se.beepbrake.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.rit.se.beepbrake.R;

public class Utils {

    public static TextView  mStatusTextView;
    public static ImageView mCpuAniImageView;
    public static int mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;

    public static Resources mResources;
    public static boolean isStatusBarHidden = false;
    public static boolean isToolbarHidden   = false;
    public static int     mSDKVersion       = Build.VERSION.SDK_INT;
    public static boolean isOlderThan16     = mSDKVersion < Build.VERSION_CODES.JELLY_BEAN;
    public static boolean isOlderThan21     = mSDKVersion < Build.VERSION_CODES.LOLLIPOP;
    public static int     OLD_OS_SBAR_HIDE  = WindowManager.LayoutParams.FLAG_FULLSCREEN;
    public static int     NEW_OS_SBAR_HIDE  =
        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_FULLSCREEN;

    public static       int OLD_OS_SBAR_SHOW               = 0; //don't know what it is
    public static       int NEW_OS_SBAR_SHOW               = 0;// don't know what it is
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
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

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public static void toggleHideyBar(View decorView) {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions    = decorView.getSystemUiVisibility();
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

    @TargetApi(21)
    public static void setToolbarTransparent(Window window) {
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

        int uiMode         = res.getConfiguration().uiMode;
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

    public static String getAppVersion(Context ctx) {
        String appVersion = "0.0.0";
        try {
            appVersion = ctx.getPackageManager().getPackageInfo(
                ctx.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    public static void makeTransparentStatusBar(Activity activity) {
        // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static String resToName(Resources res, int id) { return res.getResourceEntryName(id); }

    public static String getWritePath(Context ctx) {
        Preferences p        = Preferences.getInstance();
        int         wDirID   = R.string.write_directory;
        String      wDirName = resToName(ctx.getResources(), wDirID);

        String wDir  = p.getString(wDirName);
        String wPath = p.getString("write_path", ctx.getFilesDir().getPath());

        return wPath + wDir;
    }

    //public static String resToName(Resources res, int id) { return res.getResourceEntryName(id); }

    // I think i can delete this
    public static void closeQuietly(Closeable input) {
        try {
            if (input != null) { input.close(); }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String getFileExtension(File file) {
        if (file == null || !file.isFile()) return "";
        String fileName = file.getName();

        int index = fileName.lastIndexOf('.');
        return (index != -1 && index != 0) ? fileName.substring(index + 1) : "";
    }

    public static String getFileExtension(String file) {
        if (file == null || file.isEmpty()) return "";

        int index = file.lastIndexOf('.');
        return (index != -1 && index != 0) ? file.substring(index + 1) : "";
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("System.Utils", "Directory not created");
        }
        return file;
    }

    public static Date getNow() { return new Date(System.currentTimeMillis()); }

    public static void requestPermissionForCamera(Context context, Activity activity) {
        requestPermissionFor(context, activity, Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private static void requestPermissionFor(Context context, Activity activity, String permission,
        int reqCode) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        if (result != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{permission}, reqCode);
    }

}
