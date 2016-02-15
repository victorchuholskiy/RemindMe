package com.gmail.victorchuholskiy.todolist.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.victorchuholskiy.todolist.R;
import com.gmail.victorchuholskiy.todolist.database.DataBaseManager;
import com.gmail.victorchuholskiy.todolist.helpers.SimpleItemTouchHelperCallback;
import com.gmail.victorchuholskiy.todolist.adapter.TaskAdapter;
import com.gmail.victorchuholskiy.todolist.container.Task;
import com.gmail.victorchuholskiy.todolist.interfaces.OnStartDragListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private List<Task> mList = new ArrayList();
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private DataBaseManager mDataSource;
    private int filterValue = FILTER_VALUE_ALL;

    public static final int FILTER_VALUE_ALL = 1;
    public static final int FILTER_VALUE_WITH_NOTIFICATION = 2;
    public static final int FILTER_VALUE_WITHOUT_NOTIFICATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("id", 0);
                startActivityForResult(intent, TaskActivity.REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_taskList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

/*        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);*/

        mRecyclerView.setHasFixedSize(true);
        mTaskAdapter = new TaskAdapter(mList, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        }, getApplicationContext());

       mTaskAdapter.setItemClickListener(new TaskAdapter.ItemClickListener() {
           @Override
           public void onClick(Task task) {
               Intent intent = new Intent(MainActivity.this, TaskActivity.class);
               intent.putExtra("id", task.getId());
               intent.putExtra("alarm", false);
               startActivityForResult(intent, TaskActivity.REQUEST_CODE);
           }
       });
        mRecyclerView.setAdapter(mTaskAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mTaskAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mDataSource = DataBaseManager.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getIntent().putExtra("filterValue", filterValue);
    }

    @Override
    protected void onStart() {
        super.onStart();
        filterValue = getIntent().getIntExtra("filterValue", FILTER_VALUE_ALL);
        updateRecycleViewData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // переход в настройки в правом верхнем углу активити
        //getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            filterValue = FILTER_VALUE_ALL;
        } else if (id == R.id.nav_note) {
            filterValue = FILTER_VALUE_WITH_NOTIFICATION;
        } else if (id == R.id.nav_note_without_notification) {
            filterValue = FILTER_VALUE_WITHOUT_NOTIFICATION;
        } else if (id == R.id.nav_open_calendar) {
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis());
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(builder.build());
            startActivity(intent);
        }
        updateRecycleViewData();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == TaskActivity.REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) // считаем, что данные были изменены
            updateRecycleViewData();
    }

    protected void updateRecycleViewData(){
        mDataSource.open();
        if (mDataSource.getCount() > 0)
            mTaskAdapter.addItems(mDataSource.getLists(filterValue));
        else
            mTaskAdapter.clear();
        mDataSource.close();
    }
}
