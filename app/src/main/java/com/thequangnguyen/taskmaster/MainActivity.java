package com.thequangnguyen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
