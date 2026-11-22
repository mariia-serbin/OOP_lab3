package com.fit.tracker;

import com.fit.tracker.domain.Calculator;
import com.fit.tracker.domain.Metrics;
import com.fit.tracker.domain.UserProfile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorTest {
    private static final double DELTA = 0.0001;
    private static final UserProfile PROFILE = new UserProfile(170, 70.0);

    @Test
    public void zeroStepsProducesEmptyMetrics() {
        Metrics metrics = Calculator.calculate(0, PROFILE, 600_000L);

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getCalories(), DELTA);
        assertEquals(0, metrics.getDistanceMeters(), DELTA);
    }

    @Test
    public void negativeStepsAreClampedToZero() {
        Metrics metrics = Calculator.calculate(-10, PROFILE, 600_000L);

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getCalories(), DELTA);
        assertEquals(0, metrics.getDistanceMeters(), DELTA);
    }

    @Test
    public void distanceDependsOnHeight() {
        Metrics metrics = Calculator.calculate(100, new UserProfile(180, 70.0), 60_000L);

        assertEquals(100, metrics.getSteps());
        assertEquals(74.7, metrics.getDistanceMeters(), DELTA);
    }

    @Test
    public void caloriesUseMetFormula() {
        Metrics metrics = Calculator.calculate(1000, PROFILE, 600_000L);

        assertEquals(1000, metrics.getSteps());
        assertEquals(705.5, metrics.getDistanceMeters(), DELTA);
        assertEquals(36.9425, metrics.getCalories(), DELTA);
    }

    @Test
    public void caloriesDependOnWeight() {
        Metrics metrics = Calculator.calculate(1000, new UserProfile(170, 90.0), 600_000L);

        assertEquals(705.5, metrics.getDistanceMeters(), DELTA);
        assertEquals(47.4975, metrics.getCalories(), DELTA);
    }

    @Test
    public void missingTimeUsesAssumedCadence() {
        Metrics metrics = Calculator.calculate(1000, PROFILE, 0L);

        assertEquals(36.9425, metrics.getCalories(), DELTA);
    }

    @Test
    public void invalidProfileFallsBackToDefault() {
        Metrics metrics = Calculator.calculate(1000, new UserProfile(0, 0), 600_000L);

        assertEquals(705.5, metrics.getDistanceMeters(), DELTA);
        assertEquals(36.9425, metrics.getCalories(), DELTA);
    }
}
