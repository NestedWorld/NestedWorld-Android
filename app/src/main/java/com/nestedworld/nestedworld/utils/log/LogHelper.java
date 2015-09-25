package com.nestedworld.nestedworld.utils.log;

import com.nestedworld.nestedworld.BuildConfig;

import android.util.Log;

public class LogHelper {
    static final boolean IS_ENABLE = BuildConfig.DEBUG;

    private LogHelper() {
    }

    public static void v(String tag, String msg) {
        if (IS_ENABLE)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (IS_ENABLE)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (IS_ENABLE)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (IS_ENABLE)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (IS_ENABLE)
            Log.e(tag, msg);
    }
}
