package com.gmail.victorchuholskiy.todolist.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.typeface.CustomTypeFace;

/**
 * Created by Admin on 15.01.2016.
 */
public class ToneEditText extends TextView {
    public ToneEditText(Context context) {
        super(context);
    }

    public ToneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ToneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        if(isInEditMode()) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomStyle);
        int customFont = a.getInt(R.styleable.CustomStyle_custom_font, -1);
        if (customFont != -1){
            setCustom(context, customFont);
            a.recycle();
        }
    }

    public boolean setCustom(Context context, int customFont){
        Typeface t;

        try{
            t = CustomTypeFace.get(context, customFont);
        }catch(Exception e){
            return false;
        }
        this.setTypeface(t);
        return true;
    }
}
