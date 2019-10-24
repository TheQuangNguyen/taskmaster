package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.thequangnguyen.taskmaster.R;

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
