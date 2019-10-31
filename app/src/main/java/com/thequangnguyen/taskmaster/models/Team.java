package com.thequangnguyen.taskmaster.models;

import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;

import java.util.LinkedList;
import java.util.List;

//@Entity
public class Team {
//    @PrimaryKey(autoGenerate = true)
//    private long id;
    private String name;
    private List<Task> tasks;

    public Team (String name) {
        this.name = this.name;
        this.tasks = new LinkedList<>();
    }

    public Team (ListTeamsQuery.Item team) {
        this.name = team.name();
        this.tasks = new LinkedList<>();
    }

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTasks(Task task) {
        this.tasks.add(task);
    }
}
