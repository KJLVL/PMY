package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.ResultCompatibilityViewHolder;

import java.util.List;

public class ResultCompatibilityAdapter extends RecyclerView.Adapter<ResultCompatibilityViewHolder> {
    private Context context;
    private List<List<String>> fishList;

    public ResultCompatibilityAdapter(
            Context context,
            List<List<String>> fishList
    ) {
        this.context = context;
        this.fishList = fishList;
    }

    @NonNull
    @Override
    public ResultCompatibilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.fish_list_item_by_comp_result, parent, false);

        return new ResultCompatibilityViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultCompatibilityViewHolder holder, int position) {
        holder.name.setText(fishList.get(position).get(0));
        switch (fishList.get(position).get(1)) {
            case "1":
                holder.result.setImageResource(R.drawable.match);
                break;
            case "2":
                holder.result.setImageResource(R.drawable.overlap);
                break;
            default:
                holder.result.setImageResource(R.drawable.notmatch);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }
}
