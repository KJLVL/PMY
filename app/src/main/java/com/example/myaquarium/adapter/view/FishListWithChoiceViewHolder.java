package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class FishListWithChoiceViewHolder extends RecyclerView.ViewHolder {
    public CheckBox checkBox;

    public FishListWithChoiceViewHolder(View view){
        super(view);
        checkBox = view.findViewById(R.id.item);
    }
}
