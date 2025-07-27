package com.example.sympto;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class  MedicineReminderActivity extends AppCompatActivity {

    private EditText etMedicineName;
    private Spinner spinnerFrequency;
    private Button btnSetTime, btnSaveReminder;
    private TextView tvSelectedTime;

    private int selectedHour = -1, selectedMinute = -1;

    private FirebaseFirestore firestore;

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        // Initialize views
        etMedicineName = findViewById(R.id.etMedicineName);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        btnSetTime = findViewById(R.id.btnSetTime);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);

        firestore = FirebaseFirestore.getInstance();

        setupFrequencyDropdown();

        btnSetTime.setOnClickListener(v -> showTimePicker());
        btnSaveReminder.setOnClickListener(v -> saveReminder());

        // Request notification permission for Android 13+
        requestNotificationPermission();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MedicineReminderActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void setupFrequencyDropdown() {
        String[] frequencies = {"Daily", "Every 6 hours", "Every 8 hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(adapter);
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfDay) -> {
            selectedHour = hourOfDay;
            selectedMinute = minuteOfDay;

            String amPm = (selectedHour < 12) ? "AM" : "PM";
            int displayHour = (selectedHour % 12 == 0) ? 12 : selectedHour % 12;
            String time = String.format("%02d:%02d %s", displayHour, selectedMinute, amPm);
            tvSelectedTime.setText("Selected Time: " + time);

        }, hour, minute, false);

        dialog.show();
    }

    private void saveReminder() {
        String medicineName = etMedicineName.getText().toString().trim();
        String frequency = spinnerFrequency.getSelectedItem().toString();

        if (medicineName.isEmpty()) {
            Toast.makeText(this, "Please enter medicine name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHour == -1 || selectedMinute == -1) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("medicineName", medicineName);
        reminderData.put("frequency", frequency);
        reminderData.put("hour", selectedHour);
        reminderData.put("minute", selectedMinute);

        firestore.collection("MedicineReminders")
                .add(reminderData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reminder saved to Firebase!", Toast.LENGTH_SHORT).show();
                    setAlarm(medicineName);
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save reminder.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void setAlarm(String medicineName) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long alarmTimeInMillis = calendar.getTimeInMillis();

        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.putExtra("medicineName", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0, // request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Exact alarms not permitted. Please enable in Settings > Alarms & Reminders.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
            }

            Toast.makeText(this, "Alarm set successfully for " + selectedHour + ":" + selectedMinute, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "AlarmManager not available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etMedicineName.setText("");
        spinnerFrequency.setSelection(0);
        selectedHour = -1;
        selectedMinute = -1;
        tvSelectedTime.setText("No time selected");
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied! Alerts won't show.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}