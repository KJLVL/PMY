package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class ForumCommentsViewHolder extends RecyclerView.ViewHolder {
    public ImageView avatar;
    public TextView author;
    public TextView date;
    public TextView response;
    public TextView comment;
    public LinearLayout images;
    public ImageView switcher;
    public AppCompatButton answer;

    public ForumCommentsViewHolder(View view){
        super(view);
        avatar = view.findViewById(R.id.avatar);
        author = view.findViewById(R.id.author);
        date = view.findViewById(R.id.date);
        response = view.findViewById(R.id.response);
        comment = view.findViewById(R.id.comment);
        images = view.findViewById(R.id.images);
        switcher = view.findViewById(R.id.switcher);
        answer = view.findViewById(R.id.answer);
    }
}
