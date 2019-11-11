package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateS3ObjectMutation;
import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.cache.CacheHeaders;
import com.apollographql.apollo.exception.ApolloException;
import com.thequangnguyen.taskmaster.R;
//import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;
import com.thequangnguyen.taskmaster.models.Team;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fragment.S3Object;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import type.CreateS3ObjectInput;
import type.CreateTaskInput;

public class AddTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText inputTaskTitle;
    private EditText inputTaskDescription;
//    public AppDatabase db;
    AWSAppSyncClient awsAppSyncClient;
    List<ListTeamsQuery.Item> teams;
    ListTeamsQuery.Item selectedTeam;
    private static final String TAG = "nguyen.AddTaskActivity";
    private static final int READ_REQUEST_CODE = 42;
    private String filePath;
    TransferUtility transferUtility;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        inputTaskTitle = findViewById(R.id.input_task_title);
        inputTaskDescription = findViewById(R.id.input_task_description);
        this.filePath = null;

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // connect to AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();


        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));


        this.teams = new LinkedList<>();
        queryAllTeams();
//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "taskmaster").allowMainThreadQueries().build();
    }

    public void showSubmittedMessage(View view) {
        Toast toast = Toast.makeText(this, R.string.submitted_message, Toast.LENGTH_SHORT);
        toast.show();


        runAddTaskMutation(inputTaskTitle.getText().toString(), inputTaskDescription.getText().toString(), type.TaskState.NEW, selectedTeam);

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
    public void runAddTaskMutation(String title, String description, type.TaskState state, ListTeamsQuery.Item selectedTeam) {
        String fileKey = null;
        if (this.filePath != null) {
            CreateS3ObjectInput s3ObjectInput = CreateS3ObjectInput.builder()
                    .bucket("taskmasterfiles")
                    .key("public/" + UUID.randomUUID().toString())
                    .region("us-west-2")
                    .localUri(this.filePath)
                    .build();
            CreateS3ObjectMutation s3Object = CreateS3ObjectMutation.builder().input(s3ObjectInput).build();
            awsAppSyncClient.mutate(s3Object).enqueue(uploadFileCallBack);
            fileKey = UUID.randomUUID().toString();
            TransferObserver uploadObserver = transferUtility.upload(fileKey, new File(this.filePath));
        }

        CreateTaskInput createTaskInput = CreateTaskInput.builder()
                .title(title)
                .body(description)
                .state(state)
                .taskTeamId(selectedTeam.id())
                .fileKey(fileKey)
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

    ///////////////////////////////////// S3 Storage Code //////////////////////////////////////////

    // fires an intent to spin up the "file chooser" UI and select a file
    public void pickFile(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
//        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // after the user selects a document in the picker, onActivityResult() gets called
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            fileUri = resultData.getData();
            Log.i("filepath", fileUri.toString());
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(fileUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // String filePath contains the path of selected file
            this.filePath = cursor.getString(columnIndex);
            Log.i("filepath", "" + this.filePath);
            cursor.close();
//            uploadFiles();

            Toast toast = Toast.makeText(this, "Attached!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //
    public void uploadFiles() {
        CreateS3ObjectInput s3ObjectInput = CreateS3ObjectInput.builder()
                .bucket("taskmasterfiles-local")
                .key("public/" + UUID.randomUUID().toString())
                .region("us-west-2")
                .localUri(this.filePath)
                .build();
        CreateS3ObjectMutation s3Object = CreateS3ObjectMutation.builder().input(s3ObjectInput).build();
        awsAppSyncClient.mutate(s3Object);
        TransferObserver uploadObserver = transferUtility.upload("testFile", new File(this.filePath));
//        uploadObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    Log.i("filepath", "completed");
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
//                int percentDone = (int)percentDonef;
//
//                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
//                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Log.e("filepath", ex.getMessage());
//            }
//
//        });
    }

    public GraphQLCall.Callback<CreateS3ObjectMutation.Data> uploadFileCallBack = new GraphQLCall.Callback<CreateS3ObjectMutation.Data>() {
        @Override
        public void onResponse(@Nonnull com.apollographql.apollo.api.Response<CreateS3ObjectMutation.Data> response) {
            Log.i("filepath", response.data().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("filepath", e.getMessage());
        }
    };

}
