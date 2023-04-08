package com.example.myaquarium.adapter.view;

import android.view.View;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class ForumThemesListViewHolder extends RecyclerView.ViewHolder {
    public final SwitchCompat checkBox;

    public ForumThemesListViewHolder(View view){
        super(view);
        checkBox = view.findViewById(R.id.checkBox);
    }
}
