package com.fit.tracker.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import timber.log.Timber;

public final class SensorPicker {
    private SensorPicker() {
    }

    public static StepSensor create(Context context) {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (manager == null) {
            throw new IllegalStateException("SensorManager unavailable");
        }

        if (manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            Timber.i("Step counter selected");
            return new StepCounter(manager);
        }

        Timber.i("Accelerometer selected");
        return new MotionSensor(manager);
    }
}
