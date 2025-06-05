package edu.illinois.cs465.lockedin.models;

import java.util.Objects;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.adapters.ViewHolder;

public class StudySession {
    private String event_name;
    private String date ;
    private String start_time;
    private String end_time;
    private String reward_name;
    private String frequency;
    private String tag;
    private String id;
    // Constructor
    public StudySession(String id, String event_name, String date, String start_time,
                        String end_time, String reward_name, String frequency, String tag) {
        this.id = id;
        this.event_name = event_name;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.reward_name = reward_name;
        this.frequency = frequency;
        this.tag = tag;
    }

    // Getter and Setter

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return event_name;
    }

    public void setName(String name) {
        this.event_name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String d) {
        this.date = d;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReward() {
        return reward_name;
    }

    public void setReward(String reward_name) {
        this.reward_name = reward_name;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StudySession that = (StudySession) obj;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}