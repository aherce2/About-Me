package edu.illinois.cs465.lockedin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.adapters.LockedRewardsAdapter;
import edu.illinois.cs465.lockedin.adapters.UnlockedRewardsAdapter;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.Reward;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.StudySession;

public class SessionCardActivity extends AppCompatActivity {


    private TextView sessionTitle, sessionDate, sessionTime, sessionReward;
    private ProgressBar sessionProgressBar;
    boolean session_start_flag = false;
    private String start_time = "";
    LottieAnimationView animationView;
    //SharedViewModel viewModel;
    private Button btnStart, btnSave, btnUse;

    private Handler time_handler = new Handler(Looper.getMainLooper());
    private Runnable time_runner = new Runnable() {
        @Override
        public void run() {
            String time = sessionTime.getText().toString();
            System.out.println(time);
            checkStartVisibility(sessionDate.getText().toString(), start_time);
            time_handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_card);

        // Initialize Views
        sessionTitle = findViewById(R.id.sessionTitle);
        animationView = findViewById(R.id.reward_animation);

        sessionDate = findViewById(R.id.sessionDate);
        sessionTime = findViewById(R.id.sessionTime);
        sessionReward = findViewById(R.id.sessionReward);
        sessionProgressBar = findViewById(R.id.sessionProgressBar);
        btnStart = findViewById(R.id.btnStart);



        btnSave = findViewById(R.id.btnSave);
        btnUse = findViewById(R.id.btnUse);

        // Load saved session data
        long sessionDuration = loadSessionData();

        // Button Click Listener for Start
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start session logic
                session_start_flag = true;
                Toast.makeText(SessionCardActivity.this, "Study Session Started", Toast.LENGTH_SHORT).show();
                startStudySession(sessionDuration);
            }
        });

        // Button Click Listener for Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SessionCardActivity.this, "Session Reward Saved", Toast.LENGTH_SHORT).show();
                moveCompletedSessionAndSaveReward();
                navigateToHomeFragment();

            }

        });

        // Button Click Listener for Use (currently hidden)
        btnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for using the reward
                Toast.makeText(SessionCardActivity.this, "You can Now Use the Session Reward!", Toast.LENGTH_SHORT).show();
                moveCompletedSessionAndUseReward();
                navigateToHomeFragment();
            }
        });
        time_handler.post(time_runner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        time_handler.removeCallbacks(time_runner);
    }


    private void startStudySession(long sessionDuration) {
        final int maxProgress = 100; // Max progress bar value (100%)

        // Start the progress at 0
        sessionProgressBar.setProgress(0);

        // Hide Start Button to Minimize Distraction
        btnStart.setVisibility(View.GONE);
        //long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + sessionDuration*1000;

        // Handler to post updates to the UI
        final Handler handler = new Handler(Looper.getMainLooper());

        // Runnable that will update the progress bar based on time elapsed
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                //long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                long remainingTime = (endTime - System.currentTimeMillis())/1000;

                if (remainingTime >= 0) {
                    sessionTime.setText(formatTime((int) remainingTime));
                    int progress = (int) (((sessionDuration-remainingTime) / (float) sessionDuration) * maxProgress);
                    sessionProgressBar.setProgress(progress);
                    handler.postDelayed(this, 1000);
                } else {
                    sessionReward.setVisibility(View.VISIBLE);
                    sessionTime.setVisibility(View.GONE);


                    btnSave.setVisibility(View.VISIBLE);
                    btnUse.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();

                }
            }
        };
        // Start the progress update immediately
        handler.post(progressRunnable);
    }

    private void navigateToHomeFragment() {
        Intent intent = new Intent(SessionCardActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("NAVIGATE_TO_HOME", true);
        startActivity(intent);
        finish();
    }
    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private long loadSessionData() {
        Intent intent = getIntent();
        /*String savedTitle = intent.getStringExtra("SESSION_NAME");
        String savedDate = intent.getStringExtra("SESSION_DATE");
        String startTimeString = intent.getStringExtra("SESSION_START_TIME");
        String endTimeString = intent.getStringExtra("SESSION_END_TIME");
        String reward = intent.getStringExtra("SESSION_REWARD");*/
        String session = intent.getStringExtra("SESSION");
        Gson gson = new Gson();
        StudySession study_session = gson.fromJson(session, StudySession.class);
        String savedTitle = study_session.getName();
        String savedDate = study_session.getDate();
        String startTimeString = study_session.getStart_time();
        start_time = startTimeString;
        String endTimeString = study_session.getEnd_time();
        String reward = study_session.getReward();
        long Seconds = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        if (startTimeString != null && endTimeString != null) {
            LocalTime startTime = LocalTime.parse(startTimeString, formatter);
            LocalTime endTime = LocalTime.parse(endTimeString, formatter);
            Duration duration = Duration.between(startTime, endTime);
            Seconds = duration.getSeconds();
        }

        // Set values to the TextViews
        sessionTitle.setText(savedTitle);
        sessionDate.setText(savedDate);
        sessionProgressBar.setProgress(0); // Default progress is 0
        sessionTime.setText(formatTime((int) Seconds)); // Default time is 00:00:00
        sessionReward.setText(reward);
        checkStartVisibility(savedDate, startTimeString);

        return Seconds;
    }

    private void checkStartVisibility(String date, String start_time) {
        if (session_start_flag) {
            return;
        }
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime start_time_format = LocalTime.parse(start_time, time_formatter);
        LocalDate date_format = LocalDate.parse(date, date_formatter);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (date_format.equals(today) && (start_time_format.equals(now) || start_time_format.isBefore(now))) {
            runOnUiThread(() -> btnStart.setVisibility(View.VISIBLE)) ;
        } else {
            runOnUiThread(() -> btnStart.setVisibility(View.GONE));
        }

    }


    private void moveCompletedSessionAndSaveReward() {
        //SharedViewModel viewModel = new ViewModelProvider(SessionCardActivity.this).get(SharedViewModel.class);
        /*ManageSession manager = new ManageSession(SessionCardActivity.this);
        List<StudySession> upcomingSessions = manager.get_sessions();
        List<Reward> rewards = manager.get_rewards();
        if (upcomingSessions.isEmpty()) {
            return;
        }
        manager.add_completed_session(upcomingSessions.get(0));
        Log.d("locked reward list before removal", rewards.toString());
        manager.remove_session(upcomingSessions.get(0), rewards.get(0));
        Log.d("locked reward list after removal", manager.get_rewards().toString());
        viewModel.updateSessions(manager.get_sessions());
        //Gson gson = new Gson();
        //Log.d("locked reward list view before removal",  gson.toJson(viewModel.getRewards()));
        viewModel.updateRewards(manager.get_rewards());
        //Log.d("locked reward list view after removal", gson.toJson(viewModel.getRewards()));
        viewModel.updateCompletedSessions(manager.get_completed_sessions());
        viewModel.updateUnlockedRewards(manager.get_unlocked_rewards());*/
        SharedViewModel viewModel = new ViewModelProvider(SessionCardActivity.this).get(SharedViewModel.class);
        ManageSession manager = new ManageSession(SessionCardActivity.this);
        List<StudySession> upcomingSessions = manager.get_sessions();
        List<Reward> rewards = manager.get_rewards();
        if (upcomingSessions.isEmpty()) {
            return;
        }
        manager.add_completed_session(upcomingSessions.get(0));
        manager.remove_session(upcomingSessions.get(0), manager.get_rewards().get(0));
        viewModel.updateSessions(manager.get_sessions());
        viewModel.updateRewards(manager.get_rewards());
        viewModel.updateCompletedSessions(manager.get_completed_sessions());
        viewModel.updateUnlockedRewards(manager.get_unlocked_rewards());
    }

    private void moveCompletedSessionAndUseReward() {
        SharedViewModel viewModel = new ViewModelProvider(SessionCardActivity.this).get(SharedViewModel.class);
        ManageSession manager = new ManageSession(SessionCardActivity.this);
        List<StudySession> upcomingSessions = manager.get_sessions();
        List<Reward> rewards = manager.get_rewards();

        if (upcomingSessions.isEmpty()) {
            return;
        }
        manager.add_completed_session(upcomingSessions.get(0));
        manager.remove_session(upcomingSessions.get(0), manager.get_rewards().get(0));
        manager.remove_unlocked_reward(0);
        viewModel.updateSessions(manager.get_sessions());
        List<Reward> updatedRewards = manager.get_rewards();
        Log.d("SessionCardActivity", "Updated rewards size: " + updatedRewards.size());

        viewModel.updateRewards(manager.get_rewards());
        viewModel.updateCompletedSessions(manager.get_completed_sessions());
        viewModel.updateUnlockedRewards(manager.get_unlocked_rewards());
        Log.d("SessionCardActivity", "ViewModel updated");
    }
    private LockedRewardsAdapter adapter_l;

    private void updateRewardsUI(List<Reward> rewards) {
        if (adapter_l == null) {
            adapter_l = new LockedRewardsAdapter(getBaseContext(), new ArrayList<>());
            // Set this adapter to your RecyclerView or ListView
            // rewardsRecyclerView.setAdapter(adapter_l);
        }

        ArrayList<String> rewardStrings = new ArrayList<>();
        for (Reward reward : rewards) {
            rewardStrings.add(reward.getReward());
        }

        adapter_l.updateData(rewardStrings);
        adapter_l.notifyDataSetChanged();
    }
}