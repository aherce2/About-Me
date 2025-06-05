package edu.illinois.cs465.lockedin.fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.illinois.cs465.lockedin.adapters.CompletedExamAdapter;
import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.adapters.UpcomingExamAdapter;
import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.models.Reward;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.StudySession;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompletedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CompletedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompletedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedFragment newInstance(String param1, String param2) {
        CompletedFragment fragment = new CompletedFragment();
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
    private ArrayList<StudySession> sessions;
    CompletedExamAdapter adapter;
    SharedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.RVExamComplete);
        TextView placeholder = view.findViewById(R.id.placeholderTextCompleted);

        ManageSession manager = new ManageSession(getContext());
        sessions = manager.get_completed_sessions();
        ArrayList<StudySession> filtered_sessions = new ArrayList<>();
        filtered_sessions = getCompletedWeekSessions(sessions);
        adapter = new CompletedExamAdapter(filtered_sessions, getContext());
        if (adapter.getItemCount() == 0) {
            placeholder.setVisibility(View.VISIBLE);
        } else {
            placeholder.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getCompletedSessions().observe(getViewLifecycleOwner(), this::updateAdapterData);

        return view;
    }
    private LocalDate lastUpdateDate;
    @Override
    public void onResume() {
        super.onResume();
        checkAndUpdateData();
    }

    private void checkAndUpdateData() {
        LocalDate today = LocalDate.now();
        if (lastUpdateDate == null || !lastUpdateDate.equals(today)) {
            ManageSession manager = new ManageSession(getContext());
            ArrayList<StudySession> sessions = manager.get_completed_sessions();
            ArrayList<StudySession> filtered_sessions = getCompletedWeekSessions(sessions);
            adapter.updateData(filtered_sessions);
            adapter.notifyDataSetChanged();
            lastUpdateDate = today;
        }
    }


    private ArrayList<StudySession> getCompletedWeekSessions(ArrayList<StudySession> allSessions) {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekEarlier = today.minusWeeks(1);

        return allSessions.stream()
                .filter(session -> {
                    LocalDate sessionDate = LocalDate.parse(session.getDate(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    return !sessionDate.isAfter(today) && !sessionDate.isBefore(oneWeekEarlier);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void updateAdapterData(List<StudySession> sessions) {
        ArrayList<StudySession> filteredSessions = getCompletedWeekSessions(new ArrayList<>(sessions));
        adapter.updateData(filteredSessions);
        adapter.notifyDataSetChanged();
    }
}