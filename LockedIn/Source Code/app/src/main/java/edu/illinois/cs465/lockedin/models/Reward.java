package edu.illinois.cs465.lockedin.models;

import java.util.Objects;

public class Reward {
    private String id;
    private String reward;
    private String date;
    private String time;

    public Reward(String id, String reward, String date, String time) {
        this.id = id;
        this.reward = reward;
        this.date = date;
        this.time = time;
    }
    public String getReward() {
        return reward;
    }

    public void setReward(String reward_name) {
        this.reward = reward_name;
    }
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String d) {
        this.date = d;
    }

    public String getStart_time() {
        return time;
    }

    public void setStart_time(String start_time) {
        this.time = start_time;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reward reward1 = (Reward) o;
        return Objects.equals(id, reward1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
