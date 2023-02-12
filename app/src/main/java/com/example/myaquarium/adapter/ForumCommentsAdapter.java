package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ForumCommentsViewHolder;
import com.example.myaquarium.server.Requests;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class ForumCommentsAdapter extends RecyclerView.Adapter<ForumCommentsViewHolder> {
    private Context context;
    private List<JSONObject> commentsList;
    private Requests requests = new Requests();
    private final ForumCommentsAdapter.onAnswerClickListener onClickListener;

    public interface onAnswerClickListener {
        void onStateClick(String author);
    }

    public ForumCommentsAdapter(
            Context context,
            List<JSONObject> commentsList,
            ForumCommentsAdapter.onAnswerClickListener onClickListener
    ) {
        this.context = context;
        this.commentsList = commentsList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ForumCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.forum_answers_item, parent, false);

        return new ForumCommentsViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumCommentsViewHolder holder, int position) {
        Picasso.get()
                .load(
                        requests.urlRequestImg
                                + commentsList.get(position).optString("avatar")
                )
                .into(holder.avatar);

        holder.author.setText("автор: " + commentsList.get(position).optString("login_from"));
        holder.date.setText("дата: " + commentsList.get(position).optString("date"));

        if (!commentsList.get(position).optString("login_to").equals("null")) {
            holder.response.setText("кому: " + commentsList.get(position).optString("login_to"));
        } else {
            holder.response.setVisibility(View.GONE);
        }

        holder.comment.setText(commentsList.get(position).optString("comment"));

        if (
                commentsList.get(position).optString("images").equals("null")
                        || commentsList.get(position).optString("images").equals("")
        ) {
            holder.images.setVisibility(View.GONE);
        } else {
            Picasso.get()
                    .load(
                            requests.urlRequestImg +
                                commentsList.get(position).optString("images")
                    )
                    .into(holder.switcher);
        }
        holder.answer.setOnClickListener(view -> {
            onClickListener.onStateClick(commentsList.get(position).optString("user_id"));
        });
    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}
