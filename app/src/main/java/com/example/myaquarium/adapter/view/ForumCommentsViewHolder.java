package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.github.chrisbanes.photoview.PhotoView;


public class ForumCommentsViewHolder extends RecyclerView.ViewHolder {
    public final ImageView avatar;
    public final TextView author;
    public final TextView date;
    public final TextView comment;
    public final LinearLayout images;
    public final PhotoView switcher;
    public final AppCompatButton answer;

    public ForumCommentsViewHolder(View view){
        super(view);
        avatar = view.findViewById(R.id.avatar);
        author = view.findViewById(R.id.author);
        date = view.findViewById(R.id.date);
        comment = view.findViewById(R.id.comment);
        images = view.findViewById(R.id.images);
        switcher = view.findViewById(R.id.switcher);
        answer = view.findViewById(R.id.answer);
    }
}
