package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void redirectToAddTaskActivity(View view) {
        Intent addTaskIntent = new Intent(MainActivity.this, AddTask.class);
        MainActivity.this.startActivity(addTaskIntent);
    }

    public void redirectToAllTaskActivity(View view) {
        Intent allTasksIntent = new Intent(MainActivity.this, AllTasks.class);
        MainActivity.this.startActivity(allTasksIntent);
    }
}
