package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ForumThemesViewHolder;

import java.util.List;

public class ForumThemesAdapter extends RecyclerView.Adapter<ForumThemesViewHolder> {
    private Context context;
    private List<List<String>> themesList;
    private final OnThemeClickListener onClickListener;

    public interface OnThemeClickListener {
        void onStateClick(String themeId);
    }

    public ForumThemesAdapter(
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
    public ForumThemesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.forum_sections_item, parent, false);

        return new ForumThemesViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumThemesViewHolder holder, int position) {
//        holder.sections.setText(themesList.get(position).get(1));
        holder.theme.setText(themesList.get(position).get(2));
        holder.author.setText(themesList.get(position).get(3));
        holder.sectionsItem.setOnClickListener(
                v -> onClickListener.onStateClick(themesList.get(position).get(0))
        );
    }

    @Override
    public int getItemCount() {
        return themesList.size();
    }
}
