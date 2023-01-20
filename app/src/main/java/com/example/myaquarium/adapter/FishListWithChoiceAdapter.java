package com.example.myaquarium.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaquarium.R;
import com.example.myaquarium.adapter.view.FishListWithChoiceViewHolder;

import java.util.List;

public class FishListWithChoiceAdapter extends RecyclerView.Adapter<FishListWithChoiceViewHolder> {
    private Context context;
    private List<String> fishList;
    private final OnFishClickListener onClickListener;

    public interface OnFishClickListener {
        void onStateClick(CheckBox fish);
    }

    public FishListWithChoiceAdapter(
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
    public FishListWithChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fishItems = LayoutInflater
                .from(context)
                .inflate(R.layout.fish_list_item_by_comp, parent, false);

        return new FishListWithChoiceViewHolder(fishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull FishListWithChoiceViewHolder holder, int position) {
        holder.checkBox.setText(fishList.get(position));
//        int states[][] = {{android.R.attr.state_checked}, {}};
//        int colors[] = {};
//        CompoundButtonCompat.setButtonTintList(holder.checkBox, new ColorStateList(states, colors));
        holder.checkBox.setOnClickListener(
                v -> onClickListener.onStateClick(holder.checkBox)
        );
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }
}
