package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.thequangnguyen.taskmaster.R;
import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;

public class AddTask extends AppCompatActivity {

    private EditText inputTaskTitle;
    private EditText inputTaskDescription;
    public AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        inputTaskTitle = findViewById(R.id.input_task_title);
        inputTaskDescription = findViewById(R.id.input_task_description);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster").allowMainThreadQueries().build();
    }

    public void showSubmittedMessage(View view) {
        Toast toast = Toast.makeText(this, R.string.submitted_message, Toast.LENGTH_SHORT);
        toast.show();

        Task newTask = new Task(inputTaskTitle.getText().toString(), inputTaskDescription.getText().toString(), "new");
        db.taskDao().addTask(newTask);

        Intent addTaskToMainPageIntent = new Intent(this, MainActivity.class);
        startActivity(addTaskToMainPageIntent);
    }
}
