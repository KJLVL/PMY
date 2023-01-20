package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class FishViewHolder extends RecyclerView.ViewHolder {
    public TextView item;

    public FishViewHolder(View view){
        super(view);
        item = view.findViewById(R.id.item);
    }
}
