package com.fit.tracker.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import timber.log.Timber;

public class MotionSensor implements StepSensor, SensorEventListener {
    private static final double THRESHOLD = 12.5;
    private static final long MIN_INTERVAL_MS = 250L;

    private final SensorManager manager;
    private final Detector detector = new Detector();
    private Listener listener;

    public MotionSensor(SensorManager manager) {
        this.manager = manager;
    }

    @Override
    public void start(Listener listener) {
        this.listener = listener;
        detector.reset();
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            Timber.w("Accelerometer unavailable");
            return;
        }
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        Timber.i("Motion sensor started");
    }

    @Override
    public void stop() {
        manager.unregisterListener(this);
        listener = null;
        Timber.i("Motion sensor stopped");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        long timeMillis = event.timestamp / 1_000_000L;

        if (detector.detect(magnitude, timeMillis) && listener != null) {
            listener.onSteps(1);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static class Detector {
        private long lastStepTime = -MIN_INTERVAL_MS;

        public boolean detect(double magnitude, long timeMillis) {
            if (magnitude <= THRESHOLD || timeMillis - lastStepTime < MIN_INTERVAL_MS) {
                return false;
            }
            lastStepTime = timeMillis;
            return true;
        }

        public void reset() {
            lastStepTime = -MIN_INTERVAL_MS;
        }
    }
}
