package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.thequangnguyen.taskmaster.R;

public class AddTask extends AppCompatActivity {

    private EditText inputTaskTitle;
    private EditText inputTaskDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        inputTaskTitle = findViewById(R.id.input_task_title);
        inputTaskDescription = findViewById(R.id.input_task_description);
    }

    public void showSubmittedMessage(View view) {
        Toast toast = Toast.makeText(this, R.string.submitted_message, Toast.LENGTH_SHORT);
        toast.show();
        Intent addTaskToMainPageIntent = new Intent(this, MainActivity.class);
        addTaskToMainPageIntent.putExtra("taskTitle", inputTaskTitle.getText().toString());
        addTaskToMainPageIntent.putExtra("taskDescription", inputTaskDescription.getText().toString());
        startActivity(addTaskToMainPageIntent);
    }
}
