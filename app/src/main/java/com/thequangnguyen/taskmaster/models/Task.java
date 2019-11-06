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
    private S3Object file;

    public Task(String title, String body, type.TaskState state, Team team) {
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

    public S3Object getFile() {
        return file;
    }

    public void setFile(S3Object file) {
        this.file = file;
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
