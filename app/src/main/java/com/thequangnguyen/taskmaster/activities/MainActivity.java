package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thequangnguyen.taskmaster.R;
import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;
import com.thequangnguyen.taskmaster.models.TaskAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener {

    private static final String TAG = "MainActivity";
    private List<Task> tasks;
    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://taskmaster-api.herokuapp.com/tasks")
                .build();

        client.newCall(request).enqueue(new GetTasksFromBackendServer(this));

//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster")
//                .allowMainThreadQueries().build();

//        tasks.add(new Task("Create a Task class", "A Task should have title, body, and a state", "complete"));
//        tasks.add(new Task("Use RecyclerView for displaying task data", "hardcoded tasks for now", "in progress"));
//        tasks.add(new Task("Create a ViewAdapter class", "displays data from a list of tasks", "in progress"));

//        RecyclerView recyclerView = findViewById(R.id.recycler_tasks);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new TaskAdapter(this.tasks, this));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Intent newTask = getIntent();
//        if (newTask.getStringExtra("taskTitle") != null) {
//            tasks.add(new Task(newTask.getStringExtra("taskTitle"), newTask.getStringExtra("taskDescription"),"new"));
//        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://taskmaster-api.herokuapp.com/tasks")
                .build();

        client.newCall(request).enqueue(new GetTasksFromBackendServer(this));
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

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://taskmaster-api.herokuapp.com/tasks")
                .build();

        client.newCall(request).enqueue(new GetTasksFromBackendServer(this));
    }

    public void redirectToAddTaskActivity(View view) {
        Intent addTaskIntent = new Intent(this, AddTask.class);
        startActivity(addTaskIntent);
    }

    public void redirectToAllTaskActivity(View view) {
        Intent allTasksIntent = new Intent(this, AllTasks.class);
        startActivity(allTasksIntent);
    }

//    public void redirectToTaskDetailActivity(View view) {
//        Button taskButton = findViewById(view.getId());
//        String taskTitle = taskButton.getText().subSequence(3, taskButton.getText().length()).toString();
//        Intent taskDetailIntent = new Intent(this, TaskDetail.class);
//        taskDetailIntent.putExtra("taskTitle", taskTitle);
//        startActivity(taskDetailIntent);
//    }

    public void redirectToSettingActivity(View view) {
        Intent settingIntent = new Intent(this, Settings.class);
        startActivity(settingIntent);
    }

    @Override
    public void redirectToTaskDetailPage(Task task) {
        Intent taskDetailIntent = new Intent(this, TaskDetail.class);
        taskDetailIntent.putExtra("taskId", "" + task.getId());
        startActivity(taskDetailIntent);
    }

    class GetTasksFromBackendServer implements Callback {

        private static final String TAG = "nguyen.Callback";
        MainActivity mainActivityInstance;

        public GetTasksFromBackendServer (MainActivity mainActivityInstance) {
            this.mainActivityInstance = mainActivityInstance;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.e(TAG, "something went wrong with connecting to backend server");
            Log.e(TAG, e.getMessage());
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String allTasks = response.body().string();
            Log.i(TAG, allTasks);
            Gson gson = new Gson();
            Task[] listOfTasksFromServer = gson.fromJson(allTasks, Task[].class);

            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster")
                    .allowMainThreadQueries().build();

            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    Task[] listOfTasks = (Task[])inputMessage.obj;
                    for (Task task: listOfTasks) {
                        if (db.taskDao().getTasksByTitleAndBody(task.getTitle(), task.getBody()) == null) {
                            db.taskDao().addTask(task);
                        }
                    }
                    mainActivityInstance.tasks = db.taskDao().getAll();
                    RecyclerView recyclerView = findViewById(R.id.recycler_tasks);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityInstance));
                    recyclerView.setAdapter(new TaskAdapter(mainActivityInstance.tasks, mainActivityInstance));
                }
            };
            Message completeMessage = handlerForMainThread.obtainMessage(0, listOfTasksFromServer);
            completeMessage.sendToTarget();
        }
    }
}
