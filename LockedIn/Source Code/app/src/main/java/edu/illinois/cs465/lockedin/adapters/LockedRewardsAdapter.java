package edu.illinois.cs465.lockedin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.StudySession;

public class LockedRewardsAdapter extends RecyclerView.Adapter<LockedRewardsAdapter.ViewHolder>{

    private List<String> locked_rewards;
    private Context context;
    public LockedRewardsAdapter(Context context, ArrayList<String> locked_rewards) {
        this.context = context;
        if (locked_rewards == null) {
            this.locked_rewards = new ArrayList<>();
        } else {
            this.locked_rewards = locked_rewards;
        }
    }
    public void updateData(List<String> rewards) {
        this.locked_rewards.clear();
        this.locked_rewards.addAll(rewards);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LockedRewardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.locked_rewards_layout, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LockedRewardsAdapter.ViewHolder holder, int position) {
        String reward = locked_rewards.get(position);
        holder.rewardNameLocked.setText(reward);
        holder.editButton.setOnClickListener(v -> {
            // Handle edit button here
            //should open edit session dialog
            showEditDialog(position);
        });
    }
    private void showEditDialog(int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null);
        TextInputEditText input = dialogView.findViewById(R.id.textInputEditText);
        input.setText(locked_rewards.get(position));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Edit Reward");
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newText = input.getText().toString();
            ManageSession manageSession = new ManageSession(context);
            ArrayList<StudySession> sessions = manageSession.get_sessions();
            StudySession sessionToUpdate = sessions.get(position);
            sessionToUpdate.setReward(newText);
            manageSession.updateSession(sessionToUpdate);
            locked_rewards.set(position, newText);
            notifyItemChanged(position);

            SharedViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SharedViewModel.class);
            viewModel.updateSessions(manageSession.get_sessions());
            viewModel.updateRewards(manageSession.get_rewards());


        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    @Override
    public int getItemCount() {
        return locked_rewards.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rewardNameLocked;
        private ImageButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rewardNameLocked = itemView.findViewById(R.id.LockedRewardName);
            editButton = itemView.findViewById(R.id.editRewardButton);
        }
    }

}