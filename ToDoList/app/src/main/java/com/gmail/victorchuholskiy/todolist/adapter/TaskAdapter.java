package com.gmail.victorchuholskiy.todolist.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.database.DataBaseManager;
import com.gmail.victorchuholskiy.todolist.helpers.CalendarHelper;
import com.gmail.victorchuholskiy.todolist.interfaces.ItemTouchHelperAdapter;
import com.gmail.victorchuholskiy.todolist.interfaces.ItemTouchHelperViewHolder;
import com.gmail.victorchuholskiy.todolist.interfaces.OnStartDragListener;
import com.gmail.victorchuholskiy.todolist.recievers.AlarmServiceBroadcastReciever;
import com.gmail.victorchuholskiy.todolist.view.ViewCircle;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 02.01.2016.
 */
public class TaskAdapter  extends RecyclerView.Adapter<TaskAdapter.TaskHolder>
        implements ItemTouchHelperAdapter {

    private ItemClickListener mItemClickListener;
    private final OnStartDragListener mDragStartListener;
    private List<Task> mList;
    private DataBaseManager mDataSource;
    private View view;
    private Context context;

    public static class TaskHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder{
        private View view;
        private TextView textViewName;
        private TextView textViewDescription;
        private ViewCircle circleView;
        private CardView cardView;
        private final ImageView handleView;
        private final ImageView alarmView;
        private final ImageView calendarView;

        public TaskHolder(final View itemView){
            super(itemView);
            view = itemView;
            textViewName = (TextView)itemView.findViewById(R.id.tv_taskHeader);
            textViewDescription = (TextView)itemView.findViewById(R.id.tv_taskDescription);
            circleView = (ViewCircle)itemView.findViewById(R.id.taskCircleView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            alarmView = (ImageView) itemView.findViewById(R.id.signal);
            calendarView = (ImageView) itemView.findViewById(R.id.calendar);
        }

        @Override
        public void onItemSelected() {
            cardView.setCardBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            cardView.setCardBackgroundColor(itemView.getResources().getColor(R.color.colorWhiteBackground));
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        mItemClickListener = itemClickListener;
    }

    public TaskAdapter(List<Task> list, OnStartDragListener dragStartListener, Context context) {
        mDragStartListener = dragStartListener;
        mList = list;
        this.context = context;
    }

    public void addItems(List<Task> list){
        mList = list;
        notifyDataSetChanged();
    }

    public void clear(){
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_task, parent, false);
        TaskHolder TaskHolder = new TaskHolder(view);
        mDataSource = DataBaseManager.getInstance(view.getContext());
        return TaskHolder;
    }

    @Override
    public void onBindViewHolder(final TaskHolder holder, int position) {
        final Task task = mList.get(position);
        holder.textViewName.setText(task.getTaskName().length() > 20 ? task.getTaskName().substring(0, 17).trim() + "..." : task.getTaskName());
        holder.textViewDescription.setText(task.getTaskDescription().length() > 50 ? task.getTaskDescription().substring(0, 47).trim() + "..." : task.getTaskDescription());
        holder.circleView.setColor(task.getTaskColor());
        holder.alarmView.setVisibility(task.isTaskWithAlarm() ? View.VISIBLE : View.INVISIBLE);
        if (task.isTaskWithAlarm()){
            holder.alarmView.setImageDrawable(
                    task.getTaskAlarmDateTime() > Calendar.getInstance().getTimeInMillis() ?
                            context.getResources().getDrawable(R.drawable.ic_access_alarm_black) :
                            context.getResources().getDrawable(R.drawable.ic_alarm_on_black));
        }
        holder.calendarView.setVisibility(task.getCalendarEventId() > 0 ? View.VISIBLE : View.INVISIBLE);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null){
                    mItemClickListener.onClick(task);
                }
            }
        });

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        mDragStartListener.onStartDrag(holder);
                        break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public interface ItemClickListener{
        void onClick(Task Task);
    }

    @Override
    public void onItemDismiss(int position) {
        if (mList.get(position).getCalendarEventId() > 0)
            // событие было в календаре и его нужно удалить
            CalendarHelper.getInstance(context).deleteEventFromCalendar(mList.get(position).getCalendarEventId());

        mDataSource.open();
        mDataSource.deleteList(mList.get(position));
        mDataSource.close();

        final Intent alarmServiceIntent = new Intent(view.getContext(), AlarmServiceBroadcastReciever.class);
        view.getContext().sendBroadcast(alarmServiceIntent, null);

        mList.remove(position);

        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        mList.get(fromPosition).setTaskPosition(toPosition);
        mList.get(toPosition).setTaskPosition(fromPosition);

        mDataSource.open();
        mDataSource.updatePositionAfterMove(mList.get(fromPosition), mList.get(toPosition));
        mDataSource.close();

        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}