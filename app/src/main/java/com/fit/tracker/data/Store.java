package com.fit.tracker.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Store {
    private static final String NAME = "tracker";
    private static final String KEY_STEPS = "steps";

    private final SharedPreferences preferences;

    public Store(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public synchronized long getSteps() {
        return preferences.getLong(KEY_STEPS, 0L);
    }

    public synchronized long addSteps(int steps) {
        long total = Math.max(0L, getSteps() + steps);
        preferences.edit().putLong(KEY_STEPS, total).apply();
        return total;
    }

    public synchronized void reset() {
        preferences.edit().putLong(KEY_STEPS, 0L).apply();
    }
}
