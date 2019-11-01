package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTeamMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.thequangnguyen.taskmaster.R;

import javax.annotation.Nonnull;

import type.CreateTeamInput;


public class AddTeam extends AppCompatActivity {

    private static final String TAG = "Quang.AddTeam";
    private EditText teamNameInput;
    AWSAppSyncClient awsAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        teamNameInput = findViewById(R.id.input_team_name);
    }

    public void submitNewTeam(View view) {
        Toast toast = Toast.makeText(this, R.string.submitted_message, Toast.LENGTH_SHORT);
        toast.show();

        runAddTeamMutation(teamNameInput.getText().toString());
    }

    // add new team mutation
    public void runAddTeamMutation(String teamName) {
        CreateTeamInput createTeamInput = CreateTeamInput.builder()
                .name(teamName)
                .build();

        awsAppSyncClient.mutate(CreateTeamMutation.builder().input(createTeamInput).build())
                .enqueue(addTeamCallBack);
    }

    public GraphQLCall.Callback<CreateTeamMutation.Data> addTeamCallBack = new GraphQLCall.Callback<CreateTeamMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateTeamMutation.Data> response) {
            Log.i(TAG, "successfully added a team");
            finish();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.getMessage());
        }
    };
}
