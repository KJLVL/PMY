package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.CalculatorFishViewHolder;

import java.util.List;

public class CalculatorFishAdapter extends RecyclerView.Adapter<CalculatorFishViewHolder> {
    private final Context context;
    private final List<String> fishList;
    private final boolean[] checked;

    public CalculatorFishAdapter(
            Context context,
            List<String> fishList
    ) {
        this.context = context;
        this.fishList = fishList;
        checked = new boolean[fishList.size()];
    }

    @NonNull
    @Override
    public CalculatorFishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.fish_list_item_by_comp, parent, false);

        return new CalculatorFishViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculatorFishViewHolder holder, int position) {
        holder.checkBox.setText(fishList.get(position));
        holder.checkBox.setChecked(checked[position]);
        holder.checkBox.setOnClickListener(
                v -> checked[position] = !checked[position]
        );
    }

    public boolean[] getChecked() {
        return checked;
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }
}
