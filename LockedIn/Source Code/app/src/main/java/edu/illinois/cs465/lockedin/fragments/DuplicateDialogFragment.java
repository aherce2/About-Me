package edu.illinois.cs465.lockedin.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

import edu.illinois.cs465.lockedin.R;


public class DuplicateDialogFragment extends AppCompatActivity {

    private LinearLayout sleepFragmentContainer, blockedFragmentContainer, eatingFragmentContainer;
    private Map<String, Integer> fragmentCounts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_container);

        // Initialize fragment containers
        sleepFragmentContainer = findViewById(R.id.sleepFragmentContainer);
        blockedFragmentContainer = findViewById(R.id.blockedFragmentContainer);
        eatingFragmentContainer = findViewById(R.id.eatingFragmentContainer);

        // Initialize fragment counts
        fragmentCounts.put("sleep", 0);
        fragmentCounts.put("blocked", 0);
        fragmentCounts.put("eating", 0);

        // Set up add buttons for each category
        ImageButton addSleepButton = findViewById(R.id.add_sleep_fragment);
        ImageButton addBlockedButton = findViewById(R.id.add_blocked_fragment);
        ImageButton addEatingButton = findViewById(R.id.add_eating_fragment);

        addSleepButton.setOnClickListener(v -> addNewFragment("sleep"));
        addBlockedButton.setOnClickListener(v -> addNewFragment("blocked"));
        addEatingButton.setOnClickListener(v -> addNewFragment("eating"));
    }

    private void addNewFragment(String category) {
        LinearLayout container;
        switch (category) {
            case "Sleep":
                container = sleepFragmentContainer;
                break;
            case "Blocked":
                container = blockedFragmentContainer;
                break;
            case "Eating":
                container = eatingFragmentContainer;
                break;
            default:
                return;
        }

        DialogFragment newFragment = new DialogFragment();

        Bundle args = new Bundle();
        args.putInt("fragmentId", fragmentCounts.get(category));
        args.putString("category", category);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(container.getId(), newFragment, category + fragmentCounts.get(category));
        transaction.setReorderingAllowed(true);
        transaction.commit();

        fragmentCounts.put(category, fragmentCounts.get(category) + 1);

        newFragment.setDeleteButtonVisibility(View.VISIBLE);
    }

}