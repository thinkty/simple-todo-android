package com.thinkty.simpletodos;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoItemAdapter extends RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder> {

    private final String TAG = "TodoItemAdapter";
    private ArrayList<String> dataset;

    public static class TodoItemViewHolder extends RecyclerView.ViewHolder {

        public TextView todoTextView;
        public Button removeTodoButton;
        public View layout;

        public TodoItemViewHolder(View view) {
            super(view);
            todoTextView = (TextView) view.findViewById(R.id.todo_item_content);
            removeTodoButton = (Button) view.findViewById(R.id.remove_todo_button);
            layout = view;
        }
    }

    public void add(String todo) {
        dataset.add(todo);
    }

    public void remove(String todo) {
        for (int i = 0; i < dataset.size(); i++) {
            if (dataset.get(i).equals(todo)) {
                dataset.remove(i);
                break;
            }
        }
        this.notifyDataSetChanged();
        if (MainActivity.todoCount > 0) {
            Log.d(TAG, "removing item from todo list");
            MainActivity.todoCount--;
        }
    }

    public TodoItemAdapter(ArrayList<String> todos) {
        this.dataset = todos;
    }

    @NonNull
    @Override
    public TodoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_layout, parent, false);
        return new TodoItemViewHolder(view);
    }

    /**
     *
     * @param holder
     * @param position  position indicates the position of the visual item
     */
    @Override
    public void onBindViewHolder(@NonNull final TodoItemViewHolder holder, int position) {
        final String content = dataset.get(position);
        holder.todoTextView.setText(content);
        holder.removeTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(content);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }




}
