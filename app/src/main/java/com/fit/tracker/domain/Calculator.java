package com.fit.tracker.domain;

public final class Calculator {
    private static final double STRIDE_LENGTH_METERS = 0.75;
    private static final double WEIGHT_KG = 70.0;
    private static final double CALORIES_PER_KG_PER_KM = 0.65;

    private Calculator() {
    }

    public static Metrics calculate(long steps) {
        long safeSteps = Math.max(0L, steps);
        double distanceMeters = safeSteps * STRIDE_LENGTH_METERS;
        double distanceKm = distanceMeters / 1000.0;
        double calories = distanceKm * WEIGHT_KG * CALORIES_PER_KG_PER_KM;
        return new Metrics(safeSteps, calories, distanceMeters);
    }
}
