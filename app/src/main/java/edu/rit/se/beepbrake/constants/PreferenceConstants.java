package edu.rit.se.beepbrake.constants;

import android.annotation.TargetApi;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.rit.se.beepbrake.R;

/**
 * @author Alvaro Pareja-Lecaros
 * @date 05.07.2016
 */
public interface PreferenceConstants {

    String logTAG = "System.Preferences";

    String setAllErrMsg = "You cannot set all settings. Invalid use of PreferenceType.ALL";

    int[] PRESET_COLORS = {
        R.color.material_dark, R.color.material_light, R.color.material_red, R.color.material_pink,
        R.color.material_purple, R.color.material_deep_purple, R.color.material_indigo,
        R.color.material_blue, R.color.material_light_blue, R.color.material_cyan,
        R.color.material_teal, R.color.material_green, R.color.material_light_green,
        R.color.material_lime, R.color.material_yellow, R.color.material_amber,
        R.color.material_orange, R.color.material_deep_orange};


    // Info from android.os.Build
    HashMap<String, String> BUILD_DEVICE = new HashMap<String, String>() {{

        // The name of the underlying board, like "goldfish".
        put("board", Build.BOARD);

        // The system bootloader version number.
        put("bootloader", Build.BOOTLOADER);

        // The brand (e.g., carrier) the software is customized for, if any.
        put("brand", Build.BRAND);

        // The name of the industrial design.
        put("device", Build.DEVICE);

        // A build ID item meant for displaying to the user
        put("display", Build.DISPLAY);

        // A item that uniquely identifies this build.
        put("fingerprint", Build.FINGERPRINT);

        // The name of the hardware (from the kernel command line or /proc).
        put("hardware", Build.HARDWARE);

        // Either a changelist number, or a label like "M4-rc20".
        put("id", Build.ID);

        // The manufacturer of the product/hardware.
        put("manufacturer", Build.MANUFACTURER);

        // The end-user-visible name for the end product.
        put("model", Build.MODEL);

        // The name of the overall product.
        put("product", Build.PRODUCT);

        // The current development codename, or the item "REL" if this is a release build.
        put("os_version", Build.VERSION.RELEASE);

        // The radio firmware version number.
        put("radio", Build.getRadioVersion());

        // Comma-separated tags describing the build, like "unsigned,debug".
        put("tags", Build.TAGS);

        // The type of build, like "user" or "eng".
        put("type", Build.TYPE);

        put("default", "default value");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            put("cpu_abi_32", Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
            put("cpu_abi_64", Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
        } else {
            put("cpu_abi", Build.CPU_ABI);
            put("cpu_abi2", Build.CPU_ABI2);
        }
    }};
}
