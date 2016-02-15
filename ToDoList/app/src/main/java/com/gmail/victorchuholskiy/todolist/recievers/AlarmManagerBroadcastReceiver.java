package com.gmail.victorchuholskiy.todolist.recievers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.gmail.victorchuholskiy.todolist.activities.TaskActivity;
import com.gmail.victorchuholskiy.todolist.container.Task;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    // обработка срабатывания "будильника"

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        final int id = bundle.getInt("id");

        Intent signalIntent = new Intent(context, TaskActivity.class);
        signalIntent.putExtra("id", id);
        signalIntent.putExtra("alarm", true);
        signalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(signalIntent);

        final Intent alarmServiceIntent = new Intent(context, AlarmServiceBroadcastReciever.class);
        context.sendBroadcast(alarmServiceIntent, null);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setAlarm(Context context, Task task){
        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("id", task.getId());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, task.getTaskAlarmDateTime(), pi);
    }
}
