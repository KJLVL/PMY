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
import com.example.myaquarium.server.Requests;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsViewHolder> {
    private Context context;
    private List<List<String>> tips;
    private Requests requests = new Requests();

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

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TipsPage.class);
            intent.putExtra("title", tips.get(position).get(1));
            context.startActivity(intent);
        });

        Picasso.get()
                .load(requests.urlRequestImg + tips.get(position).get(3))
                .into(holder.tipsImage);

    }

    @Override
    public int getItemCount() {
        return tips.size();
    }
}
