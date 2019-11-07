package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTeamMutation;
import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.amplify.generated.graphql.OnCreateTaskSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.thequangnguyen.taskmaster.R;
//import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;
import com.thequangnguyen.taskmaster.models.TaskAdapter;

import java.util.LinkedList;
import java.util.List;

import type.CreateTeamInput;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final String COGNITO = "COGNITO";
    private List<Task> tasks;
//    public AppDatabase db;
    RecyclerView recyclerView;
    AWSAppSyncClient awsAppSyncClient;
    TaskAdapter taskAdapter;
    SharedPreferences prefs;
    List<ListTeamsQuery.Item> teams;
    ListTeamsQuery.Item selectedTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize aws mobile client and check if you are logged in or not
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                // if the user is signed out, show them the sign in page
                if (result.getUserState().toString().equals("SIGNED_OUT")) {
                    signInUser();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(COGNITO, e.getMessage());
            }
        });

        setContentView(R.layout.activity_main);

        this.tasks = new LinkedList<>();
        this.teams = new LinkedList<>();

        // connect to AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        recyclerView = findViewById(R.id.recycler_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.taskAdapter = new TaskAdapter(this.tasks, this);
        recyclerView.setAdapter(taskAdapter);

//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://taskmaster-api.herokuapp.com/tasks")
//                .build();
//
//        client.newCall(request).enqueue(new GetTasksFromBackendServer(this));



//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster")
//                .allowMainThreadQueries().build();

//        tasks.add(new Task("Create a Task class", "A Task should have title, body, and a state", "complete"));
//        tasks.add(new Task("Use RecyclerView for displaying task data", "hardcoded tasks for now", "in progress"));
//        tasks.add(new Task("Create a ViewAdapter class", "displays data from a list of tasks", "in progress"));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Intent newTask = getIntent();
//        if (newTask.getStringExtra("taskTitle") != null) {
//            tasks.add(new Task(newTask.getStringExtra("taskTitle"), newTask.getStringExtra("taskDescription"),"new"));
//        }

//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://taskmaster-api.herokuapp.com/tasks")
//                .build();
//
//        client.newCall(request).enqueue(new GetTasksFromBackendServer(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String username = prefs.getString("username", "My");
//        TextView myTaskTitle = findViewById(R.id.text_my_tasks);
//        if (username.equals("My") || username.equals("")) {
//            myTaskTitle.setText("My Tasks");
//        } else {
//            myTaskTitle.setText("" + username + "'s Tasks");
//        }

        String username = AWSMobileClient.getInstance().getUsername();
        TextView myTaskTitle = findViewById(R.id.text_my_tasks);
        myTaskTitle.setText("" + username + "'s Tasks");

        queryAllTeams();

        // subscribe to future updates
        OnCreateTaskSubscription subscription = OnCreateTaskSubscription.builder().build();
        awsAppSyncClient.subscribe(subscription).execute(new AppSyncSubscriptionCall.Callback<OnCreateTaskSubscription.Data>() {
            @Override
            public void onResponse(@Nonnull com.apollographql.apollo.api.Response<OnCreateTaskSubscription.Data> response) {
                // AWS call this method when a new Task is created
//                Task newTask = new Task(response.data().onCreateTask().title(), response.data().onCreateTask().body(), response.data().onCreateTask().state());
//                taskAdapter.addTask(newTask);

            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onCompleted() {
                // call this once when you subscribe
                Log.i(TAG, "subscribed to task");
            }
        });

//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://taskmaster-api.herokuapp.com/tasks")
//                .build();
//
//        client.newCall(request).enqueue(new GetTasksFromBackendServer(this));
    }

    public void redirectToAddTaskActivity(View view) {
        Intent addTaskIntent = new Intent(this, AddTask.class);
        startActivity(addTaskIntent);
    }

    public void redirectToAddTeamActivity(View view) {
        Intent addTeamIntent = new Intent(this, AddTeam.class);
        startActivity(addTeamIntent);
    }

    public void redirectToSettingActivity(View view) {
        Intent settingIntent = new Intent(this, Settings.class);
        startActivity(settingIntent);
    }

    @Override
    public void redirectToTaskDetailPage(Task task) {
        Intent taskDetailIntent = new Intent(this, TaskDetail.class);
        taskDetailIntent.putExtra("title", "" + task.getTitle());
        taskDetailIntent.putExtra("description", "" + task.getBody());
        taskDetailIntent.putExtra("state", "" + task.getState());
        taskDetailIntent.putExtra("Uri", task.getFileUri());
        startActivity(taskDetailIntent);
    }

    public void signoutCurrentUser(View view) {
        AWSMobileClient.getInstance().signOut();
        signInUser();
    }

    public void signInUser() {
        AWSMobileClient.getInstance().showSignIn(MainActivity.this,
                // customize the built in sign in page
                SignInUIOptions.builder().backgroundColor(16763080).logo(R.drawable.taskmaster_background).build(),
                new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                Log.i(COGNITO, "successfully show signed in page");
            }

            @Override
            public void onError(Exception e) {
                Log.e(COGNITO, e.getMessage());
            }
        });
    }

//    class GetTasksFromBackendServer implements Callback {
//
//        private static final String TAG = "nguyen.Callback";
//        MainActivity mainActivityInstance;
//
//        public GetTasksFromBackendServer (MainActivity mainActivityInstance) {
//            this.mainActivityInstance = mainActivityInstance;
//        }
//
//        @Override
//        public void onFailure(@NotNull Call call, @NotNull IOException e) {
//            Log.e(TAG, "something went wrong with connecting to backend server");
//            Log.e(TAG, e.getMessage());
//        }
//
//        @Override
//        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//            String allTasks = response.body().string();
//            Log.i(TAG, allTasks);
//            Gson gson = new Gson();
//            Task[] listOfTasksFromServer = gson.fromJson(allTasks, Task[].class);
//
//            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster")
//                    .allowMainThreadQueries().build();
//
//            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(Message inputMessage) {
//                    Task[] listOfTasks = (Task[])inputMessage.obj;
//                    for (Task task: listOfTasks) {
//                        if (db.taskDao().getTasksByTitleAndBody(task.getTitle(), task.getBody()) == null) {
//                            db.taskDao().addTask(task);
//                        }
//                    }
//                    mainActivityInstance.tasks = db.taskDao().getAll();
//                    recyclerView = findViewById(R.id.recycler_tasks);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityInstance));
//                    recyclerView.setAdapter(new TaskAdapter(mainActivityInstance.tasks, mainActivityInstance));
//                }
//            };
//            Message completeMessage = handlerForMainThread.obtainMessage(0, listOfTasksFromServer);
//            completeMessage.sendToTarget();
//        }
//    }

    ////////////////////////// AWS GraphQL methods ////////////////////////////

    // Query dynamo db for all tasks
    public void queryAllTasks() {
        awsAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(getAllTasksCallback);
    }

    // callback for get all tasks
    public GraphQLCall.Callback<ListTasksQuery.Data> getAllTasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final com.apollographql.apollo.api.Response<ListTasksQuery.Data> response) {
            Log.i("graphqlgetall" , response.data().listTasks().items().toString());
            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    List<ListTasksQuery.Item> DBTasks = response.data().listTasks().items();
                    tasks.clear();
                    for (ListTasksQuery.Item task: DBTasks) {

                        tasks.add(new Task(task));
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            };

            handlerForMainThread.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.getMessage());
        }
    };

    // Query dynamo db for tasks that belongs to a certain team id
    public void queryForAllTasksOfSelectedTeam() {
        GetTeamQuery getTasksOfSelectedTeamQuery = GetTeamQuery.builder().id(selectedTeam.id()).build();
        awsAppSyncClient.query(getTasksOfSelectedTeamQuery)
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(getSelectedTeamCallback);
    }

    public GraphQLCall.Callback<GetTeamQuery.Data> getSelectedTeamCallback = new GraphQLCall.Callback<GetTeamQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<GetTeamQuery.Data> response) {

            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    List<GetTeamQuery.Item> tasksOfSelectedTeam = response.data().getTeam().tasks().items();
                    tasks.clear();
                    for(GetTeamQuery.Item task: tasksOfSelectedTeam) {
                        tasks.add(new Task(task));
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            };
            handlerForMainThread.obtainMessage().sendToTarget();
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
                    teamNames.add("All Teams");
                    for(ListTeamsQuery.Item team: teams) {
                        teamNames.add(team.name());
                    }

                    Spinner spinner =  findViewById(R.id.spinner_all_teams);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, teamNames);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(MainActivity.this);
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
        if (position == 0) {
            queryAllTasks();
        } else {
            selectedTeam = teams.get(position-1);
            queryForAllTasksOfSelectedTeam();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
