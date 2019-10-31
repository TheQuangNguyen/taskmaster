package com.thequangnguyen.taskmaster.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

//@Entity
public class Task {
//    @PrimaryKey(autoGenerate = true)
//    private long localId;
//    private long id;
    private String title;
    private String body;
    private String state;
    private Team team;

    public Task(String title, String body, String state, Team team) {
        this.title = title;
        this.body = body;
        this.state = state;
        this.team = team;
    }

    public Task(ListTasksQuery.Item task) {
        this.title = task.title();
        this.body = task.body();
        this.state = task.state();
    }

    public Task(GetTeamQuery.Item task) {
        this.title = task.title();
        this.body = task.body();
        this.state = task.state();
    }

    public Task() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    //    public long getLocalId() {
////        return localId;
////    }
////
////    public void setLocalId(long localId) {
////        this.localId = localId;
////    }

    @Override
    public String toString() {
        return String.format("Title: %s\nDescription: %s\nState: %s", this.title, this.body, this.state);
    }
}
