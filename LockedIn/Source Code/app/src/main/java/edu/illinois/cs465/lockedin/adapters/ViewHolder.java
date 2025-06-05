package edu.illinois.cs465.lockedin.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.illinois.cs465.lockedin.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView cardHeader;
    private TextView date_session;
    private TextView time_session;
    private ImageButton editButton;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        cardHeader = itemView.findViewById(R.id.card_header);
        date_session = itemView.findViewById(R.id.card_date);
        time_session = itemView.findViewById(R.id.card_time);
        //editButton = itemView.findViewById(R.id.edit_icon_upcoming);

    }
}
