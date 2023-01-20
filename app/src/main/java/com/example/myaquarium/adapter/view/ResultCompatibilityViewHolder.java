package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class ResultCompatibilityViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public ImageView result;

    public ResultCompatibilityViewHolder(View view){
        super(view);
        name = view.findViewById(R.id.fish);
        result = view.findViewById(R.id.image);
    }
}
