package edu.illinois.cs465.lockedin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.illinois.cs465.lockedin.fragments.CalendarFragment;
import edu.illinois.cs465.lockedin.fragments.HomeFragment;
import edu.illinois.cs465.lockedin.fragments.PreferencesFragment;
import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.fragments.RewardsFragment;
import edu.illinois.cs465.lockedin.activities.ProfileActivity;
import edu.illinois.cs465.lockedin.fragments.SettingsFragment;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.NotificationService;
import edu.illinois.cs465.lockedin.models.SharedViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SharedViewModel viewModel;
    private static final String PREFS_NAME = "StudySessions";
    private static final String FIRST_LAUNCH_FLAG = "isFirstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);


        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean(FIRST_LAUNCH_FLAG, true);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

       if (flag) {
            ManageSession manager = new ManageSession(this);
            manager.clear_sessions();
            manager.clear_sessions_completed();
            viewModel.updateSessions(manager.get_sessions());
            viewModel.updateCompletedSessions(manager.get_completed_sessions());
            sharedPreferences.edit().putBoolean(FIRST_LAUNCH_FLAG, false).apply();
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        CalendarFragment calFragment = new CalendarFragment();
        HomeFragment hmFragment = new HomeFragment();
        RewardsFragment rwdFragment = new RewardsFragment();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set up the Toolbar with the hamburger icon
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Menu menu = navigationView.getMenu();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment selectedFragment = null;
                if (id == R.id.preferences) {
                    selectedFragment = new PreferencesFragment();
                }
                if (id == R.id.settings) {
                    selectedFragment = new SettingsFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, selectedFragment)
                            .addToBackStack(null)
                            .commit();
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;
            if (itemId == R.id.home) {
                selectedFragment = hmFragment;
            } else if (itemId == R.id.calendar) {
                selectedFragment = calFragment;
            } else if (itemId == R.id.rewards) {
                selectedFragment = rwdFragment;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, selectedFragment)
                        .addToBackStack(null) // Add the fragment transaction to the back stack
                        .commit();
            }
            return true;
        });
        if (getIntent().getBooleanExtra("NAVIGATE_TO_HOME", false)) {
            navigateToHomeFragment();
        } else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomeFragment())
                    .commit();
        }
        CircleImageView profileImage = findViewById(R.id.profile_img);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Load the latest session data each time MainActivity resumes
        loadLatestSessionData();
    }

    private void loadLatestSessionData() {
        ManageSession manager = new ManageSession(this);
        viewModel.updateSessions(manager.get_sessions());
        viewModel.updateCompletedSessions(manager.get_completed_sessions());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("NAVIGATE_TO_HOME", false)) {
            navigateToHomeFragment();
        }
    }

    private void navigateToHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new HomeFragment())
                .commit();
    }
}