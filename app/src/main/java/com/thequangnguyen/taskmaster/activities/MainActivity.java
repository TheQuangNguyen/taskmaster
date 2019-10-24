package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thequangnguyen.taskmaster.R;
import com.thequangnguyen.taskmaster.models.Task;
import com.thequangnguyen.taskmaster.models.TaskAdapter;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener {

    private static final String TAG = "MainActivity";
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tasks = new LinkedList<>();
        tasks.add(new Task("Create a Task class", "A Task should have title, body, and a state", "complete"));
        tasks.add(new Task("Use RecyclerView for displaying task data", "hardcoded tasks for now", "in progress"));
        tasks.add(new Task("Create a ViewAdapter class", "displays data from a list of tasks", "in progress"));

        RecyclerView recyclerView = findViewById(R.id.recycler_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(this.tasks, this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent newTask = getIntent();
        if (newTask.getStringExtra("taskTitle") != null) {
            tasks.add(new Task(newTask.getStringExtra("taskTitle"), newTask.getStringExtra("taskDescription"),"new"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "My");
        TextView myTaskTitle = findViewById(R.id.text_my_tasks);
        if (username.equals("My") || username.equals("")) {
            myTaskTitle.setText("My Tasks");
        } else {
            myTaskTitle.setText("" + username + "'s Tasks");
        }
    }

    public void redirectToAddTaskActivity(View view) {
        Intent addTaskIntent = new Intent(this, AddTask.class);
        startActivity(addTaskIntent);
    }

    public void redirectToAllTaskActivity(View view) {
        Intent allTasksIntent = new Intent(this, AllTasks.class);
        startActivity(allTasksIntent);
    }

    public void redirectToTaskDetailActivity(View view) {
        Button taskButton = findViewById(view.getId());
        String taskTitle = taskButton.getText().subSequence(3, taskButton.getText().length()).toString();
        Intent taskDetailIntent = new Intent(this, TaskDetail.class);
        taskDetailIntent.putExtra("taskTitle", taskTitle);
        startActivity(taskDetailIntent);
    }

    public void redirectToSettingActivity(View view) {
        Intent settingIntent = new Intent(this, Settings.class);
        startActivity(settingIntent);
    }

    @Override
    public void redirectToTaskDetailPage(Task task) {
        Intent taskDetailIntent = new Intent(this, TaskDetail.class);
        taskDetailIntent.putExtra("taskTitle", task.getTitle());
        startActivity(taskDetailIntent);
    }
}
