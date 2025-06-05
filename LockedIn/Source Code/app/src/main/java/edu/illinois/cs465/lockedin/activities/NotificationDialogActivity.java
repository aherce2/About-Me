package edu.illinois.cs465.lockedin.activities;

import android.content.Context;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.fragments.EditSessionDialog;
import edu.illinois.cs465.lockedin.adapters.AppSelectionAdapter;
import edu.illinois.cs465.lockedin.models.AppItem;

public class NotificationDialogActivity extends AppCompatActivity {
    private int sessionId;
    private String sessionTime;
    private  String sessionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String sessionName = getIntent().getStringExtra("sessionName");
        String sessionTime = getIntent().getStringExtra("sessionTime");

        boolean breakTime = getIntent().getBooleanExtra("breakTime", false);
        Log.d("breaktime", String.valueOf(isBreakTime(breakTime)));

//        sessionName = getIntent().getStringExtra("sessionName");
//        sessionTime = getIntent().getStringExtra("sessionTime");
        sessionName = getIntent().getStringExtra("sessionName");
        sessionTime = getIntent().getStringExtra("sessionTime");
        sessionId = getIntent().getIntExtra("sessionId", -1);
        if (getIntent().getBooleanExtra("openEditDialog", false)) {
            int sessionId = getIntent().getIntExtra("sessionId", -1);
            String sessionName = getIntent().getStringExtra("sessionName");

            openEditSessionDialog(sessionId, sessionName, sessionTime);
        }

        // Check if the current time matches the session time
        if (isSessionTimeMatching(sessionTime)) {
            setFinishOnTouchOutside(false);
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );

            // Show the dialog
            showNotificationDialog();
        } else if (isBreakTime(breakTime)) {
            setFinishOnTouchOutside(false);
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            );
            // Show the dialog
            showBreakNotificationDialog();
        } else {
            // If the time doesn't match, just finish the activity
            finish();
            showScheduledBreakDialog();
//            finish();
        }
    }


    private void openEditSessionDialog(int sessionId, String sessionName, String sessionTime) {
        Bundle args = new Bundle();
        args.putInt("sessionId", sessionId);
        args.putString("sessionName", sessionName);
        args.putString("sessionTime", sessionTime);

        EditSessionDialog editDialog = new EditSessionDialog();
        editDialog.setArguments(args);
        editDialog.show(getSupportFragmentManager(), "EditSessionDialog");
    }

    private boolean isSessionTimeMatching(String sessionTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalTime now = LocalTime.now();
        LocalTime sessionLocalTime = LocalTime.parse(sessionTime, formatter);

        // Check if the current time is within 5 minutes of the session start time
        return now.isAfter(sessionLocalTime.minusMinutes(5)) && now.isBefore(sessionLocalTime);
    }

    private boolean isBreakTime(boolean breakTime) {
        return breakTime;
    }

    private AlertDialog dialog;

    private void showScheduledBreakDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        String sessionName = getIntent().getStringExtra("sessionName");
        String sessionTime = getIntent().getStringExtra("sessionTime");
        String breakSchedule = getIntent().getStringExtra("breakSchedule");
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setIcon(getDrawable(R.drawable.baseline_access_time_24))
                .setTitle("Break Schedule")
                .setMessage("Your break for session '" + sessionName + "' at " + sessionTime + " is scheduled for " + breakSchedule + ". You will be notified when it's time to take a break.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false);

        dialog = builder.create();

        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the dialog is dismissed to prevent window leaks
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    private void showBreakNotificationDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        String sessionName = getIntent().getStringExtra("sessionName");
        String sessionTime = getIntent().getStringExtra("sessionTime");
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setIcon(getDrawable(R.drawable.baseline_access_time_24))
                .setTitle("BREAK TIME!")
                .setMessage("It's time for a break during your '" + sessionName + "' session. Take a 10-minute break or choose to keep going.")
                .setPositiveButton("Start break", (dialog, which) -> {
                    takeBreak();
                    finish();
                })
                .setNegativeButton("Keep going", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNeutralButton("Choose an App", (dialog, which) -> {
                    showAppSelectionDialog();
                    dialog.dismiss();
                })
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();

        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }

    private void showAppSelectionDialog() {
        List<AppItem> appItems = new ArrayList<>();

        // Populate app items (TikTok, Instagram, YouTube)
        appItems.add(new AppItem("TikTok",
                getAppIcon("com.zhiliaoapp.musically"),
                "com.zhiliaoapp.musically"));
        appItems.add(new AppItem("Instagram",
                getAppIcon("com.instagram.android"),
                "com.instagram.android"));
        appItems.add(new AppItem("YouTube",
                getAppIcon("com.google.android.youtube"),
                "com.google.android.youtube"));

        AppSelectionAdapter adapter = new AppSelectionAdapter(this, appItems);

        new AlertDialog.Builder(this)
                .setTitle("Choose an App")
                .setAdapter(adapter, (dialog, which) -> {
                    AppItem selectedApp = appItems.get(which);
                    takeBreak();
                    openApp(selectedApp.getPackageName());
                })
                .setCancelable(true)
                .show();
    }

    private Drawable getAppIcon(String packageName) {
        try {
            return getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return ContextCompat.getDrawable(this, R.drawable.ic_default_app);
        }
    }


    private void openApp(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            // Redirect to Play Store if the app is not installed
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            startActivity(playStoreIntent);
        }
    }



    private void showNotificationDialog() {
        String sessionName = getIntent().getStringExtra("sessionName");
        String sessionTime = getIntent().getStringExtra("sessionTime");

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setIcon(getDrawable(R.drawable.baseline_access_time_24))
                .setTitle("Upcoming Session")
                .setMessage("Your session '" + sessionName + "' starts at " + sessionTime + ". Confirm or Reschedule?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    confirmSession();
                })
                .setNegativeButton("Reschedule", (dialog, which) -> {
                    rescheduleSession();
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Keep dialog visible for 30 seconds
        new Handler().postDelayed(() -> {
            if (!isFinishing() && dialog.isShowing()) {
                dialog.dismiss();
                finish();
            }
        }, 30000);
    }

    private void takeBreak() {
        long tenMinutesInMillis = 10 * 60 * 1000;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "break_timer_channel";
        String channelName = "Break Timer";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for the 10-minute break timer");
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        long endTime = System.currentTimeMillis() + tenMinutesInMillis;

        Runnable updateNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                long timeLeft = endTime - System.currentTimeMillis();
                if (timeLeft > 0) {
                    int minutes = (int) (timeLeft / 1000) / 60;
                    int seconds = (int) (timeLeft / 1000) % 60;

                    String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Break Timer")
                            .setContentText("Time left: " + timeLeftFormatted)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setOngoing(true)
                            .setSilent(true);

                    notificationManager.notify(1, builder.build());
                    handler.postDelayed(this, 1000);
                } else {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Break Over")
                            .setContentText("Your break is over! Time to get back to your session.")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);

                    notificationManager.notify(2, builder.build());
                    handler.removeCallbacks(this);
                }
            }
        };

        handler.post(updateNotificationRunnable);
    }




    private void confirmSession() {
        MaterialAlertDialogBuilder secondDialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Glad you're still Joining!")
                .setMessage("Head to the app when you're ready :)")
                .setPositiveButton("Acknowledge", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        secondDialogBuilder.show();
    }

    private void rescheduleSession() {
        MaterialAlertDialogBuilder linkDialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Aw Sorry to Hear")
                .setMessage("Modify session in the app")
                .setPositiveButton("Open LockedIn", (dialog, which) -> {
                    Bundle args = new Bundle();
                    args.putInt("sessionId", sessionId);
                    args.putString("sessionName", sessionName);
                    args.putString("sessionTime", sessionTime);

                    // Create and show the EditSessionDialog
                    EditSessionDialog editDialog = new EditSessionDialog();
                    editDialog.setArguments(args);
                    editDialog.show(getSupportFragmentManager(), "EditSessionDialog");


                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
        linkDialogBuilder.show();
    }
}

