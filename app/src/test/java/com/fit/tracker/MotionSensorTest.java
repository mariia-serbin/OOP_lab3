package com.fit.tracker;

import com.fit.tracker.sensor.MotionSensor;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MotionSensorTest {
    @Test
    public void peakAboveThresholdIsDetected() {
        MotionSensor.Detector detector = new MotionSensor.Detector();

        assertTrue(detector.detect(13.0, 1000L));
    }

    @Test
    public void peakBelowThresholdIsIgnored() {
        MotionSensor.Detector detector = new MotionSensor.Detector();

        assertFalse(detector.detect(11.0, 1000L));
    }

    @Test
    public void peaksTooCloseTogetherAreIgnored() {
        MotionSensor.Detector detector = new MotionSensor.Detector();
        detector.detect(13.0, 1000L);

        assertFalse(detector.detect(13.0, 1100L));
    }

    @Test
    public void peakAfterIntervalIsDetected() {
        MotionSensor.Detector detector = new MotionSensor.Detector();
        detector.detect(13.0, 1000L);

        assertTrue(detector.detect(13.0, 1300L));
    }

    @Test
    public void resetAllowsImmediateDetection() {
        MotionSensor.Detector detector = new MotionSensor.Detector();
        detector.detect(13.0, 1000L);
        detector.reset();

        assertTrue(detector.detect(13.0, 1100L));
    }
}
