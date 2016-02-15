package com.gmail.victorchuholskiy.todolist.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.activities.TaskActivity;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.database.DataBaseManager;
import com.gmail.victorchuholskiy.todolist.recievers.AlarmManagerBroadcastReceiver;

public class NextSignalTaskService extends Service {

	private DataBaseManager dataBaseManager;
	private IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public NextSignalTaskService getService(){
			return NextSignalTaskService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("aaa", "onStartCommand next");
		dataBaseManager = DataBaseManager.getInstance(this);
		dataBaseManager.open();
		Task alarm = dataBaseManager.getNextSignalTask();
		dataBaseManager.close();

		if (null != alarm) {
			Intent openIntent = new Intent(this, TaskActivity.class);
			openIntent.putExtra("id", alarm.getId());
			openIntent.putExtra("alarm", false);
			startActivity(openIntent);
		} else{
			Toast.makeText(this, getResources().getString(R.string.note_not_found), Toast.LENGTH_SHORT).show();
		}

		return super.onStartCommand(intent, flags, startId);
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
