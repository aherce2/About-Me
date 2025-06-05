package edu.illinois.cs465.lockedin.fragments;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.Set;

public class EventDayDecorator implements DayViewDecorator {

    private final Set<CalendarDay> dates;

    // Constructor to initialize the event dates
    public EventDayDecorator(Set<CalendarDay> eventDates) {
        this.dates = new HashSet<>(eventDates); // Convert the event dates to a set for easy lookup
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day); // Check if the current day is an event day
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED)); // Set text color to red for event days
        // You can add other decorations like background color, borders, or custom icons
    }
}
