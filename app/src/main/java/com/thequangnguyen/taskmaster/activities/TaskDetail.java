package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.thequangnguyen.taskmaster.R;
//import com.thequangnguyen.taskmaster.models.AppDatabase;
import com.thequangnguyen.taskmaster.models.Task;

import java.io.File;

public class TaskDetail extends AppCompatActivity {

//    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

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
//        String fileUri = getIntent().getStringExtra("Uri");
        TextView taskDetailTitle = findViewById(R.id.task_detail_title);
        TextView taskDetailDescription = findViewById(R.id.task_description);
        TextView taskDetailState = findViewById(R.id.task_state);
        final ImageView taskImage = findViewById(R.id.task_image);

        TransferObserver observer = transferUtility.download("testFile", new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test.jpg"));

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.i("Quang.listener", "transfer state is " + state.toString());
                    Handler mainThread = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message inputMessage) {
//                            Log.i("Quang.listener", )
                            taskImage.setImageBitmap(BitmapFactory.decodeFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test.jpg").getAbsolutePath()));
                        }
                    };
                    mainThread.obtainMessage().sendToTarget();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("Quang.listener", ex.getMessage());
            }
        });
        taskDetailTitle.setText(title);
        taskDetailDescription.setText(description);
        taskDetailState.setText(state);


    }
}
