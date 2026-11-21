package com.fit.tracker;

import com.fit.tracker.domain.Calculator;
import com.fit.tracker.domain.Metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorTest {
    private static final double DELTA = 0.0001;

    @Test
    public void zeroStepsProducesEmptyMetrics() {
        Metrics metrics = Calculator.calculate(0);

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getCalories(), DELTA);
        assertEquals(0, metrics.getDistanceMeters(), DELTA);
    }

    @Test
    public void thousandStepsProducesExpectedMetrics() {
        Metrics metrics = Calculator.calculate(1000);

        assertEquals(1000, metrics.getSteps());
        assertEquals(34.125, metrics.getCalories(), DELTA);
        assertEquals(750, metrics.getDistanceMeters(), DELTA);
    }

    @Test
    public void negativeStepsAreClampedToZero() {
        Metrics metrics = Calculator.calculate(-10);

        assertEquals(0, metrics.getSteps());
        assertEquals(0, metrics.getCalories(), DELTA);
        assertEquals(0, metrics.getDistanceMeters(), DELTA);
    }
}
