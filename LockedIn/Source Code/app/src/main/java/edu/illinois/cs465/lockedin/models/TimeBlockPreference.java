package edu.illinois.cs465.lockedin.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeBlockPreference implements Parcelable {
    private String day;
    private String type;
    private String startTime;
    private String endTime;
    private String id;

    // Constructor
    public TimeBlockPreference(String id, String day, String type, String startTime, String endTime) {
        this.id = id;
        this.day = day;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Parcelable implementation
    protected TimeBlockPreference(Parcel in) {
        id = in.readString();
        day = in.readString();
        type = in.readString();
        startTime = in.readString();
        endTime = in.readString();
    }

    public static final Creator<TimeBlockPreference> CREATOR = new Creator<TimeBlockPreference>() {
        @Override
        public TimeBlockPreference createFromParcel(Parcel in) {
            return new TimeBlockPreference(in);
        }

        @Override
        public TimeBlockPreference[] newArray(int size) {
            return new TimeBlockPreference[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(day);
        parcel.writeString(type);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
    }
}