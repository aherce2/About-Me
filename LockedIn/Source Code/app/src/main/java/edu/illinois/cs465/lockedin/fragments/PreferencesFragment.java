package edu.illinois.cs465.lockedin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.ManagePreferences;
import edu.illinois.cs465.lockedin.models.TimeBlockPreference;

public class PreferencesFragment extends Fragment {
    private static final String[] DAYS_OF_WEEK = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", " "};
    private ManagePreferences managePreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_preferences, container, false);
        LinearLayout scrollDays = view.findViewById(R.id.scrollDays);
        managePreferences = new ManagePreferences(requireContext());

        for (String day : DAYS_OF_WEEK) {
            ArrayList<TimeBlockPreference> dayPreferences = getPreferencesForDay(day);
            DayCardFragment dayCardFragment = DayCardFragment.newInstance(day, dayPreferences);
            getChildFragmentManager().beginTransaction()
                    .add(scrollDays.getId(), dayCardFragment)
                    .commit();
        }

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        managePreferences.scheduleBreakNotifications(requireContext());
//    }

    private ArrayList<TimeBlockPreference> getPreferencesForDay(String day) {
        ArrayList<TimeBlockPreference> allPreferences = managePreferences.getPreferences();
        ArrayList<TimeBlockPreference> dayPreferences = new ArrayList<>();
        for (TimeBlockPreference preference : allPreferences) {
            if (preference.getDay().equalsIgnoreCase(day)) {
                dayPreferences.add(preference);
            }
        }
        return dayPreferences;
    }
}
