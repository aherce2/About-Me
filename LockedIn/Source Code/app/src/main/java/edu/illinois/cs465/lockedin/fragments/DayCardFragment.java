package edu.illinois.cs465.lockedin.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.ManagePreferences;
import edu.illinois.cs465.lockedin.models.TimeBlockPreference;

public class DayCardFragment extends Fragment implements DialogFragment.OnDeleteClickListener {

    private ArrayList<DialogFragment> activeDialogs = new ArrayList<>();
    private static final String ARG_DAY = "day";
    private static final String ARG_PREFERENCES = "preferences";
    private String day;
    private ArrayList<TimeBlockPreference> dayPreferences;
    private Map<String, Integer> fragmentCounts = new HashMap<>();
    private CardView dayCard;
    private View expandableContent;
    private ImageButton editButton;
    private ImageButton addSleepButton, addBlockedButton, addEatingButton;
    private boolean isEditMode = false;
    private Button saveButton;
    private ManagePreferences managePreferences;
    private LinearLayout sleepContainer, blockedContainer, eatingContainer;

    public static DayCardFragment newInstance(String day, ArrayList<TimeBlockPreference> preferencesForDay) {
        DayCardFragment fragment = new DayCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        args.putParcelableArrayList(ARG_PREFERENCES, preferencesForDay); // Pass preferences
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isEditMode = savedInstanceState.getBoolean("isEditMode", false);
        }
        if (getArguments() != null) {
            day = getArguments().getString(ARG_DAY);
            dayPreferences = getArguments().getParcelableArrayList(ARG_PREFERENCES); // Get preferences for this day
        }
        fragmentCounts.put("sleep", 0);
        fragmentCounts.put("blocked", 0);
        fragmentCounts.put("eating", 0);
        managePreferences = new ManagePreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_card, container, false);
        initializeViews(view);
        loadTimeBlocks();
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        dayCard = view.findViewById(R.id.preferenceCard);
        TextView dayTitleTextView = view.findViewById(R.id.dayTitle);
        dayTitleTextView.setText(day);
        expandableContent = view.findViewById(R.id.expandableContent);
        expandableContent.setVisibility(View.GONE);
        editButton = view.findViewById(R.id.edit_preferences);
        saveButton = view.findViewById(R.id.savePreferences);
        sleepContainer = view.findViewById(R.id.sleepFragmentContainer);
        blockedContainer = view.findViewById(R.id.blockedFragmentContainer);
        eatingContainer = view.findViewById(R.id.eatingFragmentContainer);
        addSleepButton = view.findViewById(R.id.add_sleep_fragment);
        addBlockedButton = view.findViewById(R.id.add_blocked_fragment);
        addEatingButton = view.findViewById(R.id.add_eating_fragment);
        isEditMode = false;

        editButton.setOnClickListener(v -> enterEditMode());
        saveButton.setOnClickListener(v -> exitEditMode());

        addSleepButton.setOnClickListener(v -> addNewFragment("sleep"));
        addBlockedButton.setOnClickListener(v -> addNewFragment("blocked"));
        addEatingButton.setOnClickListener(v -> addNewFragment("eating"));
    }

    private void loadTimeBlocks() {
        // Clear existing fragments before loading new ones
        for (DialogFragment dialog : activeDialogs) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(dialog);
            transaction.commit();
        }
        activeDialogs.clear(); // Clear active dialogs list

        // Load preferences from SharedPreferences
        dayPreferences = managePreferences.getPreferencesForDay(day); // Make sure this method fetches data correctly

        if (dayPreferences != null) {
            for (TimeBlockPreference block : dayPreferences) {
                addTimeBlockView(block,false); // Add each block to the view
            }
        }
    }

    private void addTimeBlockView(TimeBlockPreference block, boolean isEditMode) {
        LinearLayout container = getCategoryContainer(block.getType());
        if (container == null) return;

        DialogFragment newFragment = DialogFragment.newInstance(day, block.getType(), block.getId(), isEditMode);
        newFragment.setOnDeleteClickListener(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(container.getId(), newFragment, block.getType() + fragmentCounts.get(block.getType()));
        transaction.commit();

        // Set edit mode for the new fragment
        newFragment.setEditingEnabled(isEditMode);
        newFragment.setDeleteButtonVisibility(isEditMode ? View.VISIBLE : View.GONE);

        fragmentCounts.put(block.getType(), fragmentCounts.get(block.getType()) + 1);
        activeDialogs.add(newFragment);
    }

    private void setupListeners() {
        dayCard.setOnClickListener(v -> toggleExpandableContent());
    }

    private void toggleExpandableContent() {
        if (dayCard == null || expandableContent == null) {
            Log.e("DayCardFragment", "dayCard or expandableContent is null");
            return;
        }
        TransitionManager.beginDelayedTransition(dayCard);
        if (expandableContent.getVisibility() == View.VISIBLE && !isEditMode) {
            expandableContent.setVisibility(View.GONE);
            isEditMode = false;
        } else if (expandableContent.getVisibility() == View.VISIBLE && isEditMode) {
            showToast("Please Save Changes First");
        } else {
            expandableContent.setVisibility(View.VISIBLE);
        }
    }

    private void addNewFragment(String category) {
        if (!isEditMode) {
            Log.d("DayCardFragment", "Not in edit mode, cannot add fragment");
            return;
        }

        // Check if all existing fragments have non-null values
        for (DialogFragment dialog : activeDialogs) {
            if (!dialog.areFieldsFilled()) {
                showToast("Please fill in all existing time blocks before adding a new one");
                return;
            }
        }

        LinearLayout container = getCategoryContainer(category);
        if (container == null) return;

        String id = UUID.randomUUID().toString();
        TimeBlockPreference newBlock = new TimeBlockPreference(id, day, category, "", "");
        ArrayList<TimeBlockPreference> preferences = managePreferences.getPreferences();
        preferences.add(newBlock);
        managePreferences.savePreferences(preferences);

        DialogFragment newFragment = DialogFragment.newInstance(day, category, id, isEditMode);
        newFragment.setOnDeleteClickListener(this);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(container.getId(), newFragment, category + fragmentCounts.get(category));
        transaction.commit();
        activeDialogs.add(newFragment);
        // Set delete button visibility based on edit mode
        newFragment.setDeleteButtonVisibility(isEditMode ? View.VISIBLE : View.GONE);

        fragmentCounts.put(category, fragmentCounts.get(category) + 1);
    }

    private LinearLayout getCategoryContainer(String category) {
        switch (category) {
            case "sleep": return sleepContainer;
            case "blocked": return blockedContainer;
            case "eating": return eatingContainer;
            default: return null;
        }
    }

    @Override
    public void onDeleteClick(DialogFragment fragment) {
        String category = fragment.getArguments().getString("category");
        String id = fragment.getArguments().getString("id");
        ArrayList<TimeBlockPreference> preferences = managePreferences.getPreferences();
        preferences.removeIf(pref -> pref.getId().equals(id));
        managePreferences.savePreferences(preferences);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
        activeDialogs.remove(fragment);
        fragmentCounts.put(category, fragmentCounts.get(category) - 1);
    }

    private void enterEditMode() {
        isEditMode = true;
        expandableContent.setVisibility(View.VISIBLE);
        updateAddButtonsVisibility(View.VISIBLE);
        updateAllFragmentsDeleteButtons(View.VISIBLE); // Show delete buttons
        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        for (DialogFragment dialog : activeDialogs) {
            dialog.setEditingEnabled(true);
            dialog.setDeleteButtonVisibility(View.VISIBLE);
        }
    }

    private void exitEditMode() {
        boolean allFieldsFilled = true;
        boolean validSleepBlock = false;
        ArrayList<TimeBlockPreference> updatedPreferences = new ArrayList<>();
        for (DialogFragment dialog : activeDialogs) {
            if (!dialog.areFieldsFilled()) {
                allFieldsFilled = false;
                break;
            }
            if (dialog.getArguments().getString("category").equals("sleep")) {
                if (dialog.validateSleep()) {
                    validSleepBlock = true;
                }
            }
            TimeBlockPreference updatedPreference = dialog.saveTimeBlock();
            if (updatedPreference != null) {
                updatedPreferences.add(updatedPreference);
            }
        }
        if (allFieldsFilled && validSleepBlock) {
            for (DialogFragment dialog : activeDialogs) {
                dialog.saveTimeBlock();
                dialog.setEditingEnabled(false);
                dialog.setDeleteButtonVisibility(View.GONE);
            }
            isEditMode = false;
            updateAddButtonsVisibility(View.GONE);
            updateAllFragmentsDeleteButtons(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
            // Refresh the UI
            loadTimeBlocks();
            showToast("Preferences saved");
        } else if (!allFieldsFilled) {
            showToast("Please fill all time fields before saving");
        } else {
            showToast("Please include at least one 8-hour sleep block");
        }
    }

    private void updateAllFragmentsDeleteButtons(int visibility) {
        for (DialogFragment dialog : activeDialogs) {
            dialog.setDeleteButtonVisibility(visibility);
        }
    }

    public void addActiveDialog(DialogFragment dialog) {
        activeDialogs.add(dialog);
    }

    public void removeActiveDialog(DialogFragment dialog) {
        activeDialogs.remove(dialog);
    }

    private void updateAddButtonsVisibility(int visibility) {
        addSleepButton.setVisibility(visibility);
        addBlockedButton.setVisibility(visibility);
        addEatingButton.setVisibility(visibility);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 250);
        toast.show();
    }
}
