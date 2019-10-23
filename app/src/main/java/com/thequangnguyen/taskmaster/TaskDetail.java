package com.thequangnguyen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        String taskTitle = getIntent().getStringExtra("taskTitle");
        TextView taskDetailTitle = findViewById(R.id.task_detail_title);
        taskDetailTitle.setText(taskTitle);
    }
}
