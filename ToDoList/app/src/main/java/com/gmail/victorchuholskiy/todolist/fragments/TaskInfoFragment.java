package com.gmail.victorchuholskiy.todolist.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.helpers.DateTimeStringBuilder;
import com.gmail.victorchuholskiy.todolist.container.RingtoneList;
import com.gmail.victorchuholskiy.todolist.container.Task;

import java.util.Calendar;


public class TaskInfoFragment extends Fragment {

    private Task editingTask;
    private EditText editTextName;
    private EditText editTextDescription;
    private TextView textViewAlarmOnOff;
    private TextView textViewTime;
    private TextView textViewAlarmDateTime;
    private TextView textViewAlarmTone;
    private TextView textViewVibrationOnOff;
    private TextView textViewSendToCalendar;

    private LinearLayout toneContainer;
    private ImageView playTone;

    private boolean editState = false;

    private MediaPlayer mediaPlayer;
    private CountDownTimer alarmToneTimer;

    public TaskInfoFragment() {
        // Required empty public constructor
    }

    public void setTask(Task task) {
        this.editingTask = task;
    }

    public void setEditState(boolean editState) {
        this.editState = editState;
        changeState(this.editState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_task_info, container, false);

        editTextName = (EditText)view.findViewById(R.id.editTextName);
        editTextDescription = (EditText)view.findViewById(R.id.editTextDescription);
        textViewAlarmOnOff = (TextView)view.findViewById(R.id.textViewAlarmOnOff);
        textViewTime = (TextView)view.findViewById(R.id.textViewTime);
        textViewAlarmDateTime = (TextView)view.findViewById(R.id.textViewAlarmDateTime);
        textViewAlarmTone = (TextView)view.findViewById(R.id.textViewAlarmTone);
        textViewVibrationOnOff = (TextView)view.findViewById(R.id.textViewVibrationOnOff);
        textViewSendToCalendar = (TextView)view.findViewById(R.id.textViewSendToCalendar);

        toneContainer = (LinearLayout)view.findViewById(R.id.toneContainer);
        playTone = (ImageView)view.findViewById(R.id.playButton);

        playTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTone(editingTask.getTaskAlarmTonePath());
            }
        });

        mediaPlayer = new MediaPlayer();

        return view;
    }

    @Override
    public void onStart() {
        super.onResume();
        updateData();
    }

    private void changeState(boolean editState){
        if (editTextName != null) {
            editTextName.setClickable(editState);
            editTextName.setEnabled(editState);
            editTextName.setFocusable(editState);
            if (editState) editTextName.setFocusableInTouchMode(editState);
        }
        if (editTextDescription != null) {
            editTextDescription.setClickable(editState);
            editTextDescription.setEnabled(editState);
            editTextDescription.setFocusable(editState);
            if (editState) editTextDescription.setFocusableInTouchMode(editState);
        }
    }

    public String getName(){
        return ((editTextName != null) ? editTextName.getText().toString() : "");
    }

    public String getDescription(){
        return ((editTextDescription != null) ? editTextDescription.getText().toString() : "");
    }

    public void stopMediaPlayer() {
        if (alarmToneTimer != null)
            alarmToneTimer.cancel();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    public void onAlarm(Calendar calendar){
        if (editingTask != null) {
            editingTask.setTaskAlarmDateTime(calendar.getTimeInMillis());
            if (!editingTask.isTaskWithAlarm()) {
                editingTask.setTaskWithAlarm(true);

                // если список рингтонов еще не загрузился, то ждем окончания
                // (крайне маловероятно, но всегда лучше подстраховаться)
                if (!RingtoneList.isLoaded()) {
                    try {
                        RingtoneList.getLoadThread().join();
                    } catch (InterruptedException e) {
                    }
                }
                editingTask.setTaskAlarmTonePath(RingtoneList.getAlarmTonePaths().get(0));
                editingTask.setTaskAlarmWithVibration(false);
            }
            updateAlarmData();
        }
    }

    public void offAlarm(){
        if (editingTask != null) {
            editingTask.setTaskWithAlarm(false);
            editingTask.setTaskAlarmTonePath(null);
            editingTask.setTaskAlarmWithVibration(false);
            updateAlarmData();
        }
    }

    public void onVibration(){
        if (editingTask != null) {
            editingTask.setTaskAlarmWithVibration(true);
            updateAlarmData();
        }
    }

    public void offVibration(){
        if (editingTask != null) {
            editingTask.setTaskAlarmWithVibration(false);
            updateAlarmData();
        }
    }

    public void sendToCalendarChangeState(){
        editingTask.setCalendarEventId(editingTask.getCalendarEventId() > 0 ? 0 : 1);
        if (editingTask.getCalendarEventId() > 0) {
            textViewSendToCalendar.setText(getResources().getString(R.string.task_event_in_calendar));
        }
        else {
            textViewSendToCalendar.setText(getResources().getString(R.string.task_event_not_in_calendar));
        }
    }

    private void updateAlarmData(){
        if (editingTask != null) {
            if (editingTask.isTaskWithAlarm()) {
                textViewAlarmOnOff.setText(getResources().getString(R.string.signal_on));

                textViewTime.setVisibility(View.VISIBLE);
                textViewAlarmDateTime.setVisibility(View.VISIBLE);
                toneContainer.setVisibility(View.VISIBLE);
                textViewVibrationOnOff.setVisibility(View.VISIBLE);
                textViewSendToCalendar.setVisibility(View.VISIBLE);

                textViewAlarmDateTime.setText(DateTimeStringBuilder.getStringFromTimeInMillis(editingTask.getTaskAlarmDateTime()));
                if (!RingtoneList.isLoaded()) {
                    try {
                        RingtoneList.getLoadThread().join();
                    } catch (InterruptedException e) {
                    }
                }
                textViewAlarmTone.setText(RingtoneList.getNameByPath(editingTask.getTaskAlarmTonePath()));
                textViewVibrationOnOff.setText(editingTask.isTaskAlarmWithVibration() ? getResources().getString(R.string.vibration_on) : getResources().getString(R.string.vibration_off));
                textViewSendToCalendar.setText(editingTask.getCalendarEventId() > 0 ? getResources().getString(R.string.task_event_in_calendar) : getResources().getString(R.string.task_event_not_in_calendar));
            } else {
                textViewAlarmOnOff.setText(getResources().getString(R.string.signal_off));

                textViewTime.setVisibility(View.INVISIBLE);
                textViewAlarmDateTime.setVisibility(View.INVISIBLE);
                toneContainer.setVisibility(View.INVISIBLE);
                textViewVibrationOnOff.setVisibility(View.INVISIBLE);
                textViewSendToCalendar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setTaskDescription(String description){
        if (editTextDescription != null) {
            editTextDescription.setText(description);
        }
    }

    public void setTaskName(String name){
        if (editTextName != null) editTextName.setText(name);
    }

    public boolean isEditState() {
        return editState;
    }

    public void updateData(){
        editTextName.setText(editingTask == null ? "" : editingTask.getTaskName());
        editTextDescription.setText(editingTask == null ? "" : editingTask.getTaskDescription());
        updateAlarmData();
        changeState(editState);
    }

    public void playTone(String tonePath){
        stopMediaPlayer();
        mediaPlayer.reset();
        try {
            // mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setVolume(0.2f, 0.5f);
            mediaPlayer.setDataSource(getContext(), Uri.parse(tonePath));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.start();

            // воспроизводим выбранную мелодию в течении 5 сек.
            if (alarmToneTimer != null)
                alarmToneTimer.cancel();
            alarmToneTimer = new CountDownTimer(5000, 5000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    try {
                        if (mediaPlayer.isPlaying())
                            mediaPlayer.stop();
                    } catch (Exception e) {

                    }
                }
            };
            alarmToneTimer.start();
        } catch (Exception e) {
            try {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
            } catch (Exception e2) {

            }
        }
    }

    public void showSetToneDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.task_set_signal);

        CharSequence[] items = new CharSequence[RingtoneList.getAlarmTones().size()];
        for (int i = 0; i < items.length; i++)
            items[i] = RingtoneList.getAlarmTones().get(i);

        alert.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (editingTask != null)
                    editingTask.setTaskAlarmTonePath(RingtoneList.getAlarmTonePaths().get(which));
                textViewAlarmTone.setText(RingtoneList.getAlarmTones().get(which));

                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer.reset();

                playTone(RingtoneList.getAlarmTonePaths().get(which));
            }
        });
        alert.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
