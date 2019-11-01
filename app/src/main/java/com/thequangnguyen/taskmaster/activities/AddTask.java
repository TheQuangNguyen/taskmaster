package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.exception.ApolloException;
import com.thequangnguyen.taskmaster.R;
//import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;
import com.thequangnguyen.taskmaster.models.Team;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import type.CreateTaskInput;

public class AddTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText inputTaskTitle;
    private EditText inputTaskDescription;
//    public AppDatabase db;
    AWSAppSyncClient awsAppSyncClient;
    List<ListTeamsQuery.Item> teams;
    ListTeamsQuery.Item selectedTeam;
    private static final String TAG = "nguyen.AddTaskActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        inputTaskTitle = findViewById(R.id.input_task_title);
        inputTaskDescription = findViewById(R.id.input_task_description);
        // connect to AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        this.teams = new LinkedList<>();
        queryAllTeams();
//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster").allowMainThreadQueries().build();
    }

    public void showSubmittedMessage(View view) {
        Toast toast = Toast.makeText(this, R.string.submitted_message, Toast.LENGTH_SHORT);
        toast.show();

        runAddTaskMutation(inputTaskTitle.getText().toString(), inputTaskDescription.getText().toString(), Task.State.NEW, selectedTeam);

//        finish();
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("title", inputTaskTitle.getText().toString())
//                .add("body", inputTaskDescription.getText().toString())
//                .build();
//        Request request = new Request.Builder()
//                .url("http://taskmaster-api.herokuapp.com/tasks")
//                .post(requestBody)
//                .build();
//
//        client.newCall(request).enqueue(new PostTasksToBackendServer(this));

//        Task newTask = new Task(inputTaskTitle.getText().toString(), inputTaskDescription.getText().toString(), "new");
//        db.taskDao().addTask(newTask);

//        Intent addTaskToMainPageIntent = new Intent(this, MainActivity.class);
//        startActivity(addTaskToMainPageIntent);
//        finish();
    }

    class PostTasksToBackendServer implements Callback {

        AddTask addTaskActivity;

        public PostTasksToBackendServer(AddTask addTaskActivity) {
            this.addTaskActivity = addTaskActivity;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Log.e(TAG, "something went wrong with connecting to backend server");
            Log.e(TAG, e.getMessage());
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    addTaskActivity.finish();
                }
            };

            Message completeMessage = handlerForMainThread.obtainMessage(0);
            completeMessage.sendToTarget();
        }
    }

    //////////////////////////// AWS GraphQL methods ///////////////////////////////

    // insert a new task
    public void runAddTaskMutation(String title, String description, Task.State state, ListTeamsQuery.Item selectedTeam) {
        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(title)
                .body(description)
                .state(state)
                .taskTeamId(selectedTeam.id())
                .build();
        awsAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(addTaskCallBack);
    }

    // callback for inserting a task
    public GraphQLCall.Callback<CreateTaskMutation.Data> addTaskCallBack = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull com.apollographql.apollo.api.Response<CreateTaskMutation.Data> response) {
            finish();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.getMessage());
        }
    };

    // query for all teams in dynamoDB
    public void queryAllTeams() {
        awsAppSyncClient.query(ListTeamsQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(getAllTeamsCallback);
    }

    public GraphQLCall.Callback<ListTeamsQuery.Data> getAllTeamsCallback = new GraphQLCall.Callback<ListTeamsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final com.apollographql.apollo.api.Response<ListTeamsQuery.Data> response) {

            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    teams.clear();
                    teams.addAll(response.data().listTeams().items());

                    LinkedList<String> teamNames = new LinkedList<>();
                    for(ListTeamsQuery.Item team: teams) {
                        teamNames.add(team.name());
                    }

                    Spinner spinner =  findViewById(R.id.spinner_select_team);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTask.this, android.R.layout.simple_spinner_item, teamNames);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(AddTask.this);
                }
            };

            handlerForMainThread.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("error", "error getting teams from cloud database");
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedTeam = teams.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
