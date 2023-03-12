package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.FishListViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FishListAdapter extends RecyclerView.Adapter<FishListViewHolder> {
    private Context context;
    private TextView myFish;
    private static List<JSONObject> fishList;

    public FishListAdapter(Context context, List<JSONObject> fishList, TextView myFish) {
        this.context = context;
        this.fishList = fishList;
        this.myFish = myFish;
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
        holder.nameView.setText(fishList.get(position).optString("fish"));
        holder.countView.setText(fishList.get(position).optString("count"));

        holder.addButton.setOnClickListener(view -> {
            int count = Integer.parseInt(holder.countView.getText().toString()) + 1;
            holder.countView.setText(String.valueOf(count));
            try {
                fishList.get(position).put("count", String.valueOf(count));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        holder.removeButton.setOnClickListener(view -> {
          int count = Integer.parseInt(holder.countView.getText().toString()) - 1;
          if (count < 0) count = 0;

          holder.countView.setText(String.valueOf(count));
            try {
                fishList.get(position).put("count", String.valueOf(count));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            fishList.remove(position);
            if (fishList.size() == 0) {
                myFish.setVisibility(View.VISIBLE);
            } else {
                myFish.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }
}
