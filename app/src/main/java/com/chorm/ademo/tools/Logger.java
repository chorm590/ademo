package com.chorm.ademo.tools;

import android.util.Log;


public class Logger {

    private static final String TAG = "chorm";

    private enum LEVEL {
        DEBUG, INFO, WARNING, ERROR
    };

    private static final int LOG_LEVEL = LEVEL.DEBUG.ordinal();

    public static void debug(String msg){
        debug(TAG, msg);
    }

    public static void debug(String tag, String msg){
        if(LOG_LEVEL == LEVEL.DEBUG.ordinal())
            Log.d(tag, msg);
    }

    public static void error(String msg){
        error(TAG, msg);
    }

    public static void error(String tag, String msg){
        if(LOG_LEVEL <= LEVEL.ERROR.ordinal())
            Log.e(tag, msg);
    }
}
