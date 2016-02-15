package com.gmail.victorchuholskiy.todolist.helpers;

import java.util.Calendar;

/**
 * Created by Admin on 11.01.2016.
 */
public class DateTimeStringBuilder {

    public static String getStringFromCalendar(Calendar calendar){

        // если воспользоваться SimpleDateFormat, то мы получим не совсем правильное значение "часов"
        // причина в том, что в Calendar хранится значение в 12-тичасовом формате (с AM и PM)

        return String.format("%02d.%02d.%d %02d:%02d", // немного форматирования :)
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1, // в Calendar месяцы считаются от 0
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        );
    }

    public static String getStringFromTimeInMillis(long time){
        Calendar calendar  = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return getStringFromCalendar(calendar);
    }
}
