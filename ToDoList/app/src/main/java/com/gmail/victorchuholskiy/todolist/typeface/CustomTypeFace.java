package com.gmail.victorchuholskiy.todolist.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Admin on 15.01.2016.
 */
public class CustomTypeFace {
    public static final int ABeeZee_Italic = 0;
    public static final int AbrilFatface_Regular = 1;

    private static class FontHashMap extends HashMap<Integer, String> {
        {
            put(ABeeZee_Italic, "ABeeZee-Italic.ttf");
            put(AbrilFatface_Regular, "AbrilFatface-Regular.ttf");
        }
    }

    // используется для вылетающего окошка с вариантами
    private static final HashMap<Integer, String> fontHashMap = new FontHashMap();

    // для кэширования
    private static final Hashtable<String, Typeface> cash = new Hashtable<>();

    // получаем по имени
    public static Typeface get(Context context, String assetPath){
        synchronized (cash) { // кэшируем
            if (!cash.containsKey(assetPath)) {
                try {
                    Typeface tf = Typeface.createFromAsset(context.getAssets(), assetPath);
                    cash.put(assetPath, tf);
                } catch (Exception e) {
                    Log.w("MyTypeface", "Could not get typeface: " + assetPath);
                }
            }
            return cash.get(assetPath);
        }
    }

    // для перечисления
    public static Typeface get(Context context, int fontEnum){
        String name = fontHashMap.get(fontEnum);
        if (name == null){
            throw new IllegalArgumentException("Could not get typeface");
        }
        return get(context, "fonts/" + name);
    }
}
