package edu.rit.se.beepbrake.utils;

import android.content.Context;

import edu.rit.se.beepbrake.R;

public class PreferencesHelper {

    public static String getWritePath(Context ctx) {
        int wDirID = R.string.write_directory;
        String defaultWDir = ctx.getString(wDirID);
        String wDirName = Utils.resToName(ctx.getResources(), wDirID);
        String wDir = Preferences.getSetting(ctx, wDirName, defaultWDir);
        String wPath = Preferences.getSetting(ctx, "write_path", ctx.getFilesDir().getPath());

        return wPath + wDir;
    }
}


