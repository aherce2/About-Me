package edu.illinois.cs465.lockedin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Locale;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.ManagePreferences;
import edu.illinois.cs465.lockedin.models.TimeBlockPreference;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private ImageButton deleteFragment;
    private TimeBlockPreference timeBlock;
    private OnDeleteClickListener deleteListener;

    public static DialogFragment newInstance(String day, String category, String id,boolean isEditMode) {
        DialogFragment fragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putString("day", day);
        args.putString("category", category);
        args.putString("id", id);
        args.putBoolean("isEditMode", isEditMode);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof DayCardFragment) {
            ((DayCardFragment) getParentFragment()).addActiveDialog(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getParentFragment() instanceof DayCardFragment) {
            ((DayCardFragment) getParentFragment()).removeActiveDialog(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        startTimeEditText = view.findViewById(R.id.start_time_edit_text);
        endTimeEditText = view.findViewById(R.id.end_time_edit_text);
        deleteFragment = view.findViewById(R.id.delete_fragment);
//        deleteFragment.setVisibility(View.GONE);
        boolean isEditMode = getArguments().getBoolean("isEditMode", false);
        deleteFragment.setVisibility(isEditMode ? View.VISIBLE : View.GONE);



        String id = getArguments().getString("id");
        ManagePreferences managePreferences = new ManagePreferences(getContext());

        for (TimeBlockPreference pref : managePreferences.getPreferences()) {
            if (pref.getId().equals(id)) {
                timeBlock = pref;
                break;
            }
        }

        if (timeBlock != null) {
            startTimeEditText.setText(timeBlock.getStartTime());
            endTimeEditText.setText(timeBlock.getEndTime());
        }

        startTimeEditText.setOnClickListener(v -> showTimePickerDialog(true));
        endTimeEditText.setOnClickListener(v -> showTimePickerDialog(false));

        startTimeEditText.setInputType(InputType.TYPE_NULL);
        endTimeEditText.setInputType(InputType.TYPE_NULL);

        deleteFragment.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(this);
            }
        });

        return view;
    }
    public boolean areFieldsFilled() {
        String startTime = startTimeEditText.getText().toString();
        String endTime = endTimeEditText.getText().toString();
        return !startTime.isEmpty() && !endTime.isEmpty() && !startTime.equals("Not set") && !endTime.equals("Not set");
    }
    public boolean validateSleep() {
        if (getArguments().getString("category").equals("sleep")) {
            String startTime = startTimeEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();

            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int endHour = Integer.parseInt(endParts[0]);

            int duration = endHour - startHour;
            if (duration < 0) {
                duration += 24; // Handle overnight sleep
            }

            return duration >= 8;
        }
        return false;
    }
    private void showTimePickerDialog(boolean isStartTime) {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(isStartTime ? "Select Start Time" : "Select End Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            int hourOfDay = materialTimePicker.getHour();
            int minute = materialTimePicker.getMinute();

            String amPm = (hourOfDay < 12) ? "AM" : "PM";
            int hour = (hourOfDay > 12) ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
            String time = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm);

            if (isStartTime) {
                startTimeEditText.setText(time);
            } else {
                endTimeEditText.setText(time);
            }
        });

        materialTimePicker.show(getChildFragmentManager(), "timePicker");
    }
    public TimeBlockPreference saveTimeBlock() {
        if (timeBlock != null) {
            String startTime = startTimeEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();

            if (startTime.equals("Not set") || endTime.equals("Not set")) {
                return null;
            }

            timeBlock.setStartTime(startTime);
            timeBlock.setEndTime(endTime);
            ManagePreferences managePreferences = new ManagePreferences(getContext());
            managePreferences.updatePreference(timeBlock);

            // Update UI
            startTimeEditText.setText(startTime);
            endTimeEditText.setText(endTime);
            return timeBlock;
        }
        return null;
    }

    public void setDeleteButtonVisibility(int visibility) {
        if (deleteFragment != null) {
            deleteFragment.setVisibility(visibility);
        }
    }

//    public void setEditingEnabled(boolean enabled) {
//        startTimeEditText.setEnabled(enabled);
//        endTimeEditText.setEnabled(enabled);
//        deleteFragment.setVisibility(View.VISIBLE);
//    }
    public void setEditingEnabled(boolean enabled) {
        if (startTimeEditText != null) {
            startTimeEditText.setEnabled(enabled);
        }
        if (endTimeEditText != null) {
            endTimeEditText.setEnabled(enabled);
        }
        if (deleteFragment != null) {
            deleteFragment.setVisibility(enabled ? View.VISIBLE : View.GONE);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(DialogFragment fragment);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }
}