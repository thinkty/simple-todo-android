package com.thinkty.simpletodos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TodoAlarmReceiver extends BroadcastReceiver {

    private final String TAG = "simpleTodos_TodoAlarmReceiver";

    public TodoAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm again since the alarms get disabled after boot
            MainActivity activity = new MainActivity();
            activity.setNotification(true);
            return;
        }

        // Since Android OS caps maximum 10 seconds for time to react,
        // quickly call a service to make it in time (sort of a work around)

        Log.d(TAG, "Received intent, starting service\n");
        Intent intent1 = new Intent(context, TodoAlarmIntentService.class);
        context.startService(intent1);
    }
}
