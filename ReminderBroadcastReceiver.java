package com.example.sympto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderBroadcast";
    private static final String CHANNEL_ID = "MedicineReminderChannel";
    private static final int NOTIFICATION_ID = 1001; // Use unique ID or generate random for multiple

    @Override
    public void onReceive(Context context, Intent intent) {

        String medicineName = intent.getStringExtra("medicineName");

        if (medicineName == null || medicineName.isEmpty()) {
            medicineName = "your medicine";
        }

        Log.d(TAG, "Alarm received! Showing notification for: " + medicineName);

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)  // ✅ Ensure ic_reminder.png exists in drawable folder!
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take your medicine: " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Notification displayed successfully!");
        } else {
            Log.e(TAG, "NotificationManager is NULL! Notification failed!");
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Medicine Reminder Channel";
            String description = "Channel for Medicine Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created!");
            } else {
                Log.e(TAG, "NotificationManager is NULL when creating channel!");
            }
        }
    }
}
