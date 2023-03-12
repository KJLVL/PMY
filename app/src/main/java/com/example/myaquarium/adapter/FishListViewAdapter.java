package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.FishViewHolder;

import org.json.JSONException;

import java.util.List;

public class FishListViewAdapter extends RecyclerView.Adapter<FishViewHolder> {
    private Context context;
    private List<String> fishList;
    private final OnFishClickListener onClickListener;

    public interface OnFishClickListener {
        void onStateClick(String fish) throws JSONException;
    }

    public FishListViewAdapter(
            Context context,
            List<String> fishList,
            OnFishClickListener onClickListener
    ) {
        this.context = context;
        this.fishList = fishList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public FishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.fish_list_item, parent, false);

        return new FishViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull FishViewHolder holder, int position) {
        holder.item.setText(fishList.get(position));
        holder.itemView.setOnClickListener(
                v -> {
                    try {
                        onClickListener.onStateClick(fishList.get(position));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }
}
