package edu.illinois.cs465.lockedin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.illinois.cs465.lockedin.models.ManageSession;
import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.SharedViewModel;
import edu.illinois.cs465.lockedin.models.StudySession;
import edu.illinois.cs465.lockedin.adapters.UpcomingExamAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpcomingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpcomingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpcomingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingFragment newInstance(String param1, String param2) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ArrayList<StudySession> sessions;
    UpcomingExamAdapter adapter;
    private SharedViewModel viewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.RVExam);
        TextView placeholder = view.findViewById(R.id.placeholderTextUpcoming);


        ArrayList<StudySession> filtered_sessions = new ArrayList<>();
        ManageSession manager = new ManageSession(getContext());
        sessions = manager.get_sessions();
        filtered_sessions = getUpcomingWeekSessions(sessions);
        adapter = new UpcomingExamAdapter(filtered_sessions);
        if (adapter.getItemCount() == 0) {
            placeholder.setVisibility(View.VISIBLE);
        } else {
            placeholder.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getSessions().observe(getViewLifecycleOwner(), this::updateAdapterData);
        return view;
    }
    private ArrayList<StudySession> getUpcomingWeekSessions(ArrayList<StudySession> allSessions) {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusWeeks(1);

        return allSessions.stream()
                .filter(session -> {
                    LocalDate sessionDate = LocalDate.parse(session.getDate(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    return !sessionDate.isBefore(today) && !sessionDate.isAfter(oneWeekLater);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private void updateAdapterData(List<StudySession> sessions) {
        ArrayList<StudySession> filteredSessions = getUpcomingWeekSessions(new ArrayList<>(sessions));
        adapter.updateData(filteredSessions);
//        adapter.updateData(sessions);
        adapter.notifyDataSetChanged();
    }

}