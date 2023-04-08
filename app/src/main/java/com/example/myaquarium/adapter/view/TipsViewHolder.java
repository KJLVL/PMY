package com.example.myaquarium.adapter.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;

public class TipsViewHolder extends RecyclerView.ViewHolder {
    public final ImageView tipsImage;
    public final TextView tipsTitle;
    public final TextView tipsContent;

    public TipsViewHolder(View view) {
        super(view);
        tipsImage = view.findViewById(R.id.tipsImage);
        tipsTitle = view.findViewById(R.id.tipsTitle);
        tipsContent = view.findViewById(R.id.tipsContent);
    }
}
