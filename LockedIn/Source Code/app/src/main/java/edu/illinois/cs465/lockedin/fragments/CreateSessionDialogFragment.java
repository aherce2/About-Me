package edu.illinois.cs465.lockedin.fragments;

import static edu.illinois.cs465.lockedin.models.TagManager.tagOptions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import edu.illinois.cs465.lockedin.activities.NotificationDialogActivity;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.StudySession;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.TagManager;

/**
 *
 */
public class CreateSessionDialogFragment extends BottomSheetDialogFragment {


    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private Spinner classTagSpinner;

    private Spinner rewardSpinner;
    private Spinner repeatInSpinner;

    private ArrayAdapter<String> classTagAdapter;

    private final String[] rewardOptions = {"Choose Reward", "Get Coffee", "Go to Chicago", "Go Shopping"};
    private final String[] repeatOptions = {"Choose Time", "Never", "Daily", "Weekly", "Monthly", "Weekdays"};
    private Calendar startTimeCalendar = Calendar.getInstance();
    private Calendar endTimeCalendar = Calendar.getInstance();

    private SharedViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,

                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_session, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Initialize UI elements
        eventNameEditText = view.findViewById(R.id.eventName);
        eventDateEditText = view.findViewById(R.id.eventDate);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        endTimeEditText = view.findViewById(R.id.endTimeEditText);
        classTagSpinner = view.findViewById(R.id.classTagSpinner);
        rewardSpinner = view.findViewById(R.id.rewardSpinner);
        repeatInSpinner = view.findViewById(R.id.repeatInSpinner);

        ImageButton cancelButton = view.findViewById(R.id.close_create_session);
        Button createButton = view.findViewById(R.id.createButton);

        List<String> tagOptions = TagManager.getTagOptions();
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tagOptions);
        classTagSpinner.setAdapter(tagAdapter);
        setupTagSpinner(tagAdapter);

        ArrayAdapter<String> rewardAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, rewardOptions);
        rewardSpinner.setAdapter(rewardAdapter);

        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, repeatOptions);
        repeatInSpinner.setAdapter(repeatAdapter);

        // Set up date picker
        eventDateEditText.setFocusable(false);
        eventDateEditText.setClickable(true);
        eventDateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Set up time pickers
        startTimeEditText.setFocusable(false);
        startTimeEditText.setClickable(true);
        startTimeEditText.setOnClickListener(v -> showTimePickerDialog(startTimeEditText, true));

        endTimeEditText.setFocusable(false);
        endTimeEditText.setClickable(true);
        endTimeEditText.setOnClickListener(v -> showTimePickerDialog(endTimeEditText, false));

        // Set up cancel button
        cancelButton.setOnClickListener(v -> dismiss());

        // Set up create button
        createButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString().trim();
            String eventDate = eventDateEditText.getText().toString().trim();
            String startTime = startTimeEditText.getText().toString().trim();
            String endTime = endTimeEditText.getText().toString().trim();
            String reward = rewardSpinner.getSelectedItem().toString();
            String tag = classTagSpinner.getSelectedItem().toString();
            String frequency = "";
            if (eventName.isEmpty() || eventDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else {

                ManageSession manager = new ManageSession(getContext());
                UUID sessionId = UUID.randomUUID();
                StudySession session = new StudySession(sessionId.toString(), eventName, eventDate, startTime, endTime, reward, tag, frequency);
                Log.d("tag", tag);
                session.setTag(tag);
                manager.add_session(session, getContext());
//                manager.updateSession(session);
                viewModel.updateSessions(manager.get_sessions());
                viewModel.updateRewards(manager.get_rewards());
                Toast.makeText(getContext(), "Session created and saved!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return view;
    }

    private void setupTagSpinner(ArrayAdapter<String> tagAdapter) {
        classTagAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tagOptions);
        classTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classTagSpinner.setAdapter(classTagAdapter);
        classTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tagOptions.get(position).equals("Add New Tag")) {
                    TagManager.showAddTagDialog(requireContext(), tagAdapter, classTagSpinner);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    // Show DatePickerDialog
    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build())
                .build();


        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String selectedDate = String.format(Locale.US, "%02d/%02d/%04d",
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH) + 1,
                    calendar.get(Calendar.YEAR));
            eventDateEditText.setText(selectedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    // Show TimePickerDialog
    private void showTimePickerDialog(EditText timeEditText, boolean isStartTime) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText(isStartTime ? "Select Start Time" : "Select End Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = materialTimePicker.getHour();
            int selectedMinute = materialTimePicker.getMinute();

            String formattedTime = String.format("%02d:%02d %s",
                    (selectedHour % 12 == 0) ? 12 : selectedHour % 12,
                    selectedMinute,
                    (selectedHour >= 12) ? "PM" : "AM");

            timeEditText.setText(formattedTime);

            // Store selected time
            Calendar selectedCalendar = isStartTime ? startTimeCalendar : endTimeCalendar;
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedCalendar.set(Calendar.MINUTE, selectedMinute);

            // Check time conflicts
            if (isStartTime) {
                if (!endTimeEditText.getText().toString().isEmpty() && startTimeCalendar.after(endTimeCalendar)) {
                    Toast.makeText(getContext(), "Start time cannot be later than end time", Toast.LENGTH_SHORT).show();
                    endTimeEditText.setText("");
                }
            } else {
                if (endTimeCalendar.before(startTimeCalendar)) {
                    Toast.makeText(getContext(), "End time cannot be earlier than start time", Toast.LENGTH_SHORT).show();
                    endTimeEditText.setText("");
                }
            }
        });

        materialTimePicker.show(getParentFragmentManager(), "TIME_PICKER");
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            behavior.setFitToContents(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
