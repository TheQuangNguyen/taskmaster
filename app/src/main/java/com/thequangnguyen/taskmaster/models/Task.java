package com.thequangnguyen.taskmaster.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

//@Entity
public class Task {
//    @PrimaryKey(autoGenerate = true)
//    private long localId;
//    private long id;

//    public enum TaskState
//{
//    NEW,ASSIGNED,IN_PROGRESS,COMPLETE
//}

    private String title;
    private String body;
    private type.TaskState state;
    private Team team;
    private String fileUri;

    public Task(String title, String body, type.TaskState state, Team team, String fileUri) {
        this.title = title;
        this.body = body;
        this.state = state;
        this.team = team;
        this.fileUri = fileUri;
    }

    public Task(ListTasksQuery.Item task) {
        this.title = task.title();
        this.body = task.body();
        this.state = task.state();
        this.fileUri = task.attachFileUri();
    }

    public Task(GetTeamQuery.Item task) {
        this.title = task.title();
        this.body = task.body();
        this.state = task.state();
        this.fileUri = task.attachFileUri();
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

    public type.TaskState getState() {
        return state;
    }

    public void setState(type.TaskState state) {
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

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
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
