package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.FishListViewHolder;

import java.util.List;

public class FishListAdapter extends RecyclerView.Adapter<FishListViewHolder> {
    private Context context;
    private static List<List<String>> fishList;

    public FishListAdapter(Context context, List<List<String>> fishList) {
        this.context = context;
        this.fishList = fishList;
    }

    @NonNull
    @Override
    public FishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.fish_item, parent, false);

        return new FishListViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull FishListViewHolder holder, int position) {
        holder.nameView.setText(fishList.get(position).get(0));
        holder.countView.setText(fishList.get(position).get(1));

        holder.addButton.setOnClickListener(view -> {
            int count = Integer.parseInt(holder.countView.getText().toString()) + 1;
            holder.countView.setText(String.valueOf(count));
            fishList.get(position).set(1, String.valueOf(count));
        });

        holder.removeButton.setOnClickListener(view -> {
          int count = Integer.parseInt(holder.countView.getText().toString()) - 1;
          if (count < 0) count = 0;

          holder.countView.setText(String.valueOf(count));
          fishList.get(position).set(1, String.valueOf(count));

        });

        holder.deleteButton.setOnClickListener(view -> {
            fishList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }
}
