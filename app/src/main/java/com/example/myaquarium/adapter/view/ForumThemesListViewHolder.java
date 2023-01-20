package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class ForumThemesListViewHolder extends RecyclerView.ViewHolder {
    public TextView section;
    public CheckBox checkBox;

    public ForumThemesListViewHolder(View view){
        super(view);
        section = view.findViewById(R.id.section);
        checkBox = view.findViewById(R.id.checkBox);
    }
}
