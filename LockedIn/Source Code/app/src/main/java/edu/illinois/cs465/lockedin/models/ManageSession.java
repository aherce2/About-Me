package edu.illinois.cs465.lockedin.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.activities.NotificationDialogActivity;
import edu.illinois.cs465.lockedin.adapters.ViewHolder;

public class ManageSession {
    private static final String PREF_NAME = "StudySessions";
    private static final String SESSIONS_UPCOMING = "upcoming_study_sessions_list";
    private static final String LOCKED_REWARDS = "locked_rewards_list";
    private static final String UNLOCKED_REWARDS = "unlocked_rewards_list";
    private static final String SESSIONS_COMPLETED = "completed_study_sessions_list";
    private static final String FAVORITE_REWARDS = "favorite_rewards";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ManageSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();;
    }

    public ArrayList<StudySession> get_sessions() {

//        Use to clear data
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();

        String json = sharedPreferences.getString(SESSIONS_UPCOMING, null);
        Type type = new TypeToken<ArrayList<StudySession>>() {}.getType();
        ArrayList<StudySession> studySessions = gson.fromJson(json, type);
        if (studySessions == null) {
            studySessions = new ArrayList<>();
        }
        return studySessions;
    }

    public void updateSession(StudySession updatedSession) {
        ArrayList<StudySession> sessions = get_sessions();
        ArrayList<Reward> rewards = get_rewards();
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getID().equals(updatedSession.getID())) {
                sessions.set(i, updatedSession);
                Reward r = rewards.get(i);
                r.setReward(updatedSession.getReward());
                rewards.set(i, r);
                break;
            }
        }
        add_helper(sessions, rewards);
    }

    public ArrayList<StudySession> get_completed_sessions() {
        String json = sharedPreferences.getString(SESSIONS_COMPLETED, null);
        Type type = new TypeToken<ArrayList<StudySession>>() {}.getType();
        ArrayList<StudySession> studySessions = gson.fromJson(json, type);
        if (studySessions == null) {
            studySessions = new ArrayList<>();
        }
        return studySessions;
    }

    public ArrayList<Reward> get_rewards() {
        String json = sharedPreferences.getString(LOCKED_REWARDS, null);
        Type type = new TypeToken<ArrayList<Reward>>() {}.getType();
        ArrayList<Reward> rewards = gson.fromJson(json, type);
        if (rewards == null) {
            rewards = new ArrayList<>();
        }
        Log.d("what get_rewards returns", rewards.toString());
        return rewards;
    }

    public ArrayList<Reward> get_unlocked_rewards() {
        String json = sharedPreferences.getString(UNLOCKED_REWARDS, null);
        Type type = new TypeToken<ArrayList<Reward>>() {}.getType();
        ArrayList<Reward> rewards = gson.fromJson(json, type);
        if (rewards == null) {
            rewards = new ArrayList<>();
        }
        return rewards;
    }

    public void add_session(StudySession session, Context context) {
        ArrayList<StudySession> sessions = get_sessions();
        ArrayList<Reward> rewards = get_rewards();
        sessions.add(session);
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("hh:mm a");
        Collections.sort(sessions, (s1, s2) -> {
            LocalDate date1 = LocalDate.parse(s1.getDate(), date_formatter);
            LocalDate date2 = LocalDate.parse(s2.getDate(), date_formatter);
            int dateComparison = date1.compareTo(date2);
            if (dateComparison != 0) {
                return dateComparison;
            }
            LocalTime time1 = LocalTime.parse(s1.getStart_time(), time_formatter);
            LocalTime time2 = LocalTime.parse(s2.getStart_time(), time_formatter);
            return time1.compareTo(time2);
        });
        rewards.add(new Reward(session.getID(), session.getReward(), session.getDate(), session.getStart_time()));
        Collections.sort(rewards, (s1, s2) -> {
            LocalDate date1 = LocalDate.parse(s1.getDate(), date_formatter);
            LocalDate date2 = LocalDate.parse(s2.getDate(), date_formatter);
            int dateComparison = date1.compareTo(date2);
            if (dateComparison != 0) {
                return dateComparison;
            }
            LocalTime time1 = LocalTime.parse(s1.getStart_time(), time_formatter);
            LocalTime time2 = LocalTime.parse(s2.getStart_time(), time_formatter);
            return time1.compareTo(time2);
        });
        add_helper(sessions, rewards);
        //schedule notifications:
        scheduleNotification(session, context);
    }

    public void BreakScheduleNotifications (StudySession session, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationDialogActivity.class);
        intent.putExtra("sessionName", session.getName());
        intent.putExtra("sessionTime", session.getStart_time());
        String breakSchedule = "";
        int[] breakTime = calculateBreakTime(session);
        int hour = breakTime[0];
        int minute = breakTime[1];
        boolean isAM = breakTime[breakTime.length - 1] == 1;

        String formattedHour = String.format("%02d", hour);
        String formattedMinute = String.format("%02d", minute);

        breakSchedule = formattedHour + ":" + formattedMinute + (isAM ? " AM" : " PM");

        intent.putExtra("breakSchedule", breakSchedule);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                session.getID().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
//                        | PendingIntent.FLAG_ALLOW_BACKGROUND_ACTIVITY_STARTS
        );
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

    public int[] calculateBreakTime(StudySession session) {
        // Parse start time
        String startTime = session.getStart_time();
        String[] startTimeParts = startTime.split(":");
        int start_hour = Integer.parseInt(startTimeParts[0]);
        int start_minute = Integer.parseInt(startTimeParts[1].replaceAll("[^0-9]", ""));
        boolean isStartPM = startTime.contains("PM");

        // Convert to 24-hour format if needed
        if (isStartPM && start_hour != 12) {
            start_hour += 12; // Convert PM times, except 12 PM
        } else if (!isStartPM && start_hour == 12) {
            start_hour = 0; // Midnight case
        }

        // Parse end time
        String endTime = session.getEnd_time();
        String[] endTimeParts = endTime.split(":");
        int end_hour = Integer.parseInt(endTimeParts[0]);
        int end_minute = Integer.parseInt(endTimeParts[1].replaceAll("[^0-9]", ""));
        boolean isEndPM = endTime.contains("PM");

        // Convert to 24-hour format if needed
        if (isEndPM && end_hour != 12) {
            end_hour += 12;
        } else if (!isEndPM && end_hour == 12) {
            end_hour = 0;
        }

        // Calculate total session duration in minutes
        int startTotalMinutes = start_hour * 60 + start_minute;
        int endTotalMinutes = end_hour * 60 + end_minute;

        // Adjust for overnight sessions
        if (endTotalMinutes < startTotalMinutes) {
            endTotalMinutes += 24 * 60;
        }

        int sessionDuration = endTotalMinutes - startTotalMinutes;
        int halfSessionMinutes = sessionDuration / 2;

        // Calculate notification time in total minutes
        int notificationTotalMinutes = startTotalMinutes + halfSessionMinutes;

        // Convert back to hours and minutes
        int notificationHour = (notificationTotalMinutes / 60) % 24;
        int notificationMinute = notificationTotalMinutes % 60;

        // Determine if the notification time is AM or PM
        boolean isNotificationAM = notificationHour < 12;

        // Adjust notification hour to 12-hour format
        int adjustedNotificationHour = notificationHour % 12;
        if (adjustedNotificationHour == 0) {
            adjustedNotificationHour = 12;
        }

        // Return the results as an integer array
        int[] calculation = new int[] {
                adjustedNotificationHour,
                notificationMinute,
                end_hour,
                end_minute,
                isNotificationAM ? 1 : 0
        };

        return calculation;
    }





    public void scheduleBreakNotifications (Context context) {
        ArrayList<StudySession> sessions = get_sessions();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (StudySession session : sessions) {
            if (isBreakHandled(session)) {
                continue;
            }
            int notificationHour = calculateBreakTime(session)[0];
            int notificationMinute = calculateBreakTime(session)[1];
            int end_hour = calculateBreakTime(session)[2];
            int end_minute = calculateBreakTime(session)[3];

            // Set notification time in Calendar
            Calendar notificationTime = Calendar.getInstance();
//            notificationTime.set(Calendar.DATE, session.getDate());
            notificationTime.set(Calendar.HOUR_OF_DAY, notificationHour);
            notificationTime.set(Calendar.MINUTE, notificationMinute);
            notificationTime.set(Calendar.SECOND, 0);
            notificationTime.set(Calendar.MILLISECOND, 0);

            // Session end time
            Calendar sessionEndTime = Calendar.getInstance();
            sessionEndTime.set(Calendar.HOUR_OF_DAY, end_hour);
            sessionEndTime.set(Calendar.MINUTE, end_minute);
//            sessionEndTime.set(Calendar.SECOND, 0);
            sessionEndTime.set(Calendar.MILLISECOND, 0);

            if (notificationTime.getTimeInMillis() == Math.round(System.currentTimeMillis()/1000.)*1000
                    && sessionEndTime.getTimeInMillis() > Math.round(System.currentTimeMillis()/1000.)*1000) {

                Log.d("time", "Notification: " + String.valueOf(sessionEndTime.getTimeInMillis()) + " " + "System: " + String.valueOf((System.currentTimeMillis()/1000.) * 1000));

                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("sessionName", session.getName() != null ? session.getName() : "Unknown Session");
                intent.putExtra("sessionTime", session.getStart_time());
                intent.putExtra("breakTime", true);
                intent.putExtra("title", "Upcoming Break");
                intent.putExtra("message", "It's time for a break!");
                if (intent.getExtras() != null) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context,
                            session.getID().hashCode(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
                    );
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
                }

                Intent fullScreenIntent = new Intent(context, NotificationDialogActivity.class);
                fullScreenIntent.putExtra("sessionName", session.getName() != null ? session.getName() : "Unknown Session");
                fullScreenIntent.putExtra("sessionTime", session.getStart_time());
                fullScreenIntent.putExtra("breakTime", true);
                fullScreenIntent.putExtra("title", "Upcoming Break");
                fullScreenIntent.putExtra("message", "It's time for a break!");
                if (intent.getExtras() != null) {
                    PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                            context,
                            session.getID().hashCode(),
                            fullScreenIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
                    );
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), fullScreenPendingIntent);
                }
            }
        }
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            NotificationHelper.sendNotification(context, title, message);
        }
    }
    private static final String BREAK_HANDLED = "break_handled";


    public boolean isBreakHandled(StudySession session) {
        return sharedPreferences.getBoolean(session.getID() + BREAK_HANDLED, false);
    }

    public void setBreakHandled(StudySession session, boolean handled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(session.getID() + BREAK_HANDLED, handled);
        editor.apply();
    }









    private void scheduleNotification(StudySession session, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationDialogActivity.class);
        intent.putExtra("sessionName", session.getName());
        intent.putExtra("sessionTime", session.getStart_time());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                session.getID().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        // Parse session date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDate sessionDate = LocalDate.parse(session.getDate(), dateFormatter);
        LocalTime sessionTime = LocalTime.parse(session.getStart_time(), timeFormatter);
        LocalDateTime sessionDateTime = LocalDateTime.of(sessionDate, sessionTime);

        // Set alarm for 5 minutes before the session
        long triggerAtMillis = sessionDateTime.minusMinutes(5)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }

    public void add_completed_session(StudySession session) {
        ArrayList<StudySession> sessions = get_completed_sessions();
        ArrayList<Reward> rewards = get_unlocked_rewards();
        sessions.add(session);
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("hh:mm a");
        Collections.sort(sessions, (s1, s2) -> {
            LocalDate date1 = LocalDate.parse(s1.getDate(), date_formatter);
            LocalDate date2 = LocalDate.parse(s2.getDate(), date_formatter);
            int dateComparison = date2.compareTo(date1);
            if (dateComparison != 0) {
                return dateComparison;
            }
            LocalTime time1 = LocalTime.parse(s1.getStart_time(), time_formatter);
            LocalTime time2 = LocalTime.parse(s2.getStart_time(), time_formatter);
            return time2.compareTo(time1);
        });
        rewards.add(new Reward(session.getID(), session.getReward(), session.getDate(), session.getStart_time()));
        Collections.sort(rewards, (s1, s2) -> {
            LocalDate date1 = LocalDate.parse(s1.getDate(), date_formatter);
            LocalDate date2 = LocalDate.parse(s2.getDate(), date_formatter);
            int dateComparison = date2.compareTo(date1);
            if (dateComparison != 0) {
                return dateComparison;
            }
            LocalTime time1 = LocalTime.parse(s1.getStart_time(), time_formatter);
            LocalTime time2 = LocalTime.parse(s2.getStart_time(), time_formatter);
            return time2.compareTo(time1);
        });
        add_helper_completed(sessions, rewards);
    }

//    private void sort_sessions(ArrayList<StudySession> sessions) {
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
//
//        Collections.sort(sessions, (s1, s2) -> {
//            LocalDate date1 = LocalDate.parse(s1.getDate(), dateFormatter);
//            LocalDate date2 = LocalDate.parse(s2.getDate(), dateFormatter);
//
//            int dateComparison = date1.compareTo(date2);
//            if (dateComparison != 0) {
//                return dateComparison;
//            }
//
//            LocalTime time1 = LocalTime.parse(s1.getStart_time(), timeFormatter);
//            LocalTime time2 = LocalTime.parse(s2.getStart_time(), timeFormatter);
//            return time1.compareTo(time2);
//        });
//    }

    private void add_helper(ArrayList<StudySession> sessions, ArrayList<Reward> rewards) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(sessions);
        String rewards_json = gson.toJson(rewards);
        System.out.println(rewards_json);
        editor.putString(SESSIONS_UPCOMING, json);
        editor.putString(LOCKED_REWARDS, rewards_json);
        editor.apply();

    }

    private void add_helper_completed(ArrayList<StudySession> sessions, ArrayList<Reward> rewards) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(sessions);
        String rewards_json = gson.toJson(rewards);
        editor.putString(SESSIONS_COMPLETED, json);
        editor.putString(UNLOCKED_REWARDS, rewards_json);
        editor.apply();
    }

    public void remove_session(StudySession session, Reward reward) {
        ArrayList<StudySession> sessions = get_sessions();
        ArrayList<Reward> rewards = get_rewards();
        System.out.println(reward.toString());
        sessions.remove(session);
        System.out.println(rewards.toString());
        rewards.remove(reward);
        System.out.println(rewards.toString());
        add_helper(sessions, rewards);
    }

    public void remove_session_completed(StudySession session, Reward reward) {
        ArrayList<StudySession> sessions = get_completed_sessions();
        ArrayList<Reward> rewards = get_unlocked_rewards();
        sessions.remove(session);
        rewards.remove(reward);
        add_helper_completed(sessions, rewards);
    }

    public void remove_unlocked_reward(int reward) {
        ArrayList<Reward> rewards = get_unlocked_rewards();
        rewards.remove(reward);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(rewards);
        editor.putString(UNLOCKED_REWARDS, json);
        editor.apply();
    }

    public void clear_sessions() {
        ArrayList<StudySession> sessions = get_sessions();
        ArrayList<Reward> rewards = get_rewards();
        sessions.clear();
        rewards.clear();
        add_helper(sessions, rewards);
    }

    public void clear_sessions_completed() {
        ArrayList<StudySession> sessions = get_completed_sessions();
        ArrayList<Reward> rewards = get_unlocked_rewards();
        sessions.clear();
        rewards.clear();
        add_helper_completed(sessions, rewards);
    }

    //methods to check for upcoming sessions for notification handling:
    public StudySession getUpcomingSession() {
        ArrayList<StudySession> sessions = get_sessions();
        if (sessions.isEmpty()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        for (StudySession session : sessions) {
            LocalDate sessionDate = LocalDate.parse(session.getDate(), dateFormatter);
            LocalTime sessionTime = LocalTime.parse(session.getStart_time(), timeFormatter);
            LocalDateTime sessionDateTime = LocalDateTime.of(sessionDate, sessionTime);

            Duration timeUntilSession = Duration.between(now, sessionDateTime);
            long minutesUntilSession = timeUntilSession.toMinutes();

            if (minutesUntilSession > 0 && minutesUntilSession <= 5) {
                return session;
            }
        }

        return null;
    }

    public boolean RewardExistInUnlocked(String Id) {
        ArrayList<Reward> rewards = get_unlocked_rewards();
        for (Reward reward : rewards) {
            if (reward.getID().equals(Id)) {
                return true;
            }
        }
        return false;
    }

}
