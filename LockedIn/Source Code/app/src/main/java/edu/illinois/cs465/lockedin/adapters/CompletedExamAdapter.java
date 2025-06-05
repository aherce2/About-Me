package edu.illinois.cs465.lockedin.adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.activities.SessionCardActivity;
import edu.illinois.cs465.lockedin.fragments.EditSessionDialog;
import edu.illinois.cs465.lockedin.models.Reward;
import edu.illinois.cs465.lockedin.models.StudySession;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.SharedViewModel;

public class CompletedExamAdapter extends RecyclerView.Adapter<CompletedExamAdapter.ViewHolder> {
    private final ArrayList<StudySession> completedStudySessions;
    private final ManageSession manageSession;
    private final SharedViewModel viewModel;

    // Constructor
    public CompletedExamAdapter(ArrayList<StudySession> completedStudySessions, Context context) {
        if (completedStudySessions == null) {
            this.completedStudySessions = new ArrayList<>();
        } else {
            this.completedStudySessions = completedStudySessions;
        }
        this.manageSession = new ManageSession(context);
        if (context instanceof AppCompatActivity) {
            this.viewModel = new ViewModelProvider((AppCompatActivity) context).get(SharedViewModel.class);
        } else {
            throw new IllegalArgumentException("Context must be an instance of AppCompatActivity");
        }
    }

    @NonNull
    @Override
    public CompletedExamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedExamAdapter.ViewHolder holder, int position) {
        StudySession model = completedStudySessions.get(position);
        holder.cardHeader.setText(model.getName());
        holder.date_session.setText(model.getDate());
        holder.time_session.setText(model.getStart_time() + " - " + model.getEnd_time());
        if (manageSession.RewardExistInUnlocked(model.getID())) {
            holder.view_reward.setVisibility(View.VISIBLE);
            holder.view_reward.setOnClickListener(v -> {
                Context context = v.getContext();
                showRewardDialog(context, model, position);
            });
        } else {
            holder.view_reward.setVisibility(View.GONE);
        }
        Log.d("completedFragment", "Data loaded: " + completedStudySessions.size());
    }

    private void showRewardDialog(Context c, StudySession s, int position) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(c);
        View dialogView = LayoutInflater.from(c).inflate(R.layout.view_reward_layout, null);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LottieAnimationView animationView = dialogView.findViewById(R.id.reward_animation);
        animationView.playAnimation();
        TextView rewardText = dialogView.findViewById(R.id.reward_text);
        TextView rewardText2 = dialogView.findViewById(R.id.reward_text_2);
        TextView reward = dialogView.findViewById(R.id.rewardname);
        Button claimButton = dialogView.findViewById(R.id.claim_button);

        reward.setText(s.getReward());

        claimButton.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Reward Claimed!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            UseReward(position);
        });

        dialog.show();
    }

    private void UseReward(int position) {
        List<Reward> rewards = manageSession.get_unlocked_rewards();
        if (rewards.isEmpty()) {
            return;
        }
        manageSession.remove_unlocked_reward(position);
        viewModel.updateUnlockedRewards(manageSession.get_unlocked_rewards());
        viewModel.updateCompletedSessions(manageSession.get_completed_sessions());
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        Log.d("completedFragment", "Item count: " + completedStudySessions.size());
        return completedStudySessions.size();
    }

    public void updateData(List<StudySession> newSessions) {
        this.completedStudySessions.clear();
        this.completedStudySessions.addAll(newSessions);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardHeader;
        private final TextView date_session;
        private final TextView time_session;
        private Button view_reward;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardHeader = itemView.findViewById(R.id.card_header_complete);
            date_session = itemView.findViewById(R.id.card_date_complete);
            time_session = itemView.findViewById(R.id.card_time_complete);
            view_reward = itemView.findViewById(R.id.view_reward_button);
        }
    }


}
