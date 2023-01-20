package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class FishListViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public TextView countView;
    public Button addButton;
    public Button removeButton;
    public Button deleteButton;

    public FishListViewHolder(View view){
        super(view);
        nameView = view.findViewById(R.id.nameView);
        countView = view.findViewById(R.id.countView);
        addButton = view.findViewById(R.id.addButton);
        removeButton = view.findViewById(R.id.removeButton);
        deleteButton = view.findViewById(R.id.deleteButton);
    }
}
