package edu.illinois.cs465.lockedin.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.StudySession;

public class EventsAdapter extends ArrayAdapter<StudySession> {

    public EventsAdapter(@NonNull Context context, @NonNull List<StudySession> sessions) {
        super(context, 0, sessions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
        }

        StudySession currentSession = getItem(position);

        TextView eventNameTextView = listItem.findViewById(R.id.eventName);
        TextView eventTimeTextView = listItem.findViewById(R.id.eventTime);
//        TextView eventRewardTextView = listItem.findViewById(R.id.eventReward);

        if (currentSession != null) {
            eventNameTextView.setText(currentSession.getName());
            eventTimeTextView.setText(currentSession.getStart_time() + " - " + currentSession.getEnd_time());
//            eventRewardTextView.setText("Reward: " + currentSession.getReward());
        }

        return listItem;
    }
}
