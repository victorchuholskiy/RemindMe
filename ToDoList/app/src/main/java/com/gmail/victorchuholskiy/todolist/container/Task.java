package com.gmail.victorchuholskiy.todolist.container;


import java.io.Serializable;

/**
 * Created by Admin on 02.01.2016.
 */
public class Task implements Serializable {
    private int id;
    private String taskName;
    private String taskDescription;
    private int taskPosition;
    private int taskColor;
    private boolean taskWithAlarm;
    private long taskAlarmDateTime;
    private String taskAlarmTonePath;
    private boolean taskAlarmWithVibration;
    private long calendarEventId;

    public Task(int id, String taskName, String taskDescription,
                int taskPosition, int taskColor, boolean taskWithAlarm,
                long taskAlarmDateTime, String taskAlarmTonePath,
                boolean taskAlarmWithVibration, long calendarEventId) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskPosition = taskPosition;
        this.taskColor = taskColor;
        this.taskWithAlarm = taskWithAlarm;
        this.taskAlarmDateTime = taskAlarmDateTime;
        this.taskAlarmTonePath = taskAlarmTonePath;
        this.taskAlarmWithVibration = taskAlarmWithVibration;
        this.calendarEventId = calendarEventId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public int getTaskColor() {
        return taskColor;
    }

    public int getTaskPosition() {
        return taskPosition;
    }

    public int getId() {
        return id;
    }

    public long getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(long calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public void setTaskPosition(int taskPosition) {
        this.taskPosition = taskPosition;
    }

    public boolean isTaskWithAlarm() {
        return taskWithAlarm;
    }

    public long getTaskAlarmDateTime() {
        return taskAlarmDateTime;
    }

    public String getTaskAlarmTonePath() {
        return taskAlarmTonePath;
    }

    public boolean isTaskAlarmWithVibration() {
        return taskAlarmWithVibration;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskColor(int taskColor) {
        this.taskColor = taskColor;
    }

    public void setTaskAlarmTonePath(String taskAlarmTonePath) {
        this.taskAlarmTonePath = taskAlarmTonePath;
    }

    public void setTaskWithAlarm(boolean taskWithAlarm) {
        this.taskWithAlarm = taskWithAlarm;
    }

    public void setTaskAlarmDateTime(long taskAlarmDateTime) {
        this.taskAlarmDateTime = taskAlarmDateTime;
    }

    public void setTaskAlarmWithVibration(boolean taskAlarmWithVibration) {
        this.taskAlarmWithVibration = taskAlarmWithVibration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskPosition=" + taskPosition +
                ", taskColor=" + taskColor +
                ", taskWithAlarm=" + taskWithAlarm +
                ", taskAlarmDateTime=" + taskAlarmDateTime +
                ", taskAlarmTonePath='" + taskAlarmTonePath + '\'' +
                ", taskAlarmWithVibration=" + taskAlarmWithVibration +
                ", calendarEventId=" + calendarEventId +
                '}';
    }

    public void syncTasks(Task task){
        this.id = task.id;
        this.taskName = task.taskName;
        this.taskDescription = task.taskDescription;
        this.taskPosition = task.taskPosition;
        this.taskColor = task.taskColor;
        this.taskWithAlarm = task.taskWithAlarm;
        this.taskAlarmDateTime = task.taskAlarmDateTime;
        this.taskAlarmTonePath = task.taskAlarmTonePath;
        this.taskAlarmWithVibration = task.taskAlarmWithVibration;
        this.calendarEventId = task.calendarEventId;
    }
}
