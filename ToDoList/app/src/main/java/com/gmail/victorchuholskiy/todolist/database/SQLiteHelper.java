package com.gmail.victorchuholskiy.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 02.01.2016.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 6;
    private static final String DB_NAME = "gmail.victorchuholskiy.todolistdb";

    public static final String TABLE_TASK = "tasks";
    public static final String TASK_ID = "id";
    public static final String TASK_ID_GROUP = "id_group";
    public static final String TASK_NAME = "name";
    public static final String TASK_DESCRIPTION = "description";
    public static final String TASK_COLOR = "color";
    public static final String TASK_POSITION = "position";
    public static final String TASK_WITH_ALARM = "with_alarm";
    public static final String TASK_ALARM_DATE = "alarm_date_time";
    public static final String TASK_ALARM_RINGTONE_PATH = "alarm_tone_path";
    public static final String TASK_ALARM_WITH_VIBRATION = "alarm_with_vibration";
    public static final String CALENDAR_EVENT_ID = "calendar_event_id";


    private static final String DB_CREATE_TASKS = "create table " +
            TABLE_TASK + "(" +
            TASK_ID	+ " integer primary key autoincrement, " +
            TASK_ID_GROUP	+ " integer, " +
            TASK_NAME + " text, " +
            TASK_DESCRIPTION + " text," +
            TASK_COLOR	+ " integer, " +
            TASK_POSITION	+ " integer," +
            TASK_WITH_ALARM	+ " boolean, " +
            TASK_ALARM_DATE	+ " bigint, " +
            TASK_ALARM_RINGTONE_PATH + " text," +
            TASK_ALARM_WITH_VIBRATION	+ " boolean," +
            CALENDAR_EVENT_ID + " bigint" +
            ");";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(DB_CREATE_TASKS);
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.endTransaction();
        onCreate(db);
    }




}