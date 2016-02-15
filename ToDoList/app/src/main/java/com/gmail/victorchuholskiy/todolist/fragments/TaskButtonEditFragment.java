package com.gmail.victorchuholskiy.todolist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.interfaces.ButtonClickListener;

import at.markushi.ui.CircleButton;


public class TaskButtonEditFragment extends Fragment {

    private CircleButton cbOk;
    private CircleButton cbCancel;
    private CircleButton cbSpeak;
    private CircleButton cbAddAlarm;
    private CircleButton cbSetTime;
    private CircleButton cbSetTone;
    private CircleButton cbAddVibration;
    private CircleButton cbSendToCalendar;

    private boolean onAlarm = false; // true - оповещение включено и при следующем нажатии на кнопку cbAddAlarm необходимо отключить сигнал
    // false - оповещение выключено и при следующем нажатии на кнопку cbAddAlarm необходимо включить сигнал

    private ButtonClickListener cbOkButtonClickListener;
    private ButtonClickListener cbCancelButtonClickListener;
    private ButtonClickListener cbSpeakButtonClickListener;
    private ButtonClickListener cbAddAlarmButtonClickListener;
    private ButtonClickListener cbSetTimeButtonClickListener;
    private ButtonClickListener cbSetToneButtonClickListener;
    private ButtonClickListener cbAddVibrationButtonClickListener;
    private ButtonClickListener cbSendToCalendarButtonClickListener;

    public TaskButtonEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_task_edit_buttons, container, false);

        cbOk = (CircleButton) view.findViewById(R.id.buttonOk);
        cbCancel = (CircleButton) view.findViewById(R.id.buttonCancel);
        cbSpeak = (CircleButton) view.findViewById(R.id.buttonSpeak);
        cbAddAlarm = (CircleButton) view.findViewById(R.id.buttonAddAlarm);
        cbSetTime = (CircleButton) view.findViewById(R.id.buttonSetTime);
        cbSetTone = (CircleButton) view.findViewById(R.id.buttonSetTone);
        cbAddVibration = (CircleButton) view.findViewById(R.id.buttonAddVibration);
        cbSendToCalendar = (CircleButton) view.findViewById(R.id.buttonSendToCalendar);

        cbOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbOkButtonClickListener != null){
                    cbOkButtonClickListener.onClick();
                }
            }
        });

        cbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbCancelButtonClickListener != null){
                    cbCancelButtonClickListener.onClick();
                }
            }
        });

        cbSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSpeakButtonClickListener != null){
                    cbSpeakButtonClickListener.onClick();
                }
            }
        });

        cbAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAddAlarmButtonClickListener != null) {
                    cbAddAlarmButtonClickListener.onClick();
                }
            }
        });

        cbSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSetTimeButtonClickListener != null) {
                    cbSetTimeButtonClickListener.onClick();
                }
            }
        });

        cbSetTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSetToneButtonClickListener != null) {
                    cbSetToneButtonClickListener.onClick();
                }
            }
        });

        cbAddVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAddVibrationButtonClickListener != null) {
                    cbAddVibrationButtonClickListener.onClick();
                }
            }
        });

        cbSendToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSendToCalendarButtonClickListener != null) {
                    cbSendToCalendarButtonClickListener.onClick();
                }
            }
        });

        if (savedInstanceState != null){
            onAlarm = savedInstanceState.getBoolean("onAlarm");
        }

        setDrawableCbAddAlarm();
        setStateButtonSetTime(onAlarm);
        setStateButtonSetTone(onAlarm);
        setStateButtonAddVibration(onAlarm);
        setStateButtonSendToCalendar(onAlarm);

        return view;
    }

    public void setOnClickListenerButtonOk(ButtonClickListener onClickListener){
        cbOkButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonCancel(ButtonClickListener onClickListener){
        cbCancelButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonSpeak(ButtonClickListener onClickListener){
        cbSpeakButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonAddAlarm(ButtonClickListener onClickListener){
        cbAddAlarmButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonSetTime(ButtonClickListener onClickListener){
        cbSetTimeButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonSetTone(ButtonClickListener onClickListener){
        cbSetToneButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonAddVibration(ButtonClickListener onClickListener){
        cbAddVibrationButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonSendToCalendar(ButtonClickListener onClickListener){
        cbSendToCalendarButtonClickListener = onClickListener;
    }

    public void setStateButtonSetTime(boolean state){
        if (cbSetTime != null) {
            cbSetTime.setEnabled(state);
            cbSetTime.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setStateButtonSetTone(boolean state){
        if (cbSetTone != null) {
            cbSetTone.setEnabled(state);
            cbSetTone.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setStateButtonAddVibration(boolean state){
        if (cbAddVibration != null) {
            cbAddVibration.setEnabled(state);
            cbAddVibration.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setStateButtonSendToCalendar(boolean state) {
        if (cbSendToCalendar != null) {
            cbSendToCalendar.setEnabled(state);
            cbSendToCalendar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setAlarmState(boolean state) {
        onAlarm = state;
        setDrawableCbAddAlarm();
        setStateButtonSetTime(onAlarm);
        setStateButtonSetTone(onAlarm);
        setStateButtonAddVibration(onAlarm);
        setStateButtonSendToCalendar(onAlarm);
    }

    private void setDrawableCbAddAlarm(){
        if (cbAddAlarm != null) {
            if (!onAlarm) {
                cbAddAlarm.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alarm_white));
            } else {
                cbAddAlarm.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_off_white));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null)
            outState = new Bundle();
        outState.putBoolean("onAlarm", onAlarm);
        super.onSaveInstanceState(outState);
    }
}
