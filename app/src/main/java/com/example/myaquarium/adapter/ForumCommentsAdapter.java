package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ForumCommentsViewHolder;
import com.example.myaquarium.service.Requests;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForumCommentsAdapter extends RecyclerView.Adapter<ForumCommentsViewHolder> {
    private final Context context;
    private final JSONArray commentsList;
    private final Requests requests = new Requests();
    private final ForumCommentsAdapter.onAnswerClickListener onClickListener;
    private final ForumCommentsAdapter.onClickImageListener imageClickListener;

    public interface onAnswerClickListener {
        void onStateClick(String author, String name);
    }

    public interface onClickImageListener {
        void onImageClick(String uri, PhotoView image);
    }

    public ForumCommentsAdapter (
            Context context,
            JSONArray commentsList,
            ForumCommentsAdapter.onAnswerClickListener onClickListener,
            ForumCommentsAdapter.onClickImageListener imageClickListener
    ) {
        this.context = context;
        this.commentsList = commentsList;
        this.onClickListener = onClickListener;
        this.imageClickListener = imageClickListener;
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
        try {
            JSONObject jsonObject = commentsList.getJSONObject(position);

            Picasso.get()
                    .load(
                            requests.urlRequestImg
                                    + jsonObject.getString("avatar")
                    )
                    .into(holder.avatar);
            holder.author.setText(jsonObject.optString("name"));
            holder.date.setText(jsonObject.optString("date"));

            holder.comment.setText(jsonObject.optString("comment"));

            if (
                    jsonObject.optString("images").equals("null")
                    || jsonObject.optString("images").equals("")
            ) {
                holder.images.setVisibility(View.GONE);
            } else {
                Picasso.get()
                        .load(requests.urlRequestImg + jsonObject.optString("images"))
                        .into(holder.switcher);
                holder.switcher.setOnClickListener(view -> imageClickListener.onImageClick(
                                requests.urlRequestImg + jsonObject.optString("images"),
                                holder.switcher
                        )
                );
            }
            holder.answer.setOnClickListener(view -> {
                onClickListener.onStateClick(jsonObject.optString("user_id"), jsonObject.optString("name"));
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return commentsList.length();
    }
}
