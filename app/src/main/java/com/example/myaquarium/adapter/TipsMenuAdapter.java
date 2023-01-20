package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.Tips;
import com.example.myaquarium.adapter.view.TipsMenuViewHolder;

import java.util.List;

public class TipsMenuAdapter extends RecyclerView.Adapter<TipsMenuViewHolder> {
    private Context context;
    private List<String> category;

    public TipsMenuAdapter(Context context, List<String> category) {
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public TipsMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater
                                .from(context)
                                .inflate(R.layout.information_category_item, parent, false);

        return new TipsMenuViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsMenuViewHolder holder, int position) {
        holder.categoryTitle.setText(category.get(position));

        holder.itemView.setOnClickListener(view -> Tips.showTipsByCategory(position));
    }

    @Override
    public int getItemCount() {
        return category.size();
    }
}
