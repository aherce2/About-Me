package edu.illinois.cs465.lockedin.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.ManagePreferences;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private ManageSession manageSession;

    @Override
    public void onCreate() {
        super.onCreate();
        manageSession = new ManageSession(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        // Start the foreground service with a persistent notification
        Notification notification = new NotificationCompat.Builder(this, "BreakNotificationChannel")
                .setContentTitle("Break Notification Service")
                .setContentText("Monitoring for scheduled breaks.")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
        startForeground(1, notification);

        // Start a background thread to monitor and schedule notifications
        new Thread(() -> {
            while (true) {
                manageSession.scheduleBreakNotifications(this);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources here if needed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Break Notification Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "BreakNotificationChannel",
                    "Break Notifications",
                    NotificationManager.IMPORTANCE_NONE
            );
            channel.setDescription("Channel for break notification service.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

}
