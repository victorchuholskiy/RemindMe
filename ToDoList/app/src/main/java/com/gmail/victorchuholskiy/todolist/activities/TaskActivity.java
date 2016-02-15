package com.gmail.victorchuholskiy.todolist.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.container.RingtoneList;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.database.DataBaseManager;
import com.gmail.victorchuholskiy.todolist.dialog.ColorPickerDialogFragment;
import com.gmail.victorchuholskiy.todolist.fragments.AlarmFragment;
import com.gmail.victorchuholskiy.todolist.fragments.TaskButtonEditFragment;
import com.gmail.victorchuholskiy.todolist.fragments.TaskButtonShowFragment;
import com.gmail.victorchuholskiy.todolist.fragments.TaskInfoFragment;
import com.gmail.victorchuholskiy.todolist.helpers.CalendarHelper;
import com.gmail.victorchuholskiy.todolist.interfaces.ButtonClickListener;
import com.gmail.victorchuholskiy.todolist.recievers.AlarmServiceBroadcastReciever;
import com.gmail.victorchuholskiy.todolist.view.ViewCircle;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;


public class TaskActivity extends AppCompatActivity
        implements ColorPickerDialogFragment.SetTextDialogListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    // Экран активити состоит из 2-х фрагментов: фрагмента с информацией и фрагмента с кнопками.
    // Их состояния меняются в зависимости от режима: редактирование или просмотр.
    // Главный принцип работы: существуют два экземпляра класса Task. Базовый, который считается эталонным,
    // и editingTask, которые изменяется. Ссылка на второй передается во фрагменты, так что надо быть осторожным.

    private int id;
    private Task task;
    private Task editingTask;
    private ViewCircle viewCircle;
    private Calendar signalDateTime;
    private AlarmFragment alarmFragment;
    private TaskInfoFragment taskInfoFragment;
    private TaskButtonShowFragment taskButtonShowFragment;
    private TaskButtonEditFragment taskButtonEditFragment;
    private DataBaseManager dataBaseManager;
    private boolean taskWasEditing = false;
    private boolean inEditingProcess = false;

    public static final int REQUEST_CODE = 200;
    public static final int RESULT_SPEECH_TO_TEXT_HEADER = 101;
    public static final int RESULT_SPEECH_TO_TEXT_DESCRIPTION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        dataBaseManager = DataBaseManager.getInstance(getApplicationContext());
        viewCircle = (ViewCircle) findViewById(R.id.taskCircleViewShowColor);
        viewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogFragment dialog;
                switch (v.getId()) {
                    case R.id.taskCircleViewShowColor:
                        dialog = new ColorPickerDialogFragment();
                        dialog.setPrevColor(viewCircle.getColor());
                        dialog.show(getFragmentManager(), "ColorPickerDialogFragment");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        task = loadTask(id);

        // формируем заметку, с которой будем работать в активити (восстанавливаем или инициализируем загруженным таском)
        editingTask = new Task(
                id,
                bundle.getString("taskName", task.getTaskName()),
                bundle.getString("taskDescription", task.getTaskDescription()),
                bundle.getInt("taskPosition", task.getTaskPosition()),
                bundle.getInt("taskColor", task.getTaskColor()),
                bundle.getBoolean("taskWithAlarm", task.isTaskWithAlarm()),
                bundle.getLong("taskAlarmDateTime", task.getTaskAlarmDateTime()),
                bundle.getString("taskAlarmTonePath", task.getTaskAlarmTonePath()),
                bundle.getBoolean("taskAlarmWithVibration", task.isTaskAlarmWithVibration()),
                bundle.getLong("calendarEventID", task.getCalendarEventId() > 0 ? 1 : 0)
                    // editingTest не будет содержать реальный id события в календаре. Вместо этого он будет содержать
                    // 0, если событие в календарь не отправлялось, и 1, если отправлялось.
        );

        inEditingProcess = bundle.getBoolean("inEditingProcess", !(id > 0));
        if (!inEditingProcess) {
            showTaskButtonShowFragment();
        } else {
            showTaskButtonEditFragment();
        }

        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= Build.VERSION_CODES.LOLLIPOP)
            setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        taskWasEditing = bundle.getBoolean("taskWasEditing", false);

        ViewCircle viewCircle = (ViewCircle) findViewById(R.id.taskCircleViewShowColor);
        int color = Color.TRANSPARENT;
        Drawable background = findViewById(R.id.taskFrame).getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        viewCircle.setColorBorder(color);
        viewCircle.setColor(editingTask.getTaskColor());

        showTaskInfoFragment();

        if (bundle.getBoolean("alarm", false) == true)
            showAlarmFragment();

        // загружаем список рингтонов, чтобы не тормозить интерфейс (на моем аппарате занимает около 3-5 сек.)
        RingtoneList.loadRingtoneList(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getIntent().putExtra("taskWasEditing", taskWasEditing);

        if (editingTask != null) {
            if (taskInfoFragment != null) {
                editingTask.setTaskName(taskInfoFragment.getName());
                editingTask.setTaskDescription(taskInfoFragment.getDescription());
                getIntent().putExtra("inEditingProcess", taskInfoFragment.isEditState());
            }
            editingTask.setTaskColor(viewCircle.getColor());

            getIntent().putExtra("taskName", editingTask.getTaskName());
            getIntent().putExtra("taskDescription", editingTask.getTaskDescription());
            getIntent().putExtra("taskPosition", editingTask.getTaskPosition());
            getIntent().putExtra("taskColor", editingTask.getTaskColor());
            getIntent().putExtra("taskWithAlarm", editingTask.isTaskWithAlarm());
            getIntent().putExtra("taskAlarmDateTime", editingTask.getTaskAlarmDateTime());
            getIntent().putExtra("taskAlarmTonePath", editingTask.getTaskAlarmTonePath());
            getIntent().putExtra("taskAlarmWithVibration", editingTask.isTaskAlarmWithVibration());
            getIntent().putExtra("calendarEventID", editingTask.getCalendarEventId());
        }
    }

    private Task loadTask(int id){
        Task loadedTask;
        if (id > 0) {
            dataBaseManager.open();
            loadedTask = dataBaseManager.getTask(id);
            dataBaseManager.close();
        } else {
            loadedTask = new Task(0, "", "", -1, getResources().getColor(R.color.colorPrimary), false, 0, "", false, 0);
        }
        return loadedTask;
    }


    public void showAlarmFragment() {
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= Build.VERSION_CODES.LOLLIPOP)
            setStatusBarColor(Color.BLACK);

        alarmFragment = new AlarmFragment();
        alarmFragment.setTask(task);
        alarmFragment.setParent(this);
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.alarmContainer, alarmFragment);
        tr.show(alarmFragment);
        tr.commit();
    }


    public void showTaskInfoFragment() {
        if (taskInfoFragment == null)
            taskInfoFragment = new TaskInfoFragment();
        taskInfoFragment.setTask(editingTask);
        taskInfoFragment.setEditState(inEditingProcess);
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.taskFragmentContainer, taskInfoFragment);
        tr.show(taskInfoFragment);
        tr.commit();
    }

    public void showTaskButtonEditFragment() {
        viewCircle.setEnabled(true);
        taskButtonEditFragment = new TaskButtonEditFragment();
        taskButtonEditFragment.setAlarmState(editingTask.isTaskWithAlarm());
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        //tr.setCustomAnimations(R.anim.slide_right_out, R.anim.slide_right_in);
        tr.replace(R.id.taskButtonsContainer, taskButtonEditFragment);
        tr.show(taskButtonEditFragment);
        tr.commit();

        final Activity currentActivity = this;

        // кнопка отмены
        taskButtonEditFragment.setOnClickListenerButtonCancel(new ButtonClickListener() {
            @Override
            public void onClick() {
                if (taskInfoFragment != null)
                    taskInfoFragment.stopMediaPlayer();
                if (id == 0) {
                    currentActivity.onBackPressed();
                } else {
                    editingTask.syncTasks(task);
                    editingTask.setCalendarEventId(task.getCalendarEventId() > 0 ? 1 : 0);

                    viewCircle.setColor(editingTask.getTaskColor());
                    viewCircle.invalidate();

                    showTaskButtonShowFragment();

                    taskInfoFragment.setTask(editingTask);
                    taskInfoFragment.setEditState(false);
                    taskInfoFragment.updateData();
                }
            }
        });

        // кнопка OK
        taskButtonEditFragment.setOnClickListenerButtonOk(new ButtonClickListener() {
            @Override
            public void onClick() {
                if (taskInfoFragment != null)
                    taskInfoFragment.stopMediaPlayer();

                editingTask.setTaskName(taskInfoFragment.getName());
                editingTask.setTaskDescription(taskInfoFragment.getDescription());
                editingTask.setTaskColor(viewCircle.getColor());

                if ((task.getCalendarEventId() > 0) && (editingTask.getCalendarEventId() == 0)) {
                    // событие было в календаре, но сейчас его отменили
                    CalendarHelper.getInstance(getApplicationContext()).deleteEventFromCalendar(task.getCalendarEventId());
                } else if ((task.getCalendarEventId() == 0) && (editingTask.getCalendarEventId() > 0)) {
                    // необходимо добавить событие в календарь
                    editingTask.setCalendarEventId(CalendarHelper.getInstance(getApplicationContext()).addEventToCalendar(editingTask));
                } else if ((task.getCalendarEventId() > 0) && (editingTask.getCalendarEventId() > 0)) {
                    // необходимо обновить событие
                    editingTask.setCalendarEventId(task.getCalendarEventId());
                    CalendarHelper.getInstance(getApplicationContext()).updateEventInCalendar(editingTask);
                }

                task.syncTasks(editingTask);
                editingTask.setCalendarEventId(task.getCalendarEventId() > 0 ? 1 : 0);
                taskWasEditing = true;

                if (id == 0) {
                    dataBaseManager.open();
                    dataBaseManager.insertTask(
                            task.getTaskName(),
                            task.getTaskDescription(),
                            task.getTaskColor(),
                            task.isTaskWithAlarm(),
                            task.getTaskAlarmDateTime(),
                            task.getTaskAlarmTonePath(),
                            task.isTaskAlarmWithVibration(),
                            task.getCalendarEventId()
                    );
                    dataBaseManager.close();

                    Intent alarmServiceIntent = new Intent(getApplicationContext(), AlarmServiceBroadcastReciever.class);
                    sendBroadcast(alarmServiceIntent, null);

                    currentActivity.onBackPressed();
                } else {
                    dataBaseManager.open();
                    dataBaseManager.updateTask(task);
                    dataBaseManager.close();

                    Intent alarmServiceIntent = new Intent(getApplicationContext(), AlarmServiceBroadcastReciever.class);
                    sendBroadcast(alarmServiceIntent, null);

                    showTaskButtonShowFragment();
                    taskInfoFragment.setEditState(false);
                }
            }
        });

        // добавление сигнала (оповещение)
        taskButtonEditFragment.setOnClickListenerButtonAddAlarm(new ButtonClickListener() {
            @Override
            public void onClick() {
                if (!editingTask.isTaskWithAlarm()) {
                    showDialogDatePicker();
                } else {
                    taskButtonEditFragment.setAlarmState(false);
                    taskInfoFragment.offAlarm();
                }
            }
        });

        // установка времени
        taskButtonEditFragment.setOnClickListenerButtonSetTime(new ButtonClickListener() {
            @Override
            public void onClick() {
                showDialogDatePicker();
            }
        });

        // выбор мелодии
        taskButtonEditFragment.setOnClickListenerButtonSetTone(new ButtonClickListener() {
            @Override
            public void onClick() {
                taskInfoFragment.showSetToneDialog();
            }
        });

        // включение вибрации при сигнале
        taskButtonEditFragment.setOnClickListenerButtonAddVibration(new ButtonClickListener() {
            @Override
            public void onClick() {
                if (!editingTask.isTaskAlarmWithVibration())
                    taskInfoFragment.onVibration();
                else
                    taskInfoFragment.offVibration();
            }
        });

        // отправка события в календарь
        taskButtonEditFragment.setOnClickListenerButtonSendToCalendar(new ButtonClickListener() {
            @Override
            public void onClick() {
                taskInfoFragment.sendToCalendarChangeState();
            }
        });

        // включение голосового помошника
        taskButtonEditFragment.setOnClickListenerButtonSpeak(new ButtonClickListener() {
            @Override
            public void onClick() {
                if (taskInfoFragment != null)
                    taskInfoFragment.stopMediaPlayer();

                AlertDialog.Builder speakFieldDialog = new AlertDialog.Builder(currentActivity);
                speakFieldDialog.setTitle(getResources().getString(R.string.speak_choose_field));
                speakFieldDialog.setItems(new CharSequence[]{getResources().getString(R.string.task_header), getResources().getString(R.string.task_description)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.speak_please));
                                startActivityForResult(speechIntent, (which == 0 ? RESULT_SPEECH_TO_TEXT_HEADER : RESULT_SPEECH_TO_TEXT_DESCRIPTION));
                            }
                        });
                speakFieldDialog.show();
            }
        });
    }

    public void showTaskButtonShowFragment() {
        viewCircle.setEnabled(false);
        taskButtonShowFragment = new TaskButtonShowFragment();
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        //tr.setCustomAnimations(R.anim.slide_right_out, R.anim.slide_right_in);
        tr.replace(R.id.taskButtonsContainer, taskButtonShowFragment);
        tr.show(taskButtonShowFragment);
        tr.commit();

        taskButtonShowFragment.setShowEventState(task.getCalendarEventId() > 0);

        final Activity currentActivity = this;

        // кнопка "назад"
        taskButtonShowFragment.setOnClickListenerButtonBack(new ButtonClickListener() {
            @Override
            public void onClick() {
                currentActivity.onBackPressed();
            }
        });

        // удаляем заметку
        taskButtonShowFragment.setOnClickListenerButtonDelete(new ButtonClickListener() {
            @Override
            public void onClick() {
                if (task.getCalendarEventId() > 0)
                    // событие было в календаре и его нужно удалить
                    CalendarHelper.getInstance(getApplicationContext()).deleteEventFromCalendar(task.getCalendarEventId());

                dataBaseManager.open();
                dataBaseManager.deleteList(task);
                dataBaseManager.close();

                final Intent alarmServiceIntent = new Intent(getApplicationContext(), AlarmServiceBroadcastReciever.class);
                currentActivity.sendBroadcast(alarmServiceIntent, null);
                taskWasEditing = true;
                currentActivity.onBackPressed();
            }
        });

        // редактировать заметку
        taskButtonShowFragment.setOnClickListenerButtonEdit(new ButtonClickListener() {
            @Override
            public void onClick() {
                editingTask.syncTasks(task);
                editingTask.setCalendarEventId(task.getCalendarEventId() > 0 ? 1 : 0);
                showTaskButtonEditFragment();
                taskInfoFragment.setEditState(true);
            }
        });

        // копировать данные в буфер (заголовок и описание)
        taskButtonShowFragment.setOnClickListenerButtonCopyToBuffer(new ButtonClickListener() {
            @Override
            public void onClick() {
                ((ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE))
                        .setText(task.getTaskName() + "\n" + task.getTaskDescription());
                Toast.makeText(TaskActivity.this, getResources().getString(R.string.save_buffer), Toast.LENGTH_SHORT).show();
            }
        });

        // открыть событие в календаре
        taskButtonShowFragment.setOnClickListenerButtonEvent(new ButtonClickListener() {
            @Override
            public void onClick() {
                long eventID = task.getCalendarEventId();

                // пришлось написать хитрую проверку, т.к. при удалении события через календарь он не удаляется полностью,
                // а лишь получает параметр DELETED = 1. А через некоторое время уде удаляется целиком.
                if (CalendarHelper.getInstance(getApplicationContext()).checkEventNotDeleted(eventID)) {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(TaskActivity.this, getResources().getString(R.string.event_not_found), Toast.LENGTH_SHORT).show();
                    task.setCalendarEventId(0);
                    editingTask.setCalendarEventId(0);

                    dataBaseManager.open();
                    dataBaseManager.updateTask(task); // обновляем базу
                    dataBaseManager.close();

                    taskButtonShowFragment.setShowEventState(false);
                    taskInfoFragment.updateData();
                    taskWasEditing = true;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        // присваиваем цвет статус-бару
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }

    public void hideAlarmFragment() {

        // фрагмент "будильника" занимает весь экран и выполнен в черных тонах.
        // поэтому мы изменили цвет статус-бара на черный. Надо не забыть вернуть.
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= Build.VERSION_CODES.LOLLIPOP)
            setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        if (alarmFragment != null) {
            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            tr.hide(alarmFragment);
            tr.commit();
        }
    }

    @Override
    public void onSetTextDialogPositiveClick(int color) {
        viewCircle.setColor(color);
        viewCircle.invalidate();
    }

    @Override
    public void onSetTextDialogNegativeClick() {
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        signalDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        signalDateTime.set(Calendar.MINUTE, minute);
        signalDateTime.set(Calendar.SECOND, second);

        taskButtonEditFragment.setAlarmState(true);
        taskInfoFragment.onAlarm(signalDateTime);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        signalDateTime = Calendar.getInstance();
        signalDateTime.set(year, monthOfYear, dayOfMonth);
        showDialogTimePicker();
    }

    private void showDialogDatePicker() {
        // Настройка даты срабатывания сигнала
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                TaskActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(false);
        dpd.vibrate(false);
        dpd.dismissOnPause(false);
        dpd.showYearPickerFirst(false);
        dpd.setAccentColor(getResources().getColor(R.color.colorButtonYellow));
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void showDialogTimePicker() {
        // Настройка времени срабатывания сигнала
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                TaskActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setThemeDark(false);
        tpd.vibrate(false);
        tpd.dismissOnPause(false);
        tpd.enableSeconds(false);
        tpd.setAccentColor(getResources().getColor(R.color.colorButtonYellow));
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                deleteSignalTime();
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    private void deleteSignalTime() {
        signalDateTime = null;
    }

    @Override
    public void onBackPressed() {
        if (taskInfoFragment != null)
            taskInfoFragment.stopMediaPlayer();
        setResult(taskWasEditing ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // обрабатываем надиктованный текст
        if (requestCode == RESULT_SPEECH_TO_TEXT_HEADER && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            taskInfoFragment.setTaskName(matches.get(0));
        } else if (requestCode == RESULT_SPEECH_TO_TEXT_DESCRIPTION && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            taskInfoFragment.setTaskDescription(matches.get(0));
        }
    }
}