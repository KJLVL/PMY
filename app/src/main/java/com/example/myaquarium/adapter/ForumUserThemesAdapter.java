package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ForumUserThemesViewHolder;

import org.json.JSONObject;

import java.util.List;

public class ForumUserThemesAdapter extends RecyclerView.Adapter<ForumUserThemesViewHolder> {
    private Context context;
    private List<JSONObject> themesList;
    private final OnThemeClickListener onClickListener;

    public interface OnThemeClickListener {
        void onStateClick(JSONObject themeId);
    }

    public ForumUserThemesAdapter(
            Context context,
            List<JSONObject> themesList,
            OnThemeClickListener onClickListener
    ) {
        this.context = context;
        this.themesList = themesList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ForumUserThemesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View themes = LayoutInflater
                .from(context)
                .inflate(R.layout.forum_my_sections_item, parent, false);

        return new ForumUserThemesViewHolder(themes);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumUserThemesViewHolder holder, int position) {
        holder.theme.setText(themesList.get(position).optString("title"));
        holder.date.setText(themesList.get(position).optString("date"));

        holder.sectionsItem.setOnClickListener(
                v -> onClickListener.onStateClick(themesList.get(position))
        );
        holder.edit.setOnClickListener(
                v -> onClickListener.onStateClick(themesList.get(position))
        );
    }

    @Override
    public int getItemCount() {
        return themesList.size();
    }
}
