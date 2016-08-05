package com.ebanswers.smartlib.util;

import android.util.Log;

/**
 * Created by Callanna on 2016/8/3.
 */
public class LogUtil {
    private static final String TAG = "SmartBot";
    public static boolean isDebug = true;

    public static void d(String s) {
        if (isDebug) {
            Log.d(TAG, s);
        }
    }

    public static void v(String s) {
        if (isDebug) {
            Log.v(TAG, s);
        }
    }

    public static void i(String s) {
        if (isDebug) {
            Log.i(TAG, s);
        }
    }

    public static void e(String s) {
        if (isDebug) {
            Log.e(TAG, s);
        }
    }

    public static void w(String s) {
        if (isDebug) {
            Log.w(TAG, s);
        }
    }
}
