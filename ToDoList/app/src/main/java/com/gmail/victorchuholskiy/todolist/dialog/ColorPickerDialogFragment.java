package com.gmail.victorchuholskiy.todolist.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.gmail.victorchuholskiy.todolist.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * Created by Admin on 03.01.2016.
 */
public class ColorPickerDialogFragment extends DialogFragment implements ColorPicker.OnColorChangedListener {

    private ColorPicker picker;
    private SVBar svBar;
    private OpacityBar opacityBar;
    private int prevColor;

    public ColorPickerDialogFragment() {
    }

    public void setPrevColor(int prevColor) {
        this.prevColor = prevColor;
    }

    public interface SetTextDialogListener {
        void onSetTextDialogPositiveClick(int color);
        void onSetTextDialogNegativeClick();
    }

    SetTextDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View viewColorPicker = inflater.inflate(R.layout.dialog_color_picker, null);
        picker = (ColorPicker) viewColorPicker.findViewById(R.id.picker);
        svBar = (SVBar) viewColorPicker.findViewById(R.id.svbar);
        opacityBar = (OpacityBar) viewColorPicker.findViewById(R.id.opacitybar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOnColorChangedListener(this);
        picker.setOldCenterColor(prevColor);

        builder.setView(viewColorPicker)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSetTextDialogPositiveClick(picker.getColor());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSetTextDialogNegativeClick();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SetTextDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onColorChanged(int color) {
        //gives the color when it's changed.
    }
}