package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class ForumThemesViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout sectionsItem;
    public final TextView theme;
    public final TextView author;
    public final TextView date;

    public ForumThemesViewHolder(View view){
        super(view);
        sectionsItem = view.findViewById(R.id.sectionsItem);
        theme = view.findViewById(R.id.theme);
        author = view.findViewById(R.id.author);
        date = view.findViewById(R.id.date);
    }
}
