package com.gmail.victorchuholskiy.todolist.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gmail.victorchuholskiy.todolist.activities.MainActivity;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.database.DataBaseManager;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.gmail.victorchuholskiy.todolist.recievers.AlarmManagerBroadcastReceiver;

public class AlarmService extends Service {

	// сервис устанавливает следующий будильник.
	// Суть в том, что одновременно может быть установлен лиш один будильник,
	// так что при изменениях в заметках необходимо перепроверять, правильный ли
	// будильник сейчас активен.

	private DataBaseManager dataBaseManager;
	private IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public AlarmService getService(){
			return AlarmService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		dataBaseManager = DataBaseManager.getInstance(this);
		dataBaseManager.open();
		Task alarm = dataBaseManager.getNextSignalTask();
		dataBaseManager.close();

		AlarmManagerBroadcastReceiver am = new AlarmManagerBroadcastReceiver();
		am.CancelAlarm(this.getApplicationContext());
		if (null != alarm) am.setAlarm(this.getApplicationContext(), alarm);

		return START_NOT_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
