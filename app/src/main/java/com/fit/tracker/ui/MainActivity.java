package com.fit.tracker.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fit.tracker.R;
import com.fit.tracker.data.Store;
import com.fit.tracker.domain.Calculator;
import com.fit.tracker.domain.Metrics;
import com.fit.tracker.service.TrackerService;
import com.fit.tracker.util.LocaleHelper;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String> activityPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    requestPermissionsAndStart();
                } else {
                    showDenied();
                }
            });

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    showDenied();
                }
                startTracking();
            });

    private final BroadcastReceiver stepsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            render(intent.getLongExtra(TrackerService.EXTRA_STEPS, 0L));
        }
    };

    private final BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setRunning(intent.getBooleanExtra(TrackerService.EXTRA_RUNNING, false));
        }
    };

    private TextView stepsValue;
    private TextView caloriesValue;
    private TextView distanceValue;
    private TextView statusText;
    private Button startButton;
    private Button stopButton;
    private RadioGroup languageGroup;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepsValue = findViewById(R.id.stepsValue);
        caloriesValue = findViewById(R.id.caloriesValue);
        distanceValue = findViewById(R.id.distanceValue);
        statusText = findViewById(R.id.statusText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        languageGroup = findViewById(R.id.languageGroup);

        store = new Store(this);
        render(store.getSteps());
        setRunning(false);

        int checked = LocaleHelper.ENGLISH.equals(LocaleHelper.current())
                ? R.id.languageEn
                : R.id.languageUk;
        languageGroup.check(checked);
        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String language = checkedId == R.id.languageEn
                    ? LocaleHelper.ENGLISH
                    : LocaleHelper.UKRAINIAN;
            LocaleHelper.apply(language);
            Timber.i("Language changed: %s", language);
        });

        startButton.setOnClickListener(view -> requestPermissionsAndStart());
        stopButton.setOnClickListener(view -> {
            startService(new Intent(this, TrackerService.class).setAction(TrackerService.ACTION_STOP));
            setRunning(false);
        });
        findViewById(R.id.resetButton).setOnClickListener(view -> {
            startService(new Intent(this, TrackerService.class).setAction(TrackerService.ACTION_RESET));
            render(0L);
        });

        Timber.i("MainActivity created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ContextCompat.registerReceiver(this, stepsReceiver,
                new IntentFilter(TrackerService.ACTION_STEPS_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED);
        ContextCompat.registerReceiver(this, stateReceiver,
                new IntentFilter(TrackerService.ACTION_STATE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(stepsReceiver);
        unregisterReceiver(stateReceiver);
        super.onStop();
    }

    private void requestPermissionsAndStart() {
        if (!TrackerService.hasActivityPermission(this)) {
            requestActivityPermission();
            return;
        }
        if (!TrackerService.hasNotificationPermission(this)) {
            requestNotificationPermission();
            return;
        }
        startTracking();
    }

    private void requestActivityPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.activity_permission_title)
                    .setMessage(R.string.activity_permission_message)
                    .setPositiveButton(android.R.string.ok,
                            (dialog, which) -> activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION))
                    .show();
            return;
        }
        activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
    }

    private void requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.notifications_permission_title)
                    .setMessage(R.string.notifications_permission_message)
                    .setPositiveButton(android.R.string.ok,
                            (dialog, which) -> notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS))
                    .show();
            return;
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void startTracking() {
        Intent intent = new Intent(this, TrackerService.class).setAction(TrackerService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        setRunning(true);
        Timber.i("Tracking requested");
    }

    private void showDenied() {
        if (statusText != null) {
            statusText.setText(R.string.permission_denied);
        }
    }

    private void setRunning(boolean active) {
        startButton.setEnabled(!active);
        stopButton.setEnabled(active);
        statusText.setText(active ? R.string.tracking_active : R.string.tracking_stopped);
    }

    private void render(long steps) {
        Metrics metrics = Calculator.calculate(steps);
        stepsValue.setText(getString(R.string.steps_value, metrics.getSteps()));
        caloriesValue.setText(getString(R.string.calories_value, metrics.getCalories()));
        distanceValue.setText(getString(R.string.distance_value, metrics.getDistanceMeters() / 1000.0));
    }
}
