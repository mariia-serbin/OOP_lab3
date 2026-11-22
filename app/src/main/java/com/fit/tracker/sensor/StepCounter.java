package com.fit.tracker.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import timber.log.Timber;

public class StepCounter implements StepSensor, SensorEventListener {
    private final SensorManager manager;
    private Listener listener;
    private float lastValue = -1f;

    public StepCounter(SensorManager manager) {
        this.manager = manager;
    }

    @Override
    public void start(Listener listener) {
        this.listener = listener;
        this.lastValue = -1f;
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            Timber.w("Step counter unavailable");
            return;
        }
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        Timber.i("Step counter started");
    }

    @Override
    public void stop() {
        manager.unregisterListener(this);
        listener = null;
        Timber.i("Step counter stopped");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        if (lastValue < 0f) {
            lastValue = value;
            return;
        }
        if (value < lastValue) {
            lastValue = value;
            Timber.i("Step counter reset");
            return;
        }

        int steps = (int) (value - lastValue);
        lastValue = value;
        if (steps > 0 && listener != null) {
            listener.onSteps(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
