package com.fit.tracker.domain;

public final class UserProfile {
    public static final int MIN_HEIGHT_CM = 100;
    public static final int MAX_HEIGHT_CM = 250;
    public static final int MIN_WEIGHT_KG = 30;
    public static final int MAX_WEIGHT_KG = 300;
    public static final UserProfile DEFAULT = new UserProfile(170, 70.0);

    private final int heightCm;
    private final double weightKg;

    public UserProfile(int heightCm, double weightKg) {
        this.heightCm = heightCm;
        this.weightKg = weightKg;
    }

    public boolean isValid() {
        return heightCm >= MIN_HEIGHT_CM && heightCm <= MAX_HEIGHT_CM
                && Double.isFinite(weightKg)
                && weightKg >= MIN_WEIGHT_KG && weightKg <= MAX_WEIGHT_KG;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }
}
