package com.thequangnguyen.taskmaster.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thequangnguyen.taskmaster.R;

import org.w3c.dom.Text;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public List<Task> tasks;
    public OnTaskInteractionListener listener;

    public TaskAdapter(List<Task> tasks,OnTaskInteractionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public void addTask(Task task) {
        this.tasks.add(0, task);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        Task task;
        TextView taskTitle;
        TextView taskDescription;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskTitle = itemView.findViewById(R.id.taskTitle);
            this.taskDescription = itemView.findViewById(R.id.taskDescription);
        }
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_task, parent, false);
        final TaskViewHolder holder = new TaskViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.redirectToTaskDetailPage(holder.task);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task taskAtPosition = this.tasks.get(position);
        holder.task = taskAtPosition;
        holder.taskTitle.setText("Title: " + taskAtPosition.getTitle());
        holder.taskDescription.setText("Description: " + taskAtPosition.getBody());
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }

    public static interface OnTaskInteractionListener {
        public void redirectToTaskDetailPage(Task task);
    }
}
