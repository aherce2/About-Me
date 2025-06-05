package edu.illinois.cs465.lockedin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.StudySession;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.R;

public class UnlockedRewardsAdapter extends RecyclerView.Adapter<UnlockedRewardsAdapter.ViewHolder>{
    private List<String> unlocked_rewards;
    public UnlockedRewardsAdapter(ArrayList<String> unlocked_rewards) {
        if (unlocked_rewards == null) {
            this.unlocked_rewards = new ArrayList<>();
        } else {
            this.unlocked_rewards = unlocked_rewards;
        }
    }

    public void updateData(List<String> rewards) {
        this.unlocked_rewards.clear();
        this.unlocked_rewards.addAll(rewards);
    }
    @NonNull
    @Override
    public UnlockedRewardsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unlocked_rewards_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnlockedRewardsAdapter.ViewHolder holder, int position) {
        String reward = unlocked_rewards.get(position);
        holder.rewardNameUnlocked.setText(reward);
        holder.useButton.setOnClickListener(v -> {
            // Handle use button here
            //remove it from the rewards list
            unlocked_rewards.remove(position);
            ManageSession manager = new ManageSession(v.getContext());
            manager.remove_unlocked_reward(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, unlocked_rewards.size());
        });
    }

    @Override
    public int getItemCount() {
        return unlocked_rewards.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rewardNameUnlocked;
        private Button useButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rewardNameUnlocked = itemView.findViewById(R.id.UnlockedRewardName);
            useButton = itemView.findViewById(R.id.useRewardButton);
        }
    }

}
