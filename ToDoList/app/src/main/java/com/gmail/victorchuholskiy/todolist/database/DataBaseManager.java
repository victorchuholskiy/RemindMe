package com.gmail.victorchuholskiy.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gmail.victorchuholskiy.todolist.activities.MainActivity;
import com.gmail.victorchuholskiy.todolist.container.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Admin on 02.01.2016.
 */
public class DataBaseManager {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    private String [] allListColumns = {
            SQLiteHelper.TASK_ID,
            SQLiteHelper.TASK_ID_GROUP,
            SQLiteHelper.TASK_NAME,
            SQLiteHelper.TASK_DESCRIPTION,
            SQLiteHelper.TASK_COLOR,
            SQLiteHelper.TASK_POSITION,
            SQLiteHelper.TASK_WITH_ALARM,
            SQLiteHelper.TASK_ALARM_DATE,
            SQLiteHelper.TASK_ALARM_RINGTONE_PATH,
            SQLiteHelper.TASK_ALARM_WITH_VIBRATION,
            SQLiteHelper.CALENDAR_EVENT_ID
    };

    private static final int SHIFT_AFTER_DELETE = -1;
    private static final int SHIFT_AFTER_INSERT = 1;

    private static final String SELECTION_WITH_WITHOUT_NOTIFICATION = SQLiteHelper.TASK_WITH_ALARM + "=?";
    private static final String[] SELECTION_ARGS_WITH_NOTIFICATION = new String[]{"1"};
    private static final String[] SELECTION_ARGS_WITHOUT_NOTIFICATION = new String[]{"0"};

    private static DataBaseManager instance;
    
    public static DataBaseManager getInstance(Context context){
        if (instance == null)
            instance = new DataBaseManager(context);
        return instance;
    }
    
    private DataBaseManager(Context context){
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertTask(String name, String description, int color, boolean with_alarm, long datetime, String tone_path, boolean with_vibration, long event_id){
        int newTaskDefaultPosition = 0;
        if (getCount() > 0)
            updateDataMovePosition(newTaskDefaultPosition, SHIFT_AFTER_INSERT);  // сдвигаем поле позиции
        ContentValues values = getContentValues(
                name, description, color, newTaskDefaultPosition, with_alarm, datetime, tone_path, with_vibration, event_id);
        database.insertOrThrow(SQLiteHelper.TABLE_TASK, null, values);
    }

    public void updateTask(Task task){
        ContentValues values = getContentValues(
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskColor(),
                task.getTaskPosition(),
                task.isTaskWithAlarm(),
                task.getTaskAlarmDateTime(),
                task.getTaskAlarmTonePath(),
                task.isTaskAlarmWithVibration(),
                task.getCalendarEventId());
        database.update(SQLiteHelper.TABLE_TASK, values, SQLiteHelper.TASK_ID + "=?", new String[]{String.valueOf(task.getId())});
    }

    private Task getTaskFromCursor(Cursor cursor){
        return new Task(
                cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TASK_ID)),
                cursor.getString(cursor.getColumnIndex(SQLiteHelper.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(SQLiteHelper.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TASK_POSITION)),
                cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TASK_COLOR)),
                (cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TASK_WITH_ALARM)) > 0),
                cursor.getLong(cursor.getColumnIndex(SQLiteHelper.TASK_ALARM_DATE)),
                cursor.getString(cursor.getColumnIndex(SQLiteHelper.TASK_ALARM_RINGTONE_PATH)),
                (cursor.getInt(cursor.getColumnIndex(SQLiteHelper.TASK_ALARM_WITH_VIBRATION)) > 0),
                cursor.getLong(cursor.getColumnIndex(SQLiteHelper.CALENDAR_EVENT_ID)));
    }

    private ContentValues getContentValues(String name, String description, int color, int position, boolean with_alarm, long datetime, String tone_path, boolean with_vibration, long event_id){
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.TASK_NAME, name);
        values.put(SQLiteHelper.TASK_DESCRIPTION, description);
        values.put(SQLiteHelper.TASK_COLOR, color);
        values.put(SQLiteHelper.TASK_POSITION, position);
        values.put(SQLiteHelper.TASK_WITH_ALARM, with_alarm);
        values.put(SQLiteHelper.TASK_ALARM_DATE, datetime);
        values.put(SQLiteHelper.TASK_ALARM_RINGTONE_PATH, tone_path);
        values.put(SQLiteHelper.TASK_ALARM_WITH_VIBRATION, with_vibration);
        values.put(SQLiteHelper.CALENDAR_EVENT_ID, event_id);
        return values;
    }

    public List<Task> getLists(int filterValue){ // значения фильтра из MainActivity
        List<Task> todoList = new ArrayList<>();

        String selection = null;
        String[] selectionArgs = null;
        if (filterValue == MainActivity.FILTER_VALUE_WITH_NOTIFICATION){
            selection = SELECTION_WITH_WITHOUT_NOTIFICATION;
            selectionArgs = SELECTION_ARGS_WITH_NOTIFICATION;
        } else if (filterValue == MainActivity.FILTER_VALUE_WITHOUT_NOTIFICATION){
            selection = SELECTION_WITH_WITHOUT_NOTIFICATION;
            selectionArgs = SELECTION_ARGS_WITHOUT_NOTIFICATION;
        }

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK,
                allListColumns, selection, selectionArgs,
                null, null, dbHelper.TASK_POSITION);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false)
        {
            Task item = getTaskFromCursor(cursor);
            todoList.add(item);
            cursor.moveToNext();
        }
        return todoList;
    }

    public int getCount(){
        return database.query(SQLiteHelper.TABLE_TASK,
                allListColumns, null, null,
                null, null, null).getCount();
    }

    public Task getTask(int id){
        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK,
                allListColumns, SQLiteHelper.TASK_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        cursor.moveToFirst();
        return getTaskFromCursor(cursor);
    }

    public Task getNextSignalTask(){
        String queryString =
                "SELECT * FROM " + SQLiteHelper.TABLE_TASK  +
                        " INNER JOIN (SELECT min(" + SQLiteHelper.TASK_ALARM_DATE + ") as min_alarm_time FROM " + SQLiteHelper.TABLE_TASK  +" WHERE " +
                        SQLiteHelper.TASK_WITH_ALARM + "=? and " + SQLiteHelper.TASK_ALARM_DATE + ">?) supp_table " +
                        "ON supp_table.min_alarm_time = " + SQLiteHelper.TASK_ALARM_DATE;
        Cursor cursor = database.rawQuery(queryString, new String[]{"1", String.valueOf(Calendar.getInstance().getTimeInMillis())});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getTaskFromCursor(cursor);
        }
        else{
            return null;
        }
    }

/*
    public String getListName(int id){
        String name;
        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK,
                allListColumns, SQLiteHelper.TASK_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TASK_NAME));
        return name;
    }*/

    public void deleteList(Task task){
        database.delete(SQLiteHelper.TABLE_TASK, SQLiteHelper.TASK_ID + "=?", new String[]{String.valueOf(task.getId())});
        if (getCount() > 0)
            updateDataMovePosition(task.getTaskPosition(), SHIFT_AFTER_DELETE); // сдвигаем поле позиции
    }

    private void updateDataMovePosition(int position, int shift){
        String UPDATE_DB_MOVE_POSITION
                = "update " + dbHelper.TABLE_TASK +
                    " set "+ dbHelper.TASK_POSITION + "=" + dbHelper.TASK_POSITION + "+" + String.valueOf(shift) +
                    " where " + dbHelper.TASK_POSITION + ">=" + String.valueOf(position) + ";";
        database.execSQL(UPDATE_DB_MOVE_POSITION);
    }

    public void updatePositionAfterMove(final Task TaskFrom, final Task TaskTo){
        ContentValues valuesFrom = new ContentValues();
        valuesFrom.put(dbHelper.TASK_POSITION, TaskFrom.getTaskPosition());
        ContentValues valuesTo = new ContentValues();
        valuesTo.put(dbHelper.TASK_POSITION, TaskTo.getTaskPosition());

        database.update(dbHelper.TABLE_TASK, valuesFrom, "id=?", new String[]{String.valueOf(TaskFrom.getId())});
        database.update(dbHelper.TABLE_TASK, valuesTo, "id=?", new String[]{String.valueOf(TaskTo.getId())});
    }
}
