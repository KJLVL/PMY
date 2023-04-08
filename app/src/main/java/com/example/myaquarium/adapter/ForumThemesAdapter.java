package com.example.myaquarium.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ForumThemesViewHolder;
import com.example.myaquarium.model.Theme;

import java.util.List;

public class ForumThemesAdapter extends RecyclerView.Adapter<ForumThemesViewHolder> {
    private final Context context;
    private final List<Theme> themesList;
    private final OnThemeClickListener onClickListener;

    public interface OnThemeClickListener {
        void onStateClick(String themeId, String category);
    }

    public ForumThemesAdapter(
            Context context,
            List<Theme> themesList,
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ForumThemesViewHolder holder, int position) {
        holder.theme.setText(themesList.get(position).getTitle());
        holder.author.setText("автор: " + themesList.get(position).getAuthor());
        holder.date.setText(themesList.get(position).getDate());
        this.setBackground(holder, position);

        holder.sectionsItem.setOnClickListener(
                v -> onClickListener.onStateClick(themesList.get(position).getId(), themesList.get(position).getCategoryId())
        );
    }

    private void setBackground(ForumThemesViewHolder holder, int position) {
        switch (themesList.get(position).getSections()) {
            case "Общие вопросы содержания":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.general_issues));
                break;
            case "Аквариумные рыбки":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.aquarium_fish));
                break;
            case "Болезни":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.diseases));
                break;
            case "Дизайн":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.design));
                break;
            case "Аквариумное оборудование и прочее":
            case "Аквариумы и оборудование":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.aquarium_equipment_other));
                break;
            case "Морские аквариумы и оборудование":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.marine_aquariums_equipment));
                break;
            case "Корма для рыбок и креветок":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.food_fish));
                break;
            case "Аквариумные растения":
                holder.sectionsItem.setBackgroundColor(context.getResources().getColor(R.color.aquarium_plants));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return themesList.size();
    }
}
