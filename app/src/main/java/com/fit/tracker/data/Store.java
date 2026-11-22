package com.fit.tracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.fit.tracker.domain.UserProfile;

public class Store {
    private static final String NAME = "tracker";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_HEIGHT_CM = "height_cm";
    private static final String KEY_WEIGHT_KG = "weight_kg";
    private static final String KEY_PROFILE_PROMPTED = "profile_prompted";
    private static final String KEY_ACTIVE_MS = "active_ms";
    private static final String KEY_LAST_STEP_TIME_MS = "last_step_time_ms";

    private final SharedPreferences preferences;

    public Store(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public synchronized long getSteps() {
        return preferences.getLong(KEY_STEPS, 0L);
    }

    public synchronized long addSteps(int steps, long durationMs, long eventTimeMs) {
        long total = Math.max(0L, getSteps() + Math.max(0, steps));
        long activeMs = getActiveMs() + Math.max(0L, durationMs);
        preferences.edit()
                .putLong(KEY_STEPS, total)
                .putLong(KEY_ACTIVE_MS, activeMs)
                .putLong(KEY_LAST_STEP_TIME_MS, eventTimeMs)
                .apply();
        return total;
    }

    public synchronized long getActiveMs() {
        return preferences.getLong(KEY_ACTIVE_MS, 0L);
    }

    public synchronized long getLastStepTimeMs() {
        return preferences.getLong(KEY_LAST_STEP_TIME_MS, 0L);
    }

    public synchronized void setLastStepTimeMs(long timeMs) {
        preferences.edit().putLong(KEY_LAST_STEP_TIME_MS, timeMs).apply();
    }

    public synchronized void reset() {
        preferences.edit()
                .putLong(KEY_STEPS, 0L)
                .putLong(KEY_ACTIVE_MS, 0L)
                .putLong(KEY_LAST_STEP_TIME_MS, 0L)
                .apply();
    }

    public synchronized UserProfile getProfile() {
        UserProfile profile = storedProfile();
        return profile.isValid() ? profile : UserProfile.DEFAULT;
    }

    public synchronized boolean hasProfile() {
        return storedProfile().isValid();
    }

    private UserProfile storedProfile() {
        return new UserProfile(
                preferences.getInt(KEY_HEIGHT_CM, 0),
                preferences.getFloat(KEY_WEIGHT_KG, 0f));
    }

    public synchronized void saveProfile(UserProfile profile) {
        if (!profile.isValid()) {
            throw new IllegalArgumentException("Invalid user profile");
        }
        preferences.edit()
                .putInt(KEY_HEIGHT_CM, profile.getHeightCm())
                .putFloat(KEY_WEIGHT_KG, (float) profile.getWeightKg())
                .apply();
    }

    public synchronized boolean shouldPromptProfile() {
        return !hasProfile() && !preferences.getBoolean(KEY_PROFILE_PROMPTED, false);
    }

    public synchronized void markProfilePrompted() {
        preferences.edit().putBoolean(KEY_PROFILE_PROMPTED, true).apply();
    }
}
