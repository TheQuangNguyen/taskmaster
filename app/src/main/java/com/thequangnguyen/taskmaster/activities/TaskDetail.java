package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.TextView;

import com.thequangnguyen.taskmaster.R;
//import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;

public class TaskDetail extends AppCompatActivity {

//    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster")
//                .allowMainThreadQueries().build();
//        Long taskId = Long.parseLong(getIntent().getStringExtra("taskId"));
//        Task currentTask = db.taskDao().getTasksById(taskId);
//        TextView taskDetailTitle = findViewById(R.id.task_detail_title);
//        TextView taskDetailDescription = findViewById(R.id.task_description);
//        TextView taskDetailState = findViewById(R.id.task_state);
//        taskDetailTitle.setText(currentTask.getTitle());
//        taskDetailDescription.setText("Description: " + currentTask.getBody());
//        taskDetailState.setText("State: " + currentTask.getState());
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String state = getIntent().getStringExtra("state");
        TextView taskDetailTitle = findViewById(R.id.task_detail_title);
        TextView taskDetailDescription = findViewById(R.id.task_description);
        TextView taskDetailState = findViewById(R.id.task_state);
        taskDetailTitle.setText(title);
        taskDetailDescription.setText(description);
        taskDetailState.setText(state);
    }
}
