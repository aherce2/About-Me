package edu.illinois.cs465.lockedin.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.activities.SessionCardActivity;
import edu.illinois.cs465.lockedin.fragments.EditSessionDialog;
import edu.illinois.cs465.lockedin.models.StudySession;

public class UpcomingExamAdapter extends RecyclerView.Adapter<UpcomingExamAdapter.ViewHolder> {
    private ArrayList<StudySession> upcomingStudySessions;

    public UpcomingExamAdapter(ArrayList<StudySession> upcomingStudySessions) {
        if (upcomingStudySessions == null) {
            this.upcomingStudySessions = new ArrayList<>();
        } else {
            this.upcomingStudySessions = upcomingStudySessions;
        }
    }

    @NonNull
    @Override
    public UpcomingExamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_card_layout_default, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingExamAdapter.ViewHolder holder, int position) {
        StudySession model = upcomingStudySessions.get(position);
        holder.cardHeader.setText(model.getName());


        int tagColor = getColorForTag(model.getTag(), holder);
        holder.cardHeader.setBackgroundColor(tagColor);
        Log.d("UpcomingExamAdapter", "Tag: " + model.getTag() + " Color: " + tagColor);


        holder.date_session.setText(model.getDate());
        holder.time_session.setText(model.getStart_time() + " - " + model.getEnd_time());
        holder.editButton.setOnClickListener(v -> {
            Context context = v.getContext();
                    EditSessionDialog editDialog = new EditSessionDialog();
                    editDialog.setExistingSession(model);
                    editDialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "EditSessionDialog");
                });
        Log.d("UpcomingFragment", "Data loaded: " + upcomingStudySessions.size());
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Gson gson = new Gson();
            Intent intent = new Intent(context, SessionCardActivity.class);
            intent.putExtra("SESSION", gson.toJson(model));
            context.startActivity(intent);
        });

    }

    public void updateData(List<StudySession> newSessions) {
        this.upcomingStudySessions.clear();
        this.upcomingStudySessions.addAll(newSessions);
    }

    @Override
    public int getItemCount() {
        return upcomingStudySessions.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardHeader;
        private TextView date_session;
        private TextView time_session;
        private ImageButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardHeader = itemView.findViewById(R.id.card_header);
            date_session = itemView.findViewById(R.id.card_date);
            time_session = itemView.findViewById(R.id.card_time);
            editButton = itemView.findViewById(R.id.edit_icon_upcoming);
        }
    }
    public int getColorForTag(String tag, UpcomingExamAdapter.ViewHolder holder) {
        switch (tag) {
            case "CS 465":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.tag_cs465);
            case "CS 374":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.tag_cs374);
            case "CS 461":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.tag_cs461);
            case "ECE 445":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.tag_ece445);
            case "ECE 408":
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.tag_ece408);
            default:
                return ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
        }
    }
}

