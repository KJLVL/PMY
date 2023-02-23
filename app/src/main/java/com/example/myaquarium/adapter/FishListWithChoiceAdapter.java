package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.FishListWithChoiceViewHolder;

import java.util.List;

public class FishListWithChoiceAdapter extends RecyclerView.Adapter<FishListWithChoiceViewHolder> {
    private Context context;
    private List<String> fishList;
//    private final OnFishClickListener onClickListener;
    private boolean[] checked;

//    public interface OnFishClickListener {
//        void onStateClick(SwitchCompat fish);
//    }

    public FishListWithChoiceAdapter(
            Context context,
            List<String> fishList
//            OnFishClickListener onClickListener
    ) {
        this.context = context;
        this.fishList = fishList;
//        this.onClickListener = onClickListener;
        checked = new boolean[fishList.size()];
    }

    @NonNull
    @Override
    public FishListWithChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.fish_list_item_by_comp, parent, false);

        return new FishListWithChoiceViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull FishListWithChoiceViewHolder holder, int position) {
        holder.checkBox.setText(fishList.get(position));
        holder.checkBox.setChecked(checked[position]);
        holder.checkBox.setOnClickListener(
                v -> {
                    checked[position] = !checked[position];
//                    onClickListener.onStateClick(holder.checkBox);
                }
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
