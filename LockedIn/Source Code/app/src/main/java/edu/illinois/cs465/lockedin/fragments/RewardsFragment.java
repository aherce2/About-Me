package edu.illinois.cs465.lockedin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.adapters.LockedRewardsAdapter;
import edu.illinois.cs465.lockedin.adapters.UnlockedRewardsAdapter;
import edu.illinois.cs465.lockedin.adapters.UpcomingExamAdapter;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.Reward;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.StudySession;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RewardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardsFragment newInstance(String param1, String param2) {
        RewardsFragment fragment = new RewardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    LockedRewardsAdapter adapter;
    UnlockedRewardsAdapter adapter_u;
    SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.rewards_test, container, false);
        RecyclerView unlocked_rewardsRV = view.findViewById(R.id.rvUnlockedRewards);
        RecyclerView locked_rewardsRV = view.findViewById(R.id.rvLockedRewards);
        locked_rewardsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ManageSession manager = new ManageSession(getContext());

        ArrayList<Reward> unlocked_rewards = manager.get_unlocked_rewards();
        ArrayList<String> unlocked_reward_strings = new ArrayList<>();
        for (Reward reward : unlocked_rewards) {
            unlocked_reward_strings.add(reward.getReward());
        }
        unlocked_rewardsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_u = new UnlockedRewardsAdapter(unlocked_reward_strings);
        unlocked_rewardsRV.setAdapter(adapter_u);

        //manager.clear_sessions();
        ArrayList<Reward> rewards = manager.get_rewards();
        ArrayList<String> reward_strings = new ArrayList<>();
        for (Reward reward : rewards) {
            reward_strings.add(reward.getReward());
        }
        adapter = new LockedRewardsAdapter(getContext(), reward_strings);
        locked_rewardsRV.setAdapter(adapter);
        viewModel.getRewards().observe(getViewLifecycleOwner(), this::updateAdapterData);
        viewModel.getUnlockedRewards().observe(getViewLifecycleOwner(), this::updateAdapterData2);
        return view;
    }

    private void updateAdapterData(List<Reward> locked_rewards) {
        List<String> rewardStrings = new ArrayList<>();
        for (Reward reward : locked_rewards) {
            rewardStrings.add(reward.getReward());
        }

        adapter.updateData(rewardStrings);
        adapter.notifyDataSetChanged();

    }

    private void updateAdapterData2(List<Reward> unlocked_rewards) {
        List<String> rewardStrings2 = new ArrayList<>();

        for (Reward reward : unlocked_rewards) {
            rewardStrings2.add(reward.getReward());
        }

        adapter_u.updateData(rewardStrings2);
        adapter_u.notifyDataSetChanged();
    }
}