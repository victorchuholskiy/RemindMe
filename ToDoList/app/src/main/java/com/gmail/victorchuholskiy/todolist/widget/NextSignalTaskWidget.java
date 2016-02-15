package com.gmail.victorchuholskiy.todolist.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.activities.TaskActivity;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.database.DataBaseManager;
import com.gmail.victorchuholskiy.todolist.services.NextSignalTaskService;


public class NextSignalTaskWidget extends AppWidgetProvider {

    // простой виджет из одной кнопки для открытия экрана следующей заметки с сигналом

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_next_signal_task);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_next_signal_task);

            Intent intent = new Intent(context, NextSignalTaskService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.appwidget_next_signal_image, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

