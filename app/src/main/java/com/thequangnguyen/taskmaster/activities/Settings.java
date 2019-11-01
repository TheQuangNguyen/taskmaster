package com.thequangnguyen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.exception.ApolloException;
import com.thequangnguyen.taskmaster.R;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class Settings extends AppCompatActivity {

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public void saveUsernameToSharedPreferences(View view) {
        EditText usernameEditText = findViewById(R.id.username_input);
        String username = usernameEditText.getText().toString();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply();
        finish();
    }
//    public void onTeamRadioButtonClicked(View view) {
//        RadioButton teamRadioButton = findViewById(view.getId());
//        String teamName = teamRadioButton.getText().toString();
//        if (teamName.equals("All Teams")) {
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("teamId", "0");
//            editor.apply();
//            finish();
//            return;
//        }
//
//        for(ListTeamsQuery.Item team: teams) {
//            if (team.name().equals(teamName)) {
//                selectedTeam = team;
//            }
//        }
//
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("teamId", selectedTeam.id());
//        editor.apply();
//        finish();
//    }



//    // query for all teams in dynamoDB
//    public void queryAllTeams() {
//        awsAppSyncClient.query(ListTeamsQuery.builder().build())
//                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
//                .enqueue(getAllTeamsCallback);
//    }
//
//    public GraphQLCall.Callback<ListTeamsQuery.Data> getAllTeamsCallback = new GraphQLCall.Callback<ListTeamsQuery.Data>() {
//        @Override
//        public void onResponse(@Nonnull final com.apollographql.apollo.api.Response<ListTeamsQuery.Data> response) {
//
//            Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(Message inputMessage) {
//                    List<ListTeamsQuery.Item> DBTeams = response.data().listTeams().items();
//                    teams.clear();
//                    for (ListTeamsQuery.Item team: DBTeams) {
//                        teams.add(team);
//                    }
//
//                    TextView team1 = findViewById(R.id.radio_team1);
//                    TextView team2 = findViewById(R.id.radio_team2);
//                    TextView team3 = findViewById(R.id.radio_team3);
//                    team1.setText(teams.get(0).name());
//                    team2.setText(teams.get(1).name());
//                    team3.setText(teams.get(2).name());
//                }
//            };
//
//            handlerForMainThread.obtainMessage().sendToTarget();
//        }
//
//        @Override
//        public void onFailure(@Nonnull ApolloException e) {
//            Log.e("error", "error getting teams from cloud database");
//        }
//    };
}
