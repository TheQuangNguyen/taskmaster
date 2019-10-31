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
import android.widget.EditText;
import android.widget.RadioButton;
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

public class AddTask extends AppCompatActivity {

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

        runAddTaskMutation(inputTaskTitle.getText().toString(), inputTaskDescription.getText().toString(), "NEW", selectedTeam);

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

    public void onTeamRadioButtonClicked(View view) {
        RadioButton teamRadioButton = findViewById(view.getId());
        String teamName = teamRadioButton.getText().toString();
        for(ListTeamsQuery.Item team: teams) {
            if (team.name().equals(teamName)) {
                selectedTeam = team;
            }
        }
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
    public void runAddTaskMutation(String title, String description, String state, ListTeamsQuery.Item selectedTeam) {
        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(title)
                .body(description)
                .state(state)
                .taskTeamId(selectedTeam.id())
                .build();
        awsAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(addTaskCallBack);
    }

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
                    List<ListTeamsQuery.Item> DBTeams = response.data().listTeams().items();
                    teams.clear();
                    for (ListTeamsQuery.Item team: DBTeams) {
                        teams.add(team);
                    }

                    TextView team1 = findViewById(R.id.radio_team1);
                    TextView team2 = findViewById(R.id.radio_team2);
                    TextView team3 = findViewById(R.id.radio_team3);
                    team1.setText(teams.get(0).name());
                    team2.setText(teams.get(1).name());
                    team3.setText(teams.get(2).name());
                }
            };

            handlerForMainThread.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {

        }
    };

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
}
