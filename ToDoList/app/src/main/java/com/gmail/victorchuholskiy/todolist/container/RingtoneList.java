package com.gmail.victorchuholskiy.todolist.container;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;

import java.util.ArrayList;

/**
 * Created by Admin on 09.01.2016.
 */
public class RingtoneList{
    private static ArrayList<String> alarmTones;
    private static ArrayList<String> alarmTonePaths;
    private static boolean isLoaded = false;
    private static Context context;
    private static Thread loadThread = new Thread(new Runnable() {
        public void run() {
            RingtoneManager ringtoneMgr = new RingtoneManager(context);
            ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
            Cursor alarmsCursor = ringtoneMgr.getCursor();
            alarmTones = new ArrayList<>();
            alarmTonePaths = new ArrayList<>();

            if (alarmsCursor.moveToFirst()) {
                do {
                    alarmTones.add(ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(context));
                    alarmTonePaths.add(ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString());
                } while (alarmsCursor.moveToNext());
            }
            alarmsCursor.close();
            isLoaded = true;
        }
    });;

    public static void loadRingtoneList(Context context) {
        // загрузку будем производить в параллельном потоке, чтобы не тормозить основной
        if (!isLoaded) {
            RingtoneList.context = context;
            loadThread.start();
        }
    }

    public static ArrayList<String> getAlarmTones() {
        return alarmTones;
    }

    public static ArrayList<String> getAlarmTonePaths() {
        return alarmTonePaths;
    }

    public static boolean isLoaded() {
        return isLoaded;
    }

    public static String getNameByPath(String path){
        return alarmTones.get(alarmTonePaths.indexOf(path));
    }

    public static Thread getLoadThread() {
        return loadThread;
    }
}
