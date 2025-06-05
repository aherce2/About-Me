package edu.illinois.cs465.lockedin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.ManagePreferences;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.TimeBlockPreference;
import edu.illinois.cs465.lockedin.models.StudySession;
public class CalendarFragment extends Fragment {

    private TextView monthYearTV;
    private LinearLayout weekdayContainer;
    private ListView eventListView;
    private LocalDate selectedDate;
    private LocalDate startDate;
    private EventsAdapter eventsAdapter;
    private ManagePreferences managePreferences;
    private ManageSession manageSession;
    // private LinearLayout weekdayContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        monthYearTV = view.findViewById(R.id.monthYearTV);
        weekdayContainer = view.findViewById(R.id.weekdayContainer);
        eventListView = view.findViewById(R.id.eventListView);

        managePreferences = new ManagePreferences(requireContext());
        manageSession = new ManageSession(requireContext());

        eventsAdapter = new EventsAdapter(requireContext(), new ArrayList<>());
        eventListView.setAdapter(eventsAdapter);

        //this will ensure we display the current week not a week ahead
        //selectedDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());
        selectedDate = LocalDate.now();
        startDate = selectedDate.minusDays(LocalDate.now().getDayOfWeek().getValue()).with(DayOfWeek.SUNDAY);
        updateWeekView();
        updateEventsForDate();

        ImageButton previousWeekButton = view.findViewById(R.id.previousWeekButton);
        ImageButton nextWeekButton = view.findViewById(R.id.nextWeekButton);

        previousWeekButton.setOnClickListener(v -> {
            startDate = startDate.minusWeeks(1);
            updateWeekView();
        });

        nextWeekButton.setOnClickListener(v -> {
            startDate = startDate.plusWeeks(1);
            updateWeekView();
        });

        return view;
    }

    private void updateWeekView() {
        monthYearTV.setText(selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        weekdayContainer.removeAllViews();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            addWeekdayColumn(date);
        }
    }

    private void addWeekdayColumn(LocalDate date) {
        View column = getLayoutInflater().inflate(R.layout.day_column, weekdayContainer, false);
        TextView dayOfWeek = column.findViewById(R.id.dayOfWeek);
        TextView dateOfMonth = column.findViewById(R.id.dateOfMonth);

        dayOfWeek.setText(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        dateOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if (date.equals(selectedDate)) {
            dateOfMonth.setBackgroundResource(R.drawable.selected_date_background);
            dateOfMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            dateOfMonth.setBackgroundResource(R.drawable.date_background);
            dateOfMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }

        column.setOnClickListener(v -> {
            selectedDate = date;
            Log.d("CalendarFragment", "Date selected: " + selectedDate);
            updateWeekView();
            updateEventsForDate();
        });

        weekdayContainer.addView(column);
    }

    private static LocalTime parseTime(String timeString, DateTimeFormatter formatter) {
        try {
            return LocalTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            return LocalTime.MIN;
        }
    }

    private void updateEventsForDate() {
        List<StudySession> allSessions = manageSession.get_sessions();
        List<TimeBlockPreference> recurringPreferences = managePreferences.getPreferences();
        List<StudySession> eventsForDay = new ArrayList<>();

        DayOfWeek selectedDayOfWeek = selectedDate.getDayOfWeek();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = selectedDate.format(formatter);

        for (StudySession session : allSessions) {
            Log.d("test", "(LocalDate): " + formattedDate + "string" + session.getDate());
            if (formattedDate.equals(session.getDate())) {
                eventsForDay.add(session);
            }
        }

        for (TimeBlockPreference preference : recurringPreferences) {
            if (preference.getDay().equalsIgnoreCase(selectedDayOfWeek.name())) {
                eventsForDay.add(new StudySession(
                        preference.getId(),
                        preference.getType(),
                        formattedDate,
                        preference.getStartTime(),
                        preference.getEndTime(),
                        "N/A",
                        "Weekly",
                        preference.getType()
                ));
            }
        }

        Collections.sort(eventsForDay, (session1, session2) -> {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            LocalTime time1 = parseTime(session1.getStart_time(), timeFormatter);
            LocalTime time2 = parseTime(session2.getStart_time(), timeFormatter);

            return time1.compareTo(time2);
        });

        if (!eventsForDay.isEmpty()) {
            Log.d("CalendarFragment", "test" + eventsForDay.size());

            eventsAdapter.clear();
            eventsAdapter.notifyDataSetChanged();
            eventsAdapter.addAll(eventsForDay);
            eventsAdapter.notifyDataSetChanged();
        } else {
            eventsAdapter.clear();
            Log.d("CalendarFragment", "No events for the selected date.");
        }
    }
}






//public class CalendarFragment extends Fragment {
//
//    private CalendarView calendarView;
//    private TextView selectedDateText;
//    private LinearLayout eventsContainer;
//    private ListView eventsList;
//    private TextView noEventsText;
//
//    private ManagePreferences managePreferences;
//    private ManageSession manageSession;
//    private EventsAdapter eventsAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
//
//        // Initialize UI elements
//        selectedDateText = view.findViewById(R.id.selectedDateText);
//        eventsList = view.findViewById(R.id.eventsList);
//        noEventsText = view.findViewById(R.id.noEventsText);
//        LinearLayout dateOptionsContainer = view.findViewById(R.id.dateOptionsContainer);
//
//        // Initialize Managers
//        managePreferences = new ManagePreferences(requireContext());
//        manageSession = new ManageSession(requireContext());
//
//        // Initialize adapter
//        eventsAdapter = new EventsAdapter(requireContext(), new ArrayList<>());
//        eventsList.setAdapter(eventsAdapter);
//
//        // Set the current date as selected by default
//        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        updateDateDisplay(today);
//        updateEventsForDate(today);
//
//        // Set up date options under the date
//        selectedDateText.setOnClickListener(v -> {
//            dateOptionsContainer.setVisibility(View.VISIBLE);
//            populateDateOptions(dateOptionsContainer);
//        });
//
//        return view;
//    }
//
//    private void populateDateOptions(LinearLayout dateOptionsContainer) {
//        dateOptionsContainer.removeAllViews();
//
//        // Example: Add a few date options dynamically
//        for (int i = -2; i <= 2; i++) {
//            String optionDate = LocalDate.now().plusDays(i).toString();
//            Button dateOption = new Button(requireContext());
//            dateOption.setText(optionDate);
//            dateOption.setOnClickListener(v -> {
//                updateDateDisplay(optionDate);
//                updateEventsForDate(optionDate);
//                dateOptionsContainer.setVisibility(View.GONE);
//            });
//            dateOptionsContainer.addView(dateOption);
//        }
//    }
//
//    private void updateDateDisplay(String date) {
//        LocalDate localDate = LocalDate.parse(date);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault());
//        selectedDateText.setText(localDate.format(formatter));
//    }
//
//    private void updateEventsForDate(String selectedDate) {
//        List<StudySession> allSessions = manageSession.get_sessions();
//        List<TimeBlockPreference> recurringPreferences = managePreferences.getPreferences();
//        List<StudySession> eventsForDay = new ArrayList<>();
//
//        // Filter sessions and preferences based on the selected date
//        LocalDate localDate = LocalDate.parse(selectedDate);
//        DayOfWeek selectedDayOfWeek = localDate.getDayOfWeek();
//
//        // Add sessions for the specific date
//        for (StudySession session : allSessions) {
//            if (selectedDate.equals(session.getDate())) {
//                eventsForDay.add(session);
//            }
//        }
//
//        // Add recurring preferences for the day of the week
//        for (TimeBlockPreference preference : recurringPreferences) {
//            if (preference.getDay().equalsIgnoreCase(selectedDayOfWeek.name())) {
//                eventsForDay.add(new StudySession(
//                        preference.getId(),
//                        preference.getType(),
//                        selectedDate,
//                        preference.getStartTime(),
//                        preference.getEndTime(),
//                        "Recurring",
//                        "Weekly",
//                        preference.getType()
//                ));
//            }
//        }
//
//        // Update the UI
//        if (!eventsForDay.isEmpty()) {
//            eventsContainer.setVisibility(View.VISIBLE);
//            noEventsText.setVisibility(View.GONE);
//            eventsAdapter.clear();
//            eventsAdapter.addAll(eventsForDay);
//            eventsAdapter.notifyDataSetChanged();
//        } else {
//            eventsContainer.setVisibility(View.GONE);
//            noEventsText.setVisibility(View.VISIBLE);
//        }
//    }
////}