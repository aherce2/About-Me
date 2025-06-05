package edu.illinois.cs465.lockedin.fragments;


import static edu.illinois.cs465.lockedin.models.TagManager.tagOptions;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.Reward;
import edu.illinois.cs465.lockedin.models.StudySession;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.TagManager;

public class EditSessionDialog extends BottomSheetDialogFragment {


    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private Spinner classTagSpinner;

    private Spinner rewardSpinner;
    private Spinner repeatInSpinner;

    private final String[] rewardOptions = {"Choose Reward", "Get Coffee", "Go to Chicago", "Go Shopping"};
    private final String[] repeatOptions = {"Choose Time", "Never", "Daily", "Weekly", "Monthly", "Weekdays"};
    private Calendar startTimeCalendar = Calendar.getInstance();
    private Calendar endTimeCalendar = Calendar.getInstance();

    private SharedViewModel viewModel;
    private StudySession existingSession;
    private ArrayAdapter<String> classTagAdapter;


    public void setExistingSession(StudySession session) {
        this.existingSession = session;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_session_dialog, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        eventNameEditText = view.findViewById(R.id.editEventName);
        eventDateEditText = view.findViewById(R.id.editEventDate);
        startTimeEditText = view.findViewById(R.id.editStartTimeEditText);
        endTimeEditText = view.findViewById(R.id.editEndTimeEditText);
        classTagSpinner = view.findViewById(R.id.editClassTagSpinner);
        rewardSpinner = view.findViewById(R.id.editRewardSpinner);
        repeatInSpinner = view.findViewById(R.id.editRepeatInSpinner);

        ImageButton cancelButton = view.findViewById(R.id.close_edit_session);
        Button deleteButton = view.findViewById(R.id.editDeleteButton);
        Button createButton = view.findViewById(R.id.editSaveButton);

        List<String> tagOptions = TagManager.getTagOptions();
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tagOptions);
        classTagSpinner.setAdapter(tagAdapter);
        setupTagSpinner(tagAdapter);


        ArrayAdapter<String> rewardAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, rewardOptions);
        rewardSpinner.setAdapter(rewardAdapter);


        ArrayAdapter<String> repeatAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, repeatOptions);
        repeatInSpinner.setAdapter(repeatAdapter);


        eventDateEditText.setFocusable(false);
        eventDateEditText.setClickable(true);
        eventDateEditText.setOnClickListener(v -> showDatePickerDialog());

        startTimeEditText.setFocusable(false);
        startTimeEditText.setClickable(true);
        startTimeEditText.setOnClickListener(v -> showTimePickerDialog(startTimeEditText, true));

        endTimeEditText.setFocusable(false);
        endTimeEditText.setClickable(true);
        endTimeEditText.setOnClickListener(v -> showTimePickerDialog(endTimeEditText, false));

        if (existingSession != null) {
            eventNameEditText.setText(existingSession.getName());
            eventDateEditText.setText(existingSession.getDate());
            startTimeEditText.setText(existingSession.getStart_time());
            endTimeEditText.setText(existingSession.getEnd_time());

            int tagPosition = tagOptions.indexOf(existingSession.getTag());
            if (tagPosition != -1) {
                classTagSpinner.setSelection(tagPosition);
            } else {
                TagManager.addTag(existingSession.getTag());
                tagAdapter.notifyDataSetChanged();
                classTagSpinner.setSelection(tagOptions.indexOf(existingSession.getTag()));
            }


            int rewardPosition = rewardAdapter.getPosition(existingSession.getReward());
            rewardSpinner.setSelection(rewardPosition);

        }


        cancelButton.setOnClickListener(v -> dismiss());

        deleteButton.setOnClickListener(v-> {
            showDeleteConfirmationDialog(existingSession);
        });


        createButton.setOnClickListener(v -> {
            String eventName = eventNameEditText.getText().toString();
            String eventDate = eventDateEditText.getText().toString();
            String startTime = startTimeEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();
            String reward = rewardSpinner.getSelectedItem().toString();
            String tag = classTagSpinner.getSelectedItem().toString();
            String frequency = repeatInSpinner.getSelectedItem().toString();
            if (eventName.isEmpty() || eventDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            } else {

                ManageSession manager = new ManageSession(getContext());
                if (existingSession != null) {
                    existingSession.setName(eventName);
                    existingSession.setDate(eventDate);
                    existingSession.setStart_time(startTime);
                    existingSession.setEnd_time(endTime);
                    existingSession.setReward(reward);
                    existingSession.setTag(tag);
                    existingSession.setFrequency(frequency);
                    manager.updateSession(existingSession);
                    viewModel.updateSessions(manager.get_sessions());
                    viewModel.updateRewards(manager.get_rewards());
                    Toast.makeText(getContext(), "Session Updated", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    UUID sessionId = UUID.randomUUID();
                    StudySession session = new StudySession(sessionId.toString(), eventName, eventDate, startTime, endTime, reward, tag, frequency);
                    manager.add_session(session, getContext());
                    viewModel.updateSessions(manager.get_sessions());
                    viewModel.updateRewards(manager.get_rewards());


                    Toast.makeText(getContext(), "Session created and saved!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
        return view;
    }

    private void showDeleteConfirmationDialog(StudySession session) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this session? This action cannot be undone.")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    ManageSession manager = new ManageSession(getContext());
                    manager.remove_session(session, new Reward(session.getID(), session.getReward(), session.getDate(), session.getStart_time()));
                    viewModel.updateSessions(manager.get_sessions());
                    viewModel.updateRewards(manager.get_rewards());
                    dialog.dismiss();
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
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

        /*DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selected_Date = Calendar.getInstance();
                    selected_Date.set(selectedYear, selectedMonth, selectedDay);
                    if (selected_Date.before(Calendar.getInstance())) {
                        Toast.makeText(getContext(), "Please select a future date", Toast.LENGTH_SHORT).show();
                    } else {
                        String selectedDate = String.format(Locale.US, "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                        eventDateEditText.setText(selectedDate);
                    }
                },
                year, month, day
        );*/
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    // Start and End Time
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

    /*@Override
    public void onResume() {
        super.onResume();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = (int) (metrics.widthPixels * 0.99);
            int height = (int) (metrics.heightPixels * 0.99);
            window.setLayout(width, height);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }*/
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

            //behavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.5));

            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            behavior.setFitToContents(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
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


}