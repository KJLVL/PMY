package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class ForumUserThemesViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout sectionsItem;
    public final TextView theme;
    public final TextView date;
    public final Button edit;

    public ForumUserThemesViewHolder(View view){
        super(view);
        sectionsItem = view.findViewById(R.id.sectionsItem);
        theme = view.findViewById(R.id.theme);
        date = view.findViewById(R.id.date);
        edit = view.findViewById(R.id.edit);
    }
}
