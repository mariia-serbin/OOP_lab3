package com.fit.tracker.util;

import android.annotation.SuppressLint;
import android.util.Log;

import timber.log.Timber;

@SuppressLint("LogNotTimber")
public class ReleaseTree extends Timber.Tree {
    private static final String DEFAULT_TAG = "FitTracker";

    @Override
    protected void log(int priority, String tag, String message, Throwable error) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        String logTag = tag == null ? DEFAULT_TAG : tag;
        Log.println(priority, logTag, message);

        if (error != null) {
            Log.e(logTag, message, error);
        }
    }
}
