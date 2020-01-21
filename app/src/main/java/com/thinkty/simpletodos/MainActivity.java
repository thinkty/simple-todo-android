package com.thinkty.simpletodos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout preferredTimeContainer;
    private TextView preferredTime;
    private boolean isNotificationOn;
    private String notificationTime;
    private final String TAG = "simpleTodos_Main";
    private int preferredHour;
    private int preferredMinute;

    /**
     * @brief Helper to initialize variables
     */
    private void init() {

        preferredTime = (TextView) findViewById(R.id.preferred_time_textview);
        preferredTimeContainer = (LinearLayout) findViewById(R.id.preferred_time_container);

        // Retrieve preference on notification
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        isNotificationOn = preferences.getBoolean("notification", false);
        notificationTime = preferences.getString("time", "");
        preferredHour = preferences.getInt("preferred_hour", -1);
        preferredMinute = preferences.getInt("preferred_minute", -1);

        // Set time for preferred time text view
        if (notificationTime.isEmpty()) {
            // Set current time on the text view
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            preferredTime.setText(format.format(new Date()));
        } else {
            preferredTime.setText(notificationTime);
        }

        // Set the appropriate background
        if (isNotificationOn) {
            preferredTimeContainer.setBackground(getDrawable(R.drawable.preferred_time_background_notification_on_drawable));
        } else {
            preferredTimeContainer.setBackground(getDrawable(R.drawable.preferred_time_background_drawable));
        }
    }

    /**
     * @brief Helper to enable/disable notification of TODOs
     * @param status if true, enable notification
     */
    public void setNotification(boolean status) {

        // Get the alarm manager from the OS
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Validate alarm manager
        if (alarmManager == null) {
            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve preference on notification
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferredHour = preferences.getInt("preferred_hour", -1);
        preferredMinute = preferences.getInt("preferred_minute", -1);

        Intent intent = new Intent(this, TodoAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        // Set alarm
        if (status) {
            if (preferredHour == -1 || preferredMinute == -1) {
                Log.e(TAG, "Preferred hour/minute is -1, unable to set alarm\n");
                return;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, preferredHour);
            calendar.set(Calendar.MINUTE, preferredMinute);

            // Set the alarm to start at set time
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
            Toast.makeText(getApplicationContext(), "Alarm on", Toast.LENGTH_SHORT).show();
            // Change background
            preferredTimeContainer.setBackground(getDrawable(R.drawable.preferred_time_background_notification_on_drawable));
            isNotificationOn = true;
        }
        // Cancel alarm
        else {
            alarmManager.cancel(alarmIntent);
            Toast.makeText(getApplicationContext(), "Alarm off", Toast.LENGTH_SHORT).show();
            // Change background
            preferredTimeContainer.setBackground(getDrawable(R.drawable.preferred_time_background_drawable));
            isNotificationOn = false;
        }

    }

    /**
     * @brief Helper to initialize event handlers
     */
    private void initOnClickListeners() {

        // Set onClickListener for the container so that it turns on notification
        preferredTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the notification is on, disable it
                if (isNotificationOn) {
                    setNotification(false);
                } else {
                    setNotification(true);
                }
                updatePreferences();
            }
        });

        // Set onClickListener for the editText
        preferredTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        preferredHour = hourOfDay;
                        preferredMinute = minute;

                        // Add extra 0 to the minute if single digit
                        if (minute < 10) {
                            notificationTime = hourOfDay + ":0" + minute;
                        } else {
                            notificationTime = hourOfDay + ":" + minute;
                        }

                        // update the text view
                        preferredTime.setText(notificationTime);
                        updatePreferences();
                    }
                }, 0, 0, false);

                // Show the time picker dialog to the user
                dialog.show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        init();
        // Initialize event handlers
        initOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Preferences >>\n\tisNotificationOn : " + isNotificationOn +
                "\n\tnotificationTime : " + notificationTime +
                "\n\tpreferredHour : " + preferredHour +
                "\n\tpreferredMinute : " + preferredMinute +
                "\n");
    }

    /**
     * @brief Helper to update sharedPreferences
     */
    private void updatePreferences() {

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Update the notification variable
        editor.putBoolean("notification", isNotificationOn);
        editor.putString("time", notificationTime);
        editor.putInt("preferred_hour", preferredHour);
        editor.putInt("preferred_minute", preferredMinute);
        editor.apply();
        Log.d(TAG, "Updating shared preferences\n");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Update preferences before the app closes
        updatePreferences();
    }
}
