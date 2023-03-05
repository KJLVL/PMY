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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForumCommentsAdapter extends RecyclerView.Adapter<ForumCommentsViewHolder> {
    private Context context;
    private JSONArray commentsList;
    private Requests requests = new Requests();
    private final ForumCommentsAdapter.onAnswerClickListener onClickListener;

    public interface onAnswerClickListener {
        void onStateClick(String author);
    }

    public ForumCommentsAdapter(
            Context context,
            JSONArray commentsList,
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

        try {
            JSONObject jsonObject = commentsList.getJSONObject(position);

            Picasso.get()
                    .load(
                            requests.urlRequestImg
                                    + jsonObject.getString("avatar")
                    )
                    .into(holder.avatar);
            holder.author.setText("автор: " + jsonObject.optString("login_from"));
            holder.date.setText(jsonObject.optString("date"));

            if (!jsonObject.getString("login_to").equals("null")) {
                holder.response.setText("кому: " + jsonObject.optString("login_to"));
            } else {
                holder.response.setVisibility(View.GONE);
            }

            holder.comment.setText(jsonObject.optString("comment"));

            if (
                    jsonObject.optString("images").equals("null")
                            || jsonObject.optString("images").equals("")
            ) {
                holder.images.setVisibility(View.GONE);
            } else {
                Picasso.get()
                        .load(
                                requests.urlRequestImg +
                                        jsonObject.optString("images")
                        )
                        .into(holder.switcher);
            }
            holder.answer.setOnClickListener(view -> {
                onClickListener.onStateClick(jsonObject.optString("user_id"));
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
