package com.example.myaquarium.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.TipsPage;
import com.example.myaquarium.adapter.view.TipsViewHolder;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsViewHolder> {
    private Context context;
    private List<List<String>> tips;

    public TipsAdapter(Context context, List<List<String>> tips) {
        this.context = context;
        this.tips = tips;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tipsItems = LayoutInflater
                .from(context)
                .inflate(R.layout.tips_item, parent, false);

        return new TipsViewHolder(tipsItems);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {

        holder.tipsTitle.setText(tips.get(position).get(1));
        holder.tipsContent.setText(tips.get(position).get(2));
        String title = tips.get(position).get(1);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TipsPage.class);
            intent.putExtra("title", title);
            context.startActivity(intent);
        });

//        int imageId = context.getResources().getIdentifier(
//                                                tips.get(position),
//                                                "drawable",
//                                                context.getPackageName());
//        holder.tipsImage.setImageResource(imageId);;

    }

    @Override
    public int getItemCount() {
        return tips.size();
    }
}
