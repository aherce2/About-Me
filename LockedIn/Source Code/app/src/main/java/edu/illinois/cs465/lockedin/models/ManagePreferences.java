package edu.illinois.cs465.lockedin.models;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import edu.illinois.cs465.lockedin.R;

public class ManagePreferences{
    private static final String PREF_NAME = "TimeBlockPreferences";
    private static final String KEY_PREFERENCES = "preferences";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ManagePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public ArrayList<TimeBlockPreference> getPreferences() {
        String json = sharedPreferences.getString(KEY_PREFERENCES, null);
        Type type = new TypeToken<ArrayList<TimeBlockPreference>>() {}.getType();
        ArrayList<TimeBlockPreference> preferences = gson.fromJson(json, type);
        return preferences == null ? new ArrayList<>() : preferences;
    }

    public void savePreferences(ArrayList<TimeBlockPreference> preferences) {
        String json = gson.toJson(preferences);
        sharedPreferences.edit().putString(KEY_PREFERENCES, json).apply();
    }

    public void updatePreference(TimeBlockPreference updatedPreference) {
        ArrayList<TimeBlockPreference> preferences = getPreferences();
        for (int i = 0; i < preferences.size(); i++) {
            if (preferences.get(i).getId().equals(updatedPreference.getId())) {
                preferences.set(i, updatedPreference);
                break;
            }
        }
        savePreferences(preferences);
    }

    public ArrayList<TimeBlockPreference> getPreferencesForDay(String day) {
        ArrayList<TimeBlockPreference> allPreferences = getPreferences();
        ArrayList<TimeBlockPreference> dayPreferences = new ArrayList<>();

        for (TimeBlockPreference preference : allPreferences) {
            if (preference.getDay().equalsIgnoreCase(day)) {
                dayPreferences.add(preference);
            }
        }

        return dayPreferences;
    }
}
