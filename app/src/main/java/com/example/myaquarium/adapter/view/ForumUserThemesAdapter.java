package com.example.myaquarium.adapter.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

import java.util.List;

public class ForumUserThemesAdapter extends RecyclerView.Adapter<ForumUserThemesViewHolder> {
    private Context context;
    private List<List<String>> themesList;
    private final OnThemeClickListener onClickListener;

    public interface OnThemeClickListener {
        void onStateClick(String themeId);
    }

    public ForumUserThemesAdapter(
            Context context,
            List<List<String>> themesList,
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
        holder.theme.setText(themesList.get(position).get(2));
        holder.date.setText(themesList.get(position).get(4));

        holder.sectionsItem.setOnClickListener(
                v -> onClickListener.onStateClick(themesList.get(position).get(0))
        );
    }

    @Override
    public int getItemCount() {
        return themesList.size();
    }
}
