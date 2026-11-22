package com.fit.tracker.sensor;

public interface StepSensor {
    void start(Listener listener);

    void stop();

    interface Listener {
        void onSteps(int steps);
    }
}
