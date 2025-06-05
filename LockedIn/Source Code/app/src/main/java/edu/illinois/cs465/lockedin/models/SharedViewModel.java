package edu.illinois.cs465.lockedin.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.lockedin.models.Reward;


public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<StudySession>> sessions = new MutableLiveData<>();
    private final MutableLiveData<List<Reward>> rewards = new MutableLiveData<>();
//    private final MutableLiveData<List<StudySession>> sessions = new MutableLiveData<>();
    private final MutableLiveData<List<StudySession>> completed_sessions = new MutableLiveData<>();
//    private final MutableLiveData<List<Reward>> rewards = new MutableLiveData<>();
    private final MutableLiveData<List<Reward>> unlocked_rewards = new MutableLiveData<>();



    public LiveData<List<StudySession>> getSessions() {
        return sessions;
    }
    public LiveData<List<StudySession>> getCompletedSessions() {
        return completed_sessions;
    }
    public LiveData<List<Reward>> getRewards() {
        return rewards;
    }
    public LiveData<List<Reward>> getUnlockedRewards() {
        return unlocked_rewards;
    }


    public void updateSessions(List<StudySession> newSessions) {
        sessions.setValue(newSessions);
    }
    public void updateCompletedSessions(List<StudySession> newSessions) {
        completed_sessions.setValue(newSessions);
    }
    public void updateRewards(List<Reward> newRewards) {
        rewards.setValue(newRewards);
    }
    public void updateUnlockedRewards(List<Reward> newRewards) {
        unlocked_rewards.setValue(newRewards);
    }

}