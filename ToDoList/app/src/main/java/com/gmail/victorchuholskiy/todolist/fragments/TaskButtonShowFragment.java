package com.gmail.victorchuholskiy.todolist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.interfaces.ButtonClickListener;

import at.markushi.ui.CircleButton;


public class TaskButtonShowFragment extends Fragment {

    private CircleButton cbBack;
    private CircleButton cbEdit;
    private CircleButton cbDelete;
    private CircleButton cbCopyToBuffer;
    private CircleButton cbEvent;

    private ButtonClickListener cbBackButtonClickListener;
    private ButtonClickListener cbEditButtonClickListener;
    private ButtonClickListener cbDeleteButtonClickListener;
    private ButtonClickListener cbCopyToBufferButtonClickListener;
    private ButtonClickListener cbEventButtonClickListener;

    private boolean ShowEvent = false;

    public TaskButtonShowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_task_show_buttons, container, false);

        cbBack = (CircleButton) view.findViewById(R.id.buttonBack);
        cbEdit = (CircleButton) view.findViewById(R.id.buttonEdit);
        cbDelete = (CircleButton) view.findViewById(R.id.buttonDelete);
        cbCopyToBuffer = (CircleButton)view.findViewById(R.id.buttonCopyToBuffer);
        cbEvent = (CircleButton)view.findViewById(R.id.buttonOpenEventInCalendar);

        cbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbBackButtonClickListener != null){
                    cbBackButtonClickListener.onClick();
                }
            }
        });

        cbEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbEditButtonClickListener != null){
                    cbEditButtonClickListener.onClick();
                }
            }
        });

        cbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbDeleteButtonClickListener != null) {
                    cbDeleteButtonClickListener.onClick();
                }
            }
        });

        cbCopyToBuffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbCopyToBufferButtonClickListener != null) {
                    cbCopyToBufferButtonClickListener.onClick();
                }
            }
        });

        cbEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbEventButtonClickListener != null) {
                    cbEventButtonClickListener.onClick();
                }
            }
        });

        if (savedInstanceState != null){
            ShowEvent = savedInstanceState.getBoolean("ShowEvent");
        }
        setStateShowEvent(ShowEvent);

        return view;
    }

    public void setOnClickListenerButtonBack(ButtonClickListener onClickListener){
        cbBackButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonEdit(ButtonClickListener onClickListener){
        cbEditButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonDelete(ButtonClickListener onClickListener){
        cbDeleteButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonCopyToBuffer(ButtonClickListener onClickListener){
        cbCopyToBufferButtonClickListener = onClickListener;
    }

    public void setOnClickListenerButtonEvent(ButtonClickListener onClickListener){
        cbEventButtonClickListener = onClickListener;
    }

    public void setShowEventState(boolean state) {
        ShowEvent = state;
        setStateShowEvent(ShowEvent);
    }

    public void setStateShowEvent(boolean state){
        if (cbEvent != null) {
            cbEvent.setEnabled(state);
            cbEvent.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null)
            outState = new Bundle();
        outState.putBoolean("ShowEvent", ShowEvent);
        super.onSaveInstanceState(outState);
    }
}
