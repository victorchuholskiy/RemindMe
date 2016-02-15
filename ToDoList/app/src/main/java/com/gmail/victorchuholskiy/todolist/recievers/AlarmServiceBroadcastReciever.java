package com.gmail.victorchuholskiy.todolist.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.victorchuholskiy.todolist.services.AlarmService;

public class AlarmServiceBroadcastReciever extends BroadcastReceiver {

	// предназначен для запуска сервиса установки следующего "будильника"
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, AlarmService.class));
	}

}
