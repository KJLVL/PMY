package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myaquarium.R;

public final class TipsMenuViewHolder extends RecyclerView.ViewHolder {
    public TextView categoryTitle;

    public TipsMenuViewHolder(@NonNull View view) {
        super(view);
        categoryTitle = view.findViewById(R.id.tipsMenu);
    }

}
