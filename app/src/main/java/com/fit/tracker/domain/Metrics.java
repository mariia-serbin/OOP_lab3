package com.fit.tracker.domain;

public class Metrics {
    public static final Metrics EMPTY = new Metrics(0, 0, 0);

    private final long steps;
    private final double calories;
    private final double distanceMeters;

    public Metrics(long steps, double calories, double distanceMeters) {
        this.steps = steps;
        this.calories = calories;
        this.distanceMeters = distanceMeters;
    }

    public long getSteps() {
        return steps;
    }

    public double getCalories() {
        return calories;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }
}
