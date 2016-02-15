package com.gmail.victorchuholskiy.todolist.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.activities.TaskActivity;
import com.gmail.victorchuholskiy.todolist.helpers.DateTimeStringBuilder;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.view.SlideToUnlock;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmFragment extends Fragment{

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Task task;
    private TaskActivity parent;
    private TextView textViewName;
    private TextView textViewAlarmTime;

    public AlarmFragment() {
    }



    public void setTask(Task task) {
        this.task = task;
    }

    public void setParent(TaskActivity parent) {
        this.parent = parent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        StringBuilder msgStr = new StringBuilder();
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));
        Toast.makeText(getContext(), msgStr, Toast.LENGTH_LONG).show();

        mediaPlayer = new MediaPlayer();

        TelephonyManager telephonyManager = (TelephonyManager) getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

        // обработка входящего вызова. Мы не хотим, чтобы сигнал продалжался, если кто-то позвонит.
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Входяший звонок: "
                                + incomingNumber);
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException e) {

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {

                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        if (task.isTaskAlarmWithVibration() == true) {
            vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
            long[] pattern = {1000, 200, 200, 200};
            vibrator.vibrate(pattern, 0);
        }

        try {
            mediaPlayer.setVolume(0.3f, 1.0f);
            mediaPlayer.setDataSource(getContext(),
                    Uri.parse(task.getTaskAlarmTonePath()));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            mediaPlayer.release();
        }

        //Release the lock
        wl.release();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        if (task != null) {
            textViewName = (TextView)view.findViewById(R.id.textViewTaskName);
            textViewName.setText(task.getTaskName());

            textViewAlarmTime = (TextView)view.findViewById(R.id.textViewTaskTime);
            textViewAlarmTime.setText(getString(R.string.task_set_time) + " " + DateTimeStringBuilder.getStringFromTimeInMillis(task.getTaskAlarmDateTime()));
        }

        SlideToUnlock slideToUnlockView = (SlideToUnlock) view.findViewById(R.id.slideToUnlock);
        slideToUnlockView.setExternalListener(new SlideToUnlock.OnSlideToUnlockEventListener() {
            @Override
            public void onSlideToUnlockCanceled() {
            }

            @Override
            public void onSlideToUnlockDone() {
                if (vibrator != null)
                    vibrator.cancel();
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                    } catch (IllegalStateException ise) {
                    }
                    try {
                        mediaPlayer.release();
                    } catch (Exception e) {
                    }
                }
                parent.hideAlarmFragment();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }
        super.onDestroy();
    }
}
