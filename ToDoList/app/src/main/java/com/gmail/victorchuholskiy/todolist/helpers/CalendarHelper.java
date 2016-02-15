package com.gmail.victorchuholskiy.todolist.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import com.gmail.victorchuholskiy.todolist.container.Task;

import java.util.TimeZone;

/**
 * Created by Admin on 14.01.2016.
 */
public class CalendarHelper {

    private Context context;
    private ContentResolver contentResolver;
    private String[] mProjection = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    private static CalendarHelper instance;

    public static CalendarHelper getInstance(Context context){
        if (instance == null)
            instance = new CalendarHelper(context);
        return instance;
    }

    private CalendarHelper(Context context){
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public long addEventToCalendar(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.DTSTART, task.getTaskAlarmDateTime());
        cv.put(CalendarContract.Events.DTEND, task.getTaskAlarmDateTime());
        cv.put(CalendarContract.Events.TITLE, task.getTaskName());
        cv.put(CalendarContract.Events.DESCRIPTION, task.getTaskDescription());
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        cv.put(CalendarContract.Events.EVENT_COLOR, task.getTaskColor());
        cv.put(CalendarContract.Events.CALENDAR_ID, 1);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activit|y#requestPermissions for more details.
            return 0;
        }
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv);
        return Long.parseLong(uri.getLastPathSegment());// возвращаем id события
    }

    public void updateEventInCalendar(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.DTSTART, task.getTaskAlarmDateTime());
        cv.put(CalendarContract.Events.DTEND, task.getTaskAlarmDateTime());
        cv.put(CalendarContract.Events.TITLE, task.getTaskName());
        cv.put(CalendarContract.Events.DESCRIPTION, task.getTaskDescription());
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        cv.put(CalendarContract.Events.EVENT_COLOR, task.getTaskColor());
        cv.put(CalendarContract.Events.CALENDAR_ID, 1);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        contentResolver.update(CalendarContract.Events.CONTENT_URI, cv, CalendarContract.Events._ID + "=?", new String[]{String.valueOf(task.getCalendarEventId())});
    }

    public void deleteEventFromCalendar(long calendarEventId) {
        ContentResolver contentResolver = context.getContentResolver();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int count = contentResolver.delete(CalendarContract.Events.CONTENT_URI, CalendarContract.Events._ID+"=?", new String[]{String.valueOf(calendarEventId)});
    }

    public boolean checkEventNotDeleted(long eventId){
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.DELETED
        };

        //final int ID_INDEX = 0;
        final int DELETED_INDEX = 1;

        ContentResolver cr = context.getContentResolver();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = "(" + CalendarContract.Events._ID + " = ?)";
        String[] selectionArgs = new String[] {String.valueOf(eventId)};

        Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            if (cur.getInt(DELETED_INDEX)  > 0)
                return false;
            else
                return true;
        }
        else
            return false;
    }
}
