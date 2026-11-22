package com.fit.tracker.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.fit.tracker.R;
import com.fit.tracker.data.Store;
import com.fit.tracker.domain.Calculator;
import com.fit.tracker.domain.Metrics;
import com.fit.tracker.sensor.SensorPicker;
import com.fit.tracker.sensor.StepSensor;

import timber.log.Timber;

public class TrackerService extends Service {
    public static final String ACTION_START = "com.fit.tracker.START";
    public static final String ACTION_STOP = "com.fit.tracker.STOP";
    public static final String ACTION_RESET = "com.fit.tracker.RESET";
    public static final String ACTION_STEPS_CHANGED = "com.fit.tracker.STEPS_CHANGED";
    public static final String ACTION_STATE_CHANGED = "com.fit.tracker.STATE_CHANGED";
    public static final String EXTRA_STEPS = "steps";
    public static final String EXTRA_RUNNING = "running";

    private static final String CHANNEL_ID = "tracking";
    private static final int NOTIFICATION_ID = 1;

    private Store store;
    private StepSensor sensor;
    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        store = new Store(this);
        createChannel();
        Timber.i("Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? ACTION_START : intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopTracking();
            return START_NOT_STICKY;
        }
        if (ACTION_RESET.equals(action)) {
            store.reset();
            publish();
            Timber.i("Session reset");
            return START_STICKY;
        }
        startTracking();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (running) {
            sensor.stop();
        }
        running = false;
        Timber.i("Service destroyed");
        super.onDestroy();
    }

    public static boolean hasActivityPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startTracking() {
        if (running) {
            return;
        }
        sensor = SensorPicker.create(this, hasActivityPermission(this));
        running = true;
        sensor.start(steps -> {
            long total = store.addSteps(steps);
            publish();
            Timber.d("Steps updated: %d", total);
        });
        startForeground(NOTIFICATION_ID, buildNotification());
        broadcastState(true);
        Timber.i("Tracking started");
    }

    private void stopTracking() {
        if (running) {
            sensor.stop();
        }
        running = false;
        stopForeground(true);
        broadcastState(false);
        stopSelf();
        Timber.i("Tracking stopped");
    }

    private void publish() {
        Intent intent = new Intent(ACTION_STEPS_CHANGED).setPackage(getPackageName());
        intent.putExtra(EXTRA_STEPS, store.getSteps());
        sendBroadcast(intent);
        if (running) {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, buildNotification());
        }
    }

    private void broadcastState(boolean active) {
        Intent intent = new Intent(ACTION_STATE_CHANGED).setPackage(getPackageName());
        intent.putExtra(EXTRA_RUNNING, active);
        sendBroadcast(intent);
    }

    private Notification buildNotification() {
        Metrics metrics = Calculator.calculate(store.getSteps());
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text,
                        metrics.getSteps(), metrics.getDistanceMeters() / 1000.0))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
