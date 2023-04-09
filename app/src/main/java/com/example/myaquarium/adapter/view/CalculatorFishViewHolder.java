package com.example.myaquarium.adapter.view;

import android.view.View;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class CalculatorFishViewHolder extends RecyclerView.ViewHolder {
    public final SwitchCompat checkBox;

    public CalculatorFishViewHolder(View view){
        super(view);
        checkBox = view.findViewById(R.id.item);
    }
}
