package com.example.myaquarium.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.TipsPage;
import com.example.myaquarium.adapter.view.TipsViewHolder;
import com.example.myaquarium.service.Requests;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsViewHolder> {
    private final Context context;
    private final List<JSONObject> tips;
    private final Requests requests = new Requests();

    public TipsAdapter(Context context, List<JSONObject> tips) {
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

        holder.tipsTitle.setText(tips.get(position).optString("title"));
        holder.tipsContent.setText(tips.get(position).optString("content"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            holder.tipsContent.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TipsPage.class);
            intent.putExtra("id", tips.get(position).optString("id"));
            context.startActivity(intent);
        });

        Picasso.get()
                .load(requests.urlRequestImg + tips.get(position).optString("image"))
                .into(holder.tipsImage);

    }

    @Override
    public int getItemCount() {
        return tips.size();
    }
}
