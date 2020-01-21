package com.thinkty.simpletodos;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TodoAlarmIntentService extends IntentService {

    private final String CHANNEL_ID = "simple_todo_notification_channel";
    private final int NOTIFICATION_ID = 6969;
    private final String TAG = "simpleTodos_TodoAlarmIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TodoAlarmIntentService() {
        super("TodoAlarmIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent incomingIntent) {

        // Create a pending intent that opens the app and takes the user to this activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Log.d(TAG, "Creating notification\n");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Todo Reminder")
                .setContentTitle("This is a reminder to check your todo lists")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "Notification sent\n");
    }
}
