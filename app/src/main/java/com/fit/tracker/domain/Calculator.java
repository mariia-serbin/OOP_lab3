package com.fit.tracker.domain;

public final class Calculator {
    private static final double STEP_LENGTH_TO_HEIGHT = 0.415;
    private static final double ASSUMED_CADENCE_STEPS_PER_MINUTE = 100.0;
    private static final double MIN_SPEED_KMH = 2.0;
    private static final double MAX_SPEED_KMH = 10.0;
    private static final double MET_DIVISOR = 35.0;
    private static final double VO2_PER_MET = 3.5;
    private static final double MINUTES_PER_HOUR = 60.0;
    private static final double MILLIS_PER_MINUTE = 60_000.0;

    private Calculator() {
    }

    public static Metrics calculate(long steps, UserProfile profile, long activeMs) {
        long safeSteps = Math.max(0L, steps);
        if (safeSteps == 0) {
            return Metrics.EMPTY;
        }

        UserProfile safeProfile = profile != null && profile.isValid() ? profile : UserProfile.DEFAULT;
        double stepLengthMeters = safeProfile.getHeightCm() / 100.0 * STEP_LENGTH_TO_HEIGHT;
        double distanceMeters = safeSteps * stepLengthMeters;
        double distanceKm = distanceMeters / 1000.0;

        double minutes = Math.max(0L, activeMs) / MILLIS_PER_MINUTE;
        if (minutes <= 0.0) {
            minutes = safeSteps / ASSUMED_CADENCE_STEPS_PER_MINUTE;
        }

        double speedKmh = distanceKm * MINUTES_PER_HOUR / minutes;
        speedKmh = Math.max(MIN_SPEED_KMH, Math.min(MAX_SPEED_KMH, speedKmh));
        double metersPerMinute = speedKmh * 1000.0 / MINUTES_PER_HOUR;
        double met = 1.0 + metersPerMinute / MET_DIVISOR;
        double calories = met * VO2_PER_MET * safeProfile.getWeightKg() / 200.0 * minutes;

        return new Metrics(safeSteps, calories, distanceMeters);
    }
}
